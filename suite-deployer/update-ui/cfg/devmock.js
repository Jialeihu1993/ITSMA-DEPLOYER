'use strict';
let express = require('express');
let path = require('path');
let webpack = require('webpack');
let baseConfig = require('./base');
let defaultSettings = require('./defaults');
let appConfig = require('../app/config')
// Add needed plugins here
let BowerWebpackPlugin = require('bower-webpack-plugin');
let config = Object.assign({}, baseConfig, {
  
  entry: [
    'webpack-dev-server/client?http://localhost:' + defaultSettings.port,
    'webpack/hot/dev-server',
    './src/index'
  ],
  cache: true,
  devtool: 'eval-source-map',
  devServer: {
    contentBase: './src/',
    historyApiFallback: true,
    inline: true,
    hot: true,
    port: defaultSettings.port,
    noInfo: false
  },
  plugins: [
    new webpack.HotModuleReplacementPlugin(),
    new webpack.NoErrorsPlugin(),
    new BowerWebpackPlugin({
      searchResolveModulesDirectories: false
    })
  ],
  module: defaultSettings.getDevModules()
});

// Add needed loaders to the defaults here
config.module.loaders.push({
  test: /\.(js|jsx)$/,
  loader: 'react-hot!babel-loader',
  include: [].concat(
    config.additionalPaths,
    [ path.join(__dirname, '/../src') ]
  )
   
  
});
module.exports = config;
