package com.hpe.itsma.itsmaInstaller.bean;

/**
 * Created by tianlib on 7/10/2017.
 */
public class IdmSigningKey {
  private String name;
  private String displayName;
  private String resourceType;
  private String value;
  private String defaultValue;
  private String id;
  private String description;
  private boolean deprecated;
  private boolean modifiable;
  private boolean needRestart;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated(boolean deprecated) {
    this.deprecated = deprecated;
  }

  public boolean isModifiable() {
    return modifiable;
  }

  public void setModifiable(boolean modifiable) {
    this.modifiable = modifiable;
  }

  public boolean isNeedRestart() {
    return needRestart;
  }

  public void setNeedRestart(boolean needRestart) {
    this.needRestart = needRestart;
  }
}
