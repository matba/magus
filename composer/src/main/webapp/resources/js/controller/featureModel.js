function editModalDataClass(featureModel, featureUUID) {
	editModalData = {};
	editModalData.loadFeatureUUID = featureUUID;
	editModalData.loadedFeatureFMRef = featureModel
			.findFeaturebyUUID(featureUUID);
	editModalData.loadedFeatureFMVisRef = editfeatureModelVis
			.findFeaturebyUUID(featureUUID);
	editModalData.entities = featureModel.annotations[featureUUID].entities
			.slice(0);
	editModalData.preconditions = featureModel.annotations[featureUUID].preconditions
			.slice(0);
	editModalData.effects = featureModel.annotations[featureUUID].effects
			.slice(0);
	return editModalData;

}

function loadFMAnnotationDialog(featureUUID) {

	$("#featureEdit_Alert").hide();
	hideEntityAdd();
	hideFactAdd();
	editModalData = editModalDataClass(editFeatureModel, featureUUID);

	$('#featureEdit_featureNameInput').val(
			editModalData.loadedFeatureFMRef.name);

	fillFMAnnotationDialog(editModalData, featureUUID);
}

function fillFMAnnotationDialog(editModalData) {
	var entitiesList = editModalData.entities;
	var pfactList = editModalData.preconditions;
	var efactList = editModalData.effects;
	classes = [ 'label-primary', 'label-success', 'label-info' ]
	$('#featureEdit_entitiesList').empty();
	$
			.each(
					entitiesList,
					function(index, value) {
						$("#featureEdit_entitiesList")
								.append(
										'<span style="display: inline-block;" class="label '
												+ classes[index % 3]
												+ '">'
												+ editFeatureModel
														.toStringEntity(value)
												+ '&nbsp;<a href="#" style="color:white" onclick="removeAnnotation(\'entity\','
												+ index
												+ ');">x</a></span>&nbsp;');
					})
	$('#featureEdit_preconditionsList').empty();
	$
			.each(
					pfactList,
					function(index, value) {
						$("#featureEdit_preconditionsList")
								.append(
										'<span style="display: inline-block;" class="label '
												+ classes[(index + 1) % 3]
												+ '">'
												+ editFeatureModel
														.toStringFact(value)
												+ '&nbsp;<a href="#" style="color:white" onclick="removeAnnotation(\'precondition\','
												+ index
												+ ');">x</a></span>&nbsp;');
					})
	$('#featureEdit_effectsList').empty();
	$
			.each(
					efactList,
					function(index, value) {
						$("#featureEdit_effectsList")
								.append(
										'<span style="display: inline-block;" class="label '
												+ classes[(index + 2) % 3]
												+ '">'
												+ editFeatureModel
														.toStringFact(value)
												+ '&nbsp;<a href="#" style="color:white" onclick="removeAnnotation(\'effect\','
												+ index
												+ ');">x</a></span>&nbsp;');
					});
	generateModelInputList()
}

function addAnnotation(type) {
	if (type == 'entity') {
		hideFactAdd();
		$("#featureEdit_entityAddDiv").show();
	} else {

		varData = [];
		varList = getCurrentStatusVarList();
		$.each(varList, function(index, value) {
			varData.push({
				label : value.name.name,
				value : value.name.uri
			});
		});
		var firstEntityInput = document
				.getElementById("featureEdit_firstEntityInput");
		new Awesomplete(firstEntityInput, {
			minChars : 1,
			maxItems : 15,
			list : varData
		});

		var secondEntityInput = document
				.getElementById("featureEdit_secondEntityInput");
		new Awesomplete(secondEntityInput, {
			minChars : 1,
			maxItems : 15,
			list : varData
		});
		$("#featureEdit_addFact").off();
		if (type == 'precondition') {
			hideEntityAdd();
			$("#featureEdit_factAddDiv").show();
			$("#featureEdit_addFact").click({
				param1 : "precondition"
			}, addFact)
		}
		if (type == 'effect') {
			hideEntityAdd();
			$("#featureEdit_factAddDiv").show();

			$("#featureEdit_addFact").click({
				param1 : "effect"
			}, addFact)
		}
	}
}

