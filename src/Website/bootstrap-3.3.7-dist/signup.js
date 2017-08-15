"use strict";

connection.onopen = function(){
	console.log('connection opened');
}

connection.onerror = function(error){
	console.log('WebSocket Error: '+error);
}

connection.onmessage = function(msg){
	console.log('WebSocket Message: ' + msg.data);
	var ret = JSON.parse(msg.data);
	if(ret.error!=undefined){alert(ret.error);}
	else if(ret.home!=undefined){window.open('home',"_self")}
}

function signup(){
	console.log('signup: ');
	var username = escJSON(document.getElementById("signup_username").value);
	var password = escJSON(document.getElementById("signup_password").value);
	var name = escJSON(document.getElementById("signup_name").value);
	var email = escJSON(document.getElementById("signup_email").value);
	setCookie('username',username,7);
	setCookie('password',password,7);
	connection.send('{signup:{"username":"'+username+'","password":"'+password+'","name":"'+name+'","email":"'+email+'"}}');
}

function escJSON(jsn){
	return jsn.replace(new RegExp(/\\/g),"\\\\").replace(new RegExp('"', 'g'),'\\"').replace(new RegExp('\n', 'g'),'<br>');
}
