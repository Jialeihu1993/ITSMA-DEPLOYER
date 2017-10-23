package com.hpe.itsma.itsmaInstaller.bean;

import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianlib on 7/4/2017.
 */
@ApiModel
public class InstallRequestPayload {
  private String admin_password;
  private String itsma_edition;
  private String domain_name;
  private String product_language;
  private String default_registry_url;

  private String db_engine;

  private List<ItsmaService> activated_services = new ArrayList<>();
  private List<ItsmaServiceDB> database = new ArrayList<>();

  class ItsmaService {
    private String name;
    private String registry_url;
    private String controller_img_tag;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getRegistry_url() {
      return registry_url;
    }

    public void setRegistry_url(String registry_url) {
      this.registry_url = registry_url;
    }

    public String getController_img_tag() {
      return controller_img_tag;
    }

    public void setController_img_tag(String controller_img_tag) {
      this.controller_img_tag = controller_img_tag;
    }
  }

  class ItsmaServiceDB {
    private String internal;
    private String product_name;
    private String db_engine;
    private String db_server;
    private String db_port;
    private String db_inst;
    private String db_login;
    private String db_password;

    public String getInternal() {
      return internal;
    }

    public void setInternal(String internal) {
      this.internal = internal;
    }

    public String getProduct_name() {
      return product_name;
    }

    public void setProduct_name(String product_name) {
      this.product_name = product_name;
    }

    public String getDb_engine() {
      return db_engine;
    }

    public void setDb_engine(String db_engine) {
      this.db_engine = db_engine;
    }

    public String getDb_server() {
      return db_server;
    }

    public void setDb_server(String db_server) {
      this.db_server = db_server;
    }

    public String getDb_port() {
      return db_port;
    }

    public void setDb_port(String db_port) {
      this.db_port = db_port;
    }

    public String getDb_inst() {
      return db_inst;
    }

    public void setDb_inst(String db_inst) {
      this.db_inst = db_inst;
    }

    public String getDb_login() {
      return db_login;
    }

    public void setDb_login(String db_login) {
      this.db_login = db_login;
    }

    public String getDb_password() {
      return db_password;
    }

    public void setDb_password(String db_password) {
      this.db_password = db_password;
    }
  }
}
