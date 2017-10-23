import React, { Component } from 'react';
import { browserHistory } from 'react-router';
import {Col,Button} from 'react-bootstrap';
import {FormattedMessage} from 'react-intl';
import Header from '../components/Header';
import CommonList from '../components/CommonList'
import { Scrollbars } from 'react-custom-scrollbars';
import LoadingPage from '../components/LoadingPage';
import { BASE_NAME } from '../constants/ServiceConfig';
import {startUpdate,statusUpdate,updateRunning,updateFinished} from '../actions/service';
import {getCurrentDate} from '../util/timeTag';


let timer = null;

class StartServices extends Component {

    constructor(props) {
        super(props);

        this.parentWnd = window.parent;
        if (this.parentWnd.enableBack) {
            this.parentWnd.enableBack(true);
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
        this.props.dispatch(statusUpdate());
    }

    componentWillUnmount(){
        if(timer){
            clearInterval(timer);
            timer = null;
        }
    }

    componentWillReceiveProps(nextProps){
        if(nextProps.phaseName.toUpperCase() == 'UPDATING'){
            if(this.isUpdateComplete(nextProps.servicestatus)){
             if (this.parentWnd.enableBack) {
                 this.parentWnd.enableNext(true);
             }
             if(timer){
                 clearInterval(timer);
                 timer = null;
             }
             this.setState({disableBtn:true});
             this.props.dispatch(updateFinished());
            }else{
                 this.setState({disableBtn:true});
                 if(!timer){
                     timer = setInterval(() => this.refreshData(), 2000);
                 }
            }
         }
        if(nextProps.startInfo == 'UPDATE STARTED'){
             if(timer){
                 clearInterval(timer);
                 timer = null;
             }
             timer = setInterval(() => this.refreshData(), 2000);
             this.props.dispatch(updateRunning());
        }
     }

    clickBack = () => {
        browserHistory.push(BASE_NAME +'shutdown');
    }
    
    clickNext = () => {
        browserHistory.push(BASE_NAME + 'summary');
    }

    refreshData(){
        this.props.dispatch(statusUpdate());
    }

    startService = () => {
        if(timer){
            clearInterval(timer);
            timer = null;
        }
        this.setState({disableBtn:true});
        this.props.dispatch(startUpdate());
    }

     //check if every service was updated and start
     isUpdateComplete = (servicestatusList) => {
        let isComplete = true;
        for(let index in servicestatusList){
            if(servicestatusList[index].status == 'TERMINATED' || servicestatusList[index].status == 'READY'){
                isComplete = false;
                return isComplete;
            }
        }
        return isComplete;
    }

    render(){

        let listHeight = this.props.listHeight *2+50;
        let disableBtn = this.state.disableBtn;
        let servicestatus = this.props.servicestatus;
        let isLoaded = this.props.isLoaded;
        let currentDate = getCurrentDate();

        let lists = [];
        for(let index in servicestatus){
            let name = servicestatus[index].name;
            let status = servicestatus[index].status;
            lists.push({
                'name': name,
                'status': status
            });
        }
        
        return(
            <div>
                <Header style='navbar-cus-dark' title='Services.title' subtitle={currentDate}  />
                <Col xs={12} className='backup'  style={{paddingLeft:'32px',paddingRight:'32px'}}>

                    <Col xs={12} className='colFull backupTitle backupBtn' style={{marginTop:'30px',position:'relative',paddingBottom:'30px',borderBottom:'1px solid #cccccc'}} >
                        <i className='fa fa-list-ul icon' aria-hidden='true'></i>
                        <FormattedMessage id='Services' />
                        <div className='backupBtn pull-right' style={{display:'inline-block'}}>
                            <Button  className='pull-right' disabled={disableBtn} onClick={()=> this.startService()}><FormattedMessage id='UpdateNow' /></Button>
                        </div>
                    </Col>

                    <Scrollbars  style={{height:`${listHeight}`}}>
                        <Col xs={12} className='colFull' style={{marginTop:'18px'}}>
                            <CommonList  datas={lists}  />
                        </Col>
                    </Scrollbars>

                </Col>
                

            </div>
        );
    }

}

export default StartServices;