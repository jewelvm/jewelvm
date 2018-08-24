/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.cfdump;

import jewel.core.clfile.ClassFile;
import jewel.core.clfile.ClassFormatException;
import jewel.core.clfile.ClassPath;
import jewel.core.clfile.NoClassDefFoundException;
import jewel.core.clfile.VerifyException;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public final class Main {

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("usage: cfdump [-classpath <classpath>|-o <output>] class");
      return;
    }
    ClassPath classPath = new ClassPath(".");
    PrintStream out = System.out;
    int argi;
    for (argi = 0; argi < args.length; argi++) {
      if (!args[argi].startsWith("-"))
        break;
      if (args[argi].equals("-classpath")) {
        if (argi+1 == args.length) {
          System.err.println("cfdump: no classpath provided for `"+args[argi]+"'");
          return;
        }
        classPath = new ClassPath(args[++argi]);
      } else if (args[argi].equals("-o")) {
        if (argi+1 == args.length) {
          System.err.println("cfdump: no output provided for `"+args[argi]+"'");
          return;
        }
        try {
          out = new PrintStream(new FileOutputStream(args[++argi]));
        } catch (FileNotFoundException e) {
          System.err.println("cfdump: could not write output `"+args[argi]+"'");
          return;
        }
      } else {
        System.err.println("cfdump: unknown flag `"+args[argi]+"'");
        return;
      }
    }
    if (argi == args.length) {
      System.err.println("cfdump: no class provided");
      return;
    }
    if (argi+1 != args.length) {
      System.err.println("cfdump: extra parameters found after `"+args[argi]+"'");
      return;
    }
    String mainClass = args[argi].replace('.', '/');
    byte[] buffer;
    try {
      buffer = classPath.findDefinition(mainClass);
    } catch (NoClassDefFoundException e) {
      System.err.println("cfdump: no class definition found `"+mainClass+"'");
      return;
    }
    ClassFile classFile;
    try {
      classFile = ClassFile.parseImage(buffer);
    } catch (ClassFormatException e) {
      System.err.println("cfdump: parsing failure `"+e.getMessage()+"'");
      return;
    }
    Dumper dumper = new Dumper(out);
    try {
      dumper.dumpClass(classFile);
    } catch (VerifyException e) {
      System.err.println("cfdump: static check failure `"+e.getMessage()+"'");
      return;
    }
  }

  private Main() { }

}

