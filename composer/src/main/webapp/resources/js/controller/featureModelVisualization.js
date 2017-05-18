

function featureModelVis(md,divid,tabid, svgid,nm){
	var vRad=13, fRecWidth=75,fRecHeight=30, optionalCircleRad=3, altCurveRad = 25;
	
	
	var featureModelVis={cx:0, cy:0, w:80, h:100};
	featureModelVis.name = nm;
	featureModelVis.svgW =800;
	featureModelVis.svgH =460;
	featureModelVis.offsetX = featureModelVis.svgW/2;
	featureModelVis.offsetY = 40;
	featureModelVis.vis={v:0, l:'?', uuid:'', optional: false, orgroup:false, altchilds: false, selected:false, p:{x:featureModelVis.cx, y:featureModelVis.cy},c:[]};	
	featureModelVis.size=1;
	featureModelVis.configurationMode = md;
	featureModelVis.tabId = tabid;
	featureModelVis.divId = divid;
	featureModelVis.svgId =svgid;
	featureModelVis.selFeatures =null;
	featureModelVis.reqFeatures =null;
	featureModelVis.formatter = function(inp){
		if(inp==null)
			return ["New","Feature"];
		var str = inp.trim();
		var splno =14;
		var lastspace=-1;
		var bLastSpace = 0;
		var lastcnt =0;
		var result = [];
		for(i=0; i< str.length; i++,lastcnt++ ){
			if(str.charAt(i)==' '){
				lastspace = i;
			}
			if(lastcnt == splno){
				if(lastspace!=-1){
					result.push( str.substr(bLastSpace, lastspace));
					bLastSpace = lastspace+1;
					lastcnt=0;
					i=lastspace;
					lastspace=-1;
				}else{
					result.push( str.substr(bLastSpace, i+1));
					str = str.insertAt(i, "\n");
					bLastSpace = i+1;
					lastcnt=0;
					lastspace=-1;

				}
			}
				
		}
		if(bLastSpace != str.length)
		{
			result.push( str.substr(bLastSpace, str.length));
		}
		return result;
	}

	featureModelVis.getVertices =  function(){
		var v =[];
		function getVertices(t,f){	
			v.push({v:t.v, l:t.l, p:t.p, f:f, optional:t.optional, selected: t.selected,uuid:t.uuid});	
			t.c.forEach(function(d){ return getVertices(d,{v:t.v, p:t.p}); });
		}
		getVertices(featureModelVis.vis,{});
		return v.sort(function(a,b){ return a.v - b.v;});
	}
	
	
	featureModelVis.getVerticesExceptRoot =  function(){
		var v =[];
		function getVerticesExceptRoot(t,f, include){
			if(include)
				v.push({v:t.v, l:t.l, p:t.p, f:f, optional:t.optional,selected:t.selected});	
			t.c.forEach(function(d){ return getVerticesExceptRoot(d,{v:t.v, p:t.p},true); });
		}
		getVerticesExceptRoot(featureModelVis.vis,{},false);
		return v.sort(function(a,b){ return a.v - b.v;});
	}
	
	featureModelVis.getVerticesExceptRootAndGroups =  function(){
		var v =[];
		function getVerticesExceptRootAndGroupsRec(t,f, include){
			if(include)
				v.push({v:t.v, l:t.l, p:t.p, f:f, optional:t.optional,selected:t.selected});	
			t.c.forEach(function(d){ 
				if(t.altchilds||t.orgroup)
					return getVerticesExceptRootAndGroupsRec(d,{v:t.v, p:t.p},false);
				else
					return getVerticesExceptRootAndGroupsRec(d,{v:t.v, p:t.p},true); });
		}
		getVerticesExceptRootAndGroupsRec(featureModelVis.vis,{},false);
		return v.sort(function(a,b){ return a.v - b.v;});
	}
	
	featureModelVis.getAlternativeFeatures =  function(){
		var v =[];
		function getAlternativeFeatures(t,f){	
			var st = {x:0, y:0};
			var en = {x:0, y:0};
			if(t.c.length>0){
				var mstp = {x: t.p.x , y: t.p.y + fRecHeight/2 };
				var maxX = d3.max(t.c, function(d) {  return d.p.x;});
				var minX = d3.min(t.c, function(d) {  return d.p.x;});
				var ay = t.c[0].p.y;
				var slope1 = (ay-mstp.y)/(minX-mstp.x);
				var distance = Math.sqrt( (ay-mstp.y)*(ay-mstp.y) + (minX-mstp.x)*(minX-mstp.x) );
				st.x = mstp.x+  (altCurveRad/distance)* (minX-mstp.x) ;
				st.y = mstp.y+  (altCurveRad/distance)* (ay-mstp.y) ;
				var slope2 = (ay-mstp.y)/(maxX-mstp.x);
				var distance = Math.sqrt( (ay-mstp.y)*(ay-mstp.y) + (maxX-mstp.x)*(maxX-mstp.x) );
				en.x = mstp.x+  (altCurveRad/distance)* (maxX-mstp.x) ;
				en.y = mstp.y+  (altCurveRad/distance)* (ay-mstp.y) ;
				
				
				
			}
			if(t.altchilds)
				v.push({v:t.v, l:t.l, p:t.p, f:f, curveSt: st,curveEn: en});	
			t.c.forEach(function(d){ return getAlternativeFeatures(d,{v:t.v, p:t.p}); });
		}
		getAlternativeFeatures(featureModelVis.vis,{});
		return v.sort(function(a,b){ return a.v - b.v;});
	}
	
	
	featureModelVis.getOrFeatures =  function(){
		var v =[];
		function getOrFeatures(t,f){	
			var st = {x:0, y:0};
			var en = {x:0, y:0};
			var piecenter  = {x:0, y:0};
			if(t.c.length>0){
				var mstp = {x: t.p.x , y: t.p.y + fRecHeight/2 };
				var maxX = d3.max(t.c, function(d) {  return d.p.x;});
				var minX = d3.min(t.c, function(d) {  return d.p.x;});
				var ay = t.c[0].p.y;
				var slope1 = (ay-mstp.y)/(minX-mstp.x);
				var distance = Math.sqrt( (ay-mstp.y)*(ay-mstp.y) + (minX-mstp.x)*(minX-mstp.x) );
				st.x = mstp.x+  (altCurveRad/distance)* (minX-mstp.x) ;
				st.y = mstp.y+  (altCurveRad/distance)* (ay-mstp.y) ;
				var slope2 = (ay-mstp.y)/(maxX-mstp.x);
				var distance = Math.sqrt( (ay-mstp.y)*(ay-mstp.y) + (maxX-mstp.x)*(maxX-mstp.x) );
				en.x = mstp.x+  (altCurveRad/distance)* (maxX-mstp.x) ;
				en.y = mstp.y+  (altCurveRad/distance)* (ay-mstp.y) ;
				piecenter.x = t.p.x;
				piecenter.y = t.p.y+ fRecHeight/2;
				
				
			}
			if(t.orgroup)
				v.push({v:t.v, l:t.l, p:t.p, f:f, curveSt: st,curveEn: en, piecenter : piecenter});	
			t.c.forEach(function(d){ return getOrFeatures(d,{v:t.v, p:t.p}); });
		}
		getOrFeatures(featureModelVis.vis,{});
		return v.sort(function(a,b){ return a.v - b.v;});
	}
	
	featureModelVis.getOptionalFeatures =  function(){
		var v =[];
		function getOptionalFeatures(t,f){	
			if(t.optional)
				v.push({v:t.v, l:t.l, p:t.p, f:f});	
			t.c.forEach(function(d){ return getOptionalFeatures(d,{v:t.v, p:t.p}); });
		}
		getOptionalFeatures(featureModelVis.vis,{});
		return v.sort(function(a,b){ return a.v - b.v;});
	}
	
	featureModelVis.getVertices2 =  function(){
		var v =[];
		function getVertices2(t,f){	
			var fstrArray = featureModelVis.formatter(t.l);
			for( i=0; i<fstrArray.length; i++){
				v.push({v:t.v, l:fstrArray[i], n:i , p:t.p, f:f, selected:t.selected,uuid:t.uuid});	
			}
			t.c.forEach(function(d){ return getVertices2(d,{v:t.v, p:t.p}); });
		}
		getVertices2(featureModelVis.vis,{});
		return v.sort(function(a,b){ return a.v - b.v;});
	}

	featureModelVis.getEdges =  function(){
		var e =[];
		function getEdges(_){
			_.c.forEach(function(d){ e.push({v1:_.v, l1:_.l, p1:_.p, v2:d.v, l2:d.l, p2:d.p, selected:d.selected});});
			_.c.forEach(getEdges);
		}
		getEdges(featureModelVis.vis);
		return e.sort(function(a,b){ return a.v2 - b.v2;});	
	}

	featureModelVis.addLeaf = function(_,text,opt,alternative,orgroup,uuid){
		function addLeaf(t){
			var no = -1;
			if(t.v==_){
				no = featureModelVis.size++; 
				t.c.push({v:no, l:text,optional:opt, altchilds: alternative,orgroup:orgroup,uuid:uuid , selected:false, p:{},c:[]}); 
				return no; 
			}
			else{
				//console.log(t);
				$.each(t.c,function( index, value ) {
					var currv = addLeaf(value)
					if(currv!=-1)
						no =currv;
				});
				
				
			}
			return no;
		}
		var returnValue = -1;
		returnValue = addLeaf(featureModelVis.vis);
		
		if(uuid == null)
			uuid= utility.guid();

		featureModelVis.reposition(featureModelVis.vis);
		featureModelVis.redraw();
		return returnValue;
	}
	
	
	featureModelVis.updateFeature = function(_,alternative,orgroup){
		function updateFeature(t){
			
			if(t.v==_){ t.altchilds=alternative;t.orgroup =orgroup;  return ; }
			t.c.forEach(updateFeature);
			
		}
		
		updateFeature(featureModelVis.vis);

	}
	
	featureModelVis.removeFeature = function(_,alternative,orgroup){
		function removeFeature(t){
			
			t.c.forEach(function(d){ if(d.v==_){t.c.splice(t.c.indexOf(d),1); return false; } else { removeFeature(d); } } )
			
			
		}
		
		removeFeature(featureModelVis.vis);
		
		featureModelVis.reposition(featureModelVis.vis);
		featureModelVis.redraw();
	}
	
	featureModelVis.resetSelection = function(){
		function resetSelection(t){
			t.selected = false;
			t.c.forEach(function(d){ resetSelection(d); }  );
			
			
		}
		
		resetSelection(featureModelVis.vis);
		
		
		featureModelVis.redraw();
	}
	
	featureModelVis.selectionChanged = function(_){
		function changeSelected(t,fno,newState){
			if(t.v==fno)
			{
				t.selected = newState;
			}
			else{
				$.each(t.c,function( index, value ) {
					changeSelected(value,fno,newState)
				});
			}

		}
		function getConnectedFather(t,curfno){
			
			var returnVal = -1;
			
			if(t.v==curfno)
				return -1;
			
			for(var cnt=0; cnt< t.c.length;cnt++)
			{
				var d = t.c[cnt];
				if(d.v==curfno){
					returnVal =  t.v; 
				} 
				else 
				{
					var result = getConnectedFather(d,curfno);
					if(result!=-1)
						returnVal = result;
				} 
			
			}
			return returnVal;
			
		}
		function getConnectedFatherEXOP(t,curfno){
			
			var returnVal = -1;
			
			if(t.v==curfno)
				return -1;
			
			for(var cnt=0; cnt< t.c.length;cnt++)
			{
				var d = t.c[cnt];
				if(d.v==curfno){
					if((d.optional==false )&&(!t.orgroup)&&(!t.altchilds))
						returnVal =  t.v; 
				} 
				else 
				{
					var result = getConnectedFatherEXOP(d,curfno);
					if(result!=-1)
						returnVal = result;
				} 
			
			}
			return returnVal;
			
		}
		function getSelectionStatus(t,curfno){
			
			var returnVal = false;
			
			if(t.v==curfno)
			{
				returnVal =  t.selected;
			}
			
			for(var cnt=0; cnt< t.c.length;cnt++)
			{
				var result =  getSelectionStatus(t.c[cnt],curfno);
				if(result==true)
					returnVal =result;
			}
			return returnVal;
			
		}
		function getFeature(t,curfno){
			
			var returnVal = null;
			
			if(t.v==curfno)
			{
				returnVal =  t;
			}
			
			for(var cnt=0; cnt< t.c.length;cnt++)
			{
				var result =  getFeature(t.c[cnt],curfno);
				if(result!=null)
					returnVal =result;
			}
			return returnVal;
			
		}
		var curFeature = getFeature(featureModelVis.vis,_)
		if(curFeature.selected)
		{
			var curfno = _;
			var curfatherno = getConnectedFather(featureModelVis.vis,curfno);
			
			if(curfatherno !=-1){
				var fatherFeature = getFeature(featureModelVis.vis,curfatherno)
				if(!fatherFeature.selected){
					changeSelected(featureModelVis.vis, curfatherno,true);
					featureModelVis.selectionChanged(curfatherno);
				
				}
				if(fatherFeature.altchilds){
					for(var cnt=0; cnt< fatherFeature.c.length;cnt++)
					{
						var chFeature = getFeature(featureModelVis.vis,fatherFeature.c[cnt].v)
						if(chFeature.selected && (chFeature.v !=_)){
							changeSelected(featureModelVis.vis, chFeature.v,false);
							featureModelVis.selectionChanged(chFeature.v);
						}
					}
				}
			}
			if(!curFeature.altchilds && !curFeature.orgroup ){
				for(var cnt=0; cnt< curFeature.c.length;cnt++)
				{
					var chFeature = getFeature(featureModelVis.vis,curFeature.c[cnt].v)
					if(!chFeature.optional){
						changeSelected(featureModelVis.vis, curFeature.c[cnt].v,true);
						featureModelVis.selectionChanged(curFeature.c[cnt].v);
					}
				}
			}
			
			
		}
		else
		{
			var curfno = _;
			var curfatherno = getConnectedFatherEXOP(featureModelVis.vis,curfno);
			if(curfatherno !=-1){
				
				if(getSelectionStatus(featureModelVis.vis,curfatherno)){
					changeSelected(featureModelVis.vis, curfatherno,false);
					featureModelVis.selectionChanged(curfatherno);
				
				}
				
			}
			
				for(var cnt=0; cnt< curFeature.c.length;cnt++)
				{
					var chFeature = getFeature(featureModelVis.vis,curFeature.c[cnt].v)
					
					changeSelected(featureModelVis.vis, curFeature.c[cnt].v,false);
					featureModelVis.selectionChanged(curFeature.c[cnt].v);
					
				}
			
		}
	}
	
	
	featureModelVis.toggleSelection = function(_){
		function toggleSelection(t,fno){
			if(t.v==fno)
			{
				t.selected = !t.selected;
			}
			else{
				$.each(t.c,function( index, value ) {
					toggleSelection(value,fno)
				});
			}

		}
		
		toggleSelection(featureModelVis.vis,_);
		
		featureModelVis.selectionChanged(_)
		
		
		featureModelVis.redraw();
	}
	
	featureModelVis.getSelectedFeatureListUUID= function(){
		function getSelectedFeatureListUUID(t){
			var returnVal =[];
			if(t.selected)
			{
				returnVal.push(t.uuid);
			}
			
			$.each(t.c,function( index, value ) {
				returnVal = returnVal.concat(getSelectedFeatureListUUID(value)); 
				
			});
			
			
			return returnVal;

		}
		
		return getSelectedFeatureListUUID(featureModelVis.vis);
		
	}
	
	featureModelVis.getLeafCount = function(_){
		if(_.c.length ==0) return 1;
		else return _.c.map(featureModelVis.getLeafCount).reduce(function(a,b){ return a+b;});
	}

	featureModelVis.reposition = function(v){
		var lC = featureModelVis.getLeafCount(v), left=v.p.x - featureModelVis.w*(lC-1)/2;
		v.c.forEach(function(d){
			var w =featureModelVis.w*featureModelVis.getLeafCount(d); 
			left+=w; 
			d.p = {x:left-(w+featureModelVis.w)/2, y:v.p.y+featureModelVis.h};
			featureModelVis.reposition(d);
		});		
	}	

	
	featureModelVis.redraw = function(){
		
		
		var maxX = d3.max(featureModelVis.getVertices(), function(d) {  return d.p.x;});
		var minX = d3.min(featureModelVis.getVertices(), function(d) {  return d.p.x;});
		var maxY = d3.max(featureModelVis.getVertices(), function(d) {  return d.p.y;});
		var minY = d3.min(featureModelVis.getVertices(), function(d) {  return d.p.y;});
		//console.log(featureModelVis.divId);
		if( maxX-minX+fRecHeight+50 < $("#"+featureModelVis.tabId).width())
			featureModelVis.svgW = $("#"+featureModelVis.tabId).width();
		else
			featureModelVis.svgW = maxX-minX+fRecWidth+50;
		
		if( maxY-minY+fRecHeight+50 < $("#"+featureModelVis.tabId).height()-50)
			featureModelVis.svgH = $("#"+featureModelVis.tabId).height()-50;
		else
			featureModelVis.svgH = maxY-minY+fRecHeight+50;
		
		
		featureModelVis.offsetX = featureModelVis.svgW/2;
		featureModelVis.offsetY = (featureModelVis.svgH- (maxY-minY+fRecHeight))/2;
		
		d3.select("#"+featureModelVis.divId).style("width", featureModelVis.svgW+"px").style("height", featureModelVis.svgH+"px");
		d3.select("#"+featureModelVis.svgId).attr("width", featureModelVis.svgW).attr("height", featureModelVis.svgH);
		d3.select("#featureModelVisgrp_"+featureModelVis.svgId).attr('transform',function(){ return 'translate('+featureModelVis.offsetX+','+featureModelVis.offsetY+')';});
		
		var edges = d3.select("#g_lines_"+featureModelVis.svgId).selectAll('line').data(featureModelVis.getEdges());

		edges.transition().duration(500)
			.attr('x1',function(d){ return d.p1.x;}).attr('y1',function(d){ return d.p1.y+(fRecHeight/2);})
			.attr('x2',function(d){ return d.p2.x;}).attr('y2',function(d){ return d.p2.y-(fRecHeight/2);});


		var newEdges = edges.enter().append('line');
		newEdges.attr('class','fmline')
			.attr("style","stroke:grey;stroke-width:1px;")
			.attr('x1',function(d){ return d.p1.x;}).attr('y1',function(d){ return d.p1.y+(fRecHeight/2);})
			.attr('x2',function(d){ return d.p1.x;}).attr('y2',function(d){ return d.p1.y-(fRecHeight/2);})
			.transition().duration(500)
			.attr('x2',function(d){ return d.p2.x;}).attr('y2',function(d){ return d.p2.y-(fRecHeight/2);});
			
		if(featureModelVis.configurationMode==1){
			edges.classed("notselected",function(d){return !d.selected;} );
			newEdges.classed("notselected",function(d){return !d.selected;} );
			edges.classed("selected",function(d){return d.selected;} );
			newEdges.classed("selected",function(d){return d.selected;} );
		}
		
		edges.exit().remove();
		var featueRect = d3.select("#g_rect_"+featureModelVis.svgId).selectAll('rect').data(featureModelVis.getVertices());

		featueRect.transition().duration(500).attr('x',function(d){ return d.p.x-(fRecWidth/2);}).attr('y',function(d){ return d.p.y-(fRecHeight/2);});
		
		var newFeatureRect = featueRect.enter().append('rect');
		newFeatureRect.attr('x',function(d){ return d.f.p.x-(fRecWidth/2);}).attr('y',function(d){ return d.f.p.y-(fRecHeight/2);})
			.attr('width',function(){ return fRecWidth;}).attr('height',function(){ return fRecHeight;}).attr('class','incRect').attr('style','stroke:grey;shape-rendering:crispEdges;').attr('fill','white')
			
			.transition().duration(500)
			.attr('x',function(d){ return d.p.x-(fRecWidth/2);}).attr('y',function(d){ return d.p.y-(fRecHeight/2);});
		if(featureModelVis.configurationMode==1){
			newFeatureRect.classed("notselected",function(d){return !d.selected;} );
			featueRect.classed("notselected",function(d){return !d.selected;} );
			newFeatureRect.classed("selected",function(d){return d.selected;} );
			featueRect.classed("selected",function(d){return d.selected;} );
			newFeatureRect.on('click',function(d){return featureModelVis.toggleSelection(d.v);})
		}
		if(featureModelVis.configurationMode==0){
			newFeatureRect.on('click',function(d){return featureModelVis.showAnnotation(d.l,d.p,d.v,d.uuid);})
		}
		
		featueRect.exit().remove();
		
			
		

		var altCurv = d3.select("#g_altcurv_"+featureModelVis.svgId).selectAll('path').data(featureModelVis.getAlternativeFeatures());

		altCurv.transition().duration(500).attr('d',function(d){ return "M"+d.curveSt.x+" "+d.curveSt.y + " A"+altCurveRad+" "
		+altCurveRad+" 0 0 0 "+d.curveEn.x+" "+d.curveSt.y });
		
		
		altCurv.enter().append('path').attr('d',function(d){ return "M"+d.curveSt.x+" "+d.curveSt.y + " A"+altCurveRad+" "
		+altCurveRad+" 0 0 0 "+d.curveEn.x+" "+d.curveSt.y })
			.attr('class','fmaltcurv').attr('style','stroke:grey;  stroke-width:1px;  fill:none;');
		
		
		var altCurv = d3.select("#g_orcurv_"+featureModelVis.svgId).selectAll('path').data(featureModelVis.getOrFeatures());

		altCurv.transition().duration(500).attr('d',function(d){ return "M"+d.piecenter.x+" "+d.piecenter.y  + " L"+d.curveSt.x+" "+d.curveSt.y + " A"+altCurveRad+" "
		+altCurveRad+" 0 0 0 "+d.curveEn.x+" "+d.curveSt.y +" Z"});
		
		
		altCurv.enter().append('path').attr('d',function(d){ return  "M"+d.piecenter.x+" "+d.piecenter.y  + " L"+d.curveSt.x+" "+d.curveSt.y + " A"+altCurveRad+" "
		+altCurveRad+" 0 0 0 "+d.curveEn.x+" "+d.curveSt.y +" Z"})
			.attr('class','fmorcurv').attr('style','stroke:grey;  stroke-width:1px;  fill:grey;');
		
		
		altCurv.exit().remove();
		
		var optionalCirc = d3.select("#g_optcirc_"+featureModelVis.svgId).selectAll('circle').data(featureModelVis.getVerticesExceptRootAndGroups());

		optionalCirc.transition().duration(500).attr('cx',function(d){ return d.p.x;}).attr('cy',function(d){ return d.p.y-(fRecHeight/2);});
		
		var newOptionalCirc =optionalCirc.enter().append('circle');
		
		newOptionalCirc.attr('cx',function(d){ return d.f.p.x;}).attr('cy',function(d){ return d.f.p.y-(fRecHeight/2);})
			.attr('class','fmoptionalcirc').attr('style','stroke:grey;stroke-width:1px;').attr('r',optionalCircleRad).style("fill",function(d){if(d.optional) return "white"; else return "black"; })
			.transition().duration(500)
			.attr('cx',function(d){ return d.p.x;}).attr('cy',function(d){ return d.p.y-(fRecHeight/2);});
		if(featureModelVis.configurationMode==1){
			optionalCirc.classed("notselected",function(d){return !d.selected;} );
			newOptionalCirc.classed("notselected",function(d){return !d.selected;} );
			optionalCirc.classed("selected",function(d){return d.selected;} );
			newOptionalCirc.classed("selected",function(d){return d.selected;} );
		}
		
		optionalCirc.exit().remove();
		/*
		d3.select("#fmedittab").selectAll('p').data(featureModelVis.getVertices()).text(function(d){return d.l;})
		.transition().duration(500)
		.style('left',function(d){ return offsetX+d.p.x-(fRecWidth/2)+"px";}).style('top',function(d){ return offsetY+d.p.y-(fRecHeight/2)+"px";});
		
		d3.select("#fmedittab").selectAll('p').data(featureModelVis.getVertices()).enter().append('p').attr('class','fmlabels')
			.style('width',fRecWidth+"px").style('height',fRecHeight+"px").text(function(d){return d.l;})
			.on('click',function(d){return featureModelVis.addLeaf(d.v);})
			.style('left',function(d){ return offsetX+d.f.p.x-(fRecWidth/2)+"px";}).style('top',function(d){ return offsetY+d.f.p.y-(fRecHeight/2)+"px";})
			.transition().duration(500)
			.style('left',function(d){ return offsetX+d.p.x-(fRecWidth/2)+"px";}).style('top',function(d){ return offsetY+d.p.y-(fRecHeight/2)+"px";});
		*/
		var labels = d3.select("#g_labels_"+featureModelVis.svgId).selectAll('text').data(featureModelVis.getVertices2());

		labels.text(function(d){return d.l;}).transition().duration(500)
			.attr('x',function(d){ return d.p.x;}).attr('y',function(d){ return d.p.y+(10*d.n);});
	

			
		var newlabels = labels.enter().append('text');
		newlabels.attr('x',function(d){ return (d.f.p==null)?0:d.f.p.x;}).attr('y',function(d){ return (d.f.p==null)?0:d.f.p.y+(10*d.n);})
			.text(function(d){return d.l;}).attr("class","featurelabels")
			.attr('style','text-anchor:middle;	font-size: 0.8em;')
			.transition().duration(500)
			.attr('x',function(d){ return d.p.x;}).attr('y',function(d){ return d.p.y+(10*d.n);});		

		if(featureModelVis.configurationMode==1){
			labels.classed("notselected",function(d){return !d.selected;} );
			newlabels.classed("notselected",function(d){return !d.selected;} );
			labels.classed("selected",function(d){return d.selected;} );
			newlabels.classed("selected",function(d){return d.selected;} );
			newlabels.on('click',function(d){return featureModelVis.toggleSelection(d.v);});
		}
		if(featureModelVis.configurationMode==2){
			labels.classed("noselection",function(d){return (featureModelVis.selFeatures.indexOf(d.uuid)==-1)&&(featureModelVis.reqFeatures.indexOf(d.uuid)==-1);} );
			newlabels.classed("noselection",function(d){return (featureModelVis.selFeatures.indexOf(d.uuid)==-1)&&(featureModelVis.reqFeatures.indexOf(d.uuid)==-1);} );
			labels.classed("mashupselected",function(d){return (featureModelVis.selFeatures.indexOf(d.uuid)!=-1)&&(featureModelVis.reqFeatures.indexOf(d.uuid)==-1);} );
			newlabels.classed("mashupselected",function(d){return (featureModelVis.selFeatures.indexOf(d.uuid)!=-1)&&(featureModelVis.reqFeatures.indexOf(d.uuid)==-1);} );
			labels.classed("selected",function(d){return (featureModelVis.selFeatures.indexOf(d.uuid)==-1)&&(featureModelVis.reqFeatures.indexOf(d.uuid)!=-1);} );
			newlabels.classed("selected",function(d){return (featureModelVis.selFeatures.indexOf(d.uuid)==-1)&&(featureModelVis.reqFeatures.indexOf(d.uuid)!=-1);} );
			labels.classed("selectedandmashupselected",function(d){return (featureModelVis.selFeatures.indexOf(d.uuid)!=-1)&&(featureModelVis.reqFeatures.indexOf(d.uuid)!=-1);} );
			newlabels.classed("selectedandmashupselected",function(d){return (featureModelVis.selFeatures.indexOf(d.uuid)!=-1)&&(featureModelVis.reqFeatures.indexOf(d.uuid)!=-1);} );
			newlabels.on('click',function(d){return featureModelVis.toggleSelection(d.v);});
		}
		if(featureModelVis.configurationMode==0){
			newlabels.on('click',function(d){return featureModelVis.showAnnotation(d.l,d.p,d.v,d.uuid);});
		}
		labels.exit().remove();
	}

	featureModelVis.showAnnotation = function(label,loc, fno,uuid){
		$('#annotationbox').remove();
		var template = $('#annotationTemplate').html();
		Mustache.parse(template);   // optional, speeds up future uses
		var view ={
				leftLocation: featureModelVis.offsetX+ loc.x,
				rightLocation: featureModelVis.offsetY-featureModelVis.svgH+loc.y,
				enititiesList: editFeatureModel.getEntitiesListString(uuid),
				preconditionList: editFeatureModel.getFactListString(uuid,"preconditions"),
				effectList: editFeatureModel.getFactListString(uuid,"effects"),
				featureName: label,
				featureNumber: fno,
				uuid: uuid
				
		}
		var rendered = Mustache.render(template, view);
		$('#edittabFMContainer').append(rendered);
	}
	featureModelVis.findFeaturebyUUID = function(featureUUID){
		function recursiveFeatureFind(curFeature, featureUUID){
			if(featureUUID == curFeature.uuid){
				return curFeature;
			}
			else{
				for(var cntr=0; cntr<curFeature.c.length; cntr++){
					var result = recursiveFeatureFind(curFeature.c[cntr],featureUUID);
					if(result !=null)
						return result;
				}
			}
			return null;
			
		}
		return recursiveFeatureFind(featureModelVis.vis,featureUUID);
	}

	featureModelVis.initialize = function(){
		//d3.select("body").append("div").attr('id','navdiv');
		d3.select("#"+featureModelVis.divId).append("svg").attr("width", featureModelVis.svgW).attr("height", featureModelVis.svgH).attr('id',featureModelVis.svgId).append('g').attr('id','featureModelVisgrp'+"_"+featureModelVis.svgId).attr('transform',function(){ return 'translate('+featureModelVis.offsetX+','+featureModelVis.offsetY+')';});
		d3.select("#featureModelVisgrp_"+featureModelVis.svgId).append('g').attr('id','g_lines_'+featureModelVis.svgId).selectAll('line').data(featureModelVis.getEdges()).enter().append('line')
			.attr('x1',function(d){ return d.p1.x;}).attr('y1',function(d){ return d.p1.y;})
			.attr('x2',function(d){ return d.p2.x;}).attr('y2',function(d){ return d.p2.y;});

		var frect = d3.select("#featureModelVisgrp_"+featureModelVis.svgId).append('g').attr('id','g_rect_'+featureModelVis.svgId).selectAll('rect').data(featureModelVis.getVertices()).enter()
			.append('rect').attr('x',function(d){ return d.p.x-(fRecWidth/2);}).attr('y',function(d){ return d.p.y-(fRecHeight/2);}).attr('width',fRecWidth).attr('height',fRecHeight).attr('class','incRect').attr('style','stroke:grey;shape-rendering:crispEdges;').attr('fill','white');
		
		
		/*
		d3.select("#fmedittab").selectAll('p').data(featureModelVis.getVertices()).enter().append('p').attr('class','fmlabels')
		.style('width',fRecWidth+"px").style('height',fRecHeight+"px").style('left',function(d){ return offsetX+ d.p.x-(fRecWidth/2)+"px";}).style('top',function(d){ return offsetY+d.p.y-(fRecHeight/2)+"px";}).text(function(d){return d.l;})
			.on('click',function(d){return featureModelVis.addLeaf(d.v);});
		*/
		
		
		
		var ftext = d3.select("#featureModelVisgrp_"+featureModelVis.svgId).append('g').attr('id','g_labels_'+featureModelVis.svgId).selectAll('text').data(featureModelVis.getVertices2()).enter().append('text')
			.attr('x',function(d){ return d.p.x;}).attr('y',function(d){ return d.p.y+(15*d.n);}).text(function(d){return d.l;})
			.attr("class","featurelabels").attr('style','text-anchor:middle;	font-size: 0.8em;')
			.on('click',function(d){return featureModelVis.showAnnotation(d.l,d.p,d.v,d.uuid);});	
		
		if(featureModelVis.configurationMode==1){
			frect.on('click',function(d){return  featureModelVis.toggleSelection(d.v);});
			ftext.on('click',function(d){return  featureModelVis.toggleSelection(d.v);});
			
		}
		if(featureModelVis.configurationMode==0)
		{
			frect.on('click',function(d){return featureModelVis.showAnnotation(d.l,d.p,d.v,d.uuid);});
			ftext.on('click',function(d){return featureModelVis.showAnnotation(d.l,d.p,d.v,d.uuid);});
		}	
		
			
		d3.select("#featureModelVisgrp_"+featureModelVis.svgId).append('g').attr('id','g_optcirc_'+featureModelVis.svgId);
		
		
		d3.select("#featureModelVisgrp_"+featureModelVis.svgId).append('g').attr('id','g_altcurv_'+featureModelVis.svgId);
		d3.select("#featureModelVisgrp_"+featureModelVis.svgId).append('g').attr('id','g_orcurv_'+featureModelVis.svgId);

	}
	
	
	featureModelVis.parse2 = function(xml){
		function dfsread(element,fno){
		var str = $(element).attr("name");
		var optional = false;
		if($(element).attr("type")=="optional")
			optional =true;
		var curNo = 0;
		var alternative = false;
		var orgroup = false;
		if(($(element).children().length >0)&&($($(element).children()[0]).prop("tagName") =="alternative"))
			alternative =true;
		if(($(element).children().length >0)&&($($(element).children()[0]).prop("tagName") =="orgroup"))
			orgroup =true;
		if(fno == -1){
			featureModelVis.vis.l = str;
			featureModelVis.vis.altchilds = alternative
			featureModelVis.vis.orgroup = orgroup
			//console.log(featureModelVis.vis);
		}else
		{	
			curNo = featureModelVis.addLeaf(fno,str,optional,alternative,orgroup);
		}

		$.each($(element).children(),function( index, value ) {
			if(($(value).prop("tagName") =="alternative")||($(value).prop("tagName") =="orgroup")){
				$.each($(value).children(),function( index, value ) {
					if($(value).prop("tagName") == "feature"){
						dfsread(value, curNo);
					}
				})
			}
			if($(value).prop("tagName") =="feature"){
				dfsread(value, curNo);
			}
		})

			
		}
		
		 xmlDoc = $.parseXML( xml );
		 $xml = $( xmlDoc );
		 // console.log( $xml.children()[0]);
		 
		 childNodes =  $($xml.children()[0]).children()
		 
		 for(var i = 0; i< childNodes.length; i++)
		 {
			 if($($xml.children()[0]).children()[i].nodeName=="feature" ){
				 dfsread($($xml.children()[0]).children()[i], -1);
				 break;
			 }
		 }
	
	}
	
	featureModelVis.parse = function(feature){
		function dfsread(feature,fno){
			var curNo = 0;
			if(fno == -1){
				featureModelVis.vis.l = feature.name;
				featureModelVis.vis.altchilds = feature.alternative;
				featureModelVis.vis.orgroup = feature.orgroup;
				featureModelVis.vis.uuid =  feature.uuid;
				
			}else
			{	
				curNo = featureModelVis.addLeaf(fno,feature.name,feature.optional,feature.alternative,feature.orgroup,feature.uuid);
			}
			
			$.each(feature.children,function( index, value ) {
				
					dfsread(value, curNo);
				
			})

			
		}
		featureModelVis.size=1;
		featureModelVis.vis={v:0, l:'?', optional: false, orgroup:false, altchilds: false, selected:false, p:{x:featureModelVis.cx, y:featureModelVis.cy},c:[]};
		dfsread(feature, -1);
		
	
	}
	
	
	
	featureModelVis.initialize();

	return featureModelVis;
	
	
	
	
}
String.prototype.replaceAt=function(index, character) {
    return this.substr(0, index) + character + this.substr(index+character.length);
}
String.prototype.insertAt=function(index, character) {
    return this.substr(0, index) + character + this.substr(index+character.length-1);
}



function pausecomp(millis)
 {
  var date = new Date();
  var curDate = null;
  do { curDate = new Date(); }
  while(curDate-date < millis);
}




