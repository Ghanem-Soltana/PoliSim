package test.snt.oclsolver.taxpayer.Gen; 
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.junit.Test;
import snt.oclsolver.search.SearchAlgorithmEnum;
import snt.oclsolver.tuples.ClassifierTuple;
import test.snt.oclsolver.AbstractTestCase;
import snt.oclsolver.tuples2environment.UMLEnvironment;

public class Test_All_Gen extends AbstractTestCase { 
	String umlFile = "examples/TaxPayer/TaxCard.uml";
	String path = "ObjectDiagramModel";


 @Test 
public void testQuery01() { 
ArrayList<ClassifierTuple> result = null; 
String query ="context PhysicalPerson inv inv1:   self.addresses->size() >= 1"; 
result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path); 
String str = this.verifyResult(query,result); 
assertEquals("true", str); 
} 

 @Test 
public void testQuery02() { 
ArrayList<ClassifierTuple> result = null; 
String query ="context TaxPayer inv inv2:   self.incomes->size() >= 1"; 
result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path); 
String str = this.verifyResult(query,result); 
assertEquals("true", str); 
} 

 @Test 
public void testQuery03() { 
ArrayList<ClassifierTuple> result = null; 
String query ="context Child inv inv3:   self.responsible.oclIsUndefined() = false "; 
result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path); 
String str = this.verifyResult(query,result); 
assertEquals("true", str); 
} 

 @Test 
public void testQuery04() { 
ArrayList<ClassifierTuple> result = null; 
String query ="context Income inv inv4:   self.taxpayer.oclIsUndefined() = false "; 
result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path); 
String str = this.verifyResult(query,result); 
assertEquals("true", str); 
} 

 @Test 
public void testQuery05() { 
ArrayList<ClassifierTuple> result = null; 
String query ="context TaxCard inv inv5:   self.income.oclIsUndefined() = false "; 
result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path); 
String str = this.verifyResult(query,result); 
assertEquals("true", str); 
} 

}
