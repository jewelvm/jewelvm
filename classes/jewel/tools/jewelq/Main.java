/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jewelq;

import jewel.core.Jewel;
import jewel.core.Jewel.Register;
import jewel.core.Jewel.Load;
import jewel.core.Jewel.Meta;
import jewel.core.Jewel.Context;
import jewel.core.Jewel.Link;
import jewel.core.Jewel.RealTranslate;
import jewel.core.clfile.ClassFileConstants;
import jewel.core.clfile.ClassPath;
import jewel.core.clfile.NoClassDefFoundException;
import jewel.core.jiro.Measure;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.DominatorTree;
import jewel.core.jiro.control.LoopTree;
import jewel.core.jiro.graph.Graph;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Stack;

public final class Main implements ClassFileConstants {

  private static String output = null;
  private static String server = "localhost";
  private static String port = "31005";
  private static ClassPath classPath = new ClassPath(".");
  private static boolean norgtr = false;
  private static boolean noload = false;
  private static boolean nocntx = false;
  private static boolean nometa = false;
  private static boolean nolink = false;
  private static boolean notrlt = false;
  private static String backend = "raw";
  private static String className;

  private static PrintStream out = System.out;

  private static Jewel client = null;

  private static HashMap loadedClasses = new HashMap();
  private static HashMap loadedFlags = new HashMap();
  private static Stack loadingContext = new Stack();

  public static void main(String[] args) {

    if (args.length == 0) {
      printUsage(System.err);
      return;
    }

    int argi;
    for (argi = 0; argi < args.length; argi++) {
      String arg = args[argi];
      if (!arg.startsWith("-"))
        break;
      else if (arg.equals("-help")) {
        printUsage(System.err);
        return;
      } else if (arg.equals("-version")) {
        printVersion(System.err);
        return;
      } else if (arg.equals("-classpath")) {
        if (argi+1 == args.length) {
          System.err.println("missing parameter for option `"+arg+"'");
          printUsage(System.err);
          return;
        }
        classPath = new ClassPath(args[++argi]);
      } else if (arg.equals("-server")) {
        if (argi+1 == args.length) {
          System.err.println("missing parameter for option `"+arg+"'");
          printUsage(System.err);
          return;
        }
        server = args[++argi];
      } else if (arg.equals("-port")) {
        if (argi+1 == args.length) {
          System.err.println("missing parameter for option `"+arg+"'");
          printUsage(System.err);
          return;
        }
        port = args[++argi];
      } else if (arg.equals("-output")) {
        if (argi+1 == args.length) {
          System.err.println("missing parameter for option `"+arg+"'");
          printUsage(System.err);
          return;
        }
        output = args[++argi];
      } else if (arg.equals("-norgtr")) {
        norgtr = true;
      } else if (arg.equals("-noload")) {
        noload = true;
      } else if (arg.equals("-nocntx")) {
        nocntx = true;
      } else if (arg.equals("-nometa")) {
        nometa = true;
      } else if (arg.equals("-nolink")) {
        nolink = true;
      } else if (arg.equals("-notrlt")) {
        notrlt = true;
      } else if (arg.equals("-backend")) {
        if (argi+1 == args.length) {
          System.err.println("missing parameter for option `"+arg+"'");
          printUsage(System.err);
          return;
        }
        backend = args[++argi];
      } else {
        System.err.println("unknown option `"+arg+"'");
        printUsage(System.err);
        return;
      }
    }

    if (argi == args.length) {
      System.err.println("missing argument `class'");
      printUsage(System.err);
      return;
    }

    className = args[argi++].replace('.', '/');

    if (argi < args.length) {
      System.err.println("extra argument `"+args[argi]+"'");
      printUsage(System.err);
      return;
    }

    if (output != null)
      try {
        out = new PrintStream(new FileOutputStream(output), true);
      } catch (FileNotFoundException e) {
        System.err.println("unable to open output file `"+output+"', using standard output");
      }

    try {
      client = new Jewel();
      linkClass(className);
    } catch (RuntimeException e) {
      e.printStackTrace();
      System.err.println("fatal error , message `"+e.getMessage()+"'");
    } catch (IOException e) {
      System.err.println("fatal IO error, message `"+e.getMessage()+"'");
    }

  }

