package test.snt.oclsolver.taxpayer.Gen; 
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.junit.Test;
import snt.oclsolver.search.SearchAlgorithmEnum;
import snt.oclsolver.tuples.ClassifierTuple;
import test.snt.oclsolver.AbstractTestCase;
import snt.oclsolver.tuples2environment.UMLEnvironment;

public class Test_All_Gen_Combined extends AbstractTestCase { 
	String umlFile = "examples/TaxPayer/TaxCard.uml";
		String path = "ObjectDiagramModel";

String query ="context PhysicalPerson inv inv1: self.addresses->size() >= 1"
+" and context TaxPayer inv inv2: self.incomes->size() >= 1"
+" and context Child inv inv3: self.responsible.oclIsUndefined() = false "
+" and context Income inv inv4: self.taxpayer.oclIsUndefined() = false "
+" and context TaxCard inv inv5: self.income.oclIsUndefined() = false ";

@Test 
public void testQuery01() throws Exception{
ArrayList<ClassifierTuple> result = null; 
result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path); 
String str = this.verifyResult(query,result);
assertEquals("true", str);
}
}

