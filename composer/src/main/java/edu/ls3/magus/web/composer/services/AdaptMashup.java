package edu.ls3.magus.web.composer.services;

import javax.ws.rs.Consumes;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;

import edu.ls3.magus.web.composer.core.AdaptationRequest;

@Path("/mashupadaptation")
public class AdaptMashup {
	@POST
	@Path("/adaptmashup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AdaptMashupResponse adaptMashup(AdaptMashupRequest rq) {
		AdaptMashupResponse resp = new AdaptMashupResponse();
		AdaptationRequest cr = null;
		
		try {
			cr = new AdaptationRequest(rq.getOntologyXml(), rq.getFeatureModelXml(),rq.getServiceAnnotationXmls(), rq.getSelectedFeatures(),rq.getAvailableServiceURIs(),rq.getFailedServiceURI(),rq.getFailedWorkflow(),rq.getFailedBPELXml());
			
			cr.Adapt();
			
			resp.setWorkflowJSON(cr.getWorkflowJSON());
			resp.setBpelXML(cr.getBpelXML());
			resp.setStatus(cr.getRequestLog().toString());
			if(cr.getAlternateFeatureModelConfiguration()!=null){
				resp.setAlternateFeatureModel(cr.getAlternateFeatureModelConfiguration().getSelectedFeatureUUIDs().toArray(new String[0]));
				resp.setAlternateFMDistance(cr.getAlternateFeatureModelConfiguration().getDistance());
			}
			resp.setReplacementServiceName(cr.getReplacementServiceName());
			resp.setAdaptationType(cr.getAdaptationType());
			resp.setUsedServiceURIs(cr.getUsedServiceURIs());
			resp.setReplacementServiceURI(cr.getReplacementServiceURI());
			
			
		} catch (Exception e) {
			
				resp.setStatus("The composition request failed reason: "+ e.getMessage());
			e.printStackTrace();
		}
		
		

		return resp;

	}

}
