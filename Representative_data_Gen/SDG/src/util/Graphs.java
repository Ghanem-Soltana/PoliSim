package util;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;



public class Graphs {
	
	

	
	 // a noeud is class not a set of classes
	  public static DefaultDirectedGraph <String, DefaultEdge> DomainToDependenciesGraph(EPackage pck, EClass start, CreateTaxpayers gen)
	  {
		  DefaultDirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		  List<EClass> liste = EmfModelReader.getClassesFromPackage(pck);
		  liste.add(0,start);
		  
		  
		  for (EClass element : liste) {
			  if(element!=null)
			  if(element.getName()!=null)
			  if(!element.getName().trim().equals("")&&!g.vertexSet().contains(element.getName()))
				{
				String vertex = element.getName();	
				g.addVertex(vertex);	
				}	
		}
		  

		  //add OCL dependencies For refs and attributes
		  LinkedList<EObject> all = new LinkedList<EObject>();
		  all.addAll(OclAST.listeEReference);
		  all.addAll(OclAST.listeEAttribute);
		  
		  for (EObject elment : all) {
			if(elment instanceof EReference)
			{
				EReference ref = (EReference) elment;	
				LinkedList<EObject> dep = new LinkedList<EObject>();
				if(gen.getNeedsForRef(ref)!=null)
				dep.addAll(gen.getNeedsForRef(ref));
				raiseToClasses(dep);
				purifyClasses(dep);
				//System.err.println(ref.getName()+" "+dep);
				// the target is the source of the edge because it is the one which requires the other 
				EClass source = (EClass)ref.getEType();
				breakGeneralization(dep,source);
				String sourceName = source.getName();

				Set<String> nodes = g.vertexSet();
				String sourceNode = getvertex(nodes, sourceName);
				
				for (int i = 0; i < dep.size(); i++) {
					EClass target = (EClass)dep.get(i);
					String targetName = target.getName();
					if( sourceNode!=null)
					{
					LinkedList<EClass> superTypes = getGroupe(targetName,liste);
					for (EClass eClass : superTypes) {				
					String targetNode = getvertex(nodes, eClass.getName());
					if(targetNode!=null)
					{
						
								if(!targetNode.equals(sourceNode))	
								if(g.containsEdge(targetNode, sourceNode)==false)
								if (g.containsEdge(sourceNode, targetNode)==false)
									g.addEdge(targetNode,sourceNode);
								else 
								{System.err.println("(REF: "+ref.getName()+") There is a cyclic dependency between "+sourceName+ " and " + targetName);
								//System.exit(0);
								}
							
					
						

						
						}
					}
					
				}
				}
				
	
			}
			
			
			
			if(elment instanceof EAttribute)
			{
				EAttribute att = (EAttribute) elment;		
				LinkedList<EObject> dep =  new LinkedList<EObject>();
				if(gen.getNeedsForAtt(att)!=null)
				dep.addAll(gen.getNeedsForAtt(att));
				raiseToClasses(dep);
				purifyClasses(dep);
				//System.err.println(ref.getName()+" "+dep);
				// the target is the source of the edge because it is the one which requires the other 
				EClass source = (EClass)att.getEContainingClass();
				breakGeneralization(dep,source);
				String sourceName = source.getName();

				Set<String> nodes = g.vertexSet();
				String sourceNode = getvertex(nodes, sourceName);
				
				for (int i = 0; i < dep.size(); i++) {
					EClass target = (EClass)dep.get(i);
					String targetName = target.getName();
					if( sourceNode!=null)
					{
					LinkedList<EClass> superTypes = getGroupe(targetName,liste);
					for (EClass eClass : superTypes) {				
					String targetNode = getvertex(nodes, eClass.getName());
					if(targetNode!=null)
					{
						
								if(!targetNode.equals(sourceNode))	
								if(g.containsEdge(targetNode, sourceNode)==false)
								if (g.containsEdge(sourceNode, targetNode)==false)
									g.addEdge( targetNode,sourceNode);
								else 
								{System.err.println("(ATT: "+att.getName()+" | "+att.getEContainingClass().getName()+") There is a cyclic dependency between "+sourceName+ " and " + target);
								System.exit(0);}
							
					
						

						
						}
					}
					
				}
				}
				
	
			}
						
		}
		  
		  
		  //add Ocl dependencies 
		  
		  
		  return g;
	  }
	  
	
	  private static void breakGeneralization(LinkedList<EObject> dep, EClass source) {
		for (int i = 0; i < dep.size(); i++) {
			EClass theTargetClass = (EClass)dep.get(i);

				if(source.getName().equals(theTargetClass.getName()))
				{dep.remove(i);
				i--;}
			else {
				if(existsIn(CreateTaxpayers.getAllChildrenClasses(source),theTargetClass))
				{dep.remove(i);
				i--;}
				else 
				{
					if(existsIn(CreateTaxpayers.getAllChildrenClasses(theTargetClass),source))
					{dep.remove(i);
					i--;}
				}
			}
			
			}
		
	}


