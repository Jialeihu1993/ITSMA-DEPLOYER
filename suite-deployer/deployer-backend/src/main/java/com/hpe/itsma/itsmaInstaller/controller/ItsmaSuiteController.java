package com.hpe.itsma.itsmaInstaller.controller;

import com.hpe.itsma.itsmaInstaller.bean.DeployerStatus;
import com.hpe.itsma.itsmaInstaller.bean.ItsmaServiceStatus;
import com.hpe.itsma.itsmaInstaller.bean.SuiteVersion;
import com.hpe.itsma.itsmaInstaller.service.ItsmaSuite;
import com.hpe.itsma.itsmaInstaller.service.SuiteVersionService;
import io.swagger.annotations.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by tianlib on 8/28/2017.
 */
@Api
@RestController
@RequestMapping("/itsma")
public class ItsmaSuiteController {
  private Log logger = LogFactory.getLog(ItsmaSuiteController.class);

  @Autowired
  ItsmaSuite itsmaSuite;
  @Autowired
  SuiteVersionService suiteVersionService;

  @ApiOperation(value = "Endpoint for receiving manifest.yaml",
      notes = "This endpoint must be the first one to be invoked by the client, deployer decides the Itsma service's status only when it knows who the" +
          " service is and if the service depends on others, if no, then the Itsma service will be in Ready status. If yes, and the service it depends on is not" +
          " in Success status, then the current service will be in Wait status, or it will be in Ready status.")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "manifest.yaml created."),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 500, message = "Server internal error")})
  @RequestMapping(value = "/itsma_services/{itsmaServiceName}/manifest", method = RequestMethod.POST, consumes = "text/plain")
  @ResponseStatus(value = HttpStatus.CREATED)
  public void UploadServiceManifest(
      @ApiParam(value = "Itsma service name", required = true) @PathVariable String itsmaServiceName,
      @ApiParam(value = "The content of manifest.yaml", required = true) @RequestBody String manifest) throws Exception {
    logger.info("Receiving manifest.yaml of itsma service [" + itsmaServiceName + "] with payload...");
    itsmaSuite.saveManifest(itsmaServiceName, manifest);
  }

  @ApiOperation(value = "Endpoint for creating k8s resource",
      notes = "Create various resources of kubernetes. e.g. namespace, pv, secret, service, pod, deployment")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Resource created."),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 500, message = "Server internal error")})
  @RequestMapping(value = "/itsma_services/{itsmaServiceName}/kube_resources", method = RequestMethod.POST)
  @ResponseStatus(value = HttpStatus.CREATED)
  public void createKubeResourceWithPayload(
      @ApiParam(value = "Itsma service name", required = true) @PathVariable String itsmaServiceName,
      @ApiParam(value = "Name of the file that the resource will be saved in", required = true) @RequestHeader String resourcename,
      @ApiParam(value = "The resource content in yaml format", required = true) @RequestBody String kubeResource) throws Exception {
    logger.info("Create kubernetes resource of itsma service [" + itsmaServiceName + "] with payload...");
    itsmaSuite.createKubeResourceByContent(itsmaServiceName, resourcename, kubeResource);
  }

  @ApiOperation(value = "Endpoint for updating the status of specified Itsma service")
  @RequestMapping(value = "/itsma_services/{itsmaServiceName}", method = RequestMethod.PUT, consumes = "application/json")
  public void updateItsmaServiceStatus(
      @ApiParam(value = "Itsma service name", required = true) @PathVariable String itsmaServiceName,
      @ApiParam(value = "The itsma service's status is json format", required = true) @RequestBody ItsmaServiceStatus itsmaServiceStatus) throws Exception {
    logger.info("Update itsma service status...");
    itsmaSuite.setItsmaServiceStatus(itsmaServiceName, itsmaServiceStatus);
  }

  @ApiOperation(value = "Endpoint for getting the status of specified Itsma service")
  @RequestMapping(value = "/itsma_services/{itsmaServiceName}", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(code = HttpStatus.OK)
  public ItsmaServiceStatus getItsmaServiceStatus(
      @ApiParam(value = "Itsma service name", required = true) @PathVariable String itsmaServiceName) throws Exception {
    logger.info("get itsma service status...");
    return itsmaSuite.getItsmaServiceStatus(itsmaServiceName);
  }

  @ApiOperation(value = "Endpoint for shutting down all activitived Itsma services")
  @ApiResponses(value = {
      @ApiResponse(code = 202, message = "All Itsma services are marked for deletion."),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 500, message = "Server internal error")})
  @RequestMapping(value = "/itsma_services", method = RequestMethod.DELETE)
  @ResponseStatus(code = HttpStatus.ACCEPTED)
  public void shutdownSuite() throws Exception {
    itsmaSuite.stopAllServices();
  }

  @ApiOperation(value = "Endpoint for shutting down the given Itsma services")
  @ApiResponses(value = {
      @ApiResponse(code = 202, message = "The given Itsma service is marked for deletion."),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 500, message = "Server internal error")})
  @RequestMapping(value = "/itsma_services/{itsmaServiceName}", method = RequestMethod.DELETE)
  @ResponseStatus(code = HttpStatus.ACCEPTED)
  public void shutdownItsmaService(
      @ApiParam(value = "Itsma service name", required = true) @PathVariable String itsmaServiceName) throws Exception {
    itsmaSuite.stopService(itsmaServiceName);
  }

  @ApiOperation(value = "Endpoint for getting the status of suite deployer")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK."),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 500, message = "Server internal error")})
  @RequestMapping(value = "/deployer/status", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(code = HttpStatus.OK)
  public DeployerStatus getDeployerStatus() throws Exception {
    return itsmaSuite.getDeployerStatus();
  }

  @ApiOperation(value = "Endpoint for getting current suite version")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "suite version"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 500, message = "Server internal error")
  })
  @RequestMapping(value = "/version", method = RequestMethod.GET)
  @ResponseStatus(value = HttpStatus.OK)
  public SuiteVersion version() throws Exception {
    logger.info("Accept request for getting the version...");
    return suiteVersionService.currentVersion();
  }
}
