/*
Displaying Log Analyzer Jaggery App URL
*/
var carbon = require('carbon');
var configurationContextService = carbon.server.osgiService('org.wso2.carbon.utils.ConfigurationContextService');
var configCtx = configurationContextService.getServerConfigContext();
var log = new Log(); 
log.info("WSO2 Log Analyzer Console URL : "+configCtx.getProperty("la.url"));