function removeAnnotation(type, annotationIndex) {
	if (type == 'entity') {
		editModalData.entities.splice(annotationIndex, 1);
	}
	if (type == 'precondition') {
		editModalData.preconditions.splice(annotationIndex, 1);
	}
	if (type == 'effect') {
		editModalData.effects.splice(annotationIndex, 1);
	}
	fillFMAnnotationDialog(editModalData);

}

function updateFMFeature() {
	editModalData.loadedFeatureFMRef.name = $('#featureEdit_featureNameInput')
			.val();
	editModalData.loadedFeatureFMVisRef.l = $('#featureEdit_featureNameInput')
			.val();
	editFeatureModel.annotations[editModalData.loadFeatureUUID].entities = editModalData.entities;
	editFeatureModel.annotations[editModalData.loadFeatureUUID].preconditions = editModalData.preconditions;
	editFeatureModel.annotations[editModalData.loadFeatureUUID].effects = editModalData.effects;
	editfeatureModelVis.redraw();
	$('#featureEdit').modal('hide');
	$('#annotationbox').remove();
}

function addEntity() {
	function validateEntityType() {
		if ($('#featureEdit_entityTypeInput').val().length == 0) {
			$("#featureEdit_Alert").text('Entity Type cannot be empty!');
			$("#featureEdit_Alert").show();
			return false;
		}
		if (contextModel.indexOfType($('#featureEdit_entityTypeInput').val()) == -1) {
			$("#featureEdit_Alert").text('Entity Type cannot be found!');
			$("#featureEdit_Alert").show();

			return false;
		}

		return true;
	}
	function validateEntityName() {
		if ($('#featureEdit_entityInput').val().length == 0) {
			$("#featureEdit_Alert").text('Entity Name cannot be empty!');
			$("#featureEdit_Alert").show();
			return false;
		}
		if (editFeatureModel.getEntityNamesList().indexOf(
				$('#featureEdit_entityInput').val()) != -1) {
			$("#featureEdit_Alert").text('Entity Name already used!');
			$("#featureEdit_Alert").show();
			return false;
		}

		return true;
	}

	if (validateEntityType() && validateEntityName()) {
		$("#featureEdit_Alert").hide();
		var newEntity = {
			name : {
				name : $('#featureEdit_entityInput').val(),
				uri : editFeatureModel.baseURI + "#"
						+ $('#featureEdit_entityInput').val()
			},
			type : {
				name : utility.getEntityFragment($(
						'#featureEdit_entityTypeInput').val()),
				uri : $('#featureEdit_entityTypeInput').val()
			}
		};
		editModalData.entities.push(newEntity);
		fillFMAnnotationDialog(editModalData);
		hideEntityAdd();

	}

}
function getCurrentStatusVarList() {
	var currentFeatureEntities = editModalData.entities;
	var otherFeatureEntities = editFeatureModel
			.getEntitiesExceptForFeature(editModalData.loadFeatureUUID);
	return contextModel.instances.concat(otherFeatureEntities
			.concat(currentFeatureEntities));
}

