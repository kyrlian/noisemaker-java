package com.k.noiseMaker;

import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

class ElementInfoPanel extends BaseInfoPanel {
	private static final long serialVersionUID = 1L;

	ElementInfoPanel(INamedElement element, ElementActionHandler aHandler) {// First constructor only - top panel
		this(element, aHandler, true, true, true);
	}

	ElementInfoPanel(INamedElement element, ElementActionHandler aHandler, boolean showEffects, boolean showComponents, boolean recurseComponents) {// top level
		super(aHandler);
		String elementType = element.getType();
		ElementInfoPanel main = null;
		switch (elementType) {
		case "Track":
			main = new ElementInfoPanel((Track) element, "", showEffects, showComponents, recurseComponents);
			break;
		case "SignalSimpleOscillator":
			main = new ElementInfoPanel((SignalSimpleOscillator) element, "");
			break;
		case "SignalComplexOscillator":
			main = new ElementInfoPanel((SignalComplexOscillator) element, "");
			break;
		case "Wave":
			main = new ElementInfoPanel((Wave) element, "");
			break;
		case "EffectAmplify":
			main = new ElementInfoPanel((EffectAmplify) element, "");
			break;
		case "EffectEnveloppe":
			main = new ElementInfoPanel((EffectEnveloppe) element, "");
			break;
		case "EffectRepeat":
			main = new ElementInfoPanel((EffectRepeat) element, "");
			break;
		case "EffectReverb":
			main = new ElementInfoPanel((EffectReverb) element, "");
			break;
		default:
			Logger.log("Unknwon element type " + elementType);
			break;
		}
		this.add(main);
	}

	ElementInfoPanel createEffectPanel(IEffect eff, String tabs) {
		ElementInfoPanel subTrackInfoPanel;
		String effType = eff.getType();
		if (effType.equals("EffectAmplify")) {
			subTrackInfoPanel = new ElementInfoPanel((EffectAmplify) eff, addTabs + tabs);
		} else if (effType.equals("EffectEnveloppe")) {
			subTrackInfoPanel = new ElementInfoPanel((EffectEnveloppe) eff, addTabs + tabs);
		} else if (effType.equals("EffectRepeat")) {
			subTrackInfoPanel = new ElementInfoPanel((EffectRepeat) eff, addTabs + tabs);
		} else if (effType.equals("EffectReverb")) {
			subTrackInfoPanel = new ElementInfoPanel((EffectReverb) eff, addTabs + tabs);
		} else {
			subTrackInfoPanel = new ElementInfoPanel((BaseNamedElement) eff, addTabs + tabs);
		}
		return subTrackInfoPanel;
	}

	ElementInfoPanel(Track track, String tabs, boolean showEffects, boolean showComponents, boolean recurseComponents) {
		// this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));// PAGE_AXIS));
		Box mainBox = Box.createVerticalBox();
		formatBox(mainBox);
		ElementInfoPanel headInfo = new ElementInfoPanel(track, tabs);// This track info
		mainBox.add(headInfo);
		int myUid = track.getuid();
		if (!track.isCollapsed()) {
			if (showEffects) {
				for (IEffect eff : track.effectList) {// Effects
					ElementInfoPanel subTrackInfo = createEffectPanel(eff, tabs);
					addButton(myUid, "RemoveElement_" + eff.getuid(), "-", subTrackInfo);
					mainBox.add(subTrackInfo);
				}
			}
			if (showComponents) {
				for (ISoundElement so : track.soundList) {// Components
					ElementInfoPanel subTrackInfo;
					if (so.isWave()) {
						subTrackInfo = new ElementInfoPanel((Wave) so, addTabs + tabs);
					} else if (so.isTrack()) {
						subTrackInfo = new ElementInfoPanel((Track) so, addTabs + tabs, showEffects && recurseComponents, recurseComponents, recurseComponents);// if
																																								// recurse,recurse
					} else {
						subTrackInfo = new ElementInfoPanel((BaseNamedElement) so, addTabs + tabs);// should be unused
					}
					addButton(myUid, "RemoveElement_" + so.getuid(), "-", subTrackInfo);
					mainBox.add(subTrackInfo);
				}
			}
		}
		this.add(mainBox);
	}

	ElementInfoPanel(Track track, String tabs) {// this track info
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		// this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		int myUid = track.getuid();
		// if (track.isCollapsed() || tabs != "") {// not for top level - except if collapsed
		addButton(myUid, "TrackGraph", "o");
		// }
		// tabs
		addNonEditableField(myUid, tabs, "");
		// Collapse icon
		String collapseIcon = "v";
		if (track.isCollapsed()) {
			collapseIcon = ">";
		}
		addButton(myUid, "Collapse", collapseIcon);
		// Fields
		addNonEditableField(myUid, "Track", prettyPrint(myUid));
		addEditableField(myUid, "Name", track.getName());
		addEditableField(myUid, "StartTime", prettyPrint(track.getStartTime()));
		addNonEditableField(myUid, "Length", prettyPrint(track.getLazyLength()));
		// Add
		addButton(myUid, "AddTrackElement", "+");
	}

