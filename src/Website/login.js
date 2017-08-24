"use strict";

function onOpen(){
	console.log('connection opened');
	document.getElementById("login_username").value = getCookie('username');
	document.getElementById("login_password").value = getCookie('password');
}

function onError(error){
	console.log('WebSocket Error: '+error);
}

function onMessage(msg){
	console.log('WebSocket Message: ' + msg.data);
	var ret = JSON.parse(msg.data);
	if(ret.error!=undefined){alert(ret.error);}
	else if(ret.home!=undefined){window.open('home',"_self")}
}

function login(){
	console.log('login: ');
	var username = escJSON(document.getElementById("login_username").value);
	var password = escJSON(document.getElementById("login_password").value);
	setCookie('username',username,7);
	setCookie('password',password,7);
	connection.send('{login:{"username":"'+username+'","password":"'+password+'"}}');
}

function escJSON(jsn){
	return jsn.replace(new RegExp(/\\/g),"\\\\").replace(new RegExp('"', 'g'),'\\"').replace(new RegExp('\n', 'g'),'<br>');
}
