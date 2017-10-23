import React from 'react'
import {ListGroup} from 'react-bootstrap';
import ComponentsListItem from './ComponentsListItem'
import './ComponentsList.scss';

class ComponentsList extends React.Component {

  constructor(props) {
    super(props);
  }
//item:{type:'',name:'',status:''}



  render(){
    let datas = this.props.datas.map((item,index) =>
      <ComponentsListItem  key={index} name={item.name}  type={item.type} status={item.status} />);
      return(
         <ListGroup componentClass='ul' className='componentsList'>
           <li key='title' className="list-group-item" >
                <span className='titleIcon ion ion-social-codepen'></span>
                <span className='title'>Services</span>
            </li>
           {datas}
        </ListGroup>

      );
  }


}

ComponentsList.propTypes = {
    datas: React.PropTypes.array
};


export default ComponentsList;