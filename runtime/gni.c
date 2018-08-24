/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <stdlib.h>
#include <string.h>

#include <gni.h>

#include "hstructs.h"
#include "jnivm.h"

/* CountStackFrames
 */
typedef
struct {
  jint counter;
} CountStackFrames_data;
static
jboolean CountStackFrames_func(JNIEnv *env, jclass clazz, jchar index, jint line, void *untyped)
{
  CountStackFrames_data *data = untyped;

  data->counter++;
  return JNI_FALSE;
}
static
jint CountStackFrames(JNIEnv *env)
{
  CountStackFrames_data data;

  data.counter = 0;
  TraverseStackByTrace(env, CountStackFrames_func, &data);
  return data.counter;
}

/* CallStack
 */
typedef
struct {
  jsize counter;
  jsize depth;
  jobjectArray array;
} CallStack_data;
static
jboolean CallStack_func(JNIEnv *env, jclass clazz, jchar index, jint line, void *untyped)
{
  CallStack_data *data = untyped;

  if (data->counter >= data->depth) {
    (*env)->SetObjectArrayElement(env, data->array, data->counter-data->depth, clazz);
    if ((*env)->ExceptionCheck(env))
      return JNI_TRUE;
  }
  data->counter++;
  return JNI_FALSE;
}
static
jobjectArray CallStack(JNIEnv *env, jint depth)
{
  jsize counter;
  jclass clazz = NULL;
  CallStack_data data;

  counter = CountStackFrames(env);
  if (counter < depth)
    counter = depth;

  clazz = (*env)->FindClass(env, "java/lang/Class");
  if (clazz == NULL)
    goto exit;

  data.counter = 0;
  data.depth = depth;
  data.array = (*env)->NewObjectArray(env, counter-depth, clazz, NULL);
  if (data.array == NULL)
    goto exit;

  if (TraverseStackByTrace(env, CallStack_func, &data)) {
    (*env)->DeleteLocalRef(env, data.array);
    data.array = NULL;
    goto exit;
  }

exit:

  (*env)->DeleteLocalRef(env, clazz);
  return data.array;
}

/* GetStackTrace
 */
