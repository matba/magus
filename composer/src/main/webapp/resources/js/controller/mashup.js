function loadServiceMashupInstance(uri) {
	if (runningMashupInstance == null) {
		$('#editorselector').modal('hide');
		$('#pleaseWaitModal').modal('show');
	}
	mashupInstance = mashupClass(uri);

	$
			.ajax({
				url : uri + "/mashupInfo.txt",
				dataType : "text",
				success : function(data) {
					mashupInstance.readConfiguration(data);
					readRelatedServiceMashupFamily();

				}
			})
			.fail(
					function() {
						logger
								.addToLog("The specified URI does not refer to a service mashup instance.");
						$('#pleaseWaitModal').modal('hide');
					});

}

function readRelatedServiceMashupFamily() {
	$
			.ajax({
				type : "GET",
				url : mashupInstance.familyUri + "?" + utility.guid(),
				contextType : "text/plain",
				dataType : "text",
				success : function(data) {
					var xml;

					xml = data;

					serviceMashupFamily = serviceMashupFamilyClass(mashupInstance.familyUri);

					serviceMashupFamily.parse(xml, mashupInstance.familyUri)
					readFeatureModelFile();
					if (runningMashupInstance != null) {
						loadServicesForNfPropertySpecificationIteration(0);
					}

				},
				error : function() {
					logger
							.addToLog("There was an error loading service mashup family configuration file for mashup instance.");
					$('#pleaseWaitModal').modal('hide');
				}
			});
}

function readFeatureModelFile() {
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

					mashupInstance.featureModel = featureModelClass();
					if (adaptationFeatureModelVis == null) {
						adaptationFeatureModelVis = featureModelVis(3,
								"adaptTabFMContainer",
								"adaptRequirementsContainer",
								"adaptFeatureModelVisSvg", "Requirement Model");
					}
					if (runningMashupInstance != null) {
						adaptationFeatureModelVis.criticalFeatures = runningMashupInstance.criticalFeatures;
						adaptationFeatureModelVis.disableCriticalFeatureToggle = true;
					}

					mashupInstance.featureModel.parse(xml);

					adaptationFeatureModelVis.selFeatures = mashupInstance.selectedFeatures;

					adaptationFeatureModelVis
							.parse(mashupInstance.featureModel.rootfeature);

					if (runningMashupInstance != null) {
						$('#runningfmtabLink').show();
						fillRunningFMTab();
					}
					readMashupInstanceBpel();
				},
				error : function() {
					logger
							.addToLog("There was an error loading feature model file for the service mashup instance.");
					$('#pleaseWaitModal').modal('hide');
				}
			});
}

function readMashupInstanceBpel() {
	let bpeluri = mashupInstance.uri + "/bpel.xml";
	if (runningMashupInstance != null) {
		bpeluri = runningMashupInstance.uri + "/bpel.xml";
	}
	$
			.ajax({
				type : "GET",
				url : bpeluri + "?" + utility.guid(),
				contextType : "text/plain",
				dataType : "text",
				success : function(data) {
					mashupInstance.bpelXml = data;
					var newdoc = CodeMirror.Doc(data, "xml");
					bpelCodeMirror.swapDoc(newdoc);
					bpelXMLDoc = data;

					var bf = bpelFlow();
					xmlDoc = $.parseXML(data);
					$xml = $(xmlDoc);
					var x = $xml.find("[name='main']");

					bf.operationNode = bf.graphPosition(x[0]);

					bf.draw();

					mashupInstancePostLoadOperations()

				},
				error : function() {
					logger
							.addToLog("There was an error loading Bpel colde for the service mashup instance.");
					$('#pleaseWaitModal').modal('hide');
				}
			});
}
function mashupInstancePostLoadOperations() {
	$('#pleaseWaitModal').modal('hide');
	// $('#runningfmtabLink').show();
	$('#bpelGraphTabLink').show();
	$('#bpelCodeTabLink').show();
	$('#adaptationRequirementsTabLink').show();
	$('#contextStateModelTabLink').show();
	$('#adaptationRequirementsTabLink').click();

}

