var MongoClient = require('mongodb').MongoClient, format = require('util').format;
var http = require('http');
var url = require('url');
var qs = require('qs');

MongoClient.connect('mongodb://127.0.0.1:27017/kieler/', function(err, db) {
  if (err)
    throw err;

  http.createServer(function(req, res) {

    // check for valid requests
    if (req.method == 'OPTIONS') {
      // cors preflight
      res.writeHead(200, {
        'Access-Control-Allow-Origin' : '*',
        'Access-Control-Allow-Methods' : 'GET',
        'Access-Control-Allow-Headers' : 'Content-Type'
      });
      res.end();

    } else if (req.method != 'GET') {
      res.writeHead(501, {}); // Not Implemented
      res.end();
      return;
    }

    var params = url.parse(req.url);
    if (params.pathname == '/stats') {

      // use 'qs' for the querystring as it supports hierarchical nesting of the json
      var query = qs.parse(params.query);

      // get data
      var col = db.collection(query.coll);
      
      // execute the specified query
      col.find(query.obj,
       query.fields,
       query.opts 
      ).toArray(function(err, doc) {

        if (err) {
          res.writeHead(400, {});
        }

        // header
        res.writeHead(200, {
          'Access-Control-Allow-Origin' : '*', // cors
          'Content-Type' : 'json'
        });
        // json result
        res.end(JSON.stringify(doc, 0, 2));

      });

    } else {
      // unknown address
      res.writeHead(404, {}); // Not Found
      res.end();
      return;
    }

    // server end
  }).listen(1337, '127.0.0.1');
});

console.log('Server running at http://127.0.0.1:1337/');