package com.k.noiseMaker;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

class DashboardFrame extends BaseJFrame implements IRebuildable {
	private static final long serialVersionUID = 1L;
	Box topBox;
	ElementInfoPanel elementInfoPanel;
	INamedElement element;
	ElementActionHandler aHandler;
	//SharedArea sArea;//in base

	DashboardFrame(INamedElement element, SharedArea sArea) {
		this.sArea = sArea;
		String title = element.getType() + " " + element.getuid() + " " + element.getName();
		this.setTitle(title);
		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//if you close the editor, you close all and terminate the program
		this.setSize(400, 800);// w,h
		// this.setLocationRelativeTo((Component) sArea.get("MainFrame"));
		this.setLocationByPlatform(true);
		this.element = element;
		// Actions handler
		this.aHandler = new ElementActionHandler(element, sArea, this);
		// Info Panel
		//buildAll();
		RebuildAll();//will call build all plus tools
	}

	void RefreshTop() {
		topBox.repaint();
	}

	@Override
	public void buildAll() {
		topBox = Box.createHorizontalBox();
		TracerPanel tracerPanelF = new TracerPanel((IDrawable) element, sArea);// auto graph (freq for tracks, value for other)
		topBox.add(tracerPanelF);
		if (element.getType().equals("Track")) {
			TracerPanel tracerPanelV = new TracerPanel((IDrawable) element, sArea, graphTypes.VALUE);// value graph for tracks
			topBox.add(tracerPanelV);
		}
		Box bottomBox = Box.createVerticalBox();
		elementInfoPanel = new ElementInfoPanel(element, aHandler, true, true, true);// call with handler for top component - dont recurse (last false)
		bottomBox.add(elementInfoPanel);
		JScrollPane ScrollBars = new JScrollPane(elementInfoPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		bottomBox.add(ScrollBars);
		topBox.setPreferredSize(new Dimension(400, 100));// w,h for graphs
		//bottomBox.setPreferredSize(new Dimension(400, 300));// w,h for tracks will adjust
		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topBox, bottomBox);
		//this.add(splitPanel);
		this.setContentPane(splitPanel);
		/*this.pack();
		this.revalidate();
		this.repaint();*/
		// Logger.log("elementInfoPanel rebuilt:"+this.TrackInfo);
	}

}

public class ElementDashboard extends Thread {
	DashboardFrame eframe;
	SharedArea sArea;

	public ElementDashboard(INamedElement element, SharedArea sArea) {
		Logger.log("Preparing ElementDashboard thread");
		this.sArea = sArea;
		this.eframe = new DashboardFrame(element, sArea);
		Logger.log("Running ElementDashboard thread");
		this.start();
	}

	@Override
	public synchronized void run() {
		eframe.setVisible(true);
		boolean stop = false;
		while (!stop) {
			try {
				// Logger.log("ElementDashboard:wait");
				sArea.standDown(FConstants.msecWait);
				eframe.RefreshTop();// only refresh top
				// eframe.RebuildAll(); //this is called by the action handler when needed
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Logger.log("ElementDashboard:wait");
		}
		// finish();
	}

	private void finish() {
		Logger.log("End of ElementDashboard Thread");
	}

	@Override
	public void interrupt() {
		finish();
		super.interrupt();
	}
}
