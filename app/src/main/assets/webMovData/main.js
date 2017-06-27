/*global document, window, $*/
(function myApp(){
/*
  document.getElementsByTagName('body')[0].innerHTML = '<input id="btnEXE" type="button" value="EXE" style="width: 100%;"/>'+document.getElementsByTagName('body')[0].innerHTML;
  document.getElementById('btnEXE').addEventListener('click', function(){
      window.location.search ='?obj={"data":[{"pos": "06", "fechaHora": "2017-06-07 23:17", "operacion": "Validación", "titulo": "1", "tramos": "0", "viajeros": "1", "viajeTransbordo": "64", "ultimaLinea": "2", "ultimoSentido": "Ida", "autobusTranvia": "152", "saldo": "Saldo: 0.70 Euros", "operador": "00"},{"pos": "05", "fechaHora": "2017-06-07 20:08", "operacion": "Validación", "titulo": "1", "tramos": "0", "viajeros": "1", "viajeTransbordo": "64", "ultimaLinea": "1", "ultimoSentido": "3", "autobusTranvia": "160", "saldo": "Saldo: 0.70 Euros", "operador": "00"},{"pos": "04", "fechaHora": "2017-06-05 13:16", "operacion": "Recarga", "titulo": "1", "tramos": "0", "viajeros": "0", "viajeTransbordo": "0", "ultimaLinea": "0", "ultimoSentido": "0", "autobusTranvia": "1", "saldo": "Saldo: 0.70 Euros", "operador": "00"},{"pos": "02", "fechaHora": "2017-02-15 16:06", "operacion": "Recarga", "titulo": "1", "tramos": "0", "viajeros": "0", "viajeTransbordo": "0", "ultimaLinea": "0", "ultimoSentido": "0", "autobusTranvia": "1", "saldo": "Saldo: 0.00 Euros", "operador": "00"},{"pos": "03", "fechaHora": "2017-02-15 16:06", "operacion": "Recarga", "titulo": "1", "tramos": "0", "viajeros": "0", "viajeTransbordo": "0", "ultimaLinea": "0", "ultimoSentido": "0", "autobusTranvia": "1", "saldo": "Saldo: 0.70 Euros", "operador": "00"},{"pos": "01", "fechaHora": "2000-01-01 00:00", "operacion": "0", "titulo": "Ninguno", "tramos": "0", "viajeros": "0", "viajeTransbordo": "0", "ultimaLinea": "0", "ultimoSentido": "0", "autobusTranvia": "0", "saldo": "Saldo: 0.00 Euros", "operador": "00"},{"pos": "07", "fechaHora": "2000-01-01 00:00", "operacion": "0", "titulo": "Ninguno", "tramos": "0", "viajeros": "0", "viajeTransbordo": "0", "ultimaLinea": "0", "ultimoSentido": "0", "autobusTranvia": "0", "saldo": "Saldo: 0.00 Euros", "operador": "00"},{"pos": "08", "fechaHora": "2000-01-01 00:00", "operacion": "0", "titulo": "Ninguno", "tramos": "0", "viajeros": "0", "viajeTransbordo": "0", "ultimaLinea": "0", "ultimoSentido": "0", "autobusTranvia": "0", "saldo": "Saldo: 0.00 Euros", "operador": "00"},{"pos": "09", "fechaHora": "2000-01-01 00:00", "operacion": "0", "titulo": "Ninguno", "tramos": "0", "viajeros": "0", "viajeTransbordo": "0", "ultimaLinea": "0", "ultimoSentido": "0", "autobusTranvia": "0", "saldo": "Saldo: 0.00 Euros", "operador": "00"},{"pos": "10", "fechaHora": "2000-01-01 00:00", "operacion": "0", "titulo": "Ninguno", "tramos": "0", "viajeros": "0", "viajeTransbordo": "0", "ultimaLinea": "0", "ultimoSentido": "0", "autobusTranvia": "0", "saldo": "Saldo: 0.00 Euros", "operador": "00"},{"pos": "11", "fechaHora": "2000-01-01 00:00", "operacion": "0", "titulo": "Ninguno", "tramos": "0", "viajeros": "0", "viajeTransbordo": "0", "ultimaLinea": "0", "ultimoSentido": "0", "autobusTranvia": "0", "saldo": "Saldo: 0.00 Euros", "operador": "00"}]}';
  });
*/
  function getUrlVars()
  {
      var query = window.location.search.substring(1);
      query = query.replace(/%20/g,' ')
          .replace(/%22/g,'"')
          .replace(/%2C/g,',')
          .replace(/%7B/g,'{')
          .replace(/%2C/g,'[')
          .replace(/%7D/g,'}')
          .replace(/%5D/g,']')
          .replace(/%2F/g,'/')
          .replace(/%3A/g,':')
          .replace(/\+/g,' ');
      var q = query.split("&");
      var vars=[];
      for(var i=0;i<q.length;i++){
          var x=q[i].split("=");
          var k=x[0];
          var v=x[1];
          vars[k]=v;
      }
      return vars;
  }

  var data = JSON.parse(getUrlVars().obj
  .replace(/%20/g,' ')
  .replace(/%22/g,'"')
  .replace(/%2C/g,',')
  .replace(/%7B/g,'{')
  .replace(/%2C/g,'[')
  .replace(/%7D/g,'}')
  .replace(/%5D/g,']')
  .replace(/%2F/g,'/')
  .replace(/%3A/g,':')
  .replace(/%C3%B3/g,'ó')
  .replace(/\+/g,' '));

  data = data.data;


  document.getElementById('container').innerHTML ='';

  var div;

  for (var i=0;i<data.length;i++){
    div = document.createElement('div');
    div.innerHTML = data[i].fechaHora+' - '+data[i].operacion;
    div.className = 'row';
    div.var=i;
    document.getElementById('container').appendChild(div);

    div = document.createElement('div');
    div.innerHTML = ''+
      '<p>pos: '+data[i].pos+'</p>'+
      '<p>Viajeros: '+data[i].viajeros+'</p>'+
      '<p>Última Linea: '+data[i].ultimaLinea+'</p>'+
      '<p>Último Sentido: '+data[i].ultimoSentido+'</p>'+
      '<p>Tranvia: '+data[i].autobusTranvia+'</p>'+
      '<p>'+data[i].saldo+'</p>'+
      '';
    div.className = 'detail';
    div.var=i;
    document.getElementById('container').appendChild(div);
  }

    var color = function color() {
      var e = document.getElementsByClassName('detail');
      for (var i=0; i<e.length; i++){
        if(e[i].var===this.var){
          e = e[i];
          break;
        }
      }
      if(e.style.display==='none'){
        e.style.display = 'inline';
       }else{
         e.style.display = 'none';
       }

    };

    div = document.getElementsByClassName('detail');
    for (i=0; i<div.length; i++){
      div[i].style.display = 'none';
    }

    div = document.getElementsByClassName('row');
    for (i=0; i<div.length; i++){
      div[i].addEventListener('click',color,false);
    }


  var resizeElem = function resizeElem(){
      var ele = document.getElementById('container');
      var chi = document.getElementsByClassName('row');
      ele.style.height = window.innerHeight-6+'px';
      var divHeight = Math.floor((window.innerHeight-10)/chi.length)-2;

      for (var i = 0;i<chi.length;i++){
          chi[i].style.height = divHeight+'px';
          chi[i].style.lineHeight = divHeight+'px';
      }
  };

  resizeElem();
  window.addEventListener('resize', function(){
      resizeElem();
  });

})();
