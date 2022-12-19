package com.k.noiseMaker;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JFrame;

public abstract class BaseJFrame extends JFrame implements IRebuildable {
	private static final long serialVersionUID = 1L;
	//HashMap<String, Component> componentMap = new HashMap<String, Component>();
	static HashMap<Component, String> componentMap = new HashMap<Component,String>();
	SharedArea sArea;
	
	@Override
	public void RebuildAll() {
		this.getContentPane().removeAll();
		this.revalidate();
		buildAll();
		this.pack();
		this.revalidate();
		this.repaint();
		createComponentMap();
	}

	@Override
	public void Close() {
		//Logger.log("Closing "+this.getTitle());
		//this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(false);
		this.dispose();
	}

	@Override
	public void createComponentMap() {
		createComponentMap((Container) this.getContentPane());
	}

	void createComponentMap(Container c) {
		Component[] components = c.getComponents();
		for (int i = 0; i < components.length; i++) {
			Component subc = components[i];
			//componentMap.put(subc.getName(), subc);//pb:several components CAN have same name  when an element is displayed in several places
			String name = subc.getName();
			if(name != null){
				componentMap.put(subc,subc.getName());
			}
			//Logger.log("added " + components[i].getName()); 
			createComponentMap((Container) subc);//recurse
		}
	}

	@Override
	//return the FIRST component of this name
	public Component getComponentByName(String name) {
	    for (Entry<Component, String> entry : componentMap.entrySet()) {
	        if (name.equals(entry.getValue())) {
	        	return entry.getKey();
	        }
	    }
	    return null;
	}
	
	@Override
	//return ALL components of this name - possible when an element is displayed in several places
	public List<Component> getComponentsByName(String name) {
		List<Component> comps = new ArrayList<Component>();// Arrays.asList(f));
	    for (Entry<Component, String> entry : componentMap.entrySet()) {
	        if (name.equals(entry.getValue())) {
	        	comps.add(entry.getKey());
	        }
	    }
	    return comps;
	}
}
