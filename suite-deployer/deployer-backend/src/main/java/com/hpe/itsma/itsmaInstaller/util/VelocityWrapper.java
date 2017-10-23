package com.hpe.itsma.itsmaInstaller.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Wrapper of Apache velocity
 *
 * @author libin.tian
 * @since 11/2/2016.
 */
public class VelocityWrapper {
  private static final Log logger = LogFactory.getLog(VelocityWrapper.class);

  private VelocityEngine engine;
  private VelocityContext context;

  private String templatePath;
  private String outputPath;
  private Map<String, Object> properties;

  public VelocityWrapper() {
    //do nothing
  }

  /**
   * Constructs a {@code VelocityWrapper} with context
   *
   * @param properties context values held by a map
   */
  public VelocityWrapper(String templatePath, String outputPath, Map<String, Object> properties) {
    this.templatePath = templatePath;
    this.outputPath = outputPath;
    this.properties = properties;
  }

  public void init() {
    engine = new VelocityEngine();
    engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
    engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templatePath);
    engine.init();

    context = new VelocityContext(properties);
  }

  /**
   * Transform files in the given directory recursively and output the generated files to specified directory
   *
   * @throws Exception
   */
  public void transformFiles() throws Exception {
    File dir = new File(templatePath);
    File[] files = dir.listFiles();
    if (files != null) {
      for (File f : files) {
        transformFile(f.getPath());
      }
    } else {
      logger.error("No yaml template in path: " + templatePath);
      throw new FileNotFoundException("No yaml template in path: " + templatePath);
    }
  }

  /**
   * Transform one single file and output to the generated file to specified directory
   *
   * @param sourceFilePath The path of template file
   * @throws Exception
   */
  public void transformFile(String sourceFilePath) throws Exception {
    File f = new File(sourceFilePath);
    if (f.exists()) {
      Template t = engine.getTemplate(f.getName());
      try {
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
          outputDir.mkdir();
        }
        FileWriter fw = new FileWriter(outputPath + "/" + f.getName());
        t.merge(context, fw);
        fw.close();
      } catch (IOException e) {
        logger.error("Error when transform file: " + f.getPath());
        throw e;
      }
    } else {
      logger.warn(sourceFilePath + " does NOT exist");
    }
  }

  /**
   * Transform one single file and output the generated file to specified directory
   *
   * @param sourceFilePath The path of template file
   * @param outputFilePath
   * @param properties
   * @throws Exception
   */
  public void transformFile(String sourceFilePath, String outputFilePath, Map<String, Object> properties) throws Exception {
    File file = new File(sourceFilePath);
    String sSourceFileParentDir = file.getParent();
    logger.debug("The parent directory of [" + sourceFilePath + "] is " + sSourceFileParentDir);

    VelocityEngine engine = new VelocityEngine();
    engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
    engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, sSourceFileParentDir);
    engine.init();
    VelocityContext context = new VelocityContext(properties);

    if (file.exists()) {
      Template t = engine.getTemplate(file.getName());
      try {
        File outputFile = new File(outputFilePath);
        String sOutputFileParentDir = outputFile.getParent();
        logger.debug("The parent directory of [" + outputFilePath + "] is " + sOutputFileParentDir);
        File outputFileParentDir = new File(sOutputFileParentDir);
        if (!outputFileParentDir.exists()) {
          outputFileParentDir.mkdirs();
        }
        FileWriter fw = new FileWriter(outputFilePath);
        t.merge(context, fw);
        fw.close();
      } catch (IOException e) {
        logger.error("Error when transform file: " + file.getPath());
        throw e;
      }
    } else {
      logger.warn(sourceFilePath + " does NOT exist");
    }
  }
}