function addFact(event) {
	function validateFactType() {
		if ($('#featureEdit_factTypeInput').val().length == 0) {
			$("#featureEdit_Alert").text('Fact Type cannot be empty!');
			$("#featureEdit_Alert").show();
			return false;
		}
		if (contextModel.indexOfFactType($('#featureEdit_factTypeInput').val()) == -1) {
			$("#featureEdit_Alert").text('Fact Type cannot be found!');
			$("#featureEdit_Alert").show();

			return false;
		}

		return true;
	}

	function validateFactEntityName(entityURI, typeURI, controlId) {

		function indexOfEntity(entityURI, entityList) {
			var result = -1;
			$.each(entityList, function(index, value) {

				if (value.name.uri == entityURI)
					result = index;
			});

			return result;

		}
		if (entityURI.length == 0) {
			$("#featureEdit_Alert").text('Fact entity cannot be empty!');
			$("#featureEdit_Alert").show();
			return false;
		}
		var currentStatusVarList = getCurrentStatusVarList();
		var entityIndex = indexOfEntity(entityURI, currentStatusVarList);
		if (entityIndex == -1) {
			$("#featureEdit_Alert").text('Entity Does not Exist!');
			$("#featureEdit_Alert").show();
			return false;
		}
		if (currentStatusVarList[entityIndex].type.uri != typeURI) {
			$("#featureEdit_Alert").text('Entity is not of a correct type!');
			$("#featureEdit_Alert").show();
			return false;
		}

		return true;
	}

	function validateFirstEntity() {
		var controlId = '#featureEdit_firstEntityInput';
		var controlValue = $(controlId).val();
		var factType = contextModel.lookupFactType($(
				'#featureEdit_factTypeInput').val()).arguments[0].uri;
		return validateFactEntityName(controlValue, factType, controlId);
	}
	function validateSecondEntity() {
		var controlId = '#featureEdit_secondEntityInput';
		var controlValue = $(controlId).val();
		var factType = contextModel.lookupFactType($(
				'#featureEdit_factTypeInput').val()).arguments[1].uri;
		return validateFactEntityName(controlValue, factType, controlId);
	}

	type = event.data.param1;
	if (validateFactType() && validateFirstEntity() && validateSecondEntity()) {
		$("#featureEdit_Alert").hide();
		var newFactType = {
			name : utility.getEntityFragment($('#featureEdit_factTypeInput')
					.val()),
			uri : $('#featureEdit_factTypeInput').val()
		};
		var newArgument1 = {
			name : utility.getEntityFragment($('#featureEdit_firstEntityInput')
					.val()),
			uri : $('#featureEdit_firstEntityInput').val()
		};
		var newArgument2 = {
			name : utility
					.getEntityFragment($('#featureEdit_secondEntityInput')
							.val()),
			uri : $('#featureEdit_secondEntityInput').val()
		};

		newFact = {
			fact : newFactType,
			arguments : [ newArgument1, newArgument2 ]
		};
		if (type == 'precondition')
			editModalData.preconditions.push(newFact);
		if (type == 'effect')
			editModalData.effects.push(newFact);

		fillFMAnnotationDialog(editModalData);
		hideFactAdd();
	}

}

function hideEntityAdd() {
	$('#featureEdit_entityTypeInput').val('');
	$('#featureEdit_entityInput').val('');
	$("#featureEdit_entityAddDiv").hide();
}
function hideFactAdd() {
	$('#featureEdit_factTypeInput').val('');
	$('#featureEdit_firstEntityInput').val('');
	$('#featureEdit_secondEntityInput').val('');
	$("#featureEdit_factAddDiv").hide();
}

function hideFactAdd() {
	$('#featureEdit_factTypeInput').val('');
	$('#featureEdit_firstEntityInput').val('');
	$('#featureEdit_secondEntityInput').val('');
	$("#featureEdit_factAddDiv").hide();
}

function transferToConfiguration() {
	configurationFeatureModel = editFeatureModel.clone();
	conffeatureModelVis.parse(configurationFeatureModel.rootfeature);
	usedMashupServices = null;
	usedFeaturesInMashup = null;
	runningFMDistance = 0;
	$('#fmconftabLink').click();
}

function removeFeature(featureUUID, featureNo) {
	editFeatureModel.removeFeature(featureUUID);
	editfeatureModelVis.removeFeature(featureNo);
	$('#annotationbox').remove()
}

