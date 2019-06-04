
package util;
import interfaces.IOclParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.helper.OCLHelper;

import util.ProcessingException;

public class EmfOclParser implements IOclParser<Constraint, Resource> {

  @Override
  public Constraint parseOclConstraint(Object context, String key, String constraint) {
    OCL ocl = org.eclipse.ocl.ecore.OCL.newInstance();
    OCLHelper<EClassifier, EOperation, EStructuralFeature, Constraint> helper = ocl.createOCLHelper();
    
    if (context instanceof EClassifier ){     
    	  helper.setContext((EClassifier)context);
      }
    else if (context instanceof EOperation) {
    	EOperation eOp = (EOperation) context;
      helper.setOperationContext(eOp.getEContainingClass(), eOp);
    } 
    else if (context instanceof EStructuralFeature  ) {
    	EStructuralFeature sf = (EStructuralFeature) context;
      helper.setAttributeContext(sf.getEContainingClass(), sf);
    }
    try {
      Constraint c = null;
      if (key.equalsIgnoreCase("postcondition")) //$NON-NLS-1$
        c = helper.createPostcondition(constraint);
      else if (key.equalsIgnoreCase("precondition")) //$NON-NLS-1$
        c = helper.createPrecondition(constraint);
      else //"invariant"
        c = (Constraint)helper.createInvariant(constraint);    
      return c;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public Constraint parseOclConstraint1(Object context, String key, String constraint) throws ParserException {
	    OCL ocl = org.eclipse.ocl.ecore.OCL.newInstance();
	    OCLHelper<EClassifier, EOperation, EStructuralFeature, Constraint> helper = ocl.createOCLHelper();
	    
	    if (context instanceof EClassifier ){     
	    	  helper.setContext((EClassifier)context);
	      }
	    else if (context instanceof EOperation) {
	    	EOperation eOp = (EOperation) context;
	      helper.setOperationContext(eOp.getEContainingClass(), eOp);
	    } 
	    else if (context instanceof EStructuralFeature  ) {
	    	EStructuralFeature sf = (EStructuralFeature) context;
	      helper.setAttributeContext(sf.getEContainingClass(), sf);
	    }

	      Constraint c = null;
	      if (key.equalsIgnoreCase("postcondition")) //$NON-NLS-1$
	        c = helper.createPostcondition(constraint);
	      else if (key.equalsIgnoreCase("precondition")) //$NON-NLS-1$
	        c = helper.createPrecondition(constraint);
	      else //"invariant"
	        c = (Constraint)helper.createInvariant(constraint);    
	      return c;
	

	  }
	  
	  
  
  public List<Constraint> cleanConstraintsFromClass(Resource modelResource, EClass c ) {
		List<Constraint> constraints = new ArrayList<Constraint>();

			EAnnotation eaConstraints = c.getEAnnotation("http://www.eclipse.org/emf/2002/Ecore");
			EAnnotation eaOCLPivot = c.getEAnnotation("http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot");
			if (eaConstraints != null && eaOCLPivot != null) {
				String constraintNames = eaConstraints.getDetails().get("constraints");
				if (constraintNames != null) {
					String[] constraintNamesArr = constraintNames.split(" ");
					for (String constraintName : constraintNamesArr) {
						String constraintBody = eaOCLPivot.getDetails().get(constraintName);
						Constraint ct=null;
						try{
						ct = parseOclConstraint1(c, constraintName, constraintBody);
						}catch(Exception e)
						{
							//System.err.println(eaConstraints.getDetails());
							//System.err.println(eaOCLPivot.getDetails().remove(savPivot));
						}
						if (ct != null) {
							ct.setName(constraintName);
							constraints.add(ct);
						}
					}
				}
			}
			return constraints;
  }
	public List<Constraint> parseEmbeddedConstraints(Resource modelResource,ArrayList<EClass> allClasses) {
		List<Constraint> constraints = new ArrayList<Constraint>();

		for (EClass c : allClasses) {

			// TODO: where are these constants defined?
			// TODO: make code more robust and verbose
			EAnnotation eaConstraints = c.getEAnnotation("http://www.eclipse.org/emf/2002/Ecore");
			EAnnotation eaOCLPivot = c.getEAnnotation("http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot");
			if (eaConstraints != null && eaOCLPivot != null) {
				String constraintNames = eaConstraints.getDetails().get("constraints");
				if (constraintNames != null) {
					String[] constraintNamesArr = constraintNames.split(" ");
					for (String constraintName : constraintNamesArr) {
						String constraintBody = eaOCLPivot.getDetails().get(constraintName);
						//System.out.println("Processing constraint: " + constraintName);
						//System.out.println("Body: " + constraintBody);
						Constraint ct = parseOclConstraint(c, constraintName, constraintBody);
						if (ct != null) {
							ct.setName(constraintName);
							constraints.add(ct);
						}
					}
				}
			}

			/*
			 * FIXME: This way of getting the OCL constraints seems to be out of
			 * date?
			 */
			List<EAnnotation> annotationList = c.getEAnnotations();
			if (annotationList != null)
				for (EAnnotation ea : annotationList) {

					if (ea.getSource().endsWith("Pivot") |ea.getSource().endsWith("ocl") || ea.getSource().endsWith("OCL")) //$NON-NLS-1$ //$NON-NLS-2$
						for (String key : ea.getDetails().keySet()) {
							EObject context = ea.getEModelElement();
							String val = ea.getDetails().get(key);
							if (!key.equalsIgnoreCase("invariant") && !key.equalsIgnoreCase("body") && !key.equalsIgnoreCase("derivation") && !key.equalsIgnoreCase("precondition") && !key.equalsIgnoreCase("postcondition") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
									&& !key.equalsIgnoreCase("inv") && !key.equalsIgnoreCase("pre") && !key.equalsIgnoreCase("post") && context instanceof EClassifier) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								Constraint ct = parseOclConstraint(ea.getEModelElement(), key, val);
								ct.setName(key);
								constraints.add(ct);
							}
						}

				}

			List<EOperation> operationList = c.getEOperations();
			if (operationList != null)
				for (EOperation op : operationList) {
					annotationList = op.getEAnnotations();
					if (annotationList != null)
						for (EAnnotation ea : annotationList)
							if (ea.getSource().endsWith("Pivot") |ea.getSource().endsWith("ocl") || ea.getSource().endsWith("OCL")) 
								for (String key : ea.getDetails().keySet()) {
									EObject context = ea.getEModelElement();
									String val = ea.getDetails().get(key);
									if (key.equalsIgnoreCase("precondition") || key.equalsIgnoreCase("pre") || key.equalsIgnoreCase("postcondition") || key.equalsIgnoreCase("post") && context instanceof EOperation) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
										Constraint ct = parseOclConstraint(ea.getEModelElement(), key, val);
										ct.setName(op.getName() + "_" + key); //$NON-NLS-1$
										constraints.add(ct);
									}
								}
				}
		}
		return constraints;
	}

