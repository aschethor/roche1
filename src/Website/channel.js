﻿"use strict";

var linked_nodes=new Array();
var write_permission = false;

function onOpen(){
	console.log('connection opened');
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{channel:{"username":"'+username+'","password":"'+password+'","id":"'+id+'"}}');
}

function onError(error){
	console.log('WebSocket Error: '+error);
}

function onMessage(msg){
	console.log('WebSocket Message: ' + msg.data);
	var ret = JSON.parse(msg.data);
	if(ret.error!=undefined){alert(ret.error);}
	else if(ret.home!=undefined){window.open('../home',"_self");}
	else if(ret.logout!=undefined){logout();}
	else if(ret.channel_name!=undefined){
		document.getElementById("channel_name").innerHTML = ret.channel_name;
		document.getElementById("change_name").placeholder = ret.channel_name;}
	else if(ret.channel_unit!=undefined){
		document.getElementById("channel_unit").innerHTML = '['+ret.channel_unit+']';
		document.getElementById("change_unit").placeholder = ret.channel_unit;}
	else if(ret.channel_comment!=undefined){document.getElementById("comment").innerHTML = ret.channel_comment;}
	else if(ret.sample!=undefined){
		if(write_permission){document.getElementById("samples").innerHTML = '<a id="sample_'+ret.sample.id+'" class="list-group-item"><div style="width:30%;float: right;">'+ret.sample.comment+'<div class="pull-right" onclick="remove_sample(event,'+ret.sample.id+')">X</div></div><div style="width:20%;float: right;">'+ret.sample.value+'</div><div style="width:50%;">'+ret.sample.time+'</div></a>'+document.getElementById("samples").innerHTML;}
		else{document.getElementById("samples").innerHTML = '<a id="sample_'+ret.sample.id+'" class="list-group-item"><div style="width:50%;float: right;">'+ret.sample.value+'</div><div style="width:50%;">'+ret.sample.time+'</div></a>'+document.getElementById("samples").innerHTML;}
	}
	else if(ret.remove_sample!=undefined){removeElementById("sample_"+ret.remove_sample);}
	else if(ret.study_id!=undefined){document.getElementById("study").setAttribute('href','../study/'+ret.study_id);}
	else if(ret.design!=undefined){fill_design(ret.design);}
	else if(ret.link_node!=undefined){link_node(ret.link_node);}
	else if(ret.write_permission!=undefined){write_permission = ret.write_permission;if(write_permission==true)add_edit_elements();}
}

function add_edit_elements(){
	document.getElementById("comment_tile").innerHTML += '<button class="btn btn-default" id="edit_button" onclick="edit_comment()">edit</button>';
	document.getElementById("insert_part").innerHTML = '<h2>Insert</h2><div class="row"><div class="col-sm-6"><input type="text" id="create_sample_time" class="create_sample form-control" placeholder="time (yyyy-mm-dd hh:mm:ss.xx)"></div>'+
		'<div class="col-sm-6"><input type="text" id="create_sample_value" class="create_sample form-control" placeholder="value (xx.xx)" onkeypress="if(event.keyCode==13)create_sample()"></div>'+
		'<div class="col-sm-12"><input type="text" id="create_sample_comment" class="create_sample form-control" placeholder="comment" onkeypress="if(event.keyCode==13)create_sample()"></div></div>';
	document.getElementById("edit_part").innerHTML = '<h2>Edit</h2><div class="row"><div class="col-sm-6"><input type="text" id="change_name" class="create_sample form-control" placeholder="name" onkeypress="if(event.keyCode==13)change_name()"></div><div class="col-sm-6"><input type="text" id="change_unit" class="create_sample form-control" placeholder="unit" onkeypress="if(event.keyCode==13)change_unit()"></div></div>';
    network.setOptions({interaction: {selectable:true}});
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
	var sample_comment = document.getElementById("create_sample_comment").value;
	document.getElementById("create_sample_value").value = "";
	document.getElementById("create_sample_time").value = "";
	document.getElementById("create_sample_comment").value = "";
	connection.send('{channel_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","create_sample":{"time":"'+sample_time+'","value":"'+sample_value+'","comment":"'+sample_comment+'"}}}');
}

function change_name(){
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	var name = document.getElementById("change_name").value;
	document.getElementById("change_name").value = "";
	connection.send('{channel_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","change_name":"'+name+'"}}');
}

function change_unit(){
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	var unit = document.getElementById("change_unit").value;
	document.getElementById("change_unit").value = "";
	connection.send('{channel_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","change_unit":"'+unit+'"}}');	
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

function edit_comment(){
	var btn = document.getElementById("edit_button");
	if(btn.innerHTML=="edit"){
		var value = document.getElementById("comment").innerHTML;
		document.getElementById("comment_tile").innerHTML = '<h2>Comment</h2><textarea class="form-control" id="edit_comment"></textarea><button class="btn btn-default" id="edit_button" onclick="edit_comment()">save</button>'
		document.getElementById("edit_comment").value = unEscJSON(value);
		document.getElementById("edit_comment").style.height = (document.getElementById("edit_comment").scrollHeight+50)+"px";
	}else{
		var value = document.getElementById("edit_comment").value;
		document.getElementById("comment_tile").innerHTML = '<h2>Comment</h2><div class="well" id="comment"></div><button class="btn btn-default" id="edit_button" onclick="edit_comment()">edit</button>'
		var username = getCookie('username');
		var password = getCookie('password');
		var id = window.location.href.split('/').slice(-1)[0];
		connection.send('{channel_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","change_comment":"'+escJSON(value)+'"}}');
	}
}