var nfRequirementCounter = 0;

function getNfUnit(code) {
	switch (code) {
	case 'RELIABILITY':
		return '/1';
	case 'EXECUTION_TIME':
		return 'ms';
	case 'COST':
		return '$';
	}

	return '';
}

function getNfText(code) {
	switch (code) {
	case 'RELIABILITY':
		return 'Reliability';
	case 'EXECUTION_TIME':
		return 'Response Time';
	case 'COST':
		return 'Invocation Cost';
	}
	return 'Unknown Nunfunctional Property';
}

function getNeutralValue(code) {
	switch (code) {
	case 'RELIABILITY':
		return 1;
	case 'EXECUTION_TIME':
		return 0;
	case 'COST':
		return 0;
	}
	return 0;
}

function getNfRelationText(code) {
	switch (code) {
	case 'l':
		return 'lower';
	case 'g':
		return 'greater';
	}
	return 'Unknown Relation';
}

function addNewNonfunctionalConstraints() {
	let template = $('#nfRowTemplate').html();
	Mustache.parse(template); // optional, speeds up future uses
	let view = {
		no : nfRequirementCounter

	}
	let rendered = Mustache.render(template, view);
	let curReqNo = nfRequirementCounter;
	$('#nfRequirementsRows').append(rendered);

	$('#nfRequirementTypeList' + curReqNo + ' li').on(
			'click',
			function() {
				$('#nfRequirementTypeList' + curReqNo).attr('data-selected',
						$(this).attr('data-code'));
				$('#nfRequirementType' + curReqNo).text(
						$(this).attr('data-text'));
				$('#nfRequirementUnit' + curReqNo).text(
						getNfUnit($(this).attr('data-code')));
			});

	$('#nfRequirementRelationList' + nfRequirementCounter + ' li').on(
			'click',
			function() {
				$('#nfRequirementRelationList' + curReqNo).attr(
						'data-selected', $(this).attr('data-code'));
				$('#nfRequirementRelation' + curReqNo).text(
						$(this).attr('data-text'));
			});

	$('#nfRowRemove' + curReqNo).on('click', function() {
		$('#nfRequirementRow' + curReqNo).remove();
	});
	nfRequirementCounter++;
}

function registerNonfunctionalRequirements() {
	runningMashupInstance = {};
	runningMashupInstance.mashupInstanceUri = mashupInstance.uri;
	runningMashupInstance.criticalFeatureUUIDs = adaptationFeatureModelVis.criticalFeatures;
	runningMashupInstance.constraints = [];

	for (let cntr = 0; cntr < nfRequirementCounter; cntr++) {
		if ($('#nfRequirementRow' + cntr).length) {
			runningMashupInstance.constraints.push({
				nonfunctionalPropertyID : $('#nfRequirementTypeList' + cntr)
						.attr('data-selected'),
				value : $('#nfRequirementThreshold' + cntr).val(),
				relation : $('#nfRequirementRelationList' + cntr).attr(
						'data-selected')
			});

		}
	}

	$('#pleaseWaitModal').modal('show');

	$
			.ajax({
				type : "POST",
				dataType : "json",
				url : "services/mashupadaptation/register",
				contentType : "application/json",
				accepts : "application/json",
				data : JSON.stringify(runningMashupInstance),
				success : function(data) {
					$('#pleaseWaitModal').modal('hide');
					logger.addToLog(data.statusMessage);
					if (data.statusCode != 0) {
						return;
					}
					runningMashupInstance.uri = data.runningInstanceUri;

					$('#successfulRegisterModel').modal('show');
					$('#runningMashupInstanse').val(data.runningInstanceUri);
					disableNfEdit();
					loadServicesForNfPropertySpecificationIteration(0);
					$('#runningfmtabLink').show();
					mashupStatus = {};
					mashupStatus.isWorking = true;
					mashupStatus.providedFeatures = mashupInstance.selectedFeatures;
					mashupStatus.currentNfProperties = [];
					for (let nfCntr = 0; nfCntr < runningMashupInstance.constraints.length; nfCntr++) {
						mashupStatus.currentNfProperties
								.push({
									nonfunctionalPropertyID : runningMashupInstance.constraints[nfCntr].nonfunctionalPropertyID,
									value : getNeutralValue(runningMashupInstance.constraints[nfCntr].nonfunctionalPropertyID)
								})
					}
					fillRunningFMTab();

				},
				error : function() {
					logger
							.addToLog("There was an error saving mashup requirements.");
					$('#pleaseWaitModal').modal('hide');
				}
			});

}

