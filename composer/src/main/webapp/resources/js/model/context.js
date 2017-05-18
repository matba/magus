

function contextClass(){
	var context ={};
	context.contextTypes=[];
	context.contextFactTypes=[];
	context.instances =[];
	context.baseURI="";
	
	context.indexOfType = function(typeURI){
		result = -1
		$.each(contextModel.contextTypes,function( index, value ) {
			
			if(value.uri == typeURI)
				result=  index;
		});
		
		return result;
		
	}
	context.indexOfFactType = function(facttypeURI){
		result = -1;
		$.each(contextModel.contextFactTypes,function( index, value ) {
			
			if(value.type.uri == facttypeURI)
				result=  index;
		});
		
		return result;
		
	}
	context.lookupFactType = function(facttypeURI){
		result = null;
		ftIndex= context.indexOfFactType(facttypeURI);
		if(ftIndex!=-1)
			result =  context.contextFactTypes[ftIndex];
		
		return result;
		
	}
	
	context.toStringFact=function(value){
		return value.type.name+"(<i>"+value.arguments[0].name+"</i>"+","+"<i>"+value.arguments[1].name+"</i>"+")";
	}
	
	context.parse = function(xml){
		function readContextType(element){
			context.contextTypes.push({uri:context.baseURI+"#"+$(element).attr("rdf:ID"),name:$(element).attr("rdf:ID")});
			
		}
		function readContextFactType(element){
			var childNodes =  $(element).children()
			newFactType ={}
			newFactType.type = {uri:context.baseURI+"#"+$(element).attr("rdf:ID"),name:$(element).attr("rdf:ID")};
			newFactType.arguments = [{},{}];
			for(var i = 0; i< childNodes.length; i++)
			{
				 if(childNodes[i].nodeName=="rdfs:domain" ){
					 var domainText =  $(childNodes[i]).attr("rdf:resource");
					 if( domainText.startsWith("#")){
						 domainText = context.baseURI+domainText;
					 }
					 newFactType.arguments[0] = {uri:domainText, name:  utility.getEntityFragment(domainText)};
				 }
				 if(childNodes[i].nodeName=="rdfs:range" ){
					 domainText =  $(childNodes[i]).attr("rdf:resource");
					 if( domainText.startsWith("#")){
						 domainText = context.baseURI+domainText;
					 }
					 newFactType.arguments[1] = {uri:domainText, name:  utility.getEntityFragment(domainText)};
				 }
			}
			context.contextFactTypes.push(newFactType);
			//console.log("hello")
		}
		function readInstance(element){
			var childNodes =  $(element).children()
			var newInstance ={}
			newInstance.name = {uri:context.baseURI+$(element).attr("rdf:about"),name:$(element).attr("rdf:about").slice(1)};
			
			for(var i = 0; i< childNodes.length; i++)
			{
				 if(childNodes[i].nodeName=="rdf:type" ){
					 var domainText =  $(childNodes[i]).attr("rdf:resource");
					 if( domainText.startsWith("#")){
						 domainText = context.baseURI+domainText;
					 }
					 newInstance.type = {uri:domainText, name:  utility.getEntityFragment(domainText)};
				 }
				 
			}
			context.instances.push(newInstance);
			//console.log("hello")
		}
		 
		 xmlDoc = $.parseXML( xml );
		 $xml = $( xmlDoc );
		 
		 context.baseURI = "";
		 context.baseURI = $($xml.children()[0]).attr("xml:base")
		 
		 
		 
			
		 var childNodes =  $($xml.children()[0]).children()
		 
		 for(var i = 0; i< childNodes.length; i++)
		 {
			 if(childNodes[i].nodeName=="owl:Class" ){
				 readContextType(childNodes[i]);
				 
			 }
			 if($($xml.children()[0]).children()[i].nodeName=="owl:ObjectProperty" ){
				 readContextFactType(childNodes[i]);
				 
			 }
			 if($($xml.children()[0]).children()[i].nodeName=="owl:Thing" ){
				 readInstance(childNodes[i]);
				 
			 }
		 }
		 //console.log(context.contextFactTypes);
	}
	
	context.serializeToXML= function(){
		var xmlDoc = document.implementation.createDocument('http://www.w3.org/1999/02/22-rdf-syntax-ns#', 'rdf:RDF');
		var rdfNode= xmlDoc.childNodes[0];
		
		
		rdfNode.setAttribute('xmlns', context.baseURI); 
		rdfNode.setAttribute('xmlns:rdfs', 'http://www.w3.org/2000/01/rdf-schema#');
		rdfNode.setAttribute('xmlns:owl', 'http://www.w3.org/2002/07/owl#'); 
		rdfNode.setAttribute('xmlns:rdf', 'http://www.w3.org/1999/02/22-rdf-syntax-ns#');
		rdfNode.setAttribute('xmlns:xsd', 'http://www.w3.org/2001/XMLSchema#'); 
		rdfNode.setAttribute('xml:base', context.baseURI); 
		
		
		for(var classCntr=0;classCntr<context.contextTypes.length;classCntr++){
			var classNode =  xmlDoc.createElement('owl:Class');
			
			classNode.setAttribute('rdf:ID',context.contextTypes[classCntr].name);
			
			rdfNode.appendChild(classNode);
			
		}
		
		for(var propertyCntr=0;propertyCntr<context.contextFactTypes.length;propertyCntr++){
			var propertyNode =  xmlDoc.createElement('owl:ObjectProperty');
			
			propertyNode.setAttribute('rdf:ID',context.contextFactTypes[propertyCntr].type.name);
			
			var typeNode = xmlDoc.createElement('rdf:type');
			typeNode.setAttribute('rdf:resource','http://www.w3.org/2002/07/owl#ObjectProperty');
			
			var domainNode = xmlDoc.createElement('rdfs:domain');
			if(context.contextFactTypes[propertyCntr].arguments[0].uri.startsWith(context.baseURI))
				domainNode.setAttribute('rdf:resource',context.contextFactTypes[propertyCntr].arguments[0].uri.slice(context.baseURI.length)) ;
			else
				domainNode.setAttribute('rdf:resource',context.contextFactTypes[propertyCntr].arguments[0].uri) ;
			
			
			var resourceNode = xmlDoc.createElement('rdfs:range');
			if(context.contextFactTypes[propertyCntr].arguments[1].uri.startsWith(context.baseURI))
				resourceNode.setAttribute('rdf:resource',context.contextFactTypes[propertyCntr].arguments[1].uri.slice(context.baseURI.length)) ;
			else
				resourceNode.setAttribute('rdf:resource',context.contextFactTypes[propertyCntr].arguments[1].uri) ;
			
			
			propertyNode.appendChild(typeNode);
			propertyNode.appendChild(domainNode);
			propertyNode.appendChild(resourceNode);
				
				
			rdfNode.appendChild(propertyNode);
			
		}
		for(var instanceCntr=0;instanceCntr<context.instances.length;instanceCntr++){
			var instanceNode =  xmlDoc.createElement('owl:Thing');
			
			if(context.instances[instanceCntr].name.uri.startsWith(context.baseURI))
				instanceNode.setAttribute('rdf:about',context.instances[instanceCntr].name.uri.slice(context.baseURI.length)) ;
			else
				instanceNode.setAttribute('rdf:about',context.instances[instanceCntr].name.uri) ;
			
			var typeNode = xmlDoc.createElement('rdf:type');
			
			if(context.instances[instanceCntr].type.uri.startsWith(context.baseURI))
				typeNode.setAttribute('rdf:resource',context.instances[instanceCntr].type.uri.slice(context.baseURI.length)) ;
			else
				typeNode.setAttribute('rdf:resource',context.instances[instanceCntr].type.uri) ;
			
			instanceNode.appendChild(typeNode);
			
			rdfNode.appendChild(instanceNode);
			
		}
		return xmlDoc;
		    
	}
	return context;
}










