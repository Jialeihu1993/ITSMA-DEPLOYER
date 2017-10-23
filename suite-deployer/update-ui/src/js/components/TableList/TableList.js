import React from 'react'
import {ListGroup} from 'react-bootstrap';
import TableListItem from './TableListItem'
import {FormattedMessage} from 'react-intl';
import './TableList.scss';

class TableList extends React.Component {

  constructor(props) {
    super(props);
  }
//item:{name:'',timeTag:'',status:''}



  render(){
    let datas = '';
    if(this.props.datas){
    datas = this.props.datas.map((item,index) =>
      <TableListItem  key={index}  name={item.name} timeTag={item.timeTag} status={item.status} errorMsg={item.errorMsg} startBackup={item.startBackup}  percent={item.percent} />);
    }
      return(
         <ListGroup componentClass='ul' className='TableList'>
            <li key='title' className="list-group-item titleItem" >
                <span className='title' style={{display:'inline-block',marginLeft:'30px'}}><FormattedMessage id='Name' /></span>
                <span className='title' style={{display:'inline-block',position:'absolute', left:'50%'}}><FormattedMessage id='Time' /></span>
            </li>
           {datas}
        </ListGroup>

      );
  }


}

TableList.propTypes = {
    //name time status
    datas: React.PropTypes.array
};


export default TableList;