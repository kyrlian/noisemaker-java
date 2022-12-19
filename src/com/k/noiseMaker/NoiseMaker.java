package com.k.noiseMaker;

public class NoiseMaker {
	public static void main(String[] args) {
		Logger.log("Let's go!");
		//General setup
		//int samplesPerSecond = 44100;//cd bitrate 44100
		int samplesPerSecond = 128000;//good kbps
		//int samplesPerSecond = 160000;//high kbps
		//int samplesPerSecond = 320000;//higher kbps
		//int secondsToSample = 10;//infinite if -1 or ommited
		//int secondsToSave = 10;//For writer
		//int secondsToPlay = secondsToSample;//For player - infinite if -1 or ommited
		// Prepare tracks
		Logger.log("Preparing music");
		//
		Track Ltrack = new Track();
		Track Rtrack = new Track();
		//==PASTE DUMP HERE==

		//=============DUMP==============
		//=============LEFT==============
		Track t1 = new Track();
		Wave wla = new Wave(ShapeType.SIN,440.0,0.0,1.0);
		wla.setName("wla");		
		SignalSimpleOscillator flfo = new SignalSimpleOscillator(ShapeType.SIN,1,0.0,1.0);//period = 10s
		//flfo.setAmplitude(1);//lfos have better result with small amplitude
		flfo.setName("flfo");
		wla.setLFO(LFOType.PHASE,flfo);//sin phase gives a variable frequency, stable in time
		t1.addSoundElement(wla);
		t1.setName("t1");
		//=============/LEFT==============
		//=============RIGHT==============
		Track t2 = new Track();
		t2.addSoundElement(t1);

		//=============/RIGHT==============
		Ltrack = t1;
		Rtrack = t2;
		//=============/DUMP==============

		//==DO NOT MODDIFY BELOW
		
		// Prepare buffers
		Logger.log("Preparing buffers");
		SharedArea sArea = new SharedArea();
		sArea.put("samplesPerSecond",samplesPerSecond);
		//sArea.put("secondsToSample",secondsToSample);
		sArea.put("currentTime", 0.0);
		SharedByteBuffer sharedPlayBuffer = new SharedByteBuffer(4096);
		SharedByteBuffer sharedWriteBuffer = new SharedByteBuffer(4096);
		sArea.put("sharedWriteBuffer",sharedWriteBuffer);
		//Create Threads - run at launch
		new MainDashboard(Ltrack,Rtrack, sArea);
		Sampler sampleProvider = new Sampler(Ltrack,Rtrack, samplesPerSecond, sharedPlayBuffer, sharedWriteBuffer,sArea);
		sArea.put("sampleProvider", sampleProvider);
		new Player(sampleProvider, sharedPlayBuffer,sArea);// listen
		// Done
		Logger.log("Everything is running, main is done.");
	}
}
//EOF