typedef
struct {
  jsize counter;
  jclass clazz;
  jmethodID constructorID;
  jobjectArray array;
} GetStackTrace_data;
static
jboolean GetStackTrace_func(JNIEnv *env, jclass clazz, jchar index, jint line, void *untyped)
{
  GetStackTrace_data *data = untyped;
  const struct classInfo *classInfo;
  const struct methodInfo *methodInfo;
  char *dotted;
  jint i;
  struct reflectInfo *reflect;
  jstring className;
  jstring methodName;
  jstring fileName;
  jint lineNumber;
  jobject element;

  LoadMetadata(env, clazz);
  if ((*env)->ExceptionCheck(env))
    return JNI_TRUE;

  classInfo = CLASS_INFO(clazz);
  reflect = classInfo->reflect;
  methodInfo = &reflect->methods[index];

  dotted = strdup(classInfo->name);
  if (dotted == NULL)
    abort();

  for (i = 0; dotted[i] != '\0'; i++)
    if (dotted[i] == '/')
      dotted[i] = '.';

  className = (*env)->NewStringUTF(env, dotted);
  free(dotted);
  if (className == NULL)
    return JNI_TRUE;

  methodName = (*env)->NewStringUTF(env, methodInfo->name);
  if (methodName == NULL) {
    (*env)->DeleteLocalRef(env, className);
    return JNI_TRUE;
  }

  fileName = NULL;
  if (reflect->sourceFile != NULL) {
    fileName = (*env)->NewStringUTF(env, reflect->sourceFile);
    if (fileName == NULL) {
      (*env)->DeleteLocalRef(env, methodName);
      (*env)->DeleteLocalRef(env, className);
      return JNI_TRUE;
    }
  }

  lineNumber = line;
  if ((methodInfo->accessFlags & ACC_NATIVE) != 0)
    lineNumber = -2;

  element = (*env)->NewObject(env, data->clazz, data->constructorID, className, methodName, fileName, lineNumber);
  (*env)->DeleteLocalRef(env, fileName);
  (*env)->DeleteLocalRef(env, methodName);
  (*env)->DeleteLocalRef(env, className);
  if (element == NULL)
    return JNI_TRUE;

  (*env)->SetObjectArrayElement(env, data->array, data->counter, element);
  (*env)->DeleteLocalRef(env, element);
  if ((*env)->ExceptionCheck(env))
    return JNI_TRUE;

  data->counter++;
  return JNI_FALSE;
}
static
jobjectArray GetStackTrace(JNIEnv *env)
{
  jsize counter;
  GetStackTrace_data data;
  static jint entered;

  if (entered > 0)
    return NULL;

  entered++;

  counter = CountStackFrames(env);

  data.counter = 0;
  data.clazz = (*env)->FindClass(env, "java/lang/StackTraceElement");
  if (data.clazz == NULL)
    goto exit;
  data.constructorID = (*env)->GetMethodID(env, data.clazz, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");
  if (data.constructorID == NULL)
    goto exit;
  data.array = (*env)->NewObjectArray(env, counter, data.clazz, NULL);
  if (data.array == NULL)
    goto exit;

  if (TraverseStackByTrace(env, GetStackTrace_func, &data)) {
    (*env)->DeleteLocalRef(env, data.array);
    data.array = NULL;
    goto exit;
  }

exit:

  entered--;

  (*env)->DeleteLocalRef(env, data.clazz);
  return data.array;
}

/* Clone
 */
static
jobject JNICALL GNI_Clone(JNIEnv *env, jobject object)
{
  return CloneObject(env, object);
}

/* GetStackTrace
 */
static
jobjectArray JNICALL GNI_GetStackTrace(JNIEnv *env)
{
  return GetStackTrace(env);
}

/* Wait
 */
static
void JNICALL GNI_Wait(JNIEnv *env, jobject object, jlong millis)
{
  jint error;

  error = MonitorWait(env, object, millis);
  if (error != JNI_OK)
    if (!(*env)->ExceptionCheck(env))
      ThrowByName(env, "java/lang/IllegalMonitorStateException", NULL);
}

/* Notify
 */
static
void JNICALL GNI_Notify(JNIEnv *env, jobject object)
{
  jint error;

  error = MonitorNotify(env, object);
  if (error != JNI_OK)
    if (!(*env)->ExceptionCheck(env))
      ThrowByName(env, "java/lang/IllegalMonitorStateException", NULL);
}

/* NotifyAll
 */
static
void JNICALL GNI_NotifyAll(JNIEnv *env, jobject object)
{
  jint error;

  error = MonitorNotifyAll(env, object);
  if (error != JNI_OK)
    if (!(*env)->ExceptionCheck(env))
      ThrowByName(env, "java/lang/IllegalMonitorStateException", NULL);
}

/* CompilerEnable
 */
static
void JNICALL GNI_CompilerEnable(JNIEnv *env)
{
}

/* CompilerDisable
 */
static
void JNICALL GNI_CompilerDisable(JNIEnv *env)
{
}

/* CompilerCommand
 */
static
jobject JNICALL GNI_CompilerCommand(JNIEnv *env, jobject command)
{
  return NULL;
}

/* CompileClass
 */
static
jboolean JNICALL GNI_CompileClass(JNIEnv *env, jclass clazz)
{
  return JNI_FALSE;
}

/* CompileClasses
 */
static
jboolean JNICALL GNI_CompileClasses(JNIEnv *env, jstring classes)
{
  return JNI_FALSE;
}

/* GC
 */
static
void JNICALL GNI_GC(JNIEnv *env)
{
  gc(env);
}

/* TotalMemory
 */
static
jlong JNICALL GNI_TotalMemory(JNIEnv *env)
{
  return TotalMemory(env);
}

/* FreeMemory
 */
static
jlong JNICALL GNI_FreeMemory(JNIEnv *env)
{
  return FreeMemory(env);
}

/* MaxMemory
 */
static
jlong JNICALL GNI_MaxMemory(JNIEnv *env)
{
  return MaxMemory(env);
}

/* TraceMethodCalls
 */
static
void JNICALL GNI_TraceMethodCalls(JNIEnv *env, jboolean on)
{
}

/* TraceInstruction
 */
static
void JNICALL GNI_TraceInstructions(JNIEnv *env, jboolean on)
{
}

/* IdentityHashCode
 */
static
jint JNICALL GNI_IdentityHashCode(JNIEnv *env, jobject object)
{
  if (object == NULL)
    return 0;
  return (jint)object ^ 0xD4F307A9;
}

/* CurrentThread
 */
static
jobject JNICALL GNI_CurrentThread(JNIEnv *env)
{
  return CurrentThread(env);
}

/* ThreadYield
 */
static
void JNICALL GNI_ThreadYield(JNIEnv *env)
{
  thread_yield();
}

/* CallerClass
 */
static
jobject JNICALL GNI_CallerClass(JNIEnv *env)
{
  return CallerClass(env, 3);
}

/* CallStack
 */
static
jobjectArray JNICALL GNI_CallStack(JNIEnv *env)
{
  return CallStack(env, 3);
}

/* GetClassModifiers
 */
static
jint JNICALL GNI_GetClassModifiers(JNIEnv *env, jclass clazz)
{
  return (jint)CLASS_INFO(clazz)->accessFlags;
}

/* GetClassLoader
 */
static
jobject JNICALL GNI_GetClassLoader(JNIEnv *env, jclass clazz)
{
  return (*env)->NewLocalRef(env, CLASS_INFO(clazz)->loader);
}

/* GetClassName
 */
static
jstring JNICALL GNI_GetClassName(JNIEnv *env, jobject object)
{
  const char *name;
  jint length;
  char *dotted;
  jstring string = NULL;
  jint i;

  name = CLASS_INFO(object)->name;

  if (name[0] == '<') {

    length = strlen(name)-2;
    dotted = malloc(length+1);
    if (dotted == NULL) {
      ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
      goto exit;
    }
    strncpy(dotted, &name[1], length);
    dotted[length] = '\0';

  } else {

    dotted = strdup(name);
    if (dotted == NULL) {
      ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
      goto exit;
    }
    for (i = 0; dotted[i] != '\0'; i++)
      if (dotted[i] == '/')
        dotted[i] = '.';

  }

  string = (*env)->NewStringUTF(env, dotted);

  free(dotted);

exit:

  return string;
}

/* InitializeClass
 */
static
void JNICALL GNI_InitializeClass(JNIEnv *env, jclass clazz)
{
  InitializeClass(env, clazz);
}

/* LinkClass
 */
static
void JNICALL GNI_LinkClass(JNIEnv *env, jclass clazz)
{
  LinkClass(env, clazz);
}

/* DeriveClass
 */
static
jclass JNICALL GNI_DeriveClass(JNIEnv *env, jobject loader, jstring name, jbyteArray array, jint start, jint length)
{
  return DeriveClass(env, loader, name, array, start, length);
}

/* GetDeclaringClass
 */
static
jclass JNICALL GNI_GetDeclaringClass(JNIEnv *env, jclass clazz)
{
  const char *name;

  LoadMetadata(env, clazz);
  if ((*env)->ExceptionCheck(env))
    return NULL;
  
  name = CLASS_INFO(clazz)->reflect->declaringClass;
  if (name == NULL)
    return NULL;

  return FindClassFromClassLoader(env, CLASS_INFO(clazz)->loader, name);
}

/* GetDeclaredClasses
 */
static
jobjectArray JNICALL GNI_GetDeclaredClasses(JNIEnv *env, jclass clazz)
{
  const struct innerInfo *inners;
  jint count;
  jobjectArray array;
  jint index;
  jclass inner;

  LoadMetadata(env, clazz);
  if ((*env)->ExceptionCheck(env))
    return NULL;
  
  count = 0;
  inners = CLASS_INFO(clazz)->reflect->inners;
  if (inners != NULL)
    for (index = 0; inners[index].name != NULL; index++)
      count++;

  array = (*env)->NewObjectArray(env, count, GETCLASS(clazz), NULL);
  if (array == NULL)
    return NULL;

  count = 0;
  if (inners != NULL)
    for (index = 0; inners[index].name != NULL; index++) {
      inner = FindClassFromClassLoader(env, CLASS_INFO(clazz)->loader, inners[index].name);
      if (inner == NULL) {
        (*env)->DeleteLocalRef(env, array);
        return NULL;
      }
      (*env)->SetObjectArrayElement(env, array, count++, inner);
      (*env)->DeleteLocalRef(env, inner);
      if ((*env)->ExceptionCheck(env)) {
        (*env)->DeleteLocalRef(env, array);
        return NULL;
      }
    }

  return array;
}

/* GetDeclaredFields
 */
static
jobjectArray JNICALL GNI_GetDeclaredFields(JNIEnv *env, jclass clazz)
{
  const struct fieldInfo *fields;
  jint count;
  jobjectArray array;
  jfieldID fieldID;
  jobject field;
  jint index;
  jclass fieldClass;

  fieldClass = (*env)->FindClass(env, "java/lang/reflect/Field");
  if (fieldClass == NULL)
    return NULL;

  LoadMetadata(env, clazz);
  if ((*env)->ExceptionCheck(env))
    return NULL;
  
  count = 0;
  fields = CLASS_INFO(clazz)->reflect->fields;
  if (fields != NULL)
    for (index = 0; fields[index].declaringClass != NULL; index++)
      count++;

  array = (*env)->NewObjectArray(env, count, fieldClass, NULL);
  if (array == NULL)
    return NULL;

  count = 0;
  if (fields != NULL)
    for (index = 0; fields[index].declaringClass != NULL; index++) {
      fieldID = FIELD_ID(&fields[index]);
      field = (*env)->ToReflectedField(env, clazz, fieldID);
      if (field == NULL) {
        (*env)->DeleteLocalRef(env, array);
        return NULL;
      }
      (*env)->SetObjectArrayElement(env, array, count++, field);
      (*env)->DeleteLocalRef(env, field);
      if ((*env)->ExceptionCheck(env)) {
        (*env)->DeleteLocalRef(env, array);
        return NULL;
      }
    }

  return array;
}

/* GetDeclaredMethods
 */
static
jobjectArray JNICALL GNI_GetDeclaredMethods(JNIEnv *env, jclass clazz)
{
  const struct methodInfo *methods;
  jint count;
  jobjectArray array;
  jmethodID methodID;
  jobject method;
  jint index;
  jclass methodClass;

  methodClass = (*env)->FindClass(env, "java/lang/reflect/Method");
  if (methodClass == NULL)
    return NULL;

  LoadMetadata(env, clazz);
  if ((*env)->ExceptionCheck(env))
    return NULL;
  
  count = 0;
  methods = CLASS_INFO(clazz)->reflect->methods;
  if (methods != NULL)
    for (index = 0; methods[index].declaringClass != NULL; index++)
      if (methods[index].name[0] != '<')
        count++;

  array = (*env)->NewObjectArray(env, count, methodClass, NULL);
  if (array == NULL)
    return NULL;

  count = 0;
  if (methods != NULL)
    for (index = 0; methods[index].declaringClass != NULL; index++) 
      if (methods[index].name[0] != '<') {
        methodID = METHOD_ID(&methods[index]);
        method = (*env)->ToReflectedMethod(env, clazz, methodID);
        if (method == NULL) {
          (*env)->DeleteLocalRef(env, array);
          return NULL;
        }
        (*env)->SetObjectArrayElement(env, array, count++, method);
        (*env)->DeleteLocalRef(env, method);
        if ((*env)->ExceptionCheck(env)) {
          (*env)->DeleteLocalRef(env, array);
          return NULL;
        }
      }

  return array;
}

/* GetDeclaredConstructors
 */
static
jobjectArray JNICALL GNI_GetDeclaredConstructors(JNIEnv *env, jclass clazz)
{
  const struct methodInfo *methods;
  jint count;
  jobjectArray array;
  jmethodID methodID;
  jobject method;
  jint index;
  jclass methodClass;

  methodClass = (*env)->FindClass(env, "java/lang/reflect/Constructor");
  if (methodClass == NULL)
    return NULL;

  LoadMetadata(env, clazz);
  if ((*env)->ExceptionCheck(env))
    return NULL;
  
  count = 0;
  methods = CLASS_INFO(clazz)->reflect->methods;
  if (methods != NULL)
    for (index = 0; methods[index].declaringClass != NULL; index++)
      if (methods[index].name[0] == '<' && methods[index].name[1] != 'c')
        count++;

  array = (*env)->NewObjectArray(env, count, methodClass, NULL);
  if (array == NULL)
    return NULL;

  count = 0;
  if (methods != NULL)
    for (index = 0; methods[index].declaringClass != NULL; index++) 
      if (methods[index].name[0] == '<' && methods[index].name[1] != 'c') {
        methodID = METHOD_ID(&methods[index]);
        method = (*env)->ToReflectedMethod(env, clazz, methodID);
        if (method == NULL) {
          (*env)->DeleteLocalRef(env, array);
          return NULL;
        }
        (*env)->SetObjectArrayElement(env, array, count++, method);
        (*env)->DeleteLocalRef(env, method);
        if ((*env)->ExceptionCheck(env)) {
          (*env)->DeleteLocalRef(env, array);
          return NULL;
        }
      }

  return array;
}

/* GetDeclaredInterfaces
 */
static
jobjectArray JNICALL GNI_GetClassInterfaces(JNIEnv *env, jclass clazz)
{
  const struct classInfo *classInfo;
  jobjectArray array;
  jint i;
  
  classInfo = CLASS_INFO(clazz);

  array = (*env)->NewObjectArray(env, classInfo->directImpls, GETCLASS(clazz), NULL);
  if (array == NULL)
    return NULL;

  for (i = 0; i < classInfo->directImpls; i++) {
    (*env)->SetObjectArrayElement(env, array, i, classInfo->interfaces[i].clazz);
    if ((*env)->ExceptionCheck(env)) {
      (*env)->DeleteLocalRef(env, array);
      return NULL;
    }
  }

  return array;
}

/* GetClassDimensions
 */
static
jint JNICALL GNI_GetClassDimensions(JNIEnv *env, jclass clazz)
{
  const struct classInfo *classInfo;

  classInfo = CLASS_INFO(clazz);
  return classInfo->dimensions & 0xFF;
}

/* GetClassElement
 */
static
jobject JNICALL GNI_GetClassElement(JNIEnv *env, jclass clazz)
{
  const struct classInfo *classInfo;

  classInfo = CLASS_INFO(clazz);
  return (*env)->NewLocalRef(env, classInfo->elementClass);
}

/* HasStaticInitializer
 */
static
jboolean JNICALL GNI_HasStaticInitializer(JNIEnv *env, jclass clazz)
{
  const struct methodInfo *methods;
  jint index;
  
  LoadMetadata(env, clazz);
  if ((*env)->ExceptionCheck(env))
    return JNI_FALSE;
  
  methods = CLASS_INFO(clazz)->reflect->methods;
  if (methods != NULL)
    for (index = 0; methods[index].declaringClass != NULL; index++)
      if (methods[index].name[0] == '<' && methods[index].name[1] == 'c') 
        return JNI_TRUE;

  return JNI_FALSE;
}

/* ThreadStart
 */
static
void JNICALL GNI_ThreadStart(JNIEnv *env, jobject object, jint priority, jboolean daemon, jlong stackSize)
{
  ThreadStart(env, object, priority, daemon, stackSize);
}

/* Exit
 */
static
void JNICALL GNI_Exit(JNIEnv *env, jint status)
{
  Exit(env, status);
}

/* ChangeThreadPriority
 */
static
void JNICALL GNI_ChangeThreadPriority(JNIEnv *env, jobject thread, jint priority)
{
  //unimplemented
}

/* HoldsLock
 */
static
jboolean JNICALL GNI_HoldsLock(JNIEnv *env, jobject object)
{
  return MonitorOwns(env, object);
}

/* glasses interface */
static
const struct GNI_Interface gni_Interface = {
  GNI_Clone,
  GNI_GetStackTrace,
  GNI_Wait,
  GNI_Notify,
  GNI_NotifyAll,
  GNI_CompilerEnable,
  GNI_CompilerDisable,
  GNI_CompilerCommand,
  GNI_CompileClass,
  GNI_CompileClasses,
  GNI_GC,
  GNI_TotalMemory,
  GNI_FreeMemory,
  GNI_MaxMemory,
  GNI_TraceMethodCalls,
  GNI_TraceInstructions,
  GNI_IdentityHashCode,
  GNI_CurrentThread,
  GNI_ThreadYield,
  GNI_CallerClass,
  GNI_CallStack,
  GNI_GetClassModifiers,
  GNI_GetClassLoader,
  GNI_GetClassName,
  GNI_InitializeClass,
  GNI_LinkClass,
  GNI_GetClassInterfaces,
  GNI_GetClassDimensions,
  GNI_GetClassElement,
  GNI_HasStaticInitializer,
  GNI_GetDeclaringClass,
  GNI_GetDeclaredClasses,
  GNI_GetDeclaredFields,
  GNI_GetDeclaredMethods,
  GNI_GetDeclaredConstructors,
  GNI_DeriveClass,
  GNI_ThreadStart,
  GNI_Exit,
  GNI_ChangeThreadPriority,
  GNI_HoldsLock,
};

jint JNICALL GNI_GetInterface(void **pgni, jint version)
{
  if (version != GNI_VERSION_1)
    return JNI_EVERSION;
  *pgni = (void*)&gni_Interface;
  return JNI_OK;
}

