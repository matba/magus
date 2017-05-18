/**
 * 
 */
function fillServices(sv,idx){
	
	
	
	var service = sv.svs;
	fillServiceInputs(sv,idx);
	fillServiceOutputs(sv,idx);
	fillServiceVars(sv,idx);
	
	fillServicePreconditions(sv,idx);
	fillServiceEffects(sv,idx);

	
	
	if(service.receiveService!=null){
		$('#serviceEdit_callbackDiv'+String(idx)).show();
		
		
		fillServiceCOutputs(sv,idx);
		fillServiceCPreconditions(sv,idx)
		fillServiceCEffects(sv,idx);
		
		
	}
	
	
	
	
}

function fillServiceInputs(sv,idx){
	classes=['label-primary','label-success','label-info'];
	var service = sv.svs;
	var inputList = service.inputList;
	$('#serviceEdit_Name'+String(idx)).text(service.name);
	
	
	$('#serviceEdit_inputList'+String(idx)).empty();
	$.each(inputList,function( index, value ) {
		$( "#serviceEdit_inputList"+String(idx) ).append( '<span style="display:inline-block" class="label '+classes[index%3]+'">'+ sv.toStringEntity(service.inputs[value])+'&nbsp;<a href="#" style="color:white" onclick="removeFromService(\'input\','+idx+','+index+');">x</a></span>&nbsp;' );
	});
	
}
function fillServiceOutputs(sv,idx){
	classes=['label-primary','label-success','label-info'];
	var service = sv.svs;
	
	var outputList = service.outputList;
	$('#serviceEdit_outputList'+String(idx)).empty();
	$.each(outputList,function( index, value ) {
		$( "#serviceEdit_outputList"+String(idx) ).append( '<span style="display:inline-block"  class="label '+classes[(index+1)%3]+'">'+sv.toStringEntity(service.outputs[value])+'&nbsp;<a href="#" style="color:white" onclick="removeFromService(\'output\','+idx+','+index+');">x</a></span>&nbsp;' );
	});
	
}
function fillServiceVars(sv,idx){
	classes=['label-primary','label-success','label-info'];
	var service = sv.svs;
	var varList = service.varList;
	$('#serviceEdit_varList'+String(idx)).empty();
	$.each(varList,function( index, value ) {
		$( "#serviceEdit_varList"+String(idx) ).append( '<span style="display:inline-block"  class="label '+classes[(index+2)%3]+'">'+sv.toStringEntity(service.vars[value])+'&nbsp;<a href="#" style="color:white" onclick="removeFromService(\'var\','+idx+','+index+');">x</a></span>&nbsp;' );
	});
}
function fillServicePreconditions(sv,idx){
	classes=['label-primary','label-success','label-info'];
	var service = sv.svs;
	
	var preconditionList = service.precList;
	$('#serviceEdit_preconditionList'+String(idx)).empty();
	$.each(preconditionList,function( index, value ) {
		$( "#serviceEdit_preconditionList" +String(idx)).append( '<span style="display:inline-block" class="label '+classes[index%3]+'">'+sv.toStringFact(value)+'&nbsp;<a href="#" style="color:white" onclick="removeFromService(\'precondition\','+idx+','+index+');">x</a></span>&nbsp;' );
	});
}
function fillServiceEffects(sv,idx){
	classes=['label-primary','label-success','label-info'];
	var service = sv.svs;
	
	var effectList = service.effectList;
	$('#serviceEdit_effectList'+String(idx)).empty();
	$.each(effectList,function( index, value ) {
		$( "#serviceEdit_effectList"+String(idx) ).append( '<span style="display:inline-block"  class="label '+classes[(index+1)%3]+'">'+sv.toStringFact(value)+'&nbsp;<a href="#" style="color:white" onclick="removeFromService(\'effect\','+idx+','+index+');">x</a></span>&nbsp;' );
	});
	
	
}
function fillServiceCOutputs(sv,idx){
	classes=['label-primary','label-success','label-info'];
	var service = sv.svs;
	var callbackOutputList=[];
	callbackOutputList=service.receiveService.outputList;
	
	$('#serviceEdit_callbackName'+String(idx)).text(service.receiveService.name);
	$('#serviceEdit_callbackoutputList'+String(idx)).empty();
	$.each(callbackOutputList,function( index, value ) {
		$( "#serviceEdit_callbackoutputList" +String(idx)).append( '<span style="display:inline-block"  class="label '+classes[(index+1)%3]+'">'+sv.toStringEntity(service.receiveService.outputs[value])+'&nbsp;<a href="#" style="color:white" onclick="removeFromService(\'coutput\','+idx+','+index+');">x</a></span>&nbsp;' );
	});
	
}
function fillServiceCPreconditions(sv,idx){
	classes=['label-primary','label-success','label-info'];
	var service = sv.svs;
	var callbackPreconditionList = [];
	callbackPreconditionList = service.receiveService.precList;
	$('#serviceEdit_callbackpreconditionList'+String(idx)).empty();
	$.each(callbackPreconditionList,function( index, value ) {
		$( "#serviceEdit_callbackpreconditionList"+String(idx) ).append( '<span style="display:inline-block"  class="label '+classes[(index+2)%3]+'">'+sv.toStringFact(value)+'&nbsp;<a href="#" style="color:white" onclick="removeFromService(\'cprecondition\','+idx+','+index+');">x</a></span>&nbsp;' );
	});
}
function fillServiceCEffects(sv,idx){
	classes=['label-primary','label-success','label-info'];
	var service = sv.svs;
	var callbackEffectList = [];
	callbackEffectList = service.receiveService.effectList;
	$('#serviceEdit_callbackeffectList'+String(idx)).empty();
	$.each(callbackEffectList,function( index, value ) {
		$( "#serviceEdit_callbackeffectList" +String(idx)).append( '<span style="display:inline-block"  class="label '+classes[(index+3)%3]+'">'+sv.toStringFact(value)+'&nbsp;<a href="#" style="color:white" onclick="removeFromService(\'ceffect\','+idx+','+index+');">x</a></span>&nbsp;' );
	});
}

