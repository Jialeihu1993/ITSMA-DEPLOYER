package com.hpe.itsma.itsmaInstaller.controller;

import com.hpe.itsma.itsmaInstaller.bean.KubeResourceStatus;
import com.hpe.itsma.itsmaInstaller.exception.ItsmaServiceNotFoundException;
import com.hpe.itsma.itsmaInstaller.exception.ItsmaServiceNotReadyToInstallException;
import com.hpe.itsma.itsmaInstaller.exception.K8sResourceNotFoundException;
import com.hpe.itsma.itsmaInstaller.service.InstallParameterValidator;
import com.hpe.itsma.itsmaInstaller.service.ItsmaInstallerService;
import com.hpe.itsma.itsmaInstaller.service.StorageService;
import io.swagger.annotations.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

@Api
@RestController
@RequestMapping(value = "/itsma")
public class ItsmaInstallerController {

  private static Log logger = LogFactory.getLog(ItsmaInstallerController.class);

  private final ItsmaInstallerService itsmaInstallerService;

  @Autowired
  public ItsmaInstallerController(ItsmaInstallerService itsmaInstallerService) {
    this.itsmaInstallerService = itsmaInstallerService;
  }

  @Autowired
  private StorageService storageService;

  @ApiOperation(value = "Endpoint for installing itsma suite",
    notes = "The install endpoint will start the Itsma suite installation process, and return immediately. " +
      "The client then can check the status of the installation by another api. Note! This endpoint can only be called once.")
  @ApiResponses(value = {
    @ApiResponse(code = 201, message = "Installation started"),
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 404, message = "Not found"),
    @ApiResponse(code = 500, message = "Server internal error")
  })
  @RequestMapping(value = "/install", method = RequestMethod.POST, consumes = "application/json")
  @ResponseStatus(value = HttpStatus.CREATED)
  public void install(
    @ApiParam(value = "Installation payload", required = true) @RequestBody Map<String, Object> properties) throws Exception {
    logger.info("Accept request for install...");
    logger.debug("Payload of install request: " + properties);

    InstallParameterValidator installParameterValidator = new InstallParameterValidator();
    installParameterValidator.validate(properties);

    itsmaInstallerService.install(properties);
  }

  @ApiOperation(value = "Endpoint for checking the Itsma suite installation status",
    notes = "The installation status means all services' status in Itsma suite, the installation will complete as success when all services are in" +
      " 'Success' status, the installation will complete as failed if any service is in 'Failed' status.")
  @RequestMapping(value = "/install", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<String> getInstallStatus() throws Exception {
    logger.info("Getting install status...");

    String result = itsmaInstallerService.getInstallStatus();

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "Endpoint for getting the status of specified k8s pod")
  @RequestMapping(value = "/kube_pods/{podName}", method = RequestMethod.GET, produces = "application/json")
  public KubeResourceStatus getPodStatusByName(
    @ApiParam(value = "k8s pod name", required = true) @PathVariable String podName) throws Exception {
    logger.info("Getting status of pod: " + podName);

    return itsmaInstallerService.getPodStatusByName(podName);
  }

  @ApiOperation(value = "Endpoint for getting the status of specified k8s service")
  @RequestMapping(value = "/kube_services/{serviceName}", method = RequestMethod.GET, produces = "application/json")
  public KubeResourceStatus getServiceStatusByName(
    @ApiParam(value = "k8s service name", required = true) @PathVariable String serviceName) throws Exception {
    logger.info("Getting status of services: " + serviceName);

    return itsmaInstallerService.getServiceStatusByName(serviceName);
  }

  @ApiIgnore
  @RequestMapping(value = "/configuration", method = RequestMethod.GET)
  public ResponseEntity<String> getConfiguration() throws Exception {
    logger.info("Getting all configurations");

    String result = storageService.getConfiguration(null);

    return new ResponseEntity<String>(result, HttpStatus.OK);
  }

  @ApiIgnore
  @RequestMapping(value = "/configuration/{key}", method = RequestMethod.GET)
  public ResponseEntity<String> getConfiguration(@PathVariable String key) throws Exception {
    logger.info("Getting configuration of key: " + key);

    String result = storageService.getConfiguration(key);

    return new ResponseEntity<String>(result, HttpStatus.OK);
  }

  @ApiIgnore
  @RequestMapping(value = "/configuration/{section}/{key}/{value}", method = RequestMethod.POST)
  public ResponseEntity<String> createConfiguration(@PathVariable String section,
                                                    @PathVariable String key,
                                                    @PathVariable String value) throws Exception {
    logger.info("Creating configuration with section: " + section + " key: " + key + " value: " + value);

    HashMap<String, Object> properties = new HashMap<String, Object>();
    properties.put(key, value);
    String result = storageService.createConfiguration(properties);

    return new ResponseEntity<>("Result of creating configuration: " + result, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/verification/database", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<String> connOracle(@RequestBody Map<String, Object> properties) throws Exception {
    logger.info("Testing database connection...");
    logger.debug("Payload of request /verification/database: " + properties);

    return itsmaInstallerService.testDatabase(properties);
  }

  @RequestMapping(value = "/verification/ldap", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<String> connLDap(@RequestBody Map<String, Object> properties) throws Exception {
    logger.info("Testing Ldap server...");
    logger.debug("Payload of request verification/ldap: " + properties);

    return itsmaInstallerService.connLdap(properties);
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<String> exceptionHandler(Exception e) {
    if (e instanceof IllegalArgumentException) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    if (e instanceof ItsmaServiceNotFoundException) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    if (e instanceof ItsmaServiceNotReadyToInstallException) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    if (!(e instanceof K8sResourceNotFoundException)) {
      e.printStackTrace();
    }

    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}