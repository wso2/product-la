var baseUrl = getBaseUrl(window.location.href);
var urlParam;


$(document).ready(function () {

    urlParams = splitUrl();
    genTable(urlParams);

    $('#nav').affix({
        offset: {
            top: $('header').height()
        }
    });

    $("[data-toggle=popover]").popover();

    $(".ctrl-asset-type-switcher").popover({
        html: true,
        content: function () {
            return $('#content-asset-types').html();
        }
    });
});

$(function () {
    $("#searchLog").click(function () {
        var regs = urlParams["regPatterns"];
        var regjs;
        if(typeof regs != 'undefined'){
            regjs = JSON.parse(regs);
        }
        var lgStream = urlParams["logStream"];
        var fileName = urlParams["fileName"];
        var delimeter = urlParams["delimeter"];
        var data = {};
        data.logStream = lgStream;
        data.regExPatterns = regjs;
        data.fileName = fileName;
        data.delimiter = delimeter;

        jQuery.ajax({
            type: "POST",
            url: baseUrl + '/api/files/publish',
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (res) {
                window.location = baseUrl + '/loganalyzer/site/search/search.jag'; //TODO handle success in API, pass the query
            },
            error: function (res) {
                alert(res.error);
            }
        });
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