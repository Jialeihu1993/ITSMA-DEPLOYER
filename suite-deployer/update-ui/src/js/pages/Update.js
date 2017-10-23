import React, { Component } from 'react';
import { browserHistory } from 'react-router';
import Header from '../components/Header';
import {Col,Button} from 'react-bootstrap';
import {FormattedMessage} from 'react-intl';
import { Steps,Progress } from 'antd';
import {loadData} from '../actions/upgrade';
import LoadingPage from '../components/LoadingPage';

const Step = Steps.Step;
let timer = null;
class Update extends Component {

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
            this.props.dispatch(loadData(false,'getInitial'));
            if(timer){
                clearInterval(timer);
                timer = null;
            }
    }

    componentWillReceiveProps(nextProps){
        if(nextProps.upgradeStatus){
            this.parentWnd.enableNext(true);
            this.setState({disableBtn:true});
            if(timer){
                clearInterval(timer);
                timer = null;
            }
        }else if(nextProps.complete){
             this.setState({disableBtn:false});
             if(timer){
                clearInterval(timer);
                timer = null;
            }
        }
        
     }

    clickBack = () => {
        //browserHistory.push('/suiteConfig/upgrade/backup');
        browserHistory.push('/backup');
    }
    

    clickNext = () => {
        //browserHistory.push('/suiteConfig/upgrade/result');
        browserHistory.push('/service');
        
    }

    refreshData(action){
        this.props.dispatch(loadData(false,action));
    }

    startUpgrade = () => {
        this.setState({disableBtn:true});
        this.props.dispatch(loadData(false,'startUpgrade'));
        if(timer){
             clearInterval(timer);
             timer = null;
        }
        timer = setInterval(() => this.refreshData('upgradeStatusError'), 2000);
        //start Timer
    }

    retryUpgrade = () =>{
        this.setState({disableBtn:true});
        this.props.dispatch(loadData(false,'startUpgrade'));
        if(timer){
             clearInterval(timer);
             timer = null;
        }
        timer = setInterval(() => this.refreshData('upgradeStatus'), 2000);
    }

    rollBack = () =>{
        if(timer){
            clearInterval(timer);
            timer = null;
        }
        browserHistory.push('/rollback');
    }

    render(){
        let disableBtn = this.state.disableBtn;
        let dataTest = this.props.lists;
        let isLoaded = this.props.isLoaded;
        let complete = this.props.complete;
        let upgradeStatus = this.props.upgradeStatus;

        let configMap = dataTest[0].per;
        let vault = dataTest[1].per;
        let ingress = dataTest[2].per;
        let others = dataTest[3].per;

        let configMapStatus = dataTest[0].status;
        let vaultStatus = dataTest[1].status;
        let ingressStatus = dataTest[2].status;
        let othersStatus = dataTest[3].status;

        let step1 = (configMap == 100 || configMapStatus == false)?1:0;
        let step2 = (vault == 100 || vaultStatus == false)?1:0;
        let step3 = (ingress == 100 || ingressStatus == false)?1:0;


        return(
             <div>
                <Header style='navbar-cus-dark' title='Upgrade' subtitle='UPDATE 01 JUNE,2017'  />

                <Col xs={12} className='backup'  style={{paddingLeft:'32px',paddingRight:'32px'}}>

                    <Col xs={12} className='colFull backupTitle backupBtn' style={{marginTop:'30px',position:'relative',paddingBottom:'30px',borderBottom:'1px solid #cccccc'}} >
                        <i className='fa ion-android-upload icon' aria-hidden='true'></i>
                        <FormattedMessage id='Upgrading'/>
                        {(complete&&(!upgradeStatus))?
                        <div>
                            <Button  className='pull-right' disabled={disableBtn} onClick={()=>{this.rollBack()}}>Roll back</Button>
                            <Button  className='pull-right' disabled={disableBtn} style={{marginRight:'30px'}} onClick={()=>this.retryUpgrade()}>Retry</Button>
                        </div>
                            : <Button  className='pull-right' disabled={disableBtn} onClick={()=> this.startUpgrade()}>Upgrade Now</Button>
                        }

                    </Col>

                </Col>

                <Col xs={12} className='colFull updateProgress' style={{position:'relative'}} >
                   
                        <Col  style={{position:'absolute',top:'40px',left:'70px',zIndex:'999'}}>
                            <Progress type="circle" percent={configMap} status={configMapStatus?'active':'exception'} />
                        </Col>
                        <Col style={{width:'300px',position:'absolute',top:'105px',left:'138px'}}>
                            <Steps progressDot current={step1} >
                                <Step   />
                                <Step  />
                            </Steps>
                        </Col>
                            <span style={{position:'absolute',top:'180px',left:'80px',fontSize:'24px',fontFamily:'HPEMetricSemibold',lineHeight:'30px',color:'#767676'}}>Configmap</span>
                        
                        <Col style={{position:'absolute',top:'40px',left:'372px',zIndex:'999'}}>
                            <Progress type="circle" percent={vault} status={vaultStatus?'active':'exception'}/>
                        </Col>
                        <Col style={{width:'300px',position:'absolute',top:'105px',left:'440px'}}>
                            <Steps progressDot current={step2} >
                                <Step   />
                                <Step  />
                            </Steps>
                        </Col>
                            <span style={{position:'absolute',top:'180px',left:'410px',fontSize:'24px',fontFamily:'HPEMetricSemibold',lineHeight:'30px',color:'#767676'}}>Vault</span>


                        <Col style={{position:'absolute',top:'40px',left:'675px',zIndex:'999'}}>
                            <Progress type="circle" percent={ingress} status={ingressStatus?'active':'exception'} />
                        </Col>
                        <Col style={{width:'300px',position:'absolute',top:'105px',left:'743px'}}>
                            <Steps progressDot current={step3} >
                                <Step   />
                                <Step  />
                            </Steps>
                        </Col>
                            <span style={{position:'absolute',top:'180px',left:'710px',fontSize:'24px',fontFamily:'HPEMetricSemibold',lineHeight:'30px',color:'#767676'}}>Ingress</span>


                        <Col style={{position:'absolute',top:'40px',left:'978px',zIndex:'999'}}>
                            <Progress type="circle" percent={others} status={othersStatus?'active':'exception'} />
                        </Col>
                            <span style={{position:'absolute',top:'180px',left:'1020px',fontSize:'24px',fontFamily:'HPEMetricSemibold',lineHeight:'30px',color:'#767676'}}>Others</span>

                </Col>

                <LoadingPage show={!isLoaded} />
            </div>
        );
    }

}

export default Update;