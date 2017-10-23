import React, { Component } from 'react';
import { browserHistory } from 'react-router';
import Header from '../components/Header';
import {FormattedMessage} from 'react-intl';
import {Col,Well} from 'react-bootstrap';
import { BASE_NAME } from '../constants/ServiceConfig';
import {getCurrentDate} from '../util/timeTag';

class Summary extends Component {

    constructor(props) {
        super(props);
        this.state={
            summary:true               //true success  false failed
        };
        this.parentWnd = window.parent;
        if (this.parentWnd.enableBack) {
            this.parentWnd.enableBack(true);
            this.parentWnd.registerOnBackHandler(this.clickBack);
            this.parentWnd.enableNext(false);
            //this.parentWnd.registerOnNextHandler(this.clickNext);
        }
    }

   
    clickBack = () => {
        browserHistory.push(BASE_NAME + 'service');
    }

    showSummary(){
        if(this.state.summary){
            return(
            <Col xs={12} >
                <div style={{marginLeft:'30px',marginRight:'30px',backgroundColor:'rgba(211, 247, 187, 0.22)',marginTop:'75px',height:'300px'}}>
                    <div className='round'>
                        <div className='ion ion-checkmark roundIcon'>    </div>
                    </div>

                <Col xs={12} className='summaryInfo1'>
                    <FormattedMessage  id='summaryInfo1'  />
                </Col>

                <Col xs={12} className='summaryInfo2'>
                    <FormattedMessage id='summaryInfo2'  />
                </Col>

                <Col xs={12} style={{marginTop:'40px'}}>
                    <Col style={{width:'640px',margin:'auto'}}>
                        <Well>
                            <span style={{padding:'8px 22px 11px 49px',lineHeight:'21px'}}>https://shc-am-danny01.hpeswlab.net/itsmaconfig/Landingpage.html</span>
                            <span className='ion ion-arrow-right-c summaryIcon'></span>
                        </Well>
                    </Col>
                </Col>

                </div>
            </Col>
            );
        }else{
            return(
            <Col xs={12} >
                <div style={{paddingLeft:'30px',paddingRight:'30px',backgroundColor:'#fff7f7',marginTop:'75px',height:'300px'}}>
                    <div className='square'>
                        <div className='ion ion-plus roundIcon'>    </div>
                    </div>

                    <Col xs={12} className='summaryInfo1error'>
                        <FormattedMessage  id='Start Service faild'  />
                    </Col>

                    <Col xs={12} className='summaryInfo2'>
                        <FormattedMessage id='Review the following information for details, and check the installation log files. 
For troubleshooting information, refer to the ITSMA documentation: '  />
                    </Col>

                    <Col xs={12} style={{marginTop:'40px'}}>
                        <Col style={{width:'640px',margin:'auto'}}>
                            <Well>
                                <span style={{padding:'8px 22px 11px 49px',lineHeight:'21px'}}>https://docs.software.hpe.com</span>
                                <span className='ion ion-arrow-right-c summaryIcon'></span>
                            </Well>
                        </Col>
                    </Col>

                </div>
            </Col>
            );
        }
    }

    render(){
        let currentDate = getCurrentDate();
        
        return(
             <div className='summary'>
                <Header style='navbar-cus-dark' title='summary.title' subtitle={currentDate}  />

                {this.showSummary()}
            </div>
        );
    }

}

export default Summary;