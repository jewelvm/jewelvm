/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jewelg;

import jewel.core.bend.CodeOutput;
import jewel.core.bend.Extern;
import jewel.core.bend.MethodEntry;
import jewel.core.bend.Relocatable;
import jewel.core.bend.RelocatableFactory;
import jewel.core.bend.StringRef;
import jewel.core.bend.Symbol;
import jewel.core.bend.SymbolicType;
import jewel.core.clfile.LinkageException;
import jewel.core.jiro.Measure;
import jewel.tools.jewelg.Machine.HCharArray;
import jewel.tools.jewelg.Machine.HClass;
import jewel.tools.jewelg.Machine.HMethodText;
import jewel.tools.jewelg.Machine.HObject;
import jewel.tools.jewelg.Machine.HOrdinaryObject;
import jewel.tools.jewelg.Machine.FieldRecord;
import jewel.tools.jewelg.Machine.InnerRecord;
import jewel.tools.jewelg.Machine.MetaInfo;
import jewel.tools.jewelg.Machine.MethodRecord;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public final class Main {

  private static String classpath = ".";
  private static String output = null;
  private static String backend = "i386";
  private static boolean assembly = false;
  private static boolean verbose = false;

  private static Machine machine;
  private static final HashMap exported = new HashMap();
  private static final HashMap onSubLink = new HashMap();

  public static void main(String[] args) throws LinkageException, IllegalAccessException {
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
      if (arg.equals("-classpath")) {
        if (argi+1 == args.length) {
          printMessage(System.err, "missing parameter for option `"+arg+"'");
          return;
        }
        classpath = args[++argi];
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
      if (arg.equals("-backend")) {
        if (argi+1 == args.length) {
          printMessage(System.err, "missing parameter for option `"+arg+"'");
          return;
        }
        backend = args[++argi];
        continue;
      }
      if (arg.equals("-assembly")) {
        assembly = true;
        continue;
      }
      if (arg.equals("-verbose")) {
        verbose = true;
        continue;
      }
      printMessage(System.err, "unknown option `"+arg+"'");
      return;
    }

    if (argi == args.length) {
      printUsage(System.err);
      return;
    }

    OutputStream out = System.out;
    if (output != null)
      try {
        out = new BufferedOutputStream(new FileOutputStream(output));
      } catch (IOException e) {
        printMessage(System.err, "i/o error opening output file `"+output+"'");
        return;
      }

    Properties config = new Properties();
    for (int i = argi; i < args.length; i++) {
      String arg = args[i];
      try {
        config.load(new FileInputStream(arg));
      } catch (IOException e) {
        printMessage(System.err, "i/o error processing config file `"+arg+"'");
        return;
      }
    }

    machine = new Machine(classpath);

    init(System.err, config);

    try {
      ImageOutputStream ios;
      if (assembly)
        ios = new AssemblyImageOutputStream(out, false);
      else
        ios = new BinaryImageOutputStream(out, true);
      emit(System.err, ios);
      ios.close();
    } catch (IOException e) {
      printMessage(System.err, "i/o errors occurred writing output file `"+output+"'");
    }

  }

  private static void printUsage(PrintStream err) {
    err.println("usage: jewelg [-options] configs...");
    err.println("options:");
    err.println("\t-help\t\t\tprint this message");
    err.println("\t-version\t\tprint version number");
    err.println("\t-classpath <path>\tset application classpath");
    err.println("\t-backend <arch>\t\tspecify target backend");
    err.println("\t-assembly\t\toutput assembly source");
    err.println("\t-o <output>\t\tspecify output file");
    err.println("\t-verbose\t\tenable verbose output");
  }

  private static void printVersion(PrintStream err) {
    err.println("jewelg version 0.95a");
  }

  private static void printMessage(PrintStream err, String message) {
    err.println("jewelg: "+message);
  }

  private static void init(PrintStream err, Properties config) throws LinkageException, IllegalAccessException {
    Set entries = config.entrySet();
    for (Iterator i = entries.iterator(); i.hasNext(); ) {
      Entry entry = (Entry)i.next();

      String exprt = "";
      String cname = (String)entry.getKey();
      String signs = (String)entry.getValue();

      cname = cname.trim();
      signs = signs.trim();

      if (cname.startsWith("\"") && cname.endsWith("\"")) {
        exprt = cname.substring(1, cname.length()-1);
        exprt = exprt.trim();
        cname = signs;
        signs = "";
      }

      HClass clazz = cname.startsWith("<")
                   ? machine.loadPrimitiveClass(cname)
                   : (HClass)machine.loadClass(cname);

      if (exprt.length() > 0)
        exported.put(clazz, exprt);

      StringTokenizer st = new StringTokenizer(signs);
      while (st.hasMoreTokens()) {
        String sign = st.nextToken();
        sign = sign.trim();

        String name = sign;
        String descriptor = "";

        int index = sign.indexOf('(');
        if (index != -1) {
          name = sign.substring(0, index);
          descriptor = sign.substring(index);
        }

        schedule(err, clazz);

        HMethodText methodText = null;
        clazz.loadMetadata();
        MetaInfo metaInfo = clazz.getMetaData();
        for (int j = 0; j < metaInfo.getMethodCount(); j++) {
          MethodRecord methodRecord = metaInfo.getMethodAt(j);
          if (name.equals(methodRecord.getName()) && descriptor.equals(methodRecord.getDescriptor())) {
            methodText = clazz.getMethodText(methodRecord.getDispatchIndex());
            break;
          }
        }
        if (methodText == null)
          throw new NoSuchMethodError(clazz.getName()+"."+name+descriptor);

        schedule(err, methodText);
      }

    }
  }

  private static void schedule(PrintStream err, HClass clazz) throws LinkageException, IllegalAccessException {
    if (!clazz.isLinked()) {
      clazz.link();
      update(err, clazz);
    }
    for (HClass superclass = clazz; superclass != null; superclass = superclass.getHSuperclass()) {
      HMethodText initText = superclass.getMethodText(superclass.getDynamicEntries());
      if (initText != null)
        schedule(err, initText);
    }
  }

  private static void update(PrintStream err, HClass clazz) throws LinkageException, IllegalAccessException {
    HClass superclass = clazz.getHSuperclass();
    if (superclass != null)
      update(err, superclass);
    for (int i = 0; i < clazz.getInterfaceCount(); i++)
      update(err, clazz.getHInterface(i));

    if (!clazz.isAbstract()) {
      for (HClass superClass = clazz; superClass != null; superClass = superClass.getHSuperclass()) {
        ArrayList list = (ArrayList)onSubLink.get(superClass);
        if (list != null)
          for (int i = 0; i < list.size(); i++) {
            mid mid = (mid)list.get(i);
            if (valid(clazz, mid.dindex))
              schedule(err, clazz.getMethodText(mid.dindex));
          }
      }
      for (int j = 0; j < clazz.getTotalInterfaceCount(); j++) {
        HClass interFace = clazz.getHInterface(j);
        ArrayList list = (ArrayList)onSubLink.get(interFace);
        if (list != null)
          for (int i = 0; i < list.size(); i++) {
            mid mid = (mid)list.get(i);
            if (mid.absolute) {
              if (valid(clazz, mid.dindex))
                schedule(err, clazz.getMethodText(mid.dindex));
            } else {
              if (!clazz.isSubtypeOf(mid.clazz))
                continue;
              schedule(err, clazz.getMethodText(clazz.getInterfaceBase(j)+mid.dindex));
            }
          }
      }
    }
  }

  private static void schedule(final PrintStream err, HMethodText methodText) throws LinkageException, IllegalAccessException {
    if (methodText.isLazy()) {
      methodText.lazyResolve();
      HClass declaringClass = methodText.getDeclaringClass();
      int index = methodText.getIndex();
      declaringClass.loadMetadata();
      MetaInfo metaInfo = declaringClass.getMetaData();
      MethodRecord methodRecord = metaInfo.getMethodAt(index);
      if (verbose)
        err.println("Scheduling: "+declaringClass.getName()+"."+methodRecord.getName()+methodRecord.getDescriptor());
      Closure closure = new Closure(methodText.getCFG());
      closure.exec(
        new Actions() {
          public void init(String name) throws LinkageException, IllegalAccessException {
            schedule(err, (HClass)machine.loadClass(name));
          }
          public void call(String name, int dindex) throws LinkageException, IllegalAccessException {
            HClass clazz = (HClass)machine.loadClass(name);
            schedule(err, clazz);
            schedule(err, clazz.getMethodText(dindex));
          }
          public void vcall(String name, int dindex) throws LinkageException, IllegalAccessException {
            HClass clazz = (HClass)machine.loadClass(name);
            ArrayList list = (ArrayList)onSubLink.get(clazz);
            if (list == null) {
              list = new ArrayList();
              onSubLink.put(clazz, list);
            }
            list.add(new mid(clazz.isInterface(), null, dindex));
            for (Iterator i = machine.heapObjects(); i.hasNext(); ) {
              HObject object = (HObject)i.next();
              if (object instanceof HClass) {
                HClass subclass = (HClass)object;
                if (subclass.isLinked())
                  if (!subclass.isAbstract())
                    if (subclass.isSubtypeOf(clazz))
                      if (valid(subclass, dindex))
                        schedule(err, subclass.getMethodText(dindex));
              }
            }
          }
          public void icall(String name, String iname, int dindex) throws LinkageException, IllegalAccessException {
            HClass clazz = (HClass)machine.loadClass(name);
            HClass iclazz = (HClass)machine.loadClass(iname);
            ArrayList list = (ArrayList)onSubLink.get(iclazz);
            if (list == null) {
              list = new ArrayList();
              onSubLink.put(iclazz, list);
            }
            list.add(new mid(false, clazz, dindex));
            for (Iterator i = machine.heapObjects(); i.hasNext(); ) {
              HObject object = (HObject)i.next();
              if (object instanceof HClass) {
                HClass subclass = (HClass)object;
                if (subclass.isLinked())
                  if (!subclass.isAbstract())
                    if (subclass.isSubtypeOf(clazz) && subclass.isSubtypeOf(iclazz))
                      schedule(err, subclass.getMethodText(subclass.getInterfaceBase(iclazz)+dindex));
              }
            }
          }
          public void meta(String name) throws LinkageException, IllegalAccessException {
            HClass clazz = (HClass)machine.loadClass(name);
            clazz.loadMetadata();
          }
        }
      );
    }
  }

  private static boolean valid(HClass clazz, int index) {
    if (index >= clazz.getDynamicEntries())
      return false;
    for (int i = 0; i < clazz.getTotalInterfaceCount(); i++) {
      int base = clazz.getInterfaceBase(i);
      HClass iface = clazz.getHInterface(i);
      if (base <= index && index < base+iface.getDynamicEntries())
        return false;
    }
    return true;
  }

  private static class mid {
    public final boolean absolute;
    public final HClass clazz;
    public final int dindex;
    public mid(boolean absolute, HClass clazz, int dindex) {
      this.absolute = absolute;
      this.clazz = clazz;
      this.dindex = dindex;
    }
  }

  private static void emit(PrintStream err, ImageOutputStream out) throws LinkageException, IllegalAccessException, IOException {
    out.extern("_athrow_");
    out.extern("_ldiv_");
    out.extern("_lrem_");
    out.extern("_init_");
    out.extern("_newinstance_");
    out.extern("_newarray_");
    out.extern("_lock_");
    out.extern("_unlock_");
    out.extern("_islocked_");
    out.extern("_subtypeof_");
    out.extern("_comptypeof_");
    out.extern("_imlookup_");
    out.extern("_lazy_");
    out.extern("_ncalll_");
    out.extern("_ncallz_");
    out.extern("_ncallb_");
    out.extern("_ncallc_");
    out.extern("_ncalls_");
    out.extern("_ncalli_");
    out.extern("_ncallj_");
    out.extern("_ncallf_");
    out.extern("_ncalld_");
    out.extern("_ncallv_");

    out.label("heap_top");

    if (verbose) {
      err.print("[0%");
      err.flush();
    }

    int total = 1;
    int shown = 1;
    for (Iterator i = machine.heapObjects(); i.hasNext(); total++) {
      HObject object = (HObject)i.next();
      emitObject(out, object);

      if (verbose) {
        int limit = (50*total)/machine.heapSize();
        while (shown <= limit) {
          if (shown % 5 != 0)
            err.print("=");
          else {
            err.print(2*shown);
            err.print("%");
          }
          err.flush();
          shown++;
        }
      }
    }

    if (verbose)
      err.println("]");

    out.writeWord(2);
    out.writeWord(0);
    out.globl("HMainBlock");
    out.label("HMainBlock");
    out.writeWord("heap_bottom", "heap_top");
    out.label("heap_bottom");
  }

  private static void emitObject(ImageOutputStream out, HObject object) throws LinkageException, IllegalAccessException, IOException {
    HClass myclazz = object.getHClass();

    if (object instanceof HOrdinaryObject)
      out.writeWord(myclazz.finalizes() ? 0 : 2);
    if (object instanceof HCharArray) {
      HCharArray array = (HCharArray)object;
      if (out.wordSize() > 4)
        out.space(out.wordSize()-4);
      out.writeInt(array.length);
    }
    if (object instanceof HMethodText)
      out.writeWord("H"+machine.addressOf(object)+"_tail");
    if (object instanceof HClass)
      out.writeWord("H"+machine.addressOf(object)+"_tail");

    out.writeWord("H"+machine.addressOf(myclazz));

    if (object instanceof HClass) {
      String external = (String)exported.get(object);
      if (external != null) {
        out.globl(external);
        out.label(external);
      }
    }

    out.label("H"+machine.addressOf(object));
    int instanceSize = Measure.getScaled(myclazz.getInstanceSize(), out.wordSize());
    if (instanceSize > 0) {
      out.space(instanceSize);
      out.align();
    }

    if (object instanceof HCharArray) {
      HCharArray array = (HCharArray)object;
      for (int i = 0; i < array.length; i++)
        out.writeShort(array.get(i));
      out.align();
      out.writeWord(myclazz.finalizes() ? 0 : 2);
    }

    if (object instanceof HMethodText) {
      HMethodText methodText = (HMethodText)object;
      out.label("H"+machine.addressOf(methodText)+"_entry");
      emitMethodText(out, methodText);
      out.align();
      out.label("H"+machine.addressOf(methodText)+"_tail");
      out.writeWord(myclazz.finalizes() ? 0 : 2);
    }

    if (object instanceof HClass) {
      HClass clazz = (HClass)object;
      if (clazz.isLinked()) {
        for (int i = 0; i < clazz.getDynamicEntries(); i++) {
          HMethodText dynamicText = clazz.getMethodText(i);
          out.writeWord("H"+machine.addressOf(dynamicText));
        }
        HMethodText clinitText = clazz.getMethodText(clazz.getDynamicEntries());
        if (clinitText == null)
          out.writeWord(0);
        else
          out.writeWord("H"+machine.addressOf(clinitText));
        for (int i = 1; i < clazz.getStaticEntries(); i++) {
          HMethodText staticText = clazz.getMethodText(clazz.getDynamicEntries()+i);
          out.writeWord("H"+machine.addressOf(staticText));
        }
      } else {
        int entries = clazz.getDynamicEntries()+clazz.getStaticEntries();
        for (int i = 0; i < entries; i++)
          out.writeWord(0);
      }

      int staticSize = Measure.getScaled(clazz.getStaticSize(), out.wordSize());
      if (staticSize > 0) {
        out.space(staticSize);
        out.align();
      }

      emitMeta(out, clazz);

      out.label("H"+machine.addressOf(clazz)+"_name");
      out.writeUTF(clazz.getName());

      out.align();

      int nativeCount = clazz.getNativeMethodCount();
      if (nativeCount != 0) {
        out.label("H"+machine.addressOf(clazz)+"_natives");
        for (int i = 0; i < nativeCount; i++)
          out.writeWord(0);
      }

      int interfaceCount = clazz.getTotalInterfaceCount();
      if (interfaceCount > 0) {
        out.label("H"+machine.addressOf(clazz)+"_interfaces");
        for (int i = 0; i < interfaceCount; i++) {
          out.writeWord("H"+machine.addressOf(clazz.getHInterface(i)));
          out.writeInt(clazz.getInterfaceBase(i));
        }
        out.writeWord(0);
      }

      out.writeWord("H"+machine.addressOf(clazz)+"_name");
      out.writeLong(clazz.getVersion());
      out.writeWord(0); // loader
      HClass elementClass = clazz.getElementClass();
      if (elementClass == null)
        out.writeWord(0);
      else
        out.writeWord("H"+machine.addressOf(elementClass));

      out.writeShort(clazz.getAccessFlags());
      out.writeShort(clazz.getInterfaceCount());
      out.writeByte(clazz.getDimensions());
      if (clazz.getDimensions() == 0)
        out.writeByte(0);
      else
        switch (clazz.getName().charAt(1)) {
        case 'B': case 'Z': out.writeByte(1); break;
        case 'S': case 'C': out.writeByte(2); break;
        case 'F': case 'I': out.writeByte(4); break;
        case 'L': case '[': out.writeByte(out.wordSize()); break;
        case 'J': case 'D': out.writeByte(8); break;
        }

      byte gcflags = 0;
      int referentOfs = 0;
      HClass referenceClass = (HClass)machine.loadClass("java/lang/ref/Reference");
      if (clazz.isSubtypeOf(referenceClass)) {
        boolean hasReferent = false;
        MetaInfo rmInfo = referenceClass.getMetaData();
        if (rmInfo == null)
          throw new Error("Please, request metadata in configuration for class "+referenceClass.getName());
        for (int i = 0; i < rmInfo.getFieldCount(); i++) {
          FieldRecord field = rmInfo.getFieldAt(i);
          if (!field.isStatic())
            if (field.getName().equals("referent") && field.getDescriptor().equals("Ljava/lang/Object;")) {
              referentOfs = Measure.getScaled(field.getOffset(), out.wordSize());
              hasReferent = true;
            }
        }
        if (hasReferent)
          if (clazz.isSubtypeOf((HClass)machine.loadClass("java/lang/ref/SoftReference")))
            gcflags = 0x70;
          else if (clazz.isSubtypeOf((HClass)machine.loadClass("java/lang/ref/WeakReference")))
            gcflags = 0x30;
          else if (clazz.isSubtypeOf((HClass)machine.loadClass("java/lang/ref/PhantomReference")))
            gcflags = 0x10;
      }
      if (!clazz.finalizes())
        gcflags |= 0x02;
      out.writeByte(gcflags);

      out.writeByte(clazz.getStatus());
      out.writeWord(0); // initthread

      out.writeInt(referentOfs);
      out.writeInt(Measure.getScaled(clazz.getStaticSize(), out.wordSize()));
      out.writeInt(Measure.getScaled(clazz.getInstanceSize(), out.wordSize()));
      out.writeInt(Measure.getScaled(clazz.getStaticRefOfs(), out.wordSize()));
      out.writeInt(Measure.getScaled(clazz.getInstanceRefOfs(), out.wordSize()));
      out.writeShort(clazz.getStaticRefCount());
      out.writeShort(clazz.getInstanceRefCount());
      out.writeInt(clazz.getStaticEntries());
      out.writeInt(clazz.getDynamicEntries());

      if (clazz.getMetaData() == null)
        out.writeWord(0);
      else
        out.writeWord("H"+machine.addressOf(clazz)+"_reflect");

      if (clazz.getNativeMethodCount() == 0)
        out.writeWord(0);
      else
        out.writeWord("H"+machine.addressOf(clazz)+"_natives");

      if (clazz.getHSuperclass() == null)
        out.writeWord(0);
      else
        out.writeWord("H"+machine.addressOf(clazz.getHSuperclass()));
      if (clazz.getTotalInterfaceCount() == 0)
        out.writeWord(0);
      else
        out.writeWord("H"+machine.addressOf(clazz)+"_interfaces");

      out.label("H"+machine.addressOf(clazz)+"_tail");
      out.writeWord(myclazz.finalizes() ? 0 : 2);
    }

  }

  private static void emitMeta(ImageOutputStream out, HClass clazz) throws IOException {
    MetaInfo metaInfo = clazz.getMetaData();
    if (metaInfo != null) {

      out.label("H"+machine.addressOf(clazz)+"_reflect");
      out.writeShort(metaInfo.getSourceFlags());
      if (metaInfo.getFieldCount() == 0)
        out.writeWord(0);
      else
        out.writeWord("H"+machine.addressOf(clazz)+"_fields");
      if (metaInfo.getMethodCount() == 0)
        out.writeWord(0);
      else
        out.writeWord("H"+machine.addressOf(clazz)+"_methods");
      if (metaInfo.getDeclaringClass() == null)
        out.writeWord(0);
      else
        out.writeWord("H"+machine.addressOf(clazz)+"_declaring");
      if (metaInfo.getInnerCount() == 0)
        out.writeWord(0);
      else
        out.writeWord("H"+machine.addressOf(clazz)+"_innerclasses");
      if (metaInfo.getSourceFile() == null)
        out.writeWord(0);
      else
        out.writeWord("H"+machine.addressOf(clazz)+"_sourcefile");

      if (metaInfo.getFieldCount() > 0) {
        out.label("H"+machine.addressOf(clazz)+"_fields");
        for (int i = 0; i < metaInfo.getFieldCount(); i++) {
          FieldRecord field = (FieldRecord)metaInfo.getFieldAt(i);
          out.writeWord("H"+machine.addressOf(clazz));
          out.writeWord("H"+machine.addressOf(clazz)+"_field_"+i+"_name");
          out.writeWord("H"+machine.addressOf(clazz)+"_field_"+i+"_descriptor");
          out.writeInt(Measure.getScaled(field.getOffset(), out.wordSize()));
          out.writeShort(field.getAccessFlags());
        }
        out.writeWord(0);
      }

      if (metaInfo.getMethodCount() > 0) {
        out.label("H"+machine.addressOf(clazz)+"_methods");
        for (int i = 0; i < metaInfo.getMethodCount(); i++) {
          MethodRecord method = (MethodRecord)metaInfo.getMethodAt(i);
          out.writeWord("H"+machine.addressOf(clazz));
          out.writeWord("H"+machine.addressOf(clazz)+"_method_"+i+"_name");
          out.writeWord("H"+machine.addressOf(clazz)+"_method_"+i+"_descriptor");
          if (method.getExceptionCount() == 0)
            out.writeWord(0);
          else
            out.writeWord("H"+machine.addressOf(clazz)+"_method_"+i+"_exceptions");
          out.writeInt(method.getDispatchIndex());
          out.writeShort(method.getAccessFlags());
        }
        out.writeWord(0);

        for (int i = 0; i < metaInfo.getMethodCount(); i++) {
          MethodRecord method = (MethodRecord)metaInfo.getMethodAt(i);
          if (method.getExceptionCount() > 0) {
            out.label("H"+machine.addressOf(clazz)+"_method_"+i+"_exceptions");
            for (int j = 0; j < method.getExceptionCount(); j++)
              out.writeWord("H"+machine.addressOf(clazz)+"_method_"+i+"_exception_"+j);
            out.writeWord(0);
          }
        }
      }

      if (metaInfo.getInnerCount() > 0) {
        out.label("H"+machine.addressOf(clazz)+"_innerclasses");
        for (int i = 0; i < metaInfo.getInnerCount(); i++) {
          InnerRecord inner = (InnerRecord)metaInfo.getInnerAt(i);
          out.writeWord("H"+machine.addressOf(clazz)+"_innerclass_"+i+"_name");
          out.writeShort(inner.getAccessFlags());
        }
        out.writeWord(0);
      }

      for (int i = 0; i < metaInfo.getFieldCount(); i++) {
        FieldRecord field = (FieldRecord)metaInfo.getFieldAt(i);
        out.label("H"+machine.addressOf(clazz)+"_field_"+i+"_name");
        out.writeUTF(field.getName());
        out.label("H"+machine.addressOf(clazz)+"_field_"+i+"_descriptor");
        out.writeUTF(field.getDescriptor());
      }

      for (int i = 0; i < metaInfo.getMethodCount(); i++) {
        MethodRecord method = (MethodRecord)metaInfo.getMethodAt(i);
        out.label("H"+machine.addressOf(clazz)+"_method_"+i+"_name");
        out.writeUTF(method.getName());
        out.label("H"+machine.addressOf(clazz)+"_method_"+i+"_descriptor");
        out.writeUTF(method.getDescriptor());
        for (int j = 0; j < method.getExceptionCount(); j++) {
          out.label("H"+machine.addressOf(clazz)+"_method_"+i+"_exception_"+j);
          out.writeUTF(method.getExceptionAt(j));
        }
      }

      if (metaInfo.getDeclaringClass() != null) {
        out.label("H"+machine.addressOf(clazz)+"_declaring");
        out.writeUTF(metaInfo.getDeclaringClass());
      }

      for (int i = 0; i < metaInfo.getInnerCount(); i++) {
        InnerRecord inner = (InnerRecord)metaInfo.getInnerAt(i);
        out.label("H"+machine.addressOf(clazz)+"_innerclass_"+i+"_name");
        out.writeUTF(inner.getName());
      }

      if (metaInfo.getSourceFile() != null) {
        out.label("H"+machine.addressOf(clazz)+"_sourcefile");
        out.writeUTF(metaInfo.getSourceFile());
      }

    }
  }

  private static void emitMethodText(ImageOutputStream out, final HMethodText text) throws LinkageException, IllegalAccessException, IOException {
    CodeOutput code = text.getCodeOutput(backend);
    RelocObj relocObj = (RelocObj)code.prepareImage(new RelocatableFactory() {
      public Relocatable createRelocatable(byte[] array, int start, int length) {
        return new RelocObj(text, array, start, length);
      }
    });
    relocObj.writeTo(out);
  }

  private Main() { }

  private static final class RelocObj extends Relocatable {
  
    private final HMethodText text;
    private final byte[] code;
    private final boolean[] posReloc;
    private final boolean[] negReloc;
    private final String[] extern;
    private final String[] stringRef;
    private final String[] symbolicType;
    private final String[] methodType;
    private final int[] methodIndex;
  
    public RelocObj(HMethodText text, byte[] array, int start, int length) {
      this.text = text;
      code = new byte[length];
      posReloc = new boolean[length];
      negReloc = new boolean[length];
      extern = new String[length];
      stringRef = new String[length];
      symbolicType = new String[length];
      methodType = new String[length];
      methodIndex = new int[length];
      System.arraycopy(array, start, code, 0, length);
    }
  
    protected void posReloc(int offset) {
      posReloc[offset] = true;
    }
  
    protected void negReloc(int offset) {
      negReloc[offset] = true;
    }
  
    protected void posPatch(int offset, Symbol symbol) {
      if (symbol instanceof Extern) {
        extern[offset] = ((Extern)symbol).getName();
        return;
      }
      if (symbol instanceof StringRef) {
        stringRef[offset] = ((StringRef)symbol).getValue();
        return;
      }
      if (symbol instanceof SymbolicType) {
        symbolicType[offset] = ((SymbolicType)symbol).getName();
        return;
      }
      if (symbol instanceof MethodEntry) {
        methodType[offset] = ((MethodEntry)symbol).getName();
        methodIndex[offset] = ((MethodEntry)symbol).getIndex();
        return;
      }
      throw new IllegalArgumentException("Unsupported symbol");
    }
  
    protected void negPatch(int offset, Symbol symbol) {
      throw new UnsupportedOperationException();
    }
  
    public void writeTo(ImageOutputStream out) throws LinkageException, IllegalAccessException, IOException {
      for (int i = 0; i < code.length; i++) {
  
        int flags = 0;
  
        boolean pos;
        boolean neg;
        String ext;
        String sym;
        String str;
        String mtd;
        int idx;
  
        if (pos = posReloc[i])
          flags |= 1;
        if (neg = negReloc[i])
          flags |= 2;
        if ((ext = extern[i]) != null)
          flags |= 4;
        if ((sym = symbolicType[i]) != null)
          flags |= 8;
        if ((str = stringRef[i]) != null)
          flags |= 16;
        if ((mtd = methodType[i]) != null)
          flags |= 32;
        idx = methodIndex[i];
  
        if (flags == 0)
          out.writeByte(code[i] & 0xFF);
        else {
          //not portable
          int offset = (code[i  ] & 0xFF)
                     | (code[i+1] & 0xFF) <<  8
                     | (code[i+2] & 0xFF) << 16
                     | (code[i+3] & 0xFF) << 24;
          switch (flags) {
          case 1:
            out.writeWord("H"+machine.addressOf(text)+"_entry", offset);
            break;
          case 6:
            out.writeWord(ext, "H"+machine.addressOf(text)+"_entry", offset);
            break;
          case 8:
            HClass clazz = (HClass)machine.loadClass(sym);
            out.writeWord("H"+machine.addressOf(clazz), offset);
            break;
          case 16:
            HObject object = machine.resolveString(str);
            out.writeWord("H"+machine.addressOf(object), offset);
            break;
          case 32:
            clazz = (HClass)machine.loadClass(mtd);
            HMethodText callee = clazz.getMethodText(idx);
            if (callee == null)
              throw new NullPointerException();
            out.writeWord("H"+machine.addressOf(callee), offset);
            break;
          case 34:
            clazz = (HClass)machine.loadClass(mtd);
            callee = clazz.getMethodText(idx);
            if (callee == null)
              throw new NullPointerException();
            out.writeWord("H"+machine.addressOf(callee), "H"+machine.addressOf(text)+"_entry", offset);
            break;
          default:
            throw new UnsupportedOperationException("Flags: "+flags);
          }
          i += 3;
        }
  
      }
    }
  
  }

}

