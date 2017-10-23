import React from 'react'
import { Modal, Button,Image } from 'react-bootstrap';
import {FormattedMessage} from 'react-intl';
import './MessageDialog.scss';
import getImgPath from '../../util/images'
const Dialog = ({ show, title, buttons, children , onExist}) => (
    <div className="static-modal ">
        <Modal show={show}  onExit={onExist}  className="MessageDialog">
            <Modal.Header>
                {title}
            </Modal.Header>
            <Modal.Body>
                {children}
            </Modal.Body>
            <Modal.Footer>
                {buttons}
            </Modal.Footer>
        </Modal>
    </div>
);

class MessageDialog extends React.Component{
    constructor(props) {
        super(props);
        this.state={show:true};
    }
    closeModal = (event) => {
        if(this.props.eventClick){ this.props.eventClick(event); }
    }

    _warnYesHandler =() =>{
        if(this.props.warnYesHandler){this.props.warnYesHandler();}

    }

    _warnNoHandler =() =>{
        this.setState({show:false});
        if(this.props.warnNoHandler){this.props.warnNoHandler();}
    }
    
    _existHandler =() =>{
        this.setState({show:true});
    }

    
    render(){
        let title_waring;
        let buttons;
        if(this.props.type == 'error'){
             title_waring = <div> <Image className='noWarning' src={getImgPath('Error24px.png')} /> <FormattedMessage id='error' /><div className="close"><span onClick={()=>this._warnNoHandler()}>×</span></div></div>;
             buttons =  buttons = [
                    <Button key="1" className='whiteButton-big'  bsClass='btn pull-left'  onClick={()=>this._warnYesHandler()}><FormattedMessage id="ok"  /></Button>
                ];
                   return(
                       <Dialog show={this.props.show&&this.state.show} children={this.props.children} buttons={buttons} title={title_waring} ></Dialog>
                       );
        }else if(this.props.type == 'success'){
             title_waring = <div> <Image className='noWarning' src={getImgPath('Ok_32px.png')} /> <FormattedMessage id='success' /><div className="close"><span onClick={()=>this._warnNoHandler()}>×</span></div></div>;
             buttons =  buttons = [
                    <Button key="1" className='whiteButton-big' bsClass='btn pull-left'  onClick={()=>this._warnYesHandler()}><FormattedMessage id="ok"  /></Button>
                ];
                   return(
                       <Dialog show={this.props.show&&this.state.show} children={this.props.children} buttons={buttons} title={title_waring} ></Dialog>
                       );

        }else{
             title_waring = <div> <Image  src={getImgPath('warning24px.png')} /> <FormattedMessage id='test.warning' /><div className="close"><span onClick={()=>this._warnNoHandler()}>×</span></div></div>;
             buttons =  buttons = [
                    <Button key="1"  className='whiteButton-big' bsClass='btn pull-left'  onClick={()=>this._warnYesHandler()}><FormattedMessage id="yes"  /></Button>,
                    <Button key="2"  className='whiteButton-big' bsClass='btn pull-left'  onClick={()=>this._warnNoHandler()}><FormattedMessage id="cancel" /></Button>
                ];

              return(
                      <Dialog show={this.props.show&&this.state.show}  children={this.props.children} buttons={buttons} title={title_waring}  onExist={this._existHandler.bind(this)}>
                      </Dialog>
                     )
        }
    }
}


export default MessageDialog;
