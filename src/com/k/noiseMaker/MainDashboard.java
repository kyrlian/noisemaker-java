package com.k.noiseMaker;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

class MainFrame extends BaseJFrame implements IRebuildable{
	private static final long serialVersionUID = 1L;
	Track left ;
	Track right ;
	ControlerActionHandler controlerHandler;
	ElementActionHandler leftHandler;
	ElementActionHandler rightHandler;	
	SharedArea sArea;//in base
	JPanel topGrid;
	TimePanel timePanel;
	
	MainFrame(Track left ,Track right, SharedArea sArea) {
		this.sArea=sArea;
		this.left = left;
		this.right = right;
		String title = "Main";
		this.setTitle(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//if you close the editor, you close all and terminate the program
		//Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		//this.setSize(400, 800);//w,h
		//this.setLocationRelativeTo(null);
		this.setLocationByPlatform(true); 
		// Actions handler for main control box
		this.controlerHandler = new ControlerActionHandler(sArea, this);
		//for element info
		this.leftHandler = new ElementActionHandler((INamedElement)left,sArea, this);
		this.rightHandler = new ElementActionHandler((INamedElement)right,sArea, this);
		// Info Panel
		//buildAll();
		RebuildAll();//will call build all plus tools
	}

	void RefreshTop(){
		topGrid.repaint();
		timePanel.repaint();
	}
	
	@Override
	public void buildAll() {
		Box splitTop = Box.createVerticalBox();
		//top part : graphs
		topGrid = new JPanel();
		topGrid.setLayout(new GridLayout(2, 2,1,1));
		TracerPanel LeftFreq = new TracerPanel((IDrawable)left, sArea,graphTypes.FREQ);//graph
		TracerPanel LeftValue = new TracerPanel((IDrawable)left, sArea,graphTypes.VALUE);//graph
		TracerPanel RightFreq = new TracerPanel((IDrawable)right, sArea,graphTypes.FREQ);//graph
		TracerPanel RightValue = new TracerPanel((IDrawable)right, sArea,graphTypes.VALUE);//graph
		topGrid.add(LeftFreq);		
		topGrid.add(RightFreq);
		topGrid.add(LeftValue);
		topGrid.add(RightValue);
		splitTop.add(topGrid);
		splitTop.setPreferredSize(new Dimension(400, 300));// w,h graphs
		//middle part - time display
		timePanel = new TimePanel(sArea);
		splitTop.add(timePanel);
		//middle part - time controls
		ControlerInfoPanel controlPanel = new ControlerInfoPanel(controlerHandler);
		splitTop.add(controlPanel);
		//bottom part - tracks info
		Box splitBottom = Box.createVerticalBox();
		ElementInfoPanel leftInfoPanel = new ElementInfoPanel(left, leftHandler,true,true,true);// call with handler for top component - dont recurse (last false)
		splitBottom.add(leftInfoPanel);
		splitBottom.add( new JScrollPane(leftInfoPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED) );
		ElementInfoPanel rightInfoPanel = new ElementInfoPanel(right, rightHandler,true,true,true);// call with handler for top component - dont recurse (last false)				
		splitBottom.add(rightInfoPanel);
		splitBottom.add( new JScrollPane(rightInfoPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED) );		
		//splitBottom.setPreferredSize(new Dimension(400, 400));// w,h tracks will adjust
		//put together
		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,splitTop,splitBottom);
		this.setContentPane(splitPanel);
		/*this.pack();
		this.revalidate();		
		this.repaint();*/
		//Logger.log("elementInfoPanel rebuilt:"+this.TrackInfo);
	}


}

public class MainDashboard extends Thread {
	MainFrame eframe;
	SharedArea sArea;

	public MainDashboard(Track left ,Track right, SharedArea sArea) {
		Logger.log("Preparing MainDashboard thread");
		this.sArea = sArea;
		this.eframe = new MainFrame( left , right, sArea);
		Logger.log("Running MainDashboard thread");
		this.start();
	}

	@Override
	public synchronized void run() {
		eframe.setVisible(true);
		sArea.put("MainFrame", eframe);//for sub elements positioning
		boolean stop = false;
		while (!stop) {
			try {
				//Logger.log("ElementDashboard:wait");
				sArea.standDown(FConstants.msecWait);//(ms) wait until user changes something or 1sec		
				eframe.RefreshTop();//only refresh top
				//eframe.RebuildAll(); //this is called by the action handler when needed
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Logger.log("ElementDashboard:wait");
		}
		//finish();
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
