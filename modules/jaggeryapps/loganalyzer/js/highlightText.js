function doHighlight(DivText, searchTerm) {

    highlightStartTag = "<font style='color:black; background-color:greenyellow;'>";
    highlightEndTag = "</font>";
    var newText = "";
    var i = -1;
    var lcSearchTerm = searchTerm.toLowerCase();
    var lcDivText = DivText.toLowerCase();

    while (DivText.length > 0) {
        i = lcDivText.indexOf(lcSearchTerm, i + 1);
        if (i < 0) {
            newText += DivText;
            DivText = "";
        } else {
            // skip anything inside an HTML tag
            if (DivText.lastIndexOf(">", i) >= DivText.lastIndexOf("<", i)) {
                // skip anything inside a <script> block
                if (lcDivText.lastIndexOf("/script>", i) >= lcDivText.lastIndexOf("<script", i)) {
                    newText += DivText.substring(0, i) + highlightStartTag + DivText.substr(i, searchTerm.length) + highlightEndTag;
                    DivText = DivText.substr(i + searchTerm.length);
                    lcDivText = DivText.toLowerCase();
                    i = -1;
                }
            }
        }
    }
    return newText;
}

function highlightSearchTerms(searchText,divId) {
    searchArray = [searchText];
    var div=document.getElementById(divId);
    var DivText = div.innerHTML;
    for (var i = 0; i < searchArray.length; i++) {
        DivText = doHighlight(DivText, searchArray[i]);
    }
    div.innerHTML = DivText;
    return true;
}