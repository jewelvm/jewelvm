/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <stdlib.h>
#include <string.h>

#include <jni.h>

static
const char USAGE[] =
  "usage: jewel [-jar] [-options] class/jarfile [args...]\n"
  "options:\n"
  "\t-help\t\t\tprint this message\n"
  "\t-version\t\tprint version number\n"
  "\t-classpath <path>\tset application classpath\n"
  "\t-D<property>=<value>\tset a system property\n"
  "\t-verbose[:class|gc|jni]\tenable verbose output\n"
  "\t-X<option>\t\tspecify a non-standard option\n";

int main(int argc, char *argv[])
{
  int argi;
  char *classpath;
  jboolean jarFlag;
  jboolean versionFlag;
  char *progname;
  int jargi;
  int jargc;
  char **jargv;
  JavaVM *jvm;
  JNIEnv *env;
  JavaVMInitArgs args;
  jint result;
  jclass stringClass;
  jclass mainClass;
  jmethodID mainID;
  jobjectArray mainArgs;
  char *pathoption;
  const char *classname;
  char *internal;
  char *p;
  jstring mainString;

  if (argc == 1) {
    fputs(USAGE, stderr);
    return 0;
  }

  jarFlag = JNI_FALSE;
  versionFlag = JNI_FALSE;
  progname = NULL;

  classpath = getenv("CLASSPATH");
  if (classpath == NULL)
    classpath = ".";

  args.version = JNI_VERSION_1_2;
  args.nOptions = 0;
  args.options = (JavaVMOption*)calloc(argc, sizeof(JavaVMOption));
  args.ignoreUnrecognized = JNI_FALSE;

  if (args.options == NULL) {
    fprintf(stderr, "%s: %s\n", "jewel", "memory allocation failure");
    return -2;
  }

  for (argi = 1; argi < argc; argi++) {
    char *arg = argv[argi];
    if (arg[0] != '-')
      break;
    if (strcmp(arg, "-jar") == 0) {
      jarFlag = JNI_TRUE;
      continue;
    }
    if (strcmp(arg, "-classpath") == 0) {
      if (argi+1 == argc) {
        fprintf(stderr, "%s: %s `%s'\n", "jewel", "missing parameter for option", arg);
        return -1;
      }
      classpath = argv[++argi];
      continue;
    }
    if (arg[1] == 'D' || arg[1] == 'X' || strncmp(arg, "-verbose", 7) == 0) {
      if (strcmp(arg, "-Djava.class.path") == 0)
        classpath = "";
      else if (strncmp(arg, "-Djava.class.path=", 18) == 0)
        classpath = arg+18;
      else {
        args.options[args.nOptions].optionString = arg;
        args.options[args.nOptions].extraInfo = NULL;
        args.nOptions++;
      }
      continue;
    }
    if (strcmp(arg, "-version") == 0) {
      versionFlag = JNI_TRUE;
      continue;
    }
    if (strcmp(arg, "-help") == 0) {
      fputs(USAGE, stderr);
      return 0;
    }
    fprintf(stderr, "%s: %s `%s'\n", "jewel", "unknown option", arg);
    return -1;
  }

  if (argi < argc) {
    progname = argv[argi++];
    if (jarFlag)
      classpath = progname;
  }

  jargc = argc-argi;
  jargv = argv+argi;

  pathoption = (char*)malloc(18+strlen(classpath)+1);
  if (pathoption == NULL) {
    fprintf(stderr, "%s: %s\n", "jewel", "memory allocation failure");
    return -2;
  }

  strcpy(pathoption, "-Djava.class.path=");
  strcat(pathoption, classpath);
  args.options[args.nOptions].optionString = pathoption;
  args.options[args.nOptions].extraInfo = NULL;
  args.nOptions++;

  result = JNI_CreateJavaVM(&jvm, &env, &args);
  if (result != JNI_OK) {
    fprintf(stderr, "%s: %s\n", "jewel", "machine creation failure");
    return -3;
  }

  free(pathoption);
  free(args.options);

  if (versionFlag) {
    jclass systemClass;
    jmethodID methodID;
    jstring property;
    jstring namevalue;
    jstring versionvalue;
    jstring javavalue;
    const char *namechars;
    const char *versionchars;
    const char *javachars;

    systemClass = (*env)->FindClass(env, "java/lang/System");
    if (systemClass == NULL)
      goto exit;

    methodID = (*env)->GetStaticMethodID(env, systemClass, "getProperty", "(Ljava/lang/String;)Ljava/lang/String;");
    if (methodID == NULL)
      goto exit;

    property = (*env)->NewStringUTF(env, "java.vm.name");
    if (property == NULL)
      goto exit;

    namevalue = (*env)->CallStaticObjectMethod(env, systemClass, methodID, property);
    if ((*env)->ExceptionOccurred(env))
      goto exit;

    (*env)->DeleteLocalRef(env, property);

    if (namevalue == NULL) {
      fprintf(stderr, "%s: %s `%s'\n", "jewel", "no value for system property", "java.vm.name");
      goto exit;
    }

    namechars = (*env)->GetStringUTFChars(env, namevalue, NULL);
    if (namechars == NULL)
      goto exit;

    property = (*env)->NewStringUTF(env, "java.vm.version");
    if (property == NULL)
      goto exit;

    versionvalue = (*env)->CallStaticObjectMethod(env, systemClass, methodID, property);
    if ((*env)->ExceptionOccurred(env))
      goto exit;

    (*env)->DeleteLocalRef(env, property);

    if (versionvalue == NULL) {
      fprintf(stderr, "%s: %s `%s'\n", "jewel", "no value for system property", "java.vm.version");
      goto exit;
    }

    versionchars = (*env)->GetStringUTFChars(env, versionvalue, NULL);
    if (versionchars == NULL)
      goto exit;

    property = (*env)->NewStringUTF(env, "java.version");
    if (property == NULL)
      goto exit;

    javavalue = (*env)->CallStaticObjectMethod(env, systemClass, methodID, property);
    if ((*env)->ExceptionOccurred(env))
      goto exit;

    (*env)->DeleteLocalRef(env, property);

    if (javavalue == NULL) {
      fprintf(stderr, "%s: %s `%s'\n", "jewel", "no value for system property", "java.version");
      goto exit;
    }

    javachars = (*env)->GetStringUTFChars(env, javavalue, NULL);
    if (javachars == NULL)
      goto exit;

    fprintf(stderr, "%s %s (java version %s)\n", namechars, versionchars, javachars);

    (*env)->ReleaseStringUTFChars(env, namevalue, namechars);
    (*env)->ReleaseStringUTFChars(env, versionvalue, versionchars);
    (*env)->ReleaseStringUTFChars(env, javavalue, javachars);

    goto exit;
  }

  if (progname == NULL) {
    fputs(USAGE, stderr);
    goto exit;
  }

  stringClass = (*env)->FindClass(env, "java/lang/String");
  if (stringClass == NULL)
    goto exit;

  classname = progname;
  mainString = NULL;

  if (jarFlag) {
    jclass jarClass;
    jmethodID methodID;
    jmethodID initID;
    int length;
    jbyteArray byteArray;
    jstring string;
    jobject jarFile;
    jobject manifest;
    jobject attributes;

    jarClass = (*env)->FindClass(env, "java/util/jar/JarFile");
    if (jarClass == NULL)
      goto exit;

    methodID = (*env)->GetMethodID(env, jarClass, "<init>", "(Ljava/lang/String;)V");
    if (methodID == NULL)
      goto exit;

    length = strlen(progname);
    byteArray = (*env)->NewByteArray(env, length);
    if (byteArray == NULL)
      goto exit;

    (*env)->SetByteArrayRegion(env, byteArray, 0, length, progname);
    if ((*env)->ExceptionOccurred(env))
      goto exit;

    initID = (*env)->GetMethodID(env, stringClass, "<init>", "([B)V");
    if (initID == NULL)
      goto exit;

    string = (*env)->NewObject(env, stringClass, initID, byteArray);
    if (string == NULL)
      goto exit;

    (*env)->DeleteLocalRef(env, byteArray);

    jarFile = (*env)->NewObject(env, jarClass, methodID, string);
    if (jarFile == NULL) {
      fprintf(stderr, "%s: %s `%s'\n", "jewel", "jarfile loading failure", progname);
      (*env)->ExceptionClear(env);
      goto exit;
    }

    (*env)->DeleteLocalRef(env, string);

    methodID = (*env)->GetMethodID(env, jarClass, "getManifest", "()Ljava/util/jar/Manifest;");
    if (methodID == NULL)
      goto exit;

    manifest = (*env)->CallObjectMethod(env, jarFile, methodID);
    if ((*env)->ExceptionOccurred(env))
      goto exit;

    if (manifest == NULL) {
      fprintf(stderr, "%s: %s `%s'\n", "jewel", "no manifest found in jarfile", progname);
      goto exit;
    }

    (*env)->DeleteLocalRef(env, jarFile);

    methodID = (*env)->GetMethodID(env, (*env)->GetObjectClass(env, manifest), "getMainAttributes", "()Ljava/util/jar/Attributes;");
    if (methodID == NULL)
      goto exit;

    attributes = (*env)->CallObjectMethod(env, manifest, methodID);

    (*env)->DeleteLocalRef(env, manifest);

    methodID = (*env)->GetMethodID(env, (*env)->GetObjectClass(env, attributes), "getValue", "(Ljava/lang/String;)Ljava/lang/String;");
    if (methodID == NULL)
      goto exit;

    string = (*env)->NewStringUTF(env, "Main-Class");
    if (string == NULL)
      goto exit;

    mainString = (*env)->CallObjectMethod(env, attributes, methodID, string);
    if ((*env)->ExceptionOccurred(env))
      goto exit;

    (*env)->DeleteLocalRef(env, attributes);
    (*env)->DeleteLocalRef(env, string);

    if (mainString == NULL) {
      fprintf(stderr, "%s: %s `%s'\n", "jewel", "no Main-Class manifest attribute found in jarfile", progname);
      goto exit;
    }

    classname = (*env)->GetStringUTFChars(env, mainString, NULL);
    if (classname == NULL)
      goto exit;
  }

  internal = strdup(classname);
  if (internal == NULL) {
    fprintf(stderr, "%s: %s\n", "jewel", "memory allocation failure");
    return -2;
  }

  if (jarFlag) {
    (*env)->ReleaseStringUTFChars(env, mainString, classname);
    (*env)->DeleteLocalRef(env, mainString);
  }

  for (p = internal; *p != '\0'; p++)
    if (*p == '.')
      *p = '/';

  mainClass = (*env)->FindClass(env, internal);
  if (mainClass == NULL)
    goto exit;

  free(internal);

  mainID = (*env)->GetStaticMethodID(env, mainClass, "main", "([Ljava/lang/String;)V");
  if (mainID == NULL)
    goto exit;

  mainArgs = (*env)->NewObjectArray(env, jargc, stringClass, NULL);
  if (mainArgs == NULL)
    goto exit;

  if (jargc > 0) {
    jmethodID initID;
    int maxlength;
    jbyteArray byteArray;

    initID = (*env)->GetMethodID(env, stringClass, "<init>", "([BII)V");
    if (initID == NULL)
      goto exit;

    maxlength = 0;
    for (jargi = 0; jargi < jargc; jargi++) {
      char *jarg = jargv[jargi];
      int length = strlen(jarg);
      if (length > maxlength)
        maxlength = length;
    }

    byteArray = (*env)->NewByteArray(env, maxlength);
    if (byteArray == NULL)
      goto exit;

    for (jargi = 0; jargi < jargc; jargi++) {
      char *jarg = jargv[jargi];
      int length = strlen(jarg);
      jstring string;

      (*env)->SetByteArrayRegion(env, byteArray, 0, length, jarg);
      if ((*env)->ExceptionOccurred(env))
        goto exit;

      string = (*env)->NewObject(env, stringClass, initID, byteArray, 0, length);
      if (string == NULL)
        goto exit;

      (*env)->SetObjectArrayElement(env, mainArgs, jargi, string);
      if ((*env)->ExceptionOccurred(env))
        goto exit;

      (*env)->DeleteLocalRef(env, string);
    }

    (*env)->DeleteLocalRef(env, byteArray);
  }

  (*env)->CallStaticVoidMethod(env, mainClass, mainID, mainArgs);

exit:

  result = (*jvm)->DetachCurrentThread(jvm);
  if (result != JNI_OK) {
    fprintf(stderr, "%s: %s\n", "jewel", "main thread detaching failure");
    return -3;
  }

  (*jvm)->DestroyJavaVM(jvm);

  return 0;
}

