
package edu.ls3.magus.cl.fmconfigurator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.*;

public class AtomicSetNFAnnotationMap {
	private Map<AtomicSet, NonfunctionalAnnotationSet> map;
	
	public AtomicSetNFAnnotationMap(List<AtomicSet> asl){
		setMap(new HashMap<AtomicSet, NonfunctionalAnnotationSet>());
		for(AtomicSet as: asl){
			getMap().put(as, new NonfunctionalAnnotationSet());
		}
	}

	public Map<AtomicSet, NonfunctionalAnnotationSet> getMap() {
		return map;
	}

	private void setMap(Map<AtomicSet, NonfunctionalAnnotationSet> map) {
		this.map = map;
	}

}
