package de.cau.cs.kieler.doclets.extensions;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import java.util.List;
import org.eclipse.xtend.lib.Data;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.util.ToStringHelper;

@Data
@SuppressWarnings("all")
public class ExtensionDescr {
  private final MethodDoc _methodDoc;
  
  public MethodDoc getMethodDoc() {
    return this._methodDoc;
  }
  
  private final String _classification;
  
  public String getClassification() {
    return this._classification;
  }
  
  private final List<String> _category;
  
  public List<String> getCategory() {
    return this._category;
  }
  
  private final String _containingFile;
  
  public String getContainingFile() {
    return this._containingFile;
  }
  
  public String name() {
    MethodDoc _methodDoc = this.getMethodDoc();
    String _name = _methodDoc.name();
    return _name;
  }
  
  public String firstParamType() {
    MethodDoc _methodDoc = this.getMethodDoc();
    Parameter[] _parameters = _methodDoc.parameters();
    Parameter _head = IterableExtensions.<Parameter>head(((Iterable<Parameter>)Conversions.doWrapArray(_parameters)));
    String _typeName = _head.typeName();
    return _typeName;
  }
  
  public String firstParamName() {
    MethodDoc _methodDoc = this.getMethodDoc();
    Parameter[] _parameters = _methodDoc.parameters();
    Parameter _head = IterableExtensions.<Parameter>head(((Iterable<Parameter>)Conversions.doWrapArray(_parameters)));
    String _name = _head.name();
    return _name;
  }
  
  public Iterable<String> paramTypes() {
    MethodDoc _methodDoc = this.getMethodDoc();
    Parameter[] _parameters = _methodDoc.parameters();
    Iterable<Parameter> _tail = IterableExtensions.<Parameter>tail(((Iterable<Parameter>)Conversions.doWrapArray(_parameters)));
    final Function1<Parameter,String> _function = new Function1<Parameter,String>() {
      public String apply(final Parameter p) {
        String _typeName = p.typeName();
        return _typeName;
      }
    };
    Iterable<String> _map = IterableExtensions.<Parameter, String>map(_tail, _function);
    return _map;
  }
  
  public Iterable<String> paramTypeNames() {
    MethodDoc _methodDoc = this.getMethodDoc();
    Parameter[] _parameters = _methodDoc.parameters();
    Iterable<Parameter> _tail = IterableExtensions.<Parameter>tail(((Iterable<Parameter>)Conversions.doWrapArray(_parameters)));
    final Function1<Parameter,String> _function = new Function1<Parameter,String>() {
      public String apply(final Parameter p) {
        String _name = p.name();
        return _name;
      }
    };
    Iterable<String> _map = IterableExtensions.<Parameter, String>map(_tail, _function);
    return _map;
  }
  
  public ExtensionDescr(final MethodDoc methodDoc, final String classification, final List<String> category, final String containingFile) {
    super();
    this._methodDoc = methodDoc;
    this._classification = classification;
    this._category = category;
    this._containingFile = containingFile;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_methodDoc== null) ? 0 : _methodDoc.hashCode());
    result = prime * result + ((_classification== null) ? 0 : _classification.hashCode());
    result = prime * result + ((_category== null) ? 0 : _category.hashCode());
    result = prime * result + ((_containingFile== null) ? 0 : _containingFile.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ExtensionDescr other = (ExtensionDescr) obj;
    if (_methodDoc == null) {
      if (other._methodDoc != null)
        return false;
    } else if (!_methodDoc.equals(other._methodDoc))
      return false;
    if (_classification == null) {
      if (other._classification != null)
        return false;
    } else if (!_classification.equals(other._classification))
      return false;
    if (_category == null) {
      if (other._category != null)
        return false;
    } else if (!_category.equals(other._category))
      return false;
    if (_containingFile == null) {
      if (other._containingFile != null)
        return false;
    } else if (!_containingFile.equals(other._containingFile))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    String result = new ToStringHelper().toString(this);
    return result;
  }
}
