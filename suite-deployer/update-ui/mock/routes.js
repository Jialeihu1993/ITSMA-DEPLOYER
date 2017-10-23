var i18n = require('i18n');
var pug = require('pug');
var path = require('path');
var express = require('express');

i18n.configure({
  locales: ['en', 'de', 'es', 'fr'],
  directory: __dirname + './../app/locales',
  defaultLocale: 'en',
  extension: '.json'
});

module.exports = function (app) {
  let html;
  let getHtml = function () {
    html = pug.renderFile(
      path.resolve(path.join(__dirname, '/../dist/index.pug')), {
        baseName: ''
      });
    return html;
  };

  function home(req, res) {
    i18n.init(req, res);
    res.send(getHtml());
  }

  app.use('/parent', express.static(path.join(__dirname, '/../src/parent_page/static')));
  

  app.use('/', function (req, res, next) {
    if (req.url === '/index.html') {
      home(req, res);
    }
    else {
      next();
    }
  });

  app.use('/api/init/locale', function (req, res) {

    let acceptlanguage = req.headers['accept-language'];
    let languages = acceptlanguage.split(',');
    if (languages && languages.length > 0) {
      let init = {
        'code': '201',
        'locale': languages[0].split(';')[0]
      }
      res.json(init);
    } else {
      let init = {
        'code': '201',
        'locale': 'en'
      }
      res.json(init);
    }
  });

  // add other mock routes
  require('./shutdown')(app);
  require('./backup')(app);
  require('./update')(app);
  require('./service')(app);
  require('./rollback')(app);
};

