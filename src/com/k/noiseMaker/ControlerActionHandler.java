package com.k.noiseMaker;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;

class ControlerActionHandler extends BaseActionHandler implements ActionListener, FocusListener {
	ControlerActionHandler(SharedArea sArea, IRebuildable dashboardFrame) {
		super(sArea, dashboardFrame);
	}

	@Override
	public void applyAction(JButton button) {
		// addButton(myUid, "Collapse", ""+(!track.isCollapsed()));
		String fieldName = button.getName();
		String[] parts = fieldName.split("_");
		int uid = Integer.parseInt(parts[0]);
		String actionName = parts[1];
		Logger.log(uid + ":action:" + actionName);
		switch (actionName) {
			case "TimeReset":
				sArea.put("currentTime", 0.0);
				sArea.wakeUp();// UnPause
				break;		
			case "Backward":
				sArea.put("PlayMode", "Backward");
				sArea.wakeUp();// UnPause
				break;
			case "Play":
				sArea.put("PlayMode", "Play");
				sArea.wakeUp();// UnPause
				break;
			case "Pause":
				sArea.put("PlayMode", "Pause");
				break;
			case "Forward":
				sArea.put("PlayMode", "Forward");
				sArea.wakeUp();// UnPause
				break;
			case "Record":
				Boolean bRecording = (Boolean) sArea.get("Recording");
				if (!bRecording) {
					new Writer(sArea);// run writer
					sArea.wakeUp();// UnPause
				} else {
					Logger.log("Writer already recording");
				}
				break;
			case "StopRecord":
				sArea.put("StopRecord", "StopRecord");
				break;
			case "Dump"://TODO deplacer methode dans la classe MainDashBoard:MainFrame
				Logger.log("//=============DUMP==============");
				Track Ltrack = ((MainFrame)this.parentFrame).left;
				Track Rtrack = ((MainFrame)this.parentFrame).right;
				Ltrack.getSrcCodeReset();//Reset list of exported objects
				Logger.log("//=============LEFT==============");
				Logger.log(Ltrack.getSrcCode());								
				Logger.log("//=============/LEFT==============");				
				if (Ltrack.getuid() != Rtrack.getuid()){
					Logger.log("//=============RIGHT==============");
					Logger.log(Rtrack.getSrcCode());
					Logger.log("//=============/RIGHT==============");
				}
				//Logger.log("Track Ltrack = "+Ltrack.getPrefixeduid()+";");
				//Logger.log("Track Rtrack = "+Rtrack.getPrefixeduid()+";");
				Logger.log("Ltrack = "+Ltrack.getPrefixeduid()+";");
				Logger.log("Rtrack = "+Rtrack.getPrefixeduid()+";");
				Logger.log("//=============/DUMP==============");
				break;
			default:
				Logger.log("Unhandled action:" + actionName);
				break;
			
		}
		Logger.log("currentTime:" + sArea.get("currentTime"));
		// rebuildParent(); //unNeeded for those actions
	}

	@Override
	public void applyAction(JTextField textField) {

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
