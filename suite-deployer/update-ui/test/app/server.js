var http = require("http");

var express = require('express');
// listen (start app with node server.js) ======================================
let port = 8081;
let server = 'localhost';
var app = express();
var http_server = http.createServer(app);
http_server.listen(port, server, () => {
    // logger.info('[server]', 'App listening ' + server + ' on port ' + port);
    console.log('[server]', 'Mock end server started on ' + server + ':' + port);
});
http_server.on('error', (e) => {
    if (e.code == 'EADDRINUSE') {
        // logger.error(e.code + ': Port ' + port + ' already in use, please change it');
        console.log(e.code + ': Port ' + port + ' already in use, please change it');
    }
});

//Listening node request testDatabase
app.post('/itsma/verification/database', function(req, res) {
    let result = {
        "sm": {
            "code": "400",
            "message": "java.sql.SQLException: ORA-01017: invalid username/password; logon denied\n"
        }
    }
    res.send(result);
});

//Listening node request testLDAP
app.post('/itsma/verification/ldap', function(req, res) {
    // let numberMillis = 2005;
    // var now = new Date(); 
    // var exitTime = now.getTime() + numberMillis; 
    //     while (true) { 
    //         now = new Date(); 
    //         if (now.getTime() > exitTime) 
    //         break; 
    // } 

    let result = [{
        "code": "400",
        "errorCode": "UnknownHost",
        "message": "javax.naming.CommunicationException: 1.1.1.1:11 [Root exception is java.net.ConnectException: Connection timed out (Connection timed out)]"
    }]
    console.log("testLDAP, result: ", result);
    res.send(result);
});

//Listening node request install
app.post('/itsma/install', function(req, res) {
    let result = "";
    res.send(result);
});

//Listening node request getInstallStatus
let count = 0;
app.get('/itsma/install', function(req, res) {
    let result;
    if (count == 0) {
        result = {
            "servicestatus": []
        }
    } else if (count == 1) {
        result = {
            "servicestatus": []
        }
    } else if (count == 2) {
        result = {
            "servicestatus": [{
                "name": "itom-auth",
                "status": "SUCCESS"
            }, {
                "name": "itom-idm",
                "status": "SUCCESS"
            }, {
                "name": "itom-openldap",
                "status": "SUCCESS"
            }, {
                "name": "itom-ingress",
                "status": "SUCCESS"
            }, {
                "name": "itom-sm",
                "status": "SUCCESS"
            }, {
                "name": "itom-smartanalytics",
                "status": "READY"
            }, {
                "name": "itom-cmdb",
                "status": "READY"
            }, {
                "name": "itom-service-portal",
                "status": "READY"
            }, {
                "name": "itom-service-portal-ui",
                "status": "READY"
            }, {
                "name": "itom-xservices-infra",
                "status": "READY"
            }, {
                "name": "itom-xservices",
                "status": "READY"
            }, {
                "name": "itom-chat",
                "status": "READY"
            }, {
                "name": "itom-itsma-config",
                "status": "READY"
            }, {
                "name": "itom-autopass",
                "status": "READY"
            }]
        }
    } else if (count == 2) {
        result = {
            "servicestatus": [{
                "name": "itom-auth",
                "status": "SUCCESS"
            }, {
                "name": "itom-idm",
                "status": "SUCCESS"
            }, {
                "name": "itom-openldap",
                "status": "SUCCESS"
            }, {
                "name": "itom-ingress",
                "status": "SUCCESS"
            }, {
                "name": "itom-sm",
                "status": "SUCCESS"
            }, {
                "name": "itom-smartanalytics",
                "status": "READY"
            }, {
                "name": "itom-cmdb",
                "status": "READY"
            }, {
                "name": "itom-service-portal",
                "status": "READY"
            }, {
                "name": "itom-service-portal-ui",
                "status": "READY"
            }, {
                "name": "itom-xservices-infra",
                "status": "READY"
            }, {
                "name": "itom-xservices",
                "status": "READY"
            }, {
                "name": "itom-chat",
                "status": "READY"
            }, {
                "name": "itom-itsma-config",
                "status": "READY"
            }, {
                "name": "itom-autopass",
                "status": "FAILED"
            }]
        }
    } else {
        result = {
            "servicestatus": [{
                "name": "itom-auth",
                "status": "SUCCESS"
            }, {
                "name": "itom-idm",
                "status": "SUCCESS"
            }, {
                "name": "itom-openldap",
                "status": "SUCCESS"
            }, {
                "name": "itom-ingress",
                "status": "SUCCESS"
            }, {
                "name": "itom-sm",
                "status": "SUCCESS"
            }, {
                "name": "itom-smartanalytics",
                "status": "SUCCESS"
            }, {
                "name": "itom-cmdb",
                "status": "SUCCESS"
            }, {
                "name": "itom-service-portal",
                "status": "SUCCESS"
            }, {
                "name": "itom-service-portal-ui",
                "status": "SUCCESS"
            }, {
                "name": "itom-xservices-infra",
                "status": "SUCCESS"
            }, {
                "name": "itom-xservices",
                "status": "SUCCESS"
            }, {
                "name": "itom-chat",
                "status": "SUCCESS"
            }, {
                "name": "itom-itsma-config",
                "status": "SUCCESS"
            }, {
                "name": "itom-autopass",
                "status": "SUCCESS"
            }]
        }
    }
    count++;
    console.log("getInstallStatus, result: ", result);
    res.send(result);
});
