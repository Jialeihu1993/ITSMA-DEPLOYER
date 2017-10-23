package com.hpe.itsma.itsmaInstaller.controller;

import com.hpe.itsma.itsmaInstaller.bean.SuiteVersion;
import com.hpe.itsma.itsmaInstaller.service.SuiteVersionService;
import com.hpe.itsma.itsmaInstaller.service.UpdateService;
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
@RequestMapping(value = "/itsma")
public class UpdateController {
  private Log logger = LogFactory.getLog(UpdateController.class);

  @Autowired
  UpdateService updateService;

  @Autowired
  SuiteVersionService suiteVersionService;

  @ApiOperation(value = "Endpoint for update itsma suite", notes = "")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Installation started"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 500, message = "Server internal error")
  })
  @RequestMapping(value = "/update", method = RequestMethod.POST)
  @ResponseStatus(value = HttpStatus.CREATED)
  public void update() throws Exception {
    logger.info("Accept request for update...");
    updateService.update();
  }

  @ApiOperation(value = "Endpoint for getting the suite version will be updated to")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "suite version"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 500, message = "Server internal error")
  })
  @RequestMapping(value = "/update/version", method = RequestMethod.GET)
  @ResponseStatus(value = HttpStatus.OK)
  public SuiteVersion versionUpdateTo() throws Exception {
    logger.info("Accept request for getting the version to update...");
    return suiteVersionService.updateVersion();
  }
}
