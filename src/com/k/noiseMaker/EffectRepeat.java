package com.k.noiseMaker;

public class EffectRepeat extends BaseEffect implements IEffect {
	double repeatDelay = .0;
	//protected double delay = 0.0;

	EffectRepeat() {
		super();
	}

	EffectRepeat(EffectRepeat e) {//clone
		this(e.repeatDelay);
	}
	

	public EffectRepeat clone() {// clone
		return new EffectRepeat(this);
	}
	
	EffectRepeat(double repeatDelay) {
		this();
		setRepeatDelay(repeatDelay);
	}
	
	public boolean isRepeat() {
		return (repeatDelay >= .0);
	}

	public void setRepeatDelay(double repeatDelay) {
		this.repeatDelay = repeatDelay;
	}
	
	public double getRepeatDelay() {
		return this.repeatDelay;
	}
	
	@Override
	public double getEffectValue(Track track, double currentValue, double currentTime) {
		double newValue = currentValue;
		if (isRepeat()) {
			double signalLen = track.getLazyLength() + getRepeatDelay();
			// Logger.log("currentTime:"+currentTime+", signalLen:"+signalLen);
			if (signalLen > -1) {
				if (currentTime > signalLen) {
					newValue = track.getValue(currentTime % signalLen);
				}
			}
		}
		return newValue;
	}


	@Override
	public double getEffectLength(double currentLength){
		return currentLength;//change active, not len
	}

	@Override
	public boolean isEffectActive(boolean currentActive,double currentTime){
		return true;//by definition repeat is always active
	} 

	@Override
	public void setAttribute(String attrName, Object attrValue) {
		switch (attrName) {
			default:
				Logger.log("Uknown attribute:" + attrName);
				break;
			case "RepeatDelay":
				setRepeatDelay(Double.parseDouble((String) attrValue));
				break;
		}
	}

	@Override
	public String getSrcCode() {
		String sCode = "";
		if(isRepeat()){
			sCode = "EffectRepeat "+getPrefixeduid()+" = new EffectRepeat("+this.getRepeatDelay()+");\n";
		}
		return sCode;
	}
}
