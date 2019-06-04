package test.snt.oclsolver.taxpayer.Gen; 
import static org.junit.Assert.assertEquals;
import java.io.BufferedWriter; 
import java.io.File; 
import java.io.FileWriter; 
import java.util.ArrayList; 
import java.util.Collection; 
import org.junit.Before; 
import org.junit.Test;
import snt.oclsolver.search.SearchAlgorithmEnum;
import snt.oclsolver.writer.WriteToXmi;
import snt.oclsolver.tuples.ClassifierTuple;
import test.snt.oclsolver.AbstractTestCase;
import snt.oclsolver.tuples2environment.UMLEnvironment;

public class Test_User_Gen_Combined_From_Existing extends AbstractTestCase { 
	String umlFile = "examples/TaxPayer/TaxCard.uml";
	String path = "ObjectDiagramModel";
String instanceFilePath="TaxpayersOfFD.xmi";
String instancesFromTuple="InstanceDiagramFromTuples.xmi";
Collection<Collection<ClassifierTuple>> classifierTupleList = null;
UMLEnvironment umlEnv= null;

String query =;

@Before 
public void testPopulateCTs(){
	classifierTupleList = test(instanceFilePath,1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path);}


@Test
 public void testQuery01() throws Exception{
umlEnv = new UMLEnvironment();
umlEnv.setUpEnvironment(umlFile);
WriteToXmi writeToXmi= new WriteToXmi(umlEnv);
ArrayList<ClassifierTuple> previousResult = writeToXmi.getNextResult(classifierTupleList);
ArrayList<ClassifierTuple> result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path,previousResult);
String str = this.verifyResult(query,result);
assertEquals("true", str);
writeToXmi.saveFromInstance();
}
}

