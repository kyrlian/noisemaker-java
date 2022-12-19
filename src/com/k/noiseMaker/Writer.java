package com.k.noiseMaker;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class Writer extends Thread {
	// Instance data
	String myPath;
	Sampler sampleProvider;
	DataOutputStream outFile;
	SharedByteBuffer sharedBuffer;
	byte[] dummyByteBuffer;
	int samplesToSave;
	int savedSamples = 0;
	SharedArea sArea;
	
	public Writer(SharedArea sArea) {
		this("out.wav", (Sampler)sArea.get("sampleProvider"), (SharedByteBuffer)sArea.get("sharedWriteBuffer"), 60,sArea);//TODO seconds to save will be optional
	}
	public Writer(String fpath, Sampler sampleProvider, SharedByteBuffer sharedBuffer, int secondsToSave,SharedArea sArea) {
		Logger.log("Preparing Writer thread");
		this.dummyByteBuffer = new byte[sampleProvider.bytesPerSample];
		this.sampleProvider = sampleProvider;
		this.myPath = fpath;
		this.sharedBuffer = sharedBuffer;
		this.samplesToSave = sampleProvider.samplesPerSecond * secondsToSave;
		this.sArea=sArea;
		writeWavHeader(samplesToSave);
		Logger.log("Running Writer thread");
		this.start();
		sArea.put("Recording", true);//used by sampler to put in buffer
	}

	private void writeWavHeader(int nSamples) {
		int formatHeaderByteSize = 16;// 2+2+4+4+2+2;
		// int dataByteSize = nBuffers * sampleProvider.bytesPerBuffer;
		// TODO for real time we'll need to come back and set this at the end,
		// or write to a .part file, then on end generated header and concat
		int dataByteSize = nSamples * sampleProvider.nbChannels * 2;
		int fileByteSize = 4 + 4 + 4 + formatHeaderByteSize + 4 + 4 + dataByteSize;// 4 + 24 + (8 + M * Nc * Ns + (0 or 1))
		try {
			outFile = new DataOutputStream(new FileOutputStream(myPath));
			// Header
			// Nc = nb channels = 2
			// Ns = nb blocks = nBuffers * samplesPerBuffer
			// F = blocks per second = samplesPerSecond
			// M*Nc = sample size = bytesPerSample
			outFile.writeBytes("RIFF"); // 00 - RIFF (that means all int should
										// be LittleEndian as RIFF is LE)
			outFile.write(intToByteArray((int) fileByteSize), 0, 4); // 04 - rest of file size
			outFile.writeBytes("WAVE"); // 08 - WAVE
			outFile.writeBytes("fmt "); // 12 - fmt
			outFile.write(intToByteArray((int) formatHeaderByteSize), 0, 4); // 16 size of this chunk (16)
			outFile.write(shortToByteArray((short) 1), 0, 2); // 20 - 1 for PCM
			outFile.write(shortToByteArray((short) sampleProvider.nbChannels), 0, 2); // 22 - Mono 1, Stereo 2
			outFile.write(intToByteArray((int) sampleProvider.samplesPerSecond), 0, 4); // 24 samples per sec
			outFile.write(intToByteArray((int) (sampleProvider.samplesPerSecond * sampleProvider.bytesPerSample)), 0, 4); // 28 - nb bytes per second - all channels
			outFile.write(shortToByteArray((short) (sampleProvider.bytesPerSample)), 0, 2); // 32 - nb bytes per sample (all channels)
			outFile.write(shortToByteArray((short) (sampleProvider.bytesPerSample * 8 / sampleProvider.nbChannels)), 0, 2); // 34 - nb bits per sample (PER channels) (bytes * 8)
			outFile.writeBytes("data"); // 36 - data
			outFile.write(intToByteArray((int) dataByteSize), 0, 4); // 40 - data bytes size
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Data
		boolean stop=false;
		while ((savedSamples < this.samplesToSave) && !stop) {
			try {
				for (int i = 0; i < sampleProvider.bytesPerSample; i++) {
					dummyByteBuffer[i] = sharedBuffer.get();
				}
				outFile.write(dummyByteBuffer, 0, sampleProvider.bytesPerSample);
				savedSamples++;
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
				interrupt();
			}		
			if (((String)sArea.get("StopRecord")).equals("StopRecord")){
				sArea.put("Recording", false);//used by sampler
				//TODO finalise file header
				stop=true;
			}			
		}
		finish();
	}

	private void finish() {
		try {
			outFile.flush();
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.log("End of Writer Thread");
	}

	@Override
	public void interrupt() {
		finish();
		super.interrupt();
	}

	// convert an int to a byte array
	private static byte[] intToByteArray(int data) {
		byte[] bytes = new byte[4];
		for (int i = 0; i < 4; i++) {
			bytes[i] = (byte) (data >>> (i * 8));// LE
		}
		return bytes;
	}

	private static byte[] shortToByteArray(short data) {
		return new byte[] { (byte) (data & 0xff), (byte) ((data >>> 8) & 0xff) };// LE
	}

}