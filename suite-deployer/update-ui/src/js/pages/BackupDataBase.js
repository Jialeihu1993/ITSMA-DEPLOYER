import React, { Component } from 'react';
import { browserHistory } from 'react-router';
import {Col,ListGroup} from 'react-bootstrap';
import {FormattedMessage} from 'react-intl';
import Header from '../components/Header';
import {getCurrentDate} from '../util/timeTag';
import { BASE_NAME } from '../constants/ServiceConfig';


class BackupDataBase extends Component {

    constructor(props) {
        super(props);

        this.parentWnd = window.parent;
        if (this.parentWnd.enableBack) {
            this.parentWnd.enableBack(true);
            this.parentWnd.registerOnBackHandler(this.clickBack);
            this.parentWnd.enableNext(true);
            this.parentWnd.registerOnNextHandler(this.clickNext);
        }
    }

     
    clickBack = () => {
       
        browserHistory.push(BASE_NAME + 'backup');
    }
    

    clickNext = () => {
        browserHistory.push(BASE_NAME + 'shutdown');
    }

    render(){
        let listHeight = this.props.listHeight *2+50;
        let currentDate = getCurrentDate();
        
        return(
            <div>
                <Header style='navbar-cus-dark' title='backup.database' subtitle={currentDate}  />
                <Col xs={12} className='backup'  style={{paddingLeft:'32px',paddingRight:'32px'}}>

                    <Col xs={12} className='colFull backupTitle backupBtn' style={{marginTop:'30px',position:'relative',paddingBottom:'30px',borderBottom:'1px solid #cccccc'}} >
                        <i className='icon ion-social-buffer' aria-hidden='true'></i>
                        <FormattedMessage id='backup.database'/>
                    </Col>

                    <Col xs={12} className='colFull'>
                        <ListGroup componentClass='ul' className='database'>
                            <li  className="list-group-item titleItem title" >
                                <FormattedMessage id='backup.database.instruction1'/>
                            </li>

                            <li className="list-group-item subTitle" >
                                 <i className='ion ion-android-exit icon'> </i>
                                 <FormattedMessage id='backup.database.instruction2'/>
                            </li>

                            <li className="list-group-item subTitle">
                                <i className='ion ion-android-open icon'> </i>
                                <FormattedMessage id='backup.database.instruction3'/>
                            </li>

                        </ListGroup>
                    </Col>
                </Col>
            </div>
        );
    }

}

export default BackupDataBase;