import React from 'react'
//import { Modal, Button,Image } from 'react-bootstrap';
import './ComponentsList.scss';

class ComponentsListItem extends React.Component {

  constructor(props) {
    super(props);
  }

  render(){
      return(
            <li  className="list-group-item" onClick={() => {}}>
                <span className='indicator'></span>
                <span className='type'>{this.props.name}</span>
            </li>

      );
  }


}

ComponentsListItem.propTypes = {
    type: React.PropTypes.string,
    name: React.PropTypes.string,
    status: React.PropTypes.string
};


export default ComponentsListItem;