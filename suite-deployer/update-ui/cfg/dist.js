'use strict';

let path = require('path');
let webpack = require('webpack');

let baseConfig = require('./base');
let defaultSettings = require('./defaults');

// Add needed plugins here
let BowerWebpackPlugin = require('bower-webpack-plugin');
let CopyWebpackPlugin = require('copy-webpack-plugin');

require('babel-polyfill');

let config = Object.assign({}, baseConfig, {
  //entry: path.join(__dirname, '../src/index'),

  entry: ['babel-polyfill', path.join(__dirname, '../src/index')],
  cache: false,
  devtool: 'sourcemap',
  output: {
    path: path.join(__dirname, '/../dist'),
    filename: 'index.js',
    publicPath: ''
  },
  plugins: [
    new webpack.optimize.DedupePlugin(),
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': '"production"'
    }),
    new BowerWebpackPlugin({
      searchResolveModulesDirectories: false
    }),
    new webpack.optimize.UglifyJsPlugin(),
    new webpack.optimize.OccurenceOrderPlugin(),
    new webpack.optimize.AggressiveMergingPlugin(),
    new webpack.NoErrorsPlugin(),
    new CopyWebpackPlugin([
      { from: './src/index.pug' },
      { from: './src/images', to: 'images' },
      { from: './src/styles/bootstrap', to: 'styles/bootstrap' },
      { from: './src/styles/font-awesome', to: 'styles/font-awesome' },
      { from: './src/styles/ionicons', to: 'styles/ionicons'}
    ])
  ],
  module: defaultSettings.getDefaultModules()
});

// Add needed loaders to the defaults here
config.module.loaders.push({
  test: /\.(js|jsx)$/,
  loader: 'babel',
  include: [].concat(
    config.additionalPaths,
    [ path.join(__dirname, '/../src') ]
  )
});

module.exports = config;
