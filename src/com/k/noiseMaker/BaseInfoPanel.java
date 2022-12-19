package com.k.noiseMaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

class BaseInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	// Formating constants
	final String addTabs = "     ";
	final Font textFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	final Insets noMargin = new Insets(0, 0, 0, 0);// No margin
	// Action handler used for all actions
	static BaseActionHandler aHandler;
	
	
	BaseInfoPanel() {
		super();
	}

	BaseInfoPanel(BaseActionHandler aHandler) {// top level
		BaseInfoPanel.aHandler = aHandler;
	}

	static String prettyPrint(boolean b) {
		return Boolean.toString(b);
	}

	static String prettyPrint(double d) {
		return String.format(Locale.US, "%.3f", d).replaceAll("(\\.\\d+?)0*$", "$1");
	}

	static String prettyPrint(int i) {
		return Integer.toString(i);
	}

	JButton addButton(int signaluid, String actionName, String displayValue) {
		return addButton(signaluid, actionName, displayValue, this);
	}

	JButton addButton(int signaluid, String actionName, String displayValue, JComponent target) {
		JButton button = new JButton();
		button.setText(displayValue);
		button.setName(prettyPrint(signaluid) + "_" + actionName);
		button.setToolTipText(button.getName());
		button.addActionListener(aHandler);
		button.setFont(textFont);
		button.setMargin(noMargin);// No margin
		target.add(button);
		return button;
	}

	JLabel addNonEditableField(int signaluid, String attrName, String attrValue) {
		return addNonEditableField(signaluid, attrName, attrValue, this);
	}

	JLabel addNonEditableField(int signaluid, String attrName, String attrValue, JComponent target) {
		JLabel label = new JLabel(attrName + " " + attrValue);
		label.setFont(textFont);
		target.add(label);
		return label;
	}

	JTextField addEditableField(int signaluid, String attrName, Object attrValue) {
		return addEditableField(signaluid, attrName, attrValue, this);
	}

	JTextField addEditableField(int signaluid, String attrName, Object attrValue, JComponent target) {
		return addEditableField(signaluid, attrName, attrValue, target,false);
	}
	JTextField addEditableField(int signaluid, String attrName, Object attrValue, JComponent target, Boolean noHandler) {
		Box b = Box.createHorizontalBox();
		JLabel LabelAttrName = new JLabel(attrName);
		LabelAttrName.setFont(textFont);
		b.add(LabelAttrName);
		JTextField TextAttrValue;
		//if (attrValue.length() > 0) {
			TextAttrValue = new JTextField(attrValue.toString());
		/*} else {
			TextAttrValue = new JTextField();
		}*/
		String textFieldName = prettyPrint(signaluid) + "_" + attrName;// parsed later to update the field
		TextAttrValue.setName(textFieldName);
		TextAttrValue.setToolTipText(textFieldName);
		TextAttrValue.setFont(textFont);
		if(!noHandler){
		TextAttrValue.addActionListener(aHandler);}
		b.add(TextAttrValue);
		target.add(b);
		return TextAttrValue;
	}

	JCheckBox addCheckBox(int signaluid, String attrName, Boolean attrValue) {
		return addCheckBox(signaluid, attrName, attrValue, this);
	}

	JCheckBox addCheckBox(int signaluid, String attrName, Boolean attrValue, JComponent target) {
		JCheckBox checkBox = new JCheckBox(attrName, attrValue);
		String textFieldName = prettyPrint(signaluid) + "_" + attrName;// parsed later to update the field
		checkBox.setName(textFieldName);
		checkBox.addActionListener(aHandler);
		checkBox.setFont(textFont);
		checkBox.setToolTipText(textFieldName);
		target.add(checkBox);
		return checkBox; 
	}

	JComboBox<String> addComboBox(int signaluid, String attrName, String attrValue, String[] attrValues) {
		return addComboBox(signaluid, attrName, attrValue, attrValues, this);
	}

	JComboBox<String> addComboBox(int signaluid, String attrName, String attrValue, String[] attrValues, JComponent target) {
		return addComboBox(signaluid, attrName, attrValue, attrValues, target,false);
	}
	JComboBox<String> addComboBox(int signaluid, String attrName, String attrValue, String[] attrValues, JComponent target, Boolean noHandler) {
		JComboBox<String> comboBox = new JComboBox<String>(attrValues);
		for (int i = 0; i < attrValues.length; i++) {
			if (attrValues[i].equals(attrValue)) {
				comboBox.setSelectedIndex(i);
			}
		}
		String boxName = prettyPrint(signaluid) + "_" + attrName;// parsed later to update the field
		comboBox.setName(boxName );
		comboBox.setFont(textFont);
		comboBox.setToolTipText(boxName );
		if(!noHandler){
		comboBox.addActionListener(aHandler);}
		target.add(comboBox);
		return comboBox;
	}

	JSlider addSlider(int signaluid, String attrName, double attrValue, double min, double max, int orientation) {
		return addSlider(signaluid, attrName, attrValue, min, max, orientation, "LINEAR", this);
	}

	JSlider addSlider(int signaluid, String attrName, double attrValue, double min, double max, int orientation, String type) {
		return addSlider(signaluid, attrName, attrValue, min, max, orientation, type, this);
	}

	JSlider addSlider(int signaluid, String attrName, double attrValue, double min, double max, int orientation, JComponent target) {
		return addSlider(signaluid, attrName, attrValue, min, max, orientation, "LINEAR", target);
	}

	JSlider addSlider(int signaluid, String attrName, double attrValue, double min, double max, int orientation, String type, JComponent target) {
		Box b = Box.createHorizontalBox();
		JLabel LabelAttrName = new JLabel(attrName);
		LabelAttrName.setFont(textFont);
		b.add(LabelAttrName);
		String sliderName = prettyPrint(signaluid) + "_" + attrName + "_" + type;// parsed later to update the field
		double smin = min;
		double smax = max;
		double sval = attrValue;
		if (type != null && type.equals("LOG")) {
			smin = FConstants.logBase(FConstants.FreqLogBase,smin + 1);
			smax = FConstants.logBase(FConstants.FreqLogBase,smax);
			sval = FConstants.logBase(FConstants.FreqLogBase,sval);
		}
		smin = Math.min(smin, sval);
		smax = Math.max(smax, sval);
		int sliderScale = FConstants.sliderScale;
		smin *= sliderScale;
		smax *= sliderScale;
		sval *= sliderScale;
		JSlider slider = new JSlider(orientation, (int) smin, (int) smax, (int) sval);
		// SwingConstants.VERTICAL or SwingConstants.HORIZONTAL
		slider.setName(sliderName);
		slider.setToolTipText(sliderName);
		slider.addChangeListener(aHandler);
		b.add(slider);
		// numeric value
		JTextField TextAttrValue = new JTextField(""+attrValue);
		//JTextField TextAttrValue = new JTextField(attrValue);
		String textFieldName = prettyPrint(signaluid) + "_" + attrName;// parsed later to update the field
		TextAttrValue.setName(textFieldName);
		TextAttrValue.setToolTipText(textFieldName);
		TextAttrValue.setFont(textFont);
		TextAttrValue.addActionListener(aHandler);
		b.add(TextAttrValue);
		// add to target
		target.add(b);
		return slider;
	}

	void formatBox(Box b) {
		b.setBorder(BorderFactory.createLineBorder(Color.gray));
	}



}
