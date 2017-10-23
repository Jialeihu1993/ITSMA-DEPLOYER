# suite-update-ui

Itsma suite update frontend

## Use ITSMA private registry
The suite-update-ui repository is using the private NPM in ITSMA development runtime server.
Please config your local NPM client to work correctly with the private registry.

```bash
npm adduser
Username: dev
Password: dev
Email: (this IS public) dev@dev.com
Logged in as dev on http://shc-nexus-repo.hpeswlab.net:8080/repository/npm-group/.
```
## Usage
The following commands are available in your project:
```bash
# Start for development with backend
npm start 

# or start for development with frontend purely. This command will setup mockdate to simulate backend responses
npm run devmock

# Start the dev-server with the dist version
npm run serve:dist

# Just build the dist version and copy static files
npm run dist

# Run unit tests
npm test

# Auto-run unit tests on file changes
npm run test:watch

# Lint all files in src (also automatically done AFTER tests are run)
npm run lint

# Clean up the dist directory
npm run clean

# Just copy the static assets
npm run copy
```

The following command is to startup nodejs server
```bash
# Startup  
# This is necessary for developing with backend
node app/server.js
http://localhost:8080/upgrade/  #8002 for development 8080 for production mode
# Go to parent page ( You can change the iframeUrl in suite-installer.html eg. http://localhost:8002/upgrade  But you may need to disable cross-domain attacks for suite-installer.html to get the resources)
http://localhost:8080/upgrade/parent/suite-installer.html
```

## Startup with hot deployment
```bash
# Install supervisor
npm install -g supervisor
supervisor app/server.js 
http://localhost:8080/upgrade/ 
# Go to parent page
http://localhost:8080/upgrade/parent/suite-installer.html
```

## Deployment in production env
```bash
npm run dist
node app/server.js
```

## Source structure

**Highlight** is our working code frequently in daily work.

- deployer-ui
    - **app**: node server code contains basic routers, logger etc.
        - ...
    - **cfg**: webpack build script
        - ...
    - dist: production environment distribution
        - ...
    - logs: server logs
        - ...
    - node_modules: 3rdparty modules
        - ...
    - scripts: build scripts for docker
        - ...
    - **src**: client code
        - **js**
            - actions: redux actions
                - ...
            - components: shared components
                - ...
            - constants: constants for config etc.
                - ...
            - pages: pages for installation.
                - ...
            - reducers: redux reducers
                - ...
            - stores: redux stores
                - ...
			- util: util method
                - ...
            - Routes.js: react routers
        - locales: l18n resources
            - ...
        - styles: css & font files & images files etc.
            - ...
            - ...
        - favicon.ico: brand
        - index.html: main html
        - index.js: main javascript
    - test: unit tests for javascript
        - ...
    - assembly.xml: assembly for docker
    - karma.conf.js: unit tests configurations
    - **package.json**: project configuration file
    - pom.xml: pom for docker
    - README.md: readme
    - server.js: webpack server file
    - **webpack.config.js**: webpack configuration
    - other configuration files like .babelrc, .npmrc etc.


## Code Style

**An approach to React and JSX.**

Basic rules

- Only include one React Component for each file.
- Always use JSX syntax.
- Do not use React.createElement unless you're initializing the app from a file that is not JSX.

Class vs React.createClass vs stateless

- If you have internal state and/or refs, prefer class extends React.Component over React.createClass
- If you don't have state or refs, prefer normal functions (not arrow functions) over classes

Naming

- Extensions: Use .jsx extension for React components.
- Use PascalCase for filenames. E.g., ReservationCard.jsx.
- Use PascalCase for React components and camelCase for their instances
- Component Naming: Use the filename as the component name

Alignment

- Follow these alignment styles for JSX syntax([react/jsx-closing-bracket-location](https://github.com/yannickcr/eslint-plugin-react/blob/master/docs/rules/jsx-closing-bracket-location.md))

Quotes

- Always use double quotes (") for JSX attributes, but single quotes (') for all other JS

Spacing

- Always include a single space in your self-closing tag
- Do not pad JSX curly braces with spaces

Props

- Always use camelCase for prop names
- Omit the value of the prop when it is explicitly true
- Do not use accessKey on elements.
- Avoid using an array index as key prop, prefer a unique ID

Refs

- Always use ref callbacks

Parentheses

- Wrap JSX tags in parentheses when they span more than one line

Tags

- Always self-close tags that have no children
- If your component has multi-line properties, close its tag on a new line

Methods

- Use arrow functions to close over local variables.
- Bind event handlers for the render method in the constructor
- Do not use underscore prefix for internal methods of a React component.
- Be sure to return a value in your render methods

Ordering

- Ordering for class extends React.Component
	- constructor
	- getChildContext
	- componentWillMount
	- componentDidMount
	- componentWillReceiveProps
	- shouldComponentUpdate
	- componentWillUpdate
	- componentDidUpdate
	- componentWillUnmount
	- clickHandlers or eventHandlers like onClickSubmit()

References to [JavaScript Style Guide](https://github.com/airbnb/javascript) for more details.
