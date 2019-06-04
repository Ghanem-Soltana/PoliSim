
package adapters.umlImpl;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;


public class EDatatypeUtil {

	public static EClassifier convertFromString (String nameLiteral){
		EDataType type = null;
		if (nameLiteral.equalsIgnoreCase("EBoolean"))
			type = EcorePackage.eINSTANCE.getEBoolean();
		if (nameLiteral.equalsIgnoreCase("boolean"))
			type = EcorePackage.eINSTANCE.getEBoolean();
		else if (nameLiteral.equalsIgnoreCase("integer"))
			type = EcorePackage.eINSTANCE.getEBigInteger();
		if (nameLiteral.equalsIgnoreCase("real"))
			type = EcorePackage.eINSTANCE.getEBigDecimal();
		else if (nameLiteral.contains("unlimited"))
			type = EcorePackage.eINSTANCE.getEBigDecimal();
		if (nameLiteral.equalsIgnoreCase("String"))
			type = EcorePackage.eINSTANCE.getEString();
		else if (nameLiteral.contains("EInt"))
			type = EcorePackage.eINSTANCE.getEBigInteger();
		else if (nameLiteral.contains("EByte"))
			type = EcorePackage.eINSTANCE.getEByte();
		else if (nameLiteral.contains("EBigInt"))
			type = EcorePackage.eINSTANCE.getEBigInteger();
		else if (nameLiteral.contains("EBigDecimal"))
			type = EcorePackage.eINSTANCE.getEBigDecimal();
		else if (nameLiteral.contains("EString"))
			type = EcorePackage.eINSTANCE.getEString();
		else if (nameLiteral.toLowerCase().contains("edouble"))
			type = EcorePackage.eINSTANCE.getEBigDecimal();
		else if (nameLiteral.contains("String"))
			type = EcorePackage.eINSTANCE.getEString();
		else if (nameLiteral.contains("ELong"))
			type = EcorePackage.eINSTANCE.getEBigDecimal();	
		else if (nameLiteral.equalsIgnoreCase("EBigInteger"))
			type = EcorePackage.eINSTANCE.getEBigInteger();
		else if (nameLiteral.toLowerCase().contains("date"))
			type = EcorePackage.eINSTANCE.getEDate();
		
		return type;
	}
	
	public static EClassifier convertFromType (EObject obj){
		if(!(obj instanceof EDataType))
			return (EClassifier) obj;
		EClassifier type=null;
		EDataType nameLiteral =(EDataType) obj;
		if (nameLiteral.getName().equalsIgnoreCase("EBoolean"))
			type= EcorePackage.eINSTANCE.getEBoolean();
		if (nameLiteral.getName().equalsIgnoreCase("boolean"))
			type = EcorePackage.eINSTANCE.getEBoolean();
		else if (nameLiteral.getName().equalsIgnoreCase("integer"))
			type = EcorePackage.eINSTANCE.getEBigInteger();
		else if (nameLiteral.getName().equalsIgnoreCase("EBigInteger"))
			type = EcorePackage.eINSTANCE.getEBigInteger();
		if (nameLiteral.getName().equalsIgnoreCase("real"))
			type = EcorePackage.eINSTANCE.getEDouble();
		else if (nameLiteral.getName().contains("unlimited"))
			type = EcorePackage.eINSTANCE.getEDouble();
		if (nameLiteral.getName().equalsIgnoreCase("String"))
			type = EcorePackage.eINSTANCE.getEString();
		else if (nameLiteral.getName().contains("EInt"))
			type = EcorePackage.eINSTANCE.getEBigInteger();
		else if (nameLiteral.getName().contains("EByte"))
			type = EcorePackage.eINSTANCE.getEByte();
		else if (nameLiteral.getName().contains("EBigInt"))
			type = EcorePackage.eINSTANCE.getEBigInteger();
		else if (nameLiteral.getName().contains("EBigDecimal"))
			type = EcorePackage.eINSTANCE.getEDouble();
		else if (nameLiteral.getName().contains("EString"))
			type = EcorePackage.eINSTANCE.getEString();
		else if (nameLiteral.getName().contains("EDouble"))
			type = EcorePackage.eINSTANCE.getEDouble();
		else if (nameLiteral.getName().contains("String"))
			type = EcorePackage.eINSTANCE.getEString();
		else if (nameLiteral.getName().contains("ELong"))
			type = EcorePackage.eINSTANCE.getEDouble();
		else if (nameLiteral.getName().toLowerCase().contains("date"))
			type = EcorePackage.eINSTANCE.getEDate();
	
			
		return type;
	}
	
	public static String convertFromTypeToString (EObject obj){
		EDataType nameLiteral = null;
		if(obj instanceof EDataType)
		 nameLiteral =(EDataType) obj;
		else return ((EClassifier)obj).getName();
		String res=nameLiteral.getName();
		if (nameLiteral.getName().equalsIgnoreCase("EBoolean"))
			res= "Boolean";
		else if (nameLiteral.getName().equalsIgnoreCase("boolean"))
			res= "Boolean";
		else if (nameLiteral.getName().equalsIgnoreCase("integer"))
			res= "Integer";
		else if (nameLiteral.getName().equalsIgnoreCase("EBigInteger"))
			res= "Integer";
		else if (nameLiteral.getName().equalsIgnoreCase("real"))
			res= "Real";
		else if (nameLiteral.getName().equalsIgnoreCase("String"))
			res = "String";
		else if (nameLiteral.getName().contains("EInt"))
			res= "Integer";
		else if (nameLiteral.getName().contains("EByte"))
			res= "Integer";
		else if (nameLiteral.getName().contains("EBigInt"))
			res= "Integer";
		else if (nameLiteral.getName().contains("EBigDecimal"))
			res= "Real";
		else if (nameLiteral.getName().contains("EString"))
			res = "String";
		else if (nameLiteral.getName().contains("EDouble"))
			res= "Real";
		else if (nameLiteral.getName().contains("String"))
			res = "String";
		else if (nameLiteral.getName().contains("ELong"))
			res= "Real";

	
	
		return res;
	}
	
	
}
