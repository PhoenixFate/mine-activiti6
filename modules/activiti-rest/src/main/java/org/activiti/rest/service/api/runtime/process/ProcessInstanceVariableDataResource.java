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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.runtime.Execution;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Frederik Heremans
 */
@RestController
@Api(tags = { "Process Instances" }, description = "Manage Process Instances", authorizations = { @Authorization(value = "basicAuth") })
public class ProcessInstanceVariableDataResource extends BaseExecutionVariableResource {

  @ApiOperation(value = "Get the binary data for a variable", tags = {"Process Instances"}, nickname = "getProcessInstanceVariableData")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Indicates the process instance was found and the requested variables are returned."),
      @ApiResponse(code = 404, message = "Indicates the requested task was not found or the task doesn???t have a variable with the given name (in the given scope). Status message provides additional information.")
  })
  @RequestMapping(value = "/runtime/process-instances/{processInstanceId}/variables/{variableName}/data", method = RequestMethod.GET)
  public @ResponseBody
  byte[] getVariableData(@ApiParam(name = "processInstanceId") @PathVariable("processInstanceId") String processInstanceId,@ApiParam(name = "variableName") @PathVariable("variableName") String variableName, @RequestParam(value = "scope", required = false) String scope,
      HttpServletRequest request, HttpServletResponse response) {

    Execution execution = getProcessInstanceFromRequest(processInstanceId);
    return getVariableDataByteArray(execution, variableName, scope, response);
  }
}
