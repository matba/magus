package edu.ls3.magus.cl.contextmanager.basic;

public class StateFactInstanceS  implements java.io.Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4887455758893593139L;
	private StateFactInstance stateFactInstance;
	private boolean not;
	
	public StateFactInstanceS(StateFactInstance stateFactInstance, boolean not){
		this.setStateFactInstance(stateFactInstance);
		this.setNot(not);
	}

	public StateFactInstance getStateFactInstance() {
		return stateFactInstance;
	}

	public void setStateFactInstance(StateFactInstance stateFactInstance) {
		this.stateFactInstance = stateFactInstance;
	}

	public boolean isNot() {
		return not;
	}

	public void setNot(boolean not) {
		this.not = not;
	}
	
	public StateFactInstanceS getNegated()
	{
		return new StateFactInstanceS(this.getStateFactInstance(), !isNot());
	}
	
	@Override
	public boolean equals(Object i){
		if(i.getClass() != StateFactInstanceS.class)
			return false;
		StateFactInstanceS s = (StateFactInstanceS) i;
		
		if(s.isNot()!=isNot())
			return false;
		
		if(!s.getStateFactInstance().equals(getStateFactInstance()))
			return false;

		return true;
		
	}
	@Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 19 + getStateFactInstance().hashCode();
       
        
        return (isNot()?-1:1)*getStateFactInstance().hashCode();
    }
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		if(isNot())
			sb.append("!");
		
		sb.append(getStateFactInstance().toString());
		
		return sb.toString();
	}
}
