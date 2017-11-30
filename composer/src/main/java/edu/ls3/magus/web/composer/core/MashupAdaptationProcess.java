package edu.ls3.magus.web.composer.core;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.web.composer.services.NonfunctionalProperty;

public class MashupAdaptationProcess extends Process {
	final private String runningInstanceUri;

	public MashupAdaptationProcess(String runningInstanceUri) {
		this.runningInstanceUri = runningInstanceUri;
	}

	public class AdaptationResult {
		final public String bpelCodeXml;
		final public String[] providedFeaturesUuidAfterAdaptation;
		final public NonfunctionalProperty[] predictedNonfunctionalAfterAdaptation;

		public AdaptationResult(String bpelCodeXml, String[] providedFeaturesUuidAfterAdaptation,
				NonfunctionalProperty[] predictedNonfunctionalAfterAdaptation) {
			this.bpelCodeXml = bpelCodeXml;
			this.providedFeaturesUuidAfterAdaptation = providedFeaturesUuidAfterAdaptation;
			this.predictedNonfunctionalAfterAdaptation = predictedNonfunctionalAfterAdaptation;
		}

		public AdaptationResult adaptMashup()
				throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
			if (!runningInstanceUri.startsWith(Configuration.domainAddress)) {
				throw new IllegalArgumentException("Only mashup families on magus online site is supported.");
			}

			String systemAddress = runningInstanceUri.replace(Configuration.domainAddress,
					Configuration.defaultDeploymentDirectory);

			if (!systemAddress.endsWith("/")) {
				systemAddress = systemAddress + "/";
			}

			final String requestInfoAddress = systemAddress + "requestInformation.xml";

			RequestInformation requestInfo = readRequestInfo(requestInfoAddress);

			GeneratedMashupInfo mashupInfo = readGeneratedMashupInfo(requestInfo.mashupInstanceUri);

			final DomainModels domainModels;
			try {
				domainModels = DomainModels.readFromConfigurationFile(mashupInfo.mashupFamilyURI);
			} catch (Exception ex) {
				throw new IOException(
						"Reading configuration file failed with the following message: " + ex.getMessage(), ex);
			}

			return null;
		}

	}
}
