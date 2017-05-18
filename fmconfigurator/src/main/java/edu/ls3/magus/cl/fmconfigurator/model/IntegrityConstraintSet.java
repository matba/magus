package edu.ls3.magus.cl.fmconfigurator.model;

import java.util.ArrayList;
import java.util.List;

public class IntegrityConstraintSet {
	private List<IntegrityConstraint> integrityConstraints;
	public IntegrityConstraintSet(){
		this.setIntegrityConstraints(new ArrayList<IntegrityConstraint>());
	}
	public List<IntegrityConstraint> getIntegrityConstraints() {
		return integrityConstraints;
	}
	private void setIntegrityConstraints(List<IntegrityConstraint> integrityConstraints) {
		this.integrityConstraints = integrityConstraints;
	}
	
}
