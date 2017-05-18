var spacex = 10;
var spacey = 15;
var paddingx = 10;
var paddingy = 15;
var defaultWidth = 150;
var defaultHeight = 45;
var sequenceHeaderWidth = defaultWidth-paddingx;
var sequenceHeaderHeight = 20;
var flowHeaderWidth = 70;
var flowHeaderHeight = 20;
var defaultText = "Operation Node";
var opNames = ['bpel:invoke','bpel:receive','bpel:sequence','bpel:flow','bpel:reply'];
var ordNames = ['bpel:sequence','bpel:flow'];
var flowOffsetx =10;
var flowOffsety =45;


function bpelFlow(){
	var bpelFlow={};
	bpelFlow.operationNode={};
	bpelFlow.links={};
	bpelFlow.graphPosition = function(xmlDoc){
		
		
		function repositionx(node, newx){
			if(node.p.x!=newx){
				var diff = newx-node.p.x;
				node.p.x = newx;
				$(node.c).each(function(){
					var newxc  = this.p.x+diff;
					repositionx(this,newxc);
				});
			}
		}
	
		function graphPosition(xml, inpP, l){

			var result = {p:inpP, width: defaultWidth , height: defaultHeight, type: "", level:l, text:defaultText ,svIndex :-1, c:[]}
			result.type=xml.tagName;
			if(xml.tagName=="bpel:sequence"){
				//result.type = "sequence";
				result.text =$(xml).attr('name');	
				var topspace=0;
				
				
				$(xml).children().each(function(){
					if(opNames.indexOf(this.tagName)>-1){
						var initialp = {x: inpP.x+ paddingx , y:inpP.y + paddingy+ topspace}
						var curChild = graphPosition(this, initialp, l+1);
						result.c.push(curChild);
						topspace += curChild.height+ spacey;
					}
				});
				result.width = d3.max(result.c, function(d) {  return d.width;})+paddingx+paddingx;
				result.height =paddingy*2  + topspace- spacey;
				
				$(result.c).each(function(){
						var newx  = this.p.x-paddingx+result.width/2 - this.width/2;
						repositionx(this,newx);
				});
				
			}
			if(xml.tagName=="bpel:flow"){
				//result.type = "flow";
				result.text =$(xml).attr('name');	
				var topspace=0;
				$(xml).children().each(function(){
					if(opNames.indexOf(this.tagName)>-1){
						var initialp = {x: inpP.x+ paddingx+ topspace, y:inpP.y + paddingy+10};
						var curChild = graphPosition(this, initialp,l+1);
						result.c.push(curChild);
						topspace += curChild.width+ spacex;
					}
				});
				result.height = d3.max(result.c, function(d) {  return d.height;})+paddingy+paddingy;
				result.width = topspace + paddingx*2 -spacex ;
			}
			if(ordNames.indexOf(xml.tagName) == -1){		
				result.text =$(xml).attr('operation');	
				result.type=xml.tagName;
				result.input = "";
				result.output ="";
				if( xml.tagName=="bpel:invoke"){
					var svIndex = -1;
					$.each(serviceList,function( index, value ) {
						if(value.svs.name == $(xml).attr('operation'))
							svIndex = index;
					});
					result.svIndex = svIndex;
					
					if($(xml).attr('inputVariable')!=null)
					{
						result.input = $(xml).attr('inputVariable')
					}
					if($(xml).attr('outputVariable')!=null)
					{
						result.output = $(xml).attr('outputVariable')
					}
				}
				if( xml.tagName=="bpel:receive"){
					var svIndex = -1;
					$.each(serviceList,function( index, value ) {
						if(value.svs.name == $(xml).attr('operation'))
							svIndex = index;
					});
					result.svIndex = svIndex;

					if($(xml).attr('variable')!=null)
					{
						result.output = $(xml).attr('variable')
					}
				}
				if( xml.tagName=="bpel:reply"){

					if($(xml).attr('variable')!=null)
					{
						result.input = $(xml).attr('variable')
					}
				}
				
				
				
			}
			
			$(xml).children().each(function(){
				if( this.tagName=="bpel:sources"){
					$(this).children().each(function(){
						if( this.tagName=="bpel:source"){
							var linkName = $(this).attr('linkName');
							if(bpelFlow.links[linkName]==null){
								bpelFlow.links[linkName]={st:result};
								//bpelFlow.links[linkName]={st:{x:result.p.x+result.width/2,y:result.p.y+result.height}};
								
							}
							else
							{
								bpelFlow.links[linkName].st=result;
							}
						}
					});
				}
				if( this.tagName=="bpel:targets"){
					$(this).children().each(function(){
						if( this.tagName=="bpel:target"){
							var linkName = $(this).attr('linkName');
							if(bpelFlow.links[linkName]==null){
								bpelFlow.links[linkName]={en:result};
								//bpelFlow.links[linkName]={en:{x:result.p.x+result.width/2, y:result.p.y}};
							}
							else
							{
								bpelFlow.links[linkName].en=result;
							}
						}
					});
				}
				
			});
			
			return result;
		}
		bpelFlow.operationNode={};
		bpelFlow.links={};
		var pp = {x:flowOffsetx,y:flowOffsety};
		var returnvalue =graphPosition(xmlDoc, pp,0);
		
		return returnvalue;

	}
	
	bpelFlow.getLinks =  function(){
		var v=[];
		
		for (var key in bpelFlow.links) {
		    // skip loop if the property is from prototype
			
		    if (!bpelFlow.links.hasOwnProperty(key)) continue;
		    var result1 = bpelFlow.links[key].st;
		    var result2 = bpelFlow.links[key].en;
		    v.push({st:{x:result1.p.x+result1.width/2,y:result1.p.y+result1.height},en:{x:result2.p.x+result2.width/2, y:result2.p.y}});
		    
		}
		
		return v;
	}
	
	bpelFlow.getOperationContainers =  function(){
		var v =[];
		function getOperationContainers(t){	
			v.push(t);	
			t.c.forEach(function(d){ return getOperationContainers(d); });
		}
		getOperationContainers(bpelFlow.operationNode);
		return v.sort(function(a,b){ return a.level - b.level;});
	}
	
	bpelFlow.getFlows =  function(){
		var v =[];
		function getFlows(t){	
			if(t.type=="bpel:flow"){
				v.push(t);	
			}
			t.c.forEach(function(d){ return getFlows(d); });
		}
		getFlows(bpelFlow.operationNode);
		return v.sort(function(a,b){ return a.level - b.level;});
	}
	
	bpelFlow.getSequence =  function(){
		var v =[];
		function getSequence(t){	
			if(t.type=="bpel:sequence"){
				v.push(t);	
			}
			t.c.forEach(function(d){ return getSequence(d); });
		}
		getSequence(bpelFlow.operationNode);
		return v.sort(function(a,b){ return a.level - b.level;});
	}
	
	bpelFlow.getAllChildOfSequence =  function(){
		var v =[];
		function getAllChildOfSequence(t,fatherIsSequence){	
			if(fatherIsSequence){
				v.push(t);	
			}
			if(t.type =="bpel:sequence")
				t.c.forEach(function(d){ return getAllChildOfSequence(d,true); });
			else
				t.c.forEach(function(d){ return getAllChildOfSequence(d,false); });
		}
		
		getAllChildOfSequence(bpelFlow.operationNode,false);
		return v.sort(function(a,b){ return a.level - b.level;});
	}
	
	bpelFlow.getOther =  function(){
		var v =[];
		function getOther(t){	
			if(ordNames.indexOf(t.type) == -1){
				v.push(t);	
			}
			t.c.forEach(function(d){ return getOther(d); });
		}
		getOther(bpelFlow.operationNode);
		return v.sort(function(a,b){ return a.level - b.level;});
	}
	
	bpelFlow.getNodeByTag =  function(tagName){
		var v =[];
		function getNodeByTag(t){	
			if(t.type==tagName){
				v.push(t);	
			}
			t.c.forEach(function(d){ return getNodeByTag(d); });
		}
		getNodeByTag(bpelFlow.operationNode);
		return v.sort(function(a,b){ return a.level - b.level;});
	}

	bpelFlow.draw = function(){
		
		var svg = d3.select("#bpelflow").select("#maingroup");
		
		svg.selectAll("*").remove();
		var svgW = bpelFlow.operationNode.width+60;
		var svgH = bpelFlow.operationNode.height+90;
		if($("#bpelGraphTab").width()-40 > svgW)
			svgW = $("#bpelGraphTab").width()-40;
		if($("#bpelGraphTab").height()-40 >svgH)
			svgH = $("#bpelGraphTab").height()-40;
			
		var offsetX = 0;
		var offsetY = 0;
		
		offsetX = (svgW - (bpelFlow.operationNode.width+60))/2;
		offsetY = (svgH - (bpelFlow.operationNode.height+90))/2;
		
		svg.attr('transform',function(){ return 'translate('+offsetX+','+offsetY+')';});
		
		d3.select("#bpelflow").attr("width", svgW).attr("height", svgH);
		/*svg.selectAll(".bpelnode")
		  .data(bpelFlow.getOperationContainers())
		  .attr("x", function(d){return d.p.x})
		  .attr("y", function(d){return d.p.y})
		  .attr("width", function(d){return d.width})
		  .attr("height", function(d){return d.height});
		
		bpelFlow.getOperationContainers();*/
		
		svg.selectAll(".bpelnode")
		  .data(bpelFlow.getOperationContainers())
		.enter().append("rect")
		  .attr("class", "bpelnode")
		  .attr("style", "stroke:rgb(205,205,205);stroke-width:1;shape-rendering:crispEdges;")
		  .attr("x", function(d){return d.p.x})
		  .attr("y", function(d){return d.p.y})
		  .attr("rx", 5)
		  .attr("ry", 5)
		  .attr("width", function(d){return d.width})
		  .attr("height", function(d){return d.height})
		  .attr("fill", function(d){if(d.type=="bpel:sequence") return "url(#evengrad)"; return  "white";} )
		  .on('click',function(d){return toggleServiceAvailabilityGraph(d.svIndex);})
		  .on('mouseover',handleMouseOver)
		  .on('mouseout', handleMouseOut);
		svg.selectAll(".bpelnode")
		  .data(bpelFlow.getOperationContainers()).exit().remove();
		  
		 var dbfl = svg.selectAll(".flowline")
		.data(bpelFlow.getFlows())
		  
		 dbfl
		.enter().append("line")
		.attr("class", "flowline")
		.attr("style", "stroke:rgb(112,152,224);stroke-width:2;")
		.attr("x1", function(d){return d.p.x+paddingx})
		.attr("y1", function(d){return d.p.y})
		.attr("x2", function(d){return d.p.x+d.width-paddingx})
		.attr("y2", function(d){return d.p.y})
		
		
		
		
		dbfl
		.enter().append("line")
		.attr("class", "flowline")
		.attr("style", "stroke:rgb(112,152,224);stroke-width:2;")
		.attr("x1", function(d){return d.p.x+paddingx})
		.attr("y1", function(d){return d.p.y+d.height})
		.attr("x2", function(d){return d.p.x+d.width-paddingx})
		.attr("y2", function(d){return d.p.y+d.height})
		dbfl.exit().remove();
		 
		 
		 svg.selectAll(".flowheader")
			.data(bpelFlow.getFlows())
			.enter().append("rect")
			  .attr("class", "flowheader")
			  .attr("style", "stroke:rgb(205,205,205);stroke-width:1;shape-rendering:crispEdges;")
			  .attr("x", function(d){return d.p.x+paddingx+5})
			  .attr("y", function(d){return d.p.y-flowHeaderHeight/2 })
			  .attr("rx", 5)
			  .attr("ry", 5)
			  .attr("width", function(d){return flowHeaderWidth})
			  .attr("height", function(d){return flowHeaderHeight})
			  .attr("fill", function(d){ return  "white";} ) ;
			svg.selectAll(".flowheader").data(bpelFlow.getFlows()).exit().remove();
		
		svg.selectAll(".flowheadertext")
			.data(bpelFlow.getFlows())
			.enter().append("text")
			.attr("class", "flowheadertext")
			.attr("style", "font-family: Courier New;text-anchor:left;font-size: 0.8em;")
			.attr("x", function(d){return d.p.x+paddingx+10})
			.attr("y", function(d){return d.p.y+3})
			.text("bpel:flow");
			
			//.text(function(d){return (d.text==null)?"bpel:flows":d.text});
			
			svg.selectAll(".flowheadertext")
			.data(bpelFlow.getFlows()).exit().remove();
		
		svg.selectAll(".seqheader")
		.data(bpelFlow.getSequence())
		.enter().append("rect")
		  .attr("class", "seqheader")
		  .attr("style", "stroke:rgb(205,205,205);stroke-width:1;shape-rendering:crispEdges;")
		  .attr("x", function(d){return d.p.x+d.width/2- sequenceHeaderWidth/2})
		  .attr("y", function(d){return d.p.y-sequenceHeaderHeight/2 })
		  .attr("rx", 5)
		  .attr("ry", 5)
		  .attr("width", function(d){return sequenceHeaderWidth})
		  .attr("height", function(d){return sequenceHeaderHeight})
		  .attr("fill", function(d){ return  "white";} ) ;
		svg.selectAll(".seqheader").data(bpelFlow.getSequence()).exit().remove();
		
		svg.selectAll(".seqheadertext")
		.data(bpelFlow.getSequence())
		.enter().append("text")
		.attr("class", "seqheadertext")
		.attr("style", "font-family: Courier New;text-anchor:middle;font-size: 0.8em;")
		.attr("x", function(d){return d.p.x+d.width/2})
		.attr("y", function(d){return d.p.y+3})
		.text("bpel:sequence");
		
		//.text(function(d){return (d.text==null)?"bpel:sequence":d.text});
		
		svg.selectAll(".seqheadertext")
		.data(bpelFlow.getSequence()).exit().remove();
		
		 svg.selectAll(".seqconnector")
		.data(bpelFlow.getAllChildOfSequence())
		.enter().append("line")
		.attr("class", "seqconnector")
		.attr("style", "stroke:rgb(205,205,205);stroke-width:0.8;marker-end: url(#markerArrow);")
		.attr("x1", function(d){return d.p.x+d.width/2})
		.attr("y1", function(d){return d.p.y+d.height})
		.attr("x2", function(d){return d.p.x+d.width/2})
		.attr("y2", function(d){return d.p.y+d.height+spacey-5});
		
		 svg.selectAll(".seqconnector")
			.data(bpelFlow.getAllChildOfSequence()).exit().remove();
		  
		svg.selectAll(".bpeltext")
		.data(bpelFlow.getOther())
		.enter().append("text")
		.attr("class", "bpeltext")
		.attr("style", "font-family: Arial;text-anchor:middle;font-size: 0.8em;")
		.attr("x", function(d){return d.p.x+d.width/2})
		.attr("y", function(d){return d.p.y+28})
		.text(function(d){return d.text});
		
		svg.selectAll(".bpeltext")
		.data(bpelFlow.getOther()).exit().remove();
		
	
		svg.selectAll(".bpeltype")
		.data(bpelFlow.getOther())
		.enter().append("text")
		.attr("class", "bpeltype")
		.attr("style", "text-anchor:middle;	font-size: 0.8em;font-family: Courier New;")
		.attr("x", function(d){return d.p.x+d.width/2})
		.attr("y", function(d){return d.p.y+12})
		.text(function(d){return d.type});
		
		svg.selectAll(".bpeltype")
		.data(bpelFlow.getOther()).exit().remove();
		
		svg.selectAll(".bpelinputc")
		.data(bpelFlow.getOther())
		.enter().append("text")
		.attr("class", "bpelinputc")
		.attr("style", "text-anchor:left;	font-size: 0.8em;font-family: Arial;font-weight: bold;")
		.attr("x", function(d){return d.p.x+5})
		.attr("y", function(d){return d.p.y+40})
		.text(function(d){return  (d.input=="")?"":"Input:"});
		
		svg.selectAll(".bpelinputc")
		.data(bpelFlow.getOther()).exit().remove();
		
		
		svg.selectAll(".bpeloutputc")
		.data(bpelFlow.getOther())
		.enter().append("text")
		.attr("class", "bpeloutputc")
		.attr("style", "text-anchor:left;	font-size: 0.8em;font-family: Arial;font-weight: bold;")
		.attr("x", function(d){return d.p.x+d.width/2+5})
		.attr("y", function(d){return d.p.y+40})
		.text(function(d){return  (d.output=="")?"":"Output:"});
		
		svg.selectAll(".bpeloutputc")
		.data(bpelFlow.getOther()).exit().remove();
		
		svg.selectAll(".bpelinput")
		.data(bpelFlow.getOther())
		.enter().append("text")
		.attr("class", "bpelinput")
		.attr("style", "text-anchor:left;	font-size: 0.8em;font-family: Courier New;")
		.attr("x", function(d){return d.p.x+42})
		.attr("y", function(d){return d.p.y+40})
		.text(function(d){return d.input});
		
		svg.selectAll(".bpelinput")
		.data(bpelFlow.getOther()).exit().remove();
		
		svg.selectAll(".bpeloutput")
		.data(bpelFlow.getOther())
		.enter().append("text")
		.attr("class", "bpeloutput")
		.attr("style", "text-anchor:left;	font-size: 0.8em;font-family: Courier New;")
		.attr("x", function(d){return d.p.x+d.width/2+47})
		.attr("y", function(d){return d.p.y+40})
		.text(function(d){return d.output});
		
		svg.selectAll(".bpeloutput")
		.data(bpelFlow.getOther()).exit().remove();
		
		svg.append("line")
		.attr("class", "connector")
		.attr("style", "stroke:rgb(205,205,205);stroke-width:0.8;marker-end: url(#markerArrow);")
		.attr("x1", function(d){return bpelFlow.operationNode.p.x+bpelFlow.operationNode.width/2})
		.attr("y1", function(d){return bpelFlow.operationNode.p.y-25})
		.attr("x2", function(d){return bpelFlow.operationNode.p.x+bpelFlow.operationNode.width/2})
		.attr("y2", function(d){return bpelFlow.operationNode.p.y-sequenceHeaderHeight/2-5});
		
		
		svg.append("line")
		.attr("class", "connector")
		.attr("style", "stroke:rgb(205,205,205);stroke-width:0.8;marker-end: url(#markerArrow);")
		.attr("x1", function(d){return bpelFlow.operationNode.p.x+bpelFlow.operationNode.width/2})
		.attr("y1", function(d){return  bpelFlow.operationNode.p.y+bpelFlow.operationNode.height})
		.attr("x2", function(d){return bpelFlow.operationNode.p.x+bpelFlow.operationNode.width/2})
		.attr("y2", function(d){return bpelFlow.operationNode.p.y+bpelFlow.operationNode.height+5});
		  
		  
		svg.append("circle")
		.attr("cx", bpelFlow.operationNode.p.x+bpelFlow.operationNode.width/2)
		.attr("cy", bpelFlow.operationNode.p.y-30)
		.attr("r", 10)
		.attr("fill","url(#stgrad)");
		
		svg.append("circle")
		.attr("cx", bpelFlow.operationNode.p.x+bpelFlow.operationNode.width/2)
		.attr("cy", bpelFlow.operationNode.p.y+bpelFlow.operationNode.height+20)
		.attr("r", 10)
		.attr("fill","url(#engrad)");
		
		
		svg.selectAll(".invokeimg")
		.data(bpelFlow.getNodeByTag("bpel:invoke"))
		.enter().append("image")
		.attr("class", "invokeimg")
		.attr("x", function(d){return d.p.x+5})
		.attr("y", function(d){return d.p.y+2})
		.attr("width", "16px")
		.attr("height",  "16px")
		.attr("xlink:href", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAARCAIAAABbzbuTAAABa0lEQVR4nGP5//8/AymABY+cSpU34///t9u3Eavh31+GfF8LpXKve53b8GmQL/ECOpORkQHo1p9//6V7mcoVez3q3YZdg3SBV6qvIZz79c9vIJnkZQgUfzphGxYNv/8yfv3zh4HhPyMoLMAE0CZGhigPXbFcr1eTt6Fr+PuX4dvv35juXL7txrtpGDYElpxeHrcgZF4CXCTESxlIrt129+MsDD/4F54MzjCcOuP8p1lbISLcyd4//v7ZuO3h17lb4cqgGnzyjoekG/3/8//f338+eSfAIcQwKTY3e+HkH/MRqqEavLKPhqQZ/f3zjxHoqmQDoFqgBqBP1846h6YapMEj/ZBPpM63998xPfrv919MQZYdM+1ckvYHxOmDgg8SjrDE9fcPNg1AvGeeo1P83oB4A0bG/xsWXPgPjASwzn0LnbFrYADLOcTsCko0/vv374Elbpjq0DUAAVCdXeROPErRNQDBoeXuBDUAAKWAmI2f0cgeAAAAAElFTkSuQmCC");
		
		svg.selectAll(".invokeimg")
		.data(bpelFlow.getNodeByTag("bpel:invoke"))
		.exit().remove();
		
		svg.selectAll(".receiveimg")
		.data(bpelFlow.getNodeByTag("bpel:receive"))
		.enter().append("image")
		.attr("class", "receiveimg")
		.attr("x", function(d){return d.p.x+5})
		.attr("y", function(d){return d.p.y+2})
		.attr("width", "16px")
		.attr("height",  "16px")
		.attr("xlink:href", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAAQCAIAAACUZLgLAAABi0lEQVR4nGP5//8/A+mABZmz9/zjXbcf41HtpirrbCiLrg2oJ8RJX4GHjYGB8cGXH0CSgeE/Mrlm3yUs2hj+/1XiZTdp8AUyz9RvfvLtJ8N/RrB6GPn/LxZHBlmoMQGlfzAUBFiYVPqe69z87NtvZBsZYAEB1SZZ6Imk/QfQfRkBFlrlvjc6t7z68Rsh8+8fQhtQT4aHAQOKPqBGBqCgRrnLra49737+gVjI8BfJkUA1IMTxg4OBA0gzMHBwgLlARoSHhVqez93JWz/8Amv49wfZkT8+MPyAQpgjGZAw0MOsTBD/odjGAVLI8QPmRAgFsnvFhgdf5237/PsfGxMTA5ojv8/fzpmIHCQMAR4KQN0bNjz4PHfbj7//WZgYoSH5FzUCgDqBZPmMNU1pwRzRTkD7Nux48Gfe9j///zOD9AADHkSi+Q0Rviyg8OIA2vNv+fZ///+zMkIijRFCokQAkra/zIyM/5dvh/CYQU5jQFOARZubtU35hIXoSpEV2Dth0QZMps6G8Xi0wQEAuES0fZBVH4AAAAAASUVORK5CYII=");
		
		svg.selectAll(".receiveimg")
		.data(bpelFlow.getNodeByTag("bpel:receive"))
		.exit().remove();
		
		svg.selectAll(".sequenceimg")
		.data(bpelFlow.getNodeByTag("bpel:sequence"))
		.enter().append("image")
		.attr("class", "sequenceimg")
		.attr("x", function(d){return d.p.x+d.width/2- sequenceHeaderWidth/2+5})
		.attr("y", function(d){return d.p.y-sequenceHeaderHeight/2+2 })
		.attr("width", "16px")
		.attr("height",  "16px")
		.attr("xlink:href", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAQCAIAAACgHXkXAAAAd0lEQVR4nGP5//8/A27AAsQnb7488OAFmoSDgoS5ujhI+sCNB+kuxgwMMGMYGRn+/5+55xxUmuHHh++//v5n/A+VhNA/PkANDzDQWLRpB5rhFgYaUGl1dflydXkgI7NxJpCcXp+O3WkKFhZAsnPnxVGnITsNEwAA/SZlzzuH2pAAAAAASUVORK5CYII=");
		
		svg.selectAll(".sequenceimg")
		.data(bpelFlow.getNodeByTag("bpel:sequence"))
		.exit().remove();
		
		svg.selectAll(".replyimg")
		.data(bpelFlow.getNodeByTag("bpel:reply"))
		.enter().append("image")
		.attr("class", "replyimg")
		.attr("x", function(d){return d.p.x+5})
		.attr("y", function(d){return d.p.y+2})
		.attr("width", "16px")
		.attr("height",  "16px")
		.attr("xlink:href", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAAQCAIAAAB7ptM1AAABbUlEQVR4nGP5//8/A1mABY2/9/zjXbcf49HgpirrbCiLRSdQW4mfGZDx4MsPsAAjA8N/ZHLNvkvYdQYZK6XXXQQyZjfpP/n6E6yYEYX8/xe7a9unPwlJN1g74wITE4McLztE8Pm333B7GWDhgqLTv/BkSLox0NB///7Fl5+Fiv5nWNxl8urHbyj33z90nT65x0PSTP+CFQQmGSGbGFV8amWf2Zuff4DWMvxFda1n5uHAJOPfP37BrAG5DQ7+/fnLxMjIBvQAmIOi8+/vf59efWTAAf7+/sPEyMDKBPErqp275tg7J+zzidTBrvPPP4SdfzHCdu8CJ8fY3QEJhkBz1y84j6xz7yLX73//sTIx/seqEwj2L3a1j9wRnGr678+/3UtdwR5mBMbg7///gU6FsNH9CQddtWoljadAEoyMsKBiRCaxxAoErNt38vDKSFxBBQL/cKQhN2ub8gkL8Wh0s3fCrhOYmp0N4/HZCQMAjdOajp6pOX4AAAAASUVORK5CYII=");
		
		svg.selectAll(".replyimg")
		.data(bpelFlow.getNodeByTag("bpel:reply")).exit().remove();
		
		var ll = bpelFlow.getLinks();
		//console.log(ll);
		svg.selectAll(".linkline").data(ll).enter()
		.append("line")
		.attr("class", "linkline")
		.attr("style", "stroke:rgb(200,20,20);stroke-width:1;marker-end: url(#markerArrowLink);")
		.attr("stroke-dasharray","5, 5")
		.attr("x1", function(d){return d.st.x})
		.attr("y1", function(d){return d.st.y})
		.attr("x2", function(d){return d.en.x})
		.attr("y2", function(d){return d.en.y});
		
		svg.selectAll(".linkline").data(ll).exit().remove();
		
		
		svg.selectAll(".linklinelabel").data(ll).enter()
		.append("text")
		.attr("class", "linklinelabel")
		.attr("style", "fill:rgb(200,20,20);text-anchor:middle;	font-size: 0.8em;font-family: Courier New;")
		.attr("x", function(d){return (d.st.x+d.en.x)/2 -10})
		.attr("y", function(d){return (d.st.y+d.en.y)/2 -10})
		.text("bpel:link");
		
		svg.selectAll(".linklinelabel").data(ll).exit().remove();
	}
	return bpelFlow;
}
function handleMouseOver(d, i) {  // Add interactivity

	if((d.type!='bpel:invoke')&& (d.type!='bpel:receive'))
		return;
    // Use D3 to select element, change color and size
    d3.select(this).attr({
      fill: '#ffaaaa'
    })
    .attr("style", "stroke:rgb(205,50,50);stroke-width:1;shape-rendering:crispEdges;");

    // Specify where to put label of text
    
  }

function handleMouseOut(d, i) {
    // Use D3 to select element, change color back to normal
	if((d.type!='bpel:invoke')&& (d.type!='bpel:receive'))
		return;
	
	d3.select(this).attr({
      fill: "white"
    })
    .attr("style", "stroke:rgb(205,205,205);stroke-width:1;shape-rendering:crispEdges;");

    // Select text by id and then remove
    
  }
//$.ajax({
//    type: "GET",
//    url: "flow2.xml",
//	contextType: "text/plain",
//    dataType: "text",
//    success: function(data){
//		var bf = bpelFlow();
//		xmlDoc = $.parseXML( data );
//		$xml = $( xmlDoc );
//		var x = $xml.find("[name='main']");
//		
//		bf.operationNode = bf.graphPosition(x[0]);
//		
//		
//		
//		
//		bf.draw();
//  },
//  error: function() {
//    alert("There was an error reading feature model file");
//  }
//  });