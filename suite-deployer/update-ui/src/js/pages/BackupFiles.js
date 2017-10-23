import React, { Component } from 'react';
import { browserHistory } from 'react-router';
import {Col,Button} from 'react-bootstrap';
import {FormattedMessage} from 'react-intl';
import Header from '../components/Header';
import TableList from '../components/TableList'
import { Scrollbars } from 'react-custom-scrollbars';
import {startBackup,statusBackup,clearBackupStatusInit} from '../actions/backup';
import LoadingPage from '../components/LoadingPage';
import {timeTag, getCurrentDate} from '../util/timeTag';
import findIndex from 'lodash/findIndex';
import { BASE_NAME } from '../constants/ServiceConfig';

const backupSteps = [
    'BACKUP_VALIDATION',
    'GENERATE_METADATA',
    'BACKUP_CONFIGMAP_YAML',
    'BACKUP_SERVICE_YAML',
    'BACKUP_POD_YAML',
    'BACKUP_INGRESS_YAML',
    'BACKUP_DEPLOYMENT_YAML',
    'BACKUP_DAEMONSET_YAML',
    'BACKUP_SECRET_YAML',
    'BACKUP_PV_YAML',
    'BACKUP_PVC_YAML',
    'BACKUP_PV_VOLUMES',
    'COMPRESS_BACKUP_PACKAGE',
    'REMOVE_COMPRESSED_PACKAGE'
];

let timer = null;

class BackupFiles extends Component {

    constructor(props) {
        super(props);

        this.parentWnd = window.parent;
        if (this.parentWnd.enableBack) {
            this.parentWnd.enableCancel(false);
            this.parentWnd.enableBack(false);
            this.parentWnd.registerOnBackHandler(this.clickBack);
            this.parentWnd.enableNext(false);
            this.parentWnd.registerOnNextHandler(this.clickNext);
        }
        this.state = {
            disableBtn: false
        };

    }

    componentDidMount() {
        if(timer){
            clearInterval(timer);
            timer = null;
        }
        this.props.dispatch(clearBackupStatusInit());
        this.props.dispatch(statusBackup());
    }
    

    componentWillUnmount(){
        if(timer){
            clearInterval(timer);
            timer = null;
        }
    }
    

    //no need for timer if backupStatus had been finished
    componentWillReceiveProps(nextProps){
        if(nextProps.backupStatus == 'SUCCESS'){
            if(timer){
                clearInterval(timer);
                timer = null;
            }
            if (this.parentWnd.enableBack) {
                this.parentWnd.enableNext(true);
            }
            this.setState({disableBtn:false});
        }else if(nextProps.backupStatus == 'FAILED' || nextProps.backupStatus == null){
            if(timer){
                clearInterval(timer);
                timer = null;
            }
            this.setState({disableBtn:false});
        }else if(nextProps.backupStatus == 'BACKUPING' || nextProps.backupStatus == 'WAIT'){
            this.setState({disableBtn:true});
            if(!timer){
                timer = setInterval(() => this.refreshStatus(), 1000);
            }
        }
        if(nextProps.backupStatusInit == 'BACKUP PROCESS CREATED'){
            if(timer){
                clearInterval(timer);
                timer = null;
           }
            timer = setInterval(() => this.refreshStatus(), 1000);
            this.props.dispatch(clearBackupStatusInit());
        }
     }
     
    clickBack = () => {
        browserHistory.push(BASE_NAME + 'info');
    }
    
    clickNext = () => {
        browserHistory.push(BASE_NAME + 'database');
    }

    refreshStatus(){
        this.props.dispatch(statusBackup());
    }

    startBackup = () => {
        if(timer){
             clearInterval(timer);
             timer = null;
        }
        this.setState({disableBtn:true});
        this.props.dispatch(startBackup());
    }

    //listItem status
    getListItemStatus(backupStatus){
        switch(backupStatus){
            case 'WAIT':
                return 'pending';
            case 'BACKUPING':
                return 'pending';
            case 'FAILED':
                return 'error';
            case 'SUCCESS':
                return 'success';
            default:
                return 'finish';
        }
    }


