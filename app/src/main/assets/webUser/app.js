/*globals document, window*/
(function myApp(){
  var writer,height,cont;

  writer = document.getElementById('writer');
  cont = document.getElementById('container');

  //padding div 10px -> 25px
  height = Math.floor(window.innerHeight);

  cont.style.height = height-25+'px';

  height = Math.floor(parseInt(cont.style.height)/2);

  writer.style.height = height-25+'px';
  writer.innerHTML = 'Hola';

})();
