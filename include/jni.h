/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#ifndef _JNI_H
#define _JNI_H

#include <stdarg.h>
#include <stdio.h>

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

#define JNI_VERSION_1_1   0x00010001
#define JNI_VERSION_1_2   0x00010002
#define JNI_VERSION_1_4   0x00010004

#ifdef WIN32
#define JNICALL __attribute__ ((stdcall))
#else
#define JNICALL
#endif /* WIN32 */

#define JNIIMPORT
#define JNIEXPORT

#define JNI_FALSE   0
#define JNI_TRUE    1

#define JNI_COMMIT  1
#define JNI_ABORT   2

#define JNI_OK          0
#define JNI_ERR       (-1)
#define JNI_EDETACHED (-2)
#define JNI_EVERSION  (-3)

typedef unsigned char jboolean;
typedef signed char jbyte;
typedef unsigned short jchar;
typedef signed short jshort;
typedef signed int jint;
typedef signed long long jlong;
typedef float jfloat;
typedef double jdouble;

typedef jint jsize;

#ifdef __cplusplus

class _jobject { };
class _jclass : public _jobject { };
class _jthrowable : public _jobject { };
class _jstring : public _jobject { };
class _jarray : public _jobject { };
class _jbooleanArray : public _jarray { };
class _jbyteArray : public _jarray { };
class _jcharArray : public _jarray { };
class _jshortArray : public _jarray { };
class _jintArray : public _jarray { };
class _jlongArray : public _jarray { };
class _jfloatArray : public _jarray { };
class _jdoubleArray : public _jarray { };
class _jobjectArray : public _jarray { };

typedef _jobject *jobject;
typedef _jclass *jclass;
typedef _jstring *jstring;
typedef _jthrowable *jthrowable;
typedef _jweak *jweak;
typedef _jarray *jarray;
typedef _jbooleanArray *jbooleanArray;
typedef _jbyteArray *jbyteArray;
typedef _jcharArray *jcharArray;
typedef _jshortArray *jshortArray;
typedef _jintArray *jintArray;
typedef _jlongArray *jlongArray;
typedef _jfloatArray *jfloatArray;
typedef _jdoubleArray *jdoubleArray;
typedef _jobjectArray *jobjectArray;

#else

typedef struct _jobject *jobject;
typedef jobject jclass;
typedef jobject jarray;
typedef jobject jstring;
typedef jobject jthrowable;
typedef jarray jbooleanArray;
typedef jarray jbyteArray;
typedef jarray jcharArray;
typedef jarray jshortArray;
typedef jarray jintArray;
typedef jarray jlongArray;
typedef jarray jfloatArray;
typedef jarray jdoubleArray;
typedef jarray jobjectArray;

#endif /* __cplusplus */

typedef jobject jweak;

typedef struct _jfieldID *jfieldID;
typedef struct _jmethodID *jmethodID;

#ifdef __cplusplus
typedef struct JNIEnv JNIEnv;
typedef struct JavaVM JavaVM;
#else
#ifdef _JNI_IMPL
typedef struct JNIEnv JNIEnv;
typedef struct JavaVM JavaVM;
#else
typedef const struct JNINativeInterface *JNIEnv;
typedef const struct JNIInvokeInterface *JavaVM;
#endif /* _JNI_IMPL */
#endif /* __cplusplus */

typedef
union jvalue {
  jboolean z;
  jbyte b;
  jchar c;
  jshort s;
  jint i;
  jlong j;
  jfloat f;
  jdouble d;
  jobject l;
} jvalue;

typedef
struct JNINativeMethod {
  char *name;
  char *signature;
  void *fnPtr;
} JNINativeMethod;

