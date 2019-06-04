package Eval;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import snt.oclsolver.distance.Problem;
import snt.oclsolver.driver.SolverRunner;
import snt.oclsolver.eocl.EclipseOCLWrapper;
import snt.oclsolver.reader.EObjectToInstanceSpecification;
import snt.oclsolver.reader.EcoreXmiToCTuples;
import snt.oclsolver.tuples.ClassifierTuple;
import snt.oclsolver.tuples2environment.UMLEnvironment;
import snt.oclsolver.writer.WriteToXmi;
import temp.Main;
import util.Config;
import util.CreateTaxpayers;
import util.EmfModelReader;
import util.EmfOclParser;
import util.Invariant;
import util.OCLForJAVA;
import util.OclAST;
import Main.GenerateSeeds;
import Monitor.DistCalculator;
import Monitor.DistributionChecker;
import Monitor.Histogram;
import Monitor.HistogramClass;
import Monitor.InvariantEvaluation;
import Monitor.ProfileConstraintExtractor;
import adapters.umlImpl.EPackageUMLAdapter;
import adapters.umlImpl.EResourceUMLAdapter;


@SuppressWarnings("restriction")
public class MonitorOnly {
	public String modelFolder;
	public static LinkedList<EClass> allClasses;
	public static EPackage pck;
	public  static String UMLmodel;
	public  static String ecoreModel;
	public static String instances_rep="//Instances//TaxpayersOfFD.xmi";
    public  static LinkedList<EObject> roots ;
    public static String startingPointName;
	public static ResourceSet rs;
	public static int maxInteraction = 100;
	public static int searchMaxIteration = 1000;
	public static String absoluteUMLPath;
	public static String constraintsFromProfilePath = "//model//constraints_from_profile_NOIFS.ocl";
	public static String constraintsFromUserPath = "//model//user_constraints.ocl";
	public static double  diffTolerance = 0.02;
	public static double  satEuclidian = 0.03;
	
	
	public static void main(String args[])throws InterruptedException {
      long start = System.nanoTime(); 
      Thread t1 = new Thread(new PrepareEverything());
  	  t1.start();
  	  t1.join(60*500000);
  	  t1.interrupt();
  	  	
		EClass startingPointClass= OclAST.getClassByName(OclAST.allClasses, startingPointName); 	
     	ProfileConstraintExtractor extractor = new ProfileConstraintExtractor(rs,1,pck,GenerateSeeds.OriginalModel) ;
    	LinkedList<EObject> instancesTemp = new LinkedList<EObject>();
    	LinkedList<Invariant> cons = new LinkedList<Invariant>();
    	extractor.extract(pck,startingPointClass,null,instancesTemp ,startingPointClass,cons);
    	extractor.clean(cons);
    	extractor.writeMain(cons,"//model//constraints_from_profile.ocl");
    	extractor.writeUser("//model//constraints_from_profile.ocl");
    	extractor.writeOperations(OclAST.listeEAllOperation,"//model//operations.ocl");
    	instancesTemp = new LinkedList<EObject>();
    	extractor.SnapShotHistograms(pck,startingPointClass,null,instancesTemp ,startingPointClass);
    	extractor.normelizeDist();
    	saveDistFromProfile();
    	
    	//TODO
		int size = 100;
    	instances_rep = "//Instances//fileRound1_"+size+".xmi";
    	//read instances
        LinkedList<EObject> roots = readInstanes(instances_rep);
        
        // Prepare the inital constraints 
		List<Invariant> constraintsFromPrfoile = ProfileConstraintExtractor.extractInvariantsFromFileAsItIs(constraintsFromProfilePath);
		List<Invariant> constraintsFromUser = ProfileConstraintExtractor.extractInvariantsFromFileAsItIs(constraintsFromUserPath);
		List<Invariant> allConstraints = new LinkedList<Invariant>();
		allConstraints.addAll(constraintsFromPrfoile);
		allConstraints.addAll(constraintsFromUser);
		considerSolver(allConstraints);
		String baseQuery = generateInitalOCLQuery(allConstraints);
        // Init solving envirnment 

		String newQuery = "";
		double totalExecutionTImeSolver = 0;
		Problem.fitnessCounter = 0;
		EclipseOCLWrapper.operationsFileAbsolutePath ="model/operationsMain.ocl";
	    UMLEnvironment umlEnv=new UMLEnvironment();
		umlEnv.setUpEnvironment(absoluteUMLPath,true);
		Collection<ArrayList<ClassifierTuple>> previousResult = null;
		SolverRunner solver = new SolverRunner();	
		LinkedList<EObject> toSave = new LinkedList<EObject>();
		LinkedList<EObject> bests = new LinkedList<EObject>();
		int initalSize = roots.size();
		LinkedList<Invariant> historyOfFails = new LinkedList<Invariant>();
		LinkedList<Invariant> generatedConstraints = new LinkedList<Invariant>();
		LinkedList<Invariant> invariants = new LinkedList<Invariant>();
		File fileUnsat = new File("Instances//UnsatInstances.xmi");
		if(fileUnsat.exists())
		fileUnsat.delete();
		File fileSat = new File("Instances//NewInstancesSolved.xmi");
		if(fileSat.exists())
		fileSat.delete();
		File fileRound1 = new File("Instances//fileRound1.xmi");
		if(fileRound1.exists())
			fileRound1.delete();
		
		
       if(roots.isEmpty())
       {System.out.println("No instances to monitor");
       System.exit(0);
       }
        // Get distribution from sat population
     	DistCalculator plotter = new DistCalculator(roots, ProfileConstraintExtractor.hitogramsForClasses, ProfileConstraintExtractor.hitogramsForAttributes, ProfileConstraintExtractor.hitogramsForAssociations);
     	System.out.println("\n*********Calculated distributions from the generated population");
     	LinkedList<HistogramClass> popHistClasses =	getDistClassesFromPop(plotter);
     	LinkedList<Histogram> popHistAttributes = getDistAttributesFromPop(plotter);
     	LinkedList<Histogram> popHistAssociations = getDistAssociaitonsFromPop(plotter);
     	updateDistances(popHistClasses,popHistAttributes,popHistAssociations, plotter);
     	
     	System.out.println("**********Round2: Improve distributions while remaining sat *******");
		
		 solver = new SolverRunner();	
		 solver.init(umlEnv);
		 toSave = new LinkedList<EObject>();
		 double currentEucDist = Double.valueOf(plotter.calculteAgregated(popHistClasses, popHistAttributes, popHistAssociations)).doubleValue();
		 
		 double saveDist = currentEucDist;
		 
			System.out.println("**********Sort the instances*******");
			/*HashMap<EObject, Double> weights = new  HashMap<EObject, Double>();
	     	for (int i = 0; i < roots.size(); i++) {
	     		EObject toDrop = roots.get(i);
		  	    LinkedList<HistogramClass> myclassBins = plotter.getMyBinsFromClass(toDrop);
			    LinkedList<Histogram> myAttBins = plotter.getMyBinsFromAtt(toDrop);
				LinkedList<Histogram> myAssocBins = plotter.getMyBinsFromAssoc(toDrop);
				
			 	LinkedList<HistogramClass> popHistClasses3 =	getDistClassesFromPop(plotter);
		     	LinkedList<Histogram> popHistAttributes3 = getDistAttributesFromPop(plotter);
		     	LinkedList<Histogram> popHistAssociations3 = getDistAssociaitonsFromPop(plotter);
		
				DistributionChecker disChecker = new DistributionChecker(popHistClasses3, popHistAttributes3, popHistAssociations3, myclassBins,myAttBins,myAssocBins, toDrop);
				disChecker.updatePopDistToDrop();
				
		        LinkedList<EObject> tempPop = new LinkedList<EObject>(roots);
		        tempPop.remove(toDrop);
		     	DistCalculator plotterWithoutMe = new DistCalculator(tempPop, ProfileConstraintExtractor.hitogramsForClasses, ProfileConstraintExtractor.hitogramsForAttributes, ProfileConstraintExtractor.hitogramsForAssociations);
		     	double distanceWithoutME = plotterWithoutMe.calculteAgregated(popHistClasses3,popHistAttributes3,popHistAssociations3);
		     	
		     	weights.put(toDrop, distanceWithoutME);
		    
				
	     	}
	     	
	     	for (int i = 0; i < roots.size()-1; i++) {
	     		double distWithoutI = weights.get(roots.get(i));
	     		for (int j = 0; j < roots.size(); j++) {
	     			double distWithoutJ = weights.get(roots.get(j));
	     			
	     			if(distWithoutJ>distWithoutI)
	     			Collections.swap(roots, i, j);
				}
	     	}
		 */

	     int nbGenerated = 0;
	     int NbFails = 0;
     	//Start monitoring 
     	for (int i = 0; i < roots.size() && currentEucDist> satEuclidian; i++) {
	
			boolean shouldStop = false;
			EObject theInstance = EcoreUtil.copy(roots.get(i));
		
			System.out.println("\n*********Instance nb "+i);
			LinkedList<HistogramClass> myclassBins = plotter.getMyBinsFromClass(theInstance);
	     	LinkedList<Histogram> myAttBins = plotter.getMyBinsFromAtt(theInstance);
			LinkedList<Histogram> myAssocBins = plotter.getMyBinsFromAssoc(theInstance);
			displayBins(myclassBins,"****Cont. from classes intial instance");
			displayBins(myAttBins,"****Cont. from attributes intial instance");
			displayBins(myAssocBins,"****Cont. from associations intial instance");
			System.out.println("*********\n");
			
			List<Invariant> additonalConstraintsClass = new LinkedList<Invariant>();
			List<Invariant> additonalConstraintsAttributes = new LinkedList<Invariant>();
			List<Invariant> additonalConstraintsAssociaitons = new LinkedList<Invariant>();
			
		//P2 dist impact
		DistributionChecker disChecker = new DistributionChecker(popHistClasses, popHistAttributes, popHistAssociations,myclassBins,myAttBins,myAssocBins, theInstance);
		//disChecker.updatePopDistForChange(roots.get(i),roots);

	
		additonalConstraintsClass = disChecker.generateAdditionalConstraintsFromClasses(roots.get(i),roots);

	    additonalConstraintsAssociaitons = disChecker.generateAdditionalConstraintsFromAttributesOrAssociation(roots.get(i),roots,2);
			
	    additonalConstraintsAttributes = disChecker.generateAdditionalConstraintsFromAttributesOrAssociation(roots.get(i),roots,1);

	    DistributionChecker disCheckerBack = new DistributionChecker(popHistClasses, popHistAttributes, popHistAssociations,myclassBins,myAttBins,myAssocBins, roots.get(i));
	    //disCheckerBack.updatePopDistForChange(theInstance,roots);

	    shouldStop = additonalConstraintsClass.isEmpty()&&additonalConstraintsAttributes.isEmpty()&& additonalConstraintsAssociaitons.isEmpty();

		if(shouldStop==false) 
		{
			invariants.clear();
			System.out.println("**** Dynamic constraints for class selection (inital) ***");
			System.err.println(additonalConstraintsClass);
			System.out.println("**** Dynamic constraints for attributes (inital) ***");
			System.err.println(additonalConstraintsAttributes);
			System.out.println("**** Dynamic constraints for class associations (inital) ***");
			System.err.println(additonalConstraintsAssociaitons);
			invariants.addAll(additonalConstraintsClass);
			invariants.addAll(additonalConstraintsAttributes);
			invariants.addAll(additonalConstraintsAssociaitons);
			for (int j = 0; j < invariants.size(); j++) {
				if(invariants.get(j).expression.contains("getAge"))
				{
					invariants.remove(j);
					j--;
				}
			}
			generatedConstraints.addAll(invariants);
			newQuery = generateAddOCLQuery(invariants , baseQuery);
			nbGenerated = nbGenerated + invariants.size();
			System.out.println("\n*********Send the instance to the solver (inital)");
		}
		else {System.out.println("\n*********Nothing to monitor from the beginning");
		toSave.add(roots.get(i));
  		
		}
		
			ArrayList<EObject> SatObjects = new ArrayList<EObject>();
			SatObjects.add(roots.get(i));
			int communication =1;

			System.out.println("\n*********Establish communication with the solver for instance nb "+i);
			while (shouldStop == false && communication <= maxInteraction) {
				
				communication++;
	
				//parsing the eObject
				{
					EcoreXmiToCTuples ecoreToXMI = new EcoreXmiToCTuples(umlEnv);
					//prepare the solver for the new eObject
					EList<EObject> container = new BasicEList<EObject>();
					container.add(theInstance);
					previousResult = ecoreToXMI.getCTuplesFromEObjects(container);
					HashMap<EObject, String> mapNames = ecoreToXMI.geteObjectToCvtNameMap();
					EObjectToInstanceSpecification transformer = new EObjectToInstanceSpecification(container,mapNames,umlEnv);
					transformer.transformEObjectsToInstanceSpec();

				}
			

					
					System.out.println("The OCL query is: "+newQuery);
					System.err.println(i);
					boolean crash = false;
					long startSolver = System.nanoTime();
					try {
				theInstance = solver.solveFromExisting(newQuery.trim(), null, searchMaxIteration,umlEnv,MonitorOnly.ecoreModel);
					} catch (Exception e) {
						e.printStackTrace();
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						String exceptionAsString = sw.toString();
						crash = true;
						shouldStop=false;
						NbFails++;
					}
				
				
					
					
					/*if(!crash)
					{
					//Transform tuples to EObject
					 WriteToXmi tupleToEObject= new WriteToXmi(MonitorOnly.ecoreModel,"ResFromTuple.xmi",umlEnv);
					 ArrayList<EObject> res = tupleToEObject.getEObjectsFromInstance();
					 if(res.isEmpty())
					 {
						System.err.println("No result found 2");
						System.exit(-1);
					 }
					 
					 if(res.size()>1)
					 {
						System.err.println("Multiple roots found 2.1");
						System.exit(-1);
					 }
					 
					 //no need to copy
					 theInstance = res.get(0);
					}*/
					
					
				// memory leak
				solver.init(umlEnv);
				System.out.println("\n*********Check the constraints ("+communication+")");
				//P1 constraints
				boolean isSat = SolverRunner.isSat;
				if(isSat==false)
				{
				InvariantEvaluation evalCons = new InvariantEvaluation(theInstance,allConstraints);
				evalCons.updateEvaluation();
				 isSat = evalCons.isValid();
				}
				//boolean isSat = SolverRunner.isSat;
				System.out.println("isSat ("+communication+") ="+isSat);
				LinkedList<Invariant> fails = new LinkedList<Invariant>();
				long endSolver = System.nanoTime();
		    	long durationSolver = (endSolver - startSolver);
		    	double new_Solver_duration_second=(double)durationSolver / 1000000000.0;	
		    	totalExecutionTImeSolver = totalExecutionTImeSolver + new_Solver_duration_second;
			
				//LinkedList<Invariant> fails = evalCons.getUnsatisfiedConstraints();
				//addCleanFails(historyOfFails, fails);
			
				
				// see what happen with the added constraints 
				/*if(isSat && !invariants.isEmpty())
				{
				InvariantEvaluation evalConsAdded = new InvariantEvaluation(theInstance,invariants);
				evalConsAdded.updateEvaluation();
				boolean areNewConsSat = evalConsAdded.isValid();
				System.out.println("Are added constraint sat? ("+communication+") ="+areNewConsSat);
				LinkedList<Invariant> fails2 = evalConsAdded.getUnsatisfiedConstraints();
				InvariantEvaluation.diplayList(fails2);
 				addCleanFails(historyOfFails, fails2);
				}*/

				if(!isSat)
				{
					//NOT SAT ==> put the fails once again at the beginning 
					NbFails++;
					InvariantEvaluation.diplayList(fails);
				    invariants = new LinkedList<Invariant>();
					newQuery = generateAddOCLQuery(invariants , baseQuery);
					shouldStop = false;
					theInstance = roots.get(i);
				}
				else 
				{
				SatObjects.add(theInstance);
				if(communication<maxInteraction)
				{
					//explore dist of new instance
					 myclassBins = plotter.getMyBinsFromClass(theInstance);
				     myAttBins = plotter.getMyBinsFromAtt(theInstance);
					 myAssocBins = plotter.getMyBinsFromAssoc(theInstance);
					 additonalConstraintsClass = new LinkedList<Invariant>();
					 additonalConstraintsAttributes = new LinkedList<Invariant>();
					 additonalConstraintsAssociaitons = new LinkedList<Invariant>();
					
				//P2 dist impact
				disChecker = new DistributionChecker(popHistClasses, popHistAttributes, popHistAssociations,myclassBins,myAttBins,myAssocBins, theInstance);
				disChecker.updatePopDistForChange(roots.get(i),roots);

			
				additonalConstraintsClass = disChecker.generateAdditionalConstraintsFromClasses(roots.get(i),roots);

			    additonalConstraintsAssociaitons = disChecker.generateAdditionalConstraintsFromAttributesOrAssociation(roots.get(i),roots,2);
					
			    additonalConstraintsAttributes = disChecker.generateAdditionalConstraintsFromAttributesOrAssociation(roots.get(i),roots,1);

			    disCheckerBack = new DistributionChecker(popHistClasses, popHistAttributes, popHistAssociations,myclassBins,myAttBins,myAssocBins, roots.get(i));
			    disCheckerBack.updatePopDistForChange(theInstance,roots);
				shouldStop = isSat&&additonalConstraintsClass.isEmpty()&&additonalConstraintsAttributes.isEmpty()&& additonalConstraintsAssociaitons.isEmpty();
		
				if(shouldStop==false) 
				{
					invariants.clear();
					System.out.println("**** Dynamic constraints for class selection ("+communication+") ***");
					System.err.println(additonalConstraintsClass);
					System.out.println("**** Dynamic constraints for attributes ("+communication+") ***");
					System.err.println(additonalConstraintsAttributes);
					System.out.println("**** Dynamic constraints for class associations ("+communication+") ***");
					System.err.println(additonalConstraintsAssociaitons);
					invariants.addAll(additonalConstraintsClass);
					invariants.addAll(additonalConstraintsAttributes);
					invariants.addAll(additonalConstraintsAssociaitons);
					generatedConstraints.addAll(invariants);
					newQuery = generateAddOCLQuery(invariants , baseQuery);
					System.out.println("\n*********Send the instance to the solver ("+communication+")");
				}
				else System.out.println("\n*********Select best sat instance ("+communication+")");
				}
				else System.out.println("\n*********Select best sat instance ("+communication+")");
				
			}
		
			}
			
		
			/*//Decide what to do for the traversed object (tax case)
		  	if(SatObjects.isEmpty())
		  	{
		  		EObject toDrop = roots.get(i);
		  	    myclassBins = plotter.getMyBinsFromClass(toDrop);
			    myAttBins = plotter.getMyBinsFromAtt(toDrop);
				myAssocBins = plotter.getMyBinsFromAssoc(toDrop);
				//displayBins(myclassBins,"****Cont. from classes of instance to drop");
			    //displayBins(myAttBins,"****Cont. from attributes of instance to drop");
			    //displayBins(myAssocBins,"****Cont. from associations of instance to drop");
				DistributionChecker disChecker = new DistributionChecker(popHistClasses, popHistAttributes, popHistAssociations,myclassBins,myAttBins,myAssocBins, toDrop);
				disChecker.updatePopDistToDrop();
				LinkedList<EObject> tempContainer = new LinkedList<EObject>();
				
				//save dropped instances 
				tempContainer.add(roots.get(i));
				if(!fileUnsat.exists())
				CreateTaxpayers.save("UnsatInstances",tempContainer);
				else append("UnsatInstances", tempContainer);
		  		roots.remove(i);
		  		i--;
		  		nbRemoved++;
		  		logger.trace("!!!! Unable to solve\n");
		  	}*/
			if(!SatObjects.isEmpty())
		  	{
		  		EObject best = SatObjects.get(0);
		  		//myclassBins = plotter.getMyBinsFromClass(best);
			   // myAttBins = plotter.getMyBinsFromAtt(best);
				//myAssocBins = plotter.getMyBinsFromAssoc(best);
				//displayBins(myclassBins,"****Cont. from classes of instance condidate best (first)");
				//displayBins(myAttBins,"****Cont. from attributes of instance condidate best (first)");
				//displayBins(myAssocBins,"****Cont. from associations of instance condidate best (first)");
			   // disChecker = new DistributionChecker(popHistClasses, popHistAttributes, popHistAssociations,myclassBins,myAttBins,myAssocBins, best);
				// disChecker.updatePopDistForChange(roots.get(i),roots);
		     	double min = currentEucDist;
		     	//roots.set(i, best);
		     	int pos_Min = 0;

		  		for (int j = 1; j < SatObjects.size(); j++) {
					
		  			EObject condidateForBest = SatObjects.get(j);
			  		myclassBins = plotter.getMyBinsFromClass(condidateForBest);
				    myAttBins = plotter.getMyBinsFromAtt(condidateForBest);
					myAssocBins = plotter.getMyBinsFromAssoc(condidateForBest);
					//displayBins(myclassBins,"****Cont. from classes of instance condidate best "+j);
					//displayBins(myAttBins,"****Cont. from attributes of instance condidate best "+j);
					//displayBins(myAssocBins,"****Cont. from associations of instance condidate best "+j);
				    disChecker = new DistributionChecker(popHistClasses, popHistAttributes, popHistAssociations,myclassBins,myAttBins,myAssocBins, condidateForBest);
				    disChecker.updatePopDistForChange(roots.get(i),roots);
					roots.set(i, condidateForBest);
					double eval = Double.valueOf(String.valueOf(plotter.calculteAgregated(popHistClasses,popHistAttributes,popHistAssociations))).doubleValue();
		  			if(eval<=min)
		  			{
		  				min = eval;
		  				pos_Min = j;
		  			}
				}
		  		
		  		if(SatObjects.size()>1)
		  		{best = SatObjects.get(pos_Min);
		  		myclassBins = plotter.getMyBinsFromClass(best);
			    myAttBins = plotter.getMyBinsFromAtt(best);
				myAssocBins = plotter.getMyBinsFromAssoc(best);
				//displayBins(myclassBins,"****Cont. from classes of instance best");
				//displayBins(myAttBins,"****Cont. from attributes of instance best");
				//displayBins(myAssocBins,"****Cont. from associations of instance best");
			    disChecker = new DistributionChecker(popHistClasses, popHistAttributes, popHistAssociations,myclassBins,myAttBins,myAssocBins, best);
				disChecker.updatePopDistForChange(roots.get(i),roots);
				plotter.calculteAgregated(popHistClasses,popHistAttributes,popHistAssociations);
				roots.set(i, best);
		  		}
		  	
		  
		  		toSave.add(best);
		  		bests.add(best);
		  		
		  	}
			updateDistances(popHistClasses,popHistAttributes,popHistAssociations, plotter);
		    currentEucDist = Double.valueOf(String.valueOf(plotter.calculteAgregated(popHistClasses,popHistAttributes,popHistAssociations))).doubleValue();
			roots.remove(i);
			i--;
     	}

   
     	
     	if(!toSave.isEmpty())
     	{
    	if(!fileSat.exists())
		CreateTaxpayers.save("NewInstancesSolved",toSave);
     	else append("NewInstancesSolved", toSave);
     	}
     	
    	
        plotter = new DistCalculator(toSave, ProfileConstraintExtractor.hitogramsForClasses, ProfileConstraintExtractor.hitogramsForAttributes, ProfileConstraintExtractor.hitogramsForAssociations);

    	String finalEucDistance = String.valueOf(plotter.calculteAgregated(popHistClasses,popHistAttributes,popHistAssociations));
        String finalManDistance = String.valueOf(plotter.calculteAgregatedManhaten(popHistClasses,popHistAttributes,popHistAssociations));
        String finalCanberaDistance = String.valueOf(plotter.calculteCanbera(popHistClasses,popHistAttributes,popHistAssociations));

    	System.out.println("Final Euc. distance= "+ finalEucDistance);
    	System.out.println("Final Man. distance= "+ finalManDistance);
    	System.out.println("Final Canbera distance= "+ finalCanberaDistance);
		System.out.println("The inital size of the population "+initalSize);
		System.out.println("The fitness functions was called "+Problem.fitnessCounter +" time(s)");
		long endTime = System.nanoTime();
    	long duration = (endTime - start);
    	double duration_second=(double)duration / 1000000000.0;	
        System.out.println("Time spent on the solver = "+Math.round(totalExecutionTImeSolver)+ " seconds");
        System.out.println("Time spent on the monitor = "+(Math.round(duration_second)-Math.round(totalExecutionTImeSolver))+ " seconds");
        System.out.println("the number of generatred constraints "+nbGenerated);
        System.out.println("the number of fails "+NbFails);
		System.out.println("Done!");
    }
    
    
    @SuppressWarnings("unused")
	private static void addCleanFails(LinkedList<Invariant> historyOfFails, LinkedList<Invariant> fails) {
		for (Invariant constraint : fails) {
			boolean exists = false;
			int j=0;
			while (j<historyOfFails.size()&&exists==false)
			{
				if(historyOfFails.get(j).getexpression().equals(constraint.getexpression()))
					exists=true;
				else j++;
			}
			if(!exists)
			historyOfFails.add(constraint);
		}
		
	}


