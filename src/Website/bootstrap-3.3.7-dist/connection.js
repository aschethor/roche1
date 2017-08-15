"use strict";

var counter=0;
var connection;
connect();
function connect(){
	if((connection==undefined||connection.readyState!=1)
		&&counter<4){
		connection = new WebSocket('ws://192.168.0.113');
		setTimeout(function(){connect();},1000);
		counter++;
	}
	else if(counter==undefined||connection.readyState!=1)
		alert("keine Verbindung möglich!");
}
