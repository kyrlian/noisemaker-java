package com.k.noiseMaker;

public class EffectAmplify extends BaseEffect{// implements IEffect {
	double factor;
	
	EffectAmplify(){
		this(1.0);
	}
	EffectAmplify(EffectAmplify e){
		this(e.factor);
	}
	EffectAmplify(double factor){
		this.factor=factor;
	}

	public EffectAmplify clone() {// clone
		return new EffectAmplify(this);
	}
	
	public double getFactor() {
		return factor;
	}

	public void setFactor(double factor) {
		this.factor = factor;
	}
	
	@Override
	public double getEffectValue(Track track, double value, double time) {
		return value*factor;
	}
	
	@Override
	public void setAttribute(String attrName, Object attrValue) {
		switch (attrName) {
		default:
			Logger.log("Uknown attribute:" + attrName);
			break;
		case "Factor":
			setFactor(Double.parseDouble((String) attrValue));
			break;
		}
	}
	
	@Override
	public String getSrcCode() {
		String sCode = "EffectAmplify "+getPrefixeduid()+" = new EffectAmplify("+this.getFactor()+");\n";
		return sCode;
	}
}
