/*global document, window, $*/
(function myApp(){

  /*
  $('body').before($('<input type="button" value="EXE" style="width: 100%;"/>').on('click',function(){
      window.location.search ='?obj='+'{"data":[{"uid": "BDAAA4E6", "val": "true", "user": "testUser", "fecha": "2017/06/18-15:03"},{"uid": "BDAAA4E6", "val": "true", "user": "testUser", "fecha": "2017/06/18-15:04"},{"uid": "BDAAA4E6", "val": "true", "user": "testUser", "fecha": "2017/06/18-15:03"},{"uid": "BDAAA4E6", "val": "true", "user": "testUser", "fecha": "2017/06/18-15:04"},{"uid": "BDAAA4E6", "val": "false", "user": "testUser", "fecha": "2017/06/18-15:03"},{"uid": "BDAAA4E6", "val": "true", "user": "testUser", "fecha": "2017/06/18-15:04"},{"uid": "BDAAA4E6", "val": "true", "user": "testUser", "fecha": "2017/06/18-15:03"},{"uid": "BDAAA4E6", "val": "false", "user": "testUser", "fecha": "2017/06/18-15:04"},{"uid": "BDAAA4E6", "val": "true", "user": "testUser", "fecha": "2017/06/18-15:03"},{"uid": "BDAAA4E6", "val": "true", "user": "testUser", "fecha": "2017/06/18-15:04"}]}';
    }));
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

    var c = [];  c[0]=0;  c[1]=0;  c[2]=0;
    for (var i=0; i<obj.data.length; i++){
        c[0]++;
        console.log(obj.data[i].val);
        if (obj.data[i].val.trim()==='OK'){
          c[1]++;
        }
        if (obj.data[i].val.trim()==='NO OK'){
          c[2]++;
        }
    }
    $('#counterTotal').append('Total: '+c[0]+'');
    $('#counterOK').append('OK: '+c[1]+'');
    $('#counterNOOK').append('NO OK: '+c[2]+'');


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
