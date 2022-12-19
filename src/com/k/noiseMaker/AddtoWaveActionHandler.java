package com.k.noiseMaker;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;

class AddtoWaveActionHandler extends BaseActionHandler implements ActionListener, FocusListener {
	static INamedElement targetWave;
	IRebuildable popupFrame;

	AddtoWaveActionHandler(INamedElement wave, SharedArea sArea, IRebuildable myFrame) {
		super(sArea, myFrame);
		AddtoWaveActionHandler.targetWave = wave;
		this.popupFrame = myFrame;
	}

	@Override
	public void applyAction(JButton button) {
		// addButton(myUid, "Collapse", ""+(!track.isCollapsed()));
		String fieldName = button.getName();
		String[] parts = fieldName.split("_");
		int uid = Integer.parseInt(parts[0]);
		String actionName = parts[1];
		Logger.log(uid + ":action:" + actionName);
		Wave currentElement = (Wave) targetWave;// .getNamedElement(uid);
		// read fields
		Component existingUidComponent = popupFrame.getComponentByName(uid + "_Existing");
		int existingUid = Integer.parseInt(((JTextField) existingUidComponent).getText());
		INamedElement existingElement = targetWave.getNamedElement(existingUid);//problem:existing  
		Component elementTypeComponent = popupFrame.getComponentByName(uid + "_ElementType");
		@SuppressWarnings("unchecked")
		String elementType = (String) ((JComboBox<String>) elementTypeComponent).getSelectedItem();
		// act
		Wave newWave = null;
		switch (actionName) {
		case "Link":
			if (existingElement != null) {
				newWave = (Wave)existingElement ;
			}else{Logger.log("Existing element "+existingUid+" not found");}
			break;
		case "Clone":
			if (existingElement != null) {
				//pause sample first
				sArea.put("PlayMode", "Pause");
				//clone
				newWave = ((Wave)existingElement).clone();
				//unpause
				sArea.put("PlayMode", "Play");
				sArea.wakeUp();// UnPause
			}else{Logger.log("Existing element "+existingUid+" not found");}
			break;
		case "Create":
			newWave = new Wave();
			break;
		default:
			Logger.log("Unhandled action:" + actionName);
			break;			
		}
		switch (elementType) {
		case "amplLfo":
			currentElement.setLFO(LFOType.AMPL, newWave);//,PHASE,AMPL
			break;
		case "freqLfo":
			currentElement.setLFO(LFOType.FREQ, newWave);//,PHASE,AMPL
			break;
		case "phaseLfo":
			currentElement.setLFO(LFOType.PHASE, newWave);//,PHASE,AMPL
			break;
		default:
			Logger.log("Unhandled elementType:" + elementType);
			break;		
		}
		// close add popup
		popupFrame.Close();
		((BaseJFrame)sArea.get("MainFrame")).RebuildAll();//rebuild main
		//also rebuild caller (where we click "+" )
		//if ((sArea.get("toRebuild")).getClass().getSimpleName()=="DashboardFrame")
		((DashboardFrame)sArea.get("toRebuild")).RebuildAll();//rebuild original caller of the popup
		
	}

	@Override
	public void applyAction(JTextField textField) {//None

	}

	@Override
	void applyAction(JSlider slider) {// None

	}

	@Override
	void applyAction(JCheckBox checkBox) {// None

	}

	@Override
	void applyAction(JComboBox<String> source) {// None

	}

}
