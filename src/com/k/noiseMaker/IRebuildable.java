package com.k.noiseMaker;

import java.awt.Component;
import java.util.List;

public interface IRebuildable {
	void createComponentMap() 	;
	public Component getComponentByName(String name);
	public List<Component> getComponentsByName(String name);
	void buildAll();
	void RebuildAll();
	void Close();
}
