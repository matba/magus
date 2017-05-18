/**
 * Model File for service annotation
 */


function serviceClass(){
	var service ={};
	service.svs =null;
	
	service.toStringEntity=function(value){
		return value.type.name+"(<i>"+value.name.name+"</i>)";
	}
	
	service.toStringFact=function(value){ 
		var notstr="";
		if(value.not)
			notstr="~";
		return notstr+value.type.name+"("+ "<i>"+value.arguments[0].name+"</i>"+","+"<i>"+value.arguments[1].name+"</i>"+")";
	}
	
	service.emptyService = function(serviceName, baseURI, invocationService){
		var result={};
		result.inputs={};
		result.outputs={};
		result.vars={};
		result.inputList=[];
		result.outputList=[];
		result.varList=[];
		result.precList=[];
		result.effectList=[];
		result.receiveService = null;
		result.invocationService = invocationService;
		result.baseURI = baseURI;
		result.name = serviceName;
		return result;
	}
	
	service.parse = function(xml){
		function findAllNodes($node,tag){
			var result =[];
			var childNodes =  $node.children()
			for(var cnt=0; cnt<childNodes.length; cnt++){
				var curChild = childNodes[cnt];
				
				if(curChild.nodeName==tag) {
					result.push(curChild);
				}
				else{
					var curResult = findAllNodes($(curChild),tag)
					result = result.concat(curResult);
				}
				
			}
			
			return result;
		}
		function readService(node,serviceName, baseURI, invocationService){
			var result = {};
			result.inputs={};
			result.outputs={};
			result.vars={};
			result.inputList=[];
			result.outputList=[];
			result.varList=[];
			result.precList=[];
			result.effectList=[];
			result.receiveService = null;
			result.invocationService = invocationService;
			result.baseURI = baseURI;
			result.name = serviceName;
			
			var inputNodes= findAllNodes($(node),'process:Input');
			
			for(var icnt=0; icnt<inputNodes.length; icnt++){
				var curNode=inputNodes[icnt];
				var nodeID= $(curNode).attr("rdf:ID");
				var nodeChilds = $(curNode).children();
				var inputTypeURI = null;
				for(var ccnt=0; ccnt<nodeChilds.length; ccnt++){
					if(nodeChilds[ccnt].nodeName=="process:parameterType"){
						inputTypeURI = $(nodeChilds[ccnt]).text();
						break;
					}
				}
				var inputURI = baseURI+"#"+nodeID;
				var inputInstance ={type:{uri:inputTypeURI,name:utility.getEntityFragment(inputTypeURI)},name:{uri:inputURI,name:nodeID}};
				result.inputList.push(inputURI);
				result.inputs[inputURI]=inputInstance
			}
			
			
			var outputNodes= findAllNodes($(node),'process:Output');
			
			for(var ocnt=0; ocnt<outputNodes.length; ocnt++){
				var curNode=outputNodes[ocnt];
				var nodeID= $(curNode).attr("rdf:ID");
				var nodeChilds = $(curNode).children();
				var outputTypeURI = null;
				for(var ccnt=0; ccnt<nodeChilds.length; ccnt++){
					if(nodeChilds[ccnt].nodeName=="process:parameterType"){
						outputTypeURI = $(nodeChilds[ccnt]).text();
						break;
					}
				}
				var outputURI = baseURI+"#"+nodeID;
				var outputInstance ={type:{uri:outputTypeURI,name:utility.getEntityFragment(outputTypeURI)},name:{uri:outputURI,name:nodeID}};
				result.outputList.push(outputURI);
				result.outputs[outputURI]=outputInstance
			}
			
			var localVarNodes= findAllNodes($(node),'swrl:ClassAtom');
			
			for(var ocnt=0; ocnt<localVarNodes.length; ocnt++){
				var curNode=localVarNodes[ocnt];
				
				var varNodes = findAllNodes($(curNode),'swrl:Variable');
				
				if(varNodes.length==0)
					continue;
				
				var nodeID = $(varNodes[0]).attr("rdf:ID");
				var varURI = baseURI+"#"+nodeID;
				
				var classPredicateNode=  findAllNodes($(curNode),'swrl:classPredicate');
				
				var vartypeURI = $(classPredicateNode[0]).attr("rdf:resource");
				
				vartypeURI = utility.clean(vartypeURI);
				
				if(!utility.isAbsolute(vartypeURI))
				{
					vartypeURI = baseURI + vartypeURI;		
				}
				
				var varURI = baseURI+"#"+nodeID;
				var varInstance ={type:{uri:vartypeURI,name:utility.getEntityFragment(vartypeURI)},name:{uri:varURI,name:nodeID}};
				result.varList.push(varURI);
				result.vars[varURI]=varInstance

			}
			
			var precNodeList = findAllNodes($(node),'process:hasPrecondition');
			
			if(precNodeList.length >0){
				var precNode = precNodeList[0];
				var factNodeList = findAllNodes($(precNode),'swrl:IndividualPropertyAtom');
				for(var pcnt=0; pcnt<factNodeList.length; pcnt++){
					var curNode=factNodeList[pcnt];
					var factURI =null;
					var param1URI = null;
					var param2URI = null;
					var isNot = false;
					
					for(var pccnt=0; pccnt <$(curNode).children().length; pccnt++){
						var childNode = $(curNode).children()[pccnt];
						if(childNode.nodeName =="swrl:propertyPredicate"){
							factURI = $(childNode).attr("rdf:resource");
							factURI = utility.clean(factURI);
						}
						if(childNode.nodeName =="swrl:argument1"){
							if($(childNode).attr("rdf:resource")==null){
								param1URI = $($(childNode).children()[1]).attr("rdf:ID")
							}
							else{
								param1URI = $(childNode).attr("rdf:resource");
								param1URI = utility.clean(param1URI);
							}
						}
						if(childNode.nodeName =="swrl:argument2"){
							if($(childNode).attr("rdf:resource")==null){
								param2URI = $($(childNode).children()[2]).attr("rdf:ID")
							}
							else{
								param2URI = $(childNode).attr("rdf:resource");
								param2URI = utility.clean(param2URI);
							}
						}
					}
					
					if(!utility.isAbsolute(factURI)){
						if(factURI.startsWith("!"))
						{
							isNot =true;
							factURI= factURI.substring(1);
						}
						factURI = baseURI+"#"+factURI;
						
					}else{
						var t = factURI.lastIndexOf("#");
						if((factURI.length>t+2) && factURI.charAt(t+1)=='!'){
							isNot =true;
							factURI = factURI.substring(0,t+1) + factURI.substring(t+2);
						}
					}
					
					if(!utility.isAbsolute(param1URI))
						param1URI = baseURI+"#"+param1URI;
					if(!utility.isAbsolute(param2URI))
						param2URI = baseURI+"#"+param2URI;
					
					var newPrecondition ={not:isNot,type:{name:utility.getEntityFragment(factURI),uri:factURI}, arguments:[{name:utility.getEntityFragment(param1URI),uri:param1URI },{name:utility.getEntityFragment(param2URI),uri:param2URI}] };
					
					result.precList.push(newPrecondition);
					
				}
			}
			
			var 		effectNodeList = findAllNodes($(node),'process:hasResult');
			
			if(effectNodeList.length >0){
				var effectNode = effectNodeList[0];
				var factNodeList = findAllNodes($(effectNode),'swrl:IndividualPropertyAtom');
				for(var pcnt=0; pcnt<factNodeList.length; pcnt++){
					var curNode=factNodeList[pcnt];
					var factURI =null;
					var param1URI = null;
					var param2URI = null;
					var isNot = false;
					
					for(var pccnt=0; pccnt <$(curNode).children().length; pccnt++){
						var childNode = $(curNode).children()[pccnt];
						if(childNode.nodeName =="swrl:propertyPredicate"){
							factURI = $(childNode).attr("rdf:resource");
							factURI = utility.clean(factURI);
						}
						if(childNode.nodeName =="swrl:argument1"){
							if($(childNode).attr("rdf:resource")==null){
								param1URI = $($(childNode).children()[1]).attr("rdf:ID")
							}
							else{
								param1URI = $(childNode).attr("rdf:resource");
								param1URI = utility.clean(param1URI);
							}
						}
						if(childNode.nodeName =="swrl:argument2"){
							if($(childNode).attr("rdf:resource")==null){
								param2URI = $($(childNode).children()[2]).attr("rdf:ID")
							}
							else{
								param2URI = $(childNode).attr("rdf:resource");
								param2URI = utility.clean(param2URI);
							}
						}
					}
					
					if(!utility.isAbsolute(factURI)){
						if(factURI.startsWith("!"))
						{
							isNot =true;
							factURI= factURI.substring(1);
						}
						factURI = baseURI+"#"+factURI;
						
					}else{
						var t = factURI.lastIndexOf("#");
						if((factURI.length>t+2) && factURI.charAt(t+1)=='!'){
							isNot =true;
							factURI = factURI.substring(0,t+1) + factURI.substring(t+2);
						}
					}
					
					if(!utility.isAbsolute(param1URI))
						param1URI = baseURI+"#"+param1URI;
					if(!utility.isAbsolute(param2URI))
						param2URI = baseURI+"#"+param2URI;
					
					var newEffect = {not:isNot,type:{name:utility.getEntityFragment(factURI),uri:factURI}, arguments:[{name:utility.getEntityFragment(param1URI),uri:param1URI },{name:utility.getEntityFragment(param2URI),uri:param2URI}] };
					
					result.effectList.push(newEffect);
					
				}
			}
			
			return result;
		}
		
		
		 var xmlDoc = $.parseXML( xml );
		 var $xml = $( xmlDoc );
		 var $root =  $($xml.children()[0]);
		 var baseURI = "";
		 baseURI = $root.attr("xml:base");
		 
		 var svsNodes = findAllNodes($root,'service:Service');
		 
		 for(var svcnt=0; svcnt<svsNodes.length; svcnt++){
			 var serviceName = $(svsNodes[svcnt]).attr("rdf:ID");
			 if(!serviceName.endsWith("Callback")){
				 service.svs = readService(svsNodes[svcnt],serviceName, baseURI, null); 
			 }
		 }
		 for(var svcnt=0; svcnt<svsNodes.length; svcnt++){
			 var serviceName = $(svsNodes[svcnt]).attr("rdf:ID");
			 if(serviceName.endsWith("Callback")){
				 service.svs.receiveService = readService(svsNodes[svcnt],serviceName, baseURI, service.svs); 
			 }
		 }
	}
	
	service.serializeToXML= function(){
		function createInputOutputNode(xmlDoc,curInput,text){
			var hasInputNode = xmlDoc.createElement('process:has'+text);
			
			var inputNode = xmlDoc.createElement('process:'+text);
			
			inputNode.setAttribute('rdf:ID',curInput.name.name);
			
			var parameterTypeNode = xmlDoc.createElement('process:parameterType');
			parameterTypeNode.setAttribute('rdf:datatype','http://www.w3.org/2001/XMLSchema#anyURI');
			
			var parameterTypeTextNode = xmlDoc.createTextNode(curInput.type.uri);
			
			parameterTypeNode.appendChild(parameterTypeTextNode);
			
			
			inputNode.appendChild(parameterTypeNode);
			
			hasInputNode.appendChild(inputNode);
			return hasInputNode;
		}
		function recNodeCreator(xmlDoc,conds){
			var atomListNode = xmlDoc.createElement('swrl:AtomList');
			
			var firstNode = xmlDoc.createElement('rdf:first');
			firstNode.appendChild(conds[0]);
			atomListNode.appendChild(firstNode);
			
			var restNode = xmlDoc.createElement('rdf:rest');
			
			if(conds.length>1){
				restNode.appendChild(recNodeCreator(xmlDoc,conds.slice(1)));
			}
			else{
				restNode.setAttribute('rdf:resource','http://www.w3.org/1999/02/22-rdf-syntax-ns#nil');
			}
			atomListNode.appendChild(restNode);
			
			return atomListNode;
		}
		
		function createPreconditionNode(xmlDoc,svs){
			var preconditionNode= xmlDoc.createElement('process:hasPrecondition');
			var swrlConditionNode= xmlDoc.createElement('expr:SWRL-Condition');
			swrlConditionNode.setAttribute('rdf:ID',svs.name+'Prec');
			var expressionObjectNode = xmlDoc.createElement('expr:expressionObject');
			
			var conds = [];
			
			for(var inputCntr=0; inputCntr< svs.inputList.length; inputCntr++){
				var classAtomNode = xmlDoc.createElement('swrl:ClassAtom');
				
				var classPredicateNode = xmlDoc.createElement('swrl:classPredicate');
				classPredicateNode.setAttribute('rdf:resource',svs.inputs[svs.inputList[inputCntr]].type.uri);
				var argument1Node =  xmlDoc.createElement('swrl:argument1');
				argument1Node.setAttribute('rdf:resource',svs.inputs[svs.inputList[inputCntr]].name.uri);
				classAtomNode.appendChild(classPredicateNode);
				classAtomNode.appendChild(argument1Node);
				conds.push(classAtomNode);
			}
			for(var varCntr=0; varCntr< svs.varList.length; varCntr++){
				var classAtomNode = xmlDoc.createElement('swrl:ClassAtom');
				
				var classPredicateNode = xmlDoc.createElement('swrl:classPredicate');
				classPredicateNode.setAttribute('rdf:resource',svs.vars[svs.varList[varCntr]].type.uri);
				var argument1Node =  xmlDoc.createElement('swrl:argument1');
				
				var variableNode =  xmlDoc.createElement('swrl:Variable');
				variableNode.setAttribute('rdf:ID',svs.vars[svs.varList[varCntr]].name.name);
				argument1Node.appendChild(variableNode);
				
				classAtomNode.appendChild(classPredicateNode);
				classAtomNode.appendChild(argument1Node);
				conds.push(classAtomNode);
			}
			for(var precCntr=0; precCntr< svs.precList.length; precCntr++){
				var ipAtomNode = xmlDoc.createElement('swrl:IndividualPropertyAtom');
				
				var propertyPredicateNode = xmlDoc.createElement('swrl:propertyPredicate');
				if( svs.precList[precCntr].not)
					propertyPredicateNode.setAttribute('rdf:resource', svs.precList[precCntr].type.uri.substring(0, svs.precList[precCntr].type.uri.length-svs.precList[precCntr].type.name.length)+'!'+svs.precList[precCntr].type.name);
				else
					propertyPredicateNode.setAttribute('rdf:resource', svs.precList[precCntr].type.uri);
				
				var argument1Node = xmlDoc.createElement('swrl:argument1');
				argument1Node.setAttribute('rdf:resource', svs.precList[precCntr].arguments[0].uri);
				
				var argument2Node = xmlDoc.createElement('swrl:argument2');
				argument2Node.setAttribute('rdf:resource', svs.precList[precCntr].arguments[1].uri);
				
				ipAtomNode.appendChild(propertyPredicateNode);
				ipAtomNode.appendChild(argument1Node);
				ipAtomNode.appendChild(argument2Node);
				
				conds.push(ipAtomNode);
			}
			
			var atomListNode= recNodeCreator(xmlDoc,conds);
			
			expressionObjectNode.appendChild(atomListNode);
			
			swrlConditionNode.appendChild(expressionObjectNode);
			
			
			var expressionLanguageNode = xmlDoc.createElement('expr:expressionLanguage');
			expressionLanguageNode.setAttribute('rdf:resource','http://www.daml.org/services/owl-s/1.2/generic/Expression.owl#SWRL');
			swrlConditionNode.appendChild(expressionLanguageNode);
			preconditionNode.appendChild(swrlConditionNode);
			return preconditionNode;
		}
		
		function createEffectNode(xmlDoc,svs){
			var effectNode= xmlDoc.createElement('process:hasResult');
			var swrlConditionNode= xmlDoc.createElement('expr:SWRL-Condition');
			swrlConditionNode.setAttribute('rdf:ID',svs.name+'Eff');
			var expressionObjectNode = xmlDoc.createElement('expr:expressionObject');
			
			var conds = [];
			
			
			for(var effectCntr=0; effectCntr< svs.effectList.length; effectCntr++){
				var ipAtomNode = xmlDoc.createElement('swrl:IndividualPropertyAtom');
				
				var propertyPredicateNode = xmlDoc.createElement('swrl:propertyPredicate');
				if( svs.effectList[effectCntr].not)
					propertyPredicateNode.setAttribute('rdf:resource', svs.effectList[effectCntr].type.uri.substring(0, svs.effectList[effectCntr].type.uri.length-svs.effectList[effectCntr].type.name.length)+'!'+svs.effectList[effectCntr].type.name);
				else
					propertyPredicateNode.setAttribute('rdf:resource', svs.effectList[effectCntr].type.uri);
				
				var argument1Node = xmlDoc.createElement('swrl:argument1');
				argument1Node.setAttribute('rdf:resource', svs.effectList[effectCntr].arguments[0].uri);
				
				var argument2Node = xmlDoc.createElement('swrl:argument2');
				argument2Node.setAttribute('rdf:resource', svs.effectList[effectCntr].arguments[1].uri);
				
				ipAtomNode.appendChild(propertyPredicateNode);
				ipAtomNode.appendChild(argument1Node);
				ipAtomNode.appendChild(argument2Node);
				
				conds.push(ipAtomNode);
			}
			
			var atomListNode= recNodeCreator(xmlDoc,conds);
			
			expressionObjectNode.appendChild(atomListNode);
			
			swrlConditionNode.appendChild(expressionObjectNode);
			
			
			var expressionLanguageNode = xmlDoc.createElement('expr:expressionLanguage');
			expressionLanguageNode.setAttribute('rdf:resource','http://www.daml.org/services/owl-s/1.2/generic/Expression.owl#SWRL');
			swrlConditionNode.appendChild(expressionLanguageNode);
			effectNode.appendChild(swrlConditionNode);
			return effectNode;
		}
		function serializeService(xmlDoc,svs){
			var serviceNode = xmlDoc.createElement('service:Service');
			
			serviceNode.setAttribute('rdf:ID',svs.name);
			
			var describedByNode = xmlDoc.createElement('service:describedBy');
			
			var processNode = xmlDoc.createElement('process:AtomicProcess');
			
			processNode.setAttribute('rdf:ID',svs.name+'Process');
			
			var describesNode= xmlDoc.createElement('service:describes');
			describesNode.setAttribute('rdf:resource','#'+svs.name);
			
			processNode.appendChild(describesNode);
			
			for(var inputCntr=0; inputCntr< svs.inputList.length; inputCntr++){
				
				var curInput = svs.inputs[svs.inputList[inputCntr]];
				processNode.appendChild(createInputOutputNode(xmlDoc,curInput,'Input'));			
				
			}
			for(var outputCntr=0; outputCntr< svs.outputList.length; outputCntr++){
				
				var curOutput = svs.outputs[svs.outputList[outputCntr]];
				processNode.appendChild(createInputOutputNode(xmlDoc,curOutput,'Output'));			
				
			}
			if((svs.precList.length>0)||(svs.inputList.length>0)||(svs.varList.length>0))
				processNode.appendChild(createPreconditionNode(xmlDoc,svs));
			if((svs.effectList.length>0))
				processNode.appendChild(createEffectNode(xmlDoc,svs));
			
			describedByNode.appendChild(processNode);			
			serviceNode.appendChild(describedByNode);
			return serviceNode;
			
			
		}
		
		var xmlDoc = document.implementation.createDocument('http://www.w3.org/1999/02/22-rdf-syntax-ns#', 'rdf:RDF');
		var rdfNode= xmlDoc.childNodes[0];
		
		
		rdfNode.setAttribute('xmlns:process', 'http://www.daml.org/services/owl-s/1.2/Process.owl#');
		rdfNode.setAttribute('xmlns:service', 'http://www.daml.org/services/owl-s/1.2/Service.owl#');
		rdfNode.setAttribute('xmlns:rdf', 'http://www.w3.org/1999/02/22-rdf-syntax-ns#');
		rdfNode.setAttribute('xmlns:rdfs', 'http://www.w3.org/2000/01/rdf-schema#');
		rdfNode.setAttribute('xmlns:owl', 'http://www.w3.org/2002/07/owl#'); 
		rdfNode.setAttribute('xmlns:daml', 'http://www.daml.org/2001/03/daml+oil'); 
		rdfNode.setAttribute('xmlns:expr', 'http://www.daml.org/services/owl-s/1.2/generic/Expression.owl#'); 
		rdfNode.setAttribute('xmlns:swrl', 'http://www.w3.org/2003/11/swrl#'); 
		rdfNode.setAttribute('xml:base', service.svs.baseURI);
		
		rdfNode.appendChild(serializeService(xmlDoc,service.svs));
		
		if(service.svs.receiveService != null)
			rdfNode.appendChild(serializeService(xmlDoc,service.svs.receiveService));
		    
		
		
		return xmlDoc;
	}
	
	return service;
}










