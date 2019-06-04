package OPT;

import java.util.LinkedList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;

import util.Invariant;
import util.OclAST;
import Main.GenerateSeeds;
import Main.SDG;
import Monitor.ProfileConstraintExtractor;

public class Plotter implements Runnable {

	public ResourceSet rs;
	public EPackage pck;
	public EClass startingPointClass;
	
	public Plotter(ResourceSet rs, EPackage pck, EClass startingPointClass)
	{
		this.rs = rs;
		this.pck = pck;
		this.startingPointClass = startingPointClass;
	}
	
	 public void run() {
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
	    	SDG.saveDistFromProfile();
	 }
}
