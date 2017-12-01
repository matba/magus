package edu.ls3.magus.web.composer.core;

import java.io.File;
import java.io.IOException;

import edu.ls3.magus.cl.fmconfigurator.DomainModels;

public class GenerateTrainingSetProcess extends Process {
	private String mashupFamilyURI;
	
	public GenerateTrainingSetProcess(String mashupFamilyURI) {
		this.mashupFamilyURI = mashupFamilyURI;
	}
	
	public void process() throws IOException {
		final String configurationFileAddress = findSystemAddress(mashupFamilyURI);
        File configurationFile = new File(configurationFileAddress);

        if (!configurationFile.exists()) {
            throw new IOException("Configuration file does not exists.");
        }

        DomainModels domainModel;
        try {
            domainModel = DomainModels.readFromConfigurationFile(configurationFileAddress);
        } catch (Exception ex) {
            throw new IOException("Reading configuration file failed with the following message: " + ex.getMessage(),
                    ex);
        }

        
		
		
	}

}
