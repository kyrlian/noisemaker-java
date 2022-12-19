package com.k.noiseMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SignalSimpleOscillator extends BaseNamedElement implements ISignal {
	// Data
	protected double amplitude=1.0;
	protected ShapeType shape;
	protected double freq;
	protected double phase;
	//protected double amplitude;//in base
	protected Random myRnd = new Random();

	// Constructors
	SignalSimpleOscillator(ShapeType shape, double freq, double phase, double amplitude) {
		super();
		this.shape = shape;
		this.freq = freq;
		this.amplitude = amplitude;
		this.phase = phase;
	}

	SignalSimpleOscillator(SignalSimpleOscillator o) {// clone
		this(o.shape, o.freq, o.amplitude, o.phase);
	}

	@Override
	public SignalSimpleOscillator clone() {// clone
		return new SignalSimpleOscillator(this);
	}

	SignalSimpleOscillator(ShapeType shape, double freq) {
		this(shape, freq, 0.0, 1.0);
	}

	SignalSimpleOscillator(double freq) {
		this(ShapeType.SIN, freq, 0.0, 1.0);
	}

	SignalSimpleOscillator(String name, double freq) {
		this(freq);
		setName(name);
	}

	SignalSimpleOscillator() {
		this(ShapeType.SIN, 440, 0.0, 1.0);
	}
	
	@Override
	public double getAmplitude(){
		return amplitude;
	}

	@Override
	public double getAmplitude(double t){
		return amplitude;
	}
	
	@Override
	public double estimateAmplitude(double t){
		return getAmplitude(t);
	}
	
	@Override
	public double estimateMaxAmplitude(){
		return getAmplitude();
	}

	@Override
	public double getValue(double t) {		
		double r = 0.0;	
		double f = getFreq(t);//use accessors, because can be overriden if complexOscillator
		double p = getPhase(t);// phase should be -1 - 1			
		double x = t * f + p;
		if (t >= 0) {		
			double value;
			switch (shape) {
				default:
				case FLAT:
					value = 1.0;
					break;
				case SIN:
					//sin(2*pi*t * sin(2*pi*t*.003*f))
					value = Math.sin(2.0 * Math.PI * x);
					break;
				case SQR:
					if (x % 2.0 < 1.0) {
						value = 1.0;
					} else {
						value = -1.0;
					}
					break;
				case SAW:
					value = 2.0 * (x - Math.floor(x + 0.5));
					break;
				case RND:
					value = (myRnd.nextDouble() * 2.0) - 1.0;
					break;
			}
			r = getAmplitude(t) * value;
			//Logger.log("SignalSimpleOscillator:getValue:("+t+")="+r);
		}
		return r;
	}

	public List<Double> getFreqs(double t) {
		return getFreqs();
	}
	
	public List<Double> getFreqs() {
		List<Double> freqs = new ArrayList<Double>();// Arrays.asList(f));
		freqs.add(freq);
		return freqs;
	}

	public double getFreq() {
		return freq;
		//return freq.estimateMaxAmplitude();
	}

	public double getFreq(double t) {
		return freq;
	}
	
	public String getShape() {
		return shape.toString();
	}

	public double getPhase() {
		return phase;
	}
	
	public double getPhase(double t) {
		return phase;
	}
	
	public void setShape(ShapeType shape) {
		this.shape = shape;
	}
	
	public void setShape(String shape) {
		switch (shape) {
			default:
			case "FLAT":
				this.shape = ShapeType.FLAT;
				break;
			case "SIN":
				this.shape = ShapeType.SIN;
				break;
			case "SQR":
				this.shape = ShapeType.SQR;
				break;
			case "SAW":
				this.shape = ShapeType.SAW;
				break;
			case "RND":
				this.shape = ShapeType.RND;
				break;
		}
	}

	public void setFreq(double freq) {
		this.freq = freq;
	}

	public void setPhase(double phase) {
		this.phase = phase;
	}

	@Override
	public void setAmplitude(double ampl) {
		this.amplitude = ampl;
	}
	
	@Override
	public void setAttribute(String attrName, String attrValue) {
		super.setAttribute(attrName, attrValue);
		switch (attrName) {
			case "Amplitude":
				// setAmplitude(Double.parseDouble((String) attrValue));
				setAmplitude(Double.parseDouble(attrValue));
				break;
			case "Shape":
				setShape(attrValue);
				break;
			case "Freq":
				//setFreq((double) attrValue);
				setFreq(Double.parseDouble(attrValue));
				break;
			case "Phase":
				setPhase(Double.parseDouble(attrValue));
				break;
			default:
				Logger.log("Attribute " + attrName + " not supported for " + this.getType());
				break;
		}
		// notifyAll();
	}

	@Override
	public String getInfo(String tabs) {
		String info = super.getInfo(tabs);
		info += ", amplitude:" + amplitude;
		info += ", shape:" + shape;
		info += ", freq:" + freq;
		info += ", phase:" + phase;
		return info;
	}

	@Override
	public String getSrcCode() {
		String sCode = getType() + getPrefixeduid() + " = new "+getType()+"(" + "ShapeType." + getShape() + "," + getFreq() + "," + getPhase() + "," + getAmplitude() + ");\n";
		return sCode;
	}



	
}