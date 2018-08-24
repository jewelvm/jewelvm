/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <string.h>

#define _JNI_IMPL

#include "arch.h"
#include "hstructs.h"
#include "jnivm.h"

#define JNIPASCAL       __attribute__((stdcall))
#define frameCurrent()  __builtin_frame_address(0)

extern
void JNIPASCAL _athrow_(jthrowable);

static
jlong JNIPASCAL _ldiv_(jlong dividend, jlong divisor)
{
  return dividend/divisor;
}

static
jlong JNIPASCAL _lrem_(jlong dividend, jlong divisor)
{
  return dividend%divisor;
}

static
jboolean JNIPASCAL _subtypeof_(jclass sub, jclass super)
{
  return IsSubtypeOf(sub, super);
}

static
jboolean JNIPASCAL _comptypeof_(jclass sub, jclass super)
{
  return IsComptypeOf(sub, super);
}

static
jobject JNIPASCAL _imlookup_(jclass clazz, jclass inter_face, jsize index)
{
  return IMLookup(clazz, inter_face, index);
}

static
jboolean JNIPASCAL _islocked_(jobject object)
{
  JNIEnv *env;
  
  env = CurrentEnv();
  return mon_owns(&env->jvm->montab, object);
}

static
void JNIPASCAL _unlock_(jobject object)
{
  JNIEnv *env;
  
  env = CurrentEnv();
  mon_release(&env->jvm->montab, object);
}

static
void JNIPASCAL _init_(jclass clazz)
{
  JNIEnv *env;
  void *callback;
  jthrowable thrown;

  env = CurrentEnv();

  if (env->thrown == NULL) {
    callback = env->callback;
    env->callback = frameCurrent();

    SwitchFromJava(env);

    InitializeClass(env, clazz);

    SwitchToJava(env);

    env->callback = callback;
  }

  thrown = env->thrown;
  env->thrown = NULL;
  if (thrown != NULL) {
    _athrow_(thrown);
    _init_(clazz); /* unreachable, avoids tail call optimization */
  }
}

static
jobject JNIPASCAL _newinstance_(jclass clazz)
{
  jobject result = NULL;
  JNIEnv *env;
  void *callback;
  jthrowable thrown;

  env = CurrentEnv();

  if (env->thrown == NULL) {
    callback = env->callback;
    env->callback = frameCurrent();

    SwitchFromJava(env);

    result = AllocObject(env, clazz);

    SwitchToJava(env);
    env->functions->DeleteLocalRef(env, result);

    env->callback = callback;
  }

  thrown = env->thrown;
  env->thrown = NULL;
  if (thrown != NULL)
    _athrow_(thrown);

  return result;
}

static
jarray JNIPASCAL _newarray_(jclass clazz, jsize length)
{
  jarray result = NULL;
  JNIEnv *env;
  void *callback;
  jthrowable thrown;

  env = CurrentEnv();

  if (env->thrown == NULL) {
    callback = env->callback;
    env->callback = frameCurrent();

    SwitchFromJava(env);

    result = AllocArray(env, clazz, length);

    SwitchToJava(env);
    env->functions->DeleteLocalRef(env, result);

    env->callback = callback;
  }

  thrown = env->thrown;
  env->thrown = NULL;
  if (thrown != NULL)
    _athrow_(thrown);

  return result;
}

static
void JNIPASCAL _lock_(jobject object)
{
  JNIEnv *env;
  jint result;
  void *callback;
  jthrowable thrown;

  env = CurrentEnv();

  if (env->thrown == NULL) {
    callback = env->callback;
    env->callback = frameCurrent();

    object = env->functions->NewLocalRef(env, object);
    SwitchFromJava(env);

    result = env->functions->MonitorEnter(env, object);
    if (result != JNI_OK)
      env->functions->FatalError(env, "no more locks");
    env->functions->DeleteLocalRef(env, object);

    SwitchToJava(env);

    env->callback = callback;
  }

  thrown = env->thrown;
  env->thrown = NULL;
  if (thrown != NULL) {
    _athrow_(thrown);
    _lock_(object); /* unreachable, avoids tail call optimization */
  }
}

static
jobject JNIPASCAL _lazy_(jobject text, jint level)
{
  jobject result = NULL;
  JNIEnv *env;
  void *callback;
  jthrowable thrown;

  env = CurrentEnv();

  if (env->thrown == NULL) {
    callback = env->callback;
    env->callback = frameCurrent();

    SwitchFromJava(env);

    result = LazyResolve(env, text, level);

    SwitchToJava(env);
    env->functions->DeleteLocalRef(env, result);

    env->callback = callback;
  }

  thrown = env->thrown;
  env->thrown = NULL;
  if (thrown != NULL)
    _athrow_(thrown);

  return result;
}

