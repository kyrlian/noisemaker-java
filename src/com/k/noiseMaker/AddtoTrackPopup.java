package com.k.noiseMaker;

	
public class AddtoTrackPopup extends BaseJFrame implements IRebuildable {
	private static final long serialVersionUID = 1L;
	AddtoTrackActionHandler aHandler;
	INamedElement targetTrack; 	
	//JPanel topPanel;
	
	AddtoTrackPopup(INamedElement track, SharedArea sArea) {
		this.sArea=sArea;
		String title = track.getType() + " " + track.getuid() + " " + track.getName() + ": Add";
		Logger.log("Creating "+ title);
		this.setTitle(title);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//if you close the editor, you close all and terminate the program
		this.setSize(200, 100);//w,h
		//this.setLocationRelativeTo((Component) sArea.get("MainFrame"));
		this.setLocationByPlatform(true); 
		this.targetTrack=track;
		// Actions handler
		this.aHandler = new AddtoTrackActionHandler(track, sArea, this);
		// Info Panel
		RebuildAll();//will call build all plus tools
	}

	@Override
	public void buildAll() {
		AddtoTrackInfoPanel mainPanel = new AddtoTrackInfoPanel(targetTrack,this.aHandler);
		//this.add(mainPanel);
		this.setContentPane(mainPanel);//TODO generalise use of setContentPane
		/*this.pack();		
		this.revalidate();		
		this.repaint();*/
		this.setResizable(false);
		this.setVisible(true);
		//Logger.log("elementInfoPanel rebuilt:"+this.TrackInfo);
	}
}
