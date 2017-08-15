"use strict";

var currentgroup=-1;

var counter=0;
var connection;
connect();
function connect(){
	if((connection==undefined||connection.readyState!=1)
		&&counter<4){
		connection = new WebSocket('ws://127.0.0.1');
		setTimeout(function(){connect();},1000);
		counter++;
	}
	else if(counter==undefined||connection.readyState!=1)
		alert("keine Verbindung möglich!");
}

connection.onopen = function(){
	console.log('connection opened');
	var username = getCookie('username');
	var password = getCookie('password');
	connection.send('{home:{"username":"'+username+'","password":"'+password+'"}}');
}

connection.onerror = function(error){
	console.log('WebSocket Error: '+error);
}

connection.onmessage = function(msg){
	console.log('WebSocket Message: ' + msg.data);
	var ret = JSON.parse(msg.data);
	if(ret.error!=undefined){alert(ret.error);}
	else if(ret.home!=undefined){window.open('home',"_self");}
	else if(ret.name!=undefined){document.getElementById("greetings").innerHTML = "Hi "+ret.name+"!";}
	else if(ret.goto_study!=undefined){window.open('study/'+ret.goto_study,"_self");}
	else if(ret.study!=undefined){
		document.getElementById("studies").innerHTML += '<a href="study/'+ret.study.id+'" class="list-group-item">'+ret.study.name+'</a>';
	}else if(ret.logout!=undefined){logout();}
}

function logout(){
	setCookie('username','',0);
	setCookie('password','',0);
	window.open('index',"_self");
}

function create_study(){
	var username = getCookie('username');
	var password = getCookie('password');
	var study_name = escJSON(document.getElementById("create_study").value);
	connection.send('{create_study:{"username":"'+username+'","password":"'+password+'","study_name":"'+study_name+'"}}');
	document.getElementById("create_study").value="";
}

function escJSON(jsn){
	return jsn.replace(new RegExp(/\\/g),"\\\\").replace(new RegExp('"', 'g'),'\\"').replace(new RegExp('\n', 'g'),'<br>');
}