  private static ClassID linkClass(String name) throws IOException {
    ClassID classID = loadClass(name);

    Meta metaData = client.meta(classID.getName(), classID.getVersion());
    if (!nometa && name == className) {
      out.println("META ("+name+") ---------------------------------------------------");
      out.println();
      out.println("       class-id: "+classID);
      out.println();
      out.println("   access flags: 0x"+Integer.toHexString(metaData.getAccessFlags()));
      if (metaData.getFieldFlags().length == 0)
        out.println("                 <no fields>");
      for (int i = 0; i < metaData.getFieldFlags().length; i++)
        out.println("      field["+(i < 10 ? " " :"")+i+"]: 0x"+Integer.toHexString(metaData.getFieldFlags()[i])+" "+metaData.getFieldName()[i]+" "+metaData.getFieldDesc()[i]+" "+"offset "+Measure.toString(metaData.getFieldOffset()[i]));
      out.println();
      if (metaData.getMethodFlags().length == 0)
        out.println("                 <no methods>");
      for (int i = 0; i < metaData.getMethodFlags().length; i++) {
        out.println("     method["+(i < 10 ? " " :"")+i+"]: 0x"+Integer.toHexString(metaData.getMethodFlags()[i])+" "+metaData.getMethodName()[i]+metaData.getMethodDesc()[i]+" "+"index "+metaData.getMethodDispatchIndex()[i]);
        for (int j = 0; j < metaData.getMethodExceptions()[i].length; j++)
          out.println("         ex["+(j < 10 ? " " :"")+j+"]: "+metaData.getMethodExceptions()[i][j]);
      }
      out.println();
      out.println("declaring class: "+(metaData.getDeclaringClass() == null ? "<top level>" : metaData.getDeclaringClass()));
      out.println();
      if (metaData.getInnerFlags().length == 0)
        out.println("                 <no inner classes>");
      for (int i = 0; i < metaData.getInnerFlags().length; i++)
        out.println("      inner["+(i < 10 ? " " :"")+i+"]: 0x"+Integer.toHexString(metaData.getInnerFlags()[i])+" "+metaData.getInnerName()[i]);
      out.println();
      out.println("    source file: "+(metaData.getSourceFile() == null ? "<not available>" : metaData.getSourceFile()));
      out.println();
    }

    /*Context contextData = client.context(classID.getName(), classID.getVersion());
    if (!nocntx && name == className) {
      out.println("CONTEXT ("+name+") ------------------------------------------------");
      out.println();
      out.println("    class-id: "+classID);
      out.println();
      if (contextData.getClasses().length == 0)
        out.println("              <empty context>");
      for (int i = 0; i < contextData.getClasses().length; i++)
        out.println(" context["+(i < 10 ? " " :"")+i+"]: "+contextData.getClasses()[i]);
      out.println();
    }*/

    Link linkData = client.link(classID.getName(), classID.getVersion());
    if (!nolink && name == className) {
      out.println("LINK ("+name+") ---------------------------------------------------");
      out.println();
      out.println("         class-id: "+classID);
      out.println();
      if (linkData.getSymbolicLoader().length == 0)
        out.println("                   <no loading constraints>");
      for (int i = 0; i < linkData.getSymbolicLoader().length; i++)
        out.println("      loading["+(i < 10 ? " " :"")+i+"]: "+linkData.getCommonClass()[i]+" loader "+linkData.getSymbolicLoader()[i]);
      out.println();
      if (linkData.getMethodIndex().length == 0)
        out.println("                   <no implementation patches>");
      for (int i = 0; i < linkData.getMethodIndex().length; i++)
        out.println("      dynamic["+(i < 10 ? " " :"")+i+"]: "+linkData.getMethodIndex()[i]+" implements "+linkData.getImplementationIndex()[i]);
      out.println();
      out.println("        finalizes: "+linkData.getFinalizes());
      out.println();
    }

    if (!notrlt && name == className) {
      out.println("TRANSLATE ("+name+") ----------------------------------------------");
      out.println();
      out.println("   class-id: "+classID);
      out.println();
      if (linkData.getPatches().length == 0) {
        out.println("               <no text objects>");
        out.println();
      }
      for (int i = 0; i < linkData.getPatches().length; i++) {
        out.println("   text["+(i < 10 ? " " :"")+i+"]:");

        Context contextData = client.context(classID.getName(), classID.getVersion(), i);

        String[] cname = new String[contextData.getClasses().length];
        long[] cversion = new long[contextData.getClasses().length];
        boolean[] cinitialized = new boolean[contextData.getClasses().length];
        int[] cdepth = new int[contextData.getClasses().length];
        for (int j = 0; j < cname.length; j++) {
          ClassID contextID = loadClass(contextData.getClasses()[j]);
          cname[j] = contextID.getName();
          cversion[j] = contextID.getVersion();
          cinitialized[j] = false;
          cdepth[j] = contextID.getDepth();
        }

        RealTranslate rtransData = client.realTranslate(classID.getName(), classID.getVersion(), i, cname, cversion, cinitialized, cdepth, 0, backend);

        if (rtransData.getSourceClass().length == 0)
          out.println("                   <no verify constraints>");
        for (int j = 0; j < rtransData.getSourceClass().length; j++) {
          out.println("verify target["+(j < 10 ? " " :"")+j+"]: "+rtransData.getTargetClass()[j]);
          for (int k = 0; k < rtransData.getSourceClass()[j].length; k++)
            out.println("       source["+(k < 10 ? " " :"")+k+"]: "+rtransData.getSourceClass()[j][k]);
        }
        out.println();

        Object text = rtransData.getObject();

        //review
        if (text instanceof jewel.core.jiro.IRCFG)
          ((jewel.core.jiro.IRCFG)text).printTo(out);
        if (text instanceof jewel.core.bend.CodeOutput)
          ((jewel.core.bend.CodeOutput)text).printTo(out);
        //review

        if (backend.equals("raw")) {
          ControlFlowGraph cfg = (ControlFlowGraph)text;
//          Graph.show(new Graph[]{ cfg, cfg.dominatorTree(), cfg.loopTree() });
        }

        if (linkData.getPatches()[i].length == 0)
          out.println("             <no patches>");
        for (int j = 0; j < linkData.getPatches()[i].length; j++)
          out.println("  patch["+(j < 10 ? " " :"")+j+"]: "+linkData.getPatches()[i][j]);

        out.println();
      }
    }

    return classID;
  }

