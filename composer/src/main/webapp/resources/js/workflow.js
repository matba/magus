var force=null;

function drawWorkflow(graph){
	
	var width = $( ".flow-container" ).width(),
	    height = $( ".flow-container" ).height()-stlFMFixedSize;
	
	var color = d3.scale.category20();
	
	force = d3.layout.force()
	    .charge(-1800)
	    .linkDistance(100)
	    .size([width, height]);
	
	var svg = d3.select("#workflow")
	    .attr("width", width)
	    .attr("height", height);
	
	svg = svg.select("#maingroup");
		
		svg.selectAll("*").remove();
	
  force
      .nodes(graph.nodes)
      .links(graph.links)
      .start();

  var link = svg.selectAll(".workflowlink")
      .data(graph.links)
    .enter().append("line")
      .attr("class", "workflowlink");
  
 svg.selectAll(".workflowlink")
  .data(graph.links)
.exit().remove();
	  
  var workflowlink2 = svg.selectAll(".workflowlink2")
      .data(graph.links)
    .enter().append("line")
      .attr("class", "workflowlink2");
  
  svg.selectAll(".workflowlink2")
  .data(graph.links)
  .exit().remove();

  var node = svg.selectAll(".workflownode")
      .data(graph.nodes)
    .enter().append("ellipse")
      .attr("class", "workflownode")
      .attr("rx", 60)
	  .attr("ry", 30)
      .call(force.drag);
  svg.selectAll(".workflownode")
  .data(graph.nodes)
  .exit().remove();
	
  var actionLabel = svg.selectAll(".actiontext")
      .data(graph.nodes)
    .enter().append("text")
	.attr("class", "actiontext")
	.text(function(d) {return '<'+d.type+'>'})
	.call(force.drag);
  
  
  svg.selectAll(".actiontext")
  .data(graph.nodes)
  .exit().remove();
	
	 var operationLabel = svg.selectAll(".operationtext")
      .data(graph.nodes)
    .enter().append("text")
	.attr("class", "operationtext")
	.text(function(d) {return d.name})
	.call(force.drag);
	 
	 svg.selectAll(".operationtext")
     .data(graph.nodes)
     .exit().remove();

  node.append("title")
      .text(function(d) { return d.name; });

  force.on("tick", function() {
    link.attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.source.x +(d.target.x-d.source.x)/2; })
        .attr("y2", function(d) { return d.source.y +(d.target.y-d.source.y)/2; });
		
	workflowlink2.attr("x1", function(d) { return d.source.x +(d.target.x-d.source.x)/2; })
        .attr("y1", function(d) { return d.source.y +(d.target.y-d.source.y)/2; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; });
		
	actionLabel.attr("x", function(d) { return d.x; })
        .attr("y", function(d) { return d.y-8; });
		
	operationLabel.attr("x", function(d) { return d.x; })
        .attr("y", function(d) { return d.y+8; });

    node.attr("cx", function(d) { return d.x; })
        .attr("cy", function(d) { return d.y; });
  });
	

}