package com.hpe.itsma.itsmaInstaller.controller;

import com.hpe.itsma.itsmaInstaller.service.BackupInstallerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tianlib on 9/13/2017.
 */
@Api
@RestController
@RequestMapping(value = "/itsma")
public class BackupController {
  private Log logger = LogFactory.getLog(BackupController.class);

  @Autowired
  private BackupInstallerService backupInstallerService;

  @ApiOperation(value = "Endpoint for backup itsma suite", notes = "")
  @ApiResponses(value = {
      @ApiResponse(code = 202, message = "backup started"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 500, message = "Server internal error")
  })
  @RequestMapping(value = "/backup", method = RequestMethod.POST)
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  public void backup() throws Exception {
    logger.info("Accept request for backup...");
    backupInstallerService.backup();
  }

  @ApiOperation(value = "Endpoint for getting the status of backup", notes = "")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 500, message = "Server internal error")
  })
  @RequestMapping(value = "/backup", method = RequestMethod.GET, produces = "application/json")
  @ResponseStatus(value = HttpStatus.OK)
  public String backupStatus() throws Exception {
    logger.info("Accept request for getting backup status...");
    return backupInstallerService.backupStatus();
  }
}