  private static ClassID loadClass(String name) throws IOException {
    ClassID classID = (ClassID)loadedClasses.get(name);
    if (classID == null) {
      if (loadingContext.contains(name))
        throw new RuntimeException("ClassCirtularityError "+name);
      loadingContext.push(name);
      char accessFlags;
      try {
        byte[] image;

        int dims = name.lastIndexOf('[')+1;
        if (dims == 0) {
          try {
            image = classPath.findDefinition(name);
          } catch (NoClassDefFoundException e) {
            throw new RuntimeException("Could not find definition for "+name);
          }
        } else {
          boolean publicFlag = true;
          if (name.charAt(dims) == 'L') {
            String elementName = name.substring(dims+1,name.length()-1);
            loadClass(elementName); 
            if ((((Character)loadedFlags.get(elementName)).charValue() & ACC_PUBLIC) == 0)
              publicFlag = false;
          }
          image = createArrayImage(name, publicFlag);
        }

        Register registerData = client.register(name, image, 0, image.length);
        if (!norgtr && name == className) {
          out.println("REGISTER ("+name+") -----------------------------------------------");
          out.println();
          out.println("      class-id: "+new ClassID(name, registerData.getVersion()));
          out.println();
          out.println("  access flags: 0x"+Integer.toHexString(registerData.getAccessFlags()));
          out.println("    superclass: "+(registerData.getSuperclass() == null ? "<none>" : registerData.getSuperclass()));
          if (registerData.getInterfaces().length == 0)
            out.println("                <no interfaces>");
          for (int i = 0; i < registerData.getInterfaces().length; i++)
            out.println(" interface["+(i < 10 ? " " :"")+i+"]: "+registerData.getInterfaces()[i]);
          out.println();
        }

        ClassID superID = registerData.getSuperclass() == null ? null : loadClass(registerData.getSuperclass());
        String sname = null;
        long sversion = 0;
        if (superID != null) {
          sname = superID.getName();
          sversion = superID.getVersion();
        }
        String[] iname = new String[registerData.getInterfaces().length];
        long[] iversion = new long[registerData.getInterfaces().length];
        boolean[] ifaceSame = new boolean[registerData.getInterfaces().length];
        for (int i = 0; i < registerData.getInterfaces().length; i++) {
          ClassID ifaceID = loadClass(registerData.getInterfaces()[i]);
          iname[i] = ifaceID.getName();
          iversion[i] = ifaceID.getVersion();
          ifaceSame[i] = true;
        }

        Load loadData = client.load(name, registerData.getVersion(), sname, sversion, superID == null ? -1 : superID.getDepth(), iname, iversion, ifaceSame);
        if (!noload && name == className) {
          out.println("LOAD ("+name+") ---------------------------------------------------");
          out.println();
          out.println("         class-id: "+new ClassID(name, loadData.getVersion()));
          out.println();
          out.println("    static fields: size "+Measure.toString(loadData.getStaticSize())+" refs "+loadData.getStaticRefsCount()+" offset "+Measure.toString(loadData.getStaticRefsOffset()));
          out.println("  instance fields: size "+Measure.toString(loadData.getInstanceSize())+" refs "+loadData.getInstanceRefsCount()+" offset "+Measure.toString(loadData.getInstanceRefsOffset()));
          out.println("   static methods: table length ("+loadData.getNonOverridableDispatchEntryCount()+")");
          out.println(" instance methods: table length ("+loadData.getOverridableDispatchEntryCount()+")");
          for (int i = 0; i < loadData.getInterfaceOffsets().length; i++)
            out.println("    interface["+(i < 10 ? " " :"")+i+"]: offset "+loadData.getInterfaceOffsets()[i]);
          out.println("   native methods: pointers ("+(int)loadData.getNativeSlots()+")");
          out.println();
        }

        accessFlags = (char)registerData.getAccessFlags();

        classID = new ClassID(name, loadData.getVersion(), superID == null ? 0 : superID.getDepth()+1);
      } finally {
        loadingContext.pop();
      }
      loadedClasses.put(name, classID);
      loadedFlags.put(name, new Character(accessFlags));
    }
    return classID;
  }

