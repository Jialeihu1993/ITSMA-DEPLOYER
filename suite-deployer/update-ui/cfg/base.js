'use strict';
let path = require('path');
let defaultSettings = require('./defaults');

// Additional npm or bower modules to include in builds
// Add all foreign plugins you may need into this array
// @example:
// let npmBase = path.join(__dirname, '../node_modules');
// let additionalPaths = [ path.join(npmBase, 'react-bootstrap') ];
let additionalPaths = [];

module.exports = {
  additionalPaths: additionalPaths,
  port: defaultSettings.port,
  debug: true,
  devtool: 'eval',
  output: {
    path: path.join(__dirname, '/../dist'),
    filename: 'index.js',
    publicPath: defaultSettings.publicPath
  },
  resolve: {
    extensions: [
        '',
        '.react',
        '.jsx',
        '.js',
        '.json',
        '.htm',
        '.html',
        '.scss',
        '.md',
        '.svg',
        '.gif',
        '.jpg',
        '.jpeg',
        '.png'],
    root: [
        path.join(__dirname, '/../src/images'),
        path.join(__dirname, '/../src/js'),
        path.join(__dirname, '/../src/styles'),
        path.join(__dirname, '/../node_modules')
      ]
    },
  module: {}
};
