'use strict';
let baseConfig = require('./devmock');
let express = require('express');

let devServer = Object.assign({}, baseConfig.devServer, {
    proxy:{
      '!**/index.js': 'http://localhost:8080',
      '/api/**': 'http://localhost:8080'
    },
    setup(app){
      app.use('/upgrade', express.static('./src/'))
    }
});

let config = Object.assign({}, baseConfig, {
  devServer: devServer
});

module.exports = config;
