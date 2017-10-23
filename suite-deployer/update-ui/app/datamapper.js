const config = require('./config.js');
var logger = require('./logger.js');
const axios = require('axios');
const apiEndPoint = `${config.rest_protocol}://${config.rest_server}:${config.rest_port}`;
axios.defaults.baseURL = apiEndPoint;
//just for test
//const apiEndPoint = 'http://TWBQHJ3ITX.asiapacific.hpqcorp.net:8080';
//axios.defaults.baseURL = apiEndPoint;

axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.timeout = config.proxy_timeout;

/*const axios_update = axios.create({
  baseURL:'http://TWBQHJ3ITX.asiapacific.hpqcorp.net:8080'
});
*/

function startBackup(req, res){
  post('/itsma/backup').then(function () {
      res.status(202).end();
  }).catch((err) => {
      logger.error('start backup failed' + err);
      responseError(res,err,'start backup failed');
  });
}

function getBackupStatus(req, res){
  get('/itsma/backup').then(function (getStatusResult) {
      res.json(getStatusResult);
  }).catch((err) => {
      logger.error('get backup status failed' + err);
      responseError(res,err,'get backup status failed');
  });
}

function startShutdown(req,res){
  deleteWrapper('/itsma/itsma_services').then(function () {
      res.status(202).end();
  }).catch((err) => {
      logger.error('start shutdown failed' + err.status);
      responseError(res,err,'start shutdown failed');
  });
}

function startUpdate(req,res){
  post('/itsma/update').then(function () {
    res.status(201).end();
  }).catch((err) => {
      logger.error('start update failed' + err);
      responseError(res,err,'start update failed');
  });
}

function getSuiteStatus(req, res){
  get('/itsma/deployer/status').then(function (response) {
      res.json(response);
  }).catch((err) => {
      logger.error('get suite status failed' + err);
      responseError(res,err,'get suite status failed');
  });
}

function post(path, body) {
  return axios.post(path, body).then((response) => {
    return response.data;
  }).catch((error) => {
    if (error.response && error.response.data) {
      error = error.response.data;
    }
    throw error;
  });
}

function get(path) {
  return axios.get(path).then((response) => {
    return response.data;
  }).catch((error) => {
    throw error;
  });
}

function deleteWrapper(path) {
  return axios.delete(path).then((response) => {
    return response.data;
  }).catch((error) => {
    throw error;
  });
}

function responseError(frontResponse, apiResponse, message = 'common error') {
  let result = {
    message
  };

  if (apiResponse.status) {
    switch (apiResponse.status) {
      case 404:
        result.cause = `api server ${apiEndPoint} is not available`;
        break;
      default:
        result.cause = `api server ${apiEndPoint} handle request error`;
    }
    frontResponse.status(apiResponse.status).json(result);
  } else if (apiResponse.code){
    let status = 500;
    switch(apiResponse.code){
      case 'ECONNABORTED':
        result.cause = 'Connection Aborted.';
        status = 408;
        break;
      case 'ECONNREFUSED':
        result.cause = 'Connection Refused.';
        status = 404;
        break;
      default:
        result.cause = 'Connection Error.';
        status = 500;
    }
    frontResponse.status(status);
    frontResponse.json(result);
  } else {
    frontResponse.status(500);
    frontResponse.json(result);
  }
}

module.exports = {
  getBackupStatus,
  startBackup,
  startUpdate,
  startShutdown,
  getSuiteStatus

};
