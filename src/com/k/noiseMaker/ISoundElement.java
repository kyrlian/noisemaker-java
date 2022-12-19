package com.k.noiseMaker;

//signal with a duration
interface ISoundElement extends ISignal{
	public double getStartTime();
	public double getLazyLength();
	public double getLength();
	public boolean isActive(double t);
}