package com.k.noiseMaker;

import javax.swing.Box;
import javax.swing.JTextField;

class AddtoWaveInfoPanel extends BaseInfoPanel {
	private static final long serialVersionUID = 1L;

	AddtoWaveInfoPanel(INamedElement targetWave,AddtoWaveActionHandler aHandler) {// First constructor only - top panel
		super(aHandler);
		int myUid = targetWave.getuid();
		Box mainBox = Box.createVerticalBox();
		addNonEditableField(myUid, "Add sub-element to wave ", Integer.toString(myUid), mainBox);
		//line1 - type of lfo
		Box line1 = Box.createHorizontalBox();
		addNonEditableField(myUid, "LFO Type", "", line1);		
		String[] choices = new String[3];
		choices[0] = "amplLfo";
		choices[1] = "freqLfo";
		choices[2] = "phaseLfo";
		addComboBox(myUid, "ElementType", "Wave", choices,line1,true);
		//line2
		Box line2 = Box.createHorizontalBox();		
		JTextField tExisting = addEditableField(myUid, "Existing", 0, line2,true);
		tExisting.setIgnoreRepaint(true);
		addButton(myUid, "Link", "Link",line2);
		addButton(myUid, "Clone", "Clone",line2);		
		//line3
		Box line3 = Box.createHorizontalBox();	
		addButton(myUid, "Create", "Create",line3);
		//		
		mainBox.add(line1);
		mainBox.add(line2);
		mainBox.add(line3);
		this.add(mainBox);		
	}

}
