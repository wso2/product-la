/**
 * Created by nalaka on 2/19/16.
 */
$(function(){
    urlParams = splitUrl();
    setParams(urlParams);
    $(".alert-datepicker").datepicker();
    $(".alert-timepicker").timepicker({  timeFormat: 'H:i',
        interval: 15 // 15
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
function setParams(values){
    for(var key in values){
        switch(key){
            case "query":
                $("#filter-txt").val(values[key]);
                break;
        }
    }
}

