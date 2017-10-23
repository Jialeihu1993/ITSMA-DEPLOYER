// init variables from properties
var fs = require('fs');
var PropertiesReader = require('properties-reader');
var properties = PropertiesReader('itsma-update-ui.properties.default');
if (fs.existsSync('itsma-update-ui.properties')) {
  properties.append('itsma-update-ui.properties');
}

module.exports = {
  // node server
  node_server: properties.get('node.server'),
  node_port: properties.get('node.port'),
  node_https_port: properties.get('node.https_port'),
  node_base: properties.get('node.base') || '/',
  browser_urlBase: properties.get('browser.urlBase'),
  // log
  logging_level: properties.get('logging.level'),
  // rest server
  rest_protocol: properties.get('rest.protocol'),
  rest_server: properties.get('rest.server'),
  rest_port: properties.get('rest.port'),
  // # 2 minutes
  proxy_timeout: properties.get('rest.proxy_timeout'),
  // session
  session_secret: properties.get('node.session_secret')
};
