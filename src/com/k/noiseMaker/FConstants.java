package com.k.noiseMaker;

final class FConstants {
		public static final double MinSamplesPerPeriod = 50.0;
		public static final int MinFreq = 30;//Hz
		public static final int MaxFreq = 12000;//Hz
		public static final double FreqLogBase = 1.2;//log
		public static final int sliderScale = 100000;		
		public static final double fps=60;
		public static final long msecWait = (long)( 1000 / fps);
		//Logarithm base change rule
		//	logb(x) = logc(x) / logc(b)
		//	logB(x) = log10(x) / log10(B)
		public static double logBase(double logBase, double x){
			return Math.log10(x) / Math.log10(logBase);
		}
				
}
