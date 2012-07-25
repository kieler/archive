$(document).ready(function() {
    $("#sort").tablesorter({
        widgets: ['zebra'],
        
        // sort on the first column, order asc 
        sortList: [[0,0]] 
    });
    
    $(".numbercell").each(function(index) {
        var value = $(this).text();
        var leadingzeros = pad(value, 10);
        var numberwithspace = insertSpace(value);
        $(this).html('<span class="hide">'+leadingzeros+'</span>'+numberwithspace);
    });
    
    $(".hide").hide();
});

function pad(num, size) {
    var s = num+"";
    while (s.length < size) s = "0" + s;
    return s;
}

function insertSpace(number) {
    var ret = number+"";
    var i = number.length-3;
    while (i > 0) {
        ret = ret.substring(0, i) + ' ' + ret.substring(i);
        i=i-3;
    }
    return ret;
}