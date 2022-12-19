package com.k.noiseMaker;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;

class ElementActionHandler extends BaseActionHandler implements ActionListener, FocusListener {
	INamedElement element;

	ElementActionHandler(INamedElement element, SharedArea sArea, IRebuildable dashboardFrame) {
		super(sArea, dashboardFrame);
		this.element = element;
	}

	@Override
	public void applyAction(JButton button) {
		// addButton(myUid, "Collapse", ""+(!track.isCollapsed()));
		String fieldName = button.getName();
		String[] parts = fieldName.split("_");
		int uid = Integer.parseInt(parts[0]);
		String actionName = parts[1];
		Logger.log(uid + ":action:" + actionName);
		INamedElement currentElement = element.getNamedElement(uid);
		if (currentElement != null) {
			// double tMax = 1.0;
			switch (actionName) {
			case "Collapse":
				((Track) currentElement).setCollapsed(!((Track) currentElement).isCollapsed());
				rebuildParent();//we only rebuild when needed
				break;
			case "AddTrackElement":
				//Logger.log("AddElement popup");
				new AddtoTrackPopup(currentElement, sArea);
				sArea.put("toRebuild", parentFrame);//save frame where we click '+' for future rebuild
				//rebuildParent();//we only rebuild when needed
				break;
			case "AddWaveElement":
				//Logger.log("AddElement popup");
				new AddtoWavePopup(currentElement, sArea);
				sArea.put("toRebuild", parentFrame);//save frame where we click '+' for future rebuild
				///rebuildParent();//we only rebuild when needed
				break;				
			case "RemoveElement":
				//int uidParent = uid;
				int uidToRemove = Integer.parseInt(parts[2]);
				((Track) currentElement).remove(uidToRemove);
				rebuildParent();//we only rebuild when needed
				break;
			case "TrackGraph":
				new ElementDashboard(currentElement, sArea);
				break;
			case "WaveGraph":
				new ElementDashboard(currentElement, sArea);
				break;
			case "EnvGraph":
				new ElementDashboard(currentElement, sArea);
				break;
			default:
				Logger.log("Unhandled action:" + actionName);
				break;
			}
			
		} else {// neither component or effect
			Logger.log("Element not found:" + uid);
		}

	}

	@Override
	public void applyAction(JTextField textField) {
		String fieldName = textField.getName();
		String attrValue = textField.getText();
		//double attrValue = (double)textField.getValue();
		String[] parts = fieldName.split("_");
		int uid = Integer.parseInt(parts[0]);
		String attrName = parts[1];
		Logger.log(uid + ":set " + attrName + "=" + attrValue);
		INamedElement o = element.getNamedElement(uid);
		if (o != null) {
			o.setAttribute(attrName, attrValue);
			updateOtherFields(fieldName, Double.parseDouble(attrValue));
			//sArea.wakeUp();
		}
	}

	void updateOtherFields(String fieldName, double attrValue){
		//String[] parts = fieldName.split("_");
		//int uid = Integer.parseInt(parts[0]);
		//Logger.log(uid + ": publish " + fieldName + "=" + attrValue);
		//update slider value if exists (eg for wave freq / phase / amplitude)
		
		//try LOG sliders 
		if(fieldName.contains("_Freq") ||fieldName.contains("_Phase") ||fieldName.contains("_Amplitude") ){
			double  sliderVal = FConstants.logBase(FConstants.FreqLogBase,attrValue);//get log of original value
			List<Component> sliderList = this.parentFrame.getComponentsByName(fieldName+"_LOG");
			for (Component slider : sliderList) {		
				//Logger.log(uid + ": publish " + fieldName + "=" + sliderVal);
				((JSlider)slider).setValue((int)Math.round(sliderVal*FConstants.sliderScale));
			}
		}		
		//now LINEAR sliders
		if(fieldName.contains("_Freq") ||fieldName.contains("_Phase") ||fieldName.contains("_Amplitude") ){
			List<Component> sliderList = this.parentFrame.getComponentsByName(fieldName+"_LINEAR");
			for (Component slider : sliderList) {		
				//Logger.log(uid + ": publish " + fieldName + "=" + sliderVal);
				((JSlider)slider).setValue((int)Math.round(attrValue*FConstants.sliderScale));
			}
		}
		//update text field value if exists (eg for wave freq) 
		List<Component> textBoxList = this.parentFrame.getComponentsByName(deriveTextBoxName(fieldName));
		for (Component textBox : textBoxList) {
			((JTextField)textBox).setText(""+attrValue);
		}
	}
	
	private String deriveTextBoxName(String sliderName){
		String[] tmp = sliderName.split("_");
		String textBoxName=tmp[0]+"_"+tmp[1];
		return textBoxName;
	}
	
	@Override
	public void applyAction(JSlider slider) {
		String fieldName = slider.getName();
		double sliderScale = FConstants.sliderScale;
		double attrValue = (double)slider.getValue()/sliderScale;//physical value of the slider
		String[] parts = fieldName.split("_");
		int uid = Integer.parseInt(parts[0]);
		String attrName = parts[1];
		String type = parts[2];
		//Logger.log(uid + ":read " + attrName + "=" + attrValue);
		if(type!=null && type.equals("LOG")){//if using log scale, we need to pow to get the logical value
			double newAttrValue = Math.pow(FConstants.FreqLogBase,attrValue);
			//Logger.log(uid + ":read convert " + attrName + ":" + attrValue + "=>" + newAttrValue);
			attrValue = newAttrValue;
		}
		Logger.log(uid + ":set " + attrName + "=" + attrValue);
		INamedElement o = element.getNamedElement(uid);
		if (o != null) {
			o.setAttribute(attrName, Double.toString(attrValue));
			updateOtherFields(uid+"_"+attrName, attrValue);	
			//sArea.wakeUp();
		}
	}
	
	@Override
	public void applyAction(JCheckBox checkBox) {
		String fieldName = checkBox.getName();
		//String attrValue = Boolean.toString(checkBox.isSelected());
		String attrValue = Boolean.toString(checkBox.isSelected());
		String[] parts = fieldName.split("_");
		int uid = Integer.parseInt(parts[0]);
		String attrName = parts[1];
		Logger.log(uid + ":set " + attrName + "=" + attrValue);
		INamedElement o = element.getNamedElement(uid);
		if (o != null) {
			o.setAttribute(attrName, attrValue);
			//sArea.wakeUp();
		}
	}
	
	@Override
	public void applyAction(JComboBox<String> source){
		String fieldName = source.getName();
		String attrValue = (String)source.getSelectedItem();
		String[] parts = fieldName.split("_");
		int uid = Integer.parseInt(parts[0]);
		String attrName = parts[1];
		Logger.log(uid + ":set " + attrName + "=" + attrValue);
		INamedElement o = element.getNamedElement(uid);
		if (o != null) {
			o.setAttribute(attrName, attrValue);
			//sArea.wakeUp();
		}
	}
}