function loadRunningMashupInstance(uri) {
	// loadRequestInfo -> loadMashupState
	// -> loadContextStateModel-> loadMashupInstance ->
	// loadMashupFamilyConfiguration -> loadFeatureModel

	$('#editorselector').modal('hide');
	$('#pleaseWaitModal').modal('show');
	$
			.ajax({
				type : "GET",
				url : uri + "/requestInformation.xml" + "?" + utility.guid(),
				contextType : "text/plain",
				dataType : "text",
				success : function(data) {
					// $('#pleaseWaitModal').modal('hide');
					runningMashupInstance = runningMashupClass();
					runningMashupInstance.uri = uri;
					runningMashupInstance.parseConfiguration(data);
					loadContextStateModel();
					addNonfunctionalConstraintRows(runningMashupInstance.constraints);

				},
				error : function() {
					logger
							.addToLog("There was an error loading service service mashup running instance.");
					$('#pleaseWaitModal').modal('hide');
				}
			});

}

function loadContextStateModel() {
	$
			.ajax({
				type : "GET",
				url : runningMashupInstance.uri + "/contextStateModel.xml"
						+ "?" + utility.guid(),
				contextType : "text/plain",
				dataType : "text",
				success : function(data) {
					contextStateModel = contextStateModelClass();
					contextStateModel.parse(data);
					loadMashupStatus()
				},
				error : function() {
					logger
							.addToLog("There was an error loading service service mashup running instance.");
					$('#pleaseWaitModal').modal('hide');
				}
			});

}

function loadMashupStatus() {
	$
			.ajax({
				type : "GET",
				url : runningMashupInstance.uri + "/mashupStatus.xml" + "?"
						+ utility.guid(),
				contextType : "text/plain",
				dataType : "text",
				success : function(data) {
					mashupStatus = mashupStatusClass();
					mashupStatus.parse(data);
					loadServiceMashupInstance(runningMashupInstance.mashupInstanceUri);
				},
				error : function() {
					logger
							.addToLog("There was an error loading service service mashup running instance.");
					$('#pleaseWaitModal').modal('hide');
				}
			});
}

function addNonfunctionalConstraintRows(constraints) {
	for (let cntr = 0; cntr < constraints.length; cntr++) {
		addNewNonfunctionalConstraints();
		let index = nfRequirementCounter - 1;

		$('#nfRequirementTypeList' + index).attr('data-selected',
				constraints[cntr].nonfunctionalPropertyID);
		$('#nfRequirementType' + index).text(
				getNfText(constraints[cntr].nonfunctionalPropertyID));
		$('#nfRequirementUnit' + index).text(
				getNfUnit(constraints[cntr].nonfunctionalPropertyID));

		$('#nfRequirementRelationList' + index).attr('data-selected',
				constraints[cntr].relation);
		$('#nfRequirementRelation' + index).text(
				getNfRelationText(constraints[cntr].relation));

		$('#nfRequirementThreshold' + cntr).val(constraints[cntr].value);
	}

	disableNfEdit();

}

function disableNfEdit() {
	$('#registerMashupInstanceButton').remove();

	for (let cntr = 0; cntr < nfRequirementCounter; cntr++) {
		if ($('#nfRequirementRow' + cntr).length) {

			$('#nfRequirementThreshold' + cntr).prop("readonly", true);
			$('#nfRequirementTypeList' + cntr + ' li').off('click');
			$('#nfRequirementRelationList' + cntr + ' li').off('click');
			$('#nfRowRemove' + cntr).hide();
		}
	}
	$('#addNewNonfunctionalConstraintsButton').hide();

}

