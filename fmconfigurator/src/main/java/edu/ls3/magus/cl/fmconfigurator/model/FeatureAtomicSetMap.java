package edu.ls3.magus.cl.fmconfigurator.model;

import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class FeatureAtomicSetMap {
	
	
	private Map<Feature,AtomicSet> fasMap;
	
	
	public FeatureAtomicSetMap(){
		fasMap = new HashMap<Feature,AtomicSet>();
	}
	

	public Map<Feature,AtomicSet> getFasMap() {
		return fasMap;
	}

	@SuppressWarnings("unused")
	private void setFasMap(Map<Feature,AtomicSet> fasMap) {
		this.fasMap = fasMap;
	}


	public List<AtomicSet> getAllAtomicSets(boolean onlyChangableSelectionFeatures) {
		List<AtomicSet> allAS = new ArrayList<AtomicSet>();
		
		for(AtomicSet as: fasMap.values())
			if(!allAS.contains(as)&&(!onlyChangableSelectionFeatures || (onlyChangableSelectionFeatures&& !as.isSingleSelectionStateFeature())))				
				
				
				allAS.add(as);
		
		return allAS;
		
	}
	
	public List<AtomicSet> getUnchangableSelectionAtomicSets() {
		List<AtomicSet> allAS = new ArrayList<AtomicSet>();
		
		for(AtomicSet as: fasMap.values())
			if(!allAS.contains(as)&&(as.isSingleSelectionStateFeature()))				
				allAS.add(as);
		
		return allAS;
		
	}


	public void mergeAtomicSets(AtomicSet as1, AtomicSet as2) {
		AtomicSet mergedAtomicSet = new AtomicSet();
		
		mergedAtomicSet.getFeatureList().addAll(as1.getFeatureList());
		mergedAtomicSet.getFeatureList().addAll(as2.getFeatureList());
		
		mergedAtomicSet.getMainFeatureList().addAll(as1.getMainFeatureList());
		mergedAtomicSet.getMainFeatureList().addAll(as2.getMainFeatureList());
		
		if(as1.isSingleSelectionStateFeature() || as2.isSingleSelectionStateFeature())
			mergedAtomicSet.setSingleSelectionStateFeature(true);
		
		
		
		Set<Feature> updatedFeatures = new HashSet<Feature>();
		
		for(Feature f: fasMap.keySet())
			if(fasMap.get(f).equals(as1)||fasMap.get(f).equals(as2))
				updatedFeatures.add(f);
		
		for(Feature f:updatedFeatures)
			fasMap.put(f, mergedAtomicSet);
	}
	
	public static FeatureAtomicSetMap readFromXml(String xml, FeatureModel fm) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		FeatureAtomicSetMap fasm = new FeatureAtomicSetMap();
		Set<AtomicSet> asSet = AtomicSet.readFromXml(xml, fm);
		
		for(AtomicSet as : asSet) {
			as.getFeatureList().forEach(value -> fasm.getFasMap().put(value, as));
		}
		
		
		return fasm;
	}
	
	
}