	@Override
	public List<Constraint> parseEmbeddedConstraints(Resource modelResource) {
		List<Constraint> constraints = new ArrayList<Constraint>();
		EmfModelReader modelReader = new EmfModelReader(modelResource);
		for (EClass c : modelReader.getClasses()) {

			// TODO: where are these constants defined?
			// TODO: make code more robust and verbose
			EAnnotation eaConstraints = c.getEAnnotation("http://www.eclipse.org/emf/2002/Ecore");
			EAnnotation eaOCLPivot = c.getEAnnotation("http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot");
			if (eaConstraints != null && eaOCLPivot != null) {
				String constraintNames = eaConstraints.getDetails().get("constraints");
				if (constraintNames != null) {
					String[] constraintNamesArr = constraintNames.split(" ");
					for (String constraintName : constraintNamesArr) {
						String constraintBody = eaOCLPivot.getDetails().get(constraintName);
						//System.out.println("Processing constraint: " + constraintName);
						//System.out.println("Body: " + constraintBody);
						Constraint ct = parseOclConstraint(c, constraintName, constraintBody);
						if (ct != null) {
							ct.setName(constraintName);
							constraints.add(ct);
						}
					}
				}
			}

			/*
			 * FIXME: This way of getting the OCL constraints seems to be out of
			 * date?
			 */
			List<EAnnotation> annotationList = c.getEAnnotations();
			if (annotationList != null)
				for (EAnnotation ea : annotationList) {

					if (ea.getSource().endsWith("Pivot") |ea.getSource().endsWith("ocl") || ea.getSource().endsWith("OCL")) //$NON-NLS-1$ //$NON-NLS-2$
						for (String key : ea.getDetails().keySet()) {
							EObject context = ea.getEModelElement();
							String val = ea.getDetails().get(key);
							if (!key.equalsIgnoreCase("invariant") && !key.equalsIgnoreCase("body") && !key.equalsIgnoreCase("derivation") && !key.equalsIgnoreCase("precondition") && !key.equalsIgnoreCase("postcondition") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
									&& !key.equalsIgnoreCase("inv") && !key.equalsIgnoreCase("pre") && !key.equalsIgnoreCase("post") && context instanceof EClassifier) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								Constraint ct = parseOclConstraint(ea.getEModelElement(), key, val);
								ct.setName(key);
								constraints.add(ct);
							}
						}

				}

			List<EOperation> operationList = c.getEOperations();
			if (operationList != null)
				for (EOperation op : operationList) {
					annotationList = op.getEAnnotations();
					if (annotationList != null)
						for (EAnnotation ea : annotationList)
							if (ea.getSource().endsWith("Pivot") |ea.getSource().endsWith("ocl") || ea.getSource().endsWith("OCL")) 
								for (String key : ea.getDetails().keySet()) {
									EObject context = ea.getEModelElement();
									String val = ea.getDetails().get(key);
									if (key.equalsIgnoreCase("precondition") || key.equalsIgnoreCase("pre") || key.equalsIgnoreCase("postcondition") || key.equalsIgnoreCase("post") && context instanceof EOperation) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
										Constraint ct = parseOclConstraint(ea.getEModelElement(), key, val);
										ct.setName(op.getName() + "_" + key); //$NON-NLS-1$
										constraints.add(ct);
									}
								}
				}
		}
		return constraints;
	}

  @Override
  public List<Constraint> parseOclDocument(Resource modelResource, File oclDocument) throws ProcessingException {
    List<Constraint> constraints = new ArrayList<Constraint>();
    if (oclDocument != null) {
      InputStream in;
	try {
		in = new FileInputStream(oclDocument.getAbsolutePath());
	} catch (FileNotFoundException e) {
		throw new ProcessingException( e );
	}  
	
      OCLInput document = new OCLInput(in);
      Resource resource = modelResource;
      EcoreEnvironmentFactory ecoreEnv = new EcoreEnvironmentFactory(resource.getResourceSet().getPackageRegistry());
      OCL oclParser = OCL.newInstance(ecoreEnv);
      try {
    
		constraints = oclParser.parse(document);
	} catch (ParserException e) {
		throw new ProcessingException( e );
		}
    }
    return constraints;
  }  

  @Override
  public List<Constraint> parseModelConstraints(Resource modelResource, File oclDocument) throws ProcessingException  {
    List<Constraint> constraints =  parseEmbeddedConstraints(modelResource);
    constraints.addAll(parseOclDocument(modelResource,oclDocument));
    return constraints;
  }

  @Override
  public List<String> getModelConstraintsNames(Resource modelResource, File oclDocument) throws ProcessingException {
    List<Constraint> constraints = parseModelConstraints(modelResource, oclDocument);
    List<String> cNames = new ArrayList<String>();
    for (Constraint c : constraints) { 
      if (c.getName() == null) //OCL preconditions and postconditions parsed from an external OCL document don't have any name
        cNames.add(""); //$NON-NLS-1$
      cNames.add(c.getName());
    }
    return cNames;
  }
  
  @Override
  public List<String> getModelInvariantNames(Resource modelResource, File oclDocument) throws ProcessingException  {
    List<Constraint> constraints = parseModelConstraints(modelResource, oclDocument);
    List<String> cNames = new ArrayList<String>();
    for (Constraint c : constraints)     
      if (!c.getStereotype().equals("precondition") && !c.getStereotype().equals("postcondition")) //$NON-NLS-1$ //$NON-NLS-2$
        cNames.add(c.getName());
    return cNames;
  }
  
  public List<Constraint> getEveryThing(Resource modelResource, File oclDocument) 
  {
	List<Constraint> res =	parseModelConstraints (modelResource,oclDocument);
	
	for (int i = 0; i < res.size()-1; i++) {
		for (int j = i+1; j < res.size(); j++) {
			
			if(res.get(i).getName().equalsIgnoreCase(res.get(j).getName()))
			{
				res.remove(j);
			    j--;
			}
		}
		
	}
	return res;
	
  }
  
}  


