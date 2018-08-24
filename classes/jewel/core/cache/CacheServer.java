/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class CacheServer {

  public static void stop(int port) throws IOException {
    for (int i = 0; ; i++) {
      Socket socket;
      try {
        socket = new Socket(InetAddress.getLocalHost(), port);
      } catch (IOException e) {
        if (i != 0)
          return;
        throw e;
      }
      try {
        OutputStream out = socket.getOutputStream();
        out.write(3);
        out.flush();
      } finally {
        socket.close();
      }
      try {
        Thread.sleep(i*100);
      } catch (InterruptedException e) { }
    }
  }

  private final Cache cache;
  private final ServerSocket ssocket;
  private volatile boolean stopped;

  public CacheServer(Cache cache, int port) throws IOException {
    if (cache == null)
      throw new NullPointerException();
    this.cache = cache;
    ssocket = new ServerSocket(port);
  }

  public CacheServer(Cache cache, int port, int backlog) throws IOException {
    if (cache == null)
      throw new NullPointerException();
    this.cache = cache;
    ssocket = new ServerSocket(port, backlog);
  }

  public CacheServer(Cache cache, int port, int backlog, InetAddress bindAddr) throws IOException {
    if (cache == null)
      throw new NullPointerException();
    this.cache = cache;
    ssocket = new ServerSocket(port, backlog, bindAddr);
  }

  public final Cache cache() {
    return cache;
  }

  public final int getLocalPort() {
    return ssocket.getLocalPort();
  }

  public final InetAddress getInetAddress() {
    return ssocket.getInetAddress();
  }

  public void listen() throws IOException {
    for (;;) {
      final Socket socket;
      try {
        socket = ssocket.accept();
      } catch (IOException e) {
        if (stopped)
          return;
        throw e;
      }
      Thread thread = new Thread() {
        public void run() {
          try {
            handleConnection(socket);
          } catch (Throwable e) {
            e.printStackTrace(System.err);
          } finally {
            try {
              socket.close();
            } catch (IOException e) { }
          }
        }
      };
      thread.setDaemon(true);
      thread.start();
    }
  }

  public void close() throws IOException {
    ssocket.close();
  }

  private void handleConnection(Socket socket) throws IOException {
    DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    for (;;) {
      int command;
      try {
        command = in.readByte();
      } catch (IOException e) {
        return;
      }
      switch (command) {
      case 1: {
        CacheKey cacheKey = (CacheKey)RemoteCache.readObject(in);
        CacheObject cacheObject;
        try {
          cacheObject = cache.get(cacheKey);
        } catch (CacheMissException e) {
          out.writeBoolean(true);
          break;
        }
        out.writeBoolean(false);
        cacheObject.writeTo(out);
        break;
      }
      case 2: {
        CacheKey cacheKey = (CacheKey)RemoteCache.readObject(in);
        Object cacheParams = RemoteCache.readObject(in);
        CacheObject cacheObject;
        try {
          cacheObject = cache.get(cacheKey, cacheParams);
        } catch (ConstructionTargetException e) {
          Exception target = e.getTargetException();
          out.writeBoolean(true);
          out.writeUTF(target.getClass().getName());
          String message = target.getMessage();
          boolean bool = message != null;
          out.writeBoolean(bool);
          if (bool)
            out.writeUTF(message);
          break;
        }
        out.writeBoolean(false);
        cacheObject.writeTo(out);
        break;
      }
      case 3:
        if (!stopped) {
          ssocket.close();
          stopped = true;
        }
        return;
      default:
        throw new IOException();
      }
      out.flush();
      Thread.yield();
    }
  }

}

