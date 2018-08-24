/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.LoadedClassInfo.PlacedMethodInfo;
import jewel.core.cache.CacheObject;
import jewel.core.clfile.AbstractMethodException;
import jewel.core.clfile.IncompatibleClassChangeException;
import jewel.core.clfile.VerifyException;
import jewel.core.jiro.IRCFG;
import jewel.core.jiro.IRConverter;
import jewel.core.jiro.IROptimizer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

final class ConvertedMethod implements CacheObject {

  static final class Key extends MethodID {

    public Key(String name, long version, int index, int level) {
      super(name, version, index, level, "cvrt");
    }

    public CacheObject newInstance() {
      return new ConvertedMethod();
    }

  }

  static final class Params {

    public final PlacedMethodInfo method;
    public final Context context;
    public final int level;

    public Params(PlacedMethodInfo method, Context context, int level) {
      this.method = method;
      this.context = context;
      this.level = level;
    }

  }

  private IRCFG cfg;

  protected ConvertedMethod() { }

  public IRCFG getCFG() {
    return cfg;
  }

  public void construct(Object cacheParams) throws VerifyException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, IncompatibleClassChangeException, AbstractMethodException {
    PlacedMethodInfo method = ((Params)cacheParams).method;
    Context context = ((Params)cacheParams).context;
    int level = ((Params)cacheParams).level;

    Context previous = Context.get();
    Context.set(context);
    try {

      boolean verbose = System.getProperty("jewel.vm.verbose") != null;

      if (verbose)
        System.err.println("      Method: "+method.getOwner().getName()+"."+method.getName()+method.getDescriptor());

      if (level > 0) {

        cfg = method.getCFG(context, level-1);

      } else {

        IRConverter converter = new IRConverter();

        long start = System.currentTimeMillis();

        cfg = converter.convert(method);

        long end = System.currentTimeMillis();

        if (verbose)
          System.err.println("  Conversion: "+((float)(end-start)/1000)+"s");

      }

      IROptimizer optimizer = new IROptimizer(level);

      long start = System.currentTimeMillis();

      optimizer.optimize(cfg);

      long end = System.currentTimeMillis();

      if (verbose)
        System.err.println("Optimization: "+((float)(end-start)/1000)+"s");

    } finally {
      Context.set(previous);
    }
  }

  public void readFrom(InputStream is) throws IOException {
    DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
    cfg = new IRCFG();
    cfg.readFrom(in);
  }

  public void writeTo(OutputStream os) throws IOException {
    DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
    cfg.writeTo(out);
    out.flush();
  }

}

