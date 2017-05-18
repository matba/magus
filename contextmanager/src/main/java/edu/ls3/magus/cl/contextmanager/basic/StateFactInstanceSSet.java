package edu.ls3.magus.cl.contextmanager.basic;
import java.util.*;

public class StateFactInstanceSSet {
	private List<StateFactInstanceS> facts;
	public StateFactInstanceSSet(){
		setFacts(new ArrayList<StateFactInstanceS>());
	}
	public List<StateFactInstanceS> getFacts() {
		return facts;
	}
	private void setFacts(List<StateFactInstanceS> facts) {
		this.facts = facts;
	}
}
