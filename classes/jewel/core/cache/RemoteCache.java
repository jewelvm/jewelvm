/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;

public final class RemoteCache extends Cache {

  private final InetAddress address;
  private final int port;

  public RemoteCache(InetAddress address, int port) {
    if (address == null)
      throw new NullPointerException();
    if (port < 0 || port > 65535)
      throw new IllegalArgumentException();
    this.address = address;
    this.port = port;
  }

  protected CacheObject get(CacheKey cacheKey, BoolRef stored) throws CacheMissException {
    try {
      Socket socket = new Socket(address, port);
      try {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        out.writeByte(1);
        writeObject(out, (Streamable)cacheKey);
        out.flush();
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        boolean error = in.readBoolean();
        if (!error) {
          CacheObject cacheObject = cacheKey.newInstance();
          cacheObject.readFrom(in);
          return cacheObject;
        }
      } finally {
        socket.close();
      }
    } catch (IOException e) { }
    throw new CacheMissException(cacheKey.getEntry());
  }

  protected CacheObject get(CacheKey cacheKey, Object cacheParams, BoolRef stored) throws ConstructionTargetException {
    try {
      Socket socket = new Socket(address, port);
      try {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        out.writeByte(2);
        writeObject(out, (Streamable)cacheKey);
        writeObject(out, (Streamable)cacheParams);
        out.flush();
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        boolean error = in.readBoolean();
        if (!error) {
          CacheObject cacheObject = cacheKey.newInstance();
          cacheObject.readFrom(in);
          return cacheObject;
        }
        String name = in.readUTF();
        boolean hasmsg = in.readBoolean();
        String message = null;
        if (hasmsg)
          message = in.readUTF();
        throwex(name, message);
        return null;
      } finally {
        socket.close();
      }
    } catch (IOException e) {
      throw new ConstructionTargetException(e);
    }
  }

  /*private*/ protected static Streamable readObject(InputStream is) throws IOException {
    Streamable object = null;
    DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
    boolean bool = in.readBoolean();
    if (bool) {
      String name = in.readUTF();
      Class clazz;
      try {
        clazz = Class.forName(name);
      } catch (ClassNotFoundException e) {
        throw new NoClassDefFoundError(e.getMessage());
      }
      Object instance;
      try {
        instance = clazz.newInstance();
      } catch (IllegalAccessException e) {
        throw new IllegalAccessError(e.getMessage());
      } catch (InstantiationException e) {
        throw new InstantiationError(e.getMessage());
      }
      if (!(instance instanceof Streamable))
        throw new IOException();
      object = (Streamable)instance;
      object.readFrom(in);
    }
    return object;
  }

  private static void writeObject(OutputStream os, Streamable object) throws IOException {
    DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
    boolean bool = object != null;
    out.writeBoolean(bool);
    if (bool) {
      Class clazz = object.getClass();
      String name = clazz.getName();
      out.writeUTF(name);
      object.writeTo(out);
    }
  }

  private void throwex(String name, String message) throws ConstructionTargetException {
    Class clazz;
    try {
      clazz = Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new NoClassDefFoundError(e.getMessage());
    }

    Constructor init;
    try {
      init = clazz.getConstructor(new Class[]{ String.class });
    } catch (NoSuchMethodException e) {
      throw new NoSuchMethodError(e.getMessage());
    }

    Object object;
    try {
      object = init.newInstance(new Object[]{ message });
    } catch (InstantiationException e) {
      throw new InstantiationError(e.getMessage());
    } catch (IllegalAccessException e) {
      throw new IllegalAccessError(e.getMessage());
    } catch (InvocationTargetException e) {
      object = e.getTargetException();
    }

    if (object instanceof Error)
      throw (Error)object;

    if (object instanceof RuntimeException)
      throw (RuntimeException)object;

    if (object instanceof Exception)
      throw new ConstructionTargetException((Exception)object);

    throw new InternalError();
  }

}

