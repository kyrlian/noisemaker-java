package test;

import	java.io.File;
import	java.io.IOException;

import	javax.sound.sampled.AudioFormat;
import	javax.sound.sampled.AudioInputStream;
import	javax.sound.sampled.AudioSystem;
import	javax.sound.sampled.DataLine;
import	javax.sound.sampled.LineUnavailableException;
import	javax.sound.sampled.SourceDataLine;
import	java.util.Vector;


public class Sound {

	private static final int	EXTERNAL_BUFFER_SIZE = 128000;
	public Vector buffer;
	public AudioFormat audioFormat;
	
	public Sound()
	{
		buffer = new Vector(EXTERNAL_BUFFER_SIZE);
	}

	public void loadFromFile ( String filename )
	{
		File soundFile = new File(filename);
		
		/*
		  We have to read in the sound file.
		*/
		AudioInputStream	audioInputStream = null;
		try
		{
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		}
		catch (Exception e)
		{
			/*
			  In case of an exception, we dump the exception
			  including the stack trace to the console output.
			  Then, we exit the program.
			*/
			e.printStackTrace();
			System.exit(1);
		}
		

		/*
		  From the AudioInputStream, i.e. from the sound file,
		  we fetch information about the format of the
		  audio data.
		  These information include the sampling frequency,
		  the number of
		  channels and the size of the samples.
		  These information
		  are needed to ask Java Sound for a suitable output line
		  for this audio file.
		*/
		audioFormat = audioInputStream.getFormat();

		System.out.println("audioFormat: "+audioFormat);
		/*
		  Ok, finally the line is prepared. Now comes the real
		  job: we have to write data to the line. We do this
		  in a loop. First, we read data from the
		  AudioInputStream to a buffer. Then, we write from
		  this buffer to the Line. This is done until the end
		  of the file is reached, which is detected by a
		  return value of -1 from the read method of the
		  AudioInputStream.
		*/
		int	nBytesRead = 0;
		byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
		while (nBytesRead != -1)
		{
			try
			{
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			// System.out.println("   Number of bytes read: "+nBytesRead);
			if (nBytesRead >= 0)
			{
				for (int i=1; i <= nBytesRead; i++)
				{
					buffer.add(new Byte(abData[i]));
				}
			}
		}
		
	}
	
	public void play()
	{
		this.playAtRate(1.0);
	}
	
	public void playAtRate (double rate)
	{
		/*
		  Asking for a line is a rather tricky thing.
		  We have to construct an Info object that specifies
		  the desired properties for the line.
		  First, we have to say which kind of line we want. The
		  possibilities are: SourceDataLine (for playback), Clip
		  (for repeated playback)	and TargetDataLine (for
		  recording).
		  Here, we want to do normal playback, so we ask for
		  a SourceDataLine.
		  Then, we have to pass an AudioFormat object, so that
		  the Line knows which format the data passed to it
		  will have.
		  Furthermore, we can give Java Sound a hint about how
		  big the internal buffer for the line should be. This
		  isn't used here, signaling that we
		  don't care about the exact size. Java Sound will use
		  some default value for the buffer size.
		*/
		SourceDataLine	line = null;
		DataLine.Info	info = new DataLine.Info(SourceDataLine.class,
							 audioFormat);
		// System.out.println("   Getting the line");
		try
		{
			line = (SourceDataLine) AudioSystem.getLine(info);

			/*
			  The line is there, but it is not yet ready to
			  receive audio data. We have to open the line.
			*/
			// System.out.println("   Opening the line");
			line.open(audioFormat);
		}
		catch (LineUnavailableException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		/*
		  Still not enough. The line now can receive data,
		  but will not pass them on to the audio output device
		  (which means to your sound card). This has to be
		  activated.
		*/
		// System.out.println("   Starting the line");
		line.start();
		/*
		  Ok, finally the line is prepared. Now comes the real
		  job: we have to write data to the line. We do this
		  in a loop. First, we read data from the
		  AudioInputStream to a buffer. Then, we write from
		  this buffer to the Line. This is done until the end
		  of the file is reached, which is detected by a
		  return value of -1 from the read method of the
		  AudioInputStream.
		*/
		// System.out.println("   Sending the bytes");
		double snd_pos = 1; // In Frames
		int frame_length = audioFormat.getFrameSize();
		int length_in_frames = (int) (buffer.size() / frame_length)-1;
		// System.out.println("   -frame length "+frame_length+" length_in_frames "+length_in_frames);
		byte[]	tempBuff = new byte[EXTERNAL_BUFFER_SIZE];
		byte[] sample = null;
		while (Math.floor(snd_pos) <= length_in_frames)
		{
			// System.out.println("   -Fillling a buffer");
			int buff_pos = 0;
			while ((buff_pos < EXTERNAL_BUFFER_SIZE) & 
					(Math.floor(snd_pos) <= length_in_frames))
			{
				sample = getFrame((int) Math.floor(snd_pos));
				for (int i=0;i<frame_length; i++)
					{tempBuff[buff_pos++] = sample[i];}
				snd_pos += rate;
			}
			if (buff_pos > 0)
				{// System.out.println("   -Doing a write: "+buff_pos);
				int	nBytesWritten = line.write(tempBuff, 0, buff_pos);}
		}
		// System.out.println("   Draining and closing");
		/*
		  Wait until all data are played.
		  This is only necessary because of the bug noted below.
		  (If we do not wait, we would interrupt the playback by
		  prematurely closing the line and exiting the VM.)
		 
		  Thanks to Margie Fitch for bringing me on the right
		  path to this solution.
		*/
		line.drain();

		/*
		  All data are played. We can close the shop.
		*/
		line.close();		
	}
	
	public byte[] getFrame( int frameNum)
	{
	int num_channels 	= audioFormat.getChannels(); 		// mono or stereo
	int sample_size 	= audioFormat.getSampleSizeInBits(); 	// 8 or 16 bits
	int frame_size 		= audioFormat.getFrameSize();
		
	// To read the 'j'th frame in the file:
	int framePos = (int) ((frameNum-1) * frame_size);
	byte[] frame = new byte[frame_size];
	Byte sample = null;
	for(int i=0; i<frame_size; i++) 
		{
			// // System.out.println("   -frameNum: "+frameNum+" framePos "+framePos+" i "+i);
			sample = (Byte) buffer.get(framePos+i+1);
			frame[i] = sample.byteValue();
		}
	return frame;
	}
	
	public int getLengthInFrames()
	{
			int frame_length = audioFormat.getFrameSize();
			return (int) (buffer.size() / frame_length)-1;
	}
	
	public int getSample( int frameNum)
	{
		int channels	 	= audioFormat.getChannels(); 		// mono or stereo
		int sample_size 	= audioFormat.getSampleSizeInBits(); 	// 8 or 16 bits
		int sample			= 0;

		byte [] frame = getFrame(frameNum);
		// To decode the frame (frame_size is 1 for 8-bit mono, 2 for 16-bit mono 
		// and 8-bit stereo, and 4 for 16-bit stereo):
		if (channels == 1){	// mono
			if (sample_size == 8){ // 8-bit mono
				sample = frame[0] & 0xff;
				// s += "mono-8: " + sample;
			}
			else if (sample_size == 16) { // 16-bit mono
				int m1 = frame[0] & 0xff;
				int m2 = frame[1] & 0xff;
				sample = (short) ((m2 << 8) + m1);
				// s += "mono-16: " + sample;
			}
		}
		else if (channels == 2){ // stereo
			if (sample_size == 8){ // 8-bit stereo
				int L = frame[0] & 0xff;
				int R = frame[1] & 0xff;
				sample = (short) L;
				// s += "stereo-8: left = " + L + "\tright = " + R;
			}
			else if (sample_size == 16){ // 16-bit stereo
				int L1 = frame[0] & 0xff;
				int L2 = frame[1] & 0xff;
				int R1 = frame[2] & 0xff;
				int R2 = frame[3] & 0xff;
				short Lsample = (short)((L2 << 8) + L1);
				short Rsample = (short)((R2 << 8) + R1);
				sample = (int) Lsample;
				// s += "stereo-16: left = " + Lsample + "\tright = " + Rsample;
			}
		}
		return sample;
	}
	
		public void setSample (int frameNum, int sample)
	{
		int channels	 	= audioFormat.getChannels(); 		// mono or stereo
		int sample_size 	= audioFormat.getSampleSizeInBits(); 	// 8 or 16 bits
		int frame_size 		= audioFormat.getFrameSize();

		int framePos = (int) ((frameNum-1) * frame_size)+1;
		byte[] frame = new byte[frame_size];
		
		// To decode the frame (frame_size is 1 for 8-bit mono, 2 for 16-bit mono 
		// and 8-bit stereo, and 4 for 16-bit stereo):
		if (channels == 1){	// mono
			if (sample_size == 8){ // 8-bit mono
				buffer.setElementAt(new Byte((byte) sample),framePos);
			}
			else if (sample_size == 16) { // 16-bit mono
				int m2 =  ((short) sample >> 8) & 0xff; // ((byte) Math.floor((int) sample/255)) & 0xff;
				int m1 = ((short) sample) & 0xff;
				//System.out.println("m1 "+m1+" m2 "+m2);
				buffer.setElementAt(new Byte((byte) m1),framePos);
				buffer.setElementAt(new Byte((byte) m2),framePos+1);
			}
		}
		else if (channels == 2){ // stereo
			if (sample_size == 8){ // 8-bit stereo
				int m2 =  ((short) sample >> 8) & 0xff; // ((byte) Math.floor((int) sample/255)) & 0xff;
				int m1 = ((short) sample) & 0xff;
				buffer.setElementAt(new Byte((byte) m1),framePos);
				buffer.setElementAt(new Byte((byte) m2),framePos+1);
			}
			else if (sample_size == 16){ // 16-bit stereo
				int m2 =  ((short) sample >> 8) & 0xff; // ((byte) Math.floor((int) sample/255)) & 0xff;
				int m1 = ((short) sample) & 0xff;
				buffer.setElementAt(new Byte((byte) m1),framePos);
				buffer.setElementAt(new Byte((byte) m2),framePos+1);
				buffer.setElementAt(new Byte((byte) 0),framePos+2);
				buffer.setElementAt(new Byte((byte) 0),framePos+3);
			}
		}

	}
	

	public int getLength()
	{
		return buffer.size();
	}
	
	public int getChannels()
	{
		return audioFormat.getChannels();
	}
	
	public String justATaste()
	{
		return "5 samples:"+" "+buffer.get(0)+" "+buffer.get(1)+" "+buffer.get(2)+" "+buffer.get(3)+" "+buffer.get(4);
	}
	public static void main (String args[])
	{
		Sound s = new Sound();
		System.out.println("Reading the sound");
		s.loadFromFile("out.wav");
		System.out.println("Length of sound: "+s.getLength());
		System.out.println("Channels: "+s.getChannels());
		System.out.println("A Taste: "+s.justATaste());
		// byte[] sample = s.getFrame(1);
		// System.out.println("One Frame: "+sample[0]+" "+sample[1]);
		/* System.out.println("One frame value: "+s.getSample(1));
		s.setSample(1,54);
		System.out.println("Checking frame value: "+s.getSample(1));
		s.setSample(1,540);
		System.out.println("After changing to 540: "+s.getSample(1));
		s.setSample(1,-1);
		System.out.println("After changing to -1: "+s.getSample(1));
		s.setSample(1,54); */
		/*for (int i=1; i < 50;i++)
			{ System.out.println("Sample "+i+": "+s.getSample(i));}
		for (int i=1; i < s.getLengthInFrames();i++)
			{ s.setSample(i,s.getSample(i));}
		System.out.println("Playing the sound - unchanged:");
		s.play(); */
		System.out.println("Playing the sound");
		s.play();
		for (int i=1; i < s.getLengthInFrames();i++)
			{ s.setSample(i,2*s.getSample(i));}
		System.out.println("Playing the sound -- more loudly");
		s.play();
		System.out.println("Doubling the rate");
		s.playAtRate(2.0);
		System.out.println("Halving the rate");
		s.playAtRate(0.5);
		System.exit(0);
	}
}