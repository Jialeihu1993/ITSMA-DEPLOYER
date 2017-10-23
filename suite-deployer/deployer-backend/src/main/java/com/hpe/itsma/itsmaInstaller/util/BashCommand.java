package com.hpe.itsma.itsmaInstaller.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by tianlib on 12/19/2016.
 */
public class BashCommand {
  private static Log logger = LogFactory.getLog(BashCommand.class);

  private String command;

  public BashCommand(String command) {
    this.command = command;
  }

  public String exec() throws Exception {
    Process p;
    try {
      logger.info("Execute: " + command);
      p = Runtime.getRuntime().exec(command);
      p.waitFor();
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(p.getInputStream()));

      String result = reader.readLine();
      logger.debug("Result of command \"" + command + "\": " + result);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
