// CONFIG REST SERVICE
function fixBaseName(baseName) {
  let fixedBaseName = baseName;
  if (fixedBaseName) {
    fixedBaseName = fixedBaseName.trim();
    if (!(fixedBaseName[fixBaseName.length-1]!='/')) {
      fixedBaseName += '/';
    }
  } else {
    fixedBaseName = '/';
  }
  return fixedBaseName;
}

export const BASE_NAME = fixBaseName(window.config_basename);
export const INIT_LOCALE = BASE_NAME + 'api/init/locale';

export const SHUTDOWN_START_URL = BASE_NAME + 'api/upgrade/shutdown';
export const SHUTDOWN_STATUS_URL = BASE_NAME + 'api/upgrade/shutdown/status';


export const BACKUP_START_URL = BASE_NAME + 'suitebackup/backup';
export const BACKUP_STATUS_URL = BASE_NAME + 'suitebackup/backup/status';
export const BACKUP_FILES_URL = BASE_NAME + 'suitebackup/backup/packagelist';
export const BACKUP_SERVICES_URL = BASE_NAME + 'api/config/backupservice';

export const UPDATE_START_URL = BASE_NAME + 'api/upgrade/update';
export const UPDATE_STATUS_URL = BASE_NAME + 'api/upgrade/update/status';

export const ROLLBACK_START_URL = BASE_NAME + 'api/upgrade/restore';
export const ROLLBACK_STATUS_URL = BASE_NAME + 'api/upgrade/restore/status';

export const UPGRADE_LOAD_DATA_URL = BASE_NAME + 'api/upgrade/upgrade';
export const SERVICE_LOAD_DATA_URL = BASE_NAME + 'api/upgrade/service';
export const ROLLBACK_LOAD_DATA_URL = BASE_NAME + 'api/upgrade/rollback';