  private static byte[] createArrayImage(String name, boolean publicFlag) {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bout);
    try {
      out.writeInt(MAGIC);
      out.writeChar(DEFAULT_MINOR_VERSION);
      out.writeChar(DEFAULT_MAJOR_VERSION);

      out.writeChar(9);
      out.writeByte(CONSTANT_CLASS);
      out.writeChar(5);
      out.writeByte(CONSTANT_CLASS);
      out.writeChar(6);
      out.writeByte(CONSTANT_CLASS);
      out.writeChar(7);
      out.writeByte(CONSTANT_CLASS);
      out.writeChar(8);
      out.writeByte(CONSTANT_UTF8);
      out.writeUTF(name);
      out.writeByte(CONSTANT_UTF8);
      out.writeUTF("java/lang/Object");
      out.writeByte(CONSTANT_UTF8);
      out.writeUTF("java/lang/Cloneable");
      out.writeByte(CONSTANT_UTF8);
      out.writeUTF("java/io/Serializable");

      char accessFlags = ACC_FINAL|ACC_ABSTRACT|ACC_SUPER;
      if (publicFlag)
        accessFlags |= ACC_PUBLIC;
      out.writeChar(accessFlags);
      out.writeChar(1);
      out.writeChar(2);

      out.writeChar(2);
      out.writeChar(3);
      out.writeChar(4);

      out.writeChar(0);
      out.writeChar(0);
      out.writeChar(0);

      out.flush();
    } catch (IOException e) {
      throw new Error("Should never occur");
    }
    return bout.toByteArray();
  }

  private static void printUsage(PrintStream out) {
    out.println("usage: jewelq [-options] class");
    out.println("options:");
    out.println("\t-help\t\t\tprint this message");
    out.println("\t-version\t\tprint version number");
    out.println("\t-classpath <path>\tspecify classpath");
    out.println("\t-server <hostname>\tspecify compilation server");
    out.println("\t-port <number>\t\tspecify service port");
    out.println("\t-output <file>\t\tspecify where to write data");
    out.println("\t-norgtr\t\t\tsupress REGISTER data");
    out.println("\t-noload\t\t\tsupress LOAD data");
    out.println("\t-nocntx\t\t\tsupress CONTEXT data");
    out.println("\t-nometa\t\t\tsupress META data");
    out.println("\t-nolink\t\t\tsupress LINK data");
    out.println("\t-notrlt\t\t\tsupress TRANSLATE data");
    out.println("\t-backend <name>\t\tspecify TRANSLATE backend");
  }

  private static void printVersion(PrintStream out) {
    out.println("jewelq version 0.95a");
  }

  private Main() { }

  private static final class ClassID {
  
    private final String name;
    private final long version;
    private int depth;
  
    public ClassID(String name, long version) {
      if (name == null)
        throw new NullPointerException();
      this.name = name;
      this.version = version;
      depth = -1;
    }
  
    public ClassID(String name, long version, int depth) {
      if (name == null)
        throw new NullPointerException();
      if (depth < 0)
        throw new IllegalArgumentException();
      this.name = name;
      this.version = version;
      this.depth = depth;
    }
  
    public final String getName() {
      return name;
    }
  
    public final long getVersion() {
      return version;
    }

    public final int getDepth() {
      if (depth == -1)
        throw new IllegalStateException();
      return depth;
    }
  
    public final int hashCode() {
      return (int)version ^ (int)(version >> 32);
    }
  
    public final boolean equals(Object object) {
      return object instanceof ClassID
          && version == ((ClassID)object).version
          && name.equals(((ClassID)object).name);
    }
  
    public final String toString() {
      return "<"+name+"#"+Long.toHexString(version)+">";
    }
  
  }

}

