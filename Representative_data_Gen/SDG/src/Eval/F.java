package Eval;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

import Monitor.DistCalculator;

public class F {

	public static void main (String args[])
	{
		double [] tab11={0.4,0.6};
		double [] tab22={0.78,0.22};

	

	
	EuclideanDistance euc = new EuclideanDistance();
	double dist = euc.compute(tab11,tab22);
	System.out.print(dist);
	}
}