struct JNINativeInterface {
  void *reserved0;
  void *reserved1;
  void *reserved2;
  void *reserved3;
  jint (JNICALL *GetVersion)(JNIEnv*);
  jclass (JNICALL *DefineClass)(JNIEnv*,const char*,jobject,const jbyte*,jsize);
  jclass (JNICALL *FindClass)(JNIEnv*,const char*);
  jmethodID (JNICALL *FromReflectedMethod)(JNIEnv*,jobject);
  jfieldID (JNICALL *FromReflectedField)(JNIEnv*,jobject);
  jobject (JNICALL *ToReflectedMethod)(JNIEnv*,jclass,jmethodID);
  jclass (JNICALL *GetSuperclass)(JNIEnv*,jclass);
  jboolean (JNICALL *IsAssignableFrom)(JNIEnv*,jclass,jclass);
  jobject (JNICALL *ToReflectedField)(JNIEnv*,jclass,jfieldID);
  jint (JNICALL *Throw)(JNIEnv*,jthrowable);
  jint (JNICALL *ThrowNew)(JNIEnv*,jclass,const char*);
  jthrowable (JNICALL *ExceptionOccurred)(JNIEnv*);
  void (JNICALL *ExceptionDescribe)(JNIEnv*);
  void (JNICALL *ExceptionClear)(JNIEnv*);
  void (JNICALL *FatalError)(JNIEnv*,const char*);
  jint (JNICALL *PushLocalFrame)(JNIEnv*,jint);
  jobject (JNICALL *PopLocalFrame)(JNIEnv*,jobject);
  jobject (JNICALL *NewGlobalRef)(JNIEnv*,jobject);
  void (JNICALL *DeleteGlobalRef)(JNIEnv*,jobject);
  void (JNICALL *DeleteLocalRef)(JNIEnv*,jobject);
  jboolean (JNICALL *IsSameObject)(JNIEnv*,jobject,jobject);
  jobject (JNICALL *NewLocalRef)(JNIEnv*,jobject);
  jint (JNICALL *EnsureLocalCapacity)(JNIEnv*,jint);
  jobject (JNICALL *AllocObject)(JNIEnv*,jclass);
  jobject (JNICALL *NewObject)(JNIEnv*,jclass,jmethodID,...);
  jobject (JNICALL *NewObjectV)(JNIEnv*,jclass,jmethodID,va_list);
  jobject (JNICALL *NewObjectA)(JNIEnv*,jclass,jmethodID,jvalue*);
  jclass (JNICALL *GetObjectClass)(JNIEnv*,jobject);
  jboolean (JNICALL *IsInstanceOf)(JNIEnv*,jobject,jclass);
  jmethodID (JNICALL *GetMethodID)(JNIEnv*,jclass,const char*,const char*);
  jobject (JNICALL *CallObjectMethod)(JNIEnv*,jobject,jmethodID,...);
  jobject (JNICALL *CallObjectMethodV)(JNIEnv*,jobject,jmethodID,va_list);
  jobject (JNICALL *CallObjectMethodA)(JNIEnv*,jobject,jmethodID,jvalue*);
  jboolean (JNICALL *CallBooleanMethod)(JNIEnv*,jobject,jmethodID,...);
  jboolean (JNICALL *CallBooleanMethodV)(JNIEnv*,jobject,jmethodID,va_list);
  jboolean (JNICALL *CallBooleanMethodA)(JNIEnv*,jobject,jmethodID,jvalue*);
  jbyte (JNICALL *CallByteMethod)(JNIEnv*,jobject,jmethodID,...);
  jbyte (JNICALL *CallByteMethodV)(JNIEnv*,jobject,jmethodID,va_list);
  jbyte (JNICALL *CallByteMethodA)(JNIEnv*,jobject,jmethodID,jvalue*);
  jchar (JNICALL *CallCharMethod)(JNIEnv*,jobject,jmethodID,...);
  jchar (JNICALL *CallCharMethodV)(JNIEnv*,jobject,jmethodID,va_list);
  jchar (JNICALL *CallCharMethodA)(JNIEnv*,jobject,jmethodID,jvalue*);
  jshort (JNICALL *CallShortMethod)(JNIEnv*,jobject,jmethodID,...);
  jshort (JNICALL *CallShortMethodV)(JNIEnv*,jobject,jmethodID,va_list);
  jshort (JNICALL *CallShortMethodA)(JNIEnv*,jobject,jmethodID,jvalue*);
  jint (JNICALL *CallIntMethod)(JNIEnv*,jobject,jmethodID,...);
  jint (JNICALL *CallIntMethodV)(JNIEnv*,jobject,jmethodID,va_list);
  jint (JNICALL *CallIntMethodA)(JNIEnv*,jobject,jmethodID,jvalue*);
  jlong (JNICALL *CallLongMethod)(JNIEnv*,jobject,jmethodID,...);
  jlong (JNICALL *CallLongMethodV)(JNIEnv*,jobject,jmethodID,va_list);
  jlong (JNICALL *CallLongMethodA)(JNIEnv*,jobject,jmethodID,jvalue*);
  jfloat (JNICALL *CallFloatMethod)(JNIEnv*,jobject,jmethodID,...);
  jfloat (JNICALL *CallFloatMethodV)(JNIEnv*,jobject,jmethodID,va_list);
  jfloat (JNICALL *CallFloatMethodA)(JNIEnv*,jobject,jmethodID,jvalue*);
  jdouble (JNICALL *CallDoubleMethod)(JNIEnv*,jobject,jmethodID,...);
  jdouble (JNICALL *CallDoubleMethodV)(JNIEnv*,jobject,jmethodID,va_list);
  jdouble (JNICALL *CallDoubleMethodA)(JNIEnv*,jobject,jmethodID,jvalue*);
  void (JNICALL *CallVoidMethod)(JNIEnv*,jobject,jmethodID,...);
  void (JNICALL *CallVoidMethodV)(JNIEnv*,jobject,jmethodID,va_list);
  void (JNICALL *CallVoidMethodA)(JNIEnv*,jobject,jmethodID,jvalue*);
  jobject (JNICALL *CallNonvirtualObjectMethod)(JNIEnv*,jobject,jclass,jmethodID,...);
  jobject (JNICALL *CallNonvirtualObjectMethodV)(JNIEnv*,jobject,jclass,jmethodID,va_list);
  jobject (JNICALL *CallNonvirtualObjectMethodA)(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
  jboolean (JNICALL *CallNonvirtualBooleanMethod)(JNIEnv*,jobject,jclass,jmethodID,...);
  jboolean (JNICALL *CallNonvirtualBooleanMethodV)(JNIEnv*,jobject,jclass,jmethodID,va_list);
  jboolean (JNICALL *CallNonvirtualBooleanMethodA)(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
  jbyte (JNICALL *CallNonvirtualByteMethod)(JNIEnv*,jobject,jclass,jmethodID,...);
  jbyte (JNICALL *CallNonvirtualByteMethodV)(JNIEnv*,jobject,jclass,jmethodID,va_list);
  jbyte (JNICALL *CallNonvirtualByteMethodA)(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
  jchar (JNICALL *CallNonvirtualCharMethod)(JNIEnv*,jobject,jclass,jmethodID,...);
  jchar (JNICALL *CallNonvirtualCharMethodV)(JNIEnv*,jobject,jclass,jmethodID,va_list);
  jchar (JNICALL *CallNonvirtualCharMethodA)(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
  jshort (JNICALL *CallNonvirtualShortMethod)(JNIEnv*,jobject,jclass,jmethodID,...);
  jshort (JNICALL *CallNonvirtualShortMethodV)(JNIEnv*,jobject,jclass,jmethodID,va_list);
  jshort (JNICALL *CallNonvirtualShortMethodA)(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
  jint (JNICALL *CallNonvirtualIntMethod)(JNIEnv*,jobject,jclass,jmethodID,...);
  jint (JNICALL *CallNonvirtualIntMethodV)(JNIEnv*,jobject,jclass,jmethodID,va_list);
  jint (JNICALL *CallNonvirtualIntMethodA)(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
  jlong (JNICALL *CallNonvirtualLongMethod)(JNIEnv*,jobject,jclass,jmethodID,...);
  jlong (JNICALL *CallNonvirtualLongMethodV)(JNIEnv*,jobject,jclass,jmethodID,va_list);
  jlong (JNICALL *CallNonvirtualLongMethodA)(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
  jfloat (JNICALL *CallNonvirtualFloatMethod)(JNIEnv*,jobject,jclass,jmethodID,...);
  jfloat (JNICALL *CallNonvirtualFloatMethodV)(JNIEnv*,jobject,jclass,jmethodID,va_list);
  jfloat (JNICALL *CallNonvirtualFloatMethodA)(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
  jdouble (JNICALL *CallNonvirtualDoubleMethod)(JNIEnv*,jobject,jclass,jmethodID,...);
  jdouble (JNICALL *CallNonvirtualDoubleMethodV)(JNIEnv*,jobject,jclass,jmethodID,va_list);
  jdouble (JNICALL *CallNonvirtualDoubleMethodA)(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
  void (JNICALL *CallNonvirtualVoidMethod)(JNIEnv*,jobject,jclass,jmethodID,...);
  void (JNICALL *CallNonvirtualVoidMethodV)(JNIEnv*,jobject,jclass,jmethodID,va_list);
  void (JNICALL *CallNonvirtualVoidMethodA)(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
  jfieldID (JNICALL *GetFieldID)(JNIEnv*,jclass,const char*,const char*);
  jobject (JNICALL *GetObjectField)(JNIEnv*,jobject,jfieldID);
  jboolean (JNICALL *GetBooleanField)(JNIEnv*,jobject,jfieldID);
  jbyte (JNICALL *GetByteField)(JNIEnv*,jobject,jfieldID);
  jchar (JNICALL *GetCharField)(JNIEnv*,jobject,jfieldID);
  jshort (JNICALL *GetShortField)(JNIEnv*,jobject,jfieldID);
  jint (JNICALL *GetIntField)(JNIEnv*,jobject,jfieldID);
  jlong (JNICALL *GetLongField)(JNIEnv*,jobject,jfieldID);
  jfloat (JNICALL *GetFloatField)(JNIEnv*,jobject,jfieldID);
  jdouble (JNICALL *GetDoubleField)(JNIEnv*,jobject,jfieldID);
  void (JNICALL *SetObjectField)(JNIEnv*,jobject,jfieldID,jobject);
  void (JNICALL *SetBooleanField)(JNIEnv*,jobject,jfieldID,jboolean);
  void (JNICALL *SetByteField)(JNIEnv*,jobject,jfieldID,jbyte);
  void (JNICALL *SetCharField)(JNIEnv*,jobject,jfieldID,jchar);
  void (JNICALL *SetShortField)(JNIEnv*,jobject,jfieldID,jshort);
  void (JNICALL *SetIntField)(JNIEnv*,jobject,jfieldID,jint);
  void (JNICALL *SetLongField)(JNIEnv*,jobject,jfieldID,jlong);
  void (JNICALL *SetFloatField)(JNIEnv*,jobject,jfieldID,jfloat);
  void (JNICALL *SetDoubleField)(JNIEnv*,jobject,jfieldID,jdouble);
  jmethodID (JNICALL *GetStaticMethodID)(JNIEnv*,jclass,const char*,const char*);
  jobject (JNICALL *CallStaticObjectMethod)(JNIEnv*,jclass,jmethodID,...);
  jobject (JNICALL *CallStaticObjectMethodV)(JNIEnv*,jclass,jmethodID,va_list);
  jobject (JNICALL *CallStaticObjectMethodA)(JNIEnv*,jclass,jmethodID,jvalue*);
  jboolean (JNICALL *CallStaticBooleanMethod)(JNIEnv*,jclass,jmethodID,...);
  jboolean (JNICALL *CallStaticBooleanMethodV)(JNIEnv*,jclass,jmethodID,va_list);
  jboolean (JNICALL *CallStaticBooleanMethodA)(JNIEnv*,jclass,jmethodID,jvalue*);
  jbyte (JNICALL *CallStaticByteMethod)(JNIEnv*,jclass,jmethodID,...);
  jbyte (JNICALL *CallStaticByteMethodV)(JNIEnv*,jclass,jmethodID,va_list);
  jbyte (JNICALL *CallStaticByteMethodA)(JNIEnv*,jclass,jmethodID,jvalue*);
  jchar (JNICALL *CallStaticCharMethod)(JNIEnv*,jclass,jmethodID,...);
  jchar (JNICALL *CallStaticCharMethodV)(JNIEnv*,jclass,jmethodID,va_list);
  jchar (JNICALL *CallStaticCharMethodA)(JNIEnv*,jclass,jmethodID,jvalue*);
  jshort (JNICALL *CallStaticShortMethod)(JNIEnv*,jclass,jmethodID,...);
  jshort (JNICALL *CallStaticShortMethodV)(JNIEnv*,jclass,jmethodID,va_list);
  jshort (JNICALL *CallStaticShortMethodA)(JNIEnv*,jclass,jmethodID,jvalue*);
  jint (JNICALL *CallStaticIntMethod)(JNIEnv*,jclass,jmethodID,...);
  jint (JNICALL *CallStaticIntMethodV)(JNIEnv*,jclass,jmethodID,va_list);
  jint (JNICALL *CallStaticIntMethodA)(JNIEnv*,jclass,jmethodID,jvalue*);
  jlong (JNICALL *CallStaticLongMethod)(JNIEnv*,jclass,jmethodID,...);
  jlong (JNICALL *CallStaticLongMethodV)(JNIEnv*,jclass,jmethodID,va_list);
  jlong (JNICALL *CallStaticLongMethodA)(JNIEnv*,jclass,jmethodID,jvalue*);
  jfloat (JNICALL *CallStaticFloatMethod)(JNIEnv*,jclass,jmethodID,...);
  jfloat (JNICALL *CallStaticFloatMethodV)(JNIEnv*,jclass,jmethodID,va_list);
  jfloat (JNICALL *CallStaticFloatMethodA)(JNIEnv*,jclass,jmethodID,jvalue*);
  jdouble (JNICALL *CallStaticDoubleMethod)(JNIEnv*,jclass,jmethodID,...);
  jdouble (JNICALL *CallStaticDoubleMethodV)(JNIEnv*,jclass,jmethodID,va_list);
  jdouble (JNICALL *CallStaticDoubleMethodA)(JNIEnv*,jclass,jmethodID,jvalue*);
  void (JNICALL *CallStaticVoidMethod)(JNIEnv*,jclass,jmethodID,...);
  void (JNICALL *CallStaticVoidMethodV)(JNIEnv*,jclass,jmethodID,va_list);
  void (JNICALL *CallStaticVoidMethodA)(JNIEnv*,jclass,jmethodID,jvalue*);
  jfieldID (JNICALL *GetStaticFieldID)(JNIEnv*,jclass,const char*,const char*);
  jobject (JNICALL *GetStaticObjectField)(JNIEnv*,jclass,jfieldID);
  jboolean (JNICALL *GetStaticBooleanField)(JNIEnv*,jclass,jfieldID);
  jbyte (JNICALL *GetStaticByteField)(JNIEnv*,jclass,jfieldID);
  jchar (JNICALL *GetStaticCharField)(JNIEnv*,jclass,jfieldID);
  jshort (JNICALL *GetStaticShortField)(JNIEnv*,jclass,jfieldID);
  jint (JNICALL *GetStaticIntField)(JNIEnv*,jclass,jfieldID);
  jlong (JNICALL *GetStaticLongField)(JNIEnv*,jclass,jfieldID);
  jfloat (JNICALL *GetStaticFloatField)(JNIEnv*,jclass,jfieldID);
  jdouble (JNICALL *GetStaticDoubleField)(JNIEnv*,jclass,jfieldID);
  void (JNICALL *SetStaticObjectField)(JNIEnv*,jclass,jfieldID,jobject);
  void (JNICALL *SetStaticBooleanField)(JNIEnv*,jclass,jfieldID,jboolean);
  void (JNICALL *SetStaticByteField)(JNIEnv*,jclass,jfieldID,jbyte);
  void (JNICALL *SetStaticCharField)(JNIEnv*,jclass,jfieldID,jchar);
  void (JNICALL *SetStaticShortField)(JNIEnv*,jclass,jfieldID,jshort);
  void (JNICALL *SetStaticIntField)(JNIEnv*,jclass,jfieldID,jint);
  void (JNICALL *SetStaticLongField)(JNIEnv*,jclass,jfieldID,jlong);
  void (JNICALL *SetStaticFloatField)(JNIEnv*,jclass,jfieldID,jfloat);
  void (JNICALL *SetStaticDoubleField)(JNIEnv*,jclass,jfieldID,jdouble);
  jstring (JNICALL *NewString)(JNIEnv*,const jchar*,jsize);
  jsize (JNICALL *GetStringLength)(JNIEnv*,jstring);
  const jchar *(JNICALL *GetStringChars)(JNIEnv*,jstring,jboolean*);
  void (JNICALL *ReleaseStringChars)(JNIEnv*,jstring,const jchar*);
  jstring (JNICALL *NewStringUTF)(JNIEnv*,const char*);
  jsize (JNICALL *GetStringUTFLength)(JNIEnv*,jstring);
  const char *(JNICALL *GetStringUTFChars)(JNIEnv*,jstring,jboolean*);
  void (JNICALL *ReleaseStringUTFChars)(JNIEnv*,jstring,const char*);
  jsize (JNICALL *GetArrayLength)(JNIEnv*,jarray);
  jobjectArray (JNICALL *NewObjectArray)(JNIEnv*,jsize,jclass,jobject);
  jobject (JNICALL *GetObjectArrayElement)(JNIEnv*,jobjectArray,jsize);
  void (JNICALL *SetObjectArrayElement)(JNIEnv*,jobjectArray,jsize,jobject);
  jbooleanArray (JNICALL *NewBooleanArray)(JNIEnv*,jsize);
  jbyteArray (JNICALL *NewByteArray)(JNIEnv*,jsize);
  jcharArray (JNICALL *NewCharArray)(JNIEnv*,jsize);
  jshortArray (JNICALL *NewShortArray)(JNIEnv*,jsize);
  jintArray (JNICALL *NewIntArray)(JNIEnv*,jsize);
  jlongArray (JNICALL *NewLongArray)(JNIEnv*,jsize);
  jfloatArray (JNICALL *NewFloatArray)(JNIEnv*,jsize);
  jdoubleArray (JNICALL *NewDoubleArray)(JNIEnv*,jsize);
  jboolean *(JNICALL *GetBooleanArrayElements)(JNIEnv*,jbooleanArray,jboolean*);
  jbyte *(JNICALL *GetByteArrayElements)(JNIEnv*,jbyteArray,jboolean*);
  jchar *(JNICALL *GetCharArrayElements)(JNIEnv*,jcharArray,jboolean*);
  jshort *(JNICALL *GetShortArrayElements)(JNIEnv*,jshortArray,jboolean*);
  jint *(JNICALL *GetIntArrayElements)(JNIEnv*,jintArray,jboolean*);
  jlong *(JNICALL *GetLongArrayElements)(JNIEnv*,jlongArray,jboolean*);
  jfloat *(JNICALL *GetFloatArrayElements)(JNIEnv*,jfloatArray,jboolean*);
  jdouble *(JNICALL *GetDoubleArrayElements)(JNIEnv*,jdoubleArray,jboolean*);
  void (JNICALL *ReleaseBooleanArrayElements)(JNIEnv*,jbooleanArray,jboolean*,jint);
  void (JNICALL *ReleaseByteArrayElements)(JNIEnv*,jbyteArray,jbyte*,jint);
  void (JNICALL *ReleaseCharArrayElements)(JNIEnv*,jcharArray,jchar*,jint);
  void (JNICALL *ReleaseShortArrayElements)(JNIEnv*,jshortArray,jshort*,jint);
  void (JNICALL *ReleaseIntArrayElements)(JNIEnv*,jintArray,jint*,jint);
  void (JNICALL *ReleaseLongArrayElements)(JNIEnv*,jlongArray,jlong*,jint);
  void (JNICALL *ReleaseFloatArrayElements)(JNIEnv*,jfloatArray,jfloat*,jint);
  void (JNICALL *ReleaseDoubleArrayElements)(JNIEnv*,jdoubleArray,jdouble*,jint);
  void (JNICALL *GetBooleanArrayRegion)(JNIEnv*,jbooleanArray,jsize,jsize,jboolean*);
  void (JNICALL *GetByteArrayRegion)(JNIEnv*,jbyteArray,jsize,jsize,jbyte*);
  void (JNICALL *GetCharArrayRegion)(JNIEnv*,jcharArray,jsize,jsize,jchar*);
  void (JNICALL *GetShortArrayRegion)(JNIEnv*,jshortArray,jsize,jsize,jshort*);
  void (JNICALL *GetIntArrayRegion)(JNIEnv*,jintArray,jsize,jsize,jint*);
  void (JNICALL *GetLongArrayRegion)(JNIEnv*,jlongArray,jsize,jsize,jlong*);
  void (JNICALL *GetFloatArrayRegion)(JNIEnv*,jfloatArray,jsize,jsize,jfloat*);
  void (JNICALL *GetDoubleArrayRegion)(JNIEnv*,jdoubleArray,jsize,jsize,jdouble*);
  void (JNICALL *SetBooleanArrayRegion)(JNIEnv*,jbooleanArray,jsize,jsize,jboolean*);
  void (JNICALL *SetByteArrayRegion)(JNIEnv*,jbyteArray,jsize,jsize,jbyte*);
  void (JNICALL *SetCharArrayRegion)(JNIEnv*,jcharArray,jsize,jsize,jchar*);
  void (JNICALL *SetShortArrayRegion)(JNIEnv*,jshortArray,jsize,jsize,jshort*);
  void (JNICALL *SetIntArrayRegion)(JNIEnv*,jintArray,jsize,jsize,jint*);
  void (JNICALL *SetLongArrayRegion)(JNIEnv*,jlongArray,jsize,jsize,jlong*);
  void (JNICALL *SetFloatArrayRegion)(JNIEnv*,jfloatArray,jsize,jsize,jfloat*);
  void (JNICALL *SetDoubleArrayRegion)(JNIEnv*,jdoubleArray,jsize,jsize,jdouble*);
  jint (JNICALL *RegisterNatives)(JNIEnv*,jclass,const JNINativeMethod*,jint);
  jint (JNICALL *UnregisterNatives)(JNIEnv*,jclass);
  jint (JNICALL *MonitorEnter)(JNIEnv*,jobject);
  jint (JNICALL *MonitorExit)(JNIEnv*,jobject);
  jint (JNICALL *GetJavaVM)(JNIEnv*,JavaVM**);
  void (JNICALL *GetStringRegion)(JNIEnv*,jstring,jsize,jsize,jchar*);
  void (JNICALL *GetStringUTFRegion)(JNIEnv*,jstring,jsize,jsize,char*);
  void *(JNICALL *GetPrimitiveArrayCritical)(JNIEnv*,jarray,jboolean*);
  void (JNICALL *ReleasePrimitiveArrayCritical)(JNIEnv*,jarray,void*,jint);
  const jchar *(JNICALL *GetStringCritical)(JNIEnv*,jstring,jboolean*);
  void (JNICALL *ReleaseStringCritical)(JNIEnv*,jstring,const jchar*);
  jweak (JNICALL *NewWeakGlobalRef)(JNIEnv*,jobject);
  void (JNICALL *DeleteWeakGlobalRef)(JNIEnv*,jweak);
  jboolean (JNICALL *ExceptionCheck)(JNIEnv*);
  jobject (JNICALL *NewDirectByteBuffer)(JNIEnv*,void*,jlong);
  void *(JNICALL *GetDirectBufferAddress)(JNIEnv*,jobject);
  jlong (JNICALL *GetDirectBufferCapacity)(JNIEnv*,jobject);
};

#ifndef _JNI_IMPL
struct JNIEnv {
  const struct JNINativeInterface *functions;
#ifdef __cplusplus

  jint GetVersion() {
    return functions->GetVersion(this);
  }

  jclass DefineClass(const char *p0, jobject p1, const jbyte *p2, jsize p3) {
    return functions->DefineClass(this, p0, p1, p2, p3);
  }

  jclass FindClass(const char *p0) {
    return functions->FindClass(this, p0);
  }

  jmethodID FromReflectedMethod(jobject p0) {
    return functions->FromReflectedMethod(this, p0);
  }

  jfieldID FromReflectedField(jobject p0) {
    return functions->FromReflectedField(this, p0);
  }

  jobject ToReflectedMethod(jclass p0, jmethodID p1) {
    return functions->ToReflectedMethod(this, p0, p1);
  }

  jclass GetSuperclass(jclass p0) {
    return functions->GetSuperclass(this, p0);
  }

  jboolean IsAssignableFrom(jclass p0, jclass p1) {
    return functions->IsAssignableFrom(this, p0, p1);
  }

  jobject ToReflectedField(jclass p0, jfieldID p1) {
    return functions->ToReflectedField(this, p0, p1);
  }

  jint Throw(jthrowable p0) {
    return functions->Throw(this, p0);
  }

  jint ThrowNew(jclass p0, const char *p1) {
    return functions->ThrowNew(this, p0, p1);
  }

  jthrowable ExceptionOccurred() {
    return functions->ExceptionOccurred(this);
  }

  void ExceptionDescribe() {
    functions->ExceptionDescribe(this);
  }

  void ExceptionClear() {
    functions->ExceptionClear(this);
  }

  void FatalError(const char *p0) {
    functions->FatalError(this, p0);
  }

  jint PushLocalFrame(jint p0) {
    return functions->PushLocalFrame(this, p0);
  }

  jobject PopLocalFrame(jobject p0) {
    return functions->PopLocalFrame(this, p0);
  }

  jobject NewGlobalRef(jobject p0) {
    return functions->NewGlobalRef(this, p0);
  }

  void DeleteGlobalRef(jobject p0) {
    functions->DeleteGlobalRef(this, p0);
  }

  void DeleteLocalRef(jobject p0) {
    functions->DeleteLocalRef(this, p0);
  }

  jboolean IsSameObject(jobject p0, jobject p1) {
    return functions->IsSameObject(this, p0, p1);
  }

  jobject NewLocalRef(jobject p0) {
    return functions->NewLocalRef(this, p0);
  }

  jint EnsureLocalCapacity(jint p0) {
    return functions->EnsureLocalCapacity(this, p0);
  }

  jobject AllocObject(jclass p0) {
    return functions->AllocObject(this, p0);
  }

  jobject NewObject(jclass p0, jmethodID p1, ...) {
    va_list l0;
    jobject l1;
    va_start(l0, p1);
    l1 = functions->NewObjectV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jobject NewObjectV(jclass p0, jmethodID p1, va_list p2) {
    return functions->NewObjectV(this, p0, p1, p2);
  }

  jobject NewObjectA(jclass p0, jmethodID p1, jvalue *p2) {
    return functions->NewObjectA(this, p0, p1, p2);
  }

  jclass GetObjectClass(jobject p0) {
    return functions->GetObjectClass(this, p0);
  }

  jboolean IsInstanceOf(jobject p0, jclass p1) {
    return functions->IsInstanceOf(this, p0, p1);
  }

  jmethodID GetMethodID(jclass p0, const char *p1, const char *p2) {
    return functions->GetMethodID(this, p0, p1, p2);
  }

  jobject CallObjectMethod(jobject p0, jmethodID p1, ...) {
    va_list l0;
    jobject l1;
    va_start(l0, p1);
    l1 = functions->CallObjectMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jobject CallObjectMethodV(jobject p0, jmethodID p1, va_list p2) {
    return functions->CallObjectMethodV(this, p0, p1, p2);
  }

  jobject CallObjectMethodA(jobject p0, jmethodID p1, jvalue *p2) {
    return functions->CallObjectMethodA(this, p0, p1, p2);
  }

  jboolean CallBooleanMethod(jobject p0, jmethodID p1, ...) {
    va_list l0;
    jboolean l1;
    va_start(l0, p1);
    l1 = functions->CallBooleanMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jboolean CallBooleanMethodV(jobject p0, jmethodID p1, va_list p2) {
    return functions->CallBooleanMethodV(this, p0, p1, p2);
  }

  jboolean CallBooleanMethodA(jobject p0, jmethodID p1, jvalue *p2) {
    return functions->CallBooleanMethodA(this, p0, p1, p2);
  }

  jbyte CallByteMethod(jobject p0, jmethodID p1, ...) {
    va_list l0;
    jbyte l1;
    va_start(l0, p1);
    l1 = functions->CallByteMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jbyte CallByteMethodV(jobject p0, jmethodID p1, va_list p2) {
    return functions->CallByteMethodV(this, p0, p1, p2);
  }

  jbyte CallByteMethodA(jobject p0, jmethodID p1, jvalue *p2) {
    return functions->CallByteMethodA(this, p0, p1, p2);
  }

  jchar CallCharMethod(jobject p0, jmethodID p1, ...) {
    va_list l0;
    jchar l1;
    va_start(l0, p1);
    l1 = functions->CallCharMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jchar CallCharMethodV(jobject p0, jmethodID p1, va_list p2) {
    return functions->CallCharMethodV(this, p0, p1, p2);
  }

  jchar CallCharMethodA(jobject p0, jmethodID p1, jvalue *p2) {
    return functions->CallCharMethodA(this, p0, p1, p2);
  }

  jshort CallShortMethod(jobject p0, jmethodID p1, ...) {
    va_list l0;
    jshort l1;
    va_start(l0, p1);
    l1 = functions->CallShortMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jshort CallShortMethodV(jobject p0, jmethodID p1, va_list p2) {
    return functions->CallShortMethodV(this, p0, p1, p2);
  }

  jshort CallShortMethodA(jobject p0, jmethodID p1, jvalue *p2) {
    return functions->CallShortMethodA(this, p0, p1, p2);
  }

  jint CallIntMethod(jobject p0, jmethodID p1, ...) {
    va_list l0;
    jint l1;
    va_start(l0, p1);
    l1 = functions->CallIntMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jint CallIntMethodV(jobject p0, jmethodID p1, va_list p2) {
    return functions->CallIntMethodV(this, p0, p1, p2);
  }

  jint CallIntMethodA(jobject p0, jmethodID p1, jvalue *p2) {
    return functions->CallIntMethodA(this, p0, p1, p2);
  }

  jlong CallLongMethod(jobject p0, jmethodID p1, ...) {
    va_list l0;
    jlong l1;
    va_start(l0, p1);
    l1 = functions->CallLongMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jlong CallLongMethodV(jobject p0, jmethodID p1, va_list p2) {
    return functions->CallLongMethodV(this, p0, p1, p2);
  }

  jlong CallLongMethodA(jobject p0, jmethodID p1, jvalue *p2) {
    return functions->CallLongMethodA(this, p0, p1, p2);
  }

  jfloat CallFloatMethod(jobject p0, jmethodID p1, ...) {
    va_list l0;
    jfloat l1;
    va_start(l0, p1);
    l1 = functions->CallFloatMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jfloat CallFloatMethodV(jobject p0, jmethodID p1, va_list p2) {
    return functions->CallFloatMethodV(this, p0, p1, p2);
  }

  jfloat CallFloatMethodA(jobject p0, jmethodID p1, jvalue *p2) {
    return functions->CallFloatMethodA(this, p0, p1, p2);
  }

  jdouble CallDoubleMethod(jobject p0, jmethodID p1, ...) {
    va_list l0;
    jdouble l1;
    va_start(l0, p1);
    l1 = functions->CallDoubleMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jdouble CallDoubleMethodV(jobject p0, jmethodID p1, va_list p2) {
    return functions->CallDoubleMethodV(this, p0, p1, p2);
  }

  jdouble CallDoubleMethodA(jobject p0, jmethodID p1, jvalue *p2) {
    return functions->CallDoubleMethodA(this, p0, p1, p2);
  }

  void CallVoidMethod(jobject p0, jmethodID p1, ...) {
    va_list l0;
    va_start(l0, p1);
    functions->CallVoidMethodV(this, p0, p1, l0);
    va_end(l0);
  }

  void CallVoidMethodV(jobject p0, jmethodID p1, va_list p2) {
    functions->CallVoidMethodV(this, p0, p1, p2);
  }

  void CallVoidMethodA(jobject p0, jmethodID p1, jvalue *p2) {
    functions->CallVoidMethodA(this, p0, p1, p2);
  }

  jobject CallNonvirtualObjectMethod(jobject p0, jclass p1, jmethodID p2, ...) {
    va_list l0;
    jobject l1;
    va_start(l0, p2);
    l1 = functions->CallNonvirtualObjectMethodV(this, p0, p1, p2, l0);
    va_end(l0);
    return l1;
  }

  jobject CallNonvirtualObjectMethodV(jobject p0, jclass p1, jmethodID p2, va_list p3) {
    return functions->CallNonvirtualObjectMethodV(this, p0, p1, p2, p3);
  }

  jobject CallNonvirtualObjectMethodA(jobject p0, jclass p1, jmethodID p2, jvalue *p3) {
    return functions->CallNonvirtualObjectMethodA(this, p0, p1, p2, p3);
  }

  jboolean CallNonvirtualBooleanMethod(jobject p0, jclass p1, jmethodID p2, ...) {
    va_list l0;
    jboolean l1;
    va_start(l0, p2);
    l1 = functions->CallNonvirtualBooleanMethodV(this, p0, p1, p2, l0);
    va_end(l0);
    return l1;
  }

  jboolean CallNonvirtualBooleanMethodV(jobject p0, jclass p1, jmethodID p2, va_list p3) {
    return functions->CallNonvirtualBooleanMethodV(this, p0, p1, p2, p3);
  }

  jboolean CallNonvirtualBooleanMethodA(jobject p0, jclass p1, jmethodID p2, jvalue *p3) {
    return functions->CallNonvirtualBooleanMethodA(this, p0, p1, p2, p3);
  }

  jbyte CallNonvirtualByteMethod(jobject p0, jclass p1, jmethodID p2, ...) {
    va_list l0;
    jbyte l1;
    va_start(l0, p2);
    l1 = functions->CallNonvirtualByteMethodV(this, p0, p1, p2, l0);
    va_end(l0);
    return l1;
  }

  jbyte CallNonvirtualByteMethodV(jobject p0, jclass p1, jmethodID p2, va_list p3) {
    return functions->CallNonvirtualByteMethodV(this, p0, p1, p2, p3);
  }

  jbyte CallNonvirtualByteMethodA(jobject p0, jclass p1, jmethodID p2, jvalue *p3) {
    return functions->CallNonvirtualByteMethodA(this, p0, p1, p2, p3);
  }

  jchar CallNonvirtualCharMethod(jobject p0, jclass p1, jmethodID p2, ...) {
    va_list l0;
    jchar l1;
    va_start(l0, p2);
    l1 = functions->CallNonvirtualCharMethodV(this, p0, p1, p2, l0);
    va_end(l0);
    return l1;
  }

  jchar CallNonvirtualCharMethodV(jobject p0, jclass p1, jmethodID p2, va_list p3) {
    return functions->CallNonvirtualCharMethodV(this, p0, p1, p2, p3);
  }

  jchar CallNonvirtualCharMethodA(jobject p0, jclass p1, jmethodID p2, jvalue *p3) {
    return functions->CallNonvirtualCharMethodA(this, p0, p1, p2, p3);
  }

  jshort CallNonvirtualShortMethod(jobject p0, jclass p1, jmethodID p2, ...) {
    va_list l0;
    jshort l1;
    va_start(l0, p2);
    l1 = functions->CallNonvirtualShortMethodV(this, p0, p1, p2, l0);
    va_end(l0);
    return l1;
  }

  jshort CallNonvirtualShortMethodV(jobject p0, jclass p1, jmethodID p2, va_list p3) {
    return functions->CallNonvirtualShortMethodV(this, p0, p1, p2, p3);
  }

  jshort CallNonvirtualShortMethodA(jobject p0, jclass p1, jmethodID p2, jvalue *p3) {
    return functions->CallNonvirtualShortMethodA(this, p0, p1, p2, p3);
  }

  jint CallNonvirtualIntMethod(jobject p0, jclass p1, jmethodID p2, ...) {
    va_list l0;
    jint l1;
    va_start(l0, p2);
    l1 = functions->CallNonvirtualIntMethodV(this, p0, p1, p2, l0);
    va_end(l0);
    return l1;
  }

  jint CallNonvirtualIntMethodV(jobject p0, jclass p1, jmethodID p2, va_list p3) {
    return functions->CallNonvirtualIntMethodV(this, p0, p1, p2, p3);
  }

  jint CallNonvirtualIntMethodA(jobject p0, jclass p1, jmethodID p2, jvalue *p3) {
    return functions->CallNonvirtualIntMethodA(this, p0, p1, p2, p3);
  }

  jlong CallNonvirtualLongMethod(jobject p0, jclass p1, jmethodID p2, ...) {
    va_list l0;
    jlong l1;
    va_start(l0, p2);
    l1 = functions->CallNonvirtualLongMethodV(this, p0, p1, p2, l0);
    va_end(l0);
    return l1;
  }

  jlong CallNonvirtualLongMethodV(jobject p0, jclass p1, jmethodID p2, va_list p3) {
    return functions->CallNonvirtualLongMethodV(this, p0, p1, p2, p3);
  }

  jlong CallNonvirtualLongMethodA(jobject p0, jclass p1, jmethodID p2, jvalue *p3) {
    return functions->CallNonvirtualLongMethodA(this, p0, p1, p2, p3);
  }

  jfloat CallNonvirtualFloatMethod(jobject p0, jclass p1, jmethodID p2, ...) {
    va_list l0;
    jfloat l1;
    va_start(l0, p2);
    l1 = functions->CallNonvirtualFloatMethodV(this, p0, p1, p2, l0);
    va_end(l0);
    return l1;
  }

  jfloat CallNonvirtualFloatMethodV(jobject p0, jclass p1, jmethodID p2, va_list p3) {
    return functions->CallNonvirtualFloatMethodV(this, p0, p1, p2, p3);
  }

  jfloat CallNonvirtualFloatMethodA(jobject p0, jclass p1, jmethodID p2, jvalue *p3) {
    return functions->CallNonvirtualFloatMethodA(this, p0, p1, p2, p3);
  }

  jdouble CallNonvirtualDoubleMethod(jobject p0, jclass p1, jmethodID p2, ...) {
    va_list l0;
    jdouble l1;
    va_start(l0, p2);
    l1 = functions->CallNonvirtualDoubleMethodV(this, p0, p1, p2, l0);
    va_end(l0);
    return l1;
  }

  jdouble CallNonvirtualDoubleMethodV(jobject p0, jclass p1, jmethodID p2, va_list p3) {
    return functions->CallNonvirtualDoubleMethodV(this, p0, p1, p2, p3);
  }

  jdouble CallNonvirtualDoubleMethodA(jobject p0, jclass p1, jmethodID p2, jvalue *p3) {
    return functions->CallNonvirtualDoubleMethodA(this, p0, p1, p2, p3);
  }

  void CallNonvirtualVoidMethod(jobject p0, jclass p1, jmethodID p2, ...) {
    va_list l0;
    va_start(l0, p2);
    functions->CallNonvirtualVoidMethodV(this, p0, p1, p2, l0);
    va_end(l0);
  }

  void CallNonvirtualVoidMethodV(jobject p0, jclass p1, jmethodID p2, va_list p3) {
    functions->CallNonvirtualVoidMethodV(this, p0, p1, p2, p3);
  }

  void CallNonvirtualVoidMethodA(jobject p0, jclass p1, jmethodID p2, jvalue *p3) {
    functions->CallNonvirtualVoidMethodA(this, p0, p1, p2, p3);
  }

  jfieldID GetFieldID(jclass p0, const char *p1, const char *p2) {
    return functions->GetFieldID(this, p0, p1, p2);
  }

  jobject GetObjectField(jobject p0, jfieldID p1) {
    return functions->GetObjectField(this, p0, p1);
  }

  jboolean GetBooleanField(jobject p0, jfieldID p1) {
    return functions->GetBooleanField(this, p0, p1);
  }

  jbyte GetByteField(jobject p0, jfieldID p1) {
    return functions->GetByteField(this, p0, p1);
  }

  jchar GetCharField(jobject p0, jfieldID p1) {
    return functions->GetCharField(this, p0, p1);
  }

  jshort GetShortField(jobject p0, jfieldID p1) {
    return functions->GetShortField(this, p0, p1);
  }

  jint GetIntField(jobject p0, jfieldID p1) {
    return functions->GetIntField(this, p0, p1);
  }

  jlong GetLongField(jobject p0, jfieldID p1) {
    return functions->GetLongField(this, p0, p1);
  }

  jfloat GetFloatField(jobject p0, jfieldID p1) {
    return functions->GetFloatField(this, p0, p1);
  }

  jdouble GetDoubleField(jobject p0, jfieldID p1) {
    return functions->GetDoubleField(this, p0, p1);
  }

  void SetObjectField(jobject p0, jfieldID p1, jobject p2) {
    functions->SetObjectField(this, p0, p1, p2);
  }

  void SetBooleanField(jobject p0, jfieldID p1, jboolean p2) {
    functions->SetBooleanField(this, p0, p1, p2);
  }

  void SetByteField(jobject p0, jfieldID p1, jbyte p2) {
    functions->SetByteField(this, p0, p1, p2);
  }

  void SetCharField(jobject p0, jfieldID p1, jchar p2) {
    functions->SetCharField(this, p0, p1, p2);
  }

  void SetShortField(jobject p0, jfieldID p1, jshort p2) {
    functions->SetShortField(this, p0, p1, p2);
  }

  void SetIntField(jobject p0, jfieldID p1, jint p2) {
    functions->SetIntField(this, p0, p1, p2);
  }

  void SetLongField(jobject p0, jfieldID p1, jlong p2) {
    functions->SetLongField(this, p0, p1, p2);
  }

  void SetFloatField(jobject p0, jfieldID p1, jfloat p2) {
    functions->SetFloatField(this, p0, p1, p2);
  }

  void SetDoubleField(jobject p0, jfieldID p1, jdouble p2) {
    functions->SetDoubleField(this, p0, p1, p2);
  }

  jmethodID GetStaticMethodID(jclass p0, const char *p1, const char *p2) {
    return functions->GetStaticMethodID(this, p0, p1, p2);
  }

  jobject CallStaticObjectMethod(jclass p0, jmethodID p1, ...) {
    va_list l0;
    jobject l1;
    va_start(l0, p1);
    l1 = functions->CallStaticObjectMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jobject CallStaticObjectMethodV(jclass p0, jmethodID p1, va_list p2) {
    return functions->CallStaticObjectMethodV(this, p0, p1, p2);
  }

  jobject CallStaticObjectMethodA(jclass p0, jmethodID p1, jvalue *p2) {
    return functions->CallStaticObjectMethodA(this, p0, p1, p2);
  }

  jboolean CallStaticBooleanMethod(jclass p0, jmethodID p1, ...) {
    va_list l0;
    jboolean l1;
    va_start(l0, p1);
    l1 = functions->CallStaticBooleanMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jboolean CallStaticBooleanMethodV(jclass p0, jmethodID p1, va_list p2) {
    return functions->CallStaticBooleanMethodV(this, p0, p1, p2);
  }

  jboolean CallStaticBooleanMethodA(jclass p0, jmethodID p1, jvalue *p2) {
    return functions->CallStaticBooleanMethodA(this, p0, p1, p2);
  }

  jbyte CallStaticByteMethod(jclass p0, jmethodID p1, ...) {
    va_list l0;
    jbyte l1;
    va_start(l0, p1);
    l1 = functions->CallStaticByteMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jbyte CallStaticByteMethodV(jclass p0, jmethodID p1, va_list p2) {
    return functions->CallStaticByteMethodV(this, p0, p1, p2);
  }

  jbyte CallStaticByteMethodA(jclass p0, jmethodID p1, jvalue *p2) {
    return functions->CallStaticByteMethodA(this, p0, p1, p2);
  }

  jchar CallStaticCharMethod(jclass p0, jmethodID p1, ...) {
    va_list l0;
    jchar l1;
    va_start(l0, p1);
    l1 = functions->CallStaticCharMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jchar CallStaticCharMethodV(jclass p0, jmethodID p1, va_list p2) {
    return functions->CallStaticCharMethodV(this, p0, p1, p2);
  }

  jchar CallStaticCharMethodA(jclass p0, jmethodID p1, jvalue *p2) {
    return functions->CallStaticCharMethodA(this, p0, p1, p2);
  }

  jshort CallStaticShortMethod(jclass p0, jmethodID p1, ...) {
    va_list l0;
    jshort l1;
    va_start(l0, p1);
    l1 = functions->CallStaticShortMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jshort CallStaticShortMethodV(jclass p0, jmethodID p1, va_list p2) {
    return functions->CallStaticShortMethodV(this, p0, p1, p2);
  }

  jshort CallStaticShortMethodA(jclass p0, jmethodID p1, jvalue *p2) {
    return functions->CallStaticShortMethodA(this, p0, p1, p2);
  }

  jint CallStaticIntMethod(jclass p0, jmethodID p1, ...) {
    va_list l0;
    jint l1;
    va_start(l0, p1);
    l1 = functions->CallStaticIntMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jint CallStaticIntMethodV(jclass p0, jmethodID p1, va_list p2) {
    return functions->CallStaticIntMethodV(this, p0, p1, p2);
  }

  jint CallStaticIntMethodA(jclass p0, jmethodID p1, jvalue *p2) {
    return functions->CallStaticIntMethodA(this, p0, p1, p2);
  }

  jlong CallStaticLongMethod(jclass p0, jmethodID p1, ...) {
    va_list l0;
    jlong l1;
    va_start(l0, p1);
    l1 = functions->CallStaticLongMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jlong CallStaticLongMethodV(jclass p0, jmethodID p1, va_list p2) {
    return functions->CallStaticLongMethodV(this, p0, p1, p2);
  }

  jlong CallStaticLongMethodA(jclass p0, jmethodID p1, jvalue *p2) {
    return functions->CallStaticLongMethodA(this, p0, p1, p2);
  }

  jfloat CallStaticFloatMethod(jclass p0, jmethodID p1, ...) {
    va_list l0;
    jfloat l1;
    va_start(l0, p1);
    l1 = functions->CallStaticFloatMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jfloat CallStaticFloatMethodV(jclass p0, jmethodID p1, va_list p2) {
    return functions->CallStaticFloatMethodV(this, p0, p1, p2);
  }

  jfloat CallStaticFloatMethodA(jclass p0, jmethodID p1, jvalue *p2) {
    return functions->CallStaticFloatMethodA(this, p0, p1, p2);
  }

  jdouble CallStaticDoubleMethod(jclass p0, jmethodID p1, ...) {
    va_list l0;
    jdouble l1;
    va_start(l0, p1);
    l1 = functions->CallStaticDoubleMethodV(this, p0, p1, l0);
    va_end(l0);
    return l1;
  }

  jdouble CallStaticDoubleMethodV(jclass p0, jmethodID p1, va_list p2) {
    return functions->CallStaticDoubleMethodV(this, p0, p1, p2);
  }

  jdouble CallStaticDoubleMethodA(jclass p0, jmethodID p1, jvalue *p2) {
    return functions->CallStaticDoubleMethodA(this, p0, p1, p2);
  }

  void CallStaticVoidMethod(jclass p0, jmethodID p1, ...) {
    va_list l0;
    va_start(l0, p1);
    functions->CallStaticVoidMethodV(this, p0, p1, l0);
    va_end(l0);
  }

  void CallStaticVoidMethodV(jclass p0, jmethodID p1, va_list p2) {
    functions->CallStaticVoidMethodV(this, p0, p1, p2);
  }

  void CallStaticVoidMethodA(jclass p0, jmethodID p1, jvalue *p2) {
    functions->CallStaticVoidMethodA(this, p0, p1, p2);
  }

  jfieldID GetStaticFieldID(jclass p0, const char *p1, const char *p2) {
    return functions->GetStaticFieldID(this, p0, p1, p2);
  }

  jobject GetStaticObjectField(jclass p0, jfieldID p1) {
    return functions->GetStaticObjectField(this, p0, p1);
  }

  jboolean GetStaticBooleanField(jclass p0, jfieldID p1) {
    return functions->GetStaticBooleanField(this, p0, p1);
  }

  jbyte GetStaticByteField(jclass p0, jfieldID p1) {
    return functions->GetStaticByteField(this, p0, p1);
  }

  jchar GetStaticCharField(jclass p0, jfieldID p1) {
    return functions->GetStaticCharField(this, p0, p1);
  }

  jshort GetStaticShortField(jclass p0, jfieldID p1) {
    return functions->GetStaticShortField(this, p0, p1);
  }

  jint GetStaticIntField(jclass p0, jfieldID p1) {
    return functions->GetStaticIntField(this, p0, p1);
  }

  jlong GetStaticLongField(jclass p0, jfieldID p1) {
    return functions->GetStaticLongField(this, p0, p1);
  }

  jfloat GetStaticFloatField(jclass p0, jfieldID p1) {
    return functions->GetStaticFloatField(this, p0, p1);
  }

  jdouble GetStaticDoubleField(jclass p0, jfieldID p1) {
    return functions->GetStaticDoubleField(this, p0, p1);
  }

  void SetStaticObjectField(jclass p0, jfieldID p1, jobject p2) {
    functions->SetStaticObjectField(this, p0, p1, p2);
  }

  void SetStaticBooleanField(jclass p0, jfieldID p1, jboolean p2) {
    functions->SetStaticBooleanField(this, p0, p1, p2);
  }

  void SetStaticByteField(jclass p0, jfieldID p1, jbyte p2) {
    functions->SetStaticByteField(this, p0, p1, p2);
  }

  void SetStaticCharField(jclass p0, jfieldID p1, jchar p2) {
    functions->SetStaticCharField(this, p0, p1, p2);
  }

  void SetStaticShortField(jclass p0, jfieldID p1, jshort p2) {
    functions->SetStaticShortField(this, p0, p1, p2);
  }

  void SetStaticIntField(jclass p0, jfieldID p1, jint p2) {
    functions->SetStaticIntField(this, p0, p1, p2);
  }

  void SetStaticLongField(jclass p0, jfieldID p1, jlong p2) {
    functions->SetStaticLongField(this, p0, p1, p2);
  }

  void SetStaticFloatField(jclass p0, jfieldID p1, jfloat p2) {
    functions->SetStaticFloatField(this, p0, p1, p2);
  }

  void SetStaticDoubleField(jclass p0, jfieldID p1, jdouble p2) {
    functions->SetStaticDoubleField(this, p0, p1, p2);
  }

  jstring NewString(const jchar *p0, jsize p1) {
    return functions->NewString(this, p0, p1);
  }

  jsize GetStringLength(jstring p0) {
    return functions->GetStringLength(this, p0);
  }

  const jchar *GetStringChars(jstring p0, jboolean *p1) {
    return functions->GetStringChars(this, p0, p1);
  }

  void ReleaseStringChars(jstring p0, const jchar *p1) {
    functions->ReleaseStringChars(this, p0, p1);
  }

  jstring NewStringUTF(const char *p0) {
    return functions->NewStringUTF(this, p0);
  }

  jsize GetStringUTFLength(jstring p0) {
    return functions->GetStringUTFLength(this, p0);
  }

  const char *GetStringUTFChars(jstring p0,jboolean *p1) {
    return functions->GetStringUTFChars(this, p0, p1);
  }

  void ReleaseStringUTFChars(jstring p0, const char *p1) {
    functions->ReleaseStringUTFChars(this, p0, p1);
  }

  jsize GetArrayLength(jarray p0) {
    return functions->GetArrayLength(this, p0);
  }

  jobjectArray NewObjectArray(jsize p0, jclass p1, jobject p2) {
    return functions->NewObjectArray(this, p0, p1, p2);
  }

  jobject GetObjectArrayElement(jobjectArray p0, jsize p1) {
    return functions->GetObjectArrayElement(this, p0, p1);
  }

  void SetObjectArrayElement(jobjectArray p0, jsize p1, jobject p2) {
    functions->SetObjectArrayElement(this, p0, p1, p2);
  }

  jbooleanArray NewBooleanArray(jsize p0) {
    return functions->NewBooleanArray(this, p0);
  }

  jbyteArray NewByteArray(jsize p0) {
    return functions->NewByteArray(this, p0);
  }

  jcharArray NewCharArray(jsize p0) {
    return functions->NewCharArray(this, p0);
  }
  
  jshortArray NewShortArray(jsize p0) {
    return functions->NewShortArray(this, p0);
  }

  jintArray NewIntArray(jsize p0) {
    return functions->NewIntArray(this, p0);
  }

  jlongArray NewLongArray(jsize p0) {
    return functions->NewLongArray(this, p0);
  }

  jfloatArray NewFloatArray(jsize p0) {
    return functions->NewFloatArray(this, p0);
  }

  jdoubleArray NewDoubleArray(jsize p0) {
    return functions->NewDoubleArray(this, p0);
  }

  jboolean *GetBooleanArrayElements(jbooleanArray p0, jboolean *p1) {
    return functions->GetBooleanArrayElements(this, p0, p1);
  }

  jbyte *GetByteArrayElements(jbyteArray p0, jboolean *p1) {
    return functions->GetByteArrayElements(this, p0, p1);
  }

  jchar *GetCharArrayElements(jcharArray p0, jboolean *p1) {
    return functions->GetCharArrayElements(this, p0, p1);
  }

  jshort *GetShortArrayElements(jshortArray p0, jboolean *p1) {
    return functions->GetShortArrayElements(this, p0, p1);
  }

  jint *GetIntArrayElements(jintArray p0, jboolean *p1) {
    return functions->GetIntArrayElements(this, p0, p1);
  }

  jlong *GetLongArrayElements(jlongArray p0, jboolean *p1) {
    return functions->GetLongArrayElements(this, p0, p1);
  }

  jfloat *GetFloatArrayElements(jfloatArray p0, jboolean *p1) {
    return functions->GetFloatArrayElements(this, p0, p1);
  }

  jdouble *GetDoubleArrayElements(jdoubleArray p0, jboolean *p1) {
    return functions->GetDoubleArrayElements(this, p0, p1);
  }

  void ReleaseBooleanArrayElements(jbooleanArray p0, jboolean *p1, jint p2) {
    functions->ReleaseBooleanArrayElements(this, p0, p1, p2);
  }

  void ReleaseByteArrayElements(jbyteArray p0, jbyte *p1, jint p2) {
    functions->ReleaseByteArrayElements(this, p0, p1, p2);
  }

  void ReleaseCharArrayElements(jcharArray p0, jchar *p1, jint p2) {
    functions->ReleaseCharArrayElements(this, p0, p1, p2);
  }

  void ReleaseShortArrayElements(jshortArray p0, jshort *p1, jint p2) {
    functions->ReleaseShortArrayElements(this, p0, p1, p2);
  }

  void ReleaseIntArrayElements(jintArray p0, jint *p1, jint p2) {
    functions->ReleaseIntArrayElements(this, p0, p1, p2);
  }

  void ReleaseLongArrayElements(jlongArray p0, jlong *p1, jint p2) {
    functions->ReleaseLongArrayElements(this, p0, p1, p2);
  }

  void ReleaseFloatArrayElements(jfloatArray p0, jfloat *p1, jint p2) {
    functions->ReleaseFloatArrayElements(this, p0, p1, p2);
  }

  void ReleaseDoubleArrayElements(jdoubleArray p0, jdouble *p1, jint p2) {
    functions->ReleaseDoubleArrayElements(this, p0, p1, p2);
  }

  void GetBooleanArrayRegion(jbooleanArray p0, jsize p1, jsize p2, jboolean *p3) {
    functions->GetBooleanArrayRegion(this, p0, p1, p2, p3);
  }

  void GetByteArrayRegion(jbyteArray p0, jsize p1, jsize p2, jbyte *p3) {
    functions->GetByteArrayRegion(this, p0, p1, p2, p3);
  }

  void GetCharArrayRegion(jcharArray p0, jsize p1, jsize p2, jchar *p3) {
    functions->GetCharArrayRegion(this, p0, p1, p2, p3);
  }

  void GetShortArrayRegion(jshortArray p0, jsize p1, jsize p2, jshort *p3) {
    functions->GetShortArrayRegion(this, p0, p1, p2, p3);
  }

  void GetIntArrayRegion(jintArray p0, jsize p1, jsize p2, jint *p3) {
    functions->GetIntArrayRegion(this, p0, p1, p2, p3);
  }

  void GetLongArrayRegion(jlongArray p0, jsize p1, jsize p2, jlong *p3) {
    functions->GetLongArrayRegion(this, p0, p1, p2, p3);
  }

  void GetFloatArrayRegion(jfloatArray p0, jsize p1, jsize p2, jfloat *p3) {
    functions->GetFloatArrayRegion(this, p0, p1, p2, p3);
  }

  void GetDoubleArrayRegion(jdoubleArray p0, jsize p1, jsize p2, jdouble *p3) {
    functions->GetDoubleArrayRegion(this, p0, p1, p2, p3);
  }

  void SetBooleanArrayRegion(jbooleanArray p0, jsize p1, jsize p2, jboolean *p3) {
    functions->SetBooleanArrayRegion(this, p0, p1, p2, p3);
  }

  void SetByteArrayRegion(jbyteArray p0, jsize p1, jsize p2, jbyte *p3) {
    functions->SetByteArrayRegion(this, p0, p1, p2, p3);
  }

  void SetCharArrayRegion(jcharArray p0, jsize p1, jsize p2, jchar *p3) {
    functions->SetCharArrayRegion(this, p0, p1, p2, p3);
  }

  void SetShortArrayRegion(jshortArray p0, jsize p1, jsize p2, jshort *p3) {
    functions->SetShortArrayRegion(this, p0, p1, p2, p3);
  }

  void SetIntArrayRegion(jintArray p0, jsize p1, jsize p2, jint *p3) {
    functions->SetIntArrayRegion(this, p0, p1, p2, p3);
  }

  void SetLongArrayRegion(jlongArray p0, jsize p1, jsize p2, jlong *p3) {
    functions->SetLongArrayRegion(this, p0, p1, p2, p3);
  }

  void SetFloatArrayRegion(jfloatArray p0, jsize p1, jsize p2, jfloat *p3) {
    functions->SetFloatArrayRegion(this, p0, p1, p2, p3);
  }

  void SetDoubleArrayRegion(jdoubleArray p0, jsize p1, jsize p2, jdouble *p3) {
    functions->SetDoubleArrayRegion(this, p0, p1, p2, p3);
  }

  jint RegisterNatives(jclass p0, const JNINativeMethod *p1, jint p2) {
    return functions->RegisterNatives(this, p0, p1, p2);
  }

  jint UnregisterNatives(jclass p0) {
    return functions->UnregisterNatives(this, p0);
  }

  jint MonitorEnter(jobject p0) {
    return functions->MonitorEnter(this, p0);
  }

  jint MonitorExit(jobject p0) {
    return functions->MonitorExit(this, p0);
  }

  jint GetJavaVM(JavaVM **p0) {
    return functions->GetJavaVM(this, p0);
  }

  void GetStringRegion(jstring p0, jsize p1, jsize p2, jchar *p3) {
    functions->GetStringRegion(this, p0, p1, p2, p3);
  }

  void GetStringUTFRegion(jstring p0, jsize p1, jsize p2, char *p3) {
    functions->GetStringUTFRegion(this, p0, p1, p2, p3);
  }

  void *GetPrimitiveArrayCritical(jarray p0, jboolean *p1) {
    return functions->GetPrimitiveArrayCritical(this, p0, p1);
  }

  void ReleasePrimitiveArrayCritical(jarray p0, void *p1, jint p2) {
    functions->ReleasePrimitiveArrayCritical(this, p0, p1, p2);
  }

  const jchar *GetStringCritical(jstring p0, jboolean *p1) {
    return functions->GetStringCritical(this, p0, p1);
  }

  void ReleaseStringCritical(jstring p0, const jchar *p1) {
    functions->ReleaseStringCritical(this, p0, p1);
  }

  jweak NewWeakGlobalRef(jobject p0) {
    return functions->NewWeakGlobalRef(this, p0);
  }

  void DeleteWeakGlobalRef(jweak p0) {
    functions->DeleteWeakGlobalRef(this, p0);
  }

  jboolean ExceptionCheck() {
    return functions->ExceptionCheck(this);
  }

  jobject NewDirectByteBuffer(void *p0, jlong p1) {
    return functions->NewDirectByteBuffer(this, p0, p1);
  }

  void *GetDirectBufferAddress(jobject p0) {
    return functions->GetDirectBufferAddress(this, p0);
  }

  jlong GetDirectBufferCapacity(jobject p0) {
    return functions->GetDirectBufferCapacity(this, p0);
  }

#endif /* __cplusplus */
};
#endif /* _JNI_IMPL */

struct JNIInvokeInterface {
  void *reserved0;
  void *reserved1;
  void *reserved2;
  jint (JNICALL *DestroyJavaVM)(JavaVM*);
  jint (JNICALL *AttachCurrentThread)(JavaVM*,JNIEnv**,void*);
  jint (JNICALL *DetachCurrentThread)(JavaVM*);
  jint (JNICALL *GetEnv)(JavaVM*,JNIEnv**,jint);
};

#ifndef _JNI_IMPL
struct JavaVM {
  const struct JNIInvokeInterface *functions;
#ifdef __cplusplus
  
  jint DestroyJavaVM() {
    return functions->DestroyJavaVM(this);
  }

  jint AttachCurrentThread(JNIEnv **p0,void *p1) {
    return functions->AttachCurrentThread(this, p0, p1);
  }

  jint DetachCurrentThread() {
    return functions->DetachCurrentThread(this);
  }

  jint GetEnv(JNIEnv **p0, jint p1) {
    return functions->GetEnv(this, p0, p1);
  }

#endif /* __cplusplus */
};
#endif /* _JNI_IMPL */

JNIIMPORT
jint JNICALL JNI_GetDefaultJavaVMInitArgs(void*);
JNIIMPORT
jint JNICALL JNI_CreateJavaVM(JavaVM**,JNIEnv**,void*);
JNIIMPORT
jint JNICALL JNI_GetCreatedJavaVMs(JavaVM**,jsize,jsize*);

JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM*,void*);
JNIEXPORT
void JNICALL JNI_OnUnload(JavaVM*,void*);

typedef
struct JavaVMOption {
  char *optionString;
  void *extraInfo;
} JavaVMOption;

typedef
struct JavaVMInitArgs {
   jint version;
   jint nOptions;
   JavaVMOption *options;
   jboolean ignoreUnrecognized;
} JavaVMInitArgs;

typedef
struct JavaVMAttachArgs {
  jint version;
  char *name;
  jobject group;
} JavaVMAttachArgs;

typedef
struct JDK1_1InitArgs {
  jint version;
  char **properties;
  jint checkSource;
  jint nativeStackSize;
  jint javaStackSize;
  jint minHeapSize;
  jint maxHeapSize;
  jint verifyMode;
  char *classpath;
  jint (JNICALL *vfprintf)(FILE*,const char*,va_list);
  void (JNICALL *exit)(jint);
  void (JNICALL *abort)(void);
  jint enableClassGC;
  jint enableVerboseGC;
  jint disableAsyncGC;
  jint reserved0;
  jint reserved1;
  jint reserved2;
} JDK1_1InitArgs;

typedef
struct JDK1_1AttachArgs {
  void *__padding;
} JDK1_1AttachArgs;

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif /* _JNI_H */

