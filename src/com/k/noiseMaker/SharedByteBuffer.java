package com.k.noiseMaker;

public class SharedByteBuffer {

	protected byte[] buf;
	protected int in = 0;
	protected int out = 0;
	protected int count = 0;
	protected int size;

	public SharedByteBuffer(int size) {
		this.size = size;
		//byte[] sampleData = new byte[sampleProvider.bytesPerBuffer];
		buf = new byte[size];
	}

	public synchronized void put(byte o) throws InterruptedException {
		while (count == size) {
			//Logger.log("put:wait");
			wait();
		}
		//Logger.log("put:waitEnd");
		buf[in] = o;
		++count;
		in = (in + 1) % size;
		notifyAll();
	}

	public synchronized byte get() throws InterruptedException {
		while (count == 0) {
			//Logger.log("get:wait");
			wait();
		}
		//Logger.log("get:waitEnd");
		byte o = buf[out];
		//buf[out] = (byte) null;
		--count;
		out = (out + 1) % size;
		notifyAll();
		return (o);
	}
}