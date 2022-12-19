package com.k.noiseMaker;

import java.util.HashMap;

public class SharedArea {

	HashMap<String, Object> hm = new HashMap<String, Object>();
	
	public SharedArea() {
		//put("HereIs", "SomeValue");
		put("PlayMode", "Pause");
		put("Recording", false);
	}

	public synchronized void put(String key, Object value)  {
		hm.put(key, value);
		//Logger.log("hm:"+hm);
	}
	
	public synchronized Object get(String key)  {
		if(hm.containsKey(key)){ 
			return hm.get(key);
		}
		return "UNASSIGNED";
	}
	
	public synchronized void standDown(long time) throws InterruptedException  {
		wait(time);
	}
	public synchronized void standDown() throws InterruptedException  {			
		wait();
	}
	
	public synchronized void wakeUp()  {
		notifyAll();
	}
	
	void shutdown(){//unneeded for now		
		put("Shutdown","Yes");//set shutdown flag
		wakeUp();//raise all threads
	}
}
