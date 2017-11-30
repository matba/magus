package edu.ls3.magus.web.composer.core;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import edu.ls3.magus.cl.fmconfigurator.DomainModelConfiguration;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.eval.generators.owls.UtilityClass;

public class SaveMashupProcess {
	private String ontologyXml;
	private String featureModelXml ;
	private String[] serviceAnnotationXmls;
	
	public SaveMashupProcess(String ontologyXml, String featureModelXml, String[] serviceAnnotationXmls) {
		this.ontologyXml = ontologyXml;
		this.featureModelXml = featureModelXml;
		this.serviceAnnotationXmls = serviceAnnotationXmls;
	}
	
	public String saveMashup() throws IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException{
		String mashupUUID = UUID.randomUUID().toString();
		String directory = Configuration.deploymentDirectory +
				Configuration.repositorySystemAddress + mashupUUID;
		File dir = new File(directory);
		dir.mkdirs();
		UtilityClass.writeFile(new File(directory+File.separator+"contextModel.xml"), ontologyXml);
		UtilityClass.writeFile(new File(directory+File.separator+"featureModel.xml"), featureModelXml);
		
		String servicesDirectory = directory + File.separator + "services";
		File serviceDir = new File(servicesDirectory);
		serviceDir.mkdirs();
		
		int cntr = 0;
		List<String> serviceAddr = new ArrayList<>();
		for(String serviceXml : serviceAnnotationXmls) {
			String sv = directory+ File.separator + "services"+File.separator+"service"+cntr+".xml";
			UtilityClass.writeFile(new File(sv), serviceXml);
			serviceAddr.add(Configuration.repositoryWebAddress+ mashupUUID +"/services/service"+cntr+++".xml");
		}
		
		
		
		DomainModelConfiguration conf = new DomainModelConfiguration(
				Collections.singletonList(Configuration.repositoryWebAddress +mashupUUID+ "/contextModel.xml"),
				Configuration.repositoryWebAddress +mashupUUID +"/featureModel.xml",
				serviceAddr
				);
		
		
		UtilityClass.writeFile(new File(directory+File.separator+"configuration.xml"), conf.serializeToConfigurationFileXml());
				
		return Configuration.domainAddress + Configuration.repositoryWebAddress +mashupUUID+"/configuration.xml";
	}
	

}
