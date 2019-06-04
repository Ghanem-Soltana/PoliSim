package test.snt.oclsolver.taxpayer.Gen; 
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.junit.Test;
import snt.oclsolver.search.SearchAlgorithmEnum;
import snt.oclsolver.tuples.ClassifierTuple;
import test.snt.oclsolver.AbstractTestCase;
import snt.oclsolver.tuples2environment.UMLEnvironment;

public class Test_User_Gen_Combined extends AbstractTestCase { 
	String umlFile = "examples/TaxPayer/TaxCard.uml";
		String path = "ObjectDiagramModel";

String query =;

@Test 
public void testQuery01() throws Exception{
ArrayList<ClassifierTuple> result = null; 
result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path); 
String str = this.verifyResult(query,result);
assertEquals("true", str);
}
}

