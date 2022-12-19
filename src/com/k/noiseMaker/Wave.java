package com.k.noiseMaker;

//oscillator with additional time based functions
//a wave is still infinite, the enveloppe and start/length are in the track
class Wave extends SignalComplexOscillator implements ISoundElement{//], IDrawable {

	// Constructors
	Wave(ShapeType shape, double freq, double phase, double amplitude, ISignal freqLfo, ISignal phaseLfo, ISignal amplLfo) {
		super(shape,  freq,  phase,  amplitude,  freqLfo,  phaseLfo,  amplLfo);
	}
	Wave(ShapeType shape, double freq, double phase, double amplitude) {
		super(shape, freq, phase, amplitude);// null pointer on wave.getValue is handled
		// this(shape, freq, phase, amplitude, new LFO(), new LFO(), new LFO());
	}

	Wave(Wave o) {// clone
		super(o.shape, o.freq, o.amplitude, o.phase, o.freqLfo, o.phaseLfo, o.amplLfo);
	}

	Wave(SignalComplexOscillator o) {// upgrade from SignalComplexOscillator
		super(o);
		//super(o.shape, o.freq, o.amplitude, o.phase);
	}
	
	@Override
	public Wave clone() {// clone
		return new Wave(this);
	}

	Wave(ShapeType shape, double freq) {
		this(shape, freq, 0.0, 1.0);
	}

	Wave(double freq) {
		this(ShapeType.SIN, freq, 0.0, 1.0);
	}

	Wave(String name, double freq) {
		this(freq);
		setName(name);
	}

	Wave() {
		this(ShapeType.SIN, 440, 0.0, 1.0);
	}
	
	@Override
	public double getLazyLength() {
		return getLength();
	}

	@Override
	public double getLength() {
		return -1.0;
	}

	@Override
	public boolean isActive(double t) {
		return true;
	}
	@Override
	public double getStartTime() {
		return 0.0;
	}

}