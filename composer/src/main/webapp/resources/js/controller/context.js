function generateModelInputList(){
	entityTypeData = [];
	
	$.each(contextModel.contextTypes,function( index, value ) {
		entityTypeData.push({label: value.name, value:value.uri});
	});
	var entityTypeInput = document.getElementById("featureEdit_entityTypeInput");
	new Awesomplete(entityTypeInput, {
		minChars: 1,
		maxItems: 15,
		list:entityTypeData
	});
	
	factTypeData = [];
	
	$.each(contextModel.contextFactTypes,function( index, value ) {
		factTypeData.push({label: value.type.name, value:value.type.uri});
	});
	var factTypeInput = document.getElementById("featureEdit_factTypeInput");
	new Awesomplete(factTypeInput, {
		minChars: 1,
		maxItems: 15,
		list:factTypeData
	});

	
	
}


function fillContextModel(){
	fillEntityTypes();
	fillInstances();
	fillFactTypes();
	
}

function fillEntityTypes(){
	classes=['label-primary','label-success','label-info']
	var entitiesTypeList = contextModel.contextTypes;
	$('#ontologyEdit_entitieTypeList').empty();
	$.each(entitiesTypeList,function( index, value ) {
		$( "#ontologyEdit_entitieTypeList" ).append( '<span style="display:inline-block" class="label '+classes[index%3]+'">'+value.name+'&nbsp;<a href="#" style="color:white" onclick="removeFromContext(\'entityType\','+index+');">x</a></span>&nbsp;' );
	});
}
function fillInstances(){
	classes=['label-primary','label-success','label-info']
	var instanceList = contextModel.instances;
	$('#ontologyEdit_instanceList').empty();
	$.each(instanceList,function( index, value ) {
		$( "#ontologyEdit_instanceList" ).append( '<span style="display:inline-block"  class="label '+classes[(index+1)%3]+'">'+editFeatureModel.toStringEntity(value)+'&nbsp;<a href="#" style="color:white" onclick="removeFromContext(\'instance\','+index+');">x</a></span>&nbsp;' );
	});
	
}
function fillFactTypes(){
	classes=['label-primary','label-success','label-info']
	var factTypeList = contextModel.contextFactTypes;

	$('#ontologyEdit_factTypeList').empty();
	$.each(factTypeList,function( index, value ) {
		$( "#ontologyEdit_factTypeList" ).append( '<span style="display:inline-block"  class="label '+classes[(index+2)%3]+'">'+contextModel.toStringFact(value)+'&nbsp;<a href="#" style="color:white" onclick="removeFromContext(\'factType\','+index+');">x</a></span>&nbsp;' );
	});
	
}
function downloadCM(){
	var doc = contextModel.serializeToXML();
	var serializer = new XMLSerializer();
	var xmlString = serializer.serializeToString(doc);
	download(xmlString, "context.xml", "text/xml")
}


function removeFromContext(type, idx){
	if(type=='entityType'){
		contextModel.contextTypes.splice(idx, 1);
		fillEntityTypes();
	}
	if(type=='instance'){
		contextModel.instances.splice(idx, 1);
		fillInstances();
	}
	if(type=='factType'){
		contextModel.contextFactTypes.splice(idx, 1);
		fillFactTypes();
	}
	
	
}


function addToContext(type){
	if(type=='entityType'){
		$("#ontologyEdit_entityTypeAddDiv").show();
	}
	if(type=='instance'){
	
	
		
		var varData = [];
		varList = contextModel.contextTypes;
		$.each(varList,function( index, value ) {
			varData.push({label: value.name, value:value.uri});
		});
		var firstEntityInput = document.getElementById("ontologyEdit_entityInstanceTypeInput");
		new Awesomplete(firstEntityInput, {
			minChars: 1,
			maxItems: 15,
			list:varData
		});
		$("#ontologyEdit_InstanceAddDiv").show();
		
		
	}
	if(type=='factType'){
		
		
		var varData = [];
		varList = contextModel.contextTypes;
		$.each(varList,function( index, value ) {
			varData.push({label: value.name, value:value.uri});
		});
		var firstEntityInput = document.getElementById("ontologyEdit_firstEntityTypeInput");
		
		new Awesomplete(firstEntityInput, {
			minChars: 1,
			maxItems: 15,
			list:varData
		});
		
		var secondEntityInput = document.getElementById("ontologyEdit_secondEntityInput");
		
		new Awesomplete(secondEntityInput, {
			minChars: 1,
			maxItems: 15,
			list:varData
		});
		
		$("#ontologyEdit_FactTypeAddDiv").show();
	}
	
}

function commitToContext(type){
	if(type=='entityType'){
		
		var newEntityTypeInp =$('#ontologyEdit_entityTypeInput').val();
		
		if(newEntityTypeInp.length==0 )
			return;
		
		var newEntityType ={name: newEntityTypeInp ,uri: contextModel.baseURI+'#'+ newEntityTypeInp};
		
		contextModel.contextTypes.push(newEntityType)
		
		$('#ontologyEdit_entityTypeInput').val('');
		$("#ontologyEdit_entityTypeAddDiv").hide();
		fillEntityTypes();
		
	}
	if(type=='instance'){
	
		var newInstanceTypeInp = $('#ontologyEdit_entityInstanceTypeInput').val();
		var newInstanceNameInp =$('#ontologyEdit_entityInstanceNameInput').val();
		
		
		if((newInstanceNameInp.length==0)|| !utility.isAbsoluteAddress(newInstanceTypeInp) )
			return;
				
				
		var newInstanceName ={name: newInstanceNameInp ,uri: contextModel.baseURI+'#'+  newInstanceNameInp};
		var newInstanceType = {name: utility.getEntityFragment( newInstanceTypeInp) ,uri:  newInstanceTypeInp};
		
		
		var newInstance ={type:newInstanceType, name:newInstanceName};
		
		contextModel.instances.push(newInstance);
		
		$('#ontologyEdit_entityInstanceTypeInput').val('');
		$('#ontologyEdit_entityInstanceNameInput').val('');
		$("#ontologyEdit_InstanceAddDiv").hide();
		fillInstances();
		
		
	}
	if(type=='factType'){
		
		var newFactTypeInp = $('#ontologyEdit_factTypeInput').val();
		var newArgument1 =$('#ontologyEdit_firstEntityTypeInput').val();
		var newArgument2 =$('#ontologyEdit_secondEntityInput').val();
		
		if((newFactTypeInp.length==0)|| !utility.isAbsoluteAddress(newArgument1) || !utility.isAbsoluteAddress(newArgument2))
			return;
				
				
		var newFactType ={name: newFactTypeInp ,uri: contextModel.baseURI+'#'+  newFactTypeInp};
		var newArgument1 = {name: utility.getEntityFragment( newArgument1) ,uri:  newArgument1};
		var newArgument2 = {name: utility.getEntityFragment( newArgument2) ,uri:  newArgument2};
		
		var newFactType ={type:newFactType, arguments:[newArgument1,newArgument2]};
		
		contextModel.contextFactTypes.push(newFactType);
		
		$('#ontologyEdit_factTypeInput').val('');
		$('#ontologyEdit_firstEntityTypeInput').val('');
		$('#ontologyEdit_secondEntityInput').val('');
		$("#ontologyEdit_FactTypeAddDiv").hide();
		fillFactTypes();
	}
	
}
