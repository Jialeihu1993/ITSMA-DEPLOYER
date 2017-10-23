import React from 'react'
import {ListGroup} from 'react-bootstrap';
import CommonListItem from './CommonListItem'
import './CommonList.scss';

class CommonList extends React.Component {

  constructor(props) {
    super(props);
  }
//item:{type:'',name:'',status:''}



  render(){
    let datas = '';
    if(this.props.datas){
      datas = this.props.datas.map((item,index) =>
      <CommonListItem  key={index} name={item.name}  status={item.status} errorMsg={item.errorMsg} />);
    }
      return(
         <ListGroup componentClass='ul' className='CommonList'>
           {datas}
        </ListGroup>

      );
  }


}

CommonList.propTypes = {
    datas: React.PropTypes.array
};


export default CommonList;