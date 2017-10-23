'use strict';
let random = require('lodash/random');
let floor = require('lodash/floor');
let clone = require('lodash/clone');
let concat = require('lodash/concat');
let mockData ={lists: [
   {type:'Configmap', per:0,status:true},//status false with error true success
   {type:'Vault', per:0,status:true},    //per 0 - 100
   {type:'Ingress', per:0,status:true},
   {type:'Others', per:0,status:true}
 ],
 complete:false,
 upgradeStatus:false    //true backup success  false backup errors

};

module.exports = function (app) {
  let total = mockData.lists.length;
  let typeCount = 0;
  let errorCount = 1;
  app.use('/api/upgrade/upgrade/:time/:action', function (req, res) {

    let action = req.params.action;
    switch(action){
        case 'getInitial':
            typeCount = 0;
            for(let i = 0;i<total;i++){
                mockData.lists[i].status = true;
                mockData.lists[i].per =0 ;
            }
            mockData.complete = false;
            mockData.upgradeStatus = false;
        break;
        case 'startUpgrade':
            typeCount = 0;
            for(let i = 0;i<total;i++){
                mockData.lists[i].status = true;
                mockData.lists[i].per = 0;
            }
            mockData.upgradeStatus = false;
            mockData.complete = false;
        break;
        case 'upgradeStatus':
          mockData.lists[typeCount].per +=10;
          if(mockData.lists[typeCount].per >= 100){
                mockData.lists[typeCount].status = true;
                typeCount++;
          }
          if(typeCount >= 4){
            typeCount = 0;
            mockData.upgradeStatus = true;
            mockData.complete = true;
        }
        break;
        case 'upgradeStatusError':
            mockData.lists[typeCount].per +=10;
            if(typeCount == errorCount && mockData.lists[typeCount].per >= 50){
                mockData.lists[typeCount].status = false;
                typeCount ++;
            }else{
                if(mockData.lists[typeCount].per >= 100){
                    mockData.lists[typeCount].status = true;
                    typeCount++;
                }
            }
            if(typeCount >= 4){
                typeCount = 0;
                mockData.upgradeStatus = false;
                mockData.complete = true;
            }
        break;
    }
    res.json(mockData);
  });
};
