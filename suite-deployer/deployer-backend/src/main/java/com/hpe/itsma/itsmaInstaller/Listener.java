package com.hpe.itsma.itsmaInstaller;

import com.hpe.itsma.itsmaInstaller.bean.KubeResourceStatus;
import com.hpe.itsma.itsmaInstaller.bean.RuntimeProfile;
import com.hpe.itsma.itsmaInstaller.service.BackupInstallerService;
import com.hpe.itsma.itsmaInstaller.service.ItsmaSuite;
import com.hpe.itsma.itsmaInstaller.service.K8sRestClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by zhongtao on 8/28/2017.
 */
@Component
public class Listener implements ApplicationListener<ApplicationReadyEvent> {
  private static Log logger = LogFactory.getLog(Listener.class);

  @Autowired
  private BackupInstallerService backupInstallerService;

  @Autowired
  private K8sRestClient k8sRestClient;

  @Autowired
  private RuntimeProfile runtimeProfile;

  @Autowired
  private ItsmaSuite itsmaSuite;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
    try {
      itsmaSuite.restoreDeployerContext();
      itsmaSuite.createStatusRefreshThread();

      KubeResourceStatus status = k8sRestClient.getServiceStatusByName(runtimeProfile.getNamespace(), "itom-itsma-backup-svc");
      if (status.getStatus().equalsIgnoreCase("Running")) {
        logger.info("[itom-itsma-backup-svc] is alreay Running. Not create [itom-itsma-backup-svc] again.");
        return;
      } else {
        backupInstallerService.createBackupSVC();
      }
    } catch (Exception e) {
      logger.error("Fail to create [itom-itsma-backup.yaml] " + e);
    }


  }
}