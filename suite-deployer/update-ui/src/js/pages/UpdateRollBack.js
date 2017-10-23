import React, { Component } from 'react';
import { browserHistory } from 'react-router';
import Header from '../components/Header';
import {Col,Button} from 'react-bootstrap';
import {FormattedMessage} from 'react-intl';
import RollbackList from '../components/RollbackList';
import { Scrollbars } from 'react-custom-scrollbars';
import {startRollback,statusRollback,rollbackRunning,rollbackFinished} from '../actions/rollback';
import {getCurrentDate,timeTag} from '../util/timeTag';
import { BASE_NAME } from '../constants/ServiceConfig';
import {getBackupFiles} from '../actions/backup';
import findIndex from 'lodash/findIndex';
import isUndefined from 'lodash/isUndefined';

const rollbackSteps = [
    'updating.statistic',
    'updatring.vault',
    'updating.install'
];

let timer = null;

class UpdateRollBack extends Component {

    constructor(props) {
        super(props);

        this.parentWnd = window.parent;
        if (this.parentWnd.enableBack) {
            this.parentWnd.enableBack(true);
            this.parentWnd.registerOnBackHandler(this.clickBack);
            this.parentWnd.enableNext(false);
            this.parentWnd.registerOnNextHandler(this.clickNext);
        }
        this.state={
            disableBtn:true,
            startRestore:false
        }

        this.selectedStatus = undefined;
        this.selectedIndex = undefined;
    }

    componentDidMount() {
        if(timer){
            clearInterval(timer);
            timer = null;
        }
        this.props.dispatch(statusRollback());
        this.props.dispatch(getBackupFiles());
    }

    componentWillUnmount(){
        if(timer){
            clearInterval(timer);
            timer = null;
        }
    }

    componentWillReceiveProps(nextProps,nestState){
        if(nextProps.phaseName.toUpperCase() == 'RESTORING'){
            if(this.isRollbackComplete(nextProps.servicestatus)){
             if (this.parentWnd.enableBack) {
                 this.parentWnd.enableNext(true);
             }
             if(timer){
                 clearInterval(timer);
                 timer = null;
             }
             this.setState({disableBtn:true});
             //this.props.dispatch(rollbackFinished());
            }else{
                 this.setState({disableBtn:true});
                 if(!timer){
                     timer = setInterval(() => this.refreshData(), 2000);                 }
            }
         }
        if(nextProps.startInfo == 'ROLLBACK STARTED'){
             if(timer){
                 clearInterval(timer);
                 timer = null;
             }
             timer = setInterval(() => this.refreshData(), 2000);
             this.props.dispatch(rollbackRunning());
        }
     }

    clickBack = () => {
        browserHistory.push(BASE_NAME + '/service');
    }

    clickNext = () => {
        browserHistory.push(BASE_NAME + '/summary');
    }

    refreshButton(canRetry){
        if(canRetry){
            this.setState({disableBtn:false});
        }else{
            this.setState({disableBtn:true});
        }
    }

    refreshData(){
        this.props.dispatch(statusRollback());
    }

    restore(){
        if(timer){
            clearInterval(timer);
            timer = null;
        }
        //this.setState({disableBtn:true});
        this.setState({startRestore:true,disableBtn:true});
        this.props.dispatch(startRollback());
    }

     //check if every service was rollbacked and start
     isRollbackComplete = (servicestatusList) => {
        let isComplete = true;
        for(let index in servicestatusList){
            if(servicestatusList[index].status == 'TERMINATED' || servicestatusList[index].status == 'READY'){
                isComplete = false;
                return isComplete;
            }
        }
        return isComplete;
    }

    refreshList(lsStatus,currentIndex){
        this.selectedIndex = currentIndex;
        this.selectedStatus = lsStatus;
    }

    render(){
        let disableBtn = this.state.disableBtn;
        let startRestore = this.state.startRestore;
        let listHeight = this.props.listHeight *2+50;
        let currentDate = getCurrentDate();
        let backup_list = this.props.backup_list;
        let phaseDetail = this.props.phaseDetail;
        let showLists = [];
        let percent = 0;
        let procedureIndex = findIndex(rollbackSteps,function(o) { return o == phaseDetail; });
        if(procedureIndex != -1){
            percent = 100/(rollbackSteps.length)*(procedureIndex+1);
        }
        
        let file = backup_list[backup_list.length-1];
        
        if(file){
            let name = file.itom_suite_backup_package_name;
            let time =timeTag(file.itom_suite_backup_time);
            showLists.unshift({
                'name': name,
                'timeTag': time,
                'status': 'finished'
            });
        }

        if(!isUndefined(this.selectedIndex)){
            showLists[this.selectedIndex].status = this.selectedStatus;
        }

        return(
             <div>
                <Header style='navbar-cus-dark' title='rollback.title' subtitle={currentDate}  />
                <Col xs={12} className='backup'  style={{paddingLeft:'32px',paddingRight:'32px'}}>

                    <Col xs={12} className='colFull backupTitle backupBtn' style={{marginTop:'30px',position:'relative',paddingBottom:'30px',borderBottom:'1px solid #cccccc'}} >
                        <i className='fa ion-android-upload icon' aria-hidden='true'></i>
                        <FormattedMessage id='rollback.subtitle'/>
                        <Button  className='pull-right' disabled={disableBtn} onClick={()=>this.restore()}>Restore</Button>
                    </Col>
                    <Scrollbars  style={{height:`${listHeight}px`}}>
                        <Col xs={12} className='colFull'>
                            <RollbackList  datas={showLists} startRestore={startRestore} canRetry={(canRetry)=>{this.refreshButton(canRetry)} } percent={percent} refreshList={(lsStatus,currentIndex) => {this.refreshList(lsStatus,currentIndex)}} />
                        </Col>
                    </Scrollbars>
                </Col>
            </div>
        );
    }

}

export default UpdateRollBack;