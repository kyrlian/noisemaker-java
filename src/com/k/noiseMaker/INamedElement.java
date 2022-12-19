package com.k.noiseMaker;

public interface INamedElement {

	public abstract INamedElement clone();

	public abstract void setName(String n);

	public abstract String getName();

	public abstract int getuid();
	public abstract String getPrefixeduid();
	
	public abstract String getInfo(String tabs);

	public abstract INamedElement getNamedElement(int uid);

	public abstract INamedElement getNamedElement(String name);

	public abstract String getInfo();
	public abstract String getSrcCode();
	
	INamedElement getNamedElement(Object id);
	public abstract boolean isTrack();
	public abstract boolean isEffect();
	public abstract boolean isWave();
	public abstract boolean isSignal();
	//public abstract boolean isLFO();
	public String getType();

	public abstract void setAttribute(String attrName, String attrValue);
}