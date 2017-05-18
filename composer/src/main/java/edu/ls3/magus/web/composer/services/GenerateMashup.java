package edu.ls3.magus.web.composer.services;

import javax.ws.rs.Consumes;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;

import edu.ls3.magus.web.composer.core.CompositionRequest;

@Path("/mashupgeneration")
public class GenerateMashup {
	@POST
	@Path("/generatemashup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public GenerateMashupResponse generateMashup(GenerateMashupRequest rq) {
		GenerateMashupResponse resp = new GenerateMashupResponse();
		CompositionRequest cr = null;
		
		try {
			cr = new CompositionRequest(rq.getOntologyXml(), rq.getFeatureModelXml(),rq.getServiceAnnotationXmls(), rq.getSelectedFeatures(),rq.getAvailableServiceURIs());
			
			cr.CallPlanner();
			resp.setWorkflowJSON(cr.getWorkflowJSON());
			resp.setBpelXML(cr.getBpelXML());
			resp.setStatus(cr.getRequestLog().toString());
			resp.setUsedServiceURIs(cr.getUsedServiceURIs());
			
		} catch (Exception e) {
			
			if(cr!=null){
				
				if(cr.getProgressedStep()>1)
					resp.setWorkflowJSON(cr.getWorkflowJSON());
				
				if(cr.getProgressedStep()>2){
					resp.setBpelXML(cr.getBpelXML());
					resp.setUsedServiceURIs(cr.getUsedServiceURIs());
				}
				resp.setStatus(cr.getRequestLog().toString()+ "The composition request failed reason: "+ e.getMessage());
				
			}				
			else	
				resp.setStatus("The composition request failed reason: "+ e.getMessage());
			e.printStackTrace();
		}
		
		

		return resp;

	}

}
