/*global document, window, $*/
(function myApp(){

  /*
  if (window.location.search===''){
    window.location.href = window.location.href+'?obj='+'{"data":[{"uid": "BDAAA4E6", "val": "true", "user": "testUser", "fecha": "2017/06/18-15:03"},{"uid": "BDAAA4E6", "val": "true", "user": "testUser", "fecha": "2017/06/18-15:04"}]}';
  }
  */

  function getUrlVars()
  {
      var query=window.location.search.substring(1);
      var q=query.split("&");
      var vars=[];
      for(var i=0;i<q.length;i++){
          var x=q[i].split("=");
          var k=x[0];
          var v=x[1];
          vars[k]=v;
      }
    return vars;
  }

  var obj = JSON.parse(getUrlVars().obj
    .replace(/true/g,' OK')
    .replace(/false/g,'NO OK')
    .replace(/%20/g,' ')
    .replace(/%22/g,'"')
    .replace(/%2C/g,',')
    .replace(/%7B/g,'{')
    .replace(/%2C/g,'[')
    .replace(/%7D/g,'}')
    .replace(/%5D/g,']')
    .replace(/%2F/g,'/')
    .replace(/%3A/g,':')
    .replace(/\+/g,' '));

    var cOK = 0;
    var cNoOk = 0;
    for (var i=0; i<obj.data.length; i++){
        if (obj.data[i].val==='OK'){
          cOK++;
        }
        if (obj.data[i].val==='NO OK'){
          cNoOk++;
        }
    }

    $('#counter').append('OK: '+cOK+' <> NO OK: '+cNoOk+'');


  //document.getElementById('container').innerHTML = obj.data[0].uid + " - - -  " + obj.data[1].uid;

  $.dynatableSetup({
    features: {
      paginate: false,
      sort: true,
      pushState: true,
      search: false,
      recordCount: false,
      perPageSelect: false
    }
  });

  $('#table').dynatable({
    dataset: {
      records: obj.data
    }
  });

  $('[data-dynatable-column="user"] a').text('Usuario');
  $('[data-dynatable-column="uid"] a').text('UID de tarjeta');
  $('[data-dynatable-column="val"] a').text('Resultado validación');
  $('[data-dynatable-column="fecha"] a').text('Fecha Fiscalización');


})();
