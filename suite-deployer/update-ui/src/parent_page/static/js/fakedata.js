var fakeInstances11 = [
//{
//	"deploymentUuid": "e79039c3-5b5b-4ae2-83d7-7de2e412c736",
//	"suite": "itsma",
//	"namespace": "string",
//	"version": "2016.12",
//	"firstInstallDate": 1481003986312
//}, {
//	"deploymentUuid": "4d64457a-c4c8-434a-8356-b7282d3b722f",
//	"suite": "itsma",
//	"namespace": "string",
//	"version": "2016.12",
//	"firstInstallDate": 1481090862618
//}
];
var fakeLockId = {
	"lock": {
		"lockDetail": [{
			"lockType": "DEVICE",
			"value": "62CE6B6-CD8242"
		}]
	}
};
var fakeDeployment = {
	"uuid": "074f51b1-7d99-4998-8ec9-68e01693fa27",
	"status": "NEW",
	"updatedTime": 1481076566393
};
var fakeUserAgreement={
  "name": "LmsGetEndUserAgreementResponse",
  "declaredType": "com.hp.autopassj.lms.ws.beans.LmsGetEndUserAgreementResponse",
  "scope": "javax.xml.bind.JAXBElement$GlobalScope",
  "value": {
    "agreementName": [
      {
        "value": "true",
        "type": "EULA"
      },
      {
        "value": "true",
        "type": "CALL_HOME"
      }
    ],
    "errorDetail": null
  },
  "nil": false,
  "globalScope": true,
  "typeSubstituted": false
}
var fakeSuites = [{
	"suite": "itsma",
	"display_name": "ITSMA",
	"gid": "1999",
	"uid": "1999",
	"versions": [{
		"version": "2015.11",
		"features": "/itsma_suitefeatures.2016.12.json",
		"image": "shc-nexus-repo.hpeswlab.net:9000/itsma/itsma-installer:latest",
		"license_id": [
			"23134",
			"23132"
		]
	}, {
		"version": "2015.12",
		"features": "/itsma_suitefeatures.2016.12.json",
		"image": "shc-nexus-repo.hpeswlab.net:9000/itsma/itsma-installer:latest",
		"license_id": [
			"23134",
			"23132"
		]
	}]
}, {
	"suite": "opsbridge",
	"display_name": "Operations Bridge",
	"gid": "1003",
	"uid": null,
	"versions": [{
		"version": "2016.10",
		"features": "/opsbridge_suitefeatures.2016.12.json",
		"image": "suotpdock002g.hpeswlab.net:5000/opsbridge201612/opsbridge-config:latest",
		"license_id": [
			"23136"
		]
	},{
		"version": "2017.1",
		"features": "/itsma_suitefeatures.2017.1.json",
		"image": "shc-nexus-repo.hpeswlab.net:9000/itsma/itsma-installer:latest",
		"license_id": [
			"23134",
			"23132"
		]
	}]
}, {
	"suite": "dca",
	"display_name": "DCA",
	"gid": "1004",
	"uid": null,
	"versions": [{
		"version": "2016.11",
		"features": "/path_to_andor_name_of_edition-fset-set.2016.12.json",
		"image": "localhost:5000/dca-config:1.0-beta",
		"license_id": null
	}]
}, {
	"suite": "hcs",
	"display_name": "Hybrid Cloud Management",
	"gid": "1005",
	"uid": null,
	"versions": [{
		"version": "2016.12",
		"features": "/path_to_andor_name_of_edition-fset-set.2016.12.json",
		"image": "localhost:5000/hcs-config:1.0-beta",
		"license_id": null
	}]
}];

var fakeLicense = {
	"feature": [{
		"featureID": "23134",
		"featureVersion": "1",
		"featureDescription": "ITOM OpsBridge Suite Express Edition",
		"activationDateInUTCSeconds": "1480291200",
		"expirationDateInUTCSeconds": "1483228740",
		"totalCapacity": {
			"value": "1"
		},
		"availableCapacity": {
			"value": "1"
		}
	}, {
		"featureID": "23136",
		"featureVersion": "1",
		"featureDescription": "ITOM ITSMA Suite Express Edition",
		"activationDateInUTCSeconds": "1480291200",
		"expirationDateInUTCSeconds": "1483228740",
		"totalCapacity": {
			"value": "1"
		},
		"availableCapacity": {
			"value": "1"
		}
	}, {
		"featureID": "23132",
		"featureVersion": "1",
		"featureDescription": "ITOM OpsBridge Suite Premium Edition",
		"activationDateInUTCSeconds": "1480291200",
		"expirationDateInUTCSeconds": "1483228740",
		"totalCapacity": {
			"value": "1"
		},
		"availableCapacity": {
			"value": "1"
		}
	}]
};