	ElementInfoPanel(SignalSimpleOscillator wave, String tabs) {
		this(wave, tabs, true);
	}

	ElementInfoPanel(SignalSimpleOscillator wave, String tabs, Boolean analogic) {
		// this.setLayout(new FlowLayout(FlowLayout.LEADING));
		// this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		boolean isTop = (tabs.equals(""));
		int myUid = wave.getuid();
		addNonEditableField(myUid, tabs, "");
		Box mainBox;
		if (isTop) {
			mainBox = Box.createVerticalBox();
		} else {
			addButton(myUid, "WaveGraph", "o");
			mainBox = Box.createHorizontalBox();
		}
		Box waveBox = Box.createVerticalBox();
		Box waveInfoBox = Box.createHorizontalBox();
		addNonEditableField(myUid, "Wave", prettyPrint(myUid), waveInfoBox);
		addEditableField(myUid, "Name", wave.getName(), waveInfoBox);
		// addEditableField(myUid, "Shape", wave.getShape());
		addComboBox(myUid, "Shape", wave.getShape(), ShapeType.getAsList(), waveInfoBox);
		//addNonEditableField(myUid, "Length", prettyPrint(wave.getLazyLength()), waveInfoBox);
		waveBox.add(waveInfoBox);
		if (analogic) {
			Box sliders = Box.createVerticalBox();
			addSlider(myUid, "Freq", wave.getFreq(), FConstants.MinFreq, FConstants.MaxFreq, SwingConstants.HORIZONTAL, "LOG", sliders);
			addSlider(myUid, "Phase", wave.getPhase(), -1, 1, SwingConstants.HORIZONTAL, sliders);
			addSlider(myUid, "Amplitude", wave.getAmplitude(), 0, 1, SwingConstants.HORIZONTAL, sliders);
			waveBox.add(sliders);
		} else {
			Box sliders = Box.createHorizontalBox();
			addEditableField(myUid, "Freq", prettyPrint(wave.getFreq()), sliders);
			addEditableField(myUid, "Phase", prettyPrint(wave.getPhase()), sliders);
			addEditableField(myUid, "Amplitude", prettyPrint(wave.getAmplitude()), sliders);
			waveBox.add(sliders);
		}
		formatBox(waveBox);
		mainBox.add(waveBox);
		// cant add on simple signal
		//addButton(myUid, "AddWaveElement", "+",mainBox);
		this.add(mainBox);
	}
	
	ElementInfoPanel(SignalComplexOscillator wave, String tabs) {
		this(wave, tabs, true);
	}

	ElementInfoPanel(SignalComplexOscillator wave, String tabs, Boolean analogic) {
		// this.setLayout(new FlowLayout(FlowLayout.LEADING));
		// this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		boolean isTop = (tabs.equals(""));
		int myUid = wave.getuid();
		addNonEditableField(myUid, tabs, "");
		Box mainBox;
		if (isTop) {
			mainBox = Box.createVerticalBox();
		} else {
			addButton(myUid, "WaveGraph", "o");
			mainBox = Box.createHorizontalBox();
		}
		Box waveBox = Box.createVerticalBox();
		Box waveInfoBox = Box.createHorizontalBox();
		addNonEditableField(myUid, "Wave", prettyPrint(myUid), waveInfoBox);
		addEditableField(myUid, "Name", wave.getName(), waveInfoBox);
		// addEditableField(myUid, "Shape", wave.getShape());
		addComboBox(myUid, "Shape", wave.getShape(), ShapeType.getAsList(), waveInfoBox);
		//addNonEditableField(myUid, "Length", prettyPrint(wave.getLazyLength()), waveInfoBox);
		waveBox.add(waveInfoBox);
		if (analogic) {
			Box sliders = Box.createVerticalBox();
			addSlider(myUid, "Freq", wave.getFreq(), FConstants.MinFreq, FConstants.MaxFreq, SwingConstants.HORIZONTAL, "LOG", sliders);
			addSlider(myUid, "Phase", wave.getPhase(), -1, 1, SwingConstants.HORIZONTAL, sliders);
			addSlider(myUid, "Amplitude", wave.getAmplitude(), 0, 1, SwingConstants.HORIZONTAL, sliders);
			waveBox.add(sliders);
		} else {
			Box sliders = Box.createHorizontalBox();
			addEditableField(myUid, "Freq", prettyPrint(wave.getFreq()), sliders);
			addEditableField(myUid, "Phase", prettyPrint(wave.getPhase()), sliders);
			addEditableField(myUid, "Amplitude", prettyPrint(wave.getAmplitude()), sliders);
			waveBox.add(sliders);
		}
		formatBox(waveBox);
		mainBox.add(waveBox);
		// Display LFOs if applicable
		// analogic = false;
		if (isTop) {
			buildLFOpanel(wave, LFOType.AMPL, tabs, analogic, mainBox);
			buildLFOpanel(wave, LFOType.FREQ, tabs, analogic, mainBox);
			buildLFOpanel(wave, LFOType.PHASE, tabs, analogic, mainBox);
		}
		// Add
		addButton(myUid, "AddWaveElement", "+",mainBox);
		this.add(mainBox);
	}