	private static void considerSolver(List<Invariant> allConstraints) {
		for (int i = 0; i < allConstraints.size(); i++) {
			if(allConstraints.get(i).expression.contains("indexOf"))
			{
				allConstraints.remove(i);
				i--;
			}
		}
		
	}


	private static String generateInitalOCLQuery (List<Invariant> allConstraints) 
    {

    	String cumul = "";
    	   int pos=1;
		   for (Invariant cons : allConstraints) {
	
			   	
				 String header = "context "+ cons.getContext().getName()+ " inv inv"+pos+": \n";
				 String temp = header + " " + cons.expression;
				 String cleanQuery="";
					
			  	  cleanQuery = temp.replaceAll("\\r|\\n", " ");
			      cleanQuery = cleanQuery.replaceAll("\\s{2,}", " ").trim();
			      
			      /*
			      //Sorry but the solver is so stupid
			      if(cleanQuery.contains("/2"))
			      {
			    	 cleanQuery = header + " " + "self.declared_amount >= 50 and self.declared_amount <= if((self.income.income_amount / 2)>50) then self.income.income_amount / 2 else 50 endif";
			         cleanQuery = cleanQuery.replaceAll("\\r|\\n", " ").replaceAll("\\s{2,}", " ").trim();
			      }
			      
			      //Sorry but the solver is so stupid
			      if(cleanQuery.contains("self.getAge(2014) <=2 and (self.allowances->size()=1)"))
			      {
			    	 cleanQuery = header + " " + "(self.getAge(2014) <=2 and self.allowances->size()=1) or (self.getAge(2014)>2 and self.getAge(2014) <=18 and self.allowances->size()=1) or (self.getAge(2014) <=27 and self.continued_studies and self.allowances->size()=1) or (self.getAge(2014)>18 and self.continued_studies = false and self.allowances->size()=0)";
			         cleanQuery = cleanQuery.replaceAll("\\r|\\n", " ").replaceAll("\\s{2,}", " ").trim();
			      }
			      */
			      
				if(pos==1)
				cumul = cumul +cleanQuery;
				else cumul = cumul + "\n" + " and "+cleanQuery;
				pos++; 
			
		}
		   
		   
    	
    	
    	if(cumul.trim().equals(""))
    	return "true";
    	return cumul;
    }

	

	
	//TODO tweek to avoid problems
	private static String generateAddOCLQuery (LinkedList<Invariant> invariants, String cumul) 
    {
    	
    	   int pos=1;
    	   
		   for (Invariant inv : invariants) {

				 String header = "context "+ inv.getContext().getName()+ " inv add"+pos+": \n";
				 String temp = header + " " + inv.getexpression();
				 String cleanQuery = temp.replaceAll("\\r|\\n", " ").replace(")then ", ") then ").replace("(if", "( if").replaceAll(" ", "   ");
				
				 
				/*
				if(!cleanQuery.trim().startsWith("let origin :"))
				{
				 String[] tab1 = cleanQuery.split(" if ");
			     cleanQuery = merge (tab1, " if( ");
			     tab1=cleanQuery.split(" then ");
			     cleanQuery = merge (tab1, ") then ");
				}*/
			     cleanQuery = cleanQuery.replaceAll("\\s{2,}", " ").trim();
			     
			     /*
			      //Sorry but the solver is so stupid
			      if(cleanQuery.contains("/2"))
			      {
			    	 cleanQuery = header + " " + "self.declared_amount >= 50 and self.declared_amount <= if((self.income.income_amount / 2)>50) then self.income.income_amount / 2 else 50 endif";
			         cleanQuery = cleanQuery.replaceAll("\\r|\\n", " ").replaceAll("\\s{2,}", " ").trim();
			      }
			      
			      //Sorry but the solver is so stupid
			      if(cleanQuery.contains("self.getAge(2014) <=2 and (self.allowances->size()=1)"))
			      {
			    	 cleanQuery = header + " " + "(self.getAge(2014) <=2 and self.allowances->size()=1) or (self.getAge(2014)>2 and self.getAge(2014) <=18 and self.allowances->size()=1) or (self.getAge(2014) <=27 and self.continued_studies and self.allowances->size()=1) or (self.getAge(2014)>18 and self.continued_studies = false and self.allowances->size()=0)";
			         cleanQuery = cleanQuery.replaceAll("\\r|\\n", " ").replaceAll("\\s{2,}", " ").trim();
			      }
			      */
			     if(!cumul.trim().equals(""))
				cumul = cleanQuery + "\n" + " and "+cumul;
			     else cumul = cleanQuery;
				pos++; 
		
		}
		   
    	
    	
    	if(cumul.trim().equals(""))
    	return "true";
    	return cumul;
    }
    
