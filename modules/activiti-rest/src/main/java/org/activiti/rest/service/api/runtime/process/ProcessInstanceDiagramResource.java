/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.rest.service.api.runtime.process;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.io.InputStream;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Frederik Heremans
 */
@RestController
@Api(tags = { "Process Instances" }, description = "Manage Process Instances", authorizations = { @Authorization(value = "basicAuth") })
public class ProcessInstanceDiagramResource extends BaseProcessInstanceResource {

  @Autowired
  protected RepositoryService repositoryService;

  @Autowired
  protected ProcessEngineConfiguration processEngineConfiguration;

  @ApiOperation(value = "Get diagram for a process instance", tags = { "Process Instances" })
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Indicates the process instance was found and the diagram was returned."),
      @ApiResponse(code = 400, message = "Indicates the requested process instance was not found but the process doesn???t contain any graphical information (BPMN:DI) and no diagram can be created."),
      @ApiResponse(code = 404, message = "Indicates the requested process instance was not found.")
  })
  @RequestMapping(value = "/runtime/process-instances/{processInstanceId}/diagram", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getProcessInstanceDiagram(@ApiParam(name = "processInstanceId", value="The id of the process instance to get the diagram for.") @PathVariable String processInstanceId, HttpServletResponse response) {
    ProcessInstance processInstance = getProcessInstanceFromRequest(processInstanceId);

    ProcessDefinition pde = repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

    if (pde != null && pde.hasGraphicalNotation()) {
      BpmnModel bpmnModel = repositoryService.getBpmnModel(pde.getId());
      ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
      InputStream resource = diagramGenerator.generateDiagram(bpmnModel, "png", runtimeService.getActiveActivityIds(processInstance.getId()), Collections.<String> emptyList(),
          processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(), 
          processEngineConfiguration.getAnnotationFontName(), processEngineConfiguration.getClassLoader(), 1.0);

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("Content-Type", "image/png");
      try {
        return new ResponseEntity<byte[]>(IOUtils.toByteArray(resource), responseHeaders, HttpStatus.OK);
      } catch (Exception e) {
        throw new ActivitiIllegalArgumentException("Error exporting diagram", e);
      }

    } else {
      throw new ActivitiIllegalArgumentException("Process instance with id '" + processInstance.getId() + "' has no graphical notation defined.");
    }
  }
}
