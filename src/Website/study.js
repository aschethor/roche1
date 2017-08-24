"use strict";

var write_permission = false;

function onOpen(){
	console.log('connection opened');
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{study:{"username":"'+username+'","password":"'+password+'","id":"'+id+'"}}');
}

function onError(error){
	console.log('WebSocket Error: '+error);
}

function onMessage(msg){
	console.log('WebSocket Message: ' + msg.data);
	var ret = JSON.parse(msg.data);
	if(ret.error!=undefined){alert(ret.error);}
	else if(ret.home!=undefined){window.open('../home',"_self");}
	else if(ret.study_name!=undefined){document.getElementById("study_name").innerHTML = ret.study_name;document.getElementById("change_study_name").placeholder = ret.study_name;}
	else if(ret.description!=undefined){document.getElementById("description").innerHTML = ret.description;}
	else if(ret.author!=undefined){
		if(write_permission){document.getElementById("authors").innerHTML += '<a class="list-group-item" id="author_'+ret.author.id+'" onclick="window.open('+"'mailto:"+ret.author.email+"', '_self')"+'">'+ret.author.name+'<div class="pull-right" onclick="event.stopPropagation();remove_author('+ret.author.id+')">X</div></a>';}
		else{document.getElementById("authors").innerHTML += '<a class="list-group-item" id="author_'+ret.author.id+'" onclick="window.open('+"'mailto:"+ret.author.email+"', '_self')"+'">'+ret.author.name+'</a>';}
	}
	else if(ret.channel!=undefined){
		if(write_permission){document.getElementById("channels").innerHTML += '<a onclick="goto_channel(event,'+ret.channel.id+')" id="channel_'+ret.channel.id+'" class="list-group-item">'+ret.channel.name+' ['+ret.channel.unit+']<div class="pull-right" onclick="remove_channel(event,'+ret.channel.id+')">X</div></a>';}
		else{document.getElementById("channels").innerHTML += '<a onclick="goto_channel(event,'+ret.channel.id+')" id="channel_'+ret.channel.id+'" class="list-group-item">'+ret.channel.name+' ['+ret.channel.unit+']</a>';}
	}
	else if(ret.logout!=undefined){logout();}
	else if(ret.remove_author!=undefined){removeElementById("author_"+ret.remove_author);}
	else if(ret.remove_channel!=undefined){removeElementById("channel_"+ret.remove_channel);}
	else if(ret.add_tag!=undefined){add_tag_id = ret.add_tag.id;add_tag_name = ret.add_tag.name;network.addNodeMode();}
	else if(ret.design!=undefined){fill_design(ret.design);}
	else if(ret.similar_study!=undefined){document.getElementById("similar_studies").innerHTML += '<a href="../study/'+ret.similar_study.id+'" class="list-group-item">'+ret.similar_study.name+'</a>';}
	else if(ret.similar_channels!=undefined){add_similar_channels(ret.similar_channels);}
	else if(ret.goto_study!=undefined){window.open('../study/'+ret.goto_study,"_blank");}
	else if(ret.write_permission!=undefined){write_permission = ret.write_permission;if(write_permission==true)add_edit_elements();}
}

function add_edit_elements(){
	document.getElementById("description_tile").innerHTML += '<button class="btn btn-default" id="edit_button" onclick="edit_description()">edit</button>';
	document.getElementById("edit_design_row").innerHTML = '<div class="col-sm-6"><input type="text" id="add_tag" class="form-control create_sample" placeholder="add tag" onkeypress="if(event.keyCode==13)add_tag()"></div><div class="col-sm-3"><button class="btn btn-default create_sample"style = "width: 100%" type="button" onclick="network.addEdgeMode()">add link</button></div><div class="col-sm-3"><button class="btn btn-default"style = "width: 100%" type="button" onclick="network.deleteSelected()">delete</button></div>';
	document.getElementById("edit_row").innerHTML = '<div class="col-sm-6"><input type="text" id="change_study_name" class="form-control create_sample" placeholder="" onkeypress="if(event.keyCode==13)change_study_name()"></div><div class="col-sm-6"><input type="text" id="copy_study" class="form-control create_sample" placeholder="copy study" onkeypress="if(event.keyCode==13)copy_study()"></div><div class="col-sm-6"><button class="btn btn-default" type="button" onclick="delete_study()">delete study</button></div>';
	document.getElementById("authors_tile").innerHTML += '<input type="text" id="add_author" class="form-control" placeholder="add author (username)" onkeypress="if(event.keyCode==13)add_author()">';
	network.setOptions({interaction: {dragNodes:true,selectable:true}});
}

function add_similar_channels(msg){
	var addHTML = '<a class="list-group-item" onclick="goto_study(event,'+msg.s_id+')">.<div style = "float:left">'+msg.s_name+':</div>';
	if(msg["c_0"]!=undefined)addHTML+='<div onclick="goto_channel(event,'+msg.c_0.id+')" class="channel_link">'+msg.c_0.name+' ['+msg.c_0.unit+']</div>';
	var i=1;
	while(msg["c_"+i]!=undefined){
		addHTML+='<div onclick="goto_channel(event,'+msg["c_"+i].id+')" class="channel_link">, '+msg["c_"+i].name+' ['+msg["c_"+i].unit+']</div>';
		i++;
	}
	i=0;
	addHTML+='</a>';
	document.getElementById("similar_channels").innerHTML += addHTML;
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

function goto_channel(event,channel){
	event.stopPropagation();
	window.open('../channel/'+channel,"_blank");
}

function goto_study(event,study){
	event.stopPropagation();
	window.open('../study/'+study,"_blank");
}

function create_channel(){
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	var channel = document.getElementById("create_channel").value;
	document.getElementById("create_channel").value = "";
	connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","create_channel":"'+channel+'"}}');
}

function change_study_name(){
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","change_study_name":"'+document.getElementById("change_study_name").value+'"}}');
	document.getElementById("change_study_name").value="";
}

function copy_study(){
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","copy_study":"'+document.getElementById("copy_study").value+'"}}');
	document.getElementById("copy_study").value = "";
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
	return jsn.replace(new RegExp(/\\/g),"\\\\").replace(new RegExp('"', 'g'),'\\"').replace(new RegExp('\n', 'g'),'<div></div>');
}

function unEscJSON(jsn){
	return jsn.replace(new RegExp('\\"', 'g'),'"').replace(new RegExp('<div></div>','g'),'\n');
}

function search_similar_studies(){
	document.getElementById("similar_studies").innerHTML = "";
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{query:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","similar_studies":""}}');
}

function search_similar_channels(){
	document.getElementById("similar_channels").innerHTML = "";
	var username = getCookie('username');
	var password = getCookie('password');
	var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{query:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","similar_channels":""}}');
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