	@SuppressWarnings("unused")
	private static String merge(String[] tab1, String conn) {
		if(tab1==null)
		return "";
		if(tab1.length==0)
			return "";
		
		if(tab1.length==1)
			return tab1[0];
		
		String res = tab1[0];
		for (int i = 1; i < tab1.length; i++) {
			res = res +  conn + tab1[i];
		}
		return res;
		
	}



    
    @SuppressWarnings("unused")
	private  static LinkedList<Invariant>  convertToInvariants(List<Constraint> allConstraints) {
		LinkedList<Invariant> res = new LinkedList<Invariant>();
		for (org.eclipse.ocl.ecore.Constraint constraint : allConstraints) {
			Invariant temp = new Invariant((Class)ProfileConstraintExtractor.getCorrespondant(constraint.getConstrainedElements().get(0),ProfileConstraintExtractor.realClasses), ProfileConstraintExtractor.cleanExpression(constraint.getSpecification().getBodyExpression().toString()));
			res.add(temp);
		}
		
		return res;
	}

	private static void displayBins(LinkedList<?> myclassBins, String sms) {
		
		System.out.println(sms);
    for (Object object : myclassBins) {
		System.out.println(object);
	}
		
	}

	@SuppressWarnings("unused")
	private static void displayDistances(LinkedList<HistogramClass> popHistClasses,
			LinkedList<Histogram> popHistAttributes, LinkedList<Histogram> popHistAssociations, DistCalculator plotter) {
		System.out.println("*********\n");
		System.out.println("\n*********Euclidian distances");
		System.out.println("****Histograms from class selection");
		for (HistogramClass hist :popHistClasses) {
			System.out.println(hist);
			System.out.println(hist.probabilities);
			System.out.println("vs");
			System.out.println(plotter.getCorrependantClass(hist).probabilities);
			System.out.println("Distance ====> "+hist.distance);
		}
		
		System.out.println("****Histograms from attributes");
		for (Histogram hist :popHistAttributes) {
			System.out.println(hist);
			System.out.println(hist.probabilities);
			System.out.println("vs");
			System.out.println(plotter.getCorrependantAttribute(hist).probabilities);
			System.out.println("Distance ====> "+hist.distance);
		}
		
		System.out.println("****Histograms from associations");
		for (Histogram hist :popHistAssociations) {
			System.out.println(hist);
			System.out.println(hist.probabilities);
			System.out.println("vs");
			System.out.println(plotter.getCorrependantAssociation(hist).probabilities);
			System.out.println("Distance ====> "+hist.distance);
		}
		System.out.println("*********\n");
		
	}



