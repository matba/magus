//...Global Vars
var editFeatureModel =null;
var configurationFeatureModel =  null;
var runningFeatureModel =  null;
var editModalData =null;
var contextModel= null;
var conffeatureModelVis=null;
var editfeatureModelVis= null;
var runningfeatureModelVis=null;
var serviceList = [];
var serviceAvailabilityList=[];
var utility = null;
var logger = null;
var serviceMashupFamily =null;
var serviceMashupFamilyLoaded = false;
var bpelXMLDoc= null;
var workflowGraphJSON=null;
var usedMashupServices=null;
var usedFeaturesInMashup = null;
var requestedFeatures = null;
var runningFMDistance  = 0;

var serviceMashupList = [{name:'Order Processing',url:'repositories/orderprocessing/configuration.xml'},{name:'Upload Image',url:'repositories/uploadimage/configuration.xml'}];



var selectedSMComboIndex = -1;

if(mashupfamilyAddress!=null){
	serviceMashupList.push({name:"Link Open",url:mashupfamilyAddress});
	
}


utility = utilityClass();
logger = loggerClass();

function pageLoad(){
	refreshSMCombo();
	
	
}


function refreshSMCombo(){
	$('#SMSelectionUL').empty();
	$.each(serviceMashupList,function( index, value ) {
	$('#SMSelectionUL').append('<li><a onclick="SMComboSelected('+index+')" href="#">'+serviceMashupList[index].name+'('+serviceMashupList[index].url+')</a></li>');
	});
	$('#SMSelectionUL').append('<li role="separator" class="divider"></li>');
	$('#SMSelectionUL').append('<li><a data-toggle="modal" data-target="#modalSMAdd" href="#">New...</a></li>');
}

function SMComboSelected(index){
	selectedSMComboIndex=index;
	$('#SMSelectionCombo').empty();
	$('#SMSelectionCombo').html(serviceMashupList[selectedSMComboIndex].name+'<span class="caret"></span>');
}


function loadSMFamily(url,name){
	serviceMashupList.push({name:name,url:url});
	refreshSMCombo();
	
}

function loadSMClick(){
	
	if(selectedSMComboIndex==-1){
		logger.addToLog("Error: No Service Mashup Family has been selected.");
		return;
	}
	$('#pleaseWaitModal').modal('show');
	clearResultTabs();
	usedMashupServices=null;
	runningFMDistance =0;
	usedFeaturesInMashup=null;
	requestedFeatures=null;
	$.ajax({
	    type: "GET",
	    url: serviceMashupList[selectedSMComboIndex].url,
		contextType: "text/plain",
	    dataType: "text",
	    success: function(data){
	    	var xml;

			xml =data;	
			
			serviceMashupFamily =serviceMashupFamilyClass()
			
			serviceMashupFamily.parse(xml,serviceMashupList[selectedSMComboIndex].url)
			
			
			logger.addToLog("Configuration Loaded: "+  serviceMashupList[selectedSMComboIndex].url);
			
			loadContextOntolgoies();
			
			
			
	  },
	  error: function() {
		  logger.addToLog("There was an error loading Configuration file.");
		  $('#pleaseWaitModal').modal('hide');
	  }
	  });
	
	



	

	
}

function loadFeatureModel(){
	$.ajax({
	    type: "GET",
	    url: serviceMashupFamily.featureModelAddress,
		contextType: "text/plain",
	    dataType: "text",
	    success: function(data){
	      var xml;

		  xml =data;	
		  
		  editFeatureModel= featureModelClass();
		  configurationFeatureModel= featureModelClass();
		  if(conffeatureModelVis==null){
		  conffeatureModelVis= featureModelVis(1,"conftabFMContainer","fmconftab","conffeatureModelVissvg","Configuration Model");
		  editfeatureModelVis= featureModelVis(0,"edittabFMContainer","fmedittab","featureModelVissvg","Edit Model");
		  }
		  editFeatureModel.parse(xml);
		  configurationFeatureModel= editFeatureModel.clone();
		  
		  $('#integrityConstraints').empty();
		  
		  $.each(configurationFeatureModel.integrityConstraints, function(index,value){
			  $('#integrityConstraints').append('<span>'+value.source+'&nbsp;<i>'+value.type+'</i>&nbsp;'+value.target+'</span><br/>');
		  });
		  
		  logger.addToLog("Feature Model Loaded: "+ serviceMashupFamily.featureModelAddress);
		  
		  editfeatureModelVis.parse(editFeatureModel.rootfeature);
		  conffeatureModelVis.parse(configurationFeatureModel.rootfeature);
		  
		  logger.addToLog("Feature Model Parsed for Visual Representation. ");
		  
		  $('#contextModelModal').show();
		  fillContextModel();
		  
		  
		  loadServices();
			
	  },
	  error: function() {
		  logger.addToLog("There was an error loading feature model file");
		  $('#pleaseWaitModal').modal('hide');
	  }
	  });
}

