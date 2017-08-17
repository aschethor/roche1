
//Designer
var nodes = new vis.DataSet([]);

// create an array with edges
var edges = new vis.DataSet([]);

// create a network
var container = document.getElementById('mynetwork');
var data = {
	nodes: nodes,
	edges: edges
};
var add_tag_name="";
var add_tag_id=0;
var options = {
	edges:{
		smooth:false,
		color:{
			inherit: false
		}
	},
	physics:{
		enabled:false
	},
	interaction: {
		dragNodes: true,
		selectable: true
	},
	nodes:{
		color:{
			border: '#2B7CE9',
			background:'#97C2FC'
		}
	},
	manipulation: {
		enabled : false,
		addNode: function(nodeData,callback) {
		  nodeData.label = add_tag_name;
		  nodeData.id = add_tag_id;
		  document.getElementById('add_tag').placeholder = "add tag";
		  console.log(nodeData.x+" "+nodeData.y)
		  var username = getCookie('username');
		  var password = getCookie('password');
		  var id = window.location.href.split('/').slice(-1)[0];
		  connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","move_tag":{"id":"'+add_tag_id+'","x":"'+nodeData.x+'","y":"'+nodeData.y+'"}}}');
		  callback(nodeData);
		},
		addEdge: function(edgeData,callback) {
		  var username = getCookie('username');
		  var password = getCookie('password');
		  var id = window.location.href.split('/').slice(-1)[0];
		  connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","add_link":{"tag1":"'+edgeData.from+'","tag2":"'+edgeData.to+'"}}}');
		  callback(edgeData);
		},
		deleteNode: function(nodeData,callback) {
		  var username = getCookie('username');
		  var password = getCookie('password');
		  var id = window.location.href.split('/').slice(-1)[0];
		  connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","remove_tag":"'+nodeData.nodes[0]+'"}}');
		  callback(nodeData);
		},
		deleteEdge: function(edgeData,callback) {
		  var nodes = network.getConnectedNodes(edgeData.edges[0]);
		  var username = getCookie('username');
		  var password = getCookie('password');
		  var id = window.location.href.split('/').slice(-1)[0];
		  connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","remove_link":{"tag1":"'+nodes[0]+'","tag2":"'+nodes[1]+'"}}}');
		  callback(edgeData);
		}
	}
}
var network = new vis.Network(container, data, options);
network.disableEditMode();

function add_tag(){
    add_tag_name = document.getElementById('add_tag').value;
	document.getElementById('add_tag').placeholder = "click where to add tag";
    document.getElementById('add_tag').value = "";
    var username = getCookie('username');
    var password = getCookie('password');
    var id = window.location.href.split('/').slice(-1)[0];
	connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","create_tag":"'+add_tag_name+'"}}');
}

function fill_design(design){
	var i=0;
	while(design["tag"+i]!=undefined){
		nodes.add({id:design["tag"+i].id,label:design["tag"+i].name,x:design["tag"+i].x,y:design["tag"+i].y});
		i++;
	}
	i=0;
	while(design["link"+i]!=undefined){
		edges.add({from:design["link"+i].tag1,to:design["link"+i].tag2});
		i++;
	}
	var data = {
		nodes: nodes,
		edges: edges
	  };
	network = new vis.Network(container, data, options);
	network.on("dragEnd",function(obj){
		if(obj.nodes[0]!=undefined){
			var username = getCookie('username');
			var password = getCookie('password');
			var id = window.location.href.split('/').slice(-1)[0];
			connection.send('{study_change:{"username":"'+username+'","password":"'+password+'","id":"'+id+'","move_tag":{"id":"'+obj.nodes[0]+'","x":"'+network.getPositions(obj.nodes[0])[obj.nodes[0]].x+'","y":"'+network.getPositions(obj.nodes[0])[obj.nodes[0]].y+'"}}}');
		}
	});
}

