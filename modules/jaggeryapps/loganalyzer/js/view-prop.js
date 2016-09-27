function genTable(values) {
    for (var key in values) {
        switch (key) {
            case 'logStream':
                $('#summery-table > tbody:last-child').append('<tr><th width="10%" id="summery-table">Log Stream:</th><td id="summery-table">' + values[key] + '</td></tr>');
                break;
            case 'fileName':
                $('#summery-table > tbody:last-child').append('<tr><th width="10%" id="summery-table">File Name:</th><td id="summery-table">' + values[key] + '</td></tr>');
                break;
            case 'delimeter':
                if( values[key]!='') {
                    $('#summery-table > tbody:last-child').append('<tr><th width="10%" id="summery-table">Delimeter:</th><td id="summery-table">' + values[key] + '</td></tr>');
                }
                break;
            case 'regPatterns':
                regPatterns= values[key];
                if(regPatterns.length>2) {
                    $('#summery-table > tbody:last-child').append('<tr><th width="10%" id="summery-table">RegEx Patterns:</th><td id="summery-table">' + values[key] + '</td></tr>');
                }
                break;
        }
    }
    $('#summery-table > tbody:last-child').append('<tr><td colspan="2" id="summery-table"><button style="margin-left: 35%;" class="btn btn-main" id="searchLog">Search</button></td></tr>');
}