function loadContextOntolgoies(){
	
	
	$.ajax({
	    type: "GET",
	    url: serviceMashupFamily.contextOntologyAddresses[0],
		contextType: "text/plain",
	    dataType: "text",
	    success: function(data){
	    	var xml;

			xml =data;	
			
			contextModel =contextClass();
			
			contextModel.parse(xml);
			
			logger.addToLog("Context Model Ontology Loaded: "+ serviceMashupFamily.contextOntologyAddresses[0]);
			
			generateModelInputList();
			
			
			loadFeatureModel();
			
			
			
	  },
	  error: function() {
		  logger.addToLog("There was an error loading Context Model Ontology file.");
		  $('#pleaseWaitModal').modal('hide');
	  }
	  });
	
}

function loadServiceIteration(index){
	
	if(index == serviceMashupFamily.serviceAddresses.length)
	{
		serviceMashupFamilyLoaded =true;
		 $('#pleaseWaitModal').modal('hide');
		return;
	}
		
	
	$.ajax({
	    type: "GET",
	    url: serviceMashupFamily.serviceAddresses[index],
		contextType: "text/plain",
	    dataType: "text",
	    success: function(data){
	    	var xml;

			xml =data;	
			
			var service =serviceClass();
			
			service.parse(xml);
			
			serviceList.push(service);
			serviceAvailabilityList.push(true);
			
			var template = $('#serviceTemplate').html();
			Mustache.parse(template);   // optional, speeds up future uses
			var view ={
					no: index
					
			}
			var rendered = Mustache.render(template, view);
			$('#serviceAnnotationContainer').append(rendered);
			
			fillServices(service,index)
			
			logger.addToLog("Service Loaded: "+ serviceMashupFamily.serviceAddresses[index]);
			
			loadServiceIteration(index+1);
			
	  },
	  error: function() {
		  logger.addToLog("There was an error loading Service file.");
		  $('#pleaseWaitModal').modal('hide');
	  }
	  });
}

function loadServices(){
	$("#serviceAnnotationContainer").empty();
	$("#serviceAnnotationtabToolbar").show();
	serviceList =[];
	serviceAvailabilityList =[];
	loadServiceIteration(0);
}



function addServiceMashupFamily(){
	var newSMName = $('#modalSMAdd_Name').val();
	var newSMFMURI = $('#modalSMAdd_URI').val();
	var newSMCMURI = $('#modalSMAdd_CMURI').val();
	
	if(newSMName.length==0){
		$('#modalSMAdd_errorBox').text("Feauture Model Root Feature Name Cannot be empty");
		$('#modalSMAdd_errorBox').show();
		return;
	}
	if(!utility.isAbsoluteAddress(newSMFMURI)){
		$('#modalSMAdd_errorBox').text("Feature Model base URI is not valid.");
		$('#modalSMAdd_errorBox').show();
		return;
	}
	if(!utility.isAbsoluteAddress(newSMCMURI)){
		$('#modalSMAdd_errorBox').text("Context Model base URI is not valid.");
		$('#modalSMAdd_errorBox').show();
		return;
	}
	$('#modalSMAdd_errorBox').hide();
	
	serviceMashupList.push({name:newSMName,url:""});
	refreshSMCombo();
	SMComboSelected(serviceMashupList.length-1);
	
	contextModel =contextClass();
	contextModel.baseURI = newSMCMURI;
	
	$('#contextModelModal').show();
	fillContextModel();
	
	editFeatureModel= featureModelClass();
	editFeatureModel.baseURI= newSMFMURI;
	
	
	var feature ={}
	feature.name = newSMName;
	feature.uuid = utility.guid()
	feature.optional = false;
	
	feature.alternative = false;
	feature.orgroup = false;
	feature.children = [];
	editFeatureModel.rootfeature = feature;
	
	var annotation = {};
	annotation.entities =[];
	annotation.preconditions =[];
	annotation.effects =[];
	annotation.feature=newSMName;
	editFeatureModel.annotations[feature.uuid]=annotation;
	
	configurationFeatureModel= editFeatureModel.clone();
	if(conffeatureModelVis==null){
	  conffeatureModelVis= featureModelVis(true,"conftabFMContainer","fmconftab","conffeatureModelVissvg","Configuration Model");
	  editfeatureModelVis= featureModelVis(false,"edittabFMContainer","fmedittab","featureModelVissvg","Edit Model");
	}
	
	$('#integrityConstraints').empty();
	  
	  
	  
	  
	  
	editfeatureModelVis.parse(editFeatureModel.rootfeature);
	conffeatureModelVis.parse(configurationFeatureModel.rootfeature);
	
	editfeatureModelVis.redraw();
	conffeatureModelVis.redraw();
	
	serviceList =[];
	serviceAvailabilityList = [];
	$("#serviceAnnotationContainer").empty();
	$("#serviceAnnotationtabToolbar").show();
	  
	logger.addToLog("New Service Mashup Family created: "+ newSMName);
	
	
	$('#modalSMAdd_Name').val('');
	$('#modalSMAdd_URI').val('');
	$('#modalSMAdd_CMURI').val('');
	
	$('#modalSMAdd').modal('hide');
	
}






pageLoad();





