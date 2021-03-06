/*globals require, window,document,location,$*/
require.config({
  baseUrl: '',
  paths: {
    jquery: '../libsJS/jquery/dist/jquery.min',
    bootstrap: '../libsJS/bootstrap/dist/js/bootstrap.min',
    moment: '../libsJS/moment/moment'
  },
  shim: {
    bootstrap : {
      deps : ['jquery']
    }
  }
});

require([
  'jquery',
  'moment',
  'bootstrap'
  ],
  function(
    $,
    moment
  ){
  'use strict';

/*
      document.getElementsByTagName('body')[0].innerHTML = '<input id="btnEXE" type="button" value="EXE" style="width: 100%; "/>'+document.getElementsByTagName('body')[0].innerHTML;
      document.getElementById('btnEXE').addEventListener('click', function(){
      window.location.search ='?uid=C23F8DE6&ntarjeta=270628164&fecha=2017-08-23 19:30&login=testUser&fechaVal=2017-08-23 18:00&paradaVal=34930&nViajeros=1&tranvia=151&tipoTarjeta=0 - 0&operador=TDM&saldo=0,00-Euros';
      });
  */

      //$('body').html('<div id="container"></div>');
      document.getElementById('container').innerHTML ='';

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
              .replace(/%E2/g,'€')
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

      var div1,div2;

      var createDiv = function createDiv(level,flag,appendEle){
          var div;
          div = document.createElement('div');
          div.className = level;
          if (flag){
              div.innerHTML = appendEle;
          }
          return div;
      };
      var noCard = function noCard(){
          $('#div1').remove();
          $('#container').html('Tarjeta no leida o esperando lectura de tarjeta...');
          $('#container').css({'font-size': '1.5em'});
      }

      if((getUrlVars().uid===undefined)||(getUrlVars().uid==='NoData')||(getUrlVars().uid===null)||(getUrlVars().uid==='')||(getUrlVars().uid==='null')){
            noCard();
      }else{
          var aux = ' ';

          div1 = createDiv('level1',true, 'Número de Tarjeta: '+getUrlVars().ntarjeta);
          div1.style.paddingLeft='5px';
          document.getElementById('container').appendChild(div1);

          div1 = createDiv('level1',false);
          div2 = createDiv('level2',true,'UID: '+getUrlVars().uid);
          div1.appendChild(div2);
          div2 = createDiv('level2',true,'Usuario: '+getUrlVars().login);
          div1.appendChild(div2);
          document.getElementById('container').appendChild(div1);

          div1 = createDiv('level1',false);
          div2 = createDiv('level2',true,'Fecha Act.: ');
          div1.appendChild(div2);

          aux = getUrlVars().fecha;
          aux = aux.substr(8,2)+aux.substr(4,4)+aux.substr(0,4)+' '+aux.substr(11,5);
          div2 = createDiv('level2',true,aux);
          div1.appendChild(div2);
          document.getElementById('container').appendChild(div1);

          div1 = createDiv('level1',false);
          div2 = createDiv('level2',true,'Fecha Val.: ');
          div1.appendChild(div2);
          if((getUrlVars().fechaVal===undefined)||(getUrlVars().fechaVal==='NoData')||(getUrlVars().fechaVal===null)||(getUrlVars().fechaVal==='')||(getUrlVars().fechaVal==='null')){
            aux = ' ';
          }else{
            aux = getUrlVars().fechaVal;
          }
          aux = aux.substr(8,2)+aux.substr(4,4)+aux.substr(0,4)+' '+aux.substr(11,5);
          div2 = createDiv('level2',true,aux);
          div2.style.fontSize='1.5em';
          div2.style.backgroundColor='#B7CA3D';
          div1.appendChild(div2);
          document.getElementById('container').appendChild(div1);

          div1 = createDiv('level1',false);
          if((getUrlVars().paradaVal===undefined)||(getUrlVars().paradaVal==='NoData')||(getUrlVars().paradaVal===null)||(getUrlVars().paradaVal==='')||(getUrlVars().paradaVal==='null')){
            aux = ' ';
          }else{
            aux = getUrlVars().paradaVal;
          }
          div2 = createDiv('level2',true,'Parada: '+aux);
          div2.style.fontSize='1.5em';
          div1.appendChild(div2);
          if((getUrlVars().tranvia===undefined)||(getUrlVars().tranvia==='NoData')||(getUrlVars().tranvia===null)||(getUrlVars().tranvia==='')||(getUrlVars().tranvia==='null')){
            aux = ' ';
          }else{
            aux = getUrlVars().tranvia;
          }
          div2 = createDiv('level2',true,'Tranvia: '+aux);
          div2.style.fontSize='1.75em';
          div1.appendChild(div2);
          document.getElementById('container').appendChild(div1);

          if((getUrlVars().nViajeros===undefined)||(getUrlVars().nViajeros==='NoData')||(getUrlVars().nViajeros===null)||(getUrlVars().nViajeros==='')||(getUrlVars().nViajeros==='null')){
            aux = ' ';
          }else{
            aux = getUrlVars().nViajeros;
          }
          div1 = createDiv('level1',true,'Nº Viajeros: '+aux);
          div1.style.paddingLeft='5px';
          div1.style.fontSize='1.5em';
          document.getElementById('container').appendChild(div1);

          div1 = createDiv('level1',false);
          div2 = createDiv('level2',true,'Título: '+getUrlVars().tipoTarjeta);
          div1.appendChild(div2);

          if((getUrlVars().operador===undefined)||(getUrlVars().operador==='NoData')||(getUrlVars().operador===null)||(getUrlVars().operador==='')||(getUrlVars().operador==='null')){
              aux = ' ';
            }else{
              aux = getUrlVars().operador;
            }
          div2 = createDiv('level2',true,'Operador: '+aux);
          div1.appendChild(div2);
          document.getElementById('container').appendChild(div1);

          div1 = createDiv('level1',true, getUrlVars().saldo);
          div1.style.paddingLeft='5px';
          document.getElementById('container').appendChild(div1);

          var hEle = 80;
          $('.buttonCSS').css({'height': hEle+'px'});
          $('#div1').css({'height': hEle+'px'});
          var resizeElem = function resizeElem(){
              var ele = document.getElementById('container');
              var h = window.innerHeight-5-hEle;
              ele.style.height = h+'px';
              var divHeight = Math.floor((h)/ele.childNodes.length)-2;

              for (var i = 0;i<ele.childNodes.length;i++){
                  ele.childNodes[i].style.height = divHeight+'px';
                  ele.childNodes[i].style.lineHeight = divHeight+'px';
              }
          };

          var colorRedBtn = function colorRedBtn(){
            var f = moment(getUrlVars().fecha);//.format('YYYY-MM-DD hh:mm'); //second
            var fv= moment(getUrlVars().fechaVal);//.format('YYYY-MM-DD hh:mm');

            console.log(f);
            console.log(fv);

            var diff = Math.abs(f.diff(fv,'minutes'));
            console.log(diff);

            if (diff>=90){ //90min
              $('#cancelBtn').removeClass('btn-default').addClass('btn-danger').html('<p>Rechazar.</p><p>Fecha Val.</p> <p>diff > 90 min</p>');
            }
          };

          $('#okBtn').on('click',function(){
            noCard();
            Android.writeActionOK();
          });
          $('#noOkBtn').on('click',function(){
            noCard();
            Android.writeActionNoOK();
          });

          colorRedBtn();
          resizeElem();
          window.addEventListener('resize', function(){
              resizeElem();
          });

        }

});


