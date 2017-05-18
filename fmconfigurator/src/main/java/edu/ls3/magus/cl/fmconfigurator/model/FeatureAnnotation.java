package edu.ls3.magus.cl.fmconfigurator.model;




import edu.ls3.magus.cl.contextmanager.basic.InstanceMap;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceSSet;



public class FeatureAnnotation implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4869143847972975746L;
	private Feature feature;
	private InstanceMap entities;
	private StateFactInstanceSSet preconditions;
	private StateFactInstanceSSet effects;
	
	
	public FeatureAnnotation(Feature feature)
	{
		this.feature = feature;
		this.entities = new InstanceMap();
		this.preconditions = new StateFactInstanceSSet();
		this.effects = new StateFactInstanceSSet();
		
		
	}
	public Feature getFeature() {
		return feature;
	}
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	public InstanceMap getEntities() {
		return entities;
	}
	public void setEntities(InstanceMap entities) {
		this.entities = entities;
	}
	public StateFactInstanceSSet getPreconditions() {
		return preconditions;
	}
	public void setPreconditions(StateFactInstanceSSet preconditions) {
		this.preconditions = preconditions;
	}
	public StateFactInstanceSSet getEffects() {
		return effects;
	}
	public void setEffects(StateFactInstanceSSet effects) {
		this.effects = effects;
	}

	

	

	
}
