package com.k.noiseMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

enum LFOType{
	FREQ,PHASE,AMPL
}

//an oscillator with lfo s
class SignalComplexOscillator extends SignalSimpleOscillator{
	// Data
	protected ISignal freqLfo;
	protected ISignal phaseLfo;
	protected ISignal amplLfo;
	protected Random myRnd = new Random();

	// Constructors	
	SignalComplexOscillator(ShapeType shape,  double freq, double phase, double amplitude, ISignal freqLfo, ISignal phaseLfo, ISignal amplLfo) {
		super( shape,  freq,  phase,  amplitude);
		this.freqLfo = freqLfo;
		this.phaseLfo = phaseLfo;
		this.amplLfo = amplLfo;
	}
	SignalComplexOscillator(ShapeType shape, double freq, double phase, double amplitude) {
		super( shape,  freq,  phase,  amplitude);
	}
	SignalComplexOscillator(SignalComplexOscillator o) {// clone
		this(o.shape, o.freq, o.phase, o.amplitude, o.freqLfo, o.phaseLfo, o.amplLfo);
	}

	SignalComplexOscillator(SignalSimpleOscillator o) {// upgrade from SignalSimpleOscillator
		super(o.shape, o.freq, o.phase, o.amplitude);
	}
	
	@Override
	public SignalComplexOscillator clone() {// clone
		return new SignalComplexOscillator(this);
	}

	SignalComplexOscillator(ShapeType shape, double freq) {
		this(shape, freq, 0.0, 1.0);
	}

	SignalComplexOscillator(double freq) {
		this(ShapeType.SIN, freq, 0.0, 1.0);
	}

	SignalComplexOscillator(String name, double freq) {
		this(freq);
		setName(name);
	}

	SignalComplexOscillator() {
		this(ShapeType.SIN, 440, 0.0, 1.0);
	}

	@Override
	public double estimateMaxAmplitude() {
		double r=getAmplitude();
		if (amplLfo != null) {
			r += amplLfo.estimateMaxAmplitude();
		}
		return r;
	}

	void setLFO(LFOType t, ISignal lfo) {
		switch (t) {
			default:
			case AMPL:
				this.amplLfo = lfo;
				break;
			case FREQ:
				this.freqLfo = lfo;
				break;
			case PHASE:
				this.phaseLfo = lfo;
				break;
		}
	}

	ISignal getLFO(LFOType t) {
		switch (t) {
			default:
			case AMPL:
				return this.amplLfo;
			case FREQ:
				return this.freqLfo;
			case PHASE:
				return this.phaseLfo;
		}
	}

	
	@Override
	public String getSrcCode() {
		String sCode = "Wave " + this.getPrefixeduid() + " = new Wave(" + "ShapeType." + this.getShape() + "," + this.getFreq() + "," + this.getPhase() + "," + this.getAmplitude() + ");\n";
		// export LFOs if needed
		if (freqLfo!=null && freqLfo.getAmplitude() > 0) {
			if (!exportedIds.contains(freqLfo.getuid())) {
				sCode += freqLfo.getSrcCode();
			}
			sCode += getPrefixeduid() + ".setLFO(LFOType.FREQ," + freqLfo.getPrefixeduid() + ");\n";
		}
		if (amplLfo != null && amplLfo.getAmplitude() > 0) {
			if (!exportedIds.contains(amplLfo.getuid())) {
				sCode += amplLfo.getSrcCode();
			}
			sCode += getPrefixeduid() + ".setLFO(LFOType.AMPL," + amplLfo.getPrefixeduid() + ");\n";
		}
		if (phaseLfo!=null && phaseLfo.getAmplitude() > 0) {
			if (!exportedIds.contains(phaseLfo.getuid())) {
				sCode += phaseLfo.getSrcCode();
			}
			sCode += getPrefixeduid() + ".setLFO(LFOType.PHASE," + phaseLfo.getPrefixeduid() + ");\n";
		}
		return sCode;
	}

	@Override
	public List<Double> getFreqs() {
		List<Double> freqs = new ArrayList<Double>();// Arrays.asList(f));
		freqs.add(freq);
		if (freqLfo != null) {
			freqs.add(freq+freqLfo.estimateMaxAmplitude());
			freqs.add(freq-freqLfo.estimateMaxAmplitude());
		}
		return freqs;
	}
	
	@Override
	public double getFreq(double t) {
		if (freqLfo != null) {
			return freq + freqLfo.getValue(t);			
		}else{
			return freq;
		}
	}
	
	public double getAmplitude(double t) {
		if (amplLfo != null) {
			return amplitude + amplLfo.getValue(t);
		}else{
			return amplitude;
		}
	}
	
	public double getPhase(double t) {
		if (phaseLfo != null) {
			return phase + 2.0 * Math.PI * phaseLfo.getValue(t);
		}else{
			return phase;
		}
	}
	

	@Override
	public String getInfo(String tabs) {
		String info = super.getInfo(tabs);
		//TODO should add lfo info
		return info;
	}
	
	@Override
	public String getInfo() {
		return getInfo("");
	}


}