	private static LinkedList<EClass> getGroupe(String targetNode, List<EClass> liste) {
		
		LinkedList<EClass> res = new LinkedList<EClass>();
		boolean find = false ;
		int i=0;
		EClass corrEClass=null;
		while (i<liste.size()&&find == false) {
			if(liste.get(i).getName().equals(targetNode))
			{
				find =true;
				corrEClass=liste.get(i);
			}
			else i++;
		}
		LinkedList<EClass> children = CreateTaxpayers.getAllChildrenClasses(corrEClass);
		res.add(corrEClass);
		if(children!=null)
		res.addAll(children);
		

		return res;
	}


	public static void purifyClasses(LinkedList<EObject> dep)
	  {

		  for (int i = 0; i < dep.size()-1; i++) {
			  for (int j = i+1; j < dep.size(); j++) {
				EClass classeI= (EClass) dep.get(i);
				EClass classeJ= (EClass) dep.get(j);
				if(classeI.getName().trim().equals(classeJ.getName().trim()))
				{
					dep.remove(j);
					j--;
				}
			}
			
		}
		  
	  }
	  
	  
	public static void raiseToClasses(LinkedList<EObject> dep)
	{
		for (int i = 0; i < dep.size(); i++) {
			EObject element=dep.get(i);
			if(element instanceof EAttribute)
			{
				dep.add(((EAttribute)element).getEContainingClass());
				dep.remove(i);
				i--;
			}
			    if(element instanceof EReference)
			{
				dep.add(((EReference)element).getEType());
				dep.add(((EReference)element).getEContainingClass());
				dep.remove(i);
				i--;
			}
			
		}
	}
	  
	 
	  
	public static String getvertex (Set<String>  nodes,String class_Name)
	{
		String res=null;
		
		Iterator<String> iter = nodes.iterator();
		while (iter.hasNext()) {
		   res=iter.next();
		   
		   if(res.equals(class_Name))
			   return res;
		}
		
		
		return res;
	}
		

	  
	  public static LinkedList<EClass> getEquivelentClasses(EClass classe)
	  {
		  LinkedList<EClass> res = new LinkedList<EClass>();  
		  EClass base= getBaseC(classe);	
		  if(base==null)
			 base=classe;
		  LinkedList<EClass> children = CreateTaxpayers.getAllChildrenClasses((EClass)classe);
			for (EClass  eClass: children) {
				if(eClass.isInterface()==false)
				res.add(eClass);
			}
			
			if(base.isInterface())
			for (EClass eClass : children) {
				EList<EClass> parents = eClass.getEAllSuperTypes();
				  for (EClass eClass2 : parents) {
						if(eClass2.isInterface()==false)
							
					  res.addAll(getEquivelentClasses(eClass2));
				}
				
			}
			
			if( base.isInterface()==false)
			res.add(0,base);
		  return res;
	  }
	  
	  
	  public static EClass getBaseC(EClass c) {
		    if (c.getESuperTypes() == null || (c.getESuperTypes() != null && c.getESuperTypes().size() == 0))
		      return c;
		    if (c.getESuperTypes().size() > 1)
		      return null;
		    return getBaseC(c.getESuperTypes().get(0));
		  }  
	  
	 public static void purify(LinkedList<String> liste)
	 {
		 for (int i = 0; i <liste.size()-1; i++) {
			 for (int j = i+1; j <liste.size(); j++) {
					
				 if(liste.get(i).equals(liste.get(j)))
				 {
					 liste.remove(j);
					 j--;
				 }
				 
				}
		}
	 }
	 
	 public static boolean existsIn (EList<EClass> liste, EClass c)
	 {
		 boolean find = false;
		 int i =0;
		 while (i<liste.size()&&find==false)
		 {
			if(liste.get(i).getName().equals(c.getName()))
				return true;
		 else i++;
		 }
		 return find;
	 }

	 public static boolean existsIn (LinkedList<EClass> liste, EClass c)
	 {
		 boolean find = false;
		 int i =0;
		 while (i<liste.size()&&find==false)
		 {
			if(liste.get(i).getName().equals(c.getName()))
				return true;
		 else i++;
		 }
		 return find;
	 }
	
	public static void test( DirectedGraph<String, DefaultEdge> g ) {
    CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(g);

    if (cycleDetector.detectCycles()) {
       Iterator< String> iterator;
       Set<String> cycleVertices;
       Set<String> subCycle;
       String cycle;

       System.out.println("Cycles detected.");

       // Get all vertices involved in cycles.
       cycleVertices = cycleDetector.findCycles();

       // Loop through vertices trying to find disjoint cycles.
       while (! cycleVertices.isEmpty()) {
          System.out.println("Cycle:");

          // Get a vertex involved in a cycle.
          iterator = cycleVertices.iterator();
          cycle = iterator.next();

          // Get all vertices involved with this vertex.
          subCycle = cycleDetector.findCyclesContainingVertex(cycle);
          for (String sub : subCycle) {
             System.out.println("   " + sub);
             // Remove vertex so that this cycle is not encountered
             // again.
             cycleVertices.remove(sub);
          }
       }
       System.exit(0);
    
    }

    // No cycles.  Just output properly ordered vertices.
    else {
    	String v;
       TopologicalOrderIterator<String, DefaultEdge> orderIterator;

       orderIterator =
          new TopologicalOrderIterator<String, DefaultEdge>(g);
       System.out.println("\nOrdering:");
       while (orderIterator.hasNext()) {
          v = orderIterator.next();
          System.out.println(v);
       }
    }
 }
	
	
	


}