	private static double updateDistances(LinkedList<HistogramClass> popHistClasses,LinkedList<Histogram> popHistAttributes, LinkedList<Histogram> popHistAssociations, DistCalculator plotter) {
		plotter.calculteDistancesClasses(popHistClasses);
		plotter.calculteDistancesAttributes(popHistAttributes);
		plotter.calculteDistancesAssociations(popHistAttributes);
		return plotter.calculteAgregated(popHistClasses,popHistAttributes,popHistAssociations);
		
	}


	private static LinkedList<Histogram> getDistAssociaitonsFromPop(DistCalculator plotter) {
		LinkedList<Histogram> res = plotter.saveAssociations();
		System.out.println("****Histograms from associations");
		for (Histogram hist :res) {
			System.out.println(hist);
			hist.CalculateProbaFromFrequencies();
		}
		return res;
	}


	private static LinkedList<Histogram> getDistAttributesFromPop(DistCalculator plotter) {
		
		System.out.println("****Histograms from attributes");
		LinkedList<Histogram> res = plotter.saveAttributes();
		for (Histogram hist :res) {
			System.out.println(hist);
			hist.CalculateProbaFromFrequencies();
		}
		return res;
	}


	private static LinkedList<HistogramClass> getDistClassesFromPop(DistCalculator plotter) {
		System.out.println("****Histograms from class selection");
		LinkedList<HistogramClass> res  = plotter.saveClasses();
		for (HistogramClass hist :res) {
	
			System.out.println(hist);
			hist.CalculateProbaFromFrequencies();
		}
		return res;
	}


	private static void saveDistFromProfile() {
		System.out.println("\n*********Input distributions from the profile");
		System.out.println("****Histograms from class selection");
    	for (HistogramClass hist : ProfileConstraintExtractor.hitogramsForClasses) {
			System.out.println(hist);
		}
    	
    	System.out.println("****Histograms from attributes");
     	for (Histogram hist : ProfileConstraintExtractor.hitogramsForAttributes) {
			System.out.println(hist);
		}
     	
     	System.out.println("****Histograms from associations");

     	for (Histogram hist : ProfileConstraintExtractor.hitogramsForAssociations) {
			System.out.println(hist);
		}
    	System.out.println("*********\n");
		
	}


	//To test
	@SuppressWarnings("unused")
	private static void mutateEobject(EObject context) {

		
			OCLForJAVA.init("",context);
			EObject taxpayer = OCLForJAVA.evaluateEObject(context, "Tax_Payer.allInstances()->any(true)", "taxpayer", "Tax_Payer");
			OCLForJAVA.init("",taxpayer);
			String tempOCL = "self.incomes->any(true).income_type";
 			EObject old_type = OCLForJAVA.evaluateEObject(taxpayer, tempOCL, "old_type", "Income_Type");
 			tempOCL = "self.incomes->any(true)";
 			EObject income = OCLForJAVA.evaluateEObject(taxpayer, tempOCL, "income", "Income");
 			EClass mainEClass = income.eClass();
 		 	EStructuralFeature feature_To_BeUpdated = getEFeatureByName( mainEClass,"income_type");
 			EClass choosenEClass = OclAST.reader.getClassByName(mainEClass.getEPackage(), "Employment_Income");
 			EObject newType = EcoreUtil.create(choosenEClass);
 			EcoreUtil.replace(income, feature_To_BeUpdated, old_type, newType);
 		    tempOCL = "self.incomes->any(true).details->any(true)";
 			EObject detail = OCLForJAVA.evaluateEObject(taxpayer, tempOCL, "detail", "Income_Detail");
 			mainEClass = detail.eClass();
 		 	EStructuralFeature att = getEFeatureByName( mainEClass,"distance");
 		 	detail.eSet(att, 9000.0);

		}
		


