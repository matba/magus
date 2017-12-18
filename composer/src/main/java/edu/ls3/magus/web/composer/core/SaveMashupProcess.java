package edu.ls3.magus.web.composer.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.FileUtils;

import edu.ls3.magus.cl.fmconfigurator.DomainModelConfiguration;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.eval.generators.owls.UtilityClass;

public class SaveMashupProcess {
	private String mashupUri;
	private String ontologyXml;
	private String featureModelXml;
	private String[] serviceAnnotationXmls;

	public SaveMashupProcess(String mashupUri, String ontologyXml, String featureModelXml,
			String[] serviceAnnotationXmls) {
		this.ontologyXml = ontologyXml;
		this.featureModelXml = featureModelXml;
		this.serviceAnnotationXmls = serviceAnnotationXmls;
		this.mashupUri = mashupUri;
	}

	public String saveMashup() throws IOException, ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		final String respositoryUri = Configuration.domainAddress + Configuration.repositoryWebAddress;
		String mashupUUID = UUID.randomUUID().toString();
		String[] updatedServiceXmls = new String[serviceAnnotationXmls.length];

		if (mashupUri.startsWith(respositoryUri)) {
			mashupUUID = mashupUUID.substring(respositoryUri.length() + 1,
					mashupUri.indexOf("/", respositoryUri.length() + 1));
			try {
				FileUtils.deleteDirectory(new File(
						Configuration.deploymentDirectory + Configuration.repositorySystemAddress + mashupUUID));
			} catch (IOException ex) {
				throw new IOException("Cannot delete previous version of the service mashup.", ex);
			}
			updatedServiceXmls = serviceAnnotationXmls;

		} else {
			String mashupPreviousUri = mashupUri.substring(0, mashupUri.lastIndexOf("/"));
			ontologyXml = ontologyXml.replace(mashupPreviousUri, respositoryUri + mashupUUID);
			featureModelXml = featureModelXml.replace(mashupPreviousUri, respositoryUri + mashupUUID);
			for (int cntr = 0; cntr < serviceAnnotationXmls.length; cntr++)
				updatedServiceXmls[cntr] = serviceAnnotationXmls[cntr].replace(mashupPreviousUri,
						respositoryUri + mashupUUID);

		}

		String directory = Configuration.deploymentDirectory + Configuration.repositorySystemAddress + mashupUUID;
		File dir = new File(directory);
		dir.mkdirs();
		UtilityClass.writeFile(new File(directory + File.separator + "contextModel.xml"), ontologyXml);
		UtilityClass.writeFile(new File(directory + File.separator + "featureModel.xml"), featureModelXml);

		String servicesDirectory = directory + File.separator + "services";
		File serviceDir = new File(servicesDirectory);
		serviceDir.mkdirs();

		int cntr = 0;
		List<String> serviceAddr = new ArrayList<>();
		for (String serviceXml : updatedServiceXmls) {
			String sv = directory + File.separator + "services" + File.separator + "service" + cntr + ".xml";
			UtilityClass.writeFile(new File(sv), serviceXml);
			serviceAddr.add("services/service" + cntr++ + ".xml");
		}

		DomainModelConfiguration conf = new DomainModelConfiguration(Collections.singletonList("contextModel.xml"),
				"featureModel.xml", serviceAddr);

		UtilityClass.writeFile(new File(directory + File.separator + "configuration.xml"),
				conf.serializeToConfigurationFileXml());

		return respositoryUri + mashupUUID + "/configuration.xml";
	}

}
