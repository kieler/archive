(function($) {
  
  /**
   * Performs a layout request for the passed graph and returns a serialized version of the
   * requested output format.
   * 
   * All of the following parameters are to be passed as one object parameter.
   * 
   * {server}
   *            the location of the webserver, e.g., 'http://localhost:9444'.
   * {inFormat}
   *            the input format, e.g., 'org.json'.
   * {outFormat}
   *            the output format, e.g., 'org.w3.svg'.
   * {options}
   *            a map containing (key, value) pairs representing layout options.
   * {graph}
   *            the graph in a serialized form of the input format.
   *            
   * {return} the layouted graph in a serialized form of the output format.
   */
  $.kielerLayout = function(opts) {

    // gather information
    var server = opts.server || "http://localhost:9444";
    var graph = opts.graph;
    var options = opts.options || {};
    var iFormat = opts.iFormat;
    var oFormat = opts.oFormat;
    var success = opts.success;
    var error = opts.error || function() {};
    
    $.ajax({
      type : 'GET',
      contentType : 'application/json',
      dataType : 'text',
      url : server + '/live',
      data : {
        graph : JSON.stringify(graph),
        config : JSON.stringify(options),
        iFormat : iFormat,
        oFormat : oFormat
      },
      success : success,
      error : error
    });
  };
}(jQuery));