// *******CONFIG Action Types ********

//shutdown
export const SHUTDOWN_START = 'shutdown_start';
export const SHUTDOWN_STATUS = 'shutdown_status'
export const SHUTDOWN_RUNNING = 'shutdown_running';
export const SHUTDOWN_FINISHED = 'shutdown_finished'


//backup
export const BACKUP_START = 'backup_start';
export const BACKUP_STATUS = 'backup_status';
export const BACKUP_FILES = 'backup_files';
export const BACKUP_SERVICES = 'backup_services';
export const BACKUP_CLEARBACKUPINIT = 'backup_clearbackupinit';

//upgrade and start service  this is one procedure now
export const UPDATE_START = 'update_start';
export const UPDATE_STATUS = 'update_status'
export const UPDATE_RUNNING = 'update_running';
export const UPDATE_FINISHED = 'update_finished'

export const ROLLBACK_START = 'rollback_start';
export const ROLLBACK_STATUS = 'rollback_status'
export const ROLLBACK_RUNNING = 'rollback_running';
export const ROLLBACK_FINISHED = 'rollback_finished'

//service
export const SERVICE_LOAD_DATA = 'service_load_data';




