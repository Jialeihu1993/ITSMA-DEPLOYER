import React from 'react';
import {Col} from 'react-bootstrap';


class Main extends React.Component {
  constructor(props) {
    super(props);
    this.state={
      listHeight:400   //default for use in dev mode
    };
  }

  componentDidMount() {
    
    window.onresize= ()=>{
         this.resize();
    }

    let frame = window.parent.document.getElementById('mainFrame');
    if(frame){
      let frameHeight = frame.clientHeight;
      let listHeight = (frameHeight-120-120)/2-15;
      this.setState({listHeight:listHeight});
    }
}

  resize(){
    let frame = window.parent.document.getElementById('mainFrame');
    if(frame){
      let frameHeight = frame.clientHeight;
      let listHeight = (frameHeight-120-120)/2-15;
      this.setState({listHeight:listHeight});
    }
  }

 



  componentDidUpdate()
  {

  }

  /**
   * Specify navMode to make cloumn size for navigator and main content
   * @param navMode
   * @returns {XML}
   * @private
   */
  _mainStatus()
  {

    let children = React.Children.map(this.props.children, (child) => {
      return React.cloneElement(child, {
        'listHeight': this.state.listHeight
      });
    });

    return (
        <div id='container' className='container-fluid colFull'>
              <Col xs={12} className='colFull' >
                  {children}
              </Col>
        </div>
    );
  }

  render()
  {
    return (
        this._mainStatus()
    );
  }
}





export default Main;