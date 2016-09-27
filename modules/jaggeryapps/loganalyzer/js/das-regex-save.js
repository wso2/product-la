function deleteRow(row){
    //Get the id of the current row
    var rowId = $(row).parent().parent().data('rowId');
    delete regExValues[rowId];
    $(row).parent().parent().remove();
}

$(function(){
    $('#tab-saved-patterns').on('click',function(){
        //Clean the existing table
        $('#saved-regex-table > tbody').html('');
        for(var key in regExValues) {
            $('#saved-regex-table > tbody:last-child').append('<tr data-row-id="' + key + '"><td>'+key+'</td><td>'+regExValues[key]+'</td><td><a href="#" onclick="deleteRow(this)">Delete</a></td></tr>');
        }
    });
});