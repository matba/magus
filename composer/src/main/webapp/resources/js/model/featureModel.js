


function featureModelClass(){
	var featureModel ={};
	featureModel.rootfeature={};
	featureModel.annotations={};
	featureModel.integrityConstraints = [];
	featureModel.baseURI ="";
	
	featureModel.initialize = function(){
		
	}
	
	
	featureModel.clone = function(){
		function recursivefeatureModelClone(node){
			var newNode= {};
			newNode.name =node.name.slice(0);
			newNode.uuid = node.uuid.slice(0) ;
			newNode.optional = node.optional;
			newNode.alternative = node.alternative;
			newNode.orgroup = node.orgroup 
			
			newNode.children = []
			
			$.each(node.children,function( index, value ) {
				
				newNode.children.push(recursivefeatureModelClone(value))
			
			});
			
			return newNode;
		}
		
		var result = featureModelClass();
		
		result.rootfeature = recursivefeatureModelClone(featureModel.rootfeature);
		
		result.baseURI = featureModel.baseURI.slice(0);
		
		
		
		for (var key in featureModel.annotations) {
		    // skip loop if the property is from prototype
		    if (!featureModel.annotations.hasOwnProperty(key)) continue;
		
			var annotation = {};
			annotation.entities =featureModel.annotations[key].entities.slice(0);
			annotation.preconditions =featureModel.annotations[key].preconditions.slice(0);
			annotation.effects =featureModel.annotations[key].effects.slice(0);
			annotation.feature=featureModel.annotations[key].feature.slice(0);
			
			result.annotations[key]= annotation;
			
		}
		
		result.integrityConstraints = featureModel.integrityConstraints.slice(0);
		
		return result;
	}
	
	featureModel.addChild = function(featureUUID,name,optional,alternative,orgroup){
		function addChild(feature,uuid){
			
			if(feature.uuid==featureUUID){ feature.children.push({uuid:uuid, name:name,optional:optional, alternative: alternative,orgroup:orgroup,children:[]}); return ; }
			$.each(feature.children,function( index, value ) {
				
				addChild(value,uuid);
			});
			
		
			
		}
		uuid= utility.guid();
		addChild(featureModel.rootfeature,uuid);
		
		var annotation = {};
		annotation.entities =[];
		annotation.io ="none";
		annotation.preconditions =[];
		annotation.effects =[];
		annotation.feature=name;
		featureModel.annotations[uuid]=annotation

		return uuid;
	}
	
	featureModel.removeFeature = function(featureUUID){
		function removeFeature(feature){
			
			$.each(feature.children,function( index, value ) {
			 if(value.uuid==featureUUID){
				 feature.children.splice(index,1); 
				 return false;
			 }			 
			 else 
			 {
				 removeFeature(value); 
			 } 
			} );
			
			
		}
		
		removeFeature(featureModel.rootfeature);
		
		
	}
	
	featureModel.updateFeature = function(featureUUID,alternative,orgroup){
		function updateFeature(feature){
			
			if(feature.uuid==featureUUID){ feature.alternative=alternative;feature.orgroup =orgroup;  return ; }
			feature.children.forEach(updateFeature);
			
		}
		
		updateFeature(featureModel.rootfeature);

	}
	
	
	featureModel.getEntityNamesList = function(){
		result =[];
		


		for (var key in featureModel.annotations) {
		    // skip loop if the property is from prototype
		    if (!featureModel.annotations.hasOwnProperty(key)) continue;

		    var entitiesList = featureModel.annotations[key].entities;
			$.each(entitiesList,function( index, value ) {
				
				result.push(value.name.name);
			});
		    
		}


		
		
		return result;
	}
	
	featureModel.getEntitiesExceptForFeature = function(featureUUID){
		result =[];
		


		for (var key in featureModel.annotations) {
		    // skip loop if the property is from prototype
		    if (!featureModel.annotations.hasOwnProperty(key) || (key == featureUUID)) continue;

		    var entitiesList = featureModel.annotations[key].entities;
			$.each(entitiesList,function( index, value ) {
				
				result.push(value);
			});
		    
		}


		
		
		return result;
	}
	
	featureModel.toStringEntity=function(value){
		return value.type.name+"(<i>"+value.name.name+"</i>)";
	}
	
	
	
	featureModel.toStringFact=function(value){
		return value.fact.name+"(<i>"+value.arguments[0].name+"</i>"+","+"<i>"+value.arguments[1].name+"</i>"+")";
	}
	
	
	featureModel.getFactListString = function(featureUuid, factName){
		var factList = [];
		
		if(factName=="preconditions")
			factList = featureModel.annotations[featureUuid].preconditions;
		if(factName=="effects")
			factList = featureModel.annotations[featureUuid].effects;
		
		var seperator="";
		var result ="";
		$.each(factList,function( index, value ) {
			
			result = result+ seperator +featureModel.toStringFact(value);
			seperator =",";
		})
		return result;
	}
	

	
	featureModel.getEntitiesListString = function(featureUuid){
		//console.log(featureUuid)
		var entitiesList = featureModel.annotations[featureUuid].entities;
		var seperator="";
		var result ="";
		$.each(entitiesList,function( index, value ) {
			
			result = result+ seperator + featureModel.toStringEntity(value);
			seperator =",";
		})
		return result;
	}
	
	
	featureModel.findFeaturebyName = function(featureName){
		function recursiveFeatureFind(curFeature, featureName){
			if(featureName == curFeature.name){
				return curFeature;
			}
			else{
				for(var cntr=0; cntr<curFeature.children.length; cntr++){
					var result = recursiveFeatureFind(curFeature.children[cntr],featureName);
					if(result !=null)
						return result;
				}
			}
			return null;
			
		}
		return recursiveFeatureFind(featureModel.rootfeature,featureName);
	}
	featureModel.findFeaturebyUUID = function(featureUUID){
		function recursiveFeatureFind(curFeature, featureUUID){
			if(featureUUID == curFeature.uuid){
				return curFeature;
			}
			else{
				for(var cntr=0; cntr<curFeature.children.length; cntr++){
					var result = recursiveFeatureFind(curFeature.children[cntr],featureUUID);
					if(result !=null)
						return result;
				}
			}
			return null;
			
		}
		return recursiveFeatureFind(featureModel.rootfeature,featureUUID);
	}
	
	featureModel.parse = function(xml){
		function dfsread(element){
			var feature ={}
			feature.name = $(element).attr("name");
			feature.uuid = $(element).attr("uuid");
			feature.optional = false;
			if($(element).attr("type")=="optional")
				feature.optional =true;
			
			feature.alternative = false;
			feature.orgroup = false;
			if(($(element).children().length >0)&&($($(element).children()[0]).prop("tagName") =="alternative"))
				feature.alternative =true;
			if(($(element).children().length >0)&&($($(element).children()[0]).prop("tagName") =="orgroup"))
				feature.orgroup =true;
			
			feature.children = []
			$.each($(element).children(),function( index, value ) {
				if(($(value).prop("tagName") =="alternative")||($(value).prop("tagName") =="orgroup")){
					$.each($(value).children(),function( index, value ) {
						if($(value).prop("tagName") == "feature"){
							feature.children.push(dfsread(value));
						}
					})
				}
				if($(value).prop("tagName") =="feature"){
					feature.children.push(dfsread(value));
				}
			})
			
			return feature
				
			}
		function readAnnotation(element){
			var annotation = {};
			annotation.entities =[];
			annotation.preconditions =[];
			annotation.effects =[];
			annotation.feature=$(element).attr("feature");
			var annChilds = $(element).children();
			for(var acntr = 0; acntr< annChilds.length; acntr++)
			{
				 if(annChilds[acntr].nodeName=="entities" ){
					 var entitiesChilds = $(annChilds[acntr]).children();
					 for(var ecntr = 0; ecntr< entitiesChilds.length; ecntr++)
					 {
						 if(entitiesChilds[ecntr].nodeName=="entity" ){
							 var entityElement =  $(entitiesChilds[ecntr]);
							 
							 var curEntity ={name:{}, type:{}};
							 curEntity.name.name =entityElement.attr("name");
							 curEntity.name.uri =featureModel.baseURI+"#"+ entityElement.attr("name");
							 curEntity.type.name = utility.getEntityFragment(entityElement.attr("type"));
							 curEntity.type.uri =entityElement.attr("type");							 				 
							 curEntity.io =entityElement.attr("io");							 	
							 annotation.entities.push(curEntity);
						 
						 }
					 }
				 }
				 if((annChilds[acntr].nodeName=="precondition")||(annChilds[acntr].nodeName=="effect"  )){
					 var isPrecondition = true;
					 if(annChilds[acntr].nodeName=="effect"  )
						 isPrecondition = false;
					 
					 var peChilds = $(annChilds[acntr]).children()
					 for(var pcntr = 0; pcntr< peChilds.length; pcntr++)
					 {
						 
						if(peChilds[pcntr].nodeName=="facts" ){
							 var factsChilds = $(peChilds[pcntr]).children()
							 for(var ccntr = 0; ccntr< factsChilds.length; ccntr++)
							 {
								 if(factsChilds[ccntr].nodeName=="fact" ){
									 var factElement=$(factsChilds[ccntr]);
									 var curFact ={fact:{},arguments:[{},{}]};
									 curFact.fact.uri =factElement.attr("fact");
									 curFact.fact.name =utility.getEntityFragment(factElement.attr("fact"));
									 
									 
									 var argument1Text = factElement.attr("firstEntity");
									 if( argument1Text.startsWith("#")){
										 argument1Text = featureModel.baseURI+argument1Text;
									 }
									 curFact.arguments[0] = {uri:argument1Text, name:  utility.getEntityFragment(argument1Text)};
									 
									 var argument2Text = factElement.attr("secondEntity");
									 if( argument2Text.startsWith("#")){
										 argument2Text = featureModel.baseURI+argument2Text;
									 }
									 curFact.arguments[1] = {uri:argument2Text, name:  utility.getEntityFragment(argument2Text)};
									 
									 if (isPrecondition)
										 annotation.preconditions.push(curFact);
									 else
										 annotation.effects.push(curFact);
								 
								 }
							 }
						 }
					 }
				 }
				 
			}
			
			return annotation
		}
			
		 xmlDoc = $.parseXML( xml );
		 $xml = $( xmlDoc );
		 // console.log( $xml.children()[0]);
		 
		 var childNodes =  $($xml.children()[0]).children();
		 
		 for(var i = 0; i< childNodes.length; i++)
		 {
			 if(childNodes[i].nodeName=="feature" ){
				 featureModel.rootfeature = dfsread(childNodes[i]);
				 
			 }
			 
			 if(childNodes[i].nodeName=="annotations" ){
				 var annotationChilds =$(childNodes[i]).children();
				 featureModel.baseURI = $(childNodes[i]).attr("baseURI")
				 for(var acntr = 0; acntr< annotationChilds.length; acntr++)
				 {
					 if(annotationChilds[acntr].nodeName=="annotation" ){
						 var newAnnotation = readAnnotation(annotationChilds[acntr]);
						 //console.log(newAnnotation.feature);
						 //console.log(featureModel.findFeaturebyName(newAnnotation.feature).uuid);
						 featureModel.annotations[featureModel.findFeaturebyName(newAnnotation.feature).uuid]= newAnnotation;
						 
					 }
					 
				 }
			 }
			 if(childNodes[i].nodeName=="integrityconstraints" ){
				 var icChilds =$(childNodes[i]).children();
				 for(var acntr = 0; acntr< icChilds.length; acntr++)
				 {
					 if(icChilds[acntr].nodeName=="integrityconstraint" ){
						 var newIC = {};
						 newIC.type =$(icChilds[acntr]).attr("type");
						 newIC.source =$(icChilds[acntr]).attr("source");
						 newIC.target =$(icChilds[acntr]).attr("target");
						 //console.log(newAnnotation.feature);
						 //console.log(featureModel.findFeaturebyName(newAnnotation.feature).uuid);
						 featureModel.integrityConstraints.push(newIC);
						 
					 }
					 
				 }
			 }
		 }
			 
		
	}
	
	featureModel.validateConfiguration = function(selectedFeatureUUIDs){
		var returnVal={success:true}
		if(selectedFeatureUUIDs.length==0){
			returnVal={success:false,reason:'No feature is selected.'}
		}
		else
			{
				for(var featureCntr=0; featureCntr<selectedFeatureUUIDs.length;featureCntr++){
					var curFeature = featureModel.findFeaturebyUUID(selectedFeatureUUIDs[featureCntr]);
					if((curFeature.alternative || curFeature.orgroup)&&(curFeature.children.length>0)){
						var oneChildFeatureIsSelected = false;
						$.each(curFeature.children,function(index,value){if(selectedFeatureUUIDs.indexOf(value.uuid)!=-1){oneChildFeatureIsSelected=true;} });
						if(!oneChildFeatureIsSelected){
							returnVal={success:false,reason:'None of the children for feature '+curFeature.name+' has been selected.'};
							break;
						}
					}
				}
				if(returnVal.success){
					for(var icCntr=0;icCntr<featureModel.integrityConstraints.length; icCntr++){
						var curIC = featureModel.integrityConstraints[icCntr];
						var sourceFeature = featureModel.findFeaturebyName(curIC.source);
						var targetFeature = featureModel.findFeaturebyName(curIC.target);
						if(curIC.type=='requires'){
							if(selectedFeatureUUIDs.indexOf(sourceFeature.uuid)!=-1){
								if(selectedFeatureUUIDs.indexOf(targetFeature.uuid)==-1){
									returnVal={success:false,reason:'Integrity constraint '+sourceFeature.name+' requries '+targetFeature.name+' does not hold.'};
									break;
								}
							}
						}
						if(curIC.type=='excludes'){
							if(selectedFeatureUUIDs.indexOf(sourceFeature.uuid)!=-1){
								if(selectedFeatureUUIDs.indexOf(targetFeature.uuid)!=-1){
									returnVal={success:false,reason:'Integrity constraint '+sourceFeature.name+' excludes '+targetFeature.name+' does not hold.'};
									break;
								}
							}
						}
					}
				}
			}
		
		return returnVal;
	}
	
	featureModel.getAllFeatures = function(){
		function getAllFeaturesRec(feature){
			returnVal = [];
			returnVal.push(feature);
			
			
			$.each(feature.children,function(index,value){returnVal =  returnVal.concat(getAllFeaturesRec(value)); });
			
			return returnVal;
		}
		return getAllFeaturesRec(featureModel.rootfeature);
	}
	
	featureModel.serializeToXML= function(){
		function seralizeFeature(feature){
			var featureModelNode = xmlDoc.createElement('feature');
			var placeToAdd = featureModelNode;
			featureModelNode.setAttribute('name', feature.name);
			featureModelNode.setAttribute('uuid', feature.uuid);
			
			if(feature.optional)
				featureModelNode.setAttribute('type','optional') ;
			else
				featureModelNode.setAttribute('type','mandatory') ;
			
			if(feature.alternative){
				placeToAdd= xmlDoc.createElement('alternative');
				featureModelNode.appendChild(placeToAdd);
			}
			if(feature.orgroup){
				placeToAdd= xmlDoc.createElement('orgroup');
				featureModelNode.appendChild(placeToAdd);
			}
			
			for(var ccntr=0; ccntr<feature.children.length; ccntr++)
				placeToAdd.appendChild(seralizeFeature(feature.children[ccntr]));
			
			return featureModelNode;

		}
		
		function serializeIntegrityConstraints(feature){
			var ICSNode = xmlDoc.createElement('integrityconstraints');
			
			for(var icCntr=0;icCntr<featureModel.integrityConstraints.length; icCntr++){
				var curIC = featureModel.integrityConstraints[icCntr];
				
				
				var ICNode = xmlDoc.createElement('integrityconstraint');
				ICNode.setAttribute('type',curIC.type) ;
				ICNode.setAttribute('source',curIC.source) ;
				ICNode.setAttribute('target',curIC.target) ;
				
				ICSNode.appendChild(ICNode);
			}
			
			return ICSNode;
			
			

		}
		
		function serializeAnnotations(){
			var AnnsNode = xmlDoc.createElement('annotations');
			AnnsNode.setAttribute('baseURI', featureModel.baseURI);
			
			var featureList = featureModel.getAllFeatures();
			
			for(var icCntr=0;icCntr<featureList.length; icCntr++){
				var curAnn = featureModel.annotations[featureList[icCntr].uuid];
				
				
				var AnnNode = xmlDoc.createElement('annotation');
				AnnNode.setAttribute('feature',featureList[icCntr].name) ;
				
				var EntitiesNode =  xmlDoc.createElement('entities');
				
				for(var encntr=0; encntr<curAnn.entities.length;encntr++){
					varCurEntity = xmlDoc.createElement('entity');
					varCurEntity.setAttribute('name',curAnn.entities[encntr].name.name) ;
					varCurEntity.setAttribute('type',curAnn.entities[encntr].type.uri) ;
					varCurEntity.setAttribute('io',curAnn.entities[encntr].io) ;
					
					EntitiesNode.appendChild(varCurEntity);
				}
				
				AnnNode.appendChild(EntitiesNode);
				
				

				var preconditionsNode =  xmlDoc.createElement('precondition');
				var factsNode = xmlDoc.createElement('facts');
				
				for(var pccntr=0; pccntr<curAnn.preconditions.length;pccntr++){
					varCurEntity = xmlDoc.createElement('fact');
					varCurEntity.setAttribute('fact',curAnn.preconditions[pccntr].fact.uri) ;
					if(curAnn.preconditions[pccntr].arguments[0].uri.startsWith(featureModel.baseURI))
						varCurEntity.setAttribute('firstEntity',curAnn.preconditions[pccntr].arguments[0].uri.slice(featureModel.baseURI.length)) ;
					else
						varCurEntity.setAttribute('firstEntity',curAnn.preconditions[pccntr].arguments[0].uri) ;
					if(curAnn.preconditions[pccntr].arguments[1].uri.startsWith(featureModel.baseURI))
						varCurEntity.setAttribute('secondEntity',curAnn.preconditions[pccntr].arguments[1].uri.slice(featureModel.baseURI.length)) ;
					else
						varCurEntity.setAttribute('secondEntity',curAnn.preconditions[pccntr].arguments[1].uri) ;
					
					
					factsNode.appendChild(varCurEntity);
				}
				preconditionsNode.appendChild(factsNode);
				AnnNode.appendChild(preconditionsNode);
				
				
				
				var effectsNode =  xmlDoc.createElement('effect');
				var factsNode = xmlDoc.createElement('facts');
				
				for(var efcntr=0; efcntr<curAnn.effects.length;efcntr++){
					varCurEntity = xmlDoc.createElement('fact');
					varCurEntity.setAttribute('fact',curAnn.effects[efcntr].fact.uri) ;
					if(curAnn.effects[efcntr].arguments[0].uri.startsWith(featureModel.baseURI))
						varCurEntity.setAttribute('firstEntity',curAnn.effects[efcntr].arguments[0].uri.slice(featureModel.baseURI.length)) ;
					else
						varCurEntity.setAttribute('firstEntity',curAnn.effects[efcntr].arguments[0].uri) ;
					if(curAnn.effects[efcntr].arguments[1].uri.startsWith(featureModel.baseURI))
						varCurEntity.setAttribute('secondEntity',curAnn.effects[efcntr].arguments[1].uri.slice(featureModel.baseURI.length)) ;
					else
						varCurEntity.setAttribute('secondEntity',curAnn.effects[efcntr].arguments[1].uri) ;
					
					
					factsNode.appendChild(varCurEntity);
				}
				effectsNode.appendChild(factsNode);
				AnnNode.appendChild(effectsNode);
				
				
				AnnsNode.appendChild(AnnNode);
			}
			
			return AnnsNode;
			
			

		}
		
		
		var xmlDoc = document.implementation.createDocument(null, 'featureModel');
		var featureModelNode= xmlDoc.childNodes[0];
		
		
		featureModelNode.setAttribute('xmlns:xsi', 'http://www.w3.org/2001/XMLSchema-instance'); 
		featureModelNode.setAttribute('xsi:noNamespaceSchemaLocation', 'http://magus.online/resources/schema/featureModel.xsd');
		
		featureModelNode.appendChild(seralizeFeature(featureModel.rootfeature));
		featureModelNode.appendChild(serializeIntegrityConstraints())
		featureModelNode.appendChild(serializeAnnotations())
		
		return xmlDoc;
		
		
		
		
	}
	
	featureModel.initialize();
	return featureModel;
}



