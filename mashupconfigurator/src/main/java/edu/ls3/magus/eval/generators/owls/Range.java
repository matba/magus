package edu.ls3.magus.eval.generators.owls;

public class Range
{
	private int relFeature;
	private boolean isPre;
	private int mid;
	private int dev;
	public Range( int relFeature, boolean isPre, int mid, int dev){
		this.mid = mid;
		this.dev = dev;
		this.setRelFeature(relFeature);
		this.setPre(isPre);
	}
	public int getStart()
	{
		return mid-dev;
	}
	public int getEnd()
	{
		return mid+dev;
	}
	public boolean isInRange(int no) {
		
		if((no >= getStart())&& (no <= getEnd()))
				return true;
		return false;
	}
	public int relationToRange(int no) {
		
		
//		if((no >= getStart())&& (no <= getEnd()))
//				return 0;
		if(no < getStart())
			return -1;
		if(no> getEnd())
			return 1;
		return 0;
	}
	public int getRelFeature() {
		return relFeature;
	}
	public void setRelFeature(int relFeature) {
		this.relFeature = relFeature;
	}
	public boolean isPre() {
		return isPre;
	}
	public void setPre(boolean isPre) {
		this.isPre = isPre;
	}
}