	void buildLFOpanel(SignalComplexOscillator wave, LFOType t, String tabs, Boolean analogic, JComponent target) {
		ISignal lfo = wave.getLFO(t);
		if (lfo != null) {
			Box sliders = Box.createVerticalBox();
			int myUid = lfo.getuid();
			Box top = Box.createHorizontalBox();
			addButton(myUid, "WaveGraph", "o", top);
			addNonEditableField(myUid, "LFO", t.toString(), top);			
			if (lfo.getClass().getSimpleName().contains("Oscillator")){			
				addComboBox(myUid, "Shape", ((SignalSimpleOscillator)lfo).getShape(), ShapeType.getAsList(), top);
				sliders.add(top);
				if (analogic) {
					addSlider(myUid, "Freq", ((SignalSimpleOscillator)lfo).getFreq(), 0, wave.getFreq(), SwingConstants.HORIZONTAL, "LINEAR", sliders);//TODO better LINEAR or LOG for freq slider ?
					addSlider(myUid, "Amplitude", lfo.getAmplitude(), 0, wave.getFreq(), SwingConstants.HORIZONTAL, sliders);
				} else {
					addEditableField(myUid, "Freq", prettyPrint(((SignalSimpleOscillator)lfo).getFreq()), sliders);
					addEditableField(myUid, "Amplitude", prettyPrint(lfo.getAmplitude()), sliders);
				}
				formatBox(sliders);
				target.add(sliders);
			}
		}
	}

	ElementInfoPanel(BaseNamedElement eff, String tabs) {
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		int myUid = eff.getuid();
		addEditableField(myUid, "Name", eff.getName());
	}

	ElementInfoPanel(EffectAmplify eff, String tabs) {
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		// this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		int myUid = eff.getuid();
		addNonEditableField(myUid, tabs + "Amplify", prettyPrint(myUid));
		addEditableField(myUid, "Name", eff.getName());
		addEditableField(myUid, "Factor", prettyPrint(eff.getFactor()));
	}

	ElementInfoPanel(EffectEnveloppe env, String tabs) {
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		// this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		int myUid = env.getuid();
		addNonEditableField(myUid, tabs, "");
		if (tabs != "") {// not for top level
			addButton(myUid, "EnvGraph", "G");
		}
		addNonEditableField(myUid, "Enveloppe", prettyPrint(myUid));
		addEditableField(myUid, "Name", env.getName());
		addEditableField(myUid, "AttackDuration", prettyPrint(env.getAttackDuration()));
		addEditableField(myUid, "DecayDuration", prettyPrint(env.getDecayDuration()));
		addEditableField(myUid, "SustainValue", prettyPrint(env.getSustainValue()));
		addEditableField(myUid, "SustainDuration", prettyPrint(env.getSustainDuration()));
		addEditableField(myUid, "ReleaseDuration", prettyPrint(env.getReleaseDuration()));

	}

	ElementInfoPanel(EffectRepeat eff, String tabs) {
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		// this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		int myUid = eff.getuid();
		addNonEditableField(myUid, tabs + "RepeatDelay", prettyPrint(myUid));
		addEditableField(myUid, "Name", eff.getName());
		// addEditableField(myUid, "Repeat", prettyPrint(eff.isRepeat()));
		//addCheckBox(myUid, "Repeat", eff.isRepeat());
		addEditableField(myUid, "RepeatDelay", prettyPrint(eff.getRepeatDelay()));
	}

	ElementInfoPanel(EffectReverb eff, String tabs) {
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		// this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		int myUid = eff.getuid();
		addNonEditableField(myUid, tabs + "Reverb", prettyPrint(myUid));
		addEditableField(myUid, "Name", eff.getName());
		addEditableField(myUid, "Reverb", prettyPrint(eff.getDelay()));
	}

}
