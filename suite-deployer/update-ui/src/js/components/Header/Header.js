import React from 'react';
import {Navbar, Col} from 'react-bootstrap';
import {FormattedMessage} from 'react-intl';
import './Header.scss';

class Header extends React.Component {

  constructor(props) {
    super(props);
  }

  ifTagExist(){
    if(this.props.tag){
      return(
        <div>
          <Col className='tagBackground'></Col>
          <span className='tagText'> <FormattedMessage id={this.props.tag} />  </span>
       </div>
      );
    }
  }


  render() {
    return (
      <Col xs={12} className="colFull">
      <Navbar
        staticTop
        componentClass="header"
        className={'bs-docs-nav noMarginBottom colFull navbar-header-cus ' + `${this.props.style}`}
        role="banner"
        fluid>

      <Col  className='headerTitle'>
            <FormattedMessage id={this.props.title}/>
      </Col>

      
      <Col  className='headerSubTitle'>
            <span>{this.props.subtitle}</span>
      </Col>

      {this.ifTagExist()}

      </Navbar>

      </Col>
    );
  }
}

Header.propTypes = {
    title: React.PropTypes.string,
    subtitle: React.PropTypes.string,
    tag:React.PropTypes.string,
    style:React.PropTypes.string
};


export default Header;