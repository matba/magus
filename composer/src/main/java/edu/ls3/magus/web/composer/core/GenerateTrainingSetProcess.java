package edu.ls3.magus.web.composer.core;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;import java.util.stream.Collector;
import java.util.stream.Collectors;

import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.fmconfigurator.FeatureModelConfigurationMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.model.AtomicSet;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureAtomicSetMap;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.utility.UtilityClass;

public class GenerateTrainingSetProcess extends Process {
	
	private String mashupFamilyURI;
	
	public GenerateTrainingSetProcess(String mashupFamilyURI) {
		this.mashupFamilyURI = mashupFamilyURI;
	}
	
	public void process() throws Exception {
		final String configurationFileAddress = findSystemAddress(mashupFamilyURI);
        File configurationFile = new File(configurationFileAddress);

        if (!configurationFile.exists()) {
            throw new IOException("Configuration file does not exists.");
        }
        
        String familyDirectory = configurationFile.getParent();
        File trainingSetDirectory = new File(familyDirectory + File.pathSeparator + Configuration.trainingSetDirectory);
        
        if(trainingSetDirectory.exists()) {
        	return;
        } 
        
        DomainModels domainModel;
        try {
            domainModel = DomainModels.readFromConfigurationFile(configurationFileAddress);
        } catch (Exception ex) {
            throw new IOException("Reading configuration file failed with the following message: " + ex.getMessage(),
                    ex);
        }
        
        FeatureAtomicSetMap fasm = domainModel.getFeatureModel().findAtomicSets();
        
        List<FeatureModelConfiguration> adequateTrainingList = domainModel.getFeatureModel().generateRegressionConfigurations(1, fasm);
		
        Map<FeatureModelConfiguration, String> flows = new HashMap<>();
        
        for(FeatureModelConfiguration fmc : adequateTrainingList) {
        	FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(domainModel, fmc);
        	String bpel = fmcmg.buildServiceMashup().serializeToBpel(Collections.emptyList());
        	flows.put(fmc, bpel);
        	
        }
        
        StringBuilder configurationFileBuilder = new StringBuilder();
        
        trainingSetDirectory.mkdirs();
        
        for(FeatureModelConfiguration fmc : flows.keySet()) {
        	String featureModelUuid = UUID.randomUUID().toString();
        	String features = fmc.getFeatureList().stream().map(value -> value.getUuid()).collect(Collectors.joining(","));
        	if(configurationFileBuilder.length()!=0) {
        		configurationFileBuilder.append(System.lineSeparator());
        	}
        		
        	configurationFileBuilder.append(featureModelUuid).append(System.lineSeparator());
        	configurationFileBuilder.append(features);
        	
        	UtilityClass.writeFile(new File(trainingSetDirectory.getAbsolutePath()+ File.pathSeparator + "FM" + featureModelUuid), flows.get(fmc));
        	
        }
        
        Set<AtomicSet> atomicSets = fasm.getFasMap().values().stream().collect(Collectors.toSet());
        UtilityClass.writeFile(new File(trainingSetDirectory.getAbsolutePath()+ File.pathSeparator + "fasm"), AtomicSet.serializeToXml(atomicSets));
        
	}
	

}
