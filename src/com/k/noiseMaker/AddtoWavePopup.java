package com.k.noiseMaker;

	
public class AddtoWavePopup extends BaseJFrame implements IRebuildable {
	private static final long serialVersionUID = 1L;
	AddtoWaveActionHandler aHandler;
	INamedElement targetWave; 	
	//JPanel topPanel;
	
	AddtoWavePopup(INamedElement wave, SharedArea sArea) {
		this.sArea=sArea;
		String title = wave.getType() + " " + wave.getuid() + " " + wave.getName() + ": Add";
		Logger.log("Creating "+ title);
		this.setTitle(title);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//if you close the editor, you close all and terminate the program
		this.setSize(200, 100);//w,h
		//this.setLocationRelativeTo((Component) sArea.get("MainFrame"));
		this.setLocationByPlatform(true); 
		this.targetWave=wave;
		// Actions handler
		this.aHandler = new AddtoWaveActionHandler(wave, sArea, this);
		// Info Panel
		RebuildAll();//will call build all plus tools
	}

	@Override
	public void buildAll() {
		AddtoWaveInfoPanel mainPanel = new AddtoWaveInfoPanel(targetWave,this.aHandler);
		//this.add(mainPanel);
		this.setContentPane(mainPanel);
		/*this.pack();		
		this.revalidate();		
		this.repaint();*/
		this.setResizable(false);
		this.setVisible(true);
		//Logger.log("elementInfoPanel rebuilt:"+this.TrackInfo);
	}
}
