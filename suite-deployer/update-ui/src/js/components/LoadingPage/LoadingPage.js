import React, {Component} from 'react';
import Loader from 'halogen/FadeLoader';
import { Col, Modal} from 'react-bootstrap';
import './LoadingPage.scss';
 
class LoadingPage extends Component{
  	constructor(props) {
		super(props);
	}
  render(){
    return (
      <Modal show={this.props.show} className='LoadingPage'>
          <Col xs={12} >
            <Col xs={5} >
            </Col>
            <Col xs={2} >
                <Loader color="#01A982" size="16px" margin="4px"/>
            </Col>
            <Col xs={5} >
            </Col>
          </Col>
        </Modal>
    )
  }
}
export default LoadingPage;