function loadServiceMashupFamilyForEdit(uri) {
	loadServiceMashupFamily(uri);
	postLoadActionsSMEdit();
}

function loadServiceMashupFamilyForConfiguration(uri) {
	loadServiceMashupFamily(uri);
	postLoadActionsSMConfiguration();
}

function loadServiceMashupFamily(uri) {
	$('#editorselector').modal('hide');
	$('#pleaseWaitModal').modal('show');
	if (uri == "") {
		logger.addToLog("Error: No Service Mashup Family has been selected.");
		return;
	}
	usedMashupServices = null;
	runningFMDistance = 0;
	usedFeaturesInMashup = null;
	requestedFeatures = null;
	$.ajax({
		type : "GET",
		url : uri + "?" + utility.guid(),
		contextType : "text/plain",
		dataType : "text",
		success : function(data) {
			var xml;

			xml = data;

			serviceMashupFamily = serviceMashupFamilyClass(uri);

			serviceMashupFamily.parse(xml, uri)

			logger.addToLog("Configuration Loaded: " + uri);

			loadContextOntolgoies();

		},
		error : function() {
			logger.addToLog("There was an error loading Configuration file.");
			$('#pleaseWaitModal').modal('hide');
		}
	});
}

function loadFeatureModel() {
	$
			.ajax({
				type : "GET",
				url : serviceMashupFamily.featureModelAddress + "?"
						+ utility.guid(),
				contextType : "text/plain",
				dataType : "text",
				success : function(data) {
					var xml;

					xml = data;

					editFeatureModel = featureModelClass();
					configurationFeatureModel = featureModelClass();
					if (conffeatureModelVis == null) {
						conffeatureModelVis = featureModelVis(1,
								"conftabFMContainer", "fmconftab",
								"conffeatureModelVissvg", "Configuration Model");
						editfeatureModelVis = featureModelVis(0,
								"edittabFMContainer", "fmedittab",
								"featureModelVissvg", "Edit Model");
					}
					editFeatureModel.parse(xml);
					configurationFeatureModel = editFeatureModel.clone();

					$('#integrityConstraints').empty();

					$.each(configurationFeatureModel.integrityConstraints,
							function(index, value) {
								$('#integrityConstraints')
										.append(
												'<span>' + value.source
														+ '&nbsp;<i>'
														+ value.type
														+ '</i>&nbsp;'
														+ value.target
														+ '</span><br/>');
							});

					logger.addToLog("Feature Model Loaded: "
							+ serviceMashupFamily.featureModelAddress);

					editfeatureModelVis.parse(editFeatureModel.rootfeature);
					conffeatureModelVis
							.parse(configurationFeatureModel.rootfeature);

					logger
							.addToLog("Feature Model Parsed for Visual Representation. ");

					$('#contextModelModal').show();
					fillContextModel();

					loadServices();

				},
				error : function() {
					logger
							.addToLog("There was an error loading feature model file");
					$('#pleaseWaitModal').modal('hide');
				}
			});
}

function loadContextOntolgoies() {

	$
			.ajax({
				type : "GET",
				url : serviceMashupFamily.contextOntologyAddresses[0] + "?"
						+ utility.guid(),
				contextType : "text/plain",
				dataType : "text",
				success : function(data) {
					var xml;

					xml = data;

					contextModel = contextClass();

					contextModel.parse(xml);

					logger.addToLog("Context Model Ontology Loaded: "
							+ serviceMashupFamily.contextOntologyAddresses[0]);

					generateModelInputList();

					loadFeatureModel();

				},
				error : function() {
					logger
							.addToLog("There was an error loading Context Model Ontology file.");
					$('#pleaseWaitModal').modal('hide');
				}
			});

}

function loadServiceIteration(index) {

	if (index == serviceMashupFamily.serviceAddresses.length) {
		serviceMashupFamilyLoaded = true;
		$('#pleaseWaitModal').modal('hide');
		return;
	}

	$.ajax({
		type : "GET",
		url : serviceMashupFamily.serviceAddresses[index] + "?"
				+ utility.guid(),
		contextType : "text/plain",
		dataType : "text",
		success : function(data) {
			var xml;

			xml = data;

			var service = serviceClass();

			service.parse(xml);

			serviceList.push(service);
			serviceAvailabilityList.push(true);

			var template = $('#serviceTemplate').html();
			Mustache.parse(template); // optional, speeds up future uses
			var view = {
				no : index

			}
			var rendered = Mustache.render(template, view);
			$('#serviceAnnotationContainer').append(rendered);

			fillServices(service, index)

			logger.addToLog("Service Loaded: "
					+ serviceMashupFamily.serviceAddresses[index]);

			loadServiceIteration(index + 1);

		},
		error : function() {
			logger.addToLog("There was an error loading Service file.");
			$('#pleaseWaitModal').modal('hide');
		}
	});
}

function loadServices() {
	$("#serviceAnnotationContainer").empty();
	$("#serviceAnnotationtabToolbar").show();
	serviceList = [];
	serviceAvailabilityList = [];
	loadServiceIteration(0);

}