static jobject _ncalll_(jclass clazz, jint slot, char *pdesc, jobject object, ...) __attribute__((alias("_ncallj_")));
static jboolean _ncallz_(jclass clazz, jint slot, char *pdesc, jobject object, ...) __attribute__((alias("_ncallj_")));
static jbyte _ncallb_(jclass clazz, jint slot, char *pdesc, jobject object, ...) __attribute__((alias("_ncallj_")));
static jchar _ncallc_(jclass clazz, jint slot, char *pdesc, jobject object, ...) __attribute__((alias("_ncallj_")));
static jshort _ncalls_(jclass clazz, jint slot, char *pdesc, jobject object, ...) __attribute__((alias("_ncallj_")));
static jint _ncalli_(jclass clazz, jint slot, char *pdesc, jobject object, ...) __attribute__((alias("_ncallj_")));
static jfloat _ncallf_(jclass clazz, jint slot, char *pdesc, jobject object, ...) __attribute__((alias("_ncallj_")));
static jdouble _ncalld_(jclass clazz, jint slot, char *pdesc, jobject object, ...) __attribute__((alias("_ncallj_")));
static void _ncallv_(jclass clazz, jint slot, char *pdesc, jobject object, ...) __attribute__((alias("_ncallj_")));

static
jlong _ncallj_(jclass clazz, jint slot, char *pdesc, jobject object, ...)
{
  JNIEnv *env;
  void *callback;
  jthrowable thrown;
  jint frames;
  jint error;
  jsize i;
  jsize size;
  va_list args;
  void *fun;
  jlong result = 0;

  env = CurrentEnv();

  if (env->thrown == NULL) {
    callback = env->callback;
    env->callback = frameCurrent();

    frames = env->frames;
    env->frames = 0;
    error = env->functions->PushLocalFrame(env, 16);
    if (error != JNI_OK)
      env->functions->FatalError(env, "could not push local frame");
    env->functions->NewLocalRef(env, object);
    size = 0;
    if (pdesc != NULL) {
      va_start(args, object);
      for (i = 0; pdesc[i] != '\0'; i++)
        switch (pdesc[i]) {
        case '[': case 'L':
          env->functions->NewLocalRef(env, va_arg(args, jobject));
          size++;
          break;
        case 'J': case 'D':
          (void)va_arg(args, jlong);
          size += 2;
          break;
        default:
          (void)va_arg(args, jint);
          size++;
        }
      va_end(args);
    }
    SwitchFromJava(env);

    fun = ResolveNative(env, clazz, slot);
    if (fun != NULL) {
      va_start(args, object);
      result = callLongNative(env, fun, object, 4*size, args);
      va_end(args);
    }
  
    SwitchToJava(env);
    PopAllLocalFrames(env);
    env->frames = frames;

    env->callback = callback;
  }

  thrown = env->thrown;
  env->thrown = NULL;
  if (thrown != NULL)
    _athrow_(thrown);

  return result;
}

/* GetCallbackAddress
 */
void *GetCallbackAddress(const char *name)
{
       if (strcmp(name, "_athrow_") == 0) return _athrow_;
  else if (strcmp(name, "_ldiv_") == 0) return _ldiv_;
  else if (strcmp(name, "_lrem_") == 0) return _lrem_;
  else if (strcmp(name, "_init_") == 0) return _init_;
  else if (strcmp(name, "_newinstance_") == 0) return _newinstance_;
  else if (strcmp(name, "_newarray_") == 0) return _newarray_;
  else if (strcmp(name, "_lock_") == 0) return _lock_;
  else if (strcmp(name, "_unlock_") == 0) return _unlock_;
  else if (strcmp(name, "_islocked_") == 0) return _islocked_;
  else if (strcmp(name, "_subtypeof_") == 0) return _subtypeof_;
  else if (strcmp(name, "_comptypeof_") == 0) return _comptypeof_;
  else if (strcmp(name, "_imlookup_") == 0) return _imlookup_;
  else if (strcmp(name, "_lazy_") == 0) return _lazy_;
  else if (strcmp(name, "_ncalll_") == 0) return _ncalll_;
  else if (strcmp(name, "_ncallz_") == 0) return _ncallz_;
  else if (strcmp(name, "_ncallb_") == 0) return _ncallb_;
  else if (strcmp(name, "_ncallc_") == 0) return _ncallc_;
  else if (strcmp(name, "_ncalls_") == 0) return _ncalls_;
  else if (strcmp(name, "_ncalli_") == 0) return _ncalli_;
  else if (strcmp(name, "_ncallj_") == 0) return _ncallj_;
  else if (strcmp(name, "_ncallf_") == 0) return _ncallf_;
  else if (strcmp(name, "_ncalld_") == 0) return _ncalld_;
  else if (strcmp(name, "_ncallv_") == 0) return _ncallv_;
  else return NULL;
}

