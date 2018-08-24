/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jiff;

import jewel.core.clfile.ClassInfo;
import jewel.core.clfile.ClassInfo.FieldInfo;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.NoClassDefFoundException;

import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;

public final class Main {

  private static String sourceclasspath = ".";
  private static String targetclasspath = ".";
  private static boolean recursive = false;
  private static String output = null;

  public static void main(String[] args) {
    int argi;
    for (argi = 0; argi < args.length; argi++) {
      String arg = args[argi];
      if (arg.charAt(0) != '-')
        break;
      if (arg.equals("-help")) {
        printUsage(System.err);
        return;
      }
      if (arg.equals("-version")) {
        printVersion(System.err);
        return;
      }
      if (arg.equals("-sourceclasspath")) {
        if (argi+1 == args.length) {
          printMessage(System.err, "missing parameter for option `"+arg+"'");
          return;
        }
        sourceclasspath = args[++argi];
        continue;
      }
      if (arg.equals("-targetclasspath")) {
        if (argi+1 == args.length) {
          printMessage(System.err, "missing parameter for option `"+arg+"'");
          return;
        }
        targetclasspath = args[++argi];
        continue;
      }
      if (arg.equals("-recursive")) {
        recursive = true;
        continue;
      }
      if (arg.equals("-o")) {
        if (argi+1 == args.length) {
          printMessage(System.err, "missing parameter for option `"+arg+"'");
          return;
        }
        output = args[++argi];
        continue;
      }
      printMessage(System.err, "unknown option `"+arg+"'");
      return;
    }

    if (argi == args.length) {
      printUsage(System.err);
      return;
    }

    JMachine sourcemachine = new JMachine(sourceclasspath);
    JMachine targetmachine = new JMachine(targetclasspath);

    PrintStream out = System.out;
    if (output != null)
      try {
        out = new PrintStream(new BufferedOutputStream(new FileOutputStream(output)));
      } catch (IOException e) {
        printMessage(System.err, "i/o error opening output file `"+output+"'");
        return;
      }

    String[] names = sourcemachine.getClassNames();
    for (int i = 0; i < names.length; i++) {
      String name = names[i];
      for (int j = argi; j < args.length; j++) {
        String arg = args[j];
        arg = arg.replace('.', '/');
        if (name.equals(arg) || (name.startsWith(arg+"/") && (recursive || name.indexOf('/', arg.length()+1) == -1))) {

          JClass source;
          try {
            source = (JClass)sourcemachine.loadClass(name);
          } catch (Exception e) {
            e.printStackTrace();
            printMessage(System.err, "error processing source class `"+name+"'");
            return;
          }

          if (source.isPublic()) {

            JClass target = null;
            try {
              target = (JClass)targetmachine.loadClass(name);
            } catch (NoClassDefFoundException e) {
              printMessage(out, name+" is not available");
            } catch (Exception e) {
              e.printStackTrace();
              printMessage(System.err, "error processing target class `"+name+"'");
              return;
            }

            if (source != null && target != null)
              jiff(out, name, source, target);

          }

          break;
        }
      }
    }

  }

  private static void printUsage(PrintStream err) {
    err.println("usage: jiff [-options] class/package...");
    err.println("options:");
    err.println("\t-help\t\t\tprint this message");
    err.println("\t-version\t\tprint version number");
    err.println("\t-sourceclasspath <path>\tset source classpath");
    err.println("\t-targetclasspath <path>\tset target classpath");
    err.println("\t-recursive\t\tprocess subpackages");
    err.println("\t-o <output>\t\tspecify output file");
  }

  private static void printVersion(PrintStream err) {
    err.println("jiff version 0.95a");
  }

  private static void printMessage(PrintStream err, String message) {
    err.println("jiff: "+message);
  }

