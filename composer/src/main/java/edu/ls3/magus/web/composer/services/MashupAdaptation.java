package edu.ls3.magus.web.composer.services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.ls3.magus.web.composer.core.MashupContextStateModelUpdateProcess;
import edu.ls3.magus.web.composer.core.MashupContextStateModelUpdateProcess.RequirementStatus;
import edu.ls3.magus.web.composer.core.MashupRegisterProcess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/mashupadaptation")
@Api(value = "Mashup Adaptation Services")
public class MashupAdaptation {
	@POST
	@Path("/register")
	@ApiOperation(value = "Registers a service mashup for recieving adaptation recommendation", notes = "Gets a mashup instance ID and the context state model and user functional and non-functional requirements and registers that mashup as a running mashup to recieve recommendation. It returns an ID to refere to that running instance.")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MashupRegisterResponse registerMashup(
			@ApiParam(value = "A generated mashup instance ID, the context model which is made up the list of service their availablity and their list of non-functional properties, and user functional and non-functional requirements which consists of critical featurs and list of non-functional constraints.", required = true) MashupRegisterRequest request) {
		MashupRegisterProcess process = new MashupRegisterProcess(request.mashupInstanceUri, request.contextStateModel,
				request.criticalFeatureUUIDs, request.constraints);
		MashupRegisterResponse response = new MashupRegisterResponse();
		try {
			String uri = process.registerMashup();
			response.statusCode = 0;
			response.statueMessage = "Registration was successful";
			response.runningInstanceUri = uri;

		} catch (Exception ex) {
			response.statusCode = -1;
			response.statueMessage = "Registration failed with the following message: " + ex.getMessage();
			response.runningInstanceUri = "";
		}
		return response;

	}

	@POST
	@Path("/updatecontextstatemodel")
	@ApiOperation(value = "Updates context state model of a registered running service mashup which returns the satisfaction of functional and non-functional requirements as well as if adaptation is recommended.", notes = "Gets a running mashup instance ID and context state model and returns a boolean representing if the mashup satisfies its critical functional requirements as well as list of non-functional constriants and if they are satisfied.")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UpdateContextStateModelResponse updateContextStateModel(
			@ApiParam(value = "A running service mashup instance ID and the context model which is made up the list of service their availablity.", required = true) UpdateContextStateModelRequest request) {
		UpdateContextStateModelResponse response = new UpdateContextStateModelResponse();

		MashupContextStateModelUpdateProcess process = new MashupContextStateModelUpdateProcess(
				request.mashupRunningInstanceUri, request.contextStateModel);

		try {
			RequirementStatus rs = process.updateContextStateModel();
			response.statusCode = 0;
			response.statueMessage = "Update was successful";
			response.functionalPropertiesSatisfcation = rs.isFunctionalSatisfied;
			List<NonfunctionalPropertiesSatisfaction> nsl = new ArrayList<NonfunctionalPropertiesSatisfaction>();
			for (String nfmt : rs.nfSatisfaction.keySet()) {
				NonfunctionalPropertiesSatisfaction nfs = new NonfunctionalPropertiesSatisfaction();
				nfs.nonfunctionalPropertiesID = nfmt;
				nfs.nonfunctionalPropertiesSatisfaction = rs.nfSatisfaction.get(nfmt);
				nsl.add(nfs);
			}
			response.nonfunctionaPropetiesSatisfaction = nsl.toArray(new NonfunctionalPropertiesSatisfaction[0]);
			response.adaptationIsRecommended = rs.adaptationRecommended;

		} catch (Exception ex) {
			response.statusCode = -1;
			response.statueMessage = "Update failed with the following message: " + ex.getMessage();

		}
		return response;
	}

	@POST
	@Path("/adapt")
	@ApiOperation(value = "Recommends an adaptation on a running instance of service mashup and provides predicted functional and non-functional properties provided.", notes = "Gets a running mashup instance ID and recommends an adaptation based on the latest provided context state model. It returns the replacement service mashup BPEL code as well as its features and predicted non-functional properties of the replacement service mashup after adaptation.")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MashupAdaptationResponse adaptMashup(
			@ApiParam(value = "A running service mashup instance ID.", required = true) MashupAdaptationRequest request) {
		throw new IllegalArgumentException();

	}
}
