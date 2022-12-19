package com.k.noiseMaker;

import javax.swing.Box;
import javax.swing.JTextField;

class AddtoTrackInfoPanel extends BaseInfoPanel {
	private static final long serialVersionUID = 1L;

	AddtoTrackInfoPanel(INamedElement targetTrack,AddtoTrackActionHandler aHandler) {// First constructor only - top panel
		super(aHandler);
		int myUid = targetTrack.getuid();
		Box b = Box.createVerticalBox();
		addNonEditableField(myUid, "Add sub-element to track ", Integer.toString(myUid), b);
		Box b1 = Box.createHorizontalBox();		
		JTextField tExisting = addEditableField(myUid, "Existing", 0, b1,true);
		tExisting.setIgnoreRepaint(true);
		addButton(myUid, "Link", "Link",b1);
		addButton(myUid, "Clone", "Clone",b1);		
		Box b2 = Box.createHorizontalBox();
		addNonEditableField(myUid, "New", "", b2);		
		String[] choices = new String[6];
		choices[0] = "Track";
		choices[1] = "Wave";
		choices[2] = "EffectAmplify";
		choices[3] = "EffectEnveloppe";
		choices[4] = "EffectRepeat";
		choices[5] = "EffectReverb";
		addComboBox(myUid, "ElementType", "Wave", choices,b2,true);
		addButton(myUid, "Create", "Create",b2);
		b.add(b1);
		b.add(b2);
		this.add(b);		
	}

}
