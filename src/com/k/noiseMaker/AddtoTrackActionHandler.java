package com.k.noiseMaker;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;

class AddtoTrackActionHandler extends BaseActionHandler implements ActionListener, FocusListener {
	static INamedElement targetTrack;
	IRebuildable popupFrame;

	AddtoTrackActionHandler(INamedElement track, SharedArea sArea, IRebuildable myFrame) {
		super(sArea, myFrame);
		AddtoTrackActionHandler.targetTrack = track;
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
		Track currentElement = (Track) targetTrack;// .getNamedElement(uid);
		// read fields
		Component existingUidComponent = popupFrame.getComponentByName(uid + "_Existing");
		int existingUid = Integer.parseInt(((JTextField) existingUidComponent).getText());
		INamedElement existingElement = targetTrack.getNamedElement(existingUid);//problem:existing  
		Component elementTypeComponent = popupFrame.getComponentByName(uid + "_ElementType");
		@SuppressWarnings("unchecked")
		String elementType = (String) ((JComboBox<String>) elementTypeComponent).getSelectedItem();
		// act
		switch (actionName) {
		case "Link":
			if (existingElement != null) {
				currentElement.addElement(existingElement);
			}else{Logger.log("Existing element "+existingUid+" not found");}
			break;
		case "Clone":
			if (existingElement != null) {
				//pause sample first
				sArea.put("PlayMode", "Pause");
				//clone
				currentElement.addElement(existingElement.clone());
				//unpause
				sArea.put("PlayMode", "Play");
				sArea.wakeUp();// UnPause
			}else{Logger.log("Existing element "+existingUid+" not found");}
			break;
		case "Create":
			switch (elementType) {
			case "Track":
				currentElement.addElement(new Track());
				break;
			case "Wave":
				currentElement.addElement(new Wave());
				break;
			case "EffectAmplify":
				currentElement.addElement(new EffectAmplify());
				break;
			case "EffectEnveloppe":
				currentElement.addElement(new EffectEnveloppe());
				break;
			case "EffectRepeat":
				currentElement.addElement(new EffectRepeat());
				break;
			case "EffectReverb":
				currentElement.addElement(new EffectReverb());
				break;
			default:
				break;
			}
			break;
		default:
			Logger.log("Unhandled action:" + actionName);
			break;
		}
		// close add popup
		popupFrame.Close();
		((BaseJFrame)sArea.get("MainFrame")).RebuildAll();//rebuild main
		//also rebuild caller (where we click "+" )
		if ((sArea.get("toRebuild")).getClass().getSimpleName()=="DashboardFrame"){
			((DashboardFrame)sArea.get("toRebuild")).RebuildAll();//rebuild original caller of the popup
		}
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
