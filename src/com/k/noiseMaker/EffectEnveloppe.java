package com.k.noiseMaker;


class EffectEnveloppe extends BaseEffect implements IEffect,IDrawable {
	protected double attackDuration;
	protected double decayDuration;
	protected double sustainValue;
	protected double sustainDuration;
	protected double releaseDuration;
	protected double decayDurationSum;
	protected double sustainDurationSum;
	protected double releaseDurationSum;

	EffectEnveloppe(double attackDuration, double decayDuration, double sustainValue, double sustainDuration, double releaseDuration) {
		this.attackDuration = attackDuration;
		this.decayDuration = decayDuration;
		this.sustainValue = sustainValue;
		this.sustainDuration = sustainDuration;
		this.releaseDuration = releaseDuration;
		this.updateSums();
	}

	EffectEnveloppe(EffectEnveloppe e) {// Clone
		this(e.attackDuration, e.decayDuration, e.sustainValue, e.sustainDuration, e.releaseDuration);
	}

	EffectEnveloppe() {
		this(.1, .2, .8, .6, .1);
	}
	
	public EffectEnveloppe clone() {// clone
		return new EffectEnveloppe(this);
	}

	void updateSums() {
		this.decayDurationSum = attackDuration + decayDuration;
		this.sustainDurationSum = decayDurationSum + sustainDuration;
		this.releaseDurationSum = sustainDurationSum + releaseDuration;
	}

	@Override
	public double getEffectLength(double currentLength) {
		if (currentLength > 0.0) {
			return Math.min(currentLength, this.releaseDurationSum);
		} else {
			return this.releaseDurationSum;
		}
	}

	public double getLength() {
		return this.releaseDurationSum;
	}
	
	@Override
	public boolean isEffectActive(boolean currentActive, double currentTime) {
		return (currentActive && (currentTime < this.releaseDurationSum));
	}

	@Override
	public double getValue(double currentTime) {
		double factor = 0.0;
		double t = currentTime;
		if (t < attackDuration) {// 0-1
			factor = (t / attackDuration);
		} else if (t < decayDurationSum) {// 1-sustain
			// v = (t-decaySum)(1-sustainRate)/(-1*decayDuration) +
			// sustainValue
			factor = (t - decayDurationSum) * (1 - sustainValue) / (-1 * decayDuration) + sustainValue;
		} else if (t < sustainDurationSum) {// sustain
			factor = sustainValue;
		} else if (t < releaseDurationSum) {// sustain-0
			// v = sustainValue*(t-releaseSum)/(-1*releaseDuration)
			factor = sustainValue * (t - releaseDurationSum) / (-1 * releaseDuration);
		} else {// after enveloppe
			factor = 0.0;
		}
		return factor;
	}
	
	@Override
	public double getEffectValue(Track track, double currentValue, double currentTime) {
		double factor = getValue(currentTime);
		return currentValue * factor;
	}

	public double getAttackDuration() {
		return attackDuration;
	}

	public void setAttackDuration(double attackDuration) {
		this.attackDuration = attackDuration;
		updateSums();
	}

	public double getDecayDuration() {
		return decayDuration;
	}

	public void setDecayDuration(double decayDuration) {
		this.decayDuration = decayDuration;
		updateSums();
	}

	public double getSustainValue() {
		return sustainValue;
	}

	public void setSustainValue(double sustainValue) {
		this.sustainValue = sustainValue;
	}

	public double getSustainDuration() {
		return sustainDuration;
	}

	public void setSustainDuration(double sustainDuration) {
		this.sustainDuration = sustainDuration;
		updateSums();
	}

	public double getReleaseDuration() {
		return releaseDuration;
	}

	public void setReleaseDuration(double releaseDuration) {
		this.releaseDuration = releaseDuration;
		updateSums();
	}

	public void setAttribute(String attrName, Object attrValue) {
		switch (attrName) {
			default:
				Logger.log("Uknown attribute:" + attrName);
				break;
			case "Name":
				setName(attrValue.toString());
				break;
			case "AttackDuration":
				setAttackDuration(Double.parseDouble((String) attrValue));
				break;
			case "DecayDuration":
				setDecayDuration(Double.parseDouble((String) attrValue));
				break;
			case "SustainValue":
				setSustainValue(Double.parseDouble((String) attrValue));
				break;
			case "SustainDuration":
				setSustainDuration(Double.parseDouble((String) attrValue));
				break;
			case "ReleaseDuration":
				setReleaseDuration(Double.parseDouble((String) attrValue));
				break;
		}
		// notifyAll();
	}

	@Override
	public String getInfo(String tabs) {
		String info = super.getInfo(tabs);
		info += ", attackDuration:" + attackDuration;
		info += ", decayDuration:" + decayDuration;
		info += ", sustainValue:" + sustainValue;
		info += ", sustainDuration:" + sustainDuration;
		info += ", releaseDuration:" + releaseDuration;
		return info;
	}

	@Override
	public String getSrcCode() {
		//this(e.attackDuration, e.decayDuration, e.sustainValue, e.sustainDuration, e.releaseDuration);
		String sCode = "EffectEnveloppe "+getPrefixeduid()+" = new EffectEnveloppe("+this.attackDuration+", "+ this.decayDuration+", "+ this.sustainValue+", "+ this.sustainDuration+", "+this.releaseDuration+");\n";		
		return sCode;
	}
	
	@Override
	public double estimateMaxAmplitude() {
		return 1.0;
	}
}