function addChild(type, featureUUID, featureNo) {
	if (type == 'mandatory') {
		uuid = editFeatureModel.addChild(featureUUID, 'New Feature', false,
				false, false);
		editfeatureModelVis.addLeaf(featureNo, 'New Feature', false, false,
				false, uuid);

	}
	if (type == 'optional') {
		uuid = editFeatureModel.addChild(featureUUID, 'New Feature', true,
				false, false);
		editfeatureModelVis.addLeaf(featureNo, 'New Feature', true, false,
				true, uuid);
	}
	if (type == 'alternative') {

		editFeatureModel.updateFeature(featureUUID, true, false);
		uuid1 = editFeatureModel.addChild(featureUUID, 'Feature 1', false,
				false, false);
		uuid2 = editFeatureModel.addChild(featureUUID, 'Feature 2', false,
				false, false);
		editfeatureModelVis.updateFeature(featureNo, true, false);
		editfeatureModelVis.addLeaf(featureNo, 'Feature 1', false, false,
				false, uuid1);
		editfeatureModelVis.addLeaf(featureNo, 'Feature 2', false, false,
				false, uuid2);
	}
	if (type == 'orgroup') {
		editFeatureModel.updateFeature(featureUUID, false, true);
		uuid1 = editFeatureModel.addChild(featureUUID, 'Feature 1', false,
				false, false);
		uuid2 = editFeatureModel.addChild(featureUUID, 'Feature 2', false,
				false, false);
		editfeatureModelVis.updateFeature(featureNo, false, true);
		editfeatureModelVis.addLeaf(featureNo, 'Feature 1', false, false,
				false, uuid1);
		editfeatureModelVis.addLeaf(featureNo, 'Feature 2', false, false,
				false, uuid2);
	}

	$('#annotationbox').remove();

}

function downloadFM() {
	var doc = editFeatureModel.serializeToXML();
	var serializer = new XMLSerializer();
	var xmlString = serializer.serializeToString(doc);
	download(xmlString, "featuremodel.xml", "text/xml")
}
function downloadFMSVG() {
	var svgcontent = $("<div />").append($("#featureModelVissvg").clone())
			.html();
	download(svgcontent, "featuremodel.svg", "text/xml")
}

