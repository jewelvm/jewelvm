/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jiff;

import jewel.core.clfile.ClassInfo;
import jewel.core.clfile.ClassInfoLoader;
import jewel.core.clfile.ClassPath;
import jewel.core.clfile.NoClassDefFoundException;

import java.util.Arrays;

final class JMachine extends ClassInfoLoader {

  private final ClassPath classPath;

  public JMachine(String classpath) {
    this.classPath = new ClassPath(classpath);
  }

  protected byte[] findClassDef(String name) throws NoClassDefFoundException {
    return classPath.findDefinition(name);
  }

  protected ClassInfo newClassInfo(ClassInfo underlying, ClassInfo superclass, ClassInfo[] interfaces) {
    return new JClass(underlying, superclass, interfaces);
  }
  
  public String[] getClassNames() {
    String[] names =  classPath.getAllNames();
    Arrays.sort(names);
    return names;
  }

}

