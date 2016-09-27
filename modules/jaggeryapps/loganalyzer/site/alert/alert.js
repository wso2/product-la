/**
 * Created by nalaka on 2/19/16.
 */
var serverUrl = window.location.origin;


jQuery(document).ready(function() {

    jQuery('.alert-tabs .tab-links a').on('click', function(e)  {
        var currentAttrValue = jQuery(this).attr('href');

        // Show/Hide Tabs
        jQuery('.alert-tabs ' + currentAttrValue).show().siblings().hide();

        // Change/remove current tab to active
        jQuery(this).parent('li').addClass('active').siblings().removeClass('active');

        e.preventDefault();
    });

    $("#daily-timepicker").timepicker({  timeFormat: 'H:i'
        //,interval:30  // 15
    });

    $("#weekly-timepicker").timepicker({  timeFormat: 'H:i'
        //,interval:30  // 15
    });

    $("#monthly-timepicker").timepicker({  timeFormat: 'H:i'
        //,interval:30  // 15
    });

    $(".monthly-datepicker").datepicker({
        dateFormat: 'dd',
        changeYear:true,
        changeMonth:true,
        onSelect: function(dateText, inst) {
           // alert(dateText); // alerts the day name
        }
    });


    if (window.location.search.indexOf('query') > -1) {
        $(".inner-container").show();
        urlParams = splitUrl();
        setParams(urlParams);
        $(".alert-list").hide();

    }

    getAllAlerts();

    loadAction();
    loadContent();
    loadCompare();

    $("#field-data").select2();
    getColumns();


    function cb(start, end) {
        $('#reportrange span').html(start.format('MM/DD/YYYY h:mm A') + ' - ' + end.format('MM/DD/YYYY h:mm A'));
    }
    cb(moment().subtract(29, 'days'), moment());

    $('#reportrange').daterangepicker({
        timePicker: true,
        timePickerIncrement: 30,
        locale: {
            format: 'MM/DD/YYYY h:mm A'
        },
        ranges: {
            'All Time':[moment(0).unix(),moment(8640000000000000).unix()],
            'Today': [moment().startOf('days'), moment().endOf('days')],
            'Yesterday': [moment().subtract(1, 'days').startOf('days'), moment().subtract(1, 'days').endOf('days')],
            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
            'Last 30 Days': [moment().subtract(29, 'days'), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
            'Last 15 Minutes':[moment().subtract(15, 'minutes'), moment()],
            'Last Hour':[moment().subtract(60, 'minutes'),moment()],
            'Last 24 Hours':[moment().subtract(24, 'hours'),moment()]
        }
    }, cb);
});

function getAllAlerts(){
    jQuery.ajax({
        type:"GET",
        url:serverUrl+"/api/alert/getAllScheduleAlerts",
        success: function(res){
            var html = "";
            $.each(res, function (key, alert) {
                html += createTable(alert);
            });
            $("#alert-list-table").append(html);
        },
        error:function(res){
            alert(res);
        }
    });
}

function createTable(alert){
    return '<tr><td>'+ alert.alertName +'</td><td>' + alert.description + '</td><td>' + alert.cronExpression + '</td><td><a  onclick=deleteAlert(\''+alert.alertName+'\')>Delete</a></td><td><a  onclick=updateContent(\''+alert.alertName+'\')>Update</a></td></tr>';
}

function deleteAlert(alertName){
    jQuery.ajax({
        type:"DELETE",
        url:serverUrl+"/api/alert/delete/"+alertName,
        success:function(res){
            window.location=serverUrl+'/loganalyzer/site/alert/alert.jag';
        },
        error:function(res){
            alert(res);
        }
    });
}

function updateContent(alertName){
    $(".inner-container").show();
    $(".alert-list").hide();
    $("#alert-save-btn").hide();
    $("#alert-update-btn").show();
    jQuery.ajax({
        type:"GET",
        url:serverUrl+"/api/alert/getAlertContent/"+alertName,
        success:function(res){
            $("#alert-name-txt").val(res.alertName);
            $("#alert-name-txt").attr('disabled', 'disabled');
            $("#alert-des-txa").val(res.description);
            $("#filter-txt").val(res.query);
            $("#timestamp-from").val(res.timeFrom);
            $("#timestamp-to").val(res.timeTo);
            $("#cron-exp").val(res.cronExpression);
            $("#cond-type").val(res.condition);
            $("#cmp-val").val(res.conditionValue);
             $("#alert-action").val(res.alertActionType);
            if(res.alertActionType=='logger'){
                loadAction();
                $("#action-logger-uniqueId").val(res.alertActionProperties.uniqueId);
                var getContent=res.alertActionProperties.message;
                var setMessage=getContent.replace(/{{.*/,"");
                $("#logger-message").val(setMessage);
            }
            else if(res.alertActionType=='email'){
                loadAction();
                $("#action-email-address").val(res.alertActionProperties.email_address);
                $("#action-email-subject").val(res.alertActionProperties.email_subject);
               // $("#action-email-type").val(res.alertActionProperties.email_type);
                var getContent=res.alertActionProperties.message;
                var setMessage=getContent.replace(/<.*/,"");
                $("#email-message").val(setMessage);
            }
            else if(res.alertActionType=='sms') {
                loadAction();
                $("#action-sms-phoneNo").val(res.alertActionProperties.sms_no);
                var getContent=res.alertActionProperties.message;
                var setMessage=getContent.replace(/{{.*/,"");
                $("#sms-message").val(setMessage);
            }
        },
        error:function(res){
            alert(res);
        }
    });
}

function saveAlert(){

    var alertName=$("#alert-name-txt").val();
    var cmpValue=$("#cmp-val").val();
    var query=$("#filter-txt").val();
    if(!isValidName(alertName)){
        alert("Invalid Alert Name");
        return;
    }
    if (!isValidNumber(cmpValue)||cmpValue=="") {
        alert("Invalid Trigger Value");
        return;
    }
    if (query=="") {
        alert("Search can't be empty");
        return;
    }
    var payload={};
    var fields={};
    var count=0;
    var action={};
   var name=jQuery.trim($("#alert-name-txt").val());
    payload.alertName=alertName;
    payload.description=$("#alert-des-txa").val();
    payload.query=query;
    payload.timeFrom = $("#timestamp-from").val();
    payload.timeTo=$("#timestamp-to").val();
    payload.cronExpression=$("#cron-exp").val();
    payload.condition=$("#cond-type").val();
    payload.conditionValue=cmpValue;
    payload.alertActionType=$("#alert-action").val();
    var fieldData=$("#field-data").val();
    for(var field in fieldData){
        var fieldName="field"+count;
        fields[fieldName]=fieldData[field];
        count+=1;
    }
    payload.fields=fields;
    if (payload.alertActionType=="logger"){
        var uniqueId=$("#action-logger-uniqueId").val();
        var loggerMessage=$("#logger-message").val();
        if (uniqueId == "") {
            alert("Unique Id can't be empty");
            return;
        }
        if (loggerMessage == "") {
            alert("Message can't be empty");
            return;
        }

        if ($("#countSlt").is(":checked")) {
            loggerMessage+=" \nResults Link: {{url}}";
        }
        action.uniqueId=uniqueId;
        action.message=loggerMessage;
    }
    if (payload.alertActionType=="email"){
        var emailAddressesTxt=$("#action-email-address").val();
        var emailAddresses = emailAddressesTxt.split(",");
        var subject=$("#action-email-subject").val();
        var emailMessage=$("#email-message").val();
        for (var i in emailAddresses){
            var emailAddress= emailAddresses[i].trim();
            if (!isValidEmail(emailAddress)) {
                alert("Invalid Email Address "+emailAddress);
                return;
            }
        }
        if (subject == "") {
            alert("Subject can't be empty");
            return;
        }
        if (subject == "") {
            alert("Message can't be empty");
            return;
        }
        if ($("#countSlt").is(":checked")) {
            emailMessage+=" <div> <a href='{{url}}'> View Results</a></div>";
        }
        if ($("#field-data").val()) {
            emailMessage+="<div> Required fields of Results <br> {{values}} </div>";
        }

        action.email_address=emailAddressesTxt;
        action.email_subject=subject;
        action.email_type="text/html";
        action.message=emailMessage;
    }
    if (payload.alertActionType=="sms"){
        var phoneNo = $("#action-sms-phoneNo").val();
        var smsMessage =$("#sms-message").val();
        if (!isValidPhoneNo(phoneNo)) {
            alert("Invalid Phone Number");
            return;
        }
        if (smsMessage == "") {
            alert("Message can't be empty");
            return;
        }
        if ($("#countSlt").is(":checked")) {
            smsMessage+=" Results Link: {{count}}";
        }
        action.sms_no=phoneNo;
        action.message=smsMessage;
    }
    payload.alertActionProperties=action;
    var data=JSON.stringify(payload);
    jQuery.ajax({
        type: "POST",
        data : data,
        contentType : "application/json; charset=utf-8",
        url: serverUrl + "/api/alert/save",
        success: function(res) {
            window.location=serverUrl+'/loganalyzer/site/alert/alert.jag';
        },
        error: function(res) {
            var restest=JSON.parse(res.responseText);
            alert(restest.message);
            window.location=serverUrl+'/loganalyzer/site/alert/alert.jag';
        }
    });

}

function updateAlert(){
    var query=$("#filter-txt").val();
    var cmpValue=$("#cmp-val").val();
    var payload={};
    var fields={};
    var action={};
    var count=0;
    var valuesSlt=false;
    if (query=="") {
        alert("Search can't be empty");
        return;
    }
    if (!isValidNumber(cmpValue)||cmpValue=="") {
        alert("Invalid Trigger Value");
        return;
    }
    payload.alertName=$("#alert-name-txt").val();
    payload.description=$("#alert-des-txa").val();
    payload.query=query;
    payload.timeFrom = $("#timestamp-from").val();
    payload.timeTo=$("#timestamp-to").val();
    payload.cronExpression=$("#cron-exp").val();
    payload.condition=$("#cond-type").val();
    payload.conditionValue=cmpValue;
    payload.alertActionType=$("#alert-action").val();
    var fieldData=$("#field-data").val();
    for(var field in fieldData){
        var fieldName="field"+count;
        fields[fieldName]=fieldData[field];
        count+=1;
    }
    payload.fields=fields;
    if (payload.alertActionType=="logger"){
        var uniqueId=$("#action-logger-uniqueId").val();
        var loggerMessage=$("#logger-message").val();
        if (uniqueId == "") {
            alert("Unique Id can't be empty");
            return;
        }
        if (loggerMessage == "") {
            alert("Message can't be empty");
            return;
        }
        if ($("#countSlt").is(":checked")) {
            loggerMessage+=" \nResults Link: {{url}}";
        }
        action.uniqueId=uniqueId;
        action.message=loggerMessage;
    }
    if (payload.alertActionType=="email"){
        var emailAddressesTxt=$("#action-email-address").val();
        var emailAddresses = emailAddressesTxt.split(",");
        var subject=$("#action-email-subject").val();
        var emailMessage=$("#email-message").val();
        for (var i in emailAddresses){
            if (!isValidEmail(emailAddresses[i])) {
                alert("Invalid Email Address "+emailAddresses[i]);
                return;
            }
        }
        if (subject == "") {
            alert("Subject can't be empty");
            return;
        }
        if (subject == "") {
            alert("Message can't be empty");
            return;
        }
        if ($("#countSlt").is(":checked")) {
            emailMessage+=" <div><a href='{{url}}'> View Results</a>  </div>";
        }
        if ($("#field-data").val()) {
            emailMessage+="<div> Results <br> {{values}} </div>";
        }

        action.email_address=emailAddressesTxt;
        action.email_subject=subject;
        action.email_type="text/html";
        action.message=emailMessage;
    }
    if (payload.alertActionType=="sms"){
        var phoneNo = $("#action-sms-phoneNo").val();
        var smsMessage =$("#sms-message").val();
        if (!isValidPhoneNo(phoneNo)) {
            alert("Invalid Phone Number");
            return;
        }
        if (smsMessage == "") {
            alert("Message can't be empty");
            return;
        }
        if ($("#countSlt").is(":checked")) {
            smsMessage+=" Results Link: {{url}}";
        }
        action.sms_no=phoneNo;
        action.message=smsMessage;
    }
    payload.alertActionProperties=action;
    var data=JSON.stringify(payload);
    jQuery.ajax({
        type: "PUT",
        data : data,
        contentType : "application/json; charset=utf-8",
        url: serverUrl + "/api/alert/update",
        success: function(res) {
            window.location=serverUrl+'/loganalyzer/site/alert/alert.jag';
        },
        error: function(res) {
            alert(res.responseText);
        }
    });
}

    $("#add-alert-btn").click(function () {
        $(".inner-container").show();

        $(".alert-list").hide();

    });
/*---------------------------------------------------------------
*/
function isValidName(string){
    var alertNamePattern = /^([a-z]|[A-Z]|_|\.|-)([a-z]|[A-Z]|[0-9]|_|\.|-)*$/i;
    return (alertNamePattern.test(string));
}

function isValidNumber(number){
    var integerPattern= /^([0]|[1-9])*$/g;
    return (integerPattern.test(number));
}

function isValidPhoneNo(number){
    var PhoneNoPattern = /^(?:\+?(\d{1,3}))?([-. (]*(\d{3})[-. )]*)?((\d{3})[-. ]*(\d{2,4})(?:[-.x ]*(\d+))?)*$/gm;
    return (PhoneNoPattern.test(number));
}

function isValidEmail(email) {
    var emailPattern = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return emailPattern.test(email);
}

function splitUrl() {
    var urlParams;
    var match,
        pl = /\+/g,  // Regex for replacing addition symbol with a space
        search = /([^&=]+)=?([^&]*)/g,
        decode = function (s) {
            return decodeURIComponent(s.replace(pl, " "));
        },
        query = window.location.search.substring(1);
    urlParams = {};
    while (match = search.exec(query))
        urlParams[decode(match[1])] = decode(match[2]);
    return urlParams;
}


function setParams(values){
    for(var key in values){
        switch(key){
            case "query":
                $("#filter-txt").val(values[key]);
                break;
        }
    }
}

function loadCompare(){
    var value=$("#trigger-type").val();
    switch(value){
        case "0":
            $("#compare").show();
            break;
    }
}

function loadContent(){
    var value=$("#sch-type").val();
    if(value==0){
        $("#load-content-daily").hide();
        $("#load-content-weekly").hide();
        $("#load-content-monthly").hide();
        $("#load-content-cronExp").hide();
        $("#load-content-hourly").css("display", "flex");

    }
    if(value==1){
        $("#load-content-hourly").hide();
        $("#load-content-weekly").hide();
        $("#load-content-monthly").hide();
        $("#load-content-cronExp").hide();
        $("#load-content-daily").css("display", "flex");

    }
    if(value==2){
        $("#load-content-hourly").hide();
        $("#load-content-daily").hide();
        $("#load-content-monthly").hide();
        $("#load-content-cronExp").hide();
        $("#load-content-weekly").css("display", "flex");

    }
    if(value==3){
        $("#load-content-hourly").hide();
        $("#load-content-daily").hide();
        $("#load-content-weekly").hide();
        $("#load-content-cronExp").hide();
        $("#load-content-monthly").css("display", "flex");

    }
    if(value==4){
        $("#load-content-hourly").hide();
        $("#load-content-daily").hide();
        $("#load-content-weekly").hide();
        $("#load-content-monthly").hide();
        $("#load-content-cronExp").show();
    }

}

function cronBuilder (){
    var value=$("#sch-type").val();
    if(value==0){
        var minute=$("#slt-minute").val();
        $("#cron-exp").val("0 0/"+minute+" * * * ?");
    }
    if(value==1){
        var time=$("#daily-timepicker").val().split(":");
        $("#cron-exp").val("0 "+time[1]+" "+time[0]+" * * ?");

    }
    if(value==2){
        var day=$("#slt-weekday").val();
        var time=$("#weekly-timepicker").val().split(":");
        $("#cron-exp").val("0 "+time[1]+" "+time[0]+" ? * "+day);

    }
    if(value==3){
        var date=$("#monthly-datepicker").val();
        var time=$("#monthly-timepicker").val().split(":");
        $("#cron-exp").val("0 "+time[1]+" "+time[0]+" "+date+" * ?");    //0 15 10 15 * ?

    }
    if(value==4){
        var minute=$("#slt-minute").val();
        $("#cron-exp").val("0 0/"+minute+" * * * ?");

    }
   // alert($("#cron-exp").val());
}



function loadAction(){
    var value=$("#alert-action").val();
    if(value=='logger'){
        $("#action-email").hide();
        $("#action-sms").hide();
        $("#action-logger").show();
    }
    if(value=='email'){
        $("#action-sms").hide();
        $("#action-logger").hide();
        $("#action-email").show();
    }
    if(value=='sms'){
        $("#action-email").hide();
        $("#action-logger").hide();
        $("#action-sms").show();
    }

}

function backward(){
    window.location=serverUrl+'/loganalyzer/site/alert/alert.jag';
}

function getColumns(){
    jQuery.ajax({
        type:"GET",
        url: serverUrl+"/api/alert/getColumns",
        success:function(res){
                var htmlcolunm = "";
                $.each(res, function (count) {
                    var value=res[count];
                    htmlcolunm += createList(value);
                });
                $("#field-data").append(htmlcolunm);
        },
        error:function (res) {
            alert(res);
        }
    });
}

function createList(column) {
    //$("#columns").empty();
    var displayText=column.replace("_","");
    return '<option value=\"'+column+'\">'+displayText+'</option>';
}

function addAlert(){
    var timeRange=$("#reportrange").val();
    var splitRange=timeRange.split("-");
    var timeFrom=splitRange[0];
    var timeTo=splitRange[1];
    var timeFromMils=Date.parse(timeFrom);
    var timeToMils=Date.parse(timeTo);
   $("#timestamp-from").val(timeFromMils);
    $("#timestamp-to").val(timeToMils);
}

$("#search-link").click(function(){
    window.location = serverUrl + '/loganalyzer/site/search/search.jag';
});