function loadServicesForNfPropertySpecificationIteration(index) {

	if (index == serviceMashupFamily.serviceAddresses.length) {
		return;
	}

	$
			.ajax({
				type : "GET",
				url : serviceMashupFamily.serviceAddresses[index] + "?"
						+ utility.guid(),
				contextType : "text/plain",
				dataType : "text",
				success : function(data) {
					let xml;

					xml = data;

					let service = serviceClass();

					service.parse(xml);

					serviceList.push(service);

					let template = $('#nfValueTemplate').html();
					Mustache.parse(template); // optional, speeds up future
					// uses

					let view = {
						no : index,
						serviceName : service.svs.name

					}
					let rendered = Mustache.render(template, view);
					$('#nfValueFormContainer').append(rendered);

					serviceAvailabilityList.push(true);
					if ((contextStateModel != null)
							&& (contextStateModel.availableServices
									.indexOf(service.svs.baseURI) == -1)) {
						toggleContextServiceAvailability(index);

					}

					let nfInputTemplate = $('#nfValueInputTemplate').html();
					Mustache.parse(nfInputTemplate); // optional, speeds up
					// future

					for (let nfcntr = 0; nfcntr < runningMashupInstance.constraints.length; nfcntr++) {
						let nfCode = runningMashupInstance.constraints[nfcntr].nonfunctionalPropertyID;
						let inputView = {
							no : index,
							nfText : getNfText(nfCode),
							code : nfCode,
							nfUnit : getNfUnit(nfCode),
						}
						let inputRendered = Mustache.render(nfInputTemplate,
								inputView);
						$('#contextState_Form' + index).append(inputRendered);
						if (contextStateModel == null) {
							$('#nfRequirementValue' + index + '_' + nfCode)
									.val(getNeutralValue(nfCode));
						} else {
							let serviceNfValue = 0;
							for (let svCntr = 0; svCntr < contextStateModel.serviceNfProperties.length; svCntr++) {
								let curService = contextStateModel.serviceNfProperties[svCntr];
								if (curService.uri == service.svs.baseURI) {
									for (let svNfCntr = 0; svNfCntr < curService.nfs.length; svNfCntr++) {
										if (curService.nfs[svNfCntr].id == nfCode) {
											serviceNfValue = curService.nfs[svNfCntr].value;
											break;
										}
									}
									break;
								}
							}

							$('#nfRequirementValue' + index + '_' + nfCode)
									.val(serviceNfValue);
						}
					}

					loadServicesForNfPropertySpecificationIteration(index + 1);

				},
				error : function() {
					logger
							.addToLog("There was an error loading Service files for the service mashup family.");
					$('#pleaseWaitModal').modal('hide');
				}
			});
}

function toggleContextServiceAvailability(sno) {
	if (sno < 0)
		return;
	serviceAvailabilityList[sno] = !serviceAvailabilityList[sno];
	if (serviceAvailabilityList[sno]) {
		$('#contextState_ServiceAvailableBtn' + sno).css('background-color',
				'green');
		$('#contextState_ServiceAvailableBtn' + sno)
				.html(
						'<span class="glyphicon glyphicon-ok" aria-hidden="true" ></span> Available');

	} else {
		$('#contextState_ServiceAvailableBtn' + sno).css('background-color',
				'red');
		$('#contextState_ServiceAvailableBtn' + sno)
				.html(
						'<span class="glyphicon glyphicon-remove" aria-hidden="true" ></span> Unavailable');
	}

}

