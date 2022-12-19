package com.k.noiseMaker;

public interface IEffect extends INamedElement {
	//String getType();
	//double apply(double v);
	void setAttribute(String attrName, Object attrValue);
	double getEffectValue(Track track,double currentValue,double currentTime);
	double getEffectLength(double currentLength);
	boolean isEffectActive(boolean currentActive,double currentTime);
}
