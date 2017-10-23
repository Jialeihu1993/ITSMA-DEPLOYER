import React, { Component } from 'react';
import { browserHistory } from 'react-router';
import Header from '../components/Header';
import ComponentsList from '../components/ComponentsList';
import { Scrollbars } from 'react-custom-scrollbars';
import { BASE_NAME } from '../constants/ServiceConfig';

import {Col} from 'react-bootstrap';

const dataTest = [
                {name:'ITOM AutoPass'},
                {name:'IDM Service'},
                {name:'Open LDAP Service'},
                {name:'Ingress Service'},
                {name:'Service Management Service'},
                {name:'Smart Analytics Service'},
                {name:'ITOM AutoPass'},
                {name:'IDM Service'},
                {name:'Open LDAP Service'},
                {name:'Ingress Service'}
             ];

class VersionInfo extends Component {

    constructor(props) {
        super(props);

        
        this.parentWnd = window.parent;
        if (this.parentWnd.enableBack) {
            this.parentWnd.enableCancel(true);
            this.parentWnd.enableBack(false);
            this.parentWnd.enableNext(true);
            this.parentWnd.registerOnNextHandler(this.clickNext);
        }

    }

    componentDidMount(){
        //this.restore = this.restore.bind(this);
    }


    clickNext = () => {
        browserHistory.push(BASE_NAME + 'backup');
    }

    render(){
        let listHeight = this.props.listHeight;
        return(
            <div>
                <Header style='navbar-cus-dark' title='ITSMA Suite' subtitle='VERSION: 2018.02' tag='NEW!'  />
                <Scrollbars  style={{height:`${listHeight}`}}>
                    <Col xs={12} className='colFull'>
                        <ComponentsList  datas={dataTest} />
                    </Col>
                </Scrollbars>
                
                <Header style='navbar-cus-light' title='ITSMA Suite' subtitle='VERSION: 2017.10' tag='CURRENT VERSION'  />
                <Scrollbars  style={{height:`${listHeight}`}}>
                    <Col xs={12} className='colFull' >
                        <ComponentsList  datas={dataTest} />
                    </Col>
                </Scrollbars>
            </div>
        );
    }

}

export default VersionInfo;