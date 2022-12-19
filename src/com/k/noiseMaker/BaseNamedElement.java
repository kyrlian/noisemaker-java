package com.k.noiseMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseNamedElement implements INamedElement {

	protected String Name = "";
	protected final int uid;
	protected static int uidMax = 0;
	protected static List<String> nameList = new ArrayList<String>();// List of taken names
	protected static HashMap<Integer, INamedElement> hmElementsByUid = new HashMap<Integer, INamedElement>();
	protected static HashMap<String, INamedElement> hmElementsByName = new HashMap<String, INamedElement>();
	static List<Integer> exportedIds = new ArrayList<Integer>();
	
	public BaseNamedElement() {
		uidMax += 1;
		uid = uidMax;
		hmElementsByUid.put(uid, this);
	}

	public BaseNamedElement(String n) {
		this();
		setName(n);
	}

	public BaseNamedElement(INamedElement e) {
		this();
		setName(e.getName() + uid);
	}

	@Override
	public INamedElement clone() {
		return new BaseNamedElement(this);
	}

	@Override
	public void setName(String n) {
		if (nameList.contains(n)) {
			setName(n + "_" + uid); // name already taken
		} else {
			Name = n;
			nameList.add(Name);
			hmElementsByName.put(Name, this);
		}
	}

	@Override
	public String getName() {
		return Name;
	}

	@Override
	public int getuid() {
		return uid;
	}

	@Override
	public String getInfo(String tabs) {
		String info = tabs + "uid:" + uid;
		if (Name != null) {
			info += ", Name:" + Name;
		}
		return info;
	}

	@Override
	public INamedElement getNamedElement(Object id) {
		if (id.getClass().getSimpleName().equals("String")) {
			return getNamedElement((String) id);
		} else {
			return getNamedElement((int) id);
		}
		// return null;
	}

	@Override
	public INamedElement getNamedElement(int uid) {
		if (getuid() == uid) {
			return this;
		}
		return hmElementsByUid.get(uid);
	}

	@Override
	public INamedElement getNamedElement(String name) {
		String oName = getName();
		if (oName != null && oName.equals(name)) {
			return this;
		}
		return hmElementsByName.get(name);
	}

	@Override
	public boolean isTrack() {
		return (this.getType().equals("Track"));
	}
	
	@Override
	public
	boolean isEffect(){
		return (this.getType().startsWith("Effect"));
	}

	@Override
	public boolean isWave() {
		return (this.getType().equals("Wave"));
	}
	
	@Override
	public boolean isSignal() {
		return (this.getType().startsWith("Signal"));
	}

	@Override
	public String getType() {
		String cName = this.getClass().getSimpleName();
		return cName;
	}
	
	public String getPrefix(){
		if(this.isTrack()){
			return "t";
		}else if(this.isWave()){
			return "w";
		}else if(this.isEffect()){
			return "e";
		}else if(this.isSignal()){
			return "s";
		}else{
			return "o";
		}
	}

	@Override
	public void setAttribute(String attrName, String attrValue) {
		switch (attrName) {
		case "Name":
			setName(attrValue);
			break;
		default:
			//Logger.log("Base:Attribute "+attrName+" not supported for "+this.getType());
			break;
		}
	}

	@Override
	public String getInfo() {
		return getInfo("");
	}

	@Override
	public String getSrcCode() {
		String sCode = "BaseNamedElement "+uid+" = new BaseNamedElement();\n";		
		return sCode;
	}
	
	public void getSrcCodeReset() {
		exportedIds.clear();
	}

	@Override
	public String getPrefixeduid() {
		return getPrefix()+getuid()+getName();
	}

}