import 'core-js/fn/object/assign';
import React from 'react';
import ReactDOM from 'react-dom';
import Routes from './js/Routes';
import {IntlProvider, addLocaleData} from 'react-intl';
import en from 'react-intl/locale-data/en';
import de from 'react-intl/locale-data/de';
import es from 'react-intl/locale-data/es';
import fr from 'react-intl/locale-data/fr';
import axios from 'axios';
import 'index.scss';
import {INIT_LOCALE} from './js/constants/ServiceConfig';

document.documentElement.style.overflow='hidden';

axios.defaults.headers['X-API-Version'] = 200;

addLocaleData([...en, ...de, ...es, ...fr]);

axios.get(INIT_LOCALE).then(function (response) {

  let acceptLanguage = response.data.locale;

  let language = acceptLanguage || (navigator.languages && navigator.languages[0]) || navigator.language || navigator.userLanguage;
  const languageWithoutRegionCode = language.toLowerCase().split(/[_-]+/)[0];
  let messages;
  try {
    messages = require(`./locales/${language}`);
  } catch (e) {
    try {
      language = languageWithoutRegionCode;
      messages = require(`./locales/${language}`);
    } catch (e) {
      language = 'en';
      messages = require(`./locales/${language}`);
    }
  }
  window.messages = messages;

  // Render the main component into the dom
  ReactDOM.render(
    <IntlProvider locale={language} messages={messages}>
      <Routes/>
    </IntlProvider>,
    document.getElementById('app'));

            })
.catch(function (error) {
   console.log(error);
});