function adaptMashup(serviceIndex, isAvailable) {
	if (!isAvailable) {
		$('#pleaseWaitModal').modal('show');
		logger
				.addToLog('Service has become unavailable. Starting adaptation ...');
		var doc = configurationFeatureModel.serializeToXML();
		var serializer = new XMLSerializer();
		var xmlString = serializer.serializeToString(doc);

		var contextModelSerialized = serializer.serializeToString(contextModel
				.serializeToXML());

		var svXmls = [];
		var availableServices = [];

		$.each(serviceList, function(index, value) {
			svXmls.push(serializer.serializeToString(value.serializeToXML()));
			if (serviceAvailabilityList[index])
				availableServices.push(value.svs.baseURI);
		});

		req = {
			ontologyXml : contextModelSerialized,
			featureModelXml : xmlString,
			serviceAnnotationXmls : svXmls,
			selectedFeatures : requestedFeatures,
			availableServiceURIs : availableServices,
			failedWorkflow : workflowGraphJSON,
			failedBPELXml : bpelXMLDoc,
			failedServiceURI : serviceList[serviceIndex].svs.baseURI

		};
		$
				.ajax({
					type : "POST",
					dataType : "json",
					url : "services/mashupadaptation/adaptmashup",
					contentType : "application/json",
					accepts : "application/json",
					data : JSON.stringify(req)
				})
				.done(
						function(data) {
							// alert( "Service called successfully! " );
							$('#pleaseWaitModal').modal('hide');
							logger.addToLog(data.status);

							if (data.adaptationType == -1) {

								usedMashupServices = null;
								usedFeaturesInMashup = null;
								requestedFeatures = null;
								runningFMDistance = 0;
								clearResultTabs();
								utility.showMessage('Sorry',
										'Something went wrong.');
							}

							if (data.adaptationType == -2) {

								usedMashupServices = [];
								usedFeaturesInMashup = [];
								// requestedFeatures=null;
								runningFMDistance = Number.POSITIVE_INFINITY;
								clearResultTabs();
								utility
										.showMessage(
												'Adaptation Result',
												'The service mashup failure <b> <span class="label label-danger">Can Not Be Addressed</span> </b> by adaptation.');
							}

							if (data.adaptationType == 0) {
								workflowGraphJSON = data.workflowJSON;
								var graph = eval("(" + data.workflowJSON + ")");
								// workflowGraphJSON=graph;
								// console.log(graph);
								drawWorkflow(graph);

								var newdoc = CodeMirror
										.Doc(data.bpelXML, "xml");
								bpelCodeMirror.swapDoc(newdoc);
								bpelXMLDoc = data.bpelXML;

								var bf = bpelFlow();
								xmlDoc = $.parseXML(data.bpelXML);
								$xml = $(xmlDoc);
								var x = $xml.find("[name='main']");

								bf.operationNode = bf.graphPosition(x[0]);

								bf.draw();

								$
										.each(
												usedMashupServices,
												function(index, value) {
													if (value == serviceList[serviceIndex].svs.baseURI)
														usedMashupServices[index] = data.replacementServiceURI;
												});

								utility
										.showMessage(
												'Adaptation Result',
												'The service mashup functionality <b> <span class="label label-success">Recovered</span> </b> by adaptation through <b><span class="label label-success"><span class="glyphicon glyphicon-sort" aria-hidden="true"></span> Service Replacement</span></b>. The failed service <b>'
														+ serviceList[serviceIndex].svs.name
														+ '</b> has been replaced with <b>'
														+ data.replacementServiceName
														+ '</b>.Please see the result tabs for updated process.');

							}
							if (data.adaptationType == 1) {
								workflowGraphJSON = data.workflowJSON;
								var graph = eval("(" + data.workflowJSON + ")");
								// workflowGraphJSON=graph;
								// console.log(graph);
								drawWorkflow(graph);

								var newdoc = CodeMirror
										.Doc(data.bpelXML, "xml");
								bpelCodeMirror.swapDoc(newdoc);
								bpelXMLDoc = data.bpelXML;

								var bf = bpelFlow();
								xmlDoc = $.parseXML(data.bpelXML);
								$xml = $(xmlDoc);
								var x = $xml.find("[name='main']");

								bf.operationNode = bf.graphPosition(x[0]);

								bf.draw();

								usedMashupServices = data.usedServiceURIs;

								utility
										.showMessage(
												'Adaptation Result',
												'The service mashup functionality <b> <span class="label label-success">Recovered</span> </b> by adaptation through <b><span class="label label-success"><span class="glyphicon glyphicon-random" aria-hidden="true"></span> Replanning</span></b>. Please see the result tabs for updated process.');

							}

							if (data.adaptationType == 2) {
								data.workflowJSON = data.workflowJSON;
								var graph = eval("(" + data.workflowJSON + ")");
								// workflowGraphJSON=graph;
								// console.log(graph);
								drawWorkflow(graph);

								var newdoc = CodeMirror
										.Doc(data.bpelXML, "xml");
								bpelCodeMirror.swapDoc(newdoc);
								bpelXMLDoc = data.bpelXML;

								var bf = bpelFlow();
								xmlDoc = $.parseXML(data.bpelXML);
								$xml = $(xmlDoc);
								var x = $xml.find("[name='main']");

								bf.operationNode = bf.graphPosition(x[0]);

								bf.draw();

								usedMashupServices = data.usedServiceURIs;
								usedFeaturesInMashup = data.alternateFeatureModel;
								runningFMDistance = data.alternateFMDistance;
								// test
								if ($('#runningfeatureModelVissvg').length == 0) {
									runningfeatureModelVis = featureModelVis(2,
											"runningFMContainer",
											"runningfmtab",
											"runningfeatureModelVissvg",
											"Runnig FM");

								}
								runningfeatureModelVis.selFeatures = usedFeaturesInMashup;
								runningfeatureModelVis.reqFeatures = requestedFeatures;

								runningFeatureModel = configurationFeatureModel
										.clone();
								runningfeatureModelVis
										.parse(runningFeatureModel.rootfeature);

								utility
										.showMessage(
												'Adaptation Result',
												'The effect of failure on service mashup functionality <b> <span class="label label-warning">Mitigated</span> </b> by adaptation through <b><span class="label label-warning"><span class="glyphicon glyphicon-check" aria-hidden="true"></span> Feature Model Reconfiguration</span></b>. Please see the <span class="label label-default">Running FM</span> adapted Featured Model and the result tabs for updated process.');

							}

						})
				.fail(
						function() {
							logger
									.addToLog("Error in calling Adapt Service Mashup Service.");
							$('#pleaseWaitModal').modal('hide');
						});

	} else {
		$('#pleaseWaitModal').modal('show');
		logger
				.addToLog('A Service has become available. Starting the investigation if the service mashup can be improved ...');
		var doc = configurationFeatureModel.serializeToXML();
		var serializer = new XMLSerializer();
		var xmlString = serializer.serializeToString(doc);

		var contextModelSerialized = serializer.serializeToString(contextModel
				.serializeToXML());

		var svXmls = [];
		var availableServices = [];

		$.each(serviceList, function(index, value) {
			svXmls.push(serializer.serializeToString(value.serializeToXML()));
			if (serviceAvailabilityList[index])
				availableServices.push(value.svs.baseURI);
		});

		req = {
			ontologyXml : contextModelSerialized,
			featureModelXml : xmlString,
			serviceAnnotationXmls : svXmls,
			selectedFeatures : requestedFeatures,
			availableServiceURIs : availableServices,
			failedWorkflow : workflowGraphJSON,
			failedBPELXml : bpelXMLDoc,
			failedServiceURI : serviceList[serviceIndex].svs.baseURI

		};
		$
				.ajax({
					type : "POST",
					dataType : "json",
					url : "services/mashupadaptation/adaptmashup",
					contentType : "application/json",
					accepts : "application/json",
					data : JSON.stringify(req)
				})
				.done(
						function(data) {
							// alert( "Service called successfully! " );
							$('#pleaseWaitModal').modal('hide');
							logger.addToLog(data.status);

							if (data.adaptationType == -1) {

								usedMashupServices = null;
								usedFeaturesInMashup = null;
								requestedFeatures = null;
								runningFMDistance = 0;
								clearResultTabs();
								utility.showMessage('Sorry',
										'Something went wrong.');
							}

							if (data.adaptationType == 1) {
								workflowGraphJSON = data.workflowJSON;
								var graph = eval("(" + data.workflowJSON + ")");
								// workflowGraphJSON=graph;
								// console.log(graph);
								drawWorkflow(graph);

								var newdoc = CodeMirror
										.Doc(data.bpelXML, "xml");
								bpelCodeMirror.swapDoc(newdoc);
								bpelXMLDoc = data.bpelXML;

								var bf = bpelFlow();
								xmlDoc = $.parseXML(data.bpelXML);
								$xml = $(xmlDoc);
								var x = $xml.find("[name='main']");

								bf.operationNode = bf.graphPosition(x[0]);

								bf.draw();

								usedMashupServices = data.usedServiceURIs;
								usedFeaturesInMashup = requestedFeatures;
								runningFMDistance = 0;
								// test
								if ($('#runningfeatureModelVissvg').length == 0) {
									runningfeatureModelVis = featureModelVis(2,
											"runningFMContainer",
											"runningfmtab",
											"runningfeatureModelVissvg",
											"Runnig FM");

								}
								runningfeatureModelVis.selFeatures = usedFeaturesInMashup;
								runningfeatureModelVis.reqFeatures = requestedFeatures;

								runningFeatureModel = configurationFeatureModel
										.clone();
								runningfeatureModelVis
										.parse(runningFeatureModel.rootfeature);

								utility
										.showMessage(
												'Adaptation Result',
												'The service mashup functionality <b> <span class="label label-success">Recovered</span> </b> by adaptation through <b><span class="label label-success"><span class="glyphicon glyphicon-random" aria-hidden="true"></span> Replanning</span></b>. Please see the result tabs for updated process.');

							}

							if ((data.adaptationType == 2)) {

								if ((runningFMDistance > data.alternateFMDistance)) {

									data.workflowJSON = data.workflowJSON;
									var graph = eval("(" + data.workflowJSON
											+ ")");
									// workflowGraphJSON=graph;
									// console.log(graph);
									drawWorkflow(graph);

									var newdoc = CodeMirror.Doc(data.bpelXML,
											"xml");
									bpelCodeMirror.swapDoc(newdoc);
									bpelXMLDoc = data.bpelXML;

									var bf = bpelFlow();
									xmlDoc = $.parseXML(data.bpelXML);
									$xml = $(xmlDoc);
									var x = $xml.find("[name='main']");

									bf.operationNode = bf.graphPosition(x[0]);

									bf.draw();

									usedMashupServices = data.usedServiceURIs;
									usedFeaturesInMashup = data.alternateFeatureModel;
									runningFMDistance = data.alternateFMDistance;
									// test
									if ($('#runningfeatureModelVissvg').length == 0) {
										runningfeatureModelVis = featureModelVis(
												2, "runningFMContainer",
												"runningfmtab",
												"runningfeatureModelVissvg",
												"Runnig FM");

									}
									runningfeatureModelVis.selFeatures = usedFeaturesInMashup;
									runningfeatureModelVis.reqFeatures = requestedFeatures;

									runningFeatureModel = configurationFeatureModel
											.clone();
									runningfeatureModelVis
											.parse(runningFeatureModel.rootfeature);

									utility
											.showMessage(
													'Adaptation Result',
													'The effect of failure on service mashup functionality <b> <span class="label label-warning">Mitigated</span> </b> by adaptation through <b><span class="label label-warning"><span class="glyphicon glyphicon-check" aria-hidden="true"></span> Feature Model Reconfiguration</span></b>. Please see the <span class="label label-default">Running FM</span> adapted Featured Model and the result tabs for updated process.');
								} else {
									logger
											.addToLog("The mashup cannot be improved using the service has newly become available.");
								}
							}

						})
				.fail(
						function() {
							logger
									.addToLog("Error in calling Adapt Service Mashup Service.");
							$('#pleaseWaitModal').modal('hide');
						});

	}
}