function addServiceMashupFamily() {
	$('#editorselector').modal('hide');
	let smUriPrefix = "http://magus.online/user-repositories/" + utility.guid();
	serviceMashupFamily = serviceMashupFamilyClass(smUriPrefix
			+ "/configuration.xml");
	let newSMName = $('#newServiceMashupFamilyName').val();
	let newSMFMURI = smUriPrefix + "/featureModel.xml"
	let newSMCMURI = smUriPrefix + "/contextModel.xml"

	if (newSMName.length == 0) {
		newSMName = "New Service Mashup";
	}

	contextModel = contextClass();
	contextModel.baseURI = newSMCMURI;

	$('#contextModelModal').show();
	fillContextModel();

	editFeatureModel = featureModelClass();
	editFeatureModel.baseURI = newSMFMURI;

	var feature = {}
	feature.name = newSMName;
	feature.uuid = utility.guid()
	feature.optional = false;

	feature.alternative = false;
	feature.orgroup = false;
	feature.children = [];
	editFeatureModel.rootfeature = feature;

	var annotation = {};
	annotation.entities = [];
	annotation.preconditions = [];
	annotation.effects = [];
	annotation.feature = newSMName;
	editFeatureModel.annotations[feature.uuid] = annotation;

	configurationFeatureModel = editFeatureModel.clone();
	if (conffeatureModelVis == null) {
		// conffeatureModelVis = featureModelVis(true, "conftabFMContainer",
		// "fmconftab", "conffeatureModelVissvg", "Configuration Model");
		editfeatureModelVis = featureModelVis(false, "edittabFMContainer",
				"fmedittab", "featureModelVissvg", "Edit Model");
	}

	$('#integrityConstraints').empty();

	editfeatureModelVis.parse(editFeatureModel.rootfeature);
	// conffeatureModelVis.parse(configurationFeatureModel.rootfeature);

	editfeatureModelVis.redraw();
	// conffeatureModelVis.redraw();

	serviceList = [];
	serviceAvailabilityList = [];
	$("#serviceAnnotationContainer").empty();
	$("#serviceAnnotationtabToolbar").show();

	logger.addToLog("New Service Mashup Family created: " + newSMName);
	postLoadActionsSMEdit();
}

function postLoadActionsSMEdit() {
	$('#contextModeltabLink').show();
	$('#serviceAnnotationtabLink').show();
	$('#fmedittabLink').show();
	$('#serviceAnnotationtabLink').click();
	$('#fmedittabLink').click();
}

function postLoadActionsSMConfiguration() {
	$('#workflowtabLink').show();
	$('#bpelGraphTabLink').show();
	$('#bpelCodeTabLink').show();
	$('#fmconftabLink').show();
	$('#fmconftabLink').click();
	$('#bpelCodeTabLink').click();
}

function saveServiceMashupFamily() {
	let featureModel = editFeatureModel.serializeToXML();
	let serializer = new XMLSerializer();
	let featureModelXml = serializer.serializeToString(featureModel);

	let ontologyXml = serializer.serializeToString(contextModel
			.serializeToXML());

	let svXmls = [];

	$.each(serviceList, function(index, value) {
		svXmls.push(serializer.serializeToString(value.serializeToXML()));
	});

	let req = {
		mashupUri : serviceMashupFamily.uri,
		ontologyXml : ontologyXml,
		featureModelXml : featureModelXml,
		serviceAnnotationXmls : svXmls
	}

	$('#pleaseWaitModal').modal('show');
	$('#pleaseWaitState').text('Saving service mashup...')

	$.ajax({
		type : "POST",
		dataType : "json",
		url : "services/mashupcreation/save",
		contentType : "application/json",
		accepts : "application/json",
		data : JSON.stringify(req)
	}).done(function(data) {
		// alert( "Service called successfully! " );
		logger.addToLog(data.statueMessage);
		$('#pleaseWaitModal').modal('hide');
		if (data.statusCode == 0) {

			generateTrainingSet(data.serviceMashupURI);

		}

		// $("#numOfChars").html(data.numChars);
		// $("#suggestUsername").html(data.userName);
	}).fail(function() {
		logger.addToLog("Error in Saving Service Mashup Family.");
		$('#pleaseWaitState').text('');
		$('#pleaseWaitModal').modal('hide');
	});
}

function generateTrainingSet(uri) {
	$('#pleaseWaitState').text(
			'Generating Training Set for Service Mashup Family...');
	let req = {
		mashupFamilyURI : uri
	}

	$
			.ajax({
				type : "POST",
				dataType : "json",
				url : "services/mashupcreation/training",
				contentType : "application/json",
				accepts : "application/json",
				data : JSON.stringify(req)
			})
			.done(function(data) {
				$('#pleaseWaitModal').modal('hide');
				$('#pleaseWaitState').text('');
				logger.addToLog(data.statueMessage);

				if (data.statusCode == 0) {
					$('#savedMashupAddress').val(uri);
					$('#successfulSaveModel').modal('show');
				}

			})
			.fail(
					function() {
						logger
								.addToLog("Error in Generating Training Set Service Mashup Family.");
						$('#pleaseWaitModal').modal('hide');
						$('#pleaseWaitState').text('');
					});
}
