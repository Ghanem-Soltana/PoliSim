
package interfaces;

import java.io.File;
import java.util.List;

import util.ProcessingException;

public interface IOclParser<CT, R> {
  
  CT parseOclConstraint(Object context, String key, String constraint);
  
  List<CT> parseOclDocument(R modelResource,  File oclDocument) throws ProcessingException;
  
  List<CT> parseEmbeddedConstraints(R modelResource);
  
  List<CT> parseModelConstraints(R modelResource, File oclDocument) throws ProcessingException;
  
  List<String> getModelConstraintsNames(R modelResource, File oclDocument) throws ProcessingException;

  List<String> getModelInvariantNames(R modelResource, File oclDocument) throws ProcessingException;
}
