package com.k.noiseMaker;

 final class FSoundBlocks {

	 static Track chromaticScale() {
		Track track = new Track();
		EffectEnveloppe e = new EffectEnveloppe(.1, .1, .5, .3, .1);
		double A = 220;
		double semitone = A / 12;
		for (int i = 0; i < 12; i++) {
			double f = A + i * semitone;			
			Wave w =new Wave(f);
			track.addSoundElement(new Track(w, e, i * e.getLength() / 2.0 + .5));// 300 Hz, with enveloppe, starting at 2.0 seconds
		}
		return track;
	}

	 static Track chromaticScaleHarmonics() {
		Track track = new Track();
		EffectEnveloppe e = new EffectEnveloppe(.1, .2, .5, .4, .1);
		//LFO lfo = new LFO(ShapeType.SIN,.5,.01);
		double A = 220;
		double semitone = A / 12.0;
		for (int i = 0; i < 12; i++) {
			double f = A + i * semitone;
			Track w = new Track(f, 4);//4 harmonics
			w.addEffect(e);
			w.setStartTime(i * e.getLength() / 1.5 + .5);
			track.addSoundElement(w);// 300 Hz, with enveloppe, starting at 2.0 seconds
		}
		return track;
	}
	 
	 static Track chromaticScaleReverse() {
		Track track = new Track();
		EffectEnveloppe e = new EffectEnveloppe(.1, .1, .5, .3, .1);
		//LFO lfo = new LFO(ShapeType.SIN,.5,.01);
		double A = 220;
		double semitone = A / 12.0;
		for (int i = 0; i < 12; i++) {
			double f = A + i * semitone;			
			Wave w =new Wave(f);			
			track.addSoundElement(new Track(w, e, (12-i) * e.getLength() / 2.0 + .5));// 300 Hz, with enveloppe, starting at 2.0 seconds
		}
		return track;
	}
	 
	 static Track harmonics() {
		Track track = new Track(220, 4);
		EffectEnveloppe e = new EffectEnveloppe(.1, .1, .5, .3, .1);
		track.addEffect(e);
		return track;
	}

	 static Track whiteNoise() {
		 Wave w =new Wave(ShapeType.RND, 1.0, 1.0, 1.0);
		 Track track = new Track(w);
		return track;
		 
	 }
	 static Track simpleTrack() {
		EffectEnveloppe e = new EffectEnveloppe(.1, .1, .5, .1, .1);
		Wave w440 = new Wave("w440",440);//wave at 440hz	
		Track s440 = new Track(w440, e);//wave at 440hz, with enveloppe
		s440.setName("s440");
		Track track = new Track(s440,0.01);//wave at 440hz, with enveloppe, starting at .5
		//track.add(new Track(new Track(new Wave(220),e),1.0));//300 Hz, with enveloppe, starting at 2.0 seconds
		return track;
	 }
	 
	 static Track loopedTrack() {
		Track track = simpleTrack();
		((Track) track.getNamedElement("s440")).addEffect(new EffectRepeat());
		track.addEffect(new EffectRepeat());
		//track.updateInfo();//force update because I changed a subcomponent
		return track;
	 }
	 
}