function removeFromService(type,serviceIndex, idx){
	if(type=='input'){
		delete serviceList[serviceIndex].svs.inputs[serviceList[serviceIndex].svs.inputList[idx]];
		serviceList[serviceIndex].svs.inputList.splice(idx, 1);
		fillServiceInputs(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='output'){
		delete serviceList[serviceIndex].svs.outputs[serviceList[serviceIndex].svs.outputList[idx]];
		serviceList[serviceIndex].svs.outputList.splice(idx, 1);
		fillServiceOutputs(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='var'){
		delete serviceList[serviceIndex].svs.vars[serviceList[serviceIndex].svs.varList[idx]];
		serviceList[serviceIndex].svs.varList.splice(idx, 1);
		fillServiceVars(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='precondition'){
		serviceList[serviceIndex].svs.precList.splice(idx, 1);
		fillServicePreconditions(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='effect'){
		serviceList[serviceIndex].svs.effectList.splice(idx, 1);
		fillServiceEffects(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='coutput'){
		delete serviceList[serviceIndex].svs.receiveService.outputs[serviceList[serviceIndex].svs.receiveService.outputList[idx]];
		serviceList[serviceIndex].svs.receiveService.outputList.splice(idx, 1);
		fillServiceCOutputs(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='cprecondition'){
		serviceList[serviceIndex].svs.receiveService.precList.splice(idx, 1);
		fillServiceCPreconditions(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='ceffect'){
		serviceList[serviceIndex].svs.receiveService.effectList.splice(idx, 1);
		fillServiceCEffects(serviceList[serviceIndex],serviceIndex);
	}
	
}

function setUpServiceIOAdd(serviceIndex, typeControlID, containerDivID){
	var varData = [];
	varList = contextModel.contextTypes;
	$.each(varList,function( index, value ) {
		varData.push({label: value.name, value:value.uri});
	});
	var firstEntityInput = document.getElementById(typeControlID+serviceIndex);
	new Awesomplete(firstEntityInput, {
		minChars: 1,
		maxItems: 15,
		list:varData
	});
	$(containerDivID+serviceIndex).show();
}

function setUpServiceAnnotation(serviceIndex, typeControlID, firstParamControlID,secondParamControlID, containerDivID){
	var varData = [];
	varList = contextModel.contextFactTypes;
	$.each(varList,function( index, value ) {
		varData.push({label: value.type.name, value:value.type.uri});
	});
	var factTypeInput = document.getElementById(typeControlID+serviceIndex);
	new Awesomplete(factTypeInput, {
		minChars: 1,
		maxItems: 15,
		list:varData
	});
	
	var serviceVars = [];
	$.each(serviceList[serviceIndex].svs.inputList,function( index, value ) {
		serviceVars.push({label: serviceList[serviceIndex].svs.inputs[value].name.name , value:serviceList[serviceIndex].svs.inputs[value].name.uri});
	});
	$.each(serviceList[serviceIndex].svs.outputList,function( index, value ) {
		serviceVars.push({label: serviceList[serviceIndex].svs.outputs[value].name.name , value:serviceList[serviceIndex].svs.outputs[value].name.uri});
	});
	$.each(serviceList[serviceIndex].svs.varList,function( index, value ) {
		serviceVars.push({label: serviceList[serviceIndex].svs.vars[value].name.name , value:serviceList[serviceIndex].svs.vars[value].name.uri});
	});
	$.each(contextModel.instances,function( index, value ) {
		serviceVars.push({label: value.name.name , value:value.name.uri});
	});
	if(serviceList[serviceIndex].svs.receiveService!=null){
		$.each(serviceList[serviceIndex].svs.receiveService.outputList,function( index, value ) {
			serviceVars.push({label: serviceList[serviceIndex].svs.receiveService.outputs[value].name.name , value:serviceList[serviceIndex].svs.receiveService.outputs[value].name.uri});
		});
	}
	
	var firstArgumentInput = document.getElementById(firstParamControlID+serviceIndex);
	new Awesomplete(firstArgumentInput, {
		minChars: 1,
		maxItems: 15,
		list:serviceVars
	});
	
	var secondArgumentInput = document.getElementById(secondParamControlID+serviceIndex);
	new Awesomplete(secondArgumentInput, {
		minChars: 1,
		maxItems: 15,
		list:serviceVars
	});
	
	
	$(containerDivID+serviceIndex).show();
}

function addToServiceAnnotation(type,serviceIndex){
	if(type=='input'){
		
		setUpServiceIOAdd(serviceIndex, "serviceEdit_inputTypeInput", "#serviceEdit_InputAddDiv");
		
	}
	if(type=='output'){
		setUpServiceIOAdd(serviceIndex, "serviceEdit_outputTypeInput", "#serviceEdit_outputAddDiv");
	}
	if(type=='var'){
		setUpServiceIOAdd(serviceIndex, "serviceEdit_varTypeInput", "#serviceEdit_VarAddDiv");
		
	}
	if(type=='precondition'){
		setUpServiceAnnotation(serviceIndex,  "serviceEdit_preconditionFactTypeInput", "serviceEdit_preconditionFirstParameterInput","serviceEdit_preconditionSecondParameterInput", "#serviceEdit_PreconditionAddDiv");
			
	}
	if(type=='effect'){
		setUpServiceAnnotation(serviceIndex,  "serviceEdit_effectfactTypeInput", "serviceEdit_effectFirstParameterInput","serviceEdit_effectSecondParameterInput", "#serviceEdit_EffectAddDiv");
		
	}
	if(type=='coutput'){
		setUpServiceIOAdd(serviceIndex, "serviceEdit_callbackOutputTypeInput", "#serviceEdit_callbackOutputAddDiv");
	}
	if(type=='cprecondition'){
		setUpServiceAnnotation(serviceIndex,  "serviceEdit_callbackPreconditionFactTypeInput", "serviceEdit_callbackPreconditionFirstParameterInput","serviceEdit_callbackPreconditionSecondParameterInput", "#serviceEdit_callbackPreconditionAddDiv");
		
		
	}
	if(type=='ceffect'){
		setUpServiceAnnotation(serviceIndex,  "serviceEdit_callbackEffectFactTypeInput", "serviceEdit_callbackEffectFirstParameterInput","serviceEdit_callbackEffectSecondParameterInput", "#serviceEdit_callbackEffectAddDiv");
	}
	
	
}

function commitServiceIOAdd(serviceIndex,nameControlID, typeControlID, containerDivID,addArray,addDic){
	var newInstanceTypeInp = $(typeControlID+serviceIndex).val();
	var newInstanceNameInp =$(nameControlID+serviceIndex).val();
	
	
	if((newInstanceNameInp.length==0)|| !utility.isAbsoluteAddress(newInstanceTypeInp) )
		return;
			
			
	var newInstanceName ={name: newInstanceNameInp ,uri: serviceList[serviceIndex].svs.baseURI+'#'+  newInstanceNameInp};
	var newInstanceType = {name: utility.getEntityFragment( newInstanceTypeInp) ,uri:  newInstanceTypeInp};
	
	
	var newInstance ={type:newInstanceType, name:newInstanceName};
	
	addArray.push(newInstanceName.uri);
	addDic[newInstanceName.uri] = newInstance;
	
	$(typeControlID+serviceIndex).val('');
	$(nameControlID+serviceIndex).val('');
	$(containerDivID+serviceIndex).hide();
	
}

function commitServicePreEff(serviceIndex, notControlID ,typeControlID, firstParamControlID,secondParamControlID, containerDivID,addArray){
	var newFactTypeInp = $(typeControlID+serviceIndex).val();
	var newArgument1 =$(firstParamControlID+serviceIndex).val();
	var newArgument2 =$(secondParamControlID+serviceIndex).val();
	
	
	if(!utility.isAbsoluteAddress(newFactTypeInp)|| !utility.isAbsoluteAddress(newArgument1) || !utility.isAbsoluteAddress(newArgument2))
		return;
			
	
	var newFactType = {name: utility.getEntityFragment( newFactTypeInp) ,uri:  newFactTypeInp};
	var newArgument1 = {name: utility.getEntityFragment( newArgument1) ,uri:  newArgument1};
	var newArgument2 = {name: utility.getEntityFragment( newArgument2) ,uri:  newArgument2};
	
	var newFactType ={not: $(notControlID+serviceIndex).is(':checked'),type:newFactType, arguments:[newArgument1,newArgument2]};
	
	addArray.push(newFactType);
	
	$(typeControlID+serviceIndex).val('');
	$(firstParamControlID+serviceIndex).val('');
	$(secondParamControlID+serviceIndex).val('');
	$(containerDivID+serviceIndex).hide();
	
}

function commitServiceAnnotation(type,serviceIndex){
	if(type=='input'){
		
		commitServiceIOAdd(serviceIndex,"#serviceEdit_inputNameInput" ,"#serviceEdit_inputTypeInput", "#serviceEdit_InputAddDiv",serviceList[serviceIndex].svs.inputList,serviceList[serviceIndex].svs.inputs);
		fillServiceInputs(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='output'){
		commitServiceIOAdd(serviceIndex,"#serviceEdit_outputNameInput" ,"#serviceEdit_outputTypeInput", "#serviceEdit_outputAddDiv",serviceList[serviceIndex].svs.outputList,serviceList[serviceIndex].svs.outputs);
		fillServiceOutputs(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='var'){
		commitServiceIOAdd(serviceIndex,"#serviceEdit_varNameInput" ,"#serviceEdit_varTypeInput", "#serviceEdit_VarAddDiv",serviceList[serviceIndex].svs.varList,serviceList[serviceIndex].svs.vars);
		fillServiceVars(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='precondition'){
		commitServicePreEff(serviceIndex, "#serviceEdit_preconditionIsNot", "#serviceEdit_preconditionFactTypeInput", "#serviceEdit_preconditionFirstParameterInput","#serviceEdit_preconditionSecondParameterInput", "#serviceEdit_PreconditionAddDiv",serviceList[serviceIndex].svs.precList);
		fillServicePreconditions(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='effect'){
		commitServicePreEff(serviceIndex,"#serviceEdit_effectIsNot",  "#serviceEdit_effectfactTypeInput", "#serviceEdit_effectFirstParameterInput","#serviceEdit_effectSecondParameterInput", "#serviceEdit_EffectAddDiv",serviceList[serviceIndex].svs.effectList);
		fillServiceEffects(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='coutput'){
		commitServiceIOAdd(serviceIndex,"#serviceEdit_callbackOutputNameInput" ,"#serviceEdit_callbackOutputTypeInput", "#serviceEdit_callbackOutputAddDiv",serviceList[serviceIndex].svs.receiveService.outputList,serviceList[serviceIndex].svs.receiveService.outputs);
		fillServiceCOutputs(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='cprecondition'){
		commitServicePreEff(serviceIndex, "#serviceEdit_callbackPreconditionIsNot" , "#serviceEdit_callbackPreconditionFactTypeInput", "#serviceEdit_callbackPreconditionFirstParameterInput","#serviceEdit_callbackPreconditionSecondParameterInput", "#serviceEdit_callbackPreconditionAddDiv",serviceList[serviceIndex].svs.receiveService.precList);
		fillServiceCPreconditions(serviceList[serviceIndex],serviceIndex);
	}
	if(type=='ceffect'){
		commitServicePreEff(serviceIndex,"#serviceEdit_callbackEffectIsNot" , "#serviceEdit_callbackEffectFactTypeInput", "#serviceEdit_callbackEffectFirstParameterInput","#serviceEdit_callbackEffectSecondParameterInput", "#serviceEdit_callbackEffectAddDiv",serviceList[serviceIndex].svs.receiveService.effectList);
		fillServiceCEffects(serviceList[serviceIndex],serviceIndex);
	}
	
	
}

function downloadService(idx){
	var doc = serviceList[idx].serializeToXML();
	var serializer = new XMLSerializer();
	var xmlString = serializer.serializeToString(doc);
	download(xmlString,serviceList[idx].svs.name+ ".xml", "text/xml")
}

function addService(){
	var newServiceName = $('#modalServiceAdd_Name').val();
	var newServiceURI = $('#modalServiceAdd_URI').val();
	
	if(newServiceName.length==0){
		$('#modalServiceAdd_errorBox').text("Service Name Cannot be empty");
		$('#modalServiceAdd_errorBox').show();
		return;
	}
	if(!utility.isAbsoluteAddress(newServiceURI)){
		$('#modalServiceAdd_errorBox').text("Service base URI is not valid.");
		$('#modalServiceAdd_errorBox').show();
		return;
	}
	$('#modalServiceAdd_errorBox').hide();
	var newService = serviceClass();
	newService.svs = newService.emptyService(newServiceName,newServiceURI,null);
	
	if($('#modalServiceAdd_hasCallback').is(':checked')){
		newService.svs.receiveService = newService.emptyService(newServiceName+'Callback',newServiceURI,newService.svs);
	}
	
	serviceList.push(newService)
	serviceAvailabilityList.push(true)
	var index = serviceList.length-1;
	var template = $('#serviceTemplate').html();
	Mustache.parse(template);   // optional, speeds up future uses
	var view ={
			no: index
			
	}
	var rendered = Mustache.render(template, view);
	$('#serviceAnnotationContainer').append(rendered);
	
	fillServices(newService,index);
	
	 $('#modalServiceAdd_Name').val('');
	 $('#modalServiceAdd_URI').val('');
	$('#modalServiceAdd').modal('hide');
	utility.sleep(1000).then(() => {
	$('#serviceAnnotationtab' ).scrollTop($('#serviceAnnotationtab')[0].scrollHeight);
	});
}

function toggleServiceAvailability(sno){
	if(sno<0)
		return;
	serviceAvailabilityList[sno]= !serviceAvailabilityList[sno];
	if(serviceAvailabilityList[sno]){
		$('#serviceEdit_AvailableBtn'+sno).css('background-color','green');
		$('#serviceEdit_AvailableBtn'+sno).html('<span class="glyphicon glyphicon-ok" aria-hidden="true" ></span> Available');
		
	}else{
		$('#serviceEdit_AvailableBtn'+sno).css('background-color','red');
		$('#serviceEdit_AvailableBtn'+sno).html('<span class="glyphicon glyphicon-remove" aria-hidden="true" ></span> Unavailable');
	}
	
	if(!serviceAvailabilityList[sno]&&usedMashupServices.indexOf(serviceList[sno].svs.baseURI)!=-1){
		adaptMashup(sno, serviceAvailabilityList[sno]);
	}
	if(serviceAvailabilityList[sno]){
		if(runningFMDistance >0 ){
			adaptMashup(sno, serviceAvailabilityList[sno]);
		}
	}
}
function toggleServiceAvailabilityGraph(sno){
	if(sno<0)
		return;
	$('#serviceAnnotationlink').click(); 
	$('#serviceEdit_serviceModal'+sno)[0].scrollIntoView(true)
	//$('#serviceAnnotationtab').scrollTo('#serviceEdit_serviceModal'+sno);
	utility.sleep(300).then(() => {
		toggleServiceAvailability(sno);
	});
	
	
}