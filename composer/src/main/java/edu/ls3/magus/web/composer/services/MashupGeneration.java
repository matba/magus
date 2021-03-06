package edu.ls3.magus.web.composer.services;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import edu.ls3.magus.web.composer.core.GenerateMashupProcess;
import edu.ls3.magus.web.composer.core.GenerateMashupProcess.GenerationProcessResponse;
import edu.ls3.magus.web.composer.core.GenerateTrainingSetProcess;
import edu.ls3.magus.web.composer.core.SaveMashupProcess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/mashupcreation")
@Api(value = "Mashup Generation Services")
public class MashupGeneration {
	@POST
	@Path("/generate")
	@ApiOperation(value = "Generates a service mashup", notes = "Gets a service mashup family URI and a set of features and return the ID for generated mashup and BPEL code for it.")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MashupGenerationResponse generateMashup(
			@ApiParam(value = "the URI for the service mashup family that this mashup is created from and list of selected features UUIDs.", required = true) MashupGenerationRequest mashupGenerationRequest) {

		GenerateMashupProcess process = new GenerateMashupProcess(mashupGenerationRequest.getMashupFamilyURI(),
				mashupGenerationRequest.getSelectedFeaturesUuids());
		MashupGenerationResponse r = new MashupGenerationResponse();
		try {
			final GenerationProcessResponse gpr = process.generateMashup();
			final String uri = process.saveAndCreateUri(gpr.bpelXml);
			r.statusCode = 0;
			r.statusMessage = "Generating service mashup was successful. The generate mashup instance URI is: " + uri;
			r.bpelCodeXml = gpr.bpelXml;
			r.workflowJSON = gpr.workflowJSON;
			r.mashupInstanceUri = uri;

		} catch (Exception ex) {
			r.statusCode = -1;
			r.statusMessage = "Generating service mashup failed with the following message: " + ex.getMessage();
			r.bpelCodeXml = "";
			r.mashupInstanceUri = "";
			ex.printStackTrace();
		}
		return r;

	}

	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Saves a service mashup", notes = "Gets a service mashup family context model, services annotation, and annotated feature model and saves it and returns a URI for it.")
	public MashupSaveResponse saveMashup(
			@ApiParam(value = "The context model ontology serialized as XML, the annotated feature model Xml and list of services represented as OWLS.", required = true) MashupSaveRequest mashup)
			throws IOException, ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		SaveMashupProcess m = new SaveMashupProcess(mashup.mashupUri, mashup.ontologyXml, mashup.featureModelXml,
				mashup.serviceAnnotationXmls);
		MashupSaveResponse r = new MashupSaveResponse();

		try {
			final String mashupUri = m.saveMashup();
			r.statusCode = 0;
			r.statusMessage = " save was successful. It can be access through following URI: " + mashupUri;
			r.serviceMashupURI = mashupUri;
		} catch (Exception ex) {
			r.statusCode = -1;
			r.statusMessage = "Save failed with the following message: " + ex.getMessage();
			r.serviceMashupURI = "";
			ex.printStackTrace();
		}

		return r;
	}

	@POST
	@Path("/training")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Generates Training Set for a Service Mashup Family", notes = "Gets a the URI for a service mashup family and creates training set for estimating non-functional properties based on features.")
	public GenerateTrainingSetResponse MashupGenerateTrainingSet(
			@ApiParam(value = "The URI for the service mashup family.", required = true) GenerateTrainingSetRequest request) {
		GenerateTrainingSetProcess process = new GenerateTrainingSetProcess(request.mashupFamilyURI);
		GenerateTrainingSetResponse response = new GenerateTrainingSetResponse();
		try {
			process.process();
			response.statusCode = 0;
			response.statusMessage = "Generating training set was successful";

		} catch (Exception ex) {
			response.statusCode = -1;
			response.statusMessage = "Generating training set failed with the following message: " + ex.getMessage();
		}
		return response;

	}

}
