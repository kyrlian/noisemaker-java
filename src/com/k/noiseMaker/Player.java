package com.k.noiseMaker;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

class Player extends Thread {
	// Instance data
	AudioFormat format;
	DataLine.Info sourceDataInfo;
	SourceDataLine auSourceLine;// speaker
	Sampler sampleProvider;
	SharedByteBuffer sharedPlayBuffer;
	byte[] dummyByteBuffer;
	int secondsToPlay;
	int samplesToPlay;
	int playedSamples = 0;
	SharedArea sArea;
	
	public Player(Sampler sampleProvider, SharedByteBuffer sharedPlayBuffer,SharedArea sArea) {
		this( sampleProvider,  sharedPlayBuffer, -1, sArea) ;
	}
	public Player(Sampler sampleProvider, SharedByteBuffer sharedPlayBuffer, int secondsToPlay,SharedArea sArea) {
		Logger.log("Preparing Player thread");
		boolean SIGNED = true;
		boolean BIG_ENDIAN = false;// WAV if little endian
		//AudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian)
		//generated wav is : audioFormat: PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian
		//this.format = new AudioFormat(44100, 16, 2, SIGNED, BIG_ENDIAN);
		this.format = new AudioFormat((float)sampleProvider.samplesPerSecond, sampleProvider.bytesPerChannelSample * 8, sampleProvider.nbChannels, SIGNED, BIG_ENDIAN);
		this.sourceDataInfo = new DataLine.Info(SourceDataLine.class, format);
		this.sampleProvider = sampleProvider;
		this.dummyByteBuffer = new byte[sampleProvider.bytesPerSample];
		this.sharedPlayBuffer = sharedPlayBuffer;
		this.samplesToPlay = sampleProvider.samplesPerSecond * secondsToPlay;
		this.sArea=sArea;
		initAudio();
		Logger.log("Running Player thread");
		this.start();
	}

	private void initAudio() {
		// Get line to write data to
		try {
			auSourceLine = (SourceDataLine) AudioSystem.getLine(sourceDataInfo);
			auSourceLine.open(format);
			auSourceLine.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (this.samplesToPlay <0 || playedSamples < this.samplesToPlay) {
			try {
				//Logger.log("dummyByteBuffer:");
				for (int i = 0; i < sampleProvider.bytesPerSample; i++) {
					byte b = sharedPlayBuffer.get();
					dummyByteBuffer[i] = b; 
					//Logger.loginline(((Byte)b).toString());
				}
				//Logger.log("");
				auSourceLine.write(dummyByteBuffer, 0, sampleProvider.bytesPerSample);// to speakers
				playedSamples++;
				//Logger.log("Played sample "+playedSamples);
				//Logger.log("dummyByteBuffer "+dummyByteBuffer.hashCode());
			} catch (InterruptedException e) {
				e.printStackTrace();
				interrupt();
			}
		}
		finish();
	}

	private void finish() {
		auSourceLine.drain();
		auSourceLine.close();
		Logger.log("End of Player Thread");
	}

	@Override
	public void interrupt() {
		finish();
		super.interrupt();
	}

}