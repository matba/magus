//Contains information about a mashup instance
function mashupClass(uri) {
	let mashup = {};
	mashup.uri = uri;
	mashup.familyUri = null;
	mashup.selectedFeatures = [];
	mashup.bpelXml = null;
	mashup.featureModel = null;

	mashup.readConfiguration = function(configuration) {
		let configurationLines = configuration.match(/[^\r\n]+/g);

		$.each(configurationLines, function(index, value) {
			if (index == 0) {
				mashup.familyUri = value;
			} else {
				mashup.selectedFeatures.push(value);

			}
		});
	}

	return mashup;
}

function runningMashupClass() {
	let runningMashup = {};
	runningMashup.uri = null;
	runningMashup.mashupInstanceUri = null;
	runningMashup.criticalFeatures = [];
	runningMashup.constraints = [];

	runningMashup.parseConfiguration = function(xml) {
		let xmlDoc = $.parseXML(xml);
		let $xml = $(xmlDoc);

		let childNodes = $($xml.children()[0]).children()
		runningMashup.mashupInstanceUri = $($xml.children()[0]).attr(
				'instanceUri');
		runningMashup.criticalFeatures = [];
		runningMashup.constraints = [];

		for (let cntr = 0; cntr < childNodes.length; cntr++) {
			if (childNodes[cntr].nodeName == 'criticalFeatures') {
				let cfChilds = $(childNodes[cntr]).children();

				for (let acntr = 0; acntr < cfChilds.length; acntr++) {
					if (cfChilds[acntr].nodeName == "feature") {
						runningMashup.criticalFeatures.push($(cfChilds[acntr])
								.attr('uuid'))
					}
				}

			} else if (childNodes[cntr].nodeName == 'constraints') {
				let cfChilds = $(childNodes[cntr]).children();

				for (let acntr = 0; acntr < cfChilds.length; acntr++) {
					if (cfChilds[acntr].nodeName == "constraint") {
						runningMashup.constraints.push({
							nonfunctionalPropertyID : $(cfChilds[acntr]).attr(
									'id'),
							value : $(cfChilds[acntr]).attr('value'),
							relation : $(cfChilds[acntr]).attr('relation')
						});

					}
				}
			}

		}
	}
	return runningMashup;

}

function contextStateModelClass() {
	let csm = {};
	csm.availableServices = [];
	csm.serviceNfProperties = [];

	csm.parse = function(xml) {
		let xmlDoc = $.parseXML(xml);
		let $xml = $(xmlDoc);

		let childNodes = $($xml.children()[0]).children();
		csm.availableServices = [];
		csm.serviceNfProperties = [];

		for (let cntr = 0; cntr < childNodes.length; cntr++) {
			if (childNodes[cntr].nodeName == 'availability') {
				let cfChilds = $(childNodes[cntr]).children();
				for (let acntr = 0; acntr < cfChilds.length; acntr++) {
					if (cfChilds[acntr].nodeName == "service"
							&& ($(cfChilds[acntr]).attr('available') == 'true')) {
						csm.availableServices.push($(cfChilds[acntr]).attr(
								'uri'));
					}
				}

			} else if (childNodes[cntr].nodeName == 'nonfunctional') {
				let cfChilds = $(childNodes[cntr]).children();
				for (let acntr = 0; acntr < cfChilds.length; acntr++) {
					if (cfChilds[acntr].nodeName == "service") {
						let serviceUri = $(cfChilds[acntr]).attr('uri');
						let serviceNfs = [];
						let sChilds = $(cfChilds[acntr]).children();
						for (let scntr = 0; scntr < sChilds.length; scntr++) {
							if (sChilds[scntr].nodeName == "property") {
								serviceNfs.push({
									id : $(sChilds[scntr]).attr('name'),
									value : $(sChilds[scntr]).attr('value')
								})
							}
						}
						csm.serviceNfProperties.push({
							uri : serviceUri,
							nfs : serviceNfs
						});

					}
				}
			}
		}

	}
	return csm;
}

function mashupStatusClass() {
	let msm = {
		isWorking : false,
		providedFeatures : [],
		currentNfProperties : []
	}

	msm.parse = function(xml) {
		let xmlDoc = $.parseXML(xml);
		let $xml = $(xmlDoc);

		let childNodes = $($xml.children()[0]).children();
		msm.isWorking = ($($xml.children()[0]).attr('working') == 'true');
		msm.providedFeatures = [];
		msm.currentNfProperties = [];

		for (let cntr = 0; cntr < childNodes.length; cntr++) {
			if (childNodes[cntr].nodeName == 'criticalFeatures') {
				let cfChilds = $(childNodes[cntr]).children();

				for (let acntr = 0; acntr < cfChilds.length; acntr++) {
					if (cfChilds[acntr].nodeName == "feature") {
						msm.providedFeatures.push($(cfChilds[acntr]).attr(
								'uuid'))
					}
				}

			} else if (childNodes[cntr].nodeName == 'constraints') {
				let cfChilds = $(childNodes[cntr]).children();

				for (let acntr = 0; acntr < cfChilds.length; acntr++) {
					if (cfChilds[acntr].nodeName == "constraint") {
						msm.currentNfProperties.push({
							nonfunctionalPropertyID : $(cfChilds[acntr]).attr(
									'id'),
							value : $(cfChilds[acntr]).attr('value')
						});

					}
				}
			}
		}
	}

	return msm;
}