	public static EStructuralFeature getEFeatureByName(EClass c, String name)
	{
		EList<EStructuralFeature> all = c.getEAllStructuralFeatures();
		for (EStructuralFeature eStructuralFeature : all) {
		if(eStructuralFeature.getName().trim().equals(name.trim()))
			return eStructuralFeature;
		}
	return null;
	}


    
    public static LinkedList<EObject> readInstanes(String instances_rep1)
    {
    	//prepare stand alone

		
		 
		    File f=new File("");
		    final String folderPath = f.getAbsolutePath();
		//Loading instances
		System.out.println("Loading Instances..");
		ResourceSet load_resourceSet = new ResourceSetImpl();
		load_resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		load_resourceSet.getPackageRegistry().put(OclAST.reader.getPackages().get(0).getNsURI(),OclAST.reader.getPackages().get(0));
		Resource load_resource = load_resourceSet.getResource(URI.createURI(folderPath+instances_rep1),true);
		EList<EObject> elements= load_resource.getContents();
		OCLForJAVA.init("FD",elements.get(0));
		String tempOCL= "Tax_Payer.allInstances()->any(true)";
		EObject taxpayer = OCLForJAVA.evaluateEObject(elements.get(0), tempOCL, "taxpayer","Tax_Payer"); 
		OCLForJAVA.init("FD",taxpayer);
		// add housholds
		String ocla = "let p1:Set(Household)= Household.allInstances() in p1";
		Collection<EObject> taxpayersCollection = OCLForJAVA.evaluateECollection(taxpayer,ocla,"all","Tax_Case","OrderedSet"); 
		LinkedList<EObject> roots = new LinkedList<EObject>();
		Iterator<EObject> it = taxpayersCollection.iterator();
		while(it.hasNext())
		{roots.add(it.next());}
		// getWhat is to exclude 
		 ocla = "let p1:Set(Household)= Household.allInstances() in "
		 		+ " let p3:Set(Physical_Person)=Set{p1.parents.individual_A,p1.parents.individual_B}->flatten() in p3";
	      taxpayersCollection = OCLForJAVA.evaluateECollection(taxpayer,ocla,"all","Tax_Case","OrderedSet"); 
		 it = taxpayersCollection.iterator();
		LinkedList<EObject> toExclude = new LinkedList<EObject>();
		while(it.hasNext())
		{toExclude.add(it.next());}
	
		//add taxpayers 
		
		 ocla = "let p1:Set(Tax_Payer)= Tax_Payer.allInstances() in p1";
		 taxpayersCollection = OCLForJAVA.evaluateECollection(taxpayer,ocla,"all","Tax_Case","OrderedSet"); 
		 it = taxpayersCollection.iterator();
		while(it.hasNext())
		{EObject actual = it.next();
		if(!toExclude.contains(actual))
		roots.add(actual);}
	
		return roots;
    }
    
    public static void removeDuplicatREderences(LinkedList<EReference> list)
 	{
 		 
 		LinkedHashSet<EReference> s = new LinkedHashSet<EReference>(list);
 		list.clear();
 		list.addAll(s);
 	}
    public static void removeDuplicatAttribute(LinkedList<EAttribute> list)
	{
		 
		LinkedHashSet<EAttribute> s = new LinkedHashSet<EAttribute>(list);
		list.clear();
		list.addAll(s);
	}
    
