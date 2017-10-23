var pug = require('pug');
var i18n = require('i18n');
var logger = require('./logger.js');
var config = require('./config.js');
var path = require('path');
var dataMapper = require('./datamapper.js');
var express = require('express');

i18n.configure({
  locales: ['en', 'de', 'es', 'fr'],
  directory: __dirname + '/locales',
  defaultLocale: 'en',
  extension: '.json'
});

// genereate index.html dynamically
var html;
var getHtml = function() {
  if (!html) {
    html = pug.renderFile(path.resolve(path.join(__dirname, '/../dist/index.pug')), {
      baseName: config.browser_urlBase == '/' ? '' : config.browser_urlBase
    });
  }

  return html;
};

module.exports = function(app) {

  app.use('/api/init/locale', function(req, res, next) {
    let acceptlanguage = req.headers['accept-language'];
    let languages = acceptlanguage.split(',');
    let init;
    if (languages && languages.length > 0) {
      init = {
        'code': '201',
        'locale': languages[0].split(';')[0]
      }
    } else {
      init = {
        'code': '201',
        'locale': 'en'
      }
    }
    logger.debug('/api/init/locale: ', init);
    res.json(init);
  });

  app.use('/suitebackup/backup/status',function(req,res){
    dataMapper.getBackupStatus(req,res);
  });

  app.use('/suitebackup/backup',function(req,res){
    dataMapper.startBackup(req,res);
  });

  app.use('/api/upgrade/shutdown/status',function(req,res){
    dataMapper.getSuiteStatus(req,res);
  });

  app.use('/api/upgrade/shutdown',function(req,res){
    dataMapper.startShutdown(req,res);
  });

  app.use('/api/upgrade/update/status',function(req,res){
    dataMapper.getSuiteStatus(req,res);
  });

  app.use('/api/upgrade/update',function(req,res){
    dataMapper.startUpdate(req,res);
  });

  app.use('/parent', express.static(path.join(__dirname, '/../src/parent_page/static')));

  app.get('/*', function(req, res) {
    logger.info('Genereating index.html dynamically and render it.');
    res.send(getHtml());
  });
};
