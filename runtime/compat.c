/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <signal.h>
#include <unistd.h>
#include <sys/types.h>

#include <jni.h>

JNIEXPORT
void JNICALL JVM_RaiseSignal(jint sig)
{
  raise(sig);
}

#ifdef WIN32

JNIEXPORT
jstring JNICALL JVM_GetClassName(JNIEnv *env, jclass object)
{
  jclass clazz;
  jmethodID methodID;

  clazz = (*env)->GetObjectClass(env, object);
  if (clazz == NULL)
    return NULL;

  methodID = (*env)->GetMethodID(env, clazz, "getName", "()Ljava/lang/String;");
  (*env)->DeleteLocalRef(env, clazz);
  if (methodID == NULL)
    return NULL;

  return (*env)->CallObjectMethod(env, clazz, methodID);
}

#else

JNIEXPORT
void JNICALL JVM_Sleep(JNIEnv *env, jclass clazz, jlong millis)
{
  jmethodID methodID;

  methodID = (*env)->GetStaticMethodID(env, clazz, "sleep", "(J)V");
  if (methodID == NULL)
    return;

  (*env)->CallStaticVoidMethod(env, clazz, methodID, millis);
}

#ifdef __linux__
pid_t fork1()
{
  return fork();
}
#endif /* __linux__ */

#endif /* WIN32 */

