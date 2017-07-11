/*global document, window, $*/
(function myApp(){
    /*
    document.getElementsByTagName('body')[0].innerHTML = '<input id="btnEXE" type="button" value="EXE" style="width: 100%;"/>'+document.getElementsByTagName('body')[0].innerHTML;
    document.getElementById('btnEXE').addEventListener('click', function(){
    window.location.search ='?uid=BDAAA4E6&fecha=2010/01/16-18:45:36&login=testUser&fechaVal=2010/01/16-18:45:36&paradaVal=6399&nViajeros=1&saldo=0&tranvia=NoIdea&operador=NoIdea';
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
    div2 = createDiv('level2',true,getUrlVars().fecha);
    div1.appendChild(div2);
    document.getElementById('container').appendChild(div1);

    div1 = createDiv('level1',false);
    div2 = createDiv('level2',true,'Fecha Val.: ');
    div1.appendChild(div2);
    div2 = createDiv('level2',true,getUrlVars().fechaVal);
    div2.style.fontSize='1.5em';
    div2.style.backgroundColor='#B7CA3D';
    div1.appendChild(div2);
    document.getElementById('container').appendChild(div1);

    div1 = createDiv('level1',false);
    div2 = createDiv('level2',true,'Parada: '+getUrlVars().paradaVal);
    div2.style.fontSize='1.5em';
    div1.appendChild(div2);
    div2 = createDiv('level2',true,'Tranvia: '+getUrlVars().tranvia);
    div2.style.fontSize='1.75em';
    div1.appendChild(div2);
    document.getElementById('container').appendChild(div1);

    div1 = createDiv('level1',true,'Nº Viajeros: '+getUrlVars().nViajeros);
    div1.style.paddingLeft='5px';
    div1.style.fontSize='1.5em';
    document.getElementById('container').appendChild(div1);

    div1 = createDiv('level1',false);
    div2 = createDiv('level2',true,'Tipo: '+getUrlVars().tipoTarjeta);
    div1.appendChild(div2);
    div2 = createDiv('level2',true,'Operador: '+getUrlVars().operador);
    div1.appendChild(div2);
    document.getElementById('container').appendChild(div1);

    div1 = createDiv('level1',true, getUrlVars().saldo);
    div1.style.paddingLeft='5px';
    document.getElementById('container').appendChild(div1);


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
