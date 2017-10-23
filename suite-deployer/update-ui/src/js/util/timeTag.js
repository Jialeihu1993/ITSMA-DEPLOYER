export function customDay(day){
  if(parseInt(day)%10 == 1){
    return parseInt(day)+'st';
  }else if(parseInt(day)%10 == 2){
    return parseInt(day)+'nd';
  }else if(parseInt(day)%10 == 3){
    return parseInt(day)+'rd';
  }else{
    return parseInt(day)+'th';
  }
}

//timeStr: Web Aug 30 14:49:52 CST 2017
export function timeTag(timeStr) {
  let result = timeStr.split(' ');
  let year = result[5];
  let month = result[1];
  let day = customDay(result[2]);

  return day + ' ' + month + ' ' + year;
}

export function getCurrentDate(){
  //Thu Aug 31 2017 15:30:53 GMT+0800
  let currentDate = Date();
  let result = currentDate.split(' ');
  let year = result[3];
  let month = result[1].toUpperCase();
  let day = customDay(result[2]);

  return day + ' ' + month + ', ' + year;
}