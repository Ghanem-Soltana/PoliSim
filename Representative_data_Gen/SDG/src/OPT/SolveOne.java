package OPT;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.CommonUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.helper.OCLHelper;

import snt.oclsolver.distance.ClassDiagramTestData;
import snt.oclsolver.driver.SolverRunner;
import snt.oclsolver.eocl.EclipseOCLWrapper;
import snt.oclsolver.reader.EObjectToInstanceSpecification;
import snt.oclsolver.reader.EcoreXmiToCTuples;
import snt.oclsolver.tuples.ClassifierTuple;
import snt.oclsolver.tuples2environment.UMLEnvironment;
import util.CreateTaxpayers;
import Eval.SolveOnly;
import Main.SDG;

public class SolveOne implements Runnable {
	
	public SolveOne(LinkedList<EObject> roots, UMLEnvironment umlEnv, String newQuery, int searchMaxIteration) {
		super();
		this.roots = roots;
		this.umlEnv = umlEnv;
		this.newQuery = newQuery;
		this.searchMaxIteration = searchMaxIteration;
	}

	public LinkedList<EObject> roots; 
	public UMLEnvironment umlEnv;
	public String newQuery;
	int searchMaxIteration;
	
	 public void run() {
			SolverRunner solver = new SolverRunner();	
			System.gc();	
			EObject previousRoot = null;
		    LinkedList<EObject> satRoots = new LinkedList<EObject>(); 
		    File fileRound1 = new File("Instances//fileRound1.xmi");

	for (int i = 0; i < roots.size() ; i++) {

		EObject theInstance = roots.get(i);
		System.out.println("\n*********Instance nb "+i);
 
			  //parsing the eObject
				EcoreXmiToCTuples ecoreToXMI = new EcoreXmiToCTuples(umlEnv);
				//prepare the solver for the new eObject
				
				EList<EObject> container = new BasicEList<EObject>();
				container.add(theInstance);
				Collection<ArrayList<ClassifierTuple>> previousResult = ecoreToXMI.getCTuplesFromEObjects(container);
				HashMap<EObject, String> mapNames = ecoreToXMI.geteObjectToCvtNameMap();
				ArrayList<ClassifierTuple> tuples = previousResult.iterator().next();
				EObjectToInstanceSpecification transformer = new EObjectToInstanceSpecification(container,mapNames,umlEnv);
				transformer.transformEObjectsToInstanceSpec();
				//transformer.flash(tuples);
				EclipseOCLWrapper ieos = (EclipseOCLWrapper)ClassDiagramTestData.getInstance().getDefaultIEOSObjectUsingFiles();
				OCLHelper oclHelper = ieos.instanceGenerator.getHelper();
				oclHelper.getOCL().getEvaluationEnvironment().clear();
				oclHelper.getOCL().dispose();
				
				System.out.println("The OCL query is: "+newQuery);
				boolean crash = false;
				try {
					previousRoot = solver.solveFromExisting(newQuery.trim(), tuples, searchMaxIteration,umlEnv,SolveOnly.ecoreModel);
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					crash = true;
				}
				
				if(!crash)
				{
				 theInstance = previousRoot;
				}
			// memory leak
			solver.init(umlEnv);
			
			System.out.println("\n*********Check the constraints for the root "+roots.size());
			boolean isSat = SolverRunner.isSat;
			System.out.println("isSat?= "+isSat);
			
	
			if(isSat)
			{satRoots.add(theInstance);
			}
			else {
			SolveOnly.nbRemoved++;
			}

			roots.remove(i);
			i--;
			
			/*if(count%30==0)
			if(!satRoots.isEmpty())  
				{if(!fileRound1.exists())
				  	CreateTaxpayers.save("fileRound1",satRoots);
				  	else GeneratorWithSolver.append("fileRound1", satRoots);
				 for (EObject obj : satRoots) {
					 EcoreUtil.delete(obj, true);
				}
				 satRoots.clear();
				}
				*/
	  	}
	
	if(!satRoots.isEmpty())  
	SolveOnly.satRoots.addAll(satRoots);

	 
	
	 }
}
