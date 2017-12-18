package edu.ls3.magus.web.composer.services;

public class NonfunctionalConstraint {
	public String nonfunctionalPropertyID;
	public double value;
	public String relation;

	public NonfunctionalConstraint() {

	}

	public NonfunctionalConstraint(String nonfunctionalPropertyID, double value, String relation) {
		this.nonfunctionalPropertyID = nonfunctionalPropertyID;
		this.value = value;
		this.relation = relation;
	}
}
