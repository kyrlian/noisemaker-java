package test;

public class myInterfaceCaller {
	myInterface src;
	myInterfaceCaller(myInterface src){
	this.src=src;
	}
	double getValue(){
		return src.getValue(1.0);
	}
}
