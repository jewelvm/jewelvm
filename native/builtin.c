/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <stdlib.h>
#include <string.h>

#include "jnivm.h"
#include "hstructs.h"

static
void ThrowNewByName(JNIEnv *env, const char *name, const char *message)
{
  jclass clazz;
  jint result;

  clazz = (*env)->FindClass(env, name);
  if (clazz == NULL)
    return;

  result = (*env)->ThrowNew(env, clazz, message);
  if (result != JNI_OK)
    if (!(*env)->ExceptionCheck(env))
      (*env)->FatalError(env, "could not throw exception");

  (*env)->DeleteLocalRef(env, clazz);
}

static
void ThrowOutOfMemoryError(JNIEnv *env)
{
  ThrowNewByName(env, "java/lang/OutOfMemoryError", NULL);
}

static
void ThrowInternalError(JNIEnv *env, const char *message)
{
  ThrowNewByName(env, "java/lang/InternalError", message);
}

JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
{
  return JNI_VERSION_1_2;
}

JNIEXPORT
jlong JNICALL Java_java_lang_builtin_getVersion(JNIEnv *env, jclass _clazz, jclass clazz)
{
  return CLASS_INFO(clazz)->version;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_flagFinalized(JNIEnv *env, jclass _clazz, jclass clazz)
{
  CLASS_INFO(clazz)->gcflags |= 0x02;
}

JNIEXPORT
jboolean JNICALL Java_java_lang_builtin_isInitialized(JNIEnv *env, jclass _clazz, jclass clazz)
{
  return CLASS_INFO(clazz)->status == 2;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_allocMetadata(JNIEnv *env, jclass _clazz, jobject object, jint fields, jint methods, jint inners)
{
  struct reflectInfo *reflect;
  struct classInfo *classInfo;

  reflect = (struct reflectInfo*)calloc(1, sizeof(struct reflectInfo));
  if (reflect == NULL) {
    ThrowOutOfMemoryError(env);
    return;
  }

  if (fields > 0) {
    reflect->fields = (struct fieldInfo*)calloc(1, fields*sizeof(struct fieldInfo)+sizeof(void*));
    if (reflect->fields == NULL) {
      free(reflect);
      ThrowOutOfMemoryError(env);
      return;
    }
  }
  
  if (methods > 0) {
    reflect->methods = (struct methodInfo*)calloc(1, methods*sizeof(struct methodInfo)+sizeof(void*));
    if (reflect->methods == NULL) {
      if (fields > 0) free(reflect->fields);
      free(reflect);
      ThrowOutOfMemoryError(env);
      return;
    }
  }
  
  if (inners > 0) {
    reflect->inners = (struct innerInfo*)calloc(1, inners*sizeof(struct innerInfo)+sizeof(void*));
    if (reflect->inners == NULL) {
      if (methods > 0) free(reflect->methods);
      if (fields > 0) free(reflect->fields);
      free(reflect);
      ThrowOutOfMemoryError(env);
      return;
    }
  }

  classInfo = CLASS_INFO(object);
  if (classInfo->reflect != NULL) {
    if (inners > 0) free(reflect->inners);
    if (methods > 0) free(reflect->methods);
    if (fields > 0) free(reflect->fields);
    free(reflect);
    ThrowInternalError(env, NULL);
    return;
  }

  classInfo->reflect = reflect;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_setMetaFlags(JNIEnv *env, jclass _clazz, jobject object, jint flags)
{
  struct reflectInfo *reflect;

  reflect = CLASS_INFO(object)->reflect;
  if (reflect == NULL) {
    ThrowInternalError(env, NULL);
    return;
  }

  reflect->sourceFlags = flags;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_setMetaField(JNIEnv *env, jclass _clazz, jobject object, jint index, jint flags, jstring name, jstring descriptor, jlong offset)
{
  const struct reflectInfo *reflect;
  struct fieldInfo *fieldInfo;
  const char *chars;
  char *field_name;
  char *field_desc;

  reflect = CLASS_INFO(object)->reflect;
  if (reflect == NULL) {
    ThrowInternalError(env, NULL);
    return;
  }

  chars = (*env)->GetStringUTFChars(env, name, NULL);
  if (chars == NULL)
    return;
  field_name = strdup(chars);
  (*env)->ReleaseStringUTFChars(env, name, chars);
  if (field_name == NULL) {
    ThrowOutOfMemoryError(env);
    return;
  }
  
  chars = (*env)->GetStringUTFChars(env, descriptor, NULL);
  if (chars == NULL) {
    free(field_name);
    return;
  }
  field_desc = strdup(chars);
  (*env)->ReleaseStringUTFChars(env, descriptor, chars);
  if (field_desc == NULL) {
    free(field_name);
    ThrowOutOfMemoryError(env);
    return;
  }

  fieldInfo = &reflect->fields[index];
  fieldInfo->declaringClass = object;
  fieldInfo->name = field_name;
  fieldInfo->descriptor = field_desc;
  fieldInfo->offset = SCALED(offset);
  fieldInfo->accessFlags = flags;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_setMetaMethod(JNIEnv *env, jclass _clazz, jobject object, jint index, jint flags, jstring name, jstring descriptor, jobjectArray exceptions, jint dindex)
{
  struct reflectInfo *reflect;
  struct methodInfo *methodInfo;
  const char *chars;
  char *method_name;
  char *method_desc;
  char **excps;
  jsize length;
  jsize i;
  jstring string;

  reflect = CLASS_INFO(object)->reflect;
  if (reflect == NULL) {
    ThrowInternalError(env, NULL);
    return;
  }

  chars = (*env)->GetStringUTFChars(env, name, NULL);
  if (chars == NULL)
    return;
  method_name = strdup(chars);
  (*env)->ReleaseStringUTFChars(env, name, chars);
  if (method_name == NULL) {
    ThrowOutOfMemoryError(env);
    return;
  }
  
  chars = (*env)->GetStringUTFChars(env, descriptor, NULL);
  if (chars == NULL) {
    free(method_name);
    return;
  }
  method_desc = strdup(chars);
  (*env)->ReleaseStringUTFChars(env, descriptor, chars);
  if (method_desc == NULL) {
    free(method_name);
    ThrowOutOfMemoryError(env);
    return;
  }

  length = (*env)->GetArrayLength(env, exceptions);
  if ((*env)->ExceptionCheck(env)) {
    free(method_name);
    free(method_desc);
    return;
  }

  if (length == 0)
    excps = NULL;
  else {
    excps = (char**)calloc(length+1, sizeof(char*));
    if (excps == NULL) {
      free(method_name);
      free(method_desc);
      ThrowOutOfMemoryError(env);
      return;
    }
    for (i = 0; i < length; i++) {
      string = (*env)->GetObjectArrayElement(env, exceptions, i);
      if ((*env)->ExceptionCheck(env)) {
        free(method_name);
        free(method_desc);
        while (i > 0)
          free(excps[--i]);
        free(excps);
        return;
      }
      chars = (*env)->GetStringUTFChars(env, string, NULL);
      if (chars == NULL) {
        free(method_name);
        free(method_desc);
        while (i > 0)
          free(excps[--i]);
        free(excps);
        return;
      }
      excps[i] = strdup(chars);
      (*env)->ReleaseStringUTFChars(env, string, chars);
      if (excps[i] == NULL) {
        free(method_name);
        free(method_desc);
        while (i > 0)
          free(excps[--i]);
        free(excps);
        ThrowOutOfMemoryError(env);
        return;
      }
      (*env)->DeleteLocalRef(env, string);
    }
  }

  methodInfo = &reflect->methods[index];
  methodInfo->declaringClass = object;
  methodInfo->name = method_name;
  methodInfo->descriptor = method_desc;
  methodInfo->exceptions = excps;
  methodInfo->index = dindex;
  methodInfo->accessFlags = flags;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_setMetaDeclaring(JNIEnv *env, jclass _clazz, jobject object, jstring name)
{
  struct reflectInfo *reflect;
  const char *chars;
  char *declaring;

  reflect = CLASS_INFO(object)->reflect;
  if (reflect == NULL) {
    ThrowInternalError(env, NULL);
    return;
  }

  if (name == NULL)
    declaring = NULL;
  else {
    chars = (*env)->GetStringUTFChars(env, name, NULL);
    if (chars == NULL)
      return;
    declaring = strdup(chars);
    (*env)->ReleaseStringUTFChars(env, name, chars);
    if (declaring == NULL) {
      ThrowOutOfMemoryError(env);
      return;
    }
  }

  reflect->declaringClass = declaring;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_setMetaInner(JNIEnv *env, jclass _clazz, jobject object, jint index, jint flags, jstring name)
{
  struct reflectInfo *reflect;
  struct innerInfo *innerInfo;
  const char *chars;
  char *inner_name;

  reflect = CLASS_INFO(object)->reflect;
  if (reflect == NULL) {
    ThrowInternalError(env, NULL);
    return;
  }

  chars = (*env)->GetStringUTFChars(env, name, NULL);
  if (chars == NULL)
    return;
  inner_name = strdup(chars);
  (*env)->ReleaseStringUTFChars(env, name, chars);
  if (inner_name == NULL) {
    ThrowOutOfMemoryError(env);
    return;
  }
  
  innerInfo = &reflect->inners[index];
  innerInfo->name = inner_name;
  innerInfo->accessFlags = flags;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_setMetaSource(JNIEnv *env, jclass _clazz, jobject object, jstring name)
{
  struct reflectInfo *reflect;
  const char *chars;
  char *source;

  reflect = CLASS_INFO(object)->reflect;
  if (reflect == NULL) {
    ThrowInternalError(env, NULL);
    return;
  }

  if (name == NULL)
    source = NULL;
  else {
    chars = (*env)->GetStringUTFChars(env, name, NULL);
    if (chars == NULL)
      return;
    source = strdup(chars);
    (*env)->ReleaseStringUTFChars(env, name, chars);
    if (source == NULL) {
      ThrowOutOfMemoryError(env);
      return;
    }
  }

  reflect->sourceFile = source;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_posReloc(JNIEnv *env, jclass _clazz, jobject text, jint offset)
{
  jbyte *code;

  code = ENTRY4TEXT(text);
  *(jbyte**)&code[offset] += (unsigned)code;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_negReloc(JNIEnv *env, jclass _clazz, jobject text, jint offset)
{
  jbyte *code;

  code = ENTRY4TEXT(text);
  *(jbyte**)&code[offset] -= (unsigned)code;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_posPatch(JNIEnv *env, jclass _clazz, jobject text, jint offset, jobject object)
{
  jbyte *code;

  code = ENTRY4TEXT(text);
  *(jbyte**)&code[offset] += (unsigned)object;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_negPatch(JNIEnv *env, jclass _clazz, jobject text, jint offset, jobject object)
{
  jbyte *code;

  code = ENTRY4TEXT(text);
  *(jbyte**)&code[offset] -= (unsigned)object;
}

JNIEXPORT
jclass JNICALL Java_java_lang_builtin_getDeclaringClass(JNIEnv *env, jclass _clazz, jobject text)
{
  struct textInfo *textInfo;

  textInfo = TEXT_INFO(text);
  return (*env)->NewLocalRef(env, textInfo->declaringClass);
}

JNIEXPORT
jint JNICALL Java_java_lang_builtin_getIndex(JNIEnv *env, jclass _clazz, jobject text)
{
  struct textInfo *textInfo;

  textInfo = TEXT_INFO(text);
  return textInfo->index;
}

// builtins with vm calls
JNIEXPORT
jobject JNICALL Java_java_lang_builtin_newMethodText(JNIEnv *env, jclass _clazz, jbyteArray array, jint start, jint length)
{
  jobject text;
  jbyte *code;

  text = NewMethodText(env, length);
  if (text == NULL)
    return NULL;

  code = ENTRY4TEXT(text);
  (*env)->GetByteArrayRegion(env, array, start, length, code);
  if ((*env)->ExceptionCheck(env))
    return NULL;

  return text;
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_patchSymbol(JNIEnv *env, jclass _clazz, jobject text, jint offset, jstring name)
{
  const char *chars;
  void *address;
  jbyte *code;

  chars = (*env)->GetStringUTFChars(env, name, NULL);
  if (chars == NULL)
    return;

  address = GetCallbackAddress(chars);
  (*env)->ReleaseStringUTFChars(env, name, chars);
  if (address == NULL) {
    ThrowInternalError(env, NULL);
    return;
  }

  code = ENTRY4TEXT(text);
  *(jbyte**)&code[offset] += (unsigned)address;
}

JNIEXPORT
jobject JNICALL Java_java_lang_builtin_getMethodText(JNIEnv *env, jclass _clazz, jclass clazz, jint index)
{
  return GetMethodText(env, clazz, index);
}

JNIEXPORT
void JNICALL Java_java_lang_builtin_setMethodText(JNIEnv *env, jclass _clazz, jclass clazz, jint index, jobject text)
{
  SetMethodText(env, clazz, index, text);
}

JNIEXPORT
jclass JNICALL Java_java_lang_builtin_newClass(JNIEnv *env, jclass _clazz, jobject loader, jstring name, jlong version,
                                               jchar accessflags, jclass superclass, jobjectArray interfaces,
                                               jlong staticSize, jlong staticRefOfs, jchar staticRefs,
                                               jlong instanceSize, jlong instanceRefOfs, jchar instanceRefs,
                                               jint stable, jint dtable, jintArray baseindex, jchar natives,
                                               jclass elem, jint dims)
{
  jint if_length;
  jint bi_length;
  jchar dimpls;
  jclass *ifaces;
  jint i;
  jint *indexes;
  const char *chars;
  jclass result;

  if_length = (*env)->GetArrayLength(env, interfaces);
  if ((*env)->ExceptionCheck(env))
    return NULL;

  bi_length = (*env)->GetArrayLength(env, baseindex);
  if ((*env)->ExceptionCheck(env))
    return NULL;

  if (if_length > 65535 || if_length != bi_length) {
    ThrowInternalError(env, NULL);
    return NULL;
  }
  dimpls = if_length;

  ifaces = (jclass*)malloc(dimpls*sizeof(jclass));
  if (ifaces == NULL) {
    ThrowOutOfMemoryError(env);
    return NULL;
  }

  for (i = 0; i < dimpls; i++) {
    ifaces[i] = (*env)->GetObjectArrayElement(env, interfaces, i);
    if ((*env)->ExceptionCheck(env)) {
      free(ifaces);
      return NULL;
    }
  }

  indexes = (*env)->GetIntArrayElements(env, baseindex, NULL);
  if (indexes == NULL) {
    free(ifaces);
    return NULL;
  }

  chars = (*env)->GetStringUTFChars(env, name, NULL);
  if (chars == NULL) {
    (*env)->ReleaseIntArrayElements(env, baseindex, indexes, JNI_ABORT);
    free(ifaces);
    return NULL;
  }

  result = NewClass(env, loader, chars, version, accessflags, superclass, dimpls, ifaces, indexes,
                  SCALED(staticSize), SCALED(staticRefOfs), staticRefs,
                  SCALED(instanceSize), SCALED(instanceRefOfs), instanceRefs,
                  stable, dtable, natives, elem, dims);

  (*env)->ReleaseStringUTFChars(env, name, chars);
  (*env)->ReleaseIntArrayElements(env, baseindex, indexes, JNI_ABORT);
  free(ifaces);

  return result;
}

