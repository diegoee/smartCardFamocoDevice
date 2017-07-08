/*global document, window, $*/
(function myApp(){

/*
    document.getElementsByTagName('body')[0].innerHTML = '<input id="btnEXE" type="button" value="EXE" style="width: 100%;"/>'+document.getElementsByTagName('body')[0].innerHTML;
    document.getElementById('btnEXE').addEventListener('click', function(){
    window.location.search ='?total=5&ok=2&nook=3';
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

         document.getElementById('total').innerHTML = getUrlVars().total;
         document.getElementById('noOK').innerHTML = getUrlVars().nook;
         document.getElementById('OK').innerHTML = getUrlVars().ok;

})();
