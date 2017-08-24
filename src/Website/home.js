"use strict";

function onOpen(){
	console.log('connection opened');
	var username = getCookie('username');
	var password = getCookie('password');
	connection.send('{home:{"username":"'+username+'","password":"'+password+'"}}');
}

function onError(error){
	console.log('WebSocket Error: '+error);
}

function onMessage(msg){
	console.log('WebSocket Message: ' + msg.data);
	var ret = JSON.parse(msg.data);
	if(ret.error!=undefined){alert(ret.error);}
	else if(ret.home!=undefined){window.open('home',"_self");}
	else if(ret.name!=undefined){document.getElementById("greetings").innerHTML = "Hi "+ret.name+"!";}
	else if(ret.goto_study!=undefined){window.open('study/'+ret.goto_study,"_self");}
	else if(ret.study!=undefined){
		document.getElementById("studies").innerHTML = '<a href="study/'+ret.study.id+'" class="list-group-item">'+ret.study.name+'</a>'+document.getElementById("studies").innerHTML;
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