var fakeEtcdStatus = {
	"deploymentId": "074f51b1-7d99-4998-8ec9-68e01693fa27",
	"initStep": "Intro",
	"selectedLicense": "23134",
	"userChoose": "yes",
	"currentLabel": "beta.kubernetes.io/arch:amd64",
	"currentNamespace": "itsma1",
	"currentVersion": "2016.12",
	"currentSuite": {
		"suite": "itsma",
		"display_name": "ITSMA",
		"gid": "1999",
		"uid": "1999",
		"versions": [{
			"version": "2015.11",
			"features": "/itsma_suitefeatures.2016.12.json",
			"image": "shc-nexus-repo.hpeswlab.net:9000/itsma/itsma-installer:latest",
			"license_id": [
				"23134",
				"23132"
			]
		}, {
			"version": "2015.12",
			"features": "/itsma_suitefeatures.2016.12.json",
			"image": "shc-nexus-repo.hpeswlab.net:9000/itsma/itsma-installer:latest",
			"license_id": [
				"23134",
				"23132"
			]
		}]
	},
	"currentCap": {
		"suite": "itsma",
		"description": "ITSMA Suite",
		"initial_edition_selected": "EXPRESS",
		"allow_custom_selection": true,
		"editions": [{
			"id": "EXPRESS",
			"id_defined_in_autopass": "23136",
			"name": "Express",
			"description": "Express version of ITSMA",
			"display": true,
			"selected": true,
			"has_feature_sets": [
				"FS2:selected",
				"FS3:selected",
				"FS1:selected"
			]
		}],
		"feature_sets": [{
			"id": "FS1",
			"name": "SM",
			"description": "Service Manager",
			"display": true,
			"selected": false,
			"has_features": [
				"always:F11:selected",
				"always:F12:selected"
			]
		}, {
			"id": "FS2",
			"name": "SMA",
			"description": "Smart analysis",
			"display": true,
			"selected": false,
			"has_features": [
				"always:F21:selected",
				"always:F22:selected"
			]
		}, {
			"id": "FS3",
			"name": "UCMDB",
			"description": "UCMDB",
			"display": true,
			"selected": false,
			"has_features": [
				"always:F31:selected"
			]
		}],
		"features": [{
			"id": "F11",
			"name": "SM webtier",
			"description": "Service Manager webtier",
			"also_select": [],
			"display": true,
			"selected": false
		}, {
			"id": "F12",
			"name": "SM-RTE",
			"description": "Service Manager RTE",
			"also_select": [],
			"display": true,
			"selected": false
		}, {
			"id": "F21",
			"name": "SMA",
			"description": "Smart analysis",
			"also_select": [],
			"display": false,
			"selected": false
		}, {
			"id": "F22",
			"name": "IDOL",
			"description": "IDOL",
			"also_select": [],
			"display": true,
			"selected": false
		}, {
			"id": "F31",
			"name": "UCMDB",
			"description": "UCMDB",
			"also_select": [],
			"display": true,
			"selected": false
		}],
		"i18n": [
			"000001:nl_BE:Dit is een vertaling",
			"000002:nl_BE:Dit is ook een vertaling"
		],
		"images": [{
			"image": "sm-thing:2017.03.latest"
		}, {
			"image": "fg-222thing:2017.03.008"
		}, {
			"image": "am-web-svc:2016.12.latest"
		}],
		"version": "2017.01"
	}

}
var fakeCsrfToken = {
	"csrfToken": "sauyfdgsdfgsdjkdsgfdg"
}
var fakeLocale = {
	"Express": "Express",
	"Premium": "Premium",
	"Ultimate": "Ultimate",
	"SUITE_DESC": "This is suite desc.",
	"EXPRESS_DESC": "This is express desc.",
	"PREMIUM_DESC": "This is premium desc.",
	"ULTIMATE_DESC": "This is ultimate desc.",
	"FS1_DESC": "This is fs1 desc.",
	"FS2_DESC": "This is fs2 desc.",
	"FS3_DESC": "This is fs3 desc.",
	"F1_DESC": "This is f1 desc.",
	"F2_DESC": "This is f2 desc.",
	"F3_DESC": "This is f3 desc.",
}
var fakei18nConfigJson = {
  "suiteFeature": {
    "suite": "Operations Bridge",
    "description": "<<SUITE_DESC>>",
    "welcome_msg": "<<WELCOME_MSG>>",
    "initial_edition_selected": "PREMIUM",
    "allow_custom_selection": true,
    "acronym": "OpsB", 
    "core_platform_versions_supported": [ "2017.01", "2017.03" ],
    "version": null,
    "images": [
      {
        "image": "suotpdock002g.hpeswlab.net:5000/opsbridge201703/opsbridge-config:latest"
      }
    ],
    "install_size":[{
    	"id":"SMALL",
    	"name":"<<SMALL_NAME>>",
    	"description":"<<SMALL_DESC>>",
    	"selected":false,
    },
    {
    	"id":"MEDIUM",
    	"name":"<<MEDIUM_NAME>>",
    	"description":"<<MEDIUM_DESC>>",
    	"selected":false,
    },
    {
    	"id":"LARGE",
    	"name":"<<LARGE_NAME>>",
    	"description":"<<LARGE_DESC>>",
    	"selected":false,
    }],
    "editions": [
      {
        "id": "EXPRESS",
        "id_defined_in_autopass": "20626",
        "name": "<<EXPRESS_NAME>>",
        "description": "<<EXPRESS_DESC>>",
        "display": true,
        "selected": false,
        "allow_size":["SMALL:selected","MEDIUM:unselected"],
        "has_feature_sets": [
          "FS2:selected"
        ]
      },
      {
        "id": "PREMIUM",
        "id_defined_in_autopass": "20628",
        "name": "<<PREMIUM_NAME>>",
        "description": "<<PREMIUM_DESC>>",
        "display": true,
        "selected": false,
        "allow_size":["SMALL:unselected","LARGE:selected"],
        "has_feature_sets": [
          "FS1:selected"
        ]
      },
      {
        "id": "ULTIMATE",
        "id_defined_in_autopass": "20630",
        "name": "<<ULTIMATE_NAME>>",
        "description": "<<ULTIMATE_DESC>>",
        "display": true,
        "selected": false,
        "allow_size":["SMALL:selected","MEDIUM:unselected","LARGE:unselected"],
        "has_feature_sets": [
          "FS1:optional",
          "FS2:optional",
          "FS3:optional"
        ]
      }
    ],
    "feature_sets": [
      {
        "id": "FS1",
        "name": "<<FS1_NAME>>",
        "description": "<<FS1_DESC>>",
        "display": true,
        "selected": false,
        "has_features": [
          "always:omi:optional",
          "always:bvd:optional",
          "always:pe:optional",
          "always:obr:optional"
        ]
      },
      {
        "id": "FS2",
        "name": "<<FS2_NAME>>",
        "description": "<<FS2_DESC>>",
        "display": true,
        "selected": false,
        "has_features": [
          "always:omi:selected"
        ]
      },
      {
        "id": "FS3",
        "name": "<<FS2_NAME>>",
        "description": "<<FS2_DESC>>",
        "display": true,
        "selected": false,
        "has_features": [
          "always:omi:selected"
        ]
      }
    ],
    "features": [
      {
        "id": "omi",
        "name": "<<OMI_NAME>>",
        "description": "<<OMI_DESC>>",
        "also_select": [],
        "display": true,
        "selected": true
      },
      {
        "id": "bvd",
        "name": "<<BVD_NAME>>",
        "description": "<<BVD_DESC>>",
        "also_select": [],
        "display": true,
        "selected": false
      },
      {
        "id": "pe",
        "name": "<<PE_NAME>>",
        "description": "<<PE_DESC>>",
        "also_select": [],
        "display": true,
        "selected": false
      },
      {
        "id": "obr",
        "name": "<<OBR_NAME>>",
        "description": "<<OBR_DESC>>",
        "also_select": [],
        "display": true,
        "selected": false
      }
    ]
  },
  "i18nMessage": {
  	"WELCOME_MSG":"<p>Welcome to ITSMA Installer</p>",
    "SUITE_DESC": "This is suite desc. en",
    "EXPRESS_DESC": "This is express desc. en",
    "PREMIUM_DESC": "This is premium desc. en",
    "ULTIMATE_DESC": "This is ultimate desc. en",
    "FS1_DESC": "This is fs1 desc. en",
    "FS2_DESC": "This is fs2 desc. en",
    "FS3_DESC": "This is fs3 desc. en",
    "OMI_DESC": "This is f1 desc. en",
    "BVD_DESC": "This is f2 desc. en",
    "PE_DESC": "This is f3 desc. en"
  }
}