 (function(jQuery) {
   
  /**
   * opens and scrolls to the selection that is specified after the # in the url
   */
  var openExtension = function() {
    // make sure we open what is in the url
    var col = location.hash.substr(1);
    if (col) {
      $('#' + col).collapse();
      $('#' + col).parents('.panel-collapse.collapse').collapse();
      var ele = $("#" + col + "");
      if (ele && ele.offset()) {
        $('html, body').animate({
          scrollTop : ele.offset().top - 100
        }, 'slow');
      }
    }
  };
  
  var prettyPrintRemoveWs = function() {
    // remove leading whitespace from each code element
    $('.prettyprint').each(function() {
       var code = $(this);
       var text = code.html();
       if (text) {
          var lines = text.split("\n");
          if (lines.length > 0) {
              var match = /^\s+(.+)$/.exec(lines[0]);
              if (match && match.length > 1) {
                var length = match[0].length - match[1].length;         
                var strippedLines = $.map(lines, function(e, i) {
                    var woWhitespaces = e.substring(length);
                    return woWhitespaces;
                });
              }
              // replace with the whitespace free code
              code.html(strippedLines.join("\n"));
          }
       }
    });
  };
 
  jQuery( document ).ready( function() {
      
    // how to style the index page typeahead
    $('input.typeahead').typeahead({
        name: 'find',
        prefetch: './extensions.json',
        template:  ['<p class="text-muted typeaheadFile">{{file}}</p>',
                    '<p class="typeaheadExtension">{{firstParam}}.<b>{{value}}</b>({{params}}): {{returnType}}</p>',
                    '{{#example}}<pre class="prettyprint lang-xtend white-space-pre">{{text}}</pre>{{/example}}'
                   ].join(''),
        engine: Hogan,
        limit: 10
    });
    
    // how to style the top bar typeahead
    $('input.typeahead-bar').typeahead({
        name: 'find-bar',
        prefetch: './extensions.json',
        template:  ['<p class="text-muted" style="font-size: 0.7em; float: right; position: relative;">{{file}}</p>',
                    '<p class="typeaheadExtension">{{firstParam}}.<b>{{value}}</b>({{params}})</p>'
                   ].join(''),
        engine: Hogan,
        limit: 10
    });
     
    // fix
    $('.tt-query').css('background-color','#fff');
     
    // open extensions that are selected within the typeahead
    $('.typeahead, .typeahead-bar').bind('typeahead:selected', function(obj, datum, name) { 
      
      // already on the page?
      if (location.pathname.indexOf("/classes.html") > -1) {
        location.hash = "#collapse" + datum.hrefid;
        openExtension();
      } else {
        location.href = "classes.html#collapse" + datum.hrefid;
      }
      
    });
    
    // open extension that is selected upon initial load
    openExtension();
    
    prettyPrintRemoveWs();
    prettyPrint();
    
    // pretty printing
    /*$.SyntaxHighlighter.init({
        // FIXME have to use linnumbers atm, otherwise strip whitespace does not work
        lineNumbers: true,
        prettifyBaseUrl: './js',
        baseUrl: './css'
    });*/
  });
}(jQuery))