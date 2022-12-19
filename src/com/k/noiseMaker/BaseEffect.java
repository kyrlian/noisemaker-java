package com.k.noiseMaker;

//common implementation to be reused among effects
public abstract class BaseEffect extends BaseNamedElement implements IEffect {
	BaseEffect() {
		super();
	}
	
	@Override
	public double getEffectLength(double currentLength) {
		return currentLength;
	}

	@Override
	public boolean isEffectActive(boolean currentActive, double currentTime) {
		return currentActive;
	}
}
