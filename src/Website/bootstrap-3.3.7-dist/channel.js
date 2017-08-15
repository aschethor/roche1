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
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{channel:{"username":"'+username+'","password":"'+password+'","id":"'+id+'"}}');
}

connection.onerror = function(error){
	console.log('WebSocket Error: '+error);
}

connection.onmessage = function(msg){
	console.log('WebSocket Message: ' + msg.data);
	var ret = JSON.parse(msg.data);
	if(ret.error!=undefined){alert(ret.error);}
	else if(ret.home!=undefined){window.open('../home',"_self");}
	else if(ret.logout!=undefined){logout();}
	else if(ret.channel_name!=undefined){document.getElementById("channel_name").innerHTML = ret.channel_name;}
	else if(ret.sample!=undefined){document.getElementById("samples").innerHTML = '<a id="sample_'+ret.sample.id+'" class="list-group-item"><div style="width:50%;float: right;">'+ret.sample.value+'<div class="pull-right" onclick="remove_sample(event,'+ret.sample.id+')">X</div></div><div style="width:50%;">'+ret.sample.time+'</div></a>'+document.getElementById("samples").innerHTML;}
	else if(ret.remove_sample!=undefined){removeElementById("channel_"+ret.remove_channel);}
	else if(ret.study_id!=undefined){document.getElementById("study").setAttribute('href','../study/'+ret.study_id);}
}

function remove_sample(event,sample){
	event.stopPropagation();
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{channel_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","remove_sample":"'+sample+'"}}');
}

function create_sample(){
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	var sample_value = document.getElementById("create_sample_value").value;
	var sample_time = document.getElementById("create_sample_time").value;
	document.getElementById("create_sample_value").value = "";
	document.getElementById("create_sample_time").value = "";
	connection.send('{channel_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","create_sample":{"time":"'+sample_time+'","value":"'+sample_value+'"}}}');
}

function logout(){
	setCookie('username','',0);
	setCookie('password','',0);
	window.open('../index',"_self");
}

function escJSON(jsn){
	return jsn.replace(new RegExp(/\\/g),"\\\\").replace(new RegExp('"', 'g'),'\\"').replace(new RegExp('\n', 'g'),'<br>');
}

function unEscJSON(jsn){
	return jsn.replace(new RegExp('\\"', 'g'),'"').replace(new RegExp('<br>','g'),'\n');
}