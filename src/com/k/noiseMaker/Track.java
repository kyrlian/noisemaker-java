package com.k.noiseMaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class Track extends BaseSoundElement implements ISignal, IDrawable {
	// protected EffectEnveloppe env;
	protected double startTime = 0.0;
	protected double signalLen = -1.0;
	protected List<ISoundElement> soundList = new ArrayList<ISoundElement>();
	protected List<IEffect> effectList = new ArrayList<IEffect>();
	double estimatedMaxAmplitude = 1.0;
	boolean collapsed = true;// collapsed is default

	Track() {
		super();
	}

	Track(Track track) {// Clone constructor
		this();
		for (ISoundElement se : track.soundList) {
			//clone childs before adding
			this.addElement(se.clone());
		}
		for (IEffect eff : track.effectList) {
			//clone childs before adding
			this.addElement(eff.clone());
		}
		// updateInfo();
	}

	@Override
	public Track clone() {// Clone method
		return new Track(this);
	}

	Track(ISoundElement se) {
		this();
		this.addSoundElement(se);
	}

	Track(double fundFreq, int nHarmonics) {
		this();
		double mainAmplitude = 1.0;
		// LFO lfo = new LFO(ShapeType.SIN, 0.5, 0.01);
		for (int i = 1; i < nHarmonics + 1; i++) {
			double iAmplitude = mainAmplitude / Math.pow(2, i);// Vary amplitude to get timbre
			double iFreq = fundFreq * i;
			Wave w = new Wave(ShapeType.SIN, iFreq, 0.0, iAmplitude);
			// w.setLFO(LFOType.PHASE, lfo);
			this.addSoundElement(w);
		}
	}

	Track(ISoundElement se, EffectEnveloppe env, double t) {
		this(se);
		this.addEffect(env);
		setStartTime(t);
	}

	Track(ISoundElement sg, double t) {
		this(sg);
		setStartTime(t);
	}

	Track(ISoundElement sg, EffectEnveloppe env) {
		this(sg);
		this.addEffect(env);
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
		updateInfo();
	}

	/*
	 * protected void setEnveloppe(EffectEnveloppe env) { this.env = env; updateInfo(); }
	 */
	void updateInfo() {
		getLength();
		estimateMaxAmplitude();// depends on len : compute len before !!
	}

	@Override
	public double getLength() {
		double tmpLen = -1.0;
		// max of sub signals
		for (ISoundElement o : soundList) {
			double subLen = o.getLength();
			if (subLen == -1.0) {
				tmpLen = -1.0;
				break;
			} else {
				tmpLen = Math.max(tmpLen, subLen + o.getStartTime());
			}
		}
		// apply effects
		for (IEffect eff : effectList) {
			tmpLen = eff.getEffectLength(tmpLen);
		}
		signalLen = tmpLen;
		return tmpLen;
	}

	@Override
	public double getLazyLength() {
		return signalLen;
	}

	void addSoundElement(ISoundElement o) {
		soundList.add(o);
		updateInfo();
	}

	void addEffect(IEffect e) {
		effectList.add(e);
		updateInfo();
	}

	void addElement(INamedElement e) {
		if (e.getType().equals("Wave") || e.getType().equals("Track")) {
			addSoundElement((ISoundElement) e);
		} else if (e.isEffect()) {
			addEffect((IEffect) e);
		}
	}

	/*
	 * EffectEnveloppe getEnveloppe(int uid) { //My enveloppe ? if(this.env != null && env.getuid() == uid){ return this.env; } // deep look in my subchilds for (ISoundElement o :
	 * componentList) { if (o.isTrack()) { EffectEnveloppe c = ((Track)o).getEnveloppe(uid); if (c != null) { return c; }} }
	 * //Logger.log("EffectEnveloppe "+uid+" not found in "+this.getuid()); return null; }
	 * 
	 * EffectEnveloppe getEnveloppe(String name) { //My enveloppe ? if(this.env != null && env.getName() == name){ return this.env; } // deep look in my subchilds for (ISoundElement
	 * o : componentList) { if (o.isTrack()) { EffectEnveloppe c = ((Track)o).getEnveloppe(name); if (c != null) { return c; }} }
	 * //Logger.log("EffectEnveloppe "+name+" not found in "+this.getuid()); return null; }
	 */
	/*
	 * public ISoundElement getSoundElement(int uid) { return getSoundElement((Object) uid); }
	 * 
	 * public ISoundElement getSoundElement(String name) { return getSoundElement((Object) name); }
	 * 
	 * public IEffect getEffect(int uid) { return getEffect((Object) uid); }
	 * 
	 * public IEffect getEffect(String name) { return getEffect((Object) uid); }
	 * 
	 * private ISoundElement getSoundElement(Object id) { INamedElement tmp = getNamedElement(id); if (tmp != null && (tmp.getType().equals("Wave") ||
	 * tmp.getType().equals("Track"))){ return (ISoundElement)tmp; }else{ return null;} }
	 * 
	 * private IEffect getEffect(Object id) { INamedElement tmp = getNamedElement(id); if (tmp != null && (tmp.getType().startsWith("IEffect"))){ return (IEffect)tmp; }else{ return
	 * null;} }
	 */
	/*
	 * private ISoundElement getSoundElement(Object id) { if (super.getNamedElement(id) != null) {// me? return this; } else { for (ISoundElement o : soundList) {// dig if
	 * (o.getNamedElement(id) != null) {// this son ? return o; } else { if (o.isTrack()) {// one of his ? (if a track) ISoundElement eo = ((Track) o).getSoundElement(id); if (eo !=
	 * null) { return eo; } } } } } // Logger.log("Element "+uid+" not found in "+this.getuid()); return null; }
	 * 
	 * private IEffect getEffect(Object id) { for (IEffect o : effectList) {// dig if (o.getNamedElement(id) != null) {// this son ? return o; } } // if not one of mine, maybe in a
	 * son for (ISoundElement o : soundList) {// dig if (o.isTrack()) {// one of his ? (if a track) IEffect eo = ((Track) o).getEffect(id); if (eo != null) { return eo; } }
	 * 
	 * }
	 * 
	 * // Logger.log("Element "+uid+" not found in "+this.getuid()); return null; }
	 */
	/*
	 * //@Override private <T extends INamedElement> T getNamedElement(int uid, List<T> oList) { //Is it me? if (uid == this.getuid() ) { return (T) this; } // quick look in my
	 * direct components for (INamedElement o : oList) { if (uid == o.getuid() ) { return (T) o; } } // deep look in my subchilds for (T o : oList) { INamedElement c =
	 * o.getNamedElement(uid); if (c != null) { return (T)c; } } //Logger.log("Element "+uid+" not found in "+this.getuid()); return null; }
	 * 
	 * private <T extends INamedElement> T getNamedElement(String name, List<T> oList) { //Is it me? if (name.equals(this.getName()) ) { return (T) this; } // quick look in my
	 * direct components for (INamedElement o : oList) { if (name.equals(o.getName()) ) { return (T) o; } } // deep look in my subchilds for (T o : oList) { INamedElement c =
	 * o.getNamedElement(uid); if (c != null) { return (T)c; } } //Logger.log("Element "+uid+" not found in "+this.getuid()); return null; }
	 * 
	 * /* public ISoundElement getSoundElement(int uid) { //Is it me? if (uid == this.getuid() ) { return this; } // quick look in my direct components for (ISoundElement o :
	 * componentList) { if (uid == o.getuid() ) { return o; } } // deep look in my subchilds for (ISoundElement o : componentList) { ISoundElement c = o.getComponent(uid); if (c !=
	 * null) { return c; } } //Logger.log("Element "+uid+" not found in "+this.getuid()); return null; }
	 * 
	 * public ISoundElement getSoundElement(String name) { //Is it me? if (name == this.getName()) { return this; } for (ISoundElement o : componentList) { String oName =
	 * o.getName(); if (oName != null && oName == name) { return o; } } // deep look in my subchilds for (ISoundElement o : componentList) { ISoundElement c = o.getComponent(name);
	 * if (c != null) { return c; } } return null; }
	 */
	/*
	 * void remove(String Name) { soundList.remove(getNamedElement(uid)); effectList.remove(getNamedElement(uid)); updateInfo(); }
	 */

	void remove(int uid) {
		soundList.remove(getNamedElement(uid));
		effectList.remove(getNamedElement(uid));
		/*
		 * ISoundElement o = getSoundElement(uid); if (o != null) { soundList.remove(o); } else { IEffect eff = getEffect(uid); if (eff != null) { effectList.remove(eff); } }
		 */
		updateInfo();
	}

	/*
	 * void remove(String name) { ISoundElement o = getSoundElement(name); if (o != null) { componentList.remove(o); }else{ IEffect eff = getEffect(name); if (eff != null) {
	 * effectList.remove(eff); } } updateInfo(); }
	 */

	/*
	 * boolean setRepeat(boolean repeatDelay) { this.repeat = repeatDelay; updateInfo(); return repeatDelay; }
	 * 
	 * boolean setRepeat() { return setRepeat(!repeatDelay); }
	 * 
	 * boolean getRepeat() { return repeatDelay; }
	 */
	public void removeHighFreqs(double fLimit) {
		Iterator<ISoundElement> ite = soundList.iterator();// Must use an iterator to use the proper 'remove()' method
		while (ite.hasNext()) {
			ISoundElement o = ite.next();
			if (o.isWave()) {
				if (((Wave) o).getFreq() > fLimit) {
					ite.remove();
					// Logger.log("Removed " + o.getuid());
					updateInfo();
				}
			} else if (o.isTrack()) {
				((Track) o).removeHighFreqs(fLimit);
			}
		}
		updateInfo();
	}

	public void removeLowFreqs(double fLimit) {
		Iterator<ISoundElement> ite = soundList.iterator();// Must use an iterator to use the proper 'remove()' method
		while (ite.hasNext()) {
			ISoundElement o = ite.next();
			if (o.isWave()) {
				if (((Wave) o).getFreq() < fLimit) {
					ite.remove();
					// Logger.log("Removed " + o.getuid());
					updateInfo();
				}
			} else if (o.isTrack()) {
				((Track) o).removeLowFreqs(fLimit);
			}
		}
		updateInfo();
	}

	public double estimateMaxAmplitude() {
		return estimateMaxAmplitude(3);// 3 samples per second
		// old quick n dirty way, overestimates a lot
		/*
		 * double estimated = 0.0; for (ISoundElement o : componentList) { estimated += o.estimateMaxAmplitude(); } estimated *= amplitude; return estimated;
		 */
	}

	public double estimateMaxAmplitude(double testsPerSecond) {// better
		double estimated = 0.0;
		double tStep = 1.0 / testsPerSecond;
		double tMax = Math.max(signalLen, 60);// if infinite, look at 60 secs
		for (double t = 0.0; t < tMax; t += tStep) {
			estimated = Math.max(estimated, estimateAmplitude(t));
		}
		estimatedMaxAmplitude = estimated;
		return estimated;
	}

	@Override
	public double estimateAmplitude(double t) {
		double estimated = 0.0;
		double myTime = t - startTime;
		if (isActive(t)) {
			for (ISoundElement o : soundList) {
				estimated += o.estimateAmplitude(myTime);
			}
			// Logger.log(getuid()+",t:"+t+", estimated:"+estimated);
		}
		return estimated;
	}

	@Override
	public boolean isActive(double t) {
		boolean a = false;
		double myTime = t - startTime;
		if (myTime >= 0.0 && (myTime <= signalLen || signalLen == -1.0)) {
			a = true;
		}
		// apply effects
		for (IEffect eff : effectList) {
			a = eff.isEffectActive(a, myTime);
		}
		return a;
	}

	@Override
	public double getValue(double t) {// Get sound value at time t
		double value = 0.0;
		if (isActive(t)) {
			double myTime = t - startTime;
			for (ISoundElement o : soundList) {
				value = value + o.getValue(myTime);
			}
			// apply effects
			for (IEffect e : effectList) {
				value = e.getEffectValue(this, value, myTime);
			}
		}
		// logger.log("sound:getValue:currentTime:"+t+", value:"+r);
		return value;
	}

	public List<Double> getFreqs(double t) {
		List<Double> freqs = new ArrayList<Double>();
		if (isActive(t)) {
			double myTime = (t - startTime) % getLazyLength();
			for (ISoundElement o : soundList) {
				List<Double> ofreqs = o.getFreqs(myTime);
				freqs.addAll(ofreqs);
			}
		}
		if (freqs.isEmpty()) {// to avoid null later
			freqs.add(1.0);
		}
		// logger.log("sound:getValue:currentTime:"+t+", value:"+r);
		return freqs;
	}

	public List<Double> getFreqs() {
		List<Double> freqs = new ArrayList<Double>();
		for (ISoundElement o : soundList) {
			List<Double> ofreqs = o.getFreqs();
			freqs.addAll(ofreqs);
		}
		if (freqs.isEmpty()) {// to avoid null later
			freqs.add(1.0);
		}
		// logger.log("sound:getValue:currentTime:"+t+", value:"+r);
		return freqs;
	}
	
	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	@Override
	public void setAttribute(String attrName, String attrValue) {
		switch (attrName) {
		default:
			Logger.log("Track:Attribute " + attrName + " not supported for " + this.getType());
			break;
		case "Name":
			setName(attrValue);
			break;
		case "StartTime":
			setStartTime(Double.parseDouble(attrValue));
			break;
		}
		updateInfo();
		// notifyAll();
	}

	@Override
	public String getInfo(String tabs) {
		String info = super.getInfo(tabs);
		info += ", startTime:" + startTime;
		info += "\n" + tabs + " Components:";
		for (ISoundElement o : this.soundList) {
			info += "\n" + o.getInfo(tabs + "  ");
		}
		info += "\n" + tabs + " Effects:";
		for (IEffect o : this.effectList) {
			info += "\n" + o.getInfo(tabs + "  ");
		}
		return info;
	}

	@Override
	public String getSrcCode() {
		String sCode = "";
		if (!exportedIds.contains(getuid())) {
			sCode += "Track " + getPrefixeduid() + " = new Track();\n";
			// setStarTime
			if (this.getStartTime() != 0.0) {
				sCode += getPrefixeduid() + ".setStartTime(" + this.getStartTime() + ");\n";
			}
			// Add waves
			for (ISoundElement o : this.soundList) {
				// keep a list of dumped elements to avoid redumping
				if (!exportedIds.contains(o.getuid())) {
					sCode += o.getSrcCode();
					exportedIds.add(o.getuid());
				}
				sCode += getPrefixeduid() + ".addSoundElement(" + o.getPrefixeduid() + ");\n";
			}
			// Add effects
			for (IEffect o : this.effectList) {
				if (!exportedIds.contains(o.getuid())) {
					sCode += o.getSrcCode();
					exportedIds.add(o.getuid());
				}
				sCode += getPrefixeduid() + ".addEffect(" + o.getPrefixeduid() + ");\n";
			}
			// sCode += "\n";
			exportedIds.add(getuid());
		}
		return sCode;
	}

	@Override
	public double getAmplitude(double t) {		
		return estimateAmplitude(t);
	}

	@Override
	public void setAmplitude(double amplitude) {
		//
	}

	@Override
	public double getAmplitude() {
		return estimateMaxAmplitude();
	}

	@Override
	public double getFreq() {
		return Collections.min(getFreqs());//we may have numerous freqs, just give lowest
	}
}