package test.snt.oclsolver.examples;

import java.util.ArrayList;

import org.junit.Test;

import snt.oclsolver.distance.ClassifierTuple;
import snt.oclsolver.search.SearchAlgorithmEnum;
import test.snt.oclsolver.AbstractTestCase;


public class SVVTestModel  extends AbstractTestCase{
	String umlFile = "examples/SVVTestModel/SVVTestModel.uml";
	
	@Test
	public void testQuery01()
	{
	    ArrayList<ClassifierTuple> result = null;
	    
	    String query = "Person.allInstances()->select(p|p.incomes.income_amount->sum()>0)->size() = 1";
		
		result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile);
			
	}

}
