var baseUrl = getBaseUrl(window.location.href);
var regExValues = {};
var del = '';
var logEntryTable;
var other_del = false;
var selectedLogTemp;

$(document).ready(function () {
    logEntryTable = $('#LogEntries').DataTable();
    $('#extract_panel').hide();
    searchActivities(10);
    $('#regEx_panel').hide();
    $('#delimeter_panel').hide();
});

function CreateHtmlTable(logs) {
    var datatableFormat = [];
    var log;
    for (var index = 0; index < logs.length; index++) {
        log = logs[index];
        datatableFormat.push([log]);
    }
    logEntryTable.destroy();
    logEntryTable = $('#LogEntries').DataTable({data: datatableFormat});
    $('#LogEntries tbody').on('click', 'tr', function () {
        $("#extract-btn").attr('style', 'margin-left: 50.5%');
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        }
        else {
            logEntryTable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
        $('#selected-log-entry').html(logEntryTable.row('.selected').data()[0]);
    });
}

function searchActivities(lines) {
    var urlParams = splitUrl();
    jQuery.ajax({
        type: "GET",
        url: baseUrl + "/api/files/getLogs?noOfLines=" + lines +
        "&logStream=" + urlParams["logStream"] + "&fileName=" + urlParams["fileName"],
        async: false,
        success: function (res) {
            CreateHtmlTable(res);
        },
        error: function (res) {
            alert(res.responseText);
        }
    });
}

$(function () {
    $("#verifyConf").click(function () {
        var urlParams = splitUrl();
        var regPatterns = JSON.stringify(regExValues);
        window.location = baseUrl + '/loganalyzer/site/data/verify.jag?' +
        "logStream=" + urlParams["logStream"] + "&fileName=" + encodeURIComponent(urlParams["fileName"]) + "&regPatterns=" +
        encodeURIComponent(regPatterns);
    });
});


$(function () {
    $("#verifyDelConf").click(function () {
        if (other_del) {
            del = document.getElementById("del_input").value;
            document.getElementById('del_input').value = '';
        }
        var urlParams = splitUrl();
        window.location = baseUrl + '/loganalyzer/site/data/verify.jag?' +
        "logStream=" + urlParams["logStream"] + "&fileName=" + encodeURIComponent(urlParams["fileName"]) + "&delimeter=" + encodeURIComponent(del);
    });
});

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

function updateLine(value) {
    $("#SelectedLog").html(value);
}

$(document).on("click", ".line", function (e) {
    $("#SelectedLog").html($(this).html())
});

function extractView() {
    $('#main_panel').hide();
    $('#log_entry_panel').hide();
    $('#extract_panel').show();
    selectedLogTemp = document.getElementById('selected-log-entry').innerText;

}

function regexDev() {
    $('#extract_panel').hide();
    $('#delimeter_panel').hide();
    $('#regEx_panel').show();
    $('#verify-reg-button').hide();
}

function delDev() {
    $('#extract_panel').hide();
    $('#regEx_panel').hide();
    $('#delimeter_panel').show();
    $('#del_input').hide();
    $('#verify-button').hide();
}

$(function () {
    $("#applyReg").click(function () {
        $("#").highlightRegex(/[ar]/ig);
    });
});

$(function () {
    $("#saveReg").click(function () {
        $("#reg-input-dialog").modal('show');

    });
});

$(function () {
    $("#saveRegB").click(function () {
        var regName = document.getElementById("regEx_name");
        var regVal = document.getElementById("regEx_input");
        if (regName.value in regExValues) {
            alert("RegEx name already exist");
        } else {
            regExValues[regName.value] = regVal.value;
            document.getElementById('regEx_input').value = '';
            document.getElementById('regEx_name').value = '';
            $("#reg-input-dialog").modal('hide');
        }
    });

});

$('#regEx_input').keyup(function () {
    $('#verify-reg-button').show();
    $('#selected-log-entry').html(selectedLogTemp);
    var regex;
    try {
        regex = new RegExp($(this).val(), 'ig')
    }
    catch (e) {
        $('#regEx_input').addClass('error')
    }
    if (typeof regex !== 'undefined') {
        $(this).removeClass('error');
        if ($(this).val() != '')
            $('#selected-log-entry').highlightRegex(regex);
    }
});

$('#del_space').click(function () {
    $('#selected-log-entry').html(selectedLogTemp);
    del = "space";
    $('#del_input').hide();
    $('#verify-button').show();
    var str = document.getElementById('selected-log-entry').innerHTML;
    var res = str.split(/\s+/);
    if (res.length > 1) {
        for (var i = 0; i < res.length; i++) {
            highlightSearchTerms(res[i], 'selected-log-entry');
        }
    }

});

$('#del_comma').click(function () {
    $('#selected-log-entry').html(selectedLogTemp);
    del = "comma";
    $('#del_input').hide();
    $('#verify-button').show();
    var str = document.getElementById('selected-log-entry').innerHTML;
    var res = str.split(",");
    if (res.length > 1) {
        for (var i = 0; i < res.length; i++) {
            highlightSearchTerms(res[i], 'selected-log-entry');
        }
    }
});

$('#del_pipe').click(function () {
    $('#selected-log-entry').html(selectedLogTemp);
    del = "pipe";
    $('#del_input').hide();
    $('#verify-button').show();
    var str = document.getElementById('selected-log-entry').innerHTML;
    var res = str.split("|");
    if (res.length > 1) {
        for (var i = 0; i < res.length; i++) {
            highlightSearchTerms(res[i], 'selected-log-entry');
        }
    }
});

$('#del_tab').click(function () {
    $('#selected-log-entry').html(selectedLogTemp);
    del = "tab";
    $('#del_input').hide();
    $('#verify-button').show();
    var str = document.getElementById('selected-log-entry').innerHTML;
    var res = str.split('\t');
    if (res.length > 1) {
        for (var i = 0; i < res.length; i++) {
            highlightSearchTerms(res[i], 'selected-log-entry');
        }
    }
});

$('#del_other').click(function () {
    $('#selected-log-entry').html(selectedLogTemp);
    other_del = true;
    $('#del_input').show();
});

$('#del_input').keyup(function () {
    $('#verify-button').show();
    $('#selected-log-entry').html(selectedLogTemp);
    var str = document.getElementById('selected-log-entry').innerHTML;
    if ($(this).val() != "") {
        var res = str.split($(this).val());
        if (res.length > 1) {
            for (var i = 0; i < res.length; i++) {
                highlightSearchTerms(res[i], 'selected-log-entry');
            }
        }
    }
});