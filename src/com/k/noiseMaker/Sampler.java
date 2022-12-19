package com.k.noiseMaker;

class Sampler extends Thread {
	Track srcLeft;
	Track srcRight;
	final int nbChannels = 2;// For now i have only 1 channel
	int samplesPerSecond;
	int bytesPerSample;// byte size for each sample for ALL channels
	int bytesPerChannelSample = 2;// byte size for 1 sample for 1 channels (ie 1 short)
	int channelSamplesPerSecond;
	double currentTime = 0.0;
	double realMaxAmplitudeLeft = 0.0;
	double realMaxAmplitudeRight = 0.0;
	SharedByteBuffer sharedWriteBuffer;
	SharedByteBuffer sharedPlayBuffer;
	int secondsToSample;
	int samplesToSample;
	int sampledSamples = 0;
	SharedArea sArea;

	Sampler(Track srcLeft, Track srcRight, int samplesPerSecond,SharedByteBuffer sharedPlayBuffer, SharedByteBuffer sharedWriteBuffer,  SharedArea sArea) {
		this( srcLeft, srcRight,  samplesPerSecond, sharedPlayBuffer, sharedWriteBuffer,   -1,  sArea);
	}
	Sampler(Track srcLeft, Track srcRight, int samplesPerSecond,SharedByteBuffer sharedPlayBuffer, SharedByteBuffer sharedWriteBuffer , int secondsToSample, SharedArea sArea) {
		Logger.log("Preparing Sampler thread");
		this.srcLeft = srcLeft;
		this.srcRight = srcRight;
		this.samplesPerSecond = samplesPerSecond;
		this.channelSamplesPerSecond = samplesPerSecond * nbChannels;
		this.bytesPerSample = bytesPerChannelSample * nbChannels;// byte size for each sample for ALL channels (short=2 bytes)
		this.sharedWriteBuffer = sharedWriteBuffer;
		this.sharedPlayBuffer = sharedPlayBuffer;
		this.samplesToSample = samplesPerSecond * secondsToSample;
		this.sArea = sArea;
		cleanSources();
		// Logger.log("Left:" + srcLeft.getInfo());
		// Logger.log("Right:" + srcRight.getInfo());
		Logger.log(getInfo());
		Logger.log("Running Sampler thread");
		this.start();
	}

	private void writeToBuffer(double v, double scaleFactor, SharedByteBuffer buff) {
		double ds = v * scaleFactor;
		//Logger.log("ds:"+ds);
		short ss = (short) Math.round(ds);
		try {
			buff.put((byte) (ss & 0xFF));
			buff.put((byte) (ss >> 8 & 0xFF));// RIFF is LE
		} catch (InterruptedException e) {
			e.printStackTrace();
			interrupt();
			finish();
		}
	}

	private void cleanSources() {
		srcLeft.removeHighFreqs((double) samplesPerSecond / FConstants.MinSamplesPerPeriod);// 50 = minimum number of points per period I want to have
		srcRight.removeHighFreqs((double) samplesPerSecond / FConstants.MinSamplesPerPeriod);// 50 = minimum number of points per period I want to have
	}

	@Override
	public void run() {
		double timeStep = 1.0 / samplesPerSecond;
		Logger.log("timeStep:"+timeStep);
		double scaleFactorLeft = Short.MAX_VALUE / srcLeft.estimatedMaxAmplitude * .8;
		double scaleFactorRight = Short.MAX_VALUE / srcRight.estimatedMaxAmplitude * .8;
		double scaleFactor = Math.min(scaleFactorLeft, scaleFactorRight);
		double nextUpdateTime = 0.0;
		//double debugOldVal=0.0;double debugOldTime=0.0;
		while (this.samplesToSample <0 || sampledSamples < this.samplesToSample) {
			currentTime = (double) sArea.get("currentTime");
			//update track info every few second
			if(currentTime > nextUpdateTime){
				//Logger.log("Update track info");
				srcLeft.updateInfo();
				srcRight.updateInfo();
				nextUpdateTime += 5.0;//delay to update track infos
				scaleFactorLeft = Short.MAX_VALUE / srcLeft.estimatedMaxAmplitude * .8;
				scaleFactorRight = Short.MAX_VALUE / srcRight.estimatedMaxAmplitude * .8;
				scaleFactor = Math.min(scaleFactorLeft, scaleFactorRight);

			}
			// current step
			//compute values
			double vLeft = srcLeft.getValue(currentTime);
			//Logger.log("Sample:left("+currentTime+")="+vLeft);
			//Logger.log("Sample:left="+vLeft);			
			realMaxAmplitudeLeft = Math.max(realMaxAmplitudeLeft, vLeft);
			double vRight = srcRight.getValue(currentTime);
			realMaxAmplitudeRight = Math.max(realMaxAmplitudeRight, vRight);
			//Logger.log("L:"+vLeft+", R:"+vRight);
			//player buffer
			writeToBuffer(vLeft, scaleFactor, sharedPlayBuffer);			
			writeToBuffer(vRight, scaleFactor, sharedPlayBuffer);
			//writer buffer
			Boolean bRecording = (Boolean)sArea.get("Recording");
			if(bRecording){
				writeToBuffer(vLeft, scaleFactor, sharedWriteBuffer);
				writeToBuffer(vRight, scaleFactor, sharedWriteBuffer);
			}
			sampledSamples++;
			// Direction
			String PlayMode = (String) sArea.get("PlayMode");
			double nextTime = currentTime;
			switch (PlayMode) {
				case "Backward":
					nextTime -= timeStep;
					break;
				case "Play":
					nextTime += timeStep;
					break;
				case "Pause":
					try {
						sArea.standDown();//Pause
					} catch (InterruptedException e) {
						e.printStackTrace();
					}//wait until user changes something
					break;
				case "Forward":
					nextTime += (2.0 * timeStep);
					break;
			}
			if(currentTime == (double) sArea.get("currentTime")){//if nobody else changed it
				sArea.put("currentTime", nextTime);
			}
		}
		finish();
	}

	private void finish() {
		Logger.log(getRunInfo());
		Logger.log("End of Sampler Thread");
	}

	@Override
	public void interrupt() {
		finish();
		super.interrupt();
	}

	void reset(double t) {
		currentTime = t;
	}

	void reset() {
		reset(0.0);
	}

	public String getInfo() {
		String info = "Sampler:";
		info += ", nbChannels:" + nbChannels;
		info += ", samplesPerSecond:" + samplesPerSecond;
		info += ", bytesPerSample:" + bytesPerSample;
		info += ", srcLeft.estimatedMaxAmplitude:" + srcLeft.estimatedMaxAmplitude;
		info += ", srcRight.estimatedMaxAmplitude:" + srcRight.estimatedMaxAmplitude;
		return info;
	}

	public String getRunInfo() {
		String info = "Sampler:";
		info += ", currentTime:" + currentTime;
		info += ", srcLeft.estimatedMaxAmplitude:" + srcLeft.estimatedMaxAmplitude;
		info += ", srcRight.estimatedMaxAmplitude:" + srcRight.estimatedMaxAmplitude;
		info += ", realMaxAmplitudeLeft:" + realMaxAmplitudeLeft;
		info += ", realMaxAmplitudeRight:" + realMaxAmplitudeRight;
		return info;
	}

}