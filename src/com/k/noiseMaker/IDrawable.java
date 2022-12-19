package com.k.noiseMaker;

//minimal interface needed to be able to draw something 
public interface IDrawable extends INamedElement{
	double estimateMaxAmplitude();
	double getValue(double t);
}