function generateMashup() {

	if (!serviceMashupFamilyLoaded) {
		logger.addToLog('No Service Mashup Family has been loaded...');
		return false;

	}

	var selectedFeatures = conffeatureModelVis.getSelectedFeatureListUUID();
	logger.addToLog('Validating the feature model configuration...');
	var validationResult = configurationFeatureModel
			.validateConfiguration(selectedFeatures);

	if (!validationResult.success) {
		logger.addToLog(validationResult.reason)
	} else {
		$('#pleaseWaitModal').modal('show');
		var doc = configurationFeatureModel.serializeToXML();
		var serializer = new XMLSerializer();
		var xmlString = serializer.serializeToString(doc);

		var contextModelSerialized = serializer.serializeToString(contextModel
				.serializeToXML());

		var svXmls = [];
		var availableServices = [];

		$.each(serviceList, function(index, value) {
			svXmls.push(serializer.serializeToString(value.serializeToXML()));
			if (serviceAvailabilityList[index])
				availableServices.push(value.svs.baseURI);
		});

		req = {
			ontologyXml : contextModelSerialized,
			featureModelXml : xmlString,
			serviceAnnotationXmls : svXmls,
			selectedFeatures : selectedFeatures,
			availableServiceURIs : availableServices
		};
		$
				.ajax({
					type : "POST",
					dataType : "json",
					url : "services/mashupgeneration/generatemashup",
					contentType : "application/json",
					accepts : "application/json",
					data : JSON.stringify(req)
				})
				.done(
						function(data) {
							// alert( "Service called successfully! " );
							logger.addToLog(data.status);

							if ((data.workflowJSON != null)
									&& (data.workflowJSON.length > 0)) {
								workflowGraphJSON = data.workflowJSON
								var graph = eval("(" + data.workflowJSON + ")");
								// workflowGraphJSON=graph;
								// console.log(graph);
								drawWorkflow(graph);

							}

							if ((data.bpelXML != null)
									&& (data.bpelXML.length > 0)) {
								var newdoc = CodeMirror
										.Doc(data.bpelXML, "xml");
								bpelCodeMirror.swapDoc(newdoc);
								bpelXMLDoc = data.bpelXML;

								var bf = bpelFlow();
								xmlDoc = $.parseXML(data.bpelXML);
								$xml = $(xmlDoc);
								var x = $xml.find("[name='main']");

								bf.operationNode = bf.graphPosition(x[0]);

								bf.draw();

								usedMashupServices = data.usedServiceURIs;
								usedFeaturesInMashup = selectedFeatures;
								runningFMDistance = 0;
								requestedFeatures = selectedFeatures;

								if ($('#runningfeatureModelVissvg').length == 0) {
									runningfeatureModelVis = featureModelVis(2,
											"runningFMContainer",
											"runningfmtab",
											"runningfeatureModelVissvg",
											"Runnig FM");

								}
								runningfeatureModelVis.selFeatures = selectedFeatures;
								runningfeatureModelVis.reqFeatures = requestedFeatures;

								runningFeatureModel = configurationFeatureModel
										.clone();
								runningfeatureModelVis
										.parse(runningFeatureModel.rootfeature);
							}

							$('#pleaseWaitModal').modal('hide');
							// $("#numOfChars").html(data.numChars);
							// $("#suggestUsername").html(data.userName);
						})
				.fail(
						function() {
							logger
									.addToLog("Error in calling Generate Service Mashup Service.");
							$('#pleaseWaitModal').modal('hide');
						});

	}

}

function generateMashupNew() {

	var selectedFeatures = conffeatureModelVis.getSelectedFeatureListUUID();
	logger.addToLog('Validating the feature model configuration...');
	var validationResult = configurationFeatureModel
			.validateConfiguration(selectedFeatures);

	if (!validationResult.success) {
		logger.addToLog(validationResult.reason)
	} else {
		$('#pleaseWaitModal').modal('show');

		req = {
			mashupFamilyURI : serviceMashupFamily.uri,
			selectedFeaturesUuids : selectedFeatures
		};
		$
				.ajax({
					type : "POST",
					dataType : "json",
					url : "services/mashupcreation/generate",
					contentType : "application/json",
					accepts : "application/json",
					data : JSON.stringify(req)
				})
				.done(
						function(data) {
							$('#pleaseWaitModal').modal('hide');
							// alert( "Service called successfully! " );
							logger.addToLog(data.statusMessage);
							if (data.statusCode != 0) {
								return;
							}

							if ((data.workflowJSON != null)
									&& (data.workflowJSON.length > 0)) {
								workflowGraphJSON = data.workflowJSON
								var graph = eval("(" + data.workflowJSON + ")");
								// workflowGraphJSON=graph;
								// console.log(graph);
								drawWorkflow(graph);

							}

							if ((data.bpelCodeXml != null)
									&& (data.bpelCodeXml.length > 0)) {
								var newdoc = CodeMirror.Doc(data.bpelCodeXml,
										"xml");
								bpelCodeMirror.swapDoc(newdoc);
								bpelXMLDoc = data.bpelCodeXml;

								var bf = bpelFlow();
								xmlDoc = $.parseXML(data.bpelCodeXml);
								$xml = $(xmlDoc);
								var x = $xml.find("[name='main']");

								bf.operationNode = bf.graphPosition(x[0]);

								bf.draw();

								usedMashupServices = data.usedServiceURIs;
								usedFeaturesInMashup = selectedFeatures;
								runningFMDistance = 0;
								requestedFeatures = selectedFeatures;

								if ($('#runningfeatureModelVissvg').length == 0) {
									runningfeatureModelVis = featureModelVis(2,
											"runningFMContainer",
											"runningfmtab",
											"runningfeatureModelVissvg",
											"Runnig FM");

								}
								runningfeatureModelVis.selFeatures = selectedFeatures;
								runningfeatureModelVis.reqFeatures = requestedFeatures;

								runningFeatureModel = configurationFeatureModel
										.clone();
								runningfeatureModelVis
										.parse(runningFeatureModel.rootfeature);
							}

							$('#generatedMashupAddress').val(
									data.mashupInstanceUri);
							$('#successfulGenerationModal').modal('show');
							// $("#numOfChars").html(data.numChars);
							// $("#suggestUsername").html(data.userName);
						})
				.fail(
						function() {
							logger
									.addToLog("Error in calling Generate Service Mashup Service.");
							$('#pleaseWaitModal').modal('hide');
						});

	}

}
