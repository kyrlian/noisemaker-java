package com.k.noiseMaker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

abstract class BaseActionHandler implements ActionListener, FocusListener, ChangeListener {
	static SharedArea sArea;
	IRebuildable parentFrame;

	BaseActionHandler(SharedArea sArea, IRebuildable dashboardFrame) {
		BaseActionHandler.sArea = sArea;
		this.parentFrame = dashboardFrame;
	}

	void rebuildParent() {
		parentFrame.RebuildAll();
		//sArea.wakeUp();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent aEvent) {
		// aEvent.getActionCommand()
		Object eventSrc = aEvent.getSource();
		String srcClass = eventSrc.getClass().getSimpleName();
		//Logger.log("srcClass:" + srcClass);
		switch (srcClass) {
			case "JTextField":
				// aEvent.getActionCommand()
				applyAction((JTextField) eventSrc);
				break;
			case "JButton":
				applyAction( (JButton) eventSrc);
				break;
			case "JCheckBox":
				applyAction((JCheckBox)eventSrc);
				break;
			case "JComboBox":
				applyAction((JComboBox<String>)eventSrc);
				break;
			default:
				Logger.log("Unhandled input object:" + srcClass);
				break;
		}
		//rebuildParent();//this causes add to redisplay :(
	}

	public void stateChanged(ChangeEvent aEvent) {
		// aEvent.getActionCommand()
		Object eventSrc = aEvent.getSource();
		String srcClass = eventSrc.getClass().getSimpleName();
		//Logger.log("srcClass:" + srcClass);
		switch (srcClass) {
			case "JSlider":
				JSlider slider = (JSlider) aEvent.getSource();
				applyAction(slider);
				break;
			default:
				Logger.log("Unhandled input object:" + srcClass);
				break;
		}
		//rebuildParent();//Doesnt work if rebuild
	}

	abstract void applyAction(JButton button);
	abstract void applyAction(JTextField textField);
	abstract void applyAction(JSlider slider) ;
	abstract void applyAction(JCheckBox checkBox); 
	abstract void applyAction(JComboBox<String> source);
	
	@Override
	public void focusGained(FocusEvent arg0) {
	}

	@Override
	public void focusLost(FocusEvent arg0) {
	}
}