function updateMashupContextState() {
	let req = {
		mashupRunningInstanceUri : runningMashupInstance.uri,
		contextStateModel : {
			serviceStateModel : []
		}
	}
	for (let svsCntr = 0; svsCntr < serviceList.length; svsCntr++) {
		let svCsm = {
			serviceURI : serviceList[svsCntr].svs.baseURI,
			isAvailable : serviceAvailabilityList[svsCntr],
			nonfunctionalProperties : []
		}

		for (let nfcntr = 0; nfcntr < runningMashupInstance.constraints.length; nfcntr++) {
			let svNf = {
				nonfunctionalPropertyID : runningMashupInstance.constraints[nfcntr].nonfunctionalPropertyID,
				value : $(
						'#nfRequirementValue'
								+ svsCntr
								+ '_'
								+ runningMashupInstance.constraints[nfcntr].nonfunctionalPropertyID)
						.val()
			}
			svCsm.nonfunctionalProperties.push(svNf);
		}

		req.contextStateModel.serviceStateModel.push(svCsm);
	}

	$('#pleaseWaitModal').modal('show');

	$
			.ajax({
				type : "POST",
				dataType : "json",
				url : "services/mashupadaptation/updatecontextstatemodel",
				contentType : "application/json",
				accepts : "application/json",
				data : JSON.stringify(req),
				success : function(data) {
					$('#pleaseWaitModal').modal('hide');
					logger.addToLog(data.statusMessage);
					if (data.statusCode != 0) {
						return;
					}

					$('#successfulStateUpdateModal').modal('show');

					if (data.functionalPropertiesSatisfcation) {
						$('#functionRequiremntSatisdactionLabel').text(
								'provides');
						$('#functionRequiremntSatisdactionLabel').removeClass(
								'label-danger');
						$('#functionRequiremntSatisdactionLabel').addClass(
								'label-success');
					} else {
						$('#functionRequiremntSatisdactionLabel').text(
								'dot not provides');
						$('#functionRequiremntSatisdactionLabel').addClass(
								'label-danger');
						$('#functionRequiremntSatisdactionLabel').removeClass(
								'label-success');
					}

					if (!data.adaptationIsRecommended) {
						$('#adaptationRequirmentLabel').text('not required');
						$('#adaptationRequirmentLabel').removeClass(
								'label-danger');
						$('#adaptationRequirmentLabel').addClass(
								'label-success');
						$('#adaptMashupButton').hide();
					} else {
						$('#adaptationRequirmentLabel').text('required');
						$('#adaptationRequirmentLabel')
								.addClass('label-danger');
						$('#adaptationRequirmentLabel').removeClass(
								'label-success');
						$('#adaptMashupButton').show();
					}

					$('#nonfunctionalRequirementSatisfactionDiv').empty();

					mashupStatus.currentNfProperties = [];

					for (let nfCntr = 0; nfCntr < data.nonfunctionaPropetiesSatisfaction.length; nfCntr++) {
						let nfps = data.nonfunctionaPropetiesSatisfaction[nfCntr];
						let satisfied = nfps.nonfunctionalPropertiesSatisfaction;
						let addedElement = '<label>The constraint '
								+ getNfText(nfps.nonfunctionalPropertiesID)
								+ ' is <span class="label label-'
								+ (satisfied ? 'success' : 'danger') + '">'
								+ (satisfied ? '' : 'not ')
								+ 'satisfied </span> and the value for it is '
								+ nfps.value + '</label><br/>';
						$('#nonfunctionalRequirementSatisfactionDiv').append(
								addedElement);

						mashupStatus.currentNfProperties
								.push({
									nonfunctionalPropertyID : nfps.nonfunctionalPropertiesID,
									value : nfps.value
								});

					}
					if (!data.functionalPropertiesSatisfcation) {
						mashupStatus.isWorking = false;
					} else {
						mashupStatus.isWorking = true;
					}

					fillRunningFMTab();

				},
				error : function() {
					logger
							.addToLog("There was an error updating mashup context state model.");
					$('#pleaseWaitModal').modal('hide');
				}
			});

}

