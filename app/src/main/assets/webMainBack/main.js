/*global document, window, $*/
(function myApp(){

/*
    document.getElementsByTagName('body')[0].innerHTML = '<input id="btnEXE" type="button" value="EXE" style="width: 100%;"/>'+document.getElementsByTagName('body')[0].innerHTML;
    document.getElementById('btnEXE').addEventListener('click', function(){
    window.location.search ='?total=5&ok=2&nook=3';
    });
*/

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

    var div;

    var createDiv = function createDiv(className,html){
        var div;
        div = document.createElement('div');
        div.className = className;
        div.innerHTML = html;
        return div;
    };

    div = createDiv('','Total de Tarjetas: '+getUrlVars().total);
    document.getElementById('container').appendChild(div);
    div = createDiv('','noOK/OK = '+getUrlVars().nook+'/'+getUrlVars().ok);
    document.getElementById('container').appendChild(div);
    div = createDiv('','');
    document.getElementById('container').appendChild(div);
    div = createDiv('','');
    document.getElementById('container').appendChild(div);
    div = createDiv('','');
    document.getElementById('container').appendChild(div);
    div = createDiv('','');
    document.getElementById('container').appendChild(div);


    var resizeElem = function resizeElem(){
        var ele = document.getElementById('container');
        ele.style.height = window.innerHeight-6+'px';
        var divHeight = Math.floor((window.innerHeight-6)/ele.childNodes.length)-2;

        for (var i = 0;i<ele.childNodes.length;i++){
            ele.childNodes[i].style.height = divHeight+'px';
            ele.childNodes[i].style.lineHeight = divHeight+'px';
        }
    };

    resizeElem();
    window.addEventListener('resize', function(){
        resizeElem();
    });

})();
