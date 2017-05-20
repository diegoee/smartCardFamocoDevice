/*globals document, window*/
(function myApp(){
  var email,tfl,img,height,cont;

  email = document.getElementById('email');
  tfl = document.getElementById('tlf');
  img = document.getElementById('img');
  cont = document.getElementById('container');

  //padding div 10px -> 25px
  height = Math.floor(window.innerHeight);

  cont.style.height = height-25+"px";

  height = Math.floor(parseInt(cont.style.height)/2);

  img.style.height = height-25+"px";

  height = Math.floor(parseInt(cont.style.height)/4);
  tfl.style.height = height-25+"px";
  email.style.height = height-25+"px";

})();