function fillRunningFMTab() {
	if ($('#runningfeatureModelVissvg').length == 0) {
		runningfeatureModelVis = featureModelVis(2, "runningFMContainer",
				"runningFMTopContainer", "runningfeatureModelVissvg",
				"Runnig FM");

	}

	runningfeatureModelVis.selFeatures = [];
	if (mashupStatus.isWorking) {
		runningfeatureModelVis.selFeatures = mashupStatus.providedFeatures;
	}

	runningfeatureModelVis.reqFeatures = mashupInstance.selectedFeatures;

	runningFeatureModel = mashupInstance.featureModel.clone();
	runningfeatureModelVis.parse(runningFeatureModel.rootfeature);

	$('#nfRequirementsStateRows').empty();

	if (!mashupStatus.isWorking) {
		$('#nfRequirementsStateRows')
				.append(
						'Service mashup does not have any non-functional property since it does not provide its functional requirements');
		return;
	}

	for (let csCntr = 0; csCntr < runningMashupInstance.constraints.length; csCntr++) {
		let nfName = "unknown";
		let nfValue = "unknown"
		let constraint = "unknown";
		let nfUnit = getNfUnit(runningMashupInstance.constraints[csCntr].nonfunctionalPropertyID);
		let stateLabel = "default";
		for (let nfCntr = 0; nfCntr < mashupStatus.currentNfProperties.length; nfCntr++) {
			if (runningMashupInstance.constraints[csCntr].nonfunctionalPropertyID == mashupStatus.currentNfProperties[nfCntr].nonfunctionalPropertyID) {
				nfName = getNfText(mashupStatus.currentNfProperties[nfCntr].nonfunctionalPropertyID);
				nfValue = mashupStatus.currentNfProperties[nfCntr].value;

				constraint = getNfRelationText(runningMashupInstance.constraints[csCntr].relation)
						+ ' than '
						+ runningMashupInstance.constraints[csCntr].value;

				let satisfied = false;
				if (runningMashupInstance.constraints[csCntr].relation == 'l') {
					satisfied = (mashupStatus.currentNfProperties[nfCntr].value <= runningMashupInstance.constraints[csCntr].value);
				} else {
					satisfied = (mashupStatus.currentNfProperties[nfCntr].value >= runningMashupInstance.constraints[csCntr].value);
				}
				stateLabel = satisfied ? 'success' : 'danger';

				break;

			}
		}

		let template = $('#nfStateRowTemplate').html();
		Mustache.parse(template); // optional, speeds up future uses
		let view = {
			nfName : nfName,
			nfValue : nfValue,
			constraint : constraint,
			nfUnit : nfUnit,
			stateLabel : stateLabel
		}
		let rendered = Mustache.render(template, view);
		let curReqNo = nfRequirementCounter;
		$('#nfRequirementsStateRows').append(rendered);

	}

}

function adaptMashup() {
	$('#successfulStateUpdateModal').modal('hide');
	$('#pleaseWaitModal').modal('show');

	let req = {
		runningMashupInstanceUri : runningMashupInstance.uri
	}

	$
			.ajax({
				type : "POST",
				dataType : "json",
				url : "services/mashupadaptation/adapt",
				contentType : "application/json",
				accepts : "application/json",
				data : JSON.stringify(req),
				success : function(data) {
					$('#pleaseWaitModal').modal('hide');
					logger.addToLog(data.statusMessage);
					if (data.statusCode != 0) {
						mashupStatus.isWorking = false;
						mashupStatus.providedFeatures = [];
						mashupStatus.currentNfProperties = [];
						return;
					}

					let newBpelCode = data.bpelCodeXml;
					let newdoc = CodeMirror.Doc(newBpelCode, "xml");
					bpelCodeMirror.swapDoc(newdoc);
					bpelXMLDoc = newBpelCode;

					let bf = bpelFlow();
					xmlDoc = $.parseXML(newBpelCode);
					$xml = $(xmlDoc);
					let x = $xml.find("[name='main']");

					bf.operationNode = bf.graphPosition(x[0]);

					bf.draw();
					mashupStatus.isWorking = true;
					mashupStatus.providedFeatures = data.providedFeaturesUuidAfterAdaptation;
					mashupStatus.currentNfProperties = data.predictedNonfunctionalAfterAdaptation;
					fillRunningFMTab();

					utility
							.showMessage(
									'Adaptation Successful',
									'The adaptation was successful. The provided features for the alternate service mashup and its nonfunctional properties can be view in <b>Running FM</b> tab. The BPEL code for the replacement service mashup can be seen BPEL code and BPEL graph tabs.s')
				},
				error : function() {
					logger
							.addToLog("There was an error calling adapt mashup service.");
					$('#pleaseWaitModal').modal('hide');
				}
			});

}