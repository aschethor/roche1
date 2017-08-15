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
	connection.send('{study:{"username":"'+username+'","password":"'+password+'","id":"'+id+'"}}');
}

connection.onerror = function(error){
	console.log('WebSocket Error: '+error);
}

connection.onmessage = function(msg){
	console.log('WebSocket Message: ' + msg.data);
	var ret = JSON.parse(msg.data);
	if(ret.error!=undefined){alert(ret.error);}
	else if(ret.home!=undefined){window.open('../home',"_self");}
	else if(ret.study_name!=undefined){document.getElementById("study_name").innerHTML = ret.study_name;}
	else if(ret.description!=undefined){document.getElementById("description").innerHTML = ret.description;}
	else if(ret.author!=undefined){document.getElementById("authors").innerHTML += '<a class="list-group-item" id="author_'+ret.author.id+'">'+ret.author.name+'<div class="pull-right" onclick="remove_author('+ret.author.id+')">X</div></a>';}
	else if(ret.channel!=undefined){document.getElementById("channels").innerHTML += '<a onclick="goto_channel('+ret.channel.id+')" id="channel_'+ret.channel.id+'" class="list-group-item">'+ret.channel.name+'<div class="pull-right" onclick="remove_channel(event,'+ret.channel.id+')">X</div></a>';}
	else if(ret.logout!=undefined){logout();}
	else if(ret.remove_author!=undefined){removeElementById("author_"+ret.remove_author);}
	else if(ret.remove_channel!=undefined){removeElementById("channel_"+ret.remove_channel);}
}

function add_author(){
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	var author = document.getElementById("add_author").value;
	document.getElementById("add_author").value = "";
	connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","add_author":"'+author+'"}}');
}

function remove_author(author){
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","remove_author":"'+author+'"}}');
}

function remove_channel(event,channel){
	event.stopPropagation();
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","remove_channel":"'+channel+'"}}');
}

function goto_channel(channel){
	window.open('../channel/'+channel,"_self");
}

function create_channel(){
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	var channel = document.getElementById("create_channel").value;
	document.getElementById("create_channel").value = "";
	connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","create_channel":"'+channel+'"}}');
}

function delete_study(){
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","delete_study":""}}');
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

function edit_description(){
	var btn = document.getElementById("edit_button");
	if(btn.innerHTML=="edit"){
		var value = document.getElementById("description").innerHTML;
		document.getElementById("description_tile").innerHTML = '<h2>Description</h2><textarea class="form-control" id="edit_description"></textarea><button class="btn btn-default" id="edit_button" onclick="edit_description()">save</button>'
		document.getElementById("edit_description").value = unEscJSON(value);
		document.getElementById("edit_description").style.height = (document.getElementById("edit_description").scrollHeight+50)+"px";
	}else{
		var value = document.getElementById("edit_description").value;
		document.getElementById("description_tile").innerHTML = '<h2>Description</h2><div class="well" id="description"></div><button class="btn btn-default" id="edit_button" onclick="edit_description()">edit</button>'
		var username = getCookie('username');
		var password = getCookie('password');
		var id = window.location.href.split('/').slice(-1)[0];
		connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","change_description":"'+escJSON(value)+'"}}');
	}
}