    render(){
        let listHeight = this.props.listHeight *2+50;
        let disableBtn = this.state.disableBtn;
        let backup_list = this.props.backup_list;
        let isLoaded = this.props.isLoaded;
        let backupStatus = this.props.backupStatus;
        let backupProcess = this.props.backupProcess;
        let backupErrorCode = this.props.backupErrorCode;
        let backupPackageName = this.props.backupPackageName;
        let backupStartTime = this.props.backupStartTime;
        let currentDate = getCurrentDate();

        let showLists = [];
        for(let file in backup_list){
            let name = backup_list[file].itom_suite_backup_package_name;
            let time =timeTag(backup_list[file].itom_suite_backup_time);
            let status = 'finish';
            showLists.unshift({
                'name': name,
                'timeTag': time,
                'status': status,
                'startBackup':false,
                'percent':0
            });
        }
        let currentBackupFileIndex = findIndex(showLists,function(o) { return o.name == backupPackageName; })
        if(currentBackupFileIndex != -1){
                    showLists[currentBackupFileIndex].status = this.getListItemStatus(backupStatus);
                    if(backupStatus == 'FAILED'){
                        showLists[currentBackupFileIndex].errorMsg = backupErrorCode;
                        showLists[currentBackupFileIndex].startBackup = false;
                        showLists[currentBackupFileIndex].percent = 0;
                    }else if(backupStatus == 'BACKUPING' ||  backupStatus == 'SUCCESS'){
                        showLists[currentBackupFileIndex].startBackup = true;
                        let procedureIndex = findIndex(backupSteps,function(o) { return o == backupProcess; });
                        showLists[currentBackupFileIndex].percent = 100/(backupSteps.length)*(procedureIndex+1);
                    }else if(backupStatus == 'WAIT'){
                        showLists[currentBackupFileIndex].startBackup = true;
                        showLists[currentBackupFileIndex].percent = 0;
                    }
        }else if(backupStatus != null){
            let newStatus = this.getListItemStatus(backupStatus);
            showLists.unshift({
                'name': backupPackageName,
                'timeTag': timeTag(backupStartTime),
                'status': newStatus
            });
            if(backupStatus == 'FAILED'){
                showLists[0].errorMsg = backupErrorCode;
                showLists[0].startBackup = false;
                showLists[0].percent = 0;
            }else if(backupStatus == 'BACKUPING' ||  backupStatus == 'SUCCESS'){
                showLists[0].startBackup = true;
                let procedureIndex = findIndex(backupSteps,function(o) { return o == backupProcess; });
                showLists[0].percent = 100/(backupSteps.length)*(procedureIndex+1);
            }else if(backupStatus == 'WAIT'){
                showLists[0].startBackup = true;
                showLists[0].percent = 0;
            }
        }


        return(
            <div>
                <Header style='navbar-cus-dark' title='backup.title' subtitle={currentDate}  />
                <Col xs={12} className='backup'  style={{paddingLeft:'32px',paddingRight:'32px'}}>

                    <Col xs={12} className='colFull backupTitle backupBtn' style={{marginTop:'30px',position:'relative',paddingBottom:'30px',borderBottom:'1px solid #cccccc'}} >
                        <i className='icon ion-social-buffer' aria-hidden='true'></i>
                        <FormattedMessage id='backup.title'/>
                        {(backupStatus == 'FAILED')? <Button  className='pull-right' disabled={disableBtn} onClick={()=> this.startBackup()}>Retry</Button>
                            : <Button  className='pull-right' disabled={disableBtn} onClick={()=> this.startBackup()}>Backup Now</Button>
                        }
                    </Col>
                    <Scrollbars  style={{height:`${listHeight}px`}}>
                        <Col xs={12} className='colFull'>
                            <TableList  datas={showLists}    />
                        </Col>
                    </Scrollbars>
                </Col>
                <LoadingPage show={!isLoaded} />
            </div>
        );
    }

}

export default BackupFiles;