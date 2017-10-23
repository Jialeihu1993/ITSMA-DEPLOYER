// util function code for reducer only

// update property for all page: receive old redux state --> update property value -- > update to new state
export default function updateProperty(state, action){
  // copy old state
  let newState = Object.assign({}, state);
  // retrieve udpated property name and value
  let value = action.value;
  
  if (newState[value.propName])
    newState[value.propName].currentVal = value.value;
  
  return newState;
}