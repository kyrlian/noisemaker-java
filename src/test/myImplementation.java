package test;

public class myImplementation implements myInterface{
	double amplitude;
	myImplementation(double a){
		amplitude = a;
	}
	@Override
	public double getValue(double t) {
		return 0.0;	}

	@Override
	public double amplify(double factor) {
		return 0.0;
	}

}
