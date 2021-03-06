﻿"use strict";

function onOpen(){
	console.log('connection opened');
	var username = getCookie('username');
	var password = getCookie('password');
	connection.send('{account:{"username":"'+username+'","password":"'+password+'"}}');
}

function onError(error){
	console.log('WebSocket Error: '+error);
}

function onMessage(msg){
	console.log('WebSocket Message: ' + msg.data);
	var ret = JSON.parse(msg.data);
	if(ret.error!=undefined){alert(ret.error);}
	else if(ret.home!=undefined){window.open('home',"_self");}
	else if(ret.name!=undefined){
		document.getElementById("greetings").innerHTML = "Hi "+ret.name+"!";
		document.getElementById("change_name").placeholder = ret.name;
	}else if(ret.username!=undefined){
		document.getElementById("change_username").placeholder = ret.username;
		setCookie('username',ret.username,7);
	}else if(ret.password!=undefined){
		setCookie('password',ret.password,7);
	}else if(ret.email!=undefined){document.getElementById("change_email").placeholder = ret.email;
	}else if(ret.logout!=undefined){logout();}
		
}

function change_username(){
	var username = getCookie('username');
	var password = getCookie('password');
	connection.send('{account_change:{"username":"'+username+'","password":"'+password+'","change_username":"'+escJSON(document.getElementById("change_username").value)+'"}}');
	document.getElementById("change_username").value="";
}

function change_password(){
	var username = getCookie('username');
	var password = getCookie('password');
	connection.send('{account_change:{"username":"'+username+'","password":"'+password+'","change_password":"'+escJSON(document.getElementById("change_password").value)+'"}}');
	document.getElementById("change_password").value="";
}

function change_name(){
	var username = getCookie('username');
	var password = getCookie('password');
	connection.send('{account_change:{"username":"'+username+'","password":"'+password+'","change_name":"'+escJSON(document.getElementById("change_name").value)+'"}}');
	document.getElementById("change_name").value="";
}

function change_email(){
	var username = getCookie('username');
	var password = getCookie('password');
	connection.send('{account_change:{"username":"'+username+'","password":"'+password+'","change_email":"'+escJSON(document.getElementById("change_email").value)+'"}}');
	document.getElementById("change_email").value="";
}

function delete_account(){
	var username = getCookie('username');
	var password = getCookie('password');
	connection.send('{account_change:{"username":"'+username+'","password":"'+password+'","delete_account":""}}');
}

function logout(){
	setCookie('username','',0);
	setCookie('password','',0);
	window.open('index',"_self");
}

function escJSON(jsn){
	return jsn.replace(new RegExp(/\\/g),"\\\\").replace(new RegExp('"', 'g'),'\\"').replace(new RegExp('\n', 'g'),'<br>');
}
