/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.cache.Cache;
import jewel.core.cache.CacheServer;
import jewel.core.cache.FileCache;
import jewel.core.cache.RawCache;
import jewel.core.cache.SoftCache;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public final class Main {

  private static String port = null;
  private static boolean stop = false;

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
      if (arg.equals("-port")) {
        if (argi+1 == args.length) {
          printMessage(System.err, "missing parameter for option `"+arg+"'");
          return;
        }
        port = args[++argi];
        continue;
      }
      if (arg.equals("-stop")) {
        stop = true;
        continue;
      }
      printMessage(System.err, "unknown option `"+arg+"'");
      return;
    }

    if (argi != args.length) {
      printUsage(System.err);
      return;
    }

    int portno = Jewel.DEFAULT_SERVICE_PORT;
    if (port != null)
      try {
        portno = Integer.parseInt(port);
        if (portno < 0 || portno > 65535)
          throw new NumberFormatException(port);
      } catch (NumberFormatException e) {
        printMessage(System.err, "illegal port number `"+port+"'");
        return;
      }

    if (stop) {
      try {
        CacheServer.stop(portno);
      } catch (IOException e) {
        printMessage(System.err, "i/o error stopping server daemon");
      }
      return;
    }

    Cache cache = Jewel.getSystemCache();

    CacheServer server;
    try {
      server = new CacheServer(cache, portno);
    } catch (IOException e) {
      printMessage(System.err, "i/o error starting server daemon");
      return;
    }
    try {
      server.listen();
    } catch (IOException e) {
      e.printStackTrace();
      printMessage(System.err, "i/o error running server daemon");
    } finally {
      try {
        server.close();
      } catch (IOException e) { }
    }

  }

  private static void printUsage(PrintStream err) {
    err.println("usage: jeweld [-options]");
    err.println("options:");
    err.println("\t-help\t\tprint this message");
    err.println("\t-version\tprint version number");
    err.println("\t-port <port>\tspecify port to be used");
    err.println("\t-stop\t\tstop the server daemon");
    err.println("\t-J<flag>\tpass a flag directly to the runtime system");
  }

  private static void printVersion(PrintStream err) {
    err.println("jeweld version 0.95a");
  }

  private static void printMessage(PrintStream err, String message) {
    err.println("jeweld: "+message);
  }

  private Main() { }

}

