package com.k.noiseMaker;

import java.awt.Dimension;

import javax.swing.Box;

class ControlerInfoPanel extends BaseInfoPanel {
	private static final long serialVersionUID = 1L;
	ControlerInfoPanel(BaseActionHandler aHandler) {// First constructor only - top panel
		super(aHandler);
		Box b = Box.createVerticalBox();
		Box b1 = Box.createHorizontalBox();
		addButton(0, "TimeReset", "|<-",b1);
		addButton(0, "Backward", "<<",b1);
		addButton(0, "Play", ">",b1);
		addButton(0, "Pause", "||",b1);
		addButton(0, "Forward", ">>",b1);		
		Box b2 = Box.createHorizontalBox();
		addButton(0, "Record", "Record",b2);
		addButton(0, "StopRecord", "Stop",b2);
		addButton(0, "Dump", "Dump",b2);		
		b.add(b1);
		b.add(b2);
		this.add(b);
		//set my own size
		this.setMaximumSize(new Dimension(100, 10));//control panel is small
	}
}