  private static void jiff(PrintStream out, String name, JClass source, JClass target) {
    if (!target.isPublic())
      printMessage(out, name+" is not public anymore");
    
    if (!source.isFinal())
      if (target.isFinal())
        printMessage(out, name+" is now final");

    if (!source.isAbstract())
      if (target.isAbstract())
        printMessage(out, name+" is now abstract");

    if (!source.isInterface())
      if (target.isInterface())
        printMessage(out, name+" is now an interface");

    if (source.isInterface())
      if (!target.isInterface())
        printMessage(out, name+" is not an interface anymore");

    ClassInfo[] sourcetypes = source.getAllTypes();
    ClassInfo[] targettypes = target.getAllTypes();
    Arrays.sort(targettypes);
    for (int i = 0; i < sourcetypes.length; i++) {
      ClassInfo sourcetype = sourcetypes[i];
      if (!sourcetype.isPublic())
        continue;
      int index = Arrays.binarySearch(targettypes, sourcetype);
      if (index < 0) {
        printMessage(out, name+" is not a subtype of "+sourcetypes[i].getName()+" anymore");
        continue;
      }
      ClassInfo targettype = targettypes[index];
      if (!targettype.isPublic())
        printMessage(out, name+" supertype "+sourcetype.getName()+" is not public anymore");
    }


    FieldInfo[] sourcefields = source.getAllFields();
    FieldInfo[] targetfields = target.getAllFields();
    Arrays.sort(targetfields);
    for (int i = 0; i < sourcefields.length; i++) {
      FieldInfo sourcefield = sourcefields[i];
      if (!sourcefield.isPublic() && !sourcefield.isProtected())
        continue;
      int index = Arrays.binarySearch(targetfields, sourcefield);
      if (index < 0) {
        printMessage(out, name+"."+sourcefield.getName()+":"+sourcefield.getDescriptor()+" field not available anymore");
        continue;
      }
      FieldInfo targetfield = targetfields[index];
      if (targetfield.isPrivate())
        printMessage(out, name+"."+sourcefield.getName()+":"+sourcefield.getDescriptor()+" field is now private");
      if (targetfield.isPackagePrivate())
        printMessage(out, name+"."+sourcefield.getName()+":"+sourcefield.getDescriptor()+" field is now package private");
      if (targetfield.isProtected())
        if (sourcefield.isPublic())
          printMessage(out, name+"."+sourcefield.getName()+":"+sourcefield.getDescriptor()+" field is now protected");
      if (targetfield.isStatic())
        if (!sourcefield.isStatic())
          printMessage(out, name+"."+sourcefield.getName()+":"+sourcefield.getDescriptor()+" field is now static");
      if (!targetfield.isStatic())
        if (sourcefield.isStatic())
          printMessage(out, name+"."+sourcefield.getName()+":"+sourcefield.getDescriptor()+" field is not static anymore");
      if (targetfield.isFinal())
        if (!sourcefield.isFinal())
          printMessage(out, name+"."+sourcefield.getName()+":"+sourcefield.getDescriptor()+" field is now final");
    }
  
    MethodInfo[] sourcemethods = source.getAllMethods();
    MethodInfo[] targetmethods = target.getAllMethods();
    Arrays.sort(targetmethods);
    for (int i = 0; i < sourcemethods.length; i++) {
      MethodInfo sourcemethod = sourcemethods[i];
      if (sourcemethod.getName().equals("<clinit>"))
        continue;
      if (!sourcemethod.isPublic() && !sourcemethod.isProtected())
        continue;
      int index = Arrays.binarySearch(targetmethods, sourcemethod);
      if (index < 0) {
        printMessage(out, name+"."+sourcemethod.getName()+sourcemethod.getDescriptor()+" method not available anymore");
        continue;
      }
      MethodInfo targetmethod = targetmethods[index];
      if (targetmethod.isPrivate())
        printMessage(out, name+"."+sourcemethod.getName()+sourcemethod.getDescriptor()+" method is now private");
      if (targetmethod.isPackagePrivate())
        printMessage(out, name+"."+sourcemethod.getName()+sourcemethod.getDescriptor()+" method is now package private");
      if (targetmethod.isProtected())
        if (sourcemethod.isPublic())
          if (!(targetmethod.getName().equals("<init>") && targetmethod.getOwner().isAbstract()))
            printMessage(out, name+"."+sourcemethod.getName()+sourcemethod.getDescriptor()+" method is now protected");
      if (targetmethod.isStatic())
        if (!sourcemethod.isStatic())
          printMessage(out, name+"."+sourcemethod.getName()+sourcemethod.getDescriptor()+" method is now static");
      if (!targetmethod.isStatic())
        if (sourcemethod.isStatic())
          printMessage(out, name+"."+sourcemethod.getName()+sourcemethod.getDescriptor()+" method is not static anymore");
      if (targetmethod.isFinal())
        if (!sourcemethod.isFinal() && !sourcemethod.getOwner().isFinal())
          printMessage(out, name+"."+sourcemethod.getName()+sourcemethod.getDescriptor()+" method is now final");
      if (targetmethod.isAbstract())
        if (!sourcemethod.isAbstract())
          printMessage(out, name+"."+sourcemethod.getName()+sourcemethod.getDescriptor()+" method is now abstract");
    }
  }

  private Main() { }

}

