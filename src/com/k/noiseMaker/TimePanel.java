package com.k.noiseMaker;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;

class TimePanel extends BaseInfoPanel {
	private static final long serialVersionUID = 1L;
	SharedArea sArea;
	JLabel timeDisplay;
	
	TimePanel(SharedArea sArea) {
		this.sArea = sArea;
		double currentTime = (double) sArea.get("currentTime");
		timeDisplay = addNonEditableField(0, "CurrentTime", ""+currentTime); 
		this.setMaximumSize(new Dimension(100, 10));//time panel is small
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		double currentTime = (double) sArea.get("currentTime");
		timeDisplay.setText(""+currentTime);
		// Logger.log("Repainting Graph ");
	}
}
