package com.k.noiseMaker;

public class EffectReverb extends BaseEffect{// implements IEffect {

	double delay;
	
	EffectReverb(){
		this(1.0);
	}
	EffectReverb(EffectReverb e){// clone
		this(e.delay);
	}
	
	public EffectReverb clone() {// clone
		return new EffectReverb(this);
	}
	
	
	EffectReverb(double delay){
		this.delay=delay;
	}
	
	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

	@Override
	public double getEffectValue(Track track, double currentValue, double currentTime) {	
		double newValue = currentValue;
		double pastTime = currentTime-delay;
		if(track.isActive(pastTime)){
			double pastValue = track.getValue(pastTime);
			newValue +=( pastValue *  1/Math.pow(delay,2));
		}
		return newValue;
	}
	

	@Override
	public double getEffectLength(double currentLength){
		if(currentLength>0.0){
			return currentLength+this.delay;
		}else{
			return currentLength;//if -1, keep -1
		}		
	}

	
	@Override
	public void setAttribute(String attrName, Object attrValue) {
		switch (attrName) {
		default:
			Logger.log("Uknown attribute:" + attrName);
			break;
		case "Delay":
			setDelay((double)attrValue);
			break;
		}
	}
	

	@Override
	public String getSrcCode() {
		String sCode = "EffectReverb "+getPrefixeduid()+" = new EffectReverb("+this.getDelay()+");\n";
		return sCode;
	}
}