    public static void generateEcoreModel(String name, EPackage pck){
        
        ResourceSetImpl resourceSet = new ResourceSetImpl(); 
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl()); 
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(resourceSet.getPackageRegistry());
        resourceSet.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,extendedMetaData);

        final File f = new File("");
        final String dossierPath = f.getAbsolutePath().replace("\\", "//");
        final String apiSamplePath = dossierPath +"//Models//"; 
        final String file_name=name;
        String path = "file://" + apiSamplePath + file_name;
        URI uri = URI.createURI(path); 
        Resource resource = resourceSet.createResource(uri); 
        resource.getContents().add(pck); 

     
        try {
    		resource.save(null);
    		
    		cleanFileForDerived(file_name);
    		
    	} catch (Exception e) {
    		//TODO there is a problem here
    		//e.printStackTrace();
    	} 
        
    
    }
    
	//cannot Cache this
	public static void append(String name, LinkedList<EObject> inst){
		 final String model="//Papyrus//TaxCard.uml";
		 final String instances_rep="//Instances//"+name+".xmi";
		 
		 
		    File f=new File("");
		    final String folderPath = f.getAbsolutePath();
	        final String absolutePath = folderPath + model;
    
	         

	        	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
	            		    "uml", new UMLResourceFactoryImpl());
	

	      ResourceSet rs = new ResourceSetImpl(); 
	      final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
	      rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,extendedMetaData);
	      
	      
	      URI modelFileURI = URI.createFileURI(absolutePath);          
	      Resource resource=null;
	      resource = Main.loadUmlResource(modelFileURI,rs);
	    	  
			Iterator<EObject> i = resource.getAllContents();
	 		while (i.hasNext()) {
	 			Object o = i.next();
	 			 if (o instanceof EPackage) {
	 	              EPackage p = (EPackage)o;
	 	              rs.getPackageRegistry().put(p.getNsURI(), p);       	
	 	     
	 	          }
	 			

	 	 	}
			
	 		//read the model
	 		EmfModelReader reader = new EmfModelReader(resource);  
		    	 ResourceSet load_resourceSet = new ResourceSetImpl();
			 	 load_resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
			 	 load_resourceSet.getPackageRegistry().put(reader.getPackages().get(0).getNsURI(),reader.getPackages().get(0));
			 	 Resource load_resource = load_resourceSet.getResource(URI.createURI(folderPath+instances_rep),true);
			
			 	 EList<EObject> elements= load_resource.getContents();
	
    ResourceSetImpl resourceSet = new ResourceSetImpl(); 
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl()); 
    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
    final String apiSamplePath = dossierPath +"//Instances//"; 
    final String file_name=name+".xmi";
    String path = "file://" + apiSamplePath + file_name;
    URI uri = URI.createURI(path); 
    Resource resource1 = resourceSet.createResource(uri); 
    inst.addAll(elements);

    resource1.getContents().addAll(inst); 
	
    
    try {
		resource1.save(null);
	} catch (IOException e) {
		//e.printStackTrace();
	} }


	private static void cleanFileForDerived(String file_name) {
		 
		   File inputFile = new File("Models"+file_name);
	       File tempFile = new File("Models//Ecores//temp.ecore");
try{
	   BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	   BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
	   String lineToRemove1 = "transient=\"true\"";
	   String lineToRemove2 = "volatile=\"true\"";
	   String lineToRemove3 = "readonly=\"true\"";
	   String currentLine;

	 while((currentLine = reader.readLine()) != null)
	 {
	   
	   
	   writer.write(currentLine.replace(lineToRemove1, " transient=\"false\" ").replace(lineToRemove2, " volatile=\"false\" ").replace(lineToRemove3, " readonly=\"false\" "));
	   writer.newLine();
	 }
		    
		    reader.close();
		    writer.close();
		    inputFile.delete();
		    tempFile.renameTo(inputFile);

		}catch(Exception e){}
	}

	public static Resource loadUmlResource (URI modelUri,ResourceSet rSet,String modelFile) {
		final URL umlJarFileLocation = org.eclipse.uml2.uml.resources.ResourcesPlugin.class.getResource("ResourcesPlugin.class");
		String umlJarPath = umlJarFileLocation.toString();
		umlJarPath = umlJarPath.substring(0, umlJarPath.indexOf('!'));
		final Map<URI, URI> uriMap = URIConverter.URI_MAP;
		uriMap.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), URI.createURI(umlJarPath + "!/libraries/"));

		UMLResourcesUtil.init(rSet);
		rSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		UMLPackage.eINSTANCE.eClass();

		UMLResource r = (UMLResource)rSet.getResource(modelUri, true);
		GenerateSeeds.OriginalModel=(Model)r.getContents().get(0);
		EcoreUtil.resolveAll(r);	
		UMLResourcesUtil.init(rSet);
		EResourceUMLAdapter r1= new EResourceUMLAdapter(r);
	    pck =((EPackageUMLAdapter)r1.getContents().get(0)).getPck();
		generateEcoreModel(modelFile.replace(".uml", ".ecore"),pck);
		r.getContents().clear();
		r.getContents().add(pck);
		EcoreUtil.resolveAll(r);	
		UMLResourcesUtil.init(rSet);
		return r;

	}
	
	public static Resource loadEmfResource(URI modelFileURI,ResourceSet rSet) {
		return  rSet.getResource(modelFileURI, true);
	}
	
	public static void cloneAndCleanOCl(String oclFilePath, File oclFile) throws IOException
	{
		InputStream input = new FileInputStream(oclFilePath);
 		OutputStream output = new FileOutputStream(oclFilePath.replace(".ocl", "_temp.ocl"));	
 		IOUtils.copy(input, output);
		RandomAccessFile tools = new RandomAccessFile(oclFile, "rw"); 
		tools.readLine(); 
		long length = tools.length() - tools.getFilePointer(); 
		byte[] nexts = new byte[(int) length];
		tools.readFully(nexts); 
		tools.seek(0); 
		tools.write(nexts); 
		tools.setLength(nexts.length); 
		tools.close(); 
		
	}
	
	public static LinkedList<EObject> loadInstances(EmfModelReader reader)
	{
		 
	     File f=new File("");
	     String folderPath = f.getAbsolutePath();
	     System.out.println("Loading Instances..");
	 	 ResourceSet load_resourceSet = new ResourceSetImpl();
	 	 load_resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	 	 load_resourceSet.getPackageRegistry().put(reader.getPackages().get(0).getNsURI(),reader.getPackages().get(0));
	 	 Resource load_resource = load_resourceSet.getResource(URI.createURI(folderPath+instances_rep),true);
	 	 EList<EObject> elements= load_resource.getContents();
	   	 OCLForJAVA.init("FD",elements.get(0));
	 	 String tempOCL= "Tax_Payer.allInstances()->any(true)";
		 EObject taxpayer = OCLForJAVA.evaluateEObject(elements.get(0), tempOCL, "taxpayer","Tax_Payer"); 
	   	 OCLForJAVA.init("FD",taxpayer);
	 	 Collection<EObject> taxpayersCollection = OCLForJAVA.evaluateECollection(taxpayer," let all:Set(Tax_Payer)= Tax_Payer.allInstances()in all->select(oclIsKindOf(Tax_Payer))->sortedBy(incomes->sortedBy(income_amount)->last().income_amount)","taxpayers","Tax_Payer","OrderedSet"); 
	 	 LinkedList<EObject> taxpayers = new LinkedList<EObject>();
	     Iterator<EObject> it = taxpayersCollection.iterator();
	 	 while(it.hasNext())
	 	 {
	 		taxpayers.add(it.next());
	 	 }
	 	 return taxpayers;
	}
	
	
	
    private static class PrepareEverything implements Runnable {

    
        public void run() {
        	startingPointName ="Tax_Payer";
     		try{
     			startingPointName=Config.getProperty("rootClass", "dataConfig");
  			}catch(Exception e){e.printStackTrace();}
  
     		
        	String modelFolder = "Papyrus";
 			try{
 				modelFolder=Config.getProperty("modelFolder", "dataConfig");
 			}catch(Exception e){e.printStackTrace();}
 			
        	 String modelFile= "TaxCard.uml";
        	 
        		try{
        			modelFile=Config.getProperty("modelFile", "dataConfig");
     			}catch(Exception e){e.printStackTrace();}
   	 
        		UMLmodel="//"+modelFolder+"//"+modelFile;
        		
        		
        		ecoreModel= "//Models//Ecores//TaxCard.ecore";
        		try{
        			ecoreModel=Config.getProperty("ECOREFile", "dataConfig");
     			}catch(Exception e){e.printStackTrace();}

    	     
    	     String oclFile="//model//constraints.ocl";
 			try{
 				oclFile=Config.getProperty("OCLFileForSlicing", "dataConfig");
 			}catch(Exception e){e.printStackTrace();}
 			
 			try{
 				maxInteraction=Double.valueOf(Config.getProperty("maxInteraction", "dataConfig")).intValue();
 			}catch(Exception e){e.printStackTrace();}
 		
 			try{
 				searchMaxIteration=Double.valueOf(Config.getProperty("searchMaxIteration", "dataConfig")).intValue();
 			}catch(Exception e){e.printStackTrace();}
 		
 			
 			try{
 				constraintsFromProfilePath=Config.getProperty("constraintsFromProfilePath", "dataConfig");
 			}catch(Exception e){e.printStackTrace();}
 		
 			
 			
 			try{
 				instances_rep=Config.getProperty("instances", "dataConfig");
 			}catch(Exception e){e.printStackTrace();}
 		
 		
 			try{
 				constraintsFromUserPath=Config.getProperty("constraintsFromUserPath", "dataConfig");
 			}catch(Exception e){e.printStackTrace();}
 			
 			try{
 				diffTolerance=new Double(Config.getProperty("diffTolerance", "dataConfig")).doubleValue();
 			}catch(Exception e){e.printStackTrace();}
 		
 			try{
 				satEuclidian=new Double(Config.getProperty("satEuclidian", "dataConfig")).doubleValue();
 			}catch(Exception e){e.printStackTrace();}
 		
 			
 			
    	    File f=new File("");
    	    final String folderPath = f.getAbsolutePath();
    	    absoluteUMLPath = folderPath + UMLmodel; 
             String oclFilePath = folderPath + oclFile; 
             String ext = FilenameUtils.getExtension(absoluteUMLPath);      
             
       
             if (ext.equalsIgnoreCase("ecore"))
            		 {
            	         Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
               		    "ecore", new EcoreResourceFactoryImpl());
            		 }
             else if (ext.equalsIgnoreCase("uml")) 	{
            	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
                		    "uml", new UMLResourceFactoryImpl());
            	 
             } else{System.out.println("Unsupported extension");
             System.exit(0);
             }


          rs = new ResourceSetImpl(); 
          final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
          rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,extendedMetaData);
          
          
          URI modelFileURI = URI.createFileURI(absoluteUMLPath);          
          Resource resource=null;
          if (ext.equalsIgnoreCase("ecore"))
           resource = loadEmfResource(modelFileURI,rs);
          else
        	  resource = loadUmlResource(modelFileURI,rs,"//Ecores//"+modelFile);
        	
    		Iterator<EObject> i = resource.getAllContents();
     		while (i.hasNext()) {
     			Object o = i.next();
     			 if (o instanceof EPackage) {
     	              EPackage p = (EPackage)o;
     	              rs.getPackageRegistry().put(p.getNsURI(), p);       	
     	     
     	          }
     			

     	 	}
    		
     		//read the model
     		EmfModelReader reader = new EmfModelReader(resource);     

     		reader.getPackages().get(0);
     		
    		
       		 System.out.println("Loading Model..");
     	   for(EClass c: reader.getClasses())
            System.out.println("Class = " +c.getName());     
     	   OCL ocl = org.eclipse.ocl.ecore.OCL.newInstance();
     	    OCLHelper<EClassifier, EOperation, EStructuralFeature, Constraint> helper = ocl.createOCLHelper();
     	    OclAST.allEnumerations = (LinkedList<EEnum>) reader.getEnumerations();
            OclAST.allClasses = (LinkedList<EClass>) reader.getClasses();
            System.out.println("NB all classes = "+ OclAST.allClasses.size());
     	    OclAST.reader=reader;
            OclAST oclVisitor = OclAST.getInstance();     
            OclAST.listeEAllOperation = (LinkedList<EOperation>) reader.getAllOperations();
            OclAST.helper=helper;
            OclAST.ocl=ocl; 
        		System.out.println("\n Always slice");
         		EmfOclParser oclParser = new EmfOclParser();
         		File oclF=new File(oclFilePath.replace(".ocl", "_temp.ocl"));
         		try{
         		cloneAndCleanOCl (oclFilePath,oclF);  		
         		}catch (Exception e)
         		{e.printStackTrace();}
        		List<Constraint> cList = oclParser.parseOclDocument(resource, oclF);
                for (Constraint c : cList) {
                  if (!c.getStereotype().equalsIgnoreCase("precondition") && !c.getStereotype().equalsIgnoreCase("postcondition")) {
                    ExpressionInOCL<?, ?> oclExpression = (ExpressionInOCL<?, ?>) c.getSpecification();
                	EClass contextCls = (EClass) c.getConstrainedElements().get(0);
                    helper.setContext(contextCls);        
                    OclAST.addEClass(OclAST.listeEClass, contextCls); 
                    oclExpression.accept(oclVisitor);
        
                  }
                }
                
                System.out.println("NB classes reffered to = "+OclAST.listeEClass.size());
                System.out.println("NB enummuration classs used = "+OclAST.listeEnum.size());      
                System.out.println("NB attributes reffered to = " + OclAST.listeEAttribute.size());
                System.out.println("NB EReferences reffered to = " + OclAST.listeEReference.size());
                System.out.println("NB used operations = " + OclAST.listeEOperation.size());           
            
                System.out.println("*********Classes");
           
                
                for (EClass a: OclAST.listeEClass)
                	{System.out.println(a);
                	EList<EAttribute> myAtts = a.getEAllAttributes();
                	for (EAttribute eAttribute : myAtts) {
    					if(eAttribute.isID())
    					{OclAST.listeEAttribute.add(eAttribute);
    					OclAST.orderedListe.add(eAttribute);
    					}
    				}
                	}
                System.out.println("*********Enumurations");
                for (EEnum b: OclAST.listeEnum)
                	{System.out.println(b);}
                System.out.println("*********Attributes");
                for (EAttribute c: OclAST.listeEAttribute)
                	{System.out.println(c);}
                System.out.println("*********References");
                for (EReference d: OclAST.listeEReference)
                    {System.out.println(d);}
                System.out.println("*********Operations");
                for (EOperation d: OclAST.listeEOperation)
                    {System.out.println(d);}
                
                System.out.println("*********The ordered list");
                for (EObject d: OclAST.orderedListe)
                {System.out.println(d);}
                
         		
     			removeDuplicatAttribute(OclAST.listeEAttribute);
     			OclAST.listeEOperation.addAll(OclAST.listeEAllOperation );			
     			OclAST.orderedListe.addAll(OclAST.allClasses);
     			OclAST.orderedListe.addAll(OclAST.allEnumerations);
     			OclAST.orderedListe.addAll(OclAST.listeEAttribute);
     			OclAST.orderedListe.addAll(OclAST.listeEAllOperation);
     			OclAST.orderedListe.addAll(OclAST.listeEReference);

	}
            	
   	
    }
        
	
	
}