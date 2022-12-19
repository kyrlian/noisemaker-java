package com.k.noiseMaker;

import java.util.List;

//interface for waves or combination of (oscillator, complex, wave, tracks..)
interface ISignal extends IDrawable {
	double estimateAmplitude(double t);
	double getAmplitude(double t);
	void setAmplitude(double amplitude);
	double getAmplitude();
	List<Double> getFreqs();
	List<Double> getFreqs(double t);
	double getFreq();
}