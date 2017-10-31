var baseUrl = getBaseUrl(window.location.href);

$(document).ready(function () {

    var next = 1;
    $(".add-more").click(function (e) {
        e.preventDefault();
        var addto = "#logStream" + next;
        var addRemove = "#logStream" + (next);
        next = next + 1;
        var newIn = '<input autocomplete="off" class="input form-control" id="logStream' + next + '" name="logStream' + '" type="text">';
        var newInput = $(newIn);
        var removeBtn = '<button id="remove' + (next - 1) + '" class="btn btn-danger remove-field pull-right" >-</button>';
        var removeButton = $(removeBtn);
        $(addto).after(newInput);
        $(addRemove).after(removeButton);
        $("#logStream" + next).attr('data-source', $(addto).attr('data-source'));
        $("#count").val(next);

        $('.remove-field').click(function (e) {
            e.preventDefault();
            var fieldNum = this.id.charAt(this.id.length - 1);
            var fieldID = "#logStream" + fieldNum;
            $(this).remove();
            $(fieldID).remove();
        });
    });

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

    $('#add-log-form').ajaxForm({
        url: baseUrl + '/api/files/upload',

        beforeSubmit: function (arr, $form, options) {
            var fields = [];
            var fieldString = "";
            $form.find(':input').each(function () {
                if (this.type === 'text') {
                    fields.push(this.value);
                }
            });
            fieldString = fields.join(',');
            var first = true;
            arr = arr.filter(function (datum) {
                if ((datum.name === "logStream") && (!first)) {
                    return false;
                }
                if (datum.name === "logStream") {
                    datum.value = fieldString;
                    first = false;
                }
                return true;
            });
            return true;
        },
        success: function (res) {
            window.location = baseUrl + '/loganalyzer/site/data/filter.jag?' + "logStream=" + res[0] + "&" + "fileName=" + res[1];
        },
        error: function (res) {
            var errorText = JSON.parse(res.responseText)["exception"];
            handleNotification(errorText, '#notification-area', 'warning');
        }
    });
});