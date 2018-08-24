/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <stdlib.h>
#include <string.h>

#define _JNI_IMPL

#include "arch.h"
#include "hstructs.h"
#include "jnivm.h"

// CALLBACK: java/lang/ArrayIndexOutOfBoundsException <init>                 (I)V
// CALLBACK: java/lang/ArrayStoreException            <init>                 (Ljava/lang/String;)V
// CALLBACK: java/lang/Boolean                        TYPE                   Ljava/lang/Class;
// CALLBACK: java/lang/Byte                           TYPE                   Ljava/lang/Class;
// CALLBACK: java/lang/Character                      TYPE                   Ljava/lang/Class;
// CALLBACK: java/lang/Class                          <init>                 ()V
// CALLBACK: java/lang/ClassLoader                    defineClass            (Ljava/lang/ClassLoader;Ljava/lang/String;[BII)Ljava/lang/Class;
// CALLBACK: java/lang/ClassLoader                    getSystemClassLoader   ()Ljava/lang/ClassLoader;
// CALLBACK: java/lang/ClassLoader                    installBootstrapLoader ()V
// CALLBACK: java/lang/ClassLoader                    installSystemLoader    ()V
// CALLBACK: java/lang/ClassLoader                    putClass               (Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/lang/Class;)V
// CALLBACK: java/lang/Double                         TYPE                   Ljava/lang/Class;
// CALLBACK: java/lang/Float                          TYPE                   Ljava/lang/Class;
// CALLBACK: java/lang/InstantiationException         <init>                 (Ljava/lang/String;)V
// CALLBACK: java/lang/Integer                        TYPE                   Ljava/lang/Class;
// CALLBACK: java/lang/OutOfMemoryError               <init>                 (Ljava/lang/String;)V
// CALLBACK: java/lang/Long                           TYPE                   Ljava/lang/Class;
// CALLBACK: java/lang/MethodText                     <init>                 ()V
// CALLBACK: java/lang/NoSuchFieldError               <init>                 (Ljava/lang/String;)V
// CALLBACK: java/lang/NoSuchMethodError              <init>                 (Ljava/lang/String;)V
// CALLBACK: java/lang/NullPointerException           <init>                 (Ljava/lang/String;)V
// CALLBACK: java/lang/Runtime                        enqueueFinalizable     (Ljava/lang/Object;)V
// CALLBACK: java/lang/Runtime                        runShutdownHooks       ()V
// CALLBACK: java/lang/Short                          TYPE                   Ljava/lang/Class;
// CALLBACK: java/lang/String                         <init>                 ([C)V
// CALLBACK: java/lang/String                         length                 ()I
// CALLBACK: java/lang/String                         getChars               (II[CI)V
// CALLBACK: java/lang/String                         intern                 ()Ljava/lang/String;
// CALLBACK: java/lang/String                         substring              (II)Ljava/lang/String;
// CALLBACK: java/lang/System                         installSecurityManager ()V
// CALLBACK: java/lang/System                         runFinalization        ()V
// CALLBACK: java/lang/System                         setProperty            (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
// CALLBACK: java/lang/Thread                         destroy                ()V
// CALLBACK: java/lang/Thread                         getContextClassLoader  ()Ljava/lang/ClassLoader;
// CALLBACK: java/lang/Thread                         setContextClassLoader  (Ljava/lang/ClassLoader;)V
// CALLBACK: java/lang/ThreadGroup                    <init>                 (Ljava/lang/String;)V
// CALLBACK: java/lang/ThreadGroup                    getSystemGroup         ()Ljava/lang/ThreadGroup;
// CALLBACK: java/lang/Throwable                      printStackTrace        ()V
// CALLBACK: java/lang/Void                           TYPE                   Ljava/lang/Class;
// CALLBACK: java/lang/ref/Reference                  enqueue                ()Z
// CALLBACK: java/lang/reflect/Field                  <init>                 (Ljava/lang/Class;CCLjava/lang/Class;Ljava/lang/String;)V
// CALLBACK: java/lang/reflect/Field                  getIndex               ()I
// CALLBACK: java/lang/reflect/Field                  getDeclaringClass      ()Ljava/lang/Class;
// CALLBACK: java/lang/reflect/Method                 <init>                 (Ljava/lang/Class;CCLjava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Class;)V
// CALLBACK: java/lang/reflect/Method                 getIndex               ()I
// CALLBACK: java/lang/reflect/Method                 getDeclaringClass      ()Ljava/lang/Class;
// CALLBACK: java/lang/reflect/Constructor            <init>                 (Ljava/lang/Class;CC[Ljava/lang/Class;[Ljava/lang/Class;)V
// CALLBACK: java/lang/reflect/Constructor            getIndex               ()I
// CALLBACK: java/lang/reflect/Constructor            getDeclaringClass      ()Ljava/lang/Class;

/* Default stack/heap sizes
 */
#define DEFAULT_NATIVE_STACK_SIZE   (4*1024)
#define DEFAULT_JAVA_STACK_SIZE     (4*1024)
#define DEFAULT_MIN_HEAP_SIZE       (16*1024*1024)
#define DEFAULT_MAX_HEAP_SIZE       (64*1024*1024)

/* Variables that should be provided by Autoconf
 */
#ifndef PACKAGE
#define PACKAGE "jewelvm"
#endif /* PACKAGE */

#ifndef VERSION
#define VERSION "?.??"
#endif /* VERSION */

/* Default properties
 */
#define JAVA_VERSION                  "1.4"
#define JAVA_VENDOR                   "Jewel VM Project"
#define JAVA_VENDOR_URL               "http://www.jewelvm.com/"
#define JAVA_VENDOR_URL_BUG           "http://www.jewelvm.com/bugs.html"
#define JAVA_SPECIFICATION_NAME       "Java Platform API Specification"
#define JAVA_SPECIFICATION_VERSION    "1.4"
#define JAVA_SPECIFICATION_VENDOR     "Sun Microsystems Inc."
#define JAVA_CLASS_VERSION            "48.0"
#define JAVA_VM_NAME                  PACKAGE
#define JAVA_VM_VERSION               VERSION
#define JAVA_VM_INFO                  "stdalone"
#define JAVA_VM_VENDOR                "Jewel VM Project"
#define JAVA_VM_SPECIFICATION_NAME    "Java Virtual Machine Specification"
#define JAVA_VM_SPECIFICATION_VERSION "1.0"
#define JAVA_VM_SPECIFICATION_VENDOR  "Sun Microsystems Inc."
#define JAVA_RUNTIME_NAME             PACKAGE
#define JAVA_RUNTIME_VERSION          VERSION
#define JAVA_COMPILER                 "NONE"

/* forwarded native interface calls */
static jint JNICALL JNI_GetVersion(JNIEnv*);
static jclass JNICALL JNI_DefineClass(JNIEnv*,const char*,jobject,const jbyte*,jsize);
static jclass JNICALL JNI_FindClass(JNIEnv*,const char*);
static jmethodID JNICALL JNI_FromReflectedMethod(JNIEnv*,jobject);
static jfieldID JNICALL JNI_FromReflectedField(JNIEnv*,jobject);
static jobject JNICALL JNI_ToReflectedMethod(JNIEnv*,jclass,jmethodID);
static jclass JNICALL JNI_GetSuperclass(JNIEnv*,jclass);
static jboolean JNICALL JNI_IsAssignableFrom(JNIEnv*,jclass,jclass);
static jobject JNICALL JNI_ToReflectedField(JNIEnv*,jclass,jfieldID);
static jint JNICALL JNI_Throw(JNIEnv*,jthrowable);
static jint JNICALL JNI_ThrowNew(JNIEnv*,jclass,const char*);
static jthrowable JNICALL JNI_ExceptionOccurred(JNIEnv*);
static void JNICALL JNI_ExceptionDescribe(JNIEnv*);
static void JNICALL JNI_ExceptionClear(JNIEnv*);
static void JNICALL JNI_FatalError(JNIEnv*,const char*);
static jint JNICALL JNI_PushLocalFrame(JNIEnv*,jint);
static jobject JNICALL JNI_PopLocalFrame(JNIEnv*,jobject);
static jobject JNICALL JNI_NewGlobalRef(JNIEnv*,jobject);
static void JNICALL JNI_DeleteGlobalRef(JNIEnv*,jobject);
static void JNICALL JNI_DeleteLocalRef(JNIEnv*,jobject);
static jboolean JNICALL JNI_IsSameObject(JNIEnv*,jobject,jobject);
static jobject JNICALL JNI_NewLocalRef(JNIEnv*,jobject);
static jint JNICALL JNI_EnsureLocalCapacity(JNIEnv*,jint);
static jobject JNICALL JNI_AllocObject(JNIEnv*,jclass);
static jobject JNICALL JNI_NewObject(JNIEnv*,jclass,jmethodID,...);
static jobject JNICALL JNI_NewObjectV(JNIEnv*,jclass,jmethodID,va_list);
static jobject JNICALL JNI_NewObjectA(JNIEnv*,jclass,jmethodID,jvalue*);
static jclass JNICALL JNI_GetObjectClass(JNIEnv*,jobject);
static jboolean JNICALL JNI_IsInstanceOf(JNIEnv*,jobject,jclass);
static jmethodID JNICALL JNI_GetMethodID(JNIEnv*,jclass,const char*,const char*);
static jobject JNICALL JNI_CallObjectMethod(JNIEnv*,jobject,jmethodID,...);
static jobject JNICALL JNI_CallObjectMethodV(JNIEnv*,jobject,jmethodID,va_list);
static jobject JNICALL JNI_CallObjectMethodA(JNIEnv*,jobject,jmethodID,jvalue*);
static jboolean JNICALL JNI_CallBooleanMethod(JNIEnv*,jobject,jmethodID,...);
static jboolean JNICALL JNI_CallBooleanMethodV(JNIEnv*,jobject,jmethodID,va_list);
static jboolean JNICALL JNI_CallBooleanMethodA(JNIEnv*,jobject,jmethodID,jvalue*);
static jbyte JNICALL JNI_CallByteMethod(JNIEnv*,jobject,jmethodID,...);
static jbyte JNICALL JNI_CallByteMethodV(JNIEnv*,jobject,jmethodID,va_list);
static jbyte JNICALL JNI_CallByteMethodA(JNIEnv*,jobject,jmethodID,jvalue*);
static jchar JNICALL JNI_CallCharMethod(JNIEnv*,jobject,jmethodID,...);
static jchar JNICALL JNI_CallCharMethodV(JNIEnv*,jobject,jmethodID,va_list);
static jchar JNICALL JNI_CallCharMethodA(JNIEnv*,jobject,jmethodID,jvalue*);
static jshort JNICALL JNI_CallShortMethod(JNIEnv*,jobject,jmethodID,...);
static jshort JNICALL JNI_CallShortMethodV(JNIEnv*,jobject,jmethodID,va_list);
static jshort JNICALL JNI_CallShortMethodA(JNIEnv*,jobject,jmethodID,jvalue*);
static jint JNICALL JNI_CallIntMethod(JNIEnv*,jobject,jmethodID,...);
static jint JNICALL JNI_CallIntMethodV(JNIEnv*,jobject,jmethodID,va_list);
static jint JNICALL JNI_CallIntMethodA(JNIEnv*,jobject,jmethodID,jvalue*);
static jlong JNICALL JNI_CallLongMethod(JNIEnv*,jobject,jmethodID,...);
static jlong JNICALL JNI_CallLongMethodV(JNIEnv*,jobject,jmethodID,va_list);
static jlong JNICALL JNI_CallLongMethodA(JNIEnv*,jobject,jmethodID,jvalue*);
static jfloat JNICALL JNI_CallFloatMethod(JNIEnv*,jobject,jmethodID,...);
static jfloat JNICALL JNI_CallFloatMethodV(JNIEnv*,jobject,jmethodID,va_list);
static jfloat JNICALL JNI_CallFloatMethodA(JNIEnv*,jobject,jmethodID,jvalue*);
static jdouble JNICALL JNI_CallDoubleMethod(JNIEnv*,jobject,jmethodID,...);
static jdouble JNICALL JNI_CallDoubleMethodV(JNIEnv*,jobject,jmethodID,va_list);
static jdouble JNICALL JNI_CallDoubleMethodA(JNIEnv*,jobject,jmethodID,jvalue*);
static void JNICALL JNI_CallVoidMethod(JNIEnv*,jobject,jmethodID, ...);
static void JNICALL JNI_CallVoidMethodV(JNIEnv*,jobject,jmethodID,va_list);
static void JNICALL JNI_CallVoidMethodA(JNIEnv*,jobject,jmethodID,jvalue*);
static jobject JNICALL JNI_CallNonvirtualObjectMethod(JNIEnv*,jobject,jclass,jmethodID,...);
static jobject JNICALL JNI_CallNonvirtualObjectMethodV(JNIEnv*,jobject,jclass,jmethodID,va_list);
static jobject JNICALL JNI_CallNonvirtualObjectMethodA(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
static jboolean JNICALL JNI_CallNonvirtualBooleanMethod(JNIEnv*,jobject,jclass,jmethodID,...);
static jboolean JNICALL JNI_CallNonvirtualBooleanMethodV(JNIEnv*,jobject,jclass,jmethodID,va_list);
static jboolean JNICALL JNI_CallNonvirtualBooleanMethodA(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
static jbyte JNICALL JNI_CallNonvirtualByteMethod(JNIEnv*,jobject,jclass,jmethodID,...);
static jbyte JNICALL JNI_CallNonvirtualByteMethodV(JNIEnv*,jobject,jclass,jmethodID,va_list);
static jbyte JNICALL JNI_CallNonvirtualByteMethodA(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
static jchar JNICALL JNI_CallNonvirtualCharMethod(JNIEnv*,jobject,jclass,jmethodID,...);
static jchar JNICALL JNI_CallNonvirtualCharMethodV(JNIEnv*,jobject,jclass,jmethodID,va_list);
static jchar JNICALL JNI_CallNonvirtualCharMethodA(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
static jshort JNICALL JNI_CallNonvirtualShortMethod(JNIEnv*,jobject,jclass,jmethodID,...);
static jshort JNICALL JNI_CallNonvirtualShortMethodV(JNIEnv*,jobject,jclass,jmethodID,va_list);
static jshort JNICALL JNI_CallNonvirtualShortMethodA(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
static jint JNICALL JNI_CallNonvirtualIntMethod(JNIEnv*,jobject,jclass,jmethodID,...);
static jint JNICALL JNI_CallNonvirtualIntMethodV(JNIEnv*,jobject,jclass,jmethodID,va_list);
static jint JNICALL JNI_CallNonvirtualIntMethodA(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
static jlong JNICALL JNI_CallNonvirtualLongMethod(JNIEnv*,jobject,jclass,jmethodID,...);
static jlong JNICALL JNI_CallNonvirtualLongMethodV(JNIEnv*,jobject,jclass,jmethodID,va_list);
static jlong JNICALL JNI_CallNonvirtualLongMethodA(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
static jfloat JNICALL JNI_CallNonvirtualFloatMethod(JNIEnv*,jobject,jclass,jmethodID,...);
static jfloat JNICALL JNI_CallNonvirtualFloatMethodV(JNIEnv*,jobject,jclass,jmethodID,va_list);
static jfloat JNICALL JNI_CallNonvirtualFloatMethodA(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
static jdouble JNICALL JNI_CallNonvirtualDoubleMethod(JNIEnv*,jobject,jclass,jmethodID,...);
static jdouble JNICALL JNI_CallNonvirtualDoubleMethodV(JNIEnv*,jobject,jclass,jmethodID,va_list);
static jdouble JNICALL JNI_CallNonvirtualDoubleMethodA(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
static void JNICALL JNI_CallNonvirtualVoidMethod(JNIEnv*,jobject,jclass,jmethodID,...);
static void JNICALL JNI_CallNonvirtualVoidMethodV(JNIEnv*,jobject,jclass,jmethodID,va_list);
static void JNICALL JNI_CallNonvirtualVoidMethodA(JNIEnv*,jobject,jclass,jmethodID,jvalue*);
static jfieldID JNICALL JNI_GetFieldID(JNIEnv*,jclass,const char*,const char*);
static jobject JNICALL JNI_GetObjectField(JNIEnv*,jobject,jfieldID);
static jboolean JNICALL JNI_GetBooleanField(JNIEnv*,jobject,jfieldID);
static jbyte JNICALL JNI_GetByteField(JNIEnv*,jobject,jfieldID);
static jchar JNICALL JNI_GetCharField(JNIEnv*,jobject,jfieldID);
static jshort JNICALL JNI_GetShortField(JNIEnv*,jobject,jfieldID);
static jint JNICALL JNI_GetIntField(JNIEnv*,jobject,jfieldID);
static jlong JNICALL JNI_GetLongField(JNIEnv*,jobject,jfieldID);
static jfloat JNICALL JNI_GetFloatField(JNIEnv*,jobject,jfieldID);
static jdouble JNICALL JNI_GetDoubleField(JNIEnv*,jobject,jfieldID);
static void JNICALL JNI_SetObjectField(JNIEnv*,jobject,jfieldID,jobject);
static void JNICALL JNI_SetBooleanField(JNIEnv*,jobject,jfieldID,jboolean);
static void JNICALL JNI_SetByteField(JNIEnv*,jobject,jfieldID,jbyte);
static void JNICALL JNI_SetCharField(JNIEnv*,jobject,jfieldID,jchar);
static void JNICALL JNI_SetShortField(JNIEnv*,jobject,jfieldID,jshort);
static void JNICALL JNI_SetIntField(JNIEnv*,jobject,jfieldID,jint);
static void JNICALL JNI_SetLongField(JNIEnv*,jobject,jfieldID,jlong);
static void JNICALL JNI_SetFloatField(JNIEnv*,jobject,jfieldID,jfloat);
static void JNICALL JNI_SetDoubleField(JNIEnv*,jobject,jfieldID,jdouble);
static jmethodID JNICALL JNI_GetStaticMethodID(JNIEnv*,jclass,const char*,const char*);
static jobject JNICALL JNI_CallStaticObjectMethod(JNIEnv*,jclass,jmethodID,...);
static jobject JNICALL JNI_CallStaticObjectMethodV(JNIEnv*,jclass,jmethodID,va_list);
static jobject JNICALL JNI_CallStaticObjectMethodA(JNIEnv*,jclass,jmethodID,jvalue*);
static jboolean JNICALL JNI_CallStaticBooleanMethod(JNIEnv*,jclass,jmethodID,...);
static jboolean JNICALL JNI_CallStaticBooleanMethodV(JNIEnv*,jclass,jmethodID,va_list);
static jboolean JNICALL JNI_CallStaticBooleanMethodA(JNIEnv*,jclass,jmethodID, jvalue*args);
static jbyte JNICALL JNI_CallStaticByteMethod(JNIEnv*,jclass,jmethodID,...);
static jbyte JNICALL JNI_CallStaticByteMethodV(JNIEnv*,jclass,jmethodID,va_list);
static jbyte JNICALL JNI_CallStaticByteMethodA(JNIEnv*,jclass,jmethodID,jvalue*);
static jchar JNICALL JNI_CallStaticCharMethod(JNIEnv*,jclass,jmethodID,...);
static jchar JNICALL JNI_CallStaticCharMethodV(JNIEnv*,jclass,jmethodID,va_list);
static jchar JNICALL JNI_CallStaticCharMethodA(JNIEnv*,jclass,jmethodID,jvalue*);
static jshort JNICALL JNI_CallStaticShortMethod(JNIEnv*,jclass,jmethodID,...);
static jshort JNICALL JNI_CallStaticShortMethodV(JNIEnv*,jclass,jmethodID,va_list);
static jshort JNICALL JNI_CallStaticShortMethodA(JNIEnv*,jclass,jmethodID,jvalue*);
static jint JNICALL JNI_CallStaticIntMethod(JNIEnv*,jclass,jmethodID,...);
static jint JNICALL JNI_CallStaticIntMethodV(JNIEnv*,jclass,jmethodID,va_list);
static jint JNICALL JNI_CallStaticIntMethodA(JNIEnv*,jclass,jmethodID,jvalue*);
static jlong JNICALL JNI_CallStaticLongMethod(JNIEnv*,jclass,jmethodID,...);
static jlong JNICALL JNI_CallStaticLongMethodV(JNIEnv*,jclass,jmethodID,va_list);
static jlong JNICALL JNI_CallStaticLongMethodA(JNIEnv*,jclass,jmethodID,jvalue*);
static jfloat JNICALL JNI_CallStaticFloatMethod(JNIEnv*,jclass,jmethodID,...);
static jfloat JNICALL JNI_CallStaticFloatMethodV(JNIEnv*,jclass,jmethodID,va_list);
static jfloat JNICALL JNI_CallStaticFloatMethodA(JNIEnv*,jclass,jmethodID,jvalue*);
static jdouble JNICALL JNI_CallStaticDoubleMethod(JNIEnv*,jclass,jmethodID,...);
static jdouble JNICALL JNI_CallStaticDoubleMethodV(JNIEnv*,jclass,jmethodID,va_list);
static jdouble JNICALL JNI_CallStaticDoubleMethodA(JNIEnv*,jclass,jmethodID,jvalue*);
static void JNICALL JNI_CallStaticVoidMethod(JNIEnv*,jclass,jmethodID,...);
static void JNICALL JNI_CallStaticVoidMethodV(JNIEnv*,jclass,jmethodID,va_list);
static void JNICALL JNI_CallStaticVoidMethodA(JNIEnv*,jclass,jmethodID,jvalue*);
static jfieldID JNICALL JNI_GetStaticFieldID(JNIEnv*,jclass,const char*,const char*);
static jobject JNICALL JNI_GetStaticObjectField(JNIEnv*,jclass,jfieldID);
static jboolean JNICALL JNI_GetStaticBooleanField(JNIEnv*,jclass,jfieldID);
static jbyte JNICALL JNI_GetStaticByteField(JNIEnv*,jclass,jfieldID);
static jchar JNICALL JNI_GetStaticCharField(JNIEnv*,jclass,jfieldID);
static jshort JNICALL JNI_GetStaticShortField(JNIEnv*,jclass,jfieldID);
static jint JNICALL JNI_GetStaticIntField(JNIEnv*,jclass,jfieldID);
static jlong JNICALL JNI_GetStaticLongField(JNIEnv*,jclass,jfieldID);
static jfloat JNICALL JNI_GetStaticFloatField(JNIEnv*,jclass,jfieldID);
static jdouble JNICALL JNI_GetStaticDoubleField(JNIEnv*,jclass,jfieldID);
static void JNICALL JNI_SetStaticObjectField(JNIEnv*,jclass,jfieldID,jobject);
static void JNICALL JNI_SetStaticBooleanField(JNIEnv*,jclass,jfieldID,jboolean);
static void JNICALL JNI_SetStaticByteField(JNIEnv*,jclass,jfieldID,jbyte);
static void JNICALL JNI_SetStaticCharField(JNIEnv*,jclass,jfieldID,jchar);
static void JNICALL JNI_SetStaticShortField(JNIEnv*,jclass,jfieldID,jshort);
static void JNICALL JNI_SetStaticIntField(JNIEnv*,jclass,jfieldID,jint);
static void JNICALL JNI_SetStaticLongField(JNIEnv*,jclass,jfieldID,jlong);
static void JNICALL JNI_SetStaticFloatField(JNIEnv*,jclass,jfieldID,jfloat);
static void JNICALL JNI_SetStaticDoubleField(JNIEnv*,jclass,jfieldID,jdouble);
static jstring JNICALL JNI_NewString(JNIEnv*,const jchar*,jsize);
static jsize JNICALL JNI_GetStringLength(JNIEnv*,jstring);
static const jchar *JNICALL JNI_GetStringChars(JNIEnv*,jstring,jboolean*);
static void JNICALL JNI_ReleaseStringChars(JNIEnv*,jstring, const jchar*);
static jstring JNICALL JNI_NewStringUTF(JNIEnv*,const char*);
static jsize JNICALL JNI_GetStringUTFLength(JNIEnv*,jstring);
static const char *JNICALL JNI_GetStringUTFChars(JNIEnv*,jstring,jboolean*);
static void JNICALL JNI_ReleaseStringUTFChars(JNIEnv*,jstring,const char*);
static jsize JNICALL JNI_GetArrayLength(JNIEnv*,jarray);
static jobjectArray JNICALL JNI_NewObjectArray(JNIEnv*,jsize,jclass,jobject);
static jobject JNICALL JNI_GetObjectArrayElement(JNIEnv*,jobjectArray,jsize);
static void JNICALL JNI_SetObjectArrayElement(JNIEnv*,jobjectArray,jsize,jobject);
static jbooleanArray JNICALL JNI_NewBooleanArray(JNIEnv*,jsize);
static jbyteArray JNICALL JNI_NewByteArray(JNIEnv*,jsize);
static jcharArray JNICALL JNI_NewCharArray(JNIEnv*,jsize);
static jshortArray JNICALL JNI_NewShortArray(JNIEnv*,jsize);
static jintArray JNICALL JNI_NewIntArray(JNIEnv*,jsize);
static jlongArray JNICALL JNI_NewLongArray(JNIEnv*,jsize);
static jfloatArray JNICALL JNI_NewFloatArray(JNIEnv*,jsize);
static jdoubleArray JNICALL JNI_NewDoubleArray(JNIEnv*,jsize);
static jboolean *JNICALL JNI_GetBooleanArrayElements(JNIEnv*,jbooleanArray,jboolean*);
static jbyte *JNICALL JNI_GetByteArrayElements(JNIEnv*,jbyteArray,jboolean*);
static jchar *JNICALL JNI_GetCharArrayElements(JNIEnv*,jcharArray,jboolean*);
static jshort *JNICALL JNI_GetShortArrayElements(JNIEnv*,jshortArray,jboolean*);
static jint *JNICALL JNI_GetIntArrayElements(JNIEnv*,jintArray,jboolean*);
static jlong *JNICALL JNI_GetLongArrayElements(JNIEnv*,jlongArray,jboolean*);
static jfloat *JNICALL JNI_GetFloatArrayElements(JNIEnv*,jfloatArray,jboolean*);
static jdouble *JNICALL JNI_GetDoubleArrayElements(JNIEnv*,jdoubleArray,jboolean*);
static void JNICALL JNI_ReleaseBooleanArrayElements(JNIEnv*,jbooleanArray,jboolean*,jint);
static void JNICALL JNI_ReleaseByteArrayElements(JNIEnv*,jbyteArray,jbyte*,jint);
static void JNICALL JNI_ReleaseCharArrayElements(JNIEnv*,jcharArray,jchar*,jint);
static void JNICALL JNI_ReleaseShortArrayElements(JNIEnv*,jshortArray,jshort*,jint);
static void JNICALL JNI_ReleaseIntArrayElements(JNIEnv*,jintArray,jint*,jint);
static void JNICALL JNI_ReleaseLongArrayElements(JNIEnv*,jlongArray,jlong*,jint);
static void JNICALL JNI_ReleaseFloatArrayElements(JNIEnv*,jfloatArray,jfloat*,jint);
static void JNICALL JNI_ReleaseDoubleArrayElements(JNIEnv*,jdoubleArray,jdouble*,jint);
static void JNICALL JNI_GetBooleanArrayRegion(JNIEnv*,jbooleanArray,jsize,jsize,jboolean*);
static void JNICALL JNI_GetByteArrayRegion(JNIEnv*,jbyteArray,jsize,jsize,jbyte*);
static void JNICALL JNI_GetCharArrayRegion(JNIEnv*,jcharArray,jsize,jsize,jchar*);
static void JNICALL JNI_GetShortArrayRegion(JNIEnv*,jshortArray,jsize,jsize,jshort*);
static void JNICALL JNI_GetIntArrayRegion(JNIEnv*,jintArray,jsize,jsize,jint*);
static void JNICALL JNI_GetLongArrayRegion(JNIEnv*,jlongArray,jsize,jsize,jlong*);
static void JNICALL JNI_GetFloatArrayRegion(JNIEnv*,jfloatArray,jsize,jsize,jfloat*);
static void JNICALL JNI_GetDoubleArrayRegion(JNIEnv*,jdoubleArray,jsize,jsize,jdouble*);
static void JNICALL JNI_SetBooleanArrayRegion(JNIEnv*,jbooleanArray,jsize,jsize,jboolean*);
static void JNICALL JNI_SetByteArrayRegion(JNIEnv*,jbyteArray,jsize,jsize,jbyte*);
static void JNICALL JNI_SetCharArrayRegion(JNIEnv*,jcharArray,jsize,jsize,jchar*);
static void JNICALL JNI_SetShortArrayRegion(JNIEnv*,jshortArray,jsize,jsize,jshort*);
static void JNICALL JNI_SetIntArrayRegion(JNIEnv*,jintArray,jsize,jsize,jint*);
static void JNICALL JNI_SetLongArrayRegion(JNIEnv*,jlongArray,jsize,jsize,jlong*);
static void JNICALL JNI_SetFloatArrayRegion(JNIEnv*,jfloatArray,jsize,jsize,jfloat*);
static void JNICALL JNI_SetDoubleArrayRegion(JNIEnv*,jdoubleArray,jsize,jsize,jdouble*);
static jint JNICALL JNI_RegisterNatives(JNIEnv*,jclass,const JNINativeMethod*,jint);
static jint JNICALL JNI_UnregisterNatives(JNIEnv*,jclass);
static jint JNICALL JNI_MonitorEnter(JNIEnv*,jobject);
static jint JNICALL JNI_MonitorExit(JNIEnv*,jobject);
static jint JNICALL JNI_GetJavaVM(JNIEnv*,JavaVM**);
static void JNICALL JNI_GetStringRegion(JNIEnv*,jstring,jsize,jsize,jchar*);
static void JNICALL JNI_GetStringUTFRegion(JNIEnv*,jstring,jsize,jsize,char*);
static void *JNICALL JNI_GetPrimitiveArrayCritical(JNIEnv*,jarray,jboolean*);
static void JNICALL JNI_ReleasePrimitiveArrayCritical(JNIEnv*,jarray,void*,jint);
static const jchar *JNICALL JNI_GetStringCritical(JNIEnv*,jstring,jboolean*);
static void JNICALL JNI_ReleaseStringCritical(JNIEnv*,jstring,const jchar*);
static jweak JNICALL JNI_NewWeakGlobalRef(JNIEnv*,jobject);
static void JNICALL JNI_DeleteWeakGlobalRef(JNIEnv*,jweak);
static jboolean JNICALL JNI_ExceptionCheck(JNIEnv*);

/* native interface */
static
const struct JNINativeInterface nativeInterface = {
  NULL,
  NULL,
  NULL,
  NULL,
  JNI_GetVersion,
  JNI_DefineClass,
  JNI_FindClass,
  JNI_FromReflectedMethod,
  JNI_FromReflectedField,
  JNI_ToReflectedMethod,
  JNI_GetSuperclass,
  JNI_IsAssignableFrom,
  JNI_ToReflectedField,
  JNI_Throw,
  JNI_ThrowNew,
  JNI_ExceptionOccurred,
  JNI_ExceptionDescribe,
  JNI_ExceptionClear,
  JNI_FatalError,
  JNI_PushLocalFrame,
  JNI_PopLocalFrame,
  JNI_NewGlobalRef,
  JNI_DeleteGlobalRef,
  JNI_DeleteLocalRef,
  JNI_IsSameObject,
  JNI_NewLocalRef,
  JNI_EnsureLocalCapacity,
  JNI_AllocObject,
  JNI_NewObject,
  JNI_NewObjectV,
  JNI_NewObjectA,
  JNI_GetObjectClass,
  JNI_IsInstanceOf,
  JNI_GetMethodID,
  JNI_CallObjectMethod,
  JNI_CallObjectMethodV,
  JNI_CallObjectMethodA,
  JNI_CallBooleanMethod,
  JNI_CallBooleanMethodV,
  JNI_CallBooleanMethodA,
  JNI_CallByteMethod,
  JNI_CallByteMethodV,
  JNI_CallByteMethodA,
  JNI_CallCharMethod,
  JNI_CallCharMethodV,
  JNI_CallCharMethodA,
  JNI_CallShortMethod,
  JNI_CallShortMethodV,
  JNI_CallShortMethodA,
  JNI_CallIntMethod,
  JNI_CallIntMethodV,
  JNI_CallIntMethodA,
  JNI_CallLongMethod,
  JNI_CallLongMethodV,
  JNI_CallLongMethodA,
  JNI_CallFloatMethod,
  JNI_CallFloatMethodV,
  JNI_CallFloatMethodA,
  JNI_CallDoubleMethod,
  JNI_CallDoubleMethodV,
  JNI_CallDoubleMethodA,
  JNI_CallVoidMethod,
  JNI_CallVoidMethodV,
  JNI_CallVoidMethodA,
  JNI_CallNonvirtualObjectMethod,
  JNI_CallNonvirtualObjectMethodV,
  JNI_CallNonvirtualObjectMethodA,
  JNI_CallNonvirtualBooleanMethod,
  JNI_CallNonvirtualBooleanMethodV,
  JNI_CallNonvirtualBooleanMethodA,
  JNI_CallNonvirtualByteMethod,
  JNI_CallNonvirtualByteMethodV,
  JNI_CallNonvirtualByteMethodA,
  JNI_CallNonvirtualCharMethod,
  JNI_CallNonvirtualCharMethodV,
  JNI_CallNonvirtualCharMethodA,
  JNI_CallNonvirtualShortMethod,
  JNI_CallNonvirtualShortMethodV,
  JNI_CallNonvirtualShortMethodA,
  JNI_CallNonvirtualIntMethod,
  JNI_CallNonvirtualIntMethodV,
  JNI_CallNonvirtualIntMethodA,
  JNI_CallNonvirtualLongMethod,
  JNI_CallNonvirtualLongMethodV,
  JNI_CallNonvirtualLongMethodA,
  JNI_CallNonvirtualFloatMethod,
  JNI_CallNonvirtualFloatMethodV,
  JNI_CallNonvirtualFloatMethodA,
  JNI_CallNonvirtualDoubleMethod,
  JNI_CallNonvirtualDoubleMethodV,
  JNI_CallNonvirtualDoubleMethodA,
  JNI_CallNonvirtualVoidMethod,
  JNI_CallNonvirtualVoidMethodV,
  JNI_CallNonvirtualVoidMethodA,
  JNI_GetFieldID,
  JNI_GetObjectField,
  JNI_GetBooleanField,
  JNI_GetByteField,
  JNI_GetCharField,
  JNI_GetShortField,
  JNI_GetIntField,
  JNI_GetLongField,
  JNI_GetFloatField,
  JNI_GetDoubleField,
  JNI_SetObjectField,
  JNI_SetBooleanField,
  JNI_SetByteField,
  JNI_SetCharField,
  JNI_SetShortField,
  JNI_SetIntField,
  JNI_SetLongField,
  JNI_SetFloatField,
  JNI_SetDoubleField,
  JNI_GetStaticMethodID,
  JNI_CallStaticObjectMethod,
  JNI_CallStaticObjectMethodV,
  JNI_CallStaticObjectMethodA,
  JNI_CallStaticBooleanMethod,
  JNI_CallStaticBooleanMethodV,
  JNI_CallStaticBooleanMethodA,
  JNI_CallStaticByteMethod,
  JNI_CallStaticByteMethodV,
  JNI_CallStaticByteMethodA,
  JNI_CallStaticCharMethod,
  JNI_CallStaticCharMethodV,
  JNI_CallStaticCharMethodA,
  JNI_CallStaticShortMethod,
  JNI_CallStaticShortMethodV,
  JNI_CallStaticShortMethodA,
  JNI_CallStaticIntMethod,
  JNI_CallStaticIntMethodV,
  JNI_CallStaticIntMethodA,
  JNI_CallStaticLongMethod,
  JNI_CallStaticLongMethodV,
  JNI_CallStaticLongMethodA,
  JNI_CallStaticFloatMethod,
  JNI_CallStaticFloatMethodV,
  JNI_CallStaticFloatMethodA,
  JNI_CallStaticDoubleMethod,
  JNI_CallStaticDoubleMethodV,
  JNI_CallStaticDoubleMethodA,
  JNI_CallStaticVoidMethod,
  JNI_CallStaticVoidMethodV,
  JNI_CallStaticVoidMethodA,
  JNI_GetStaticFieldID,
  JNI_GetStaticObjectField,
  JNI_GetStaticBooleanField,
  JNI_GetStaticByteField,
  JNI_GetStaticCharField,
  JNI_GetStaticShortField,
  JNI_GetStaticIntField,
  JNI_GetStaticLongField,
  JNI_GetStaticFloatField,
  JNI_GetStaticDoubleField,
  JNI_SetStaticObjectField,
  JNI_SetStaticBooleanField,
  JNI_SetStaticByteField,
  JNI_SetStaticCharField,
  JNI_SetStaticShortField,
  JNI_SetStaticIntField,
  JNI_SetStaticLongField,
  JNI_SetStaticFloatField,
  JNI_SetStaticDoubleField,
  JNI_NewString,
  JNI_GetStringLength,
  JNI_GetStringChars,
  JNI_ReleaseStringChars,
  JNI_NewStringUTF,
  JNI_GetStringUTFLength,
  JNI_GetStringUTFChars,
  JNI_ReleaseStringUTFChars,
  JNI_GetArrayLength,
  JNI_NewObjectArray,
  JNI_GetObjectArrayElement,
  JNI_SetObjectArrayElement,
  JNI_NewBooleanArray,
  JNI_NewByteArray,
  JNI_NewCharArray,
  JNI_NewShortArray,
  JNI_NewIntArray,
  JNI_NewLongArray,
  JNI_NewFloatArray,
  JNI_NewDoubleArray,
  JNI_GetBooleanArrayElements,
  JNI_GetByteArrayElements,
  JNI_GetCharArrayElements,
  JNI_GetShortArrayElements,
  JNI_GetIntArrayElements,
  JNI_GetLongArrayElements,
  JNI_GetFloatArrayElements,
  JNI_GetDoubleArrayElements,
  JNI_ReleaseBooleanArrayElements,
  JNI_ReleaseByteArrayElements,
  JNI_ReleaseCharArrayElements,
  JNI_ReleaseShortArrayElements,
  JNI_ReleaseIntArrayElements,
  JNI_ReleaseLongArrayElements,
  JNI_ReleaseFloatArrayElements,
  JNI_ReleaseDoubleArrayElements,
  JNI_GetBooleanArrayRegion,
  JNI_GetByteArrayRegion,
  JNI_GetCharArrayRegion,
  JNI_GetShortArrayRegion,
  JNI_GetIntArrayRegion,
  JNI_GetLongArrayRegion,
  JNI_GetFloatArrayRegion,
  JNI_GetDoubleArrayRegion,
  JNI_SetBooleanArrayRegion,
  JNI_SetByteArrayRegion,
  JNI_SetCharArrayRegion,
  JNI_SetShortArrayRegion,
  JNI_SetIntArrayRegion,
  JNI_SetLongArrayRegion,
  JNI_SetFloatArrayRegion,
  JNI_SetDoubleArrayRegion,
  JNI_RegisterNatives,
  JNI_UnregisterNatives,
  JNI_MonitorEnter,
  JNI_MonitorExit,
  JNI_GetJavaVM,
  JNI_GetStringRegion,
  JNI_GetStringUTFRegion,
  JNI_GetPrimitiveArrayCritical,
  JNI_ReleasePrimitiveArrayCritical,
  JNI_GetStringCritical,
  JNI_ReleaseStringCritical,
  JNI_NewWeakGlobalRef,
  JNI_DeleteWeakGlobalRef,
  JNI_ExceptionCheck,
};

/* forwarded invoke interface calls */
static jint JNICALL JNI_DestroyJavaVM(JavaVM*);
static jint JNICALL JNI_AttachCurrentThread(JavaVM*,JNIEnv**,void*);
static jint JNICALL JNI_DetachCurrentThread(JavaVM*);
static jint JNICALL JNI_GetEnv(JavaVM*,JNIEnv**,jint);

/* invoke interface */
static
const struct JNIInvokeInterface invokeInterface = {
  NULL,
  NULL,
  NULL,
  JNI_DestroyJavaVM,
  JNI_AttachCurrentThread,
  JNI_DetachCurrentThread,
  JNI_GetEnv,
};

static
tls_t tlsJNI;

JNIEnv *CurrentEnv()
{
  return (JNIEnv*)tls_get(&tlsJNI);
}

/* SwitchFromJava
 */
void SwitchFromJava(JNIEnv *env)
{
  mutex_unlock(&env->lock);
}

/* SwitchToJava
 */
void SwitchToJava(JNIEnv *env)
{
  mutex_lock(&env->lock);
  beforeJava();
}

/* - - - - - - - -  end of reviewed code  - - - - - - - - */

static
jsize countParamWords(const char *descriptor)
{
  jsize size;
  jint i;

  size = 0;
  for (i = 1; descriptor[i] != ')'; i++)
    switch (descriptor[i]) {
    case '[':
      while (descriptor[i] == '[')
        i++;
      if (descriptor[i] == 'L')
        while (descriptor[i] != ';')
          i++;
      size++;
      break;
    case 'L':
      while (descriptor[i] != ';')
        i++;
      size++;
      break;
    case 'J': case 'D':
      size += 2;
      break;
    case 'Z': case 'B':
    case 'C': case 'S':
    case 'I': case 'F':
      size++;
    }
  return 4*size;
}

#define va_startj(X,Y)    xva_startj(env, &(X), methodID, Y)
#define va_endj(X)        free((void*)(X))

static
void xva_startj(JNIEnv *env, va_list *list, jmethodID methodID, jvalue *args)
{
  const struct methodInfo *methodInfo;
  jsize size;
  jbyte *params;
  jint index;
  jint i;

  methodInfo = METHOD_INFO(methodID);

  size = countParamWords(methodInfo->descriptor);

  *list = (va_list)malloc(size);
  if (*list == NULL) {
    ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
    return;
  }

  params = (jbyte*)*list;
  index = 0;
  for (i = 1; methodInfo->descriptor[i] != ')'; i++)
    switch (methodInfo->descriptor[i]) {
    case '[':
      while (methodInfo->descriptor[i] == '[')
        i++;
      if (methodInfo->descriptor[i] == 'L')
        while (methodInfo->descriptor[i] != ';')
          i++;
      *(jobject*)params = args[index++].l;
      params += sizeof(jobject);
      break;
    case 'L':
      while (methodInfo->descriptor[i] != ';')
        i++;
      *(jobject*)params = args[index++].l;
      params += sizeof(jobject);
      break;
    case 'J':
      *(jlong*)params = args[index++].j;
      params += sizeof(jlong);
      break;
    case 'D':
      *(jdouble*)params = args[index++].d;
      params += sizeof(jdouble);
      break;
    case 'F':
      *(jfloat*)params = args[index++].f;
      params += sizeof(jfloat);
      break;
    case 'I':
      *(jint*)params = args[index++].i;
      params += sizeof(jint);
      break;
    case 'Z':
      *(jint*)params = args[index++].z;
      params += sizeof(jint);
      break;
    case 'B':
      *(jint*)params = args[index++].b;
      params += sizeof(jint);
      break;
    case 'C':
      *(jint*)params = args[index++].c;
      params += sizeof(jint);
      break;
    case 'S':
      *(jint*)params = args[index++].s;
      params += sizeof(jint);
      break;
    }
}

static
char *GetBootstrapClasspath()
{
  char *libdir;
  int length;
  char *classpath = NULL;
  
  libdir = userLibdir();
  length = strlen(libdir);
  classpath = malloc(length+11+1);
  if (classpath == NULL)
    return NULL;//must not fail
  strcpy(classpath, libdir);
  strcat(classpath, "glasses.jar");
  return classpath;
}

/* - - - - - - - - start of reviewed code - - - - - - - - */

/* EnqueueFinalizable
 */
void EnqueueFinalizable(JNIEnv *env, jobject object)
{
  const struct classInfo *classInfo;
  jclass clazz = NULL;
  jmethodID methodID;

  classInfo = CLASS_INFO(GETCLASS(object));

  clazz = FindClassFromClassLoader(env, classInfo->loader, "java/lang/Runtime");
  if (clazz == NULL)
    goto exit;

  methodID = JNI_GetStaticMethodID(env, clazz, "enqueueFinalizable", "(Ljava/lang/Object;)V");
  if (methodID == NULL)
    goto exit;

  JNI_CallStaticVoidMethod(env, clazz, methodID, object);
  if (env->thrown != NULL)
    goto exit;

exit:

  JNI_DeleteLocalRef(env, clazz);
}

/* RunFinalization
 */
void RunFinalization(JNIEnv *env)
{
  jclass systemClass = NULL;
  jmethodID methodID;

  systemClass = JNI_FindClass(env, "java/lang/System");
  if (systemClass == NULL)
    goto exit;

  methodID = JNI_GetStaticMethodID(env, systemClass, "runFinalization", "()V");
  if (methodID == NULL)
    goto exit;

  JNI_CallStaticVoidMethod(env, systemClass, methodID);
  if (env->thrown != NULL)
    goto exit;

exit:

  JNI_DeleteLocalRef(env, systemClass);
}

/* EnqueueReference
 */
jboolean EnqueueReference(JNIEnv *env, jobject reference)
{
  jmethodID methodID;

  methodID = JNI_GetMethodID(env, GETCLASS(reference), "enqueue", "()Z");
  if (methodID == NULL)
    return JNI_FALSE;

  return JNI_CallBooleanMethod(env, reference, methodID);
}

/* SetSystemProperty
 */
static
void SetSystemProperty(JNIEnv *env, const char *name, const char *value)
{
  jclass systemClass = NULL;
  jmethodID methodID;
  jstring nameString = NULL;
  jstring valueString = NULL;
  jstring oldString = NULL;

  systemClass = JNI_FindClass(env, "java/lang/System");
  if (systemClass == NULL)
    goto exit;

  methodID = JNI_GetStaticMethodID(env, systemClass, "setProperty", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
  if (env->thrown != NULL)
    goto exit;

  nameString = JNI_NewStringUTF(env, name);
  if (nameString == NULL)
    goto exit;

  valueString = JNI_NewStringUTF(env, value);
  if (valueString == NULL)
    goto exit;

  oldString = JNI_CallStaticObjectMethod(env, systemClass, methodID, nameString, valueString);
  if (env->thrown != NULL)
    goto exit;

exit:

  JNI_DeleteLocalRef(env, oldString);
  JNI_DeleteLocalRef(env, valueString);
  JNI_DeleteLocalRef(env, nameString);
  JNI_DeleteLocalRef(env, systemClass);
}

/* AllocObject
 */
jobject AllocObject(JNIEnv *env, jclass clazz)
{
  jobject result;

  GCWIRED(result, heap_alloc_object(&env->jvm->gcheap, clazz));
  return result;
}

/* AllocArray
 */
jarray AllocArray(JNIEnv *env, jclass clazz, jsize length)
{
  jarray result;

  GCWIRED(result, heap_alloc_array(&env->jvm->gcheap, clazz, length));
  return result;
}

/* CloneObject
 */
jobject CloneObject(JNIEnv *env, jobject object)
{
  jobject result;

  GCWIRED(result, heap_clone(&env->jvm->gcheap, object));
  return result;
}

/* - - - - - - - -  end of reviewed code  - - - - - - - - */

/* CallerClass
 */
typedef
struct {
  jsize counter;
  jsize depth;
  jclass clazz;
} CallerClass_data;
static
jboolean CallerClass_func(JNIEnv *env, jclass clazz, jchar index, jint line, void *untyped)
{
  CallerClass_data *data = untyped;

  if (data->counter == data->depth) {
    data->clazz = JNI_NewLocalRef(env, clazz);
    return JNI_TRUE;
  }
  data->counter++;
  return JNI_FALSE;
}
//static
jclass CallerClass(JNIEnv *env, jint depth)
{
  CallerClass_data data;

  data.counter = 0;
  data.depth = depth;
  data.clazz = NULL;
  TraverseStackByTrace(env, CallerClass_func, &data);
  return data.clazz;
}

/* DeriveClass
 */
//static
jclass DeriveClass(JNIEnv *env, jobject loader, jstring name, jbyteArray array, jint start, jint length)
{
  jmethodID methodID;
  jclass machineryClass = NULL;
  jclass clazz = NULL;

  machineryClass = JNI_FindClass(env, "java/lang/Machinery");
  if (machineryClass == NULL)
    goto exit;

  methodID = JNI_GetStaticMethodID(env, machineryClass, "deriveClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;[BII)Ljava/lang/Class;");
  if (methodID == NULL)
    goto exit;

  clazz = JNI_CallStaticObjectMethod(env, machineryClass, methodID, loader, name, array, start, length);
  if (env->thrown != NULL)
    goto exit;

exit:

  JNI_DeleteLocalRef(env, machineryClass);
  return clazz;
}

/* Exit
 */
//static
void Exit(JNIEnv *env, jint status)
{
  JavaVM *jvm;

  jvm = env->jvm;
  if (jvm->exit != NULL)
    jvm->exit(status);
  else
    exit(status);
}

/* TotalMemory
 */
//static
jlong TotalMemory(JNIEnv *env)
{
  return env->jvm->gcheap.totalMemory;
}

/* FreeMemory
 */
//static
jlong FreeMemory(JNIEnv *env)
{
  return env->jvm->gcheap.freeMemory;
}

/* MaxMemory
 */
//static
jlong MaxMemory(JNIEnv *env)
{
  return env->jvm->maxHeapSize;
}

/* CurrentThread
 */
//static
jobject CurrentThread(JNIEnv *env)
{
  if (env->thread == NULL)
    env->functions->FatalError(env, "current thread is null");
  return env->functions->NewLocalRef(env, env->thread);
}

/* MonitorNotify
 */
//static
jint MonitorNotify(JNIEnv *env, jobject object)
{
  if (!mon_owns(&env->jvm->montab, object))
    return JNI_ERR;
  mon_notify(&env->jvm->montab, object);
  return JNI_OK;
}

/* MonitorNotifyAll
 */
//static
jint MonitorNotifyAll(JNIEnv *env, jobject object)
{
  if (!mon_owns(&env->jvm->montab, object))
    return JNI_ERR;
  mon_notify_all(&env->jvm->montab, object);
  return JNI_OK;
}

/* MonitorOwns
 */
//static
jboolean MonitorOwns(JNIEnv *env, jobject object)
{
  return mon_owns(&env->jvm->montab, object);
}

/* TraverseStackByTrace
 */
typedef
struct {
  JNIEnv *env;
  jboolean (*func)(JNIEnv*,jclass,jchar,jint,void*);
  void *data;
} TraverseStackByTrace_data;
static
jboolean TraverseStackByTrace_func(jboolean native, jobject text, void *frame, void *raddr, void *untyped)
{
  TraverseStackByTrace_data *data = untyped;
  const struct textInfo *textInfo;
  const struct inspectorInfo *inspector;
  jint i;
  const struct traceInfo *trace;
  jint j;
  jchar index;
  jint line;

  textInfo = TEXT_INFO(text);
  inspector = textInfo->inspector;
  if (inspector != NULL)
    for (i = 0; inspector[i].raddr != NULL; i++)
      if (inspector[i].raddr == raddr) {
        trace = inspector[i].trace;
        if (trace != NULL)
          for (j = 0; trace[j].clazz != NULL; j++) {
            index = trace[j].index;
            line = trace[j].line;
            if (index == 65535) {
              index = trace[j].line;
              line = -1;
            }
            if (data->func(data->env, trace[j].clazz, index, line, data->data))
              return JNI_TRUE;
          }
        break;
      }
  return JNI_FALSE;
}
//static
jboolean TraverseStackByTrace(JNIEnv *env, jboolean (*func)(JNIEnv*,jclass,jchar,jint,void*), void *data)
{
  TraverseStackByTrace_data data2;

  data2.env = env;
  data2.func = func;
  data2.data = data;
  return TraverseStack(env->callback, TraverseStackByTrace_func, &data2);
}

/* ThreadEntry
 */
static
void JNICALL ThreadEntry(JNIEnv *env)
{
  JavaVM *jvm;
  jmethodID methodID;
  jobject thread;

  jvm = env->jvm;

  /* Put env in TLS */
  tls_set(&tlsJNI, env);

  /* Call run method */
  thread = env->thread;
  if (thread != NULL) {
    methodID = env->functions->GetMethodID(env, GETCLASS(thread), "run", "()V");
    if (methodID != NULL)
      env->functions->CallVoidMethod(env, thread, methodID);
  }

  /* Detach and return */
  jvm->functions->DetachCurrentThread(jvm);
}

/* ThreadStart
 */
//static
void ThreadStart(JNIEnv *env, jobject thread, jint priority, jboolean daemon, jlong stackSize)
{
  jint error;
  JavaVM *jvm;
  JNIEnv *tenv;

  /* Avoids cycle when looking for run()V from the new thread because of context loader */
  LoadMetadata(env, GETCLASS(thread));
  if (env->thrown != NULL)
    return;

  jvm = env->jvm;

  /* Try to allocate space for env */
  tenv = (JNIEnv*)malloc(sizeof(JNIEnv));
  if (tenv == NULL) {
    ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
    return;
  }

  /* Configure env */
  tenv->functions = env->functions;
  tenv->jvm = jvm;
  tenv->gcstate = 0;
  tenv->daemon = daemon;
  tenv->frames = 0;
  tenv->topFrame = NULL;
  tenv->thrown = NULL;
  tenv->thread = thread;
  tenv->callback = NULL;
  mutex_init(&tenv->lock);

  error = env->functions->PushLocalFrame(tenv, 16);
  if (error != JNI_OK) {
    free(tenv);
    ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
    return;
  }
  
  /* Add to jvm */
  mutex_lock(&jvm->lock);
  if (!tenv->daemon)
    jvm->aengels++;
  tenv->previous = NULL;
  tenv->next = jvm->envs;
  if (tenv->next != NULL)
    tenv->next->previous = tenv;
  jvm->envs = tenv;
  mutex_unlock(&jvm->lock); 
  
  error = thread_create(ThreadEntry, tenv);
  if (error != JNI_OK) {
    
    /* Update machine thread list */
    mutex_lock(&jvm->lock);
    if (!env->daemon) {
      jvm->aengels--;
      if (jvm->aengels == 0)
        mutex_notify(&jvm->lock);
    }
    if (env->previous != NULL)
      env->previous->next = env->next;
    else
      jvm->envs = env->next;
    if (env->next != NULL)
      env->next->previous = env->previous;
    env->previous = NULL;
    env->next = NULL;
    mutex_unlock(&jvm->lock);
    
    PopAllLocalFrames(tenv);
    free(tenv);
    
    ThrowByName(env, "java/lang/InternalError", "Unable to start thread");
    return;
  }

}

/* RegisterSystemProperties
 */
static
void RegisterSystemProperties(JNIEnv *env)
{
  jint i;
  static
  struct {
    const char *key;
    const char *value;
  } defaultProperties[] = {
    { "java.version", JAVA_VERSION },
    { "java.vendor", JAVA_VENDOR },
    { "java.vendor.url", JAVA_VENDOR_URL },
    { "java.vendor.url.bug", JAVA_VENDOR_URL_BUG },
    { "java.specification.name", JAVA_SPECIFICATION_NAME },
    { "java.specification.version", JAVA_SPECIFICATION_VERSION },
    { "java.specification.vendor", JAVA_SPECIFICATION_VENDOR },
    { "java.class.version", JAVA_CLASS_VERSION },
    { "java.vm.name", JAVA_VM_NAME },
    { "java.vm.version", JAVA_VM_VERSION },
    { "java.vm.info", JAVA_VM_INFO },
    { "java.vm.vendor", JAVA_VM_VENDOR },
    { "java.vm.specification.name", JAVA_VM_SPECIFICATION_NAME },
    { "java.vm.specification.version", JAVA_VM_SPECIFICATION_VERSION },
    { "java.vm.specification.vendor", JAVA_VM_SPECIFICATION_VENDOR },
    { "java.runtime.name", JAVA_RUNTIME_NAME },
    { "java.runtime.version", JAVA_RUNTIME_VERSION },
    { "java.compiler", JAVA_COMPILER },
    { NULL, NULL },
  };
  
  for (i = 0; defaultProperties[i].key != NULL; i++) {
    SetSystemProperty(env, defaultProperties[i].key, defaultProperties[i].value);
    if (env->thrown != NULL)
      return;
  }
  SetSystemProperty(env, "java.home", userTopdir());
  if (env->thrown != NULL)
    return;
  SetSystemProperty(env, "java.class.path", "");
  if (env->thrown != NULL)
    return;
  SetSystemProperty(env, "java.library.path", userLibpath());
  if (env->thrown != NULL)
    return;
  SetSystemProperty(env, "java.vm.class.path", GetBootstrapClasspath());
  if (env->thrown != NULL)
    return;
  SetSystemProperty(env, "java.vm.library.path", userBindir());
  if (env->thrown != NULL)
    return;
}

/* SearchNative
 */
static
void *SearchNative(JNIEnv *env, jclass clazz, const char *name, const char *descriptor)
{
  const struct classInfo *classInfo;
  jint i;
  jclass cloaderClass = NULL;
  jmethodID methodID;
  char *cname;
  jstring scname = NULL;
  jstring sname = NULL;
  jstring sdesc = NULL;
  void *fnPtr = NULL;

  classInfo = CLASS_INFO(clazz);

  // these are required to be found, will think about it...
  if (classInfo->loader == NULL) {
    if (strcmp(classInfo->name, "java/lang/Thread") == 0 && strcmp(name, "currentThread") == 0 && strcmp(descriptor, "()Ljava/lang/Thread;") == 0)
      fnPtr = linkSymbol("Java_java_lang_Thread_currentThread", 0);
    if (strcmp(classInfo->name, "java/lang/System") == 0 && strcmp(name, "registerSystemProperties") == 0 && strcmp(descriptor, "()V") == 0)
      fnPtr = linkSymbol("Java_java_lang_System_registerSystemProperties", 0);
    if (strcmp(classInfo->name, "java/lang/lang") == 0 && strcmp(name, "identityHashCode") == 0 && strcmp(descriptor, "(Ljava/lang/Object;)I") == 0)
      fnPtr = linkSymbol("Java_java_lang_lang_identityHashCode", 1);
    if (strcmp(classInfo->name, "java/lang/lang") == 0 && strcmp(name, "callerClass") == 0 && strcmp(descriptor, "()Ljava/lang/Class;") == 0)
      fnPtr = linkSymbol("Java_java_lang_lang_callerClass", 0);
    if (strcmp(classInfo->name, "java/lang/lang") == 0 && strcmp(name, "getDefiningLoader") == 0 && strcmp(descriptor, "(Ljava/lang/Class;)Ljava/lang/ClassLoader;") == 0)
      fnPtr = linkSymbol("Java_java_lang_lang_getDefiningLoader", 1);
    if (strcmp(classInfo->name, "java/lang/lang") == 0 && strcmp(name, "load") == 0 && strcmp(descriptor, "(Ljava/lang/String;)J") == 0)
      fnPtr = linkSymbol("Java_java_lang_lang_load", 1);
    if (strcmp(classInfo->name, "java/lang/lang") == 0 && strcmp(name, "findSymbol") == 0 && strcmp(descriptor, "(JLjava/lang/String;I)J") == 0)
      fnPtr = linkSymbol("Java_java_lang_lang_findSymbol", 4);
  }

  if (fnPtr == NULL) {

    cloaderClass = JNI_FindClass(env, "java/lang/ClassLoader");
    if (cloaderClass == NULL)
      goto exit;

    methodID = JNI_GetStaticMethodID(env, cloaderClass, "findNativeMethod", "(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)J");
    if (methodID == NULL)
      goto exit;

    cname = strdup(classInfo->name);
    if (cname == NULL) {
      ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
      goto exit;
    }

    for (i = 0; cname[i] != '\0'; i++)
      if (cname[i] == '/')
        cname[i] = '.';

    scname = JNI_NewStringUTF(env, cname);
    free(cname);
    if (scname == NULL)
      goto exit;

    sname = JNI_NewStringUTF(env, name);
    if (sname == NULL)
      goto exit;

    sdesc = JNI_NewStringUTF(env, descriptor);
    if (sdesc == NULL)
      goto exit;

    fnPtr = (void*)(jint)/*non-portable for 64-bit*/JNI_CallStaticLongMethod(env, cloaderClass, methodID, classInfo->loader, scname, sname, sdesc);
    if (env->thrown != NULL)
      goto exit;

  }

exit:

  JNI_DeleteLocalRef(env, sdesc);
  JNI_DeleteLocalRef(env, sname);
  JNI_DeleteLocalRef(env, scname);
  JNI_DeleteLocalRef(env, cloaderClass);
  return fnPtr;
}

/* ResolveNative
 */
void *ResolveNative(JNIEnv *env, jclass clazz, jint slot)
{
  void *fun;
  const struct classInfo *classInfo;
  const struct reflectInfo *reflect;
  const char *name = NULL;
  const char *descriptor = NULL;
  jsize i;
  jsize j;

  classInfo = CLASS_INFO(clazz);

  fun = classInfo->nativePtrs[slot];
  if (fun == NULL) {

    LoadMetadata(env, clazz);
    if (env->thrown != NULL)
      return NULL;

    reflect = classInfo->reflect;
    j = 0;
    for (i = 0; reflect->methods[i].declaringClass != NULL; i++)
      if ((reflect->methods[i].accessFlags & ACC_NATIVE) != 0) {
        if (j == slot) {
          name = reflect->methods[i].name;
          descriptor = reflect->methods[i].descriptor;
          break;
        }
        j++;
      }

    if (env->jvm->verboseJNI)
      PrintFormatted(env, "[Resolving native method '%s.%s%s']\n", classInfo->name, name, descriptor);

    fun = SearchNative(env, clazz, name, descriptor);
    if (env->thrown != NULL)
      return NULL;

    if (fun == NULL) {
      if (env->jvm->verboseJNI)
        PrintFormatted(env, "[Unresolved native method '%s.%s%s']\n", classInfo->name, name, descriptor);
      ThrowByName(env, "java/lang/UnsatisfiedLinkError", name);
      return NULL;
    }

    classInfo->nativePtrs[slot] = fun;
  }
  return fun;
}

/* LazyResolve
 */
jobject LazyResolve(JNIEnv *env, jobject text, jint level)
{
  const struct textInfo *textInfo;
  jclass machineryClass = NULL;
  jmethodID methodID;
  jobject result = NULL;

  if (env->jvm->verboseClass) {
    textInfo = TEXT_INFO(text);
    PrintFormatted(env, "[Lazy resolution of method '%s[%d]' (level %d)]\n", CLASS_INFO(textInfo->declaringClass)->name, textInfo->index, level);
  }

  machineryClass = JNI_FindClass(env, "java/lang/Machinery");
  if (machineryClass == NULL)
    goto exit;

  methodID = JNI_GetStaticMethodID(env, machineryClass, "lazyResolve", "(Ljava/lang/MethodText;I)Ljava/lang/MethodText;");
  if (methodID == NULL)
    goto exit;

  result = JNI_CallStaticObjectMethod(env, machineryClass, methodID, text, level);
  if (env->thrown != NULL)
    goto exit;

exit:

  JNI_DeleteLocalRef(env, machineryClass);
  return result;
}

/* FindClassFromClassLoader
 */
jclass FindClassFromClassLoader(JNIEnv *env, jobject loader, const char *name)
{
  jclass cloaderClass = NULL;
  jmethodID methodID;
  char *dotted;
  jsize i;
  jstring string = NULL;
  jthrowable thrown = NULL;
  jclass notfoundClass = NULL;
  jclass clazz = NULL;

  // these are required to be found, will think about it...
  if (strcmp(name, "[C") == 0)
    return JNI_NewLocalRef(env, env->jvm->gcheap.CCharArray);
  if (strcmp(name, "java/lang/ClassLoader") == 0)
    return JNI_NewLocalRef(env, env->jvm->gcheap.CClassLoader);
  if (strcmp(name, "java/lang/String") == 0)
    return JNI_NewLocalRef(env, env->jvm->gcheap.CString);
  if (loader == NULL) {
    if (strcmp(name, "java/lang/Thread") == 0)
      return JNI_NewLocalRef(env, env->jvm->gcheap.CThread);
  }

  cloaderClass = JNI_FindClass(env, "java/lang/ClassLoader");
  if (cloaderClass == NULL)
    goto exit;

  methodID = JNI_GetStaticMethodID(env, cloaderClass, "findClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;");
  if (methodID == NULL)
    return NULL;

  dotted = strdup(name);
  if (dotted == NULL) {
    ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
    goto exit;
  }

  for (i = 0; dotted[i] != '\0'; i++)
    if (dotted[i] == '/')
      dotted[i] = '.';

  string = JNI_NewStringUTF(env, dotted);
  free(dotted);
  if (string == NULL)
    goto exit;

  clazz = JNI_CallStaticObjectMethod(env, cloaderClass, methodID, loader, string);
  if (env->thrown != NULL) {
    thrown = JNI_ExceptionOccurred(env);
    JNI_ExceptionClear(env);

    notfoundClass = JNI_FindClass(env, "java/lang/ClassNotFoundException");
    if (notfoundClass == NULL)
      goto exit;

    if (IsSubtypeOf(GETCLASS(thrown), notfoundClass)) {
      ThrowByName(env, "java/lang/NoClassDefFoundError", name);
      goto exit;
    }

    JNI_Throw(env, thrown);
  }
    
  if (clazz == NULL) {
    ThrowByName(env, "java/lang/NullPointerException", NULL);
    goto exit;
  }

exit:

  JNI_DeleteLocalRef(env, notfoundClass);
  JNI_DeleteLocalRef(env, thrown);
  JNI_DeleteLocalRef(env, string);
  JNI_DeleteLocalRef(env, cloaderClass);
  return clazz;
}

/* LinkClass
 */
void LinkClass(JNIEnv *env, jclass clazz)
{
  struct classInfo *classInfo;
  struct classInfo *superInfo;
  struct classInfo *ifaceInfo;
  jclass superclass;
  jclass inter_face;
  jint i;
  jint j;
  jint baseIndex;
  jmethodID methodID;
  jclass machineryClass;
  jobject text;
  jint error;
  jbyte status;

  classInfo = CLASS_INFO(clazz);
  if (classInfo->status != 0)
    return;

  error = JNI_MonitorEnter(env, clazz);
  if (error != JNI_OK) {
    ThrowByName(env, "java/lang/InternalError", "No more locks");
    return;
  }

  if (classInfo->status != 0)
    goto exit;

  if (env->jvm->verboseClass)
    PrintFormatted(env, "[Linking class '%s']\n", classInfo->name);

  superclass = classInfo->superClass;
  if (superclass != NULL) {
    LinkClass(env, superclass);
    if (env->thrown != NULL)
      goto exit;
  }

  for (i = 0; i < classInfo->directImpls; i++) {
    inter_face = classInfo->interfaces[i].clazz;
    LinkClass(env, inter_face);
    if (env->thrown != NULL)
      goto exit;
  }

  mutex_lock(&env->lock);
  for (i = 0; i < classInfo->directImpls; i++) {
    inter_face = classInfo->interfaces[i].clazz;
    baseIndex = classInfo->interfaces[i].baseIndex;
    ifaceInfo = CLASS_INFO(inter_face);
    for (j = 0; j < ifaceInfo->dynamicEntries; j++)
      SETMETHOD(clazz, baseIndex+j, GETMETHOD(inter_face, j));
  }

  if ((classInfo->accessFlags & ACC_INTERFACE) == 0)
    if (superclass != NULL) {
      superInfo = CLASS_INFO(superclass);
      for (j = 0; j < superInfo->dynamicEntries; j++)
        SETMETHOD(clazz, j, GETMETHOD(superclass, j));
    }
  mutex_unlock(&env->lock);

  machineryClass = JNI_FindClass(env, "java/lang/Machinery");
  if (machineryClass == NULL)
    goto exit;

  methodID = JNI_GetStaticMethodID(env, machineryClass, "linkClass", "(Ljava/lang/Class;)V");
  if (methodID == NULL) {
    JNI_DeleteLocalRef(env, machineryClass);
    goto exit;
  }

  JNI_CallStaticVoidMethod(env, machineryClass, methodID, clazz);
  JNI_DeleteLocalRef(env, machineryClass);
  if (env->thrown != NULL)
    goto exit;

  status = 1;

  if (superclass == NULL || CLASS_INFO(superclass)->status == 2) {
    text = GetMethodText(env, clazz, classInfo->dynamicEntries);
    if (text == NULL)
      status = 2;
    JNI_DeleteLocalRef(env, text);
  }

  classInfo->status = status;

exit:

  JNI_MonitorExit(env, clazz);
}

/* InitializeClass
 */
void InitializeClass(JNIEnv *env, jclass clazz)
{
  struct classInfo *classInfo;
  jint error;
  jbyte status;
  jobject text;

  LinkClass(env, clazz);
  if (env->thrown != NULL)
    return;

  classInfo = CLASS_INFO(clazz);

  error = JNI_MonitorEnter(env, clazz);
  if (error != JNI_OK) {
    ThrowByName(env, "java/lang/InternalError", "No more locks");
    return;
  }

  if (thread_equals(classInfo->initThread, thread_current()))
    goto exit2;

  while (classInfo->initThread != THREAD_NONE) {
    MonitorWait(env, clazz, 0);
    if (env->thrown != NULL)
      goto exit2;
  }

  if (classInfo->status == 2)
    goto exit2;

  if (classInfo->status == 3) {
    JNI_MonitorExit(env, clazz);
    ThrowByName(env, "java/lang/NoClassDefFoundError", classInfo->name);
    return;
  }

  classInfo->initThread = thread_current();

  JNI_MonitorExit(env, clazz);
  
  if (env->jvm->verboseClass)
    PrintFormatted(env, "[Initializing class '%s']\n", classInfo->name);

  if (classInfo->superClass != NULL) {
    InitializeClass(env, classInfo->superClass);
    if (env->thrown != NULL) {
      status = 3;
      goto exit1;
    }
  }

  text = GetMethodText(env, clazz, classInfo->dynamicEntries);
  if (text != NULL) {
    if (env->jvm->verboseClass)
      PrintFormatted(env, "[Calling <clinit> for class '%s']\n", classInfo->name);
    SwitchToJava(env);
    JNI_DeleteLocalRef(env, text);
    invokeVoid(env, text, NULL, 0, NULL, env->callback);
    SwitchFromJava(env);
    if (env->jvm->verboseClass)
      PrintFormatted(env, "[Returning from <clinit> of class '%s']\n", classInfo->name);
    if (env->thrown != NULL) {
      status = 3;
      goto exit1;
    }
  }

  status = 2;

exit1:

  JNI_MonitorEnter(env, clazz);
  SetMethodText(env, clazz, classInfo->dynamicEntries, NULL);
  classInfo->status = status;
  classInfo->initThread = THREAD_NONE;
  MonitorNotifyAll(env, clazz);

exit2:

  JNI_MonitorExit(env, clazz);
}

/* LoadMetadata
 */
void LoadMetadata(JNIEnv *env, jclass clazz)
{
  const struct classInfo *classInfo;
  jmethodID methodID;
  jclass machineryClass = NULL;

  classInfo = CLASS_INFO(clazz);
  if (classInfo->reflect == NULL) {

    if (env->jvm->verboseClass)
      PrintFormatted(env, "[Requesting metadata of class '%s']\n", classInfo->name);

    machineryClass = JNI_FindClass(env, "java/lang/Machinery");
    if (machineryClass == NULL)
      goto exit;

    methodID = JNI_GetStaticMethodID(env, machineryClass, "loadMetadata", "(Ljava/lang/Class;)V");
    if (methodID == NULL)
      goto exit;

    JNI_CallStaticVoidMethod(env, machineryClass, methodID, clazz);
    if (env->thrown != NULL)
      goto exit;

  }

exit:

  JNI_DeleteLocalRef(env, machineryClass);
}

/* MonitorWait
 */
jint MonitorWait(JNIEnv *env, jobject object, jlong millis)
{
  jboolean result;

  if (!mon_owns(&env->jvm->montab, object))
    return JNI_ERR;
  if (millis == 0 || millis > (jlong)0x7FFFFFFF)
    result = mon_wait(&env->jvm->montab, object);
  else
    result = mon_timed_wait(&env->jvm->montab, object, millis);
  if (result) {
    ThrowByName(env, "java/lang/InterruptedException", NULL);
    return JNI_ERR;
  }
  return JNI_OK;
}

/* GetMethodText
 */
jobject GetMethodText(JNIEnv *env, jclass clazz, jint index)
{
  jobject text;

  mutex_lock(&env->lock);
  text = JNI_NewLocalRef(env, GETMETHOD(clazz, index));
  mutex_unlock(&env->lock);
  return text;
}

/* SetMethodText
 */
void SetMethodText(JNIEnv *env, jclass clazz, jint index, jobject text)
{
  mutex_lock(&env->lock);
  SETMETHOD(clazz, index, text);
  mutex_unlock(&env->lock);
}

/* NewClass
 */
jclass NewClass(JNIEnv *env, jobject loader, const char *name, jlong version,
                jchar accessFlags, jclass superclass, jchar dimpls, const jclass *interfaces, const jint *baseIndexes,
                jint staticSize, jint staticRefOfs, jchar staticRefs,
                jint instanceSize, jint instanceRefOfs, jchar instanceRefs,
                jint staticEntries, jint dynamicEntries, jchar natives,
                jclass elementClass, jint dimensions)
{
  jclass classClass = NULL;
  jmethodID methodID;
  jclass refClass = NULL;
  jfieldID fieldID;
  jclass prefClass = NULL;
  jclass srefClass = NULL;
  jclass wrefClass = NULL;
  char *classname;
  jbyte gcflags;
  jsize referentOfs;
  jbyte scale;
  jclass result = NULL;


  classClass = JNI_FindClass(env, "java/lang/Class");
  if (classClass == NULL)
    goto exit;

  methodID = JNI_GetMethodID(env, classClass, "<init>", "()V");
  if (methodID == NULL)
    goto exit;

  refClass = JNI_FindClass(env, "java/lang/ref/Reference");
  if (refClass == NULL)
    goto exit;

  fieldID = JNI_GetFieldID(env, refClass, "referent", "Ljava/lang/Object;");
  if (fieldID == NULL)
    goto exit;

  prefClass = JNI_FindClass(env, "java/lang/ref/PhantomReference");
  if (prefClass == NULL)
    goto exit;

  srefClass = JNI_FindClass(env, "java/lang/ref/SoftReference");
  if (srefClass == NULL)
    goto exit;

  wrefClass = JNI_FindClass(env, "java/lang/ref/WeakReference");
  if (wrefClass == NULL)
    goto exit;

  classname = strdup(name);
  if (classname == NULL) {
    ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
    goto exit;
  }

  gcflags = 0x00;
  if (superclass != NULL)
    if (IsSubtypeOf(superclass, refClass)) {
      if (IsSubtypeOf(superclass, srefClass))
        gcflags |= 0x70;
      else if (IsSubtypeOf(superclass, wrefClass))
        gcflags |= 0x30;
      else if (IsSubtypeOf(superclass, prefClass))
        gcflags |= 0x10;
    }
  referentOfs = FIELD_INFO(fieldID)->offset;

  scale = 0;
  if (dimensions > 0)
    switch (classname[1]) {
    case '[':
    case 'L': scale = sizeof(jobject); break;
    case 'Z': scale = sizeof(jboolean); break;
    case 'C': scale = sizeof(jchar); break;
    case 'B': scale = sizeof(jbyte); break;
    case 'S': scale = sizeof(jshort); break;
    case 'I': scale = sizeof(jint); break;
    case 'J': scale = sizeof(jlong); break;
    case 'F': scale = sizeof(jfloat); break;
    case 'D': scale = sizeof(jdouble); break;
    }

  GCWIRED(result, heap_alloc_class(&env->jvm->gcheap, classClass, loader, classname, version, accessFlags, gcflags, referentOfs, superclass, interfaces, baseIndexes, dimpls,
                                   staticSize, staticRefOfs, staticRefs, instanceSize, instanceRefOfs, instanceRefs,
                                   staticEntries, dynamicEntries, natives, elementClass, dimensions, scale));
  if (result == NULL)
    goto exit;

  JNI_CallNonvirtualVoidMethod(env, result, classClass, methodID);
  if (env->thrown != NULL) {
    result = NULL;
    goto exit;
  }

exit:

  JNI_DeleteLocalRef(env, wrefClass);
  JNI_DeleteLocalRef(env, srefClass);
  JNI_DeleteLocalRef(env, prefClass);
  JNI_DeleteLocalRef(env, refClass);
  JNI_DeleteLocalRef(env, classClass);
  return result;
}

/* - - - - - - - - start of reviewed code - - - - - - - - */

/* NewMethodText
 */
jobject NewMethodText(JNIEnv *env, jsize size)
{
  jclass textClass;
  jmethodID methodID;
  jobject result;

  textClass = JNI_FindClass(env, "java/lang/MethodText");
  if (textClass == NULL)
    return NULL;

  methodID = JNI_GetMethodID(env, textClass, "<init>", "()V");
  if (methodID == NULL) {
    JNI_DeleteLocalRef(env, textClass);
    return NULL;
  }

  GCWIRED(result, heap_alloc_method_text(&env->jvm->gcheap, textClass, size));
  if (result == NULL) {
    JNI_DeleteLocalRef(env, textClass);
    return NULL;
  }

  JNI_CallNonvirtualVoidMethod(env, result, textClass, methodID);
  if (env->thrown != NULL) {
    JNI_DeleteLocalRef(env, result);
    JNI_DeleteLocalRef(env, textClass);
    return NULL;
  }

  JNI_DeleteLocalRef(env, textClass);
  return result;
}

/* PrepareMethodCall
 */
static
jobject PrepareMethodCall(JNIEnv *env, jobject object, jmethodID methodID)
{
  const struct methodInfo *methodInfo;
  const struct classInfo *classInfo;
  jclass clazz;
  jobject text;

  if (env->thrown != NULL)
    return NULL;
  if (object == NULL) {
    ThrowByName(env, "java/lang/NullPointerException", NULL);
    return NULL;
  }
  methodInfo = METHOD_INFO(methodID);
  classInfo = CLASS_INFO(methodInfo->declaringClass);
  if ((classInfo->accessFlags & ACC_INTERFACE) == 0) {
    clazz = methodInfo->declaringClass;
    if (methodInfo->index < classInfo->dynamicEntries)
      clazz = GETCLASS(object);
    text = GETMETHOD(clazz, methodInfo->index);
  } else {
    text = IMLookup(GETCLASS(object), methodInfo->declaringClass, methodInfo->index);
    if (text == NULL) {
      ThrowByName(env, "java/lang/IncompatibleClassChangeError", NULL);
      return NULL;
    }
  }
  return JNI_NewLocalRef(env, text);
}

/* PrepareNonvirtualMethodCall
 */
static
jobject PrepareNonvirtualMethodCall(JNIEnv *env, jobject object, jmethodID methodID)
{
  const struct methodInfo *methodInfo;
  jobject text;

  if (env->thrown != NULL)
    return NULL;
  if (object == NULL) {
    ThrowByName(env, "java/lang/NullPointerException", NULL);
    return NULL;
  }
  methodInfo = METHOD_INFO(methodID);
  text = GETMETHOD(methodInfo->declaringClass, methodInfo->index);
  return JNI_NewLocalRef(env, text);
}

/* PrepareStaticMethodCall
 */
static
jobject PrepareStaticMethodCall(JNIEnv *env, jmethodID methodID)
{
  const struct methodInfo *methodInfo;
  jobject text;

  if (env->thrown != NULL)
    return NULL;
  methodInfo = METHOD_INFO(methodID);
  text = GETMETHOD(methodInfo->declaringClass, methodInfo->index);
  return JNI_NewLocalRef(env, text);
}

/* GetPrimitiveClass
 */
static
jclass GetPrimitiveClass(JNIEnv *env, jchar shar)
{
  const char *name;
  jclass clazz = NULL;
  jfieldID fieldID;
  jclass result = NULL;

  switch (shar) {
  case 'V': name = "java/lang/Void"; break;
  case 'Z': name = "java/lang/Boolean"; break;
  case 'B': name = "java/lang/Byte"; break;
  case 'C': name = "java/lang/Character"; break;
  case 'S': name = "java/lang/Short"; break;
  case 'I': name = "java/lang/Integer"; break;
  case 'J': name = "java/lang/Long"; break;
  case 'F': name = "java/lang/Float"; break;
  case 'D': name = "java/lang/Double"; break;
  default:
    JNI_FatalError(env, "illegal primitive type char");
    return NULL;
  }

  clazz = JNI_FindClass(env, name);
  if (clazz == NULL)
    goto exit;

  fieldID = JNI_GetStaticFieldID(env, clazz, "TYPE", "Ljava/lang/Class;");
  if (fieldID == NULL)
    goto exit;

  result = JNI_GetStaticObjectField(env, clazz, fieldID);
  if (env->thrown != NULL)
    goto exit;

exit:

  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* PopAllLocalFrames
 */
void PopAllLocalFrames(JNIEnv *env)
{
  JNIFrame *frame;

  mutex_lock(&env->lock);
  while (env->frames > 0) {
    frame = env->topFrame;
    env->topFrame = frame->previous;
    free(frame);
    env->frames--;
  }
  mutex_unlock(&env->lock);
}

/* IsSubtypeOf
 */
jboolean IsSubtypeOf(jclass sub, jclass super)
{
  jclass clazz;
  struct interfaceInfo *interfaces;
  jsize i;
  struct classInfo *subInfo;
  struct classInfo *superInfo;
  jint subDims;
  jint superDims;

  subInfo = CLASS_INFO(sub);
  superInfo = CLASS_INFO(super);
  for (clazz = sub; clazz != NULL; clazz = CLASS_INFO(clazz)->superClass)
    if (clazz == super)
      return JNI_TRUE;
  interfaces = subInfo->interfaces;
  if (interfaces != NULL)
    for (i = 0; (clazz = interfaces[i].clazz) != NULL; i++)
      if (clazz == super)
        return JNI_TRUE;
  subDims = (jint)subInfo->dimensions & 0xFF;
  superDims = (jint)superInfo->dimensions & 0xFF;
  if (subDims > 0 && superDims > 0) {
    if (subDims == superDims)
      return IsSubtypeOf(subInfo->elementClass, superInfo->elementClass);
    if (subDims > superDims)
      return IsSubtypeOf(sub, superInfo->elementClass);
  }
  return JNI_FALSE;
}

/* IsComptypeOf
 */
jboolean IsComptypeOf(jclass sub, jclass super)
{
  struct classInfo *subInfo;
  struct classInfo *superInfo;
  jint subDims;
  jint superDims;

  subInfo = CLASS_INFO(sub);
  superInfo = CLASS_INFO(super);
  subDims = (jint)subInfo->dimensions & 0xFF;
  superDims = (jint)superInfo->dimensions & 0xFF;
  if (subDims+1 == superDims) {
    if (subDims == 0)
      return IsSubtypeOf(sub, superInfo->elementClass);
    return IsSubtypeOf(subInfo->elementClass, superInfo->elementClass);
  }
  if (subDims+1 > superDims)
    return IsSubtypeOf(sub, superInfo->elementClass);
  return JNI_FALSE;
}

/* IMLookup
 */
jobject IMLookup(jclass clazz, jclass inter_face, jsize index)
{
  const struct classInfo *classInfo;
  const struct interfaceInfo *interfaces;
  jint i;

  classInfo = CLASS_INFO(clazz);
  interfaces = classInfo->interfaces;
  if (interfaces != NULL)
    for (i = 0; interfaces[i].clazz != NULL; i++)
      if (interfaces[i].clazz == inter_face)
        return GETMETHOD(clazz, interfaces[i].baseIndex+index);
  return NULL;
}

/* ThrowByName
 */
void ThrowByName(JNIEnv *env, const char *name, const char *message)
{
  jclass clazz = NULL;
  jint result;

  clazz = JNI_FindClass(env, name);
  if (clazz == NULL)
    goto exit;

  result = JNI_ThrowNew(env, clazz, message);
  if (result != JNI_OK)
    if (env->thrown == NULL)
      JNI_FatalError(env, "could not throw exception");

exit:

  JNI_DeleteLocalRef(env, clazz);
}

/* ThrowBounds
 */
static
void ThrowBounds(JNIEnv *env, jint index)
{
  jclass boundsClass = NULL;
  jmethodID methodID;
  jthrowable thrown = NULL;

  boundsClass = JNI_FindClass(env, "java/lang/ArrayIndexOutOfBoundsException");
  if (boundsClass == NULL)
    goto exit;

  methodID = JNI_GetMethodID(env, boundsClass, "<init>", "(I)V");
  if (methodID == NULL)
    goto exit;

  thrown = JNI_NewObject(env, boundsClass, methodID, index);
  if (thrown == NULL)
    goto exit;

  JNI_Throw(env, thrown);

exit:

  JNI_DeleteLocalRef(env, thrown);
  JNI_DeleteLocalRef(env, boundsClass);
}

/* PrintFormatted
 */
void PrintFormatted(JNIEnv *env, const char *format, ...)
{
  JavaVM *jvm;
  va_list args;

  jvm = env->jvm;
  va_start(args, format);
  if (jvm->vfprintf != NULL)
    jvm->vfprintf(stderr, format, args);
  else
    vfprintf(stderr, format, args);
  va_end(args);
}

/* GetVersion
 */
static
jint JNICALL JNI_GetVersion(JNIEnv *env)
{
  return JNI_VERSION_1_2;  
}

/* AllocObject
 */
static
jobject JNICALL JNI_AllocObject(JNIEnv *env, jclass clazz)
{
  struct classInfo *classInfo;
  jchar accessFlags;

  classInfo = CLASS_INFO(clazz);
  accessFlags = classInfo->accessFlags;
  if ((accessFlags & ACC_ABSTRACT) != 0) {
    ThrowByName(env, "java/lang/InstantiationException", classInfo->name);
    return NULL;
  }
  return AllocObject(env, clazz);
}

/* NewBooleanArray
 */
static
jbooleanArray JNICALL JNI_NewBooleanArray(JNIEnv *env, jsize length)
{
  jclass clazz;
  jbooleanArray result;

  clazz = JNI_FindClass(env, "[Z");
  if (clazz == NULL)
    return NULL;
  result = AllocArray(env, clazz, length);
  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* NewByteArray
 */
static
jbyteArray JNICALL JNI_NewByteArray(JNIEnv *env, jsize length)
{
  jclass clazz;
  jbyteArray result;

  clazz = JNI_FindClass(env, "[B");
  if (clazz == NULL)
    return NULL;
  result = AllocArray(env, clazz, length);
  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* NewCharArray
 */
static
jcharArray JNICALL JNI_NewCharArray(JNIEnv *env, jsize length)
{
  jclass clazz;
  jcharArray result;

  clazz = JNI_FindClass(env, "[C");
  if (clazz == NULL)
    return NULL;
  result = AllocArray(env, clazz, length);
  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* NewShortArray
 */
static
jshortArray JNICALL JNI_NewShortArray(JNIEnv *env, jsize length)
{
  jclass clazz;
  jshortArray result;

  clazz = JNI_FindClass(env, "[S");
  if (clazz == NULL)
    return NULL;
  result = AllocArray(env, clazz, length);
  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* NewIntArray
 */
static
jintArray JNICALL JNI_NewIntArray(JNIEnv *env, jsize length)
{
  jclass clazz;
  jintArray result;

  clazz = JNI_FindClass(env, "[I");
  if (clazz == NULL)
    return NULL;
  result = AllocArray(env, clazz, length);
  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* NewLongArray
 */
static
jlongArray JNICALL JNI_NewLongArray(JNIEnv *env, jsize length)
{
  jclass clazz;
  jlongArray result;

  clazz = JNI_FindClass(env, "[J");
  if (clazz == NULL)
    return NULL;
  result = AllocArray(env, clazz, length);
  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* NewFloatArray
 */
static
jfloatArray JNICALL JNI_NewFloatArray(JNIEnv *env, jsize length)
{
  jclass clazz;
  jfloatArray result;

  clazz = JNI_FindClass(env, "[F");
  if (clazz == NULL)
    return NULL;
  result = AllocArray(env, clazz, length);
  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* NewDoubleArray
 */
static
jdoubleArray JNICALL JNI_NewDoubleArray(JNIEnv *env, jsize length)
{
  jclass clazz;
  jdoubleArray result;

  clazz = JNI_FindClass(env, "[D");
  if (clazz == NULL)
    return NULL;
  result = AllocArray(env, clazz, length);
  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* MonitorEnter
 */
static
jint JNICALL JNI_MonitorEnter(JNIEnv *env, jobject object)
{
  if (!mon_acquire(&env->jvm->montab, object))
    return JNI_ERR;
  return JNI_OK;
}

/* MonitorExit
 */
static
jint JNICALL JNI_MonitorExit(JNIEnv *env, jobject object)
{
  if (!mon_owns(&env->jvm->montab, object))
    return JNI_ERR;
  mon_release(&env->jvm->montab, object);
  return JNI_OK;
}

/* EnsureLocalCapacity
 */
static
jint JNICALL JNI_EnsureLocalCapacity(JNIEnv *env, jint extra)
{
  JNIFrame *frame;
  jint capacity;

  frame = env->topFrame;
  capacity = frame->size+extra;
  if (capacity > frame->capacity) {
    frame = (JNIFrame*)realloc(frame, sizeof(JNIFrame)+capacity*sizeof(jobject));
    if (frame == NULL)
      return JNI_ERR;
    frame->capacity = capacity;
    mutex_lock(&env->lock);
    env->topFrame = frame;
    mutex_unlock(&env->lock);
  }
  return JNI_OK;
}

/* ExceptionOccurred
 */
static
jthrowable JNICALL JNI_ExceptionOccurred(JNIEnv *env)
{
  return JNI_NewLocalRef(env, env->thrown);
}

/* GetSuperclass
 */
static
jclass JNICALL JNI_GetSuperclass(JNIEnv *env, jclass clazz)
{
  const struct classInfo *classInfo;

  classInfo = CLASS_INFO(clazz);
  if ((classInfo->accessFlags & ACC_INTERFACE) != 0)
    return NULL;
  return JNI_NewLocalRef(env, classInfo->superClass);
}

/* FatalError
 */
static
void JNICALL JNI_FatalError(JNIEnv *env, const char *message)
{
  JavaVM *jvm;

  jvm = env->jvm;
  PrintFormatted(env, "fatal error: %s\n", message);
  if (jvm->abort != NULL)
    jvm->abort();
  else
    abort();
}

/* Throw
 */
static
jint JNICALL JNI_Throw(JNIEnv *env, jthrowable thrown)
{ 
  mutex_lock(&env->lock);
  env->thrown = thrown;
  mutex_unlock(&env->lock);
  return JNI_OK;
}

/* ThrowNew
 */
static
jint JNICALL JNI_ThrowNew(JNIEnv *env, jclass clazz, const char *message)
{
  jint error;
  jmethodID methodID;
  jstring string = NULL;
  jthrowable thrown = NULL;
  jint result = JNI_ERR;

  error = JNI_EnsureLocalCapacity(env, 2);
  if (error != JNI_OK)
    goto exit;

  methodID = JNI_GetMethodID(env, clazz, "<init>", "(Ljava/lang/String;)V");
  if (methodID == NULL)
    goto exit;

  if (message != NULL) {
    string = JNI_NewStringUTF(env, message);
    if (string == NULL)
      goto exit;
  }

  thrown = JNI_NewObject(env, clazz, methodID, string);
  if (thrown != NULL)
    JNI_Throw(env, thrown);

  result = JNI_OK;

exit:

  JNI_DeleteLocalRef(env, thrown);
  JNI_DeleteLocalRef(env, string);
  return result;
}

/* ExceptionClear
 */
static
void JNICALL JNI_ExceptionClear(JNIEnv *env)
{
  mutex_lock(&env->lock);
  env->thrown = NULL;
  mutex_unlock(&env->lock);
}

/* PushLocalFrame
 */
static
jint JNICALL JNI_PushLocalFrame(JNIEnv *env, jint capacity)
{
  JNIFrame *frame;

  frame = (JNIFrame*)malloc(sizeof(JNIFrame)+capacity*sizeof(jobject));
  if (frame == NULL)
    return JNI_ERR;
  frame->previous = env->topFrame;
  frame->size = 0;
  frame->capacity = capacity;
  mutex_lock(&env->lock);
  env->topFrame = frame;
  env->frames++;
  mutex_unlock(&env->lock);
  return JNI_OK;
}

/* PopLocalFrame
 */
static
jobject JNICALL JNI_PopLocalFrame(JNIEnv *env, jobject object)
{
  JNIFrame *frame;

  if (env->frames == 0)
    JNI_FatalError(env, "frame stack underflow");
  frame = env->topFrame;
  mutex_lock(&env->lock);
  env->frames--;
  env->topFrame = frame->previous;
  mutex_unlock(&env->lock);
  free(frame);
  return JNI_NewLocalRef(env, object);
}

/* GetObjectClass
 */
static
jclass JNICALL JNI_GetObjectClass(JNIEnv *env, jobject object)
{
  return JNI_NewLocalRef(env, GETCLASS(object));
}

/* NewLocalRef
 */
static
jobject JNICALL JNI_NewLocalRef(JNIEnv *env, jobject object)
{
  JNIFrame *frame;

  if (object == NULL)
    return NULL;
  mutex_lock(&env->lock);
  frame = env->topFrame;
  if (frame->size == frame->capacity) {
    frame->capacity += 16;
    frame = (JNIFrame*)realloc(frame, sizeof(JNIFrame)+frame->capacity*sizeof(jobject));
    if (frame == NULL)
      JNI_FatalError(env, "could not create local reference");
    env->topFrame = frame;
  }
  frame->entries[frame->size++] = object;
  mutex_unlock(&env->lock);
  return object;
}

/* DeleteLocalRef
 */
static
void JNICALL JNI_DeleteLocalRef(JNIEnv *env, jobject object)
{
  JNIFrame *frame;
  jint i;

  if (object == NULL)
    return;
  mutex_lock(&env->lock);
  frame = env->topFrame;
  for (i = frame->size-1; i >= 0; i--)
    if (frame->entries[i] == object) {
      frame->entries[i] = frame->entries[--frame->size];
      mutex_unlock(&env->lock);
      return;
    }
  mutex_unlock(&env->lock);
  JNI_FatalError(env, "could not delete local reference");
}

/* CallObjectMethod
 */
static
jobject JNICALL JNI_CallObjectMethod(JNIEnv *env, jobject object, jmethodID methodID, ...)
{
  va_list args;
  jobject result;

  va_start(args, methodID);
  result = JNI_CallObjectMethodV(env, object, methodID, args);
  va_end(args);
  return result;
}

/* CallBooleanMethod
 */
static
jboolean JNICALL JNI_CallBooleanMethod(JNIEnv *env, jobject object, jmethodID methodID, ...)
{
  va_list args;
  jboolean result;

  va_start(args, methodID);
  result = JNI_CallBooleanMethodV(env, object, methodID, args);
  va_end(args);
  return result;
}

/* CallByteMethod
 */
static
jbyte JNICALL JNI_CallByteMethod(JNIEnv *env, jobject object, jmethodID methodID, ...)
{
  va_list args;
  jbyte result;

  va_start(args, methodID);
  result = JNI_CallByteMethodV(env, object, methodID, args);
  va_end(args);
  return result;
}

/* CallCharMethod
 */
static
jchar JNICALL JNI_CallCharMethod(JNIEnv *env, jobject object, jmethodID methodID, ...)
{
  va_list args;
  jchar result;

  va_start(args, methodID);
  result = JNI_CallCharMethodV(env, object, methodID, args);
  va_end(args);
  return result;
}

/* CallShortMethod
 */
static
jshort JNICALL JNI_CallShortMethod(JNIEnv *env, jobject object, jmethodID methodID, ...)
{
  va_list args;
  jshort result;

  va_start(args, methodID);
  result = JNI_CallShortMethodV(env, object, methodID, args);
  va_end(args);
  return result;
}

/* CallIntMethod
 */
static
jint JNICALL JNI_CallIntMethod(JNIEnv *env, jobject object, jmethodID methodID, ...)
{
  va_list args;
  jint result;

  va_start(args, methodID);
  result = JNI_CallIntMethodV(env, object, methodID, args);
  va_end(args);
  return result;
}

/* CallLongMethod
 */
static
jlong JNICALL JNI_CallLongMethod(JNIEnv *env, jobject object, jmethodID methodID, ...)
{
  va_list args;
  jlong result;

  va_start(args, methodID);
  result = JNI_CallLongMethodV(env, object, methodID, args);
  va_end(args);
  return result;
}

/* CallFloatMethod
 */
static
jfloat JNICALL JNI_CallFloatMethod(JNIEnv *env, jobject object, jmethodID methodID, ...)
{
  va_list args;
  jfloat result;

  va_start(args, methodID);
  result = JNI_CallFloatMethodV(env, object, methodID, args);
  va_end(args);
  return result;
}

/* CallDoubleMethod
 */
static
jdouble JNICALL JNI_CallDoubleMethod(JNIEnv *env, jobject object, jmethodID methodID, ...)
{
  va_list args;
  jdouble result;

  va_start(args, methodID);
  result = JNI_CallDoubleMethodV(env, object, methodID, args);
  va_end(args);
  return result;
}

/* CallVoidMethod
 */
static
void JNICALL JNI_CallVoidMethod(JNIEnv *env, jobject object, jmethodID methodID, ...)
{
  va_list args;

  va_start(args, methodID);
  JNI_CallVoidMethodV(env, object, methodID, args);
  va_end(args);
}

/* CallNonvirtualObjectMethod
 */
static
jobject JNICALL JNI_CallNonvirtualObjectMethod(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jobject result;

  va_start(args, methodID);
  result = JNI_CallNonvirtualObjectMethodV(env, object, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallNonvirtualBooleanMethod
 */
static
jboolean JNICALL JNI_CallNonvirtualBooleanMethod(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jboolean result;

  va_start(args, methodID);
  result = JNI_CallNonvirtualBooleanMethodV(env, object, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallNonvirtualByteMethod
 */
static
jbyte JNICALL JNI_CallNonvirtualByteMethod(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jbyte result;

  va_start(args, methodID);
  result = JNI_CallNonvirtualByteMethodV(env, object, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallNonvirtualCharMethod
 */
static
jchar JNICALL JNI_CallNonvirtualCharMethod(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jchar result;

  va_start(args, methodID);
  result = JNI_CallNonvirtualCharMethodV(env, object, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallNonvirtualShortMethod
 */
static
jshort JNICALL JNI_CallNonvirtualShortMethod(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jshort result;

  va_start(args, methodID);
  result = JNI_CallNonvirtualShortMethodV(env, object, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallNonvirtualIntMethod
 */
static
jint JNICALL JNI_CallNonvirtualIntMethod(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jint result;

  va_start(args, methodID);
  result = JNI_CallNonvirtualIntMethodV(env, object, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallNonvirtualLongMethod
 */
static
jlong JNICALL JNI_CallNonvirtualLongMethod(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jlong result;

  va_start(args, methodID);
  result = JNI_CallNonvirtualLongMethodV(env, object, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallNonvirtualFloatMethod
 */
static
jfloat JNICALL JNI_CallNonvirtualFloatMethod(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jfloat result;

  va_start(args, methodID);
  result = JNI_CallNonvirtualFloatMethodV(env, object, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallNonvirtualDoubleMethod
 */
static
jdouble JNICALL JNI_CallNonvirtualDoubleMethod(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jdouble result;

  va_start(args, methodID);
  result = JNI_CallNonvirtualDoubleMethodV(env, object, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallNonvirtualVoidMethod
 */
static
void JNICALL JNI_CallNonvirtualVoidMethod(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, ...)
{
  va_list args;

  va_start(args, methodID);
  JNI_CallNonvirtualVoidMethodV(env, object, clazz, methodID, args);
  va_end(args);
}

/* CallStaticObjectMethod
 */
static
jobject JNICALL JNI_CallStaticObjectMethod(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jobject result;

  va_start(args, methodID);
  result = JNI_CallStaticObjectMethodV(env, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallStaticBooleanMethod
 */
static
jboolean JNICALL JNI_CallStaticBooleanMethod(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jboolean result;

  va_start(args, methodID);
  result = JNI_CallStaticBooleanMethodV(env, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallStaticByteMethod
 */
static
jbyte JNICALL JNI_CallStaticByteMethod(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jbyte result;

  va_start(args, methodID);
  result = JNI_CallStaticByteMethodV(env, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallStaticCharMethod
 */
static
jchar JNICALL JNI_CallStaticCharMethod(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jchar result;

  va_start(args, methodID);
  result = JNI_CallStaticCharMethodV(env, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallStaticShortMethod
 */
static
jshort JNICALL JNI_CallStaticShortMethod(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{ 
  va_list args;
  jshort result;

  va_start(args, methodID);
  result = JNI_CallStaticShortMethodV(env, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallStaticIntMethod
 */
static
jint JNICALL JNI_CallStaticIntMethod(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{ 
  va_list args;
  jint result;

  va_start(args, methodID);
  result = JNI_CallStaticIntMethodV(env, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallStaticLongMethod
 */
static
jlong JNICALL JNI_CallStaticLongMethod(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jlong result;

  va_start(args, methodID);
  result = JNI_CallStaticLongMethodV(env, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallStaticFloatMethod
 */
static
jfloat JNICALL JNI_CallStaticFloatMethod(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jfloat result;

  va_start(args, methodID);
  result = JNI_CallStaticFloatMethodV(env, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallStaticDoubleMethod
 */
static
jdouble JNICALL JNI_CallStaticDoubleMethod(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jdouble result;

  va_start(args, methodID);
  result = JNI_CallStaticDoubleMethodV(env, clazz, methodID, args);
  va_end(args);
  return result;
}

/* CallStaticVoidMethod
 */
static
void JNICALL JNI_CallStaticVoidMethod(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  va_list args;

  va_start(args, methodID);
  JNI_CallStaticVoidMethodV(env, clazz, methodID, args);
  va_end(args);
}

/* GetArrayLength
 */
static
jsize JNICALL JNI_GetArrayLength(JNIEnv *env, jarray array)
{
  return LENGTH(array);
}

/* GetObjectField
 */
static
jobject JNICALL JNI_GetObjectField(JNIEnv *env, jobject object, jfieldID fieldID)
{ 
  const struct fieldInfo *fieldInfo;
  jobject result;

  fieldInfo = FIELD_INFO(fieldID);
  mutex_lock(&env->lock);
  result = JNI_NewLocalRef(env, FIELD(object, fieldInfo->offset, jobject));
  mutex_unlock(&env->lock);
  return result;
}

/* GetBooleanField
 */
static
jboolean JNICALL JNI_GetBooleanField(JNIEnv *env, jobject object, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jboolean result;

  fieldInfo = FIELD_INFO(fieldID);
  result = FIELD(object, fieldInfo->offset, jboolean);
  return result;
}

/* GetByteField
 */
static
jbyte JNICALL JNI_GetByteField(JNIEnv *env, jobject object, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jbyte result;

  fieldInfo = FIELD_INFO(fieldID);
  result = FIELD(object, fieldInfo->offset, jbyte);
  return result;
}

/* GetCharField
 */
static
jchar JNICALL JNI_GetCharField(JNIEnv *env, jobject object, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jchar result;

  fieldInfo = FIELD_INFO(fieldID);
  result = FIELD(object, fieldInfo->offset, jchar);
  return result;
}

/* GetShortField
 */
static
jshort JNICALL JNI_GetShortField(JNIEnv *env, jobject object, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jshort result;

  fieldInfo = FIELD_INFO(fieldID);
  result = FIELD(object, fieldInfo->offset, jshort);
  return result;
}

/* GetIntField
 */
static
jint JNICALL JNI_GetIntField(JNIEnv *env, jobject object, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jint result;

  fieldInfo = FIELD_INFO(fieldID);
  result = FIELD(object, fieldInfo->offset, jint);
  return result;
}

/* GetLongField
 */
static
jlong JNICALL JNI_GetLongField(JNIEnv *env, jobject object, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jlong result;

  fieldInfo = FIELD_INFO(fieldID);
  result = FIELD(object, fieldInfo->offset, jlong);
  return result;
}

/* GetFloatField
 */
static
jfloat JNICALL JNI_GetFloatField(JNIEnv *env, jobject object, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jfloat result;

  fieldInfo = FIELD_INFO(fieldID);
  result = FIELD(object, fieldInfo->offset, jfloat);
  return result;
}

/* GetDoubleField
 */
static
jdouble JNICALL JNI_GetDoubleField(JNIEnv *env, jobject object, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jdouble result;

  fieldInfo = FIELD_INFO(fieldID);
  result = FIELD(object, fieldInfo->offset, jdouble);
  return result;
}

/* SetObjectField
 */
static
void JNICALL JNI_SetObjectField(JNIEnv *env, jobject object, jfieldID fieldID, jobject value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  mutex_lock(&env->lock);
  FIELD(object, fieldInfo->offset, jobject) = value;
  mutex_unlock(&env->lock);
}

/* SetBooleanField
 */
static
void JNICALL JNI_SetBooleanField(JNIEnv *env, jobject object, jfieldID fieldID, jboolean value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  FIELD(object, fieldInfo->offset, jboolean) = value;
}

/* SetByteField
 */
static
void JNICALL JNI_SetByteField(JNIEnv *env, jobject object, jfieldID fieldID, jbyte value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  FIELD(object, fieldInfo->offset, jbyte) = value;
}

/* SetCharField
 */
static
void JNICALL JNI_SetCharField(JNIEnv *env, jobject object, jfieldID fieldID, jchar value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  FIELD(object, fieldInfo->offset, jchar) = value;
}

/* SetShortField
 */
static
void JNICALL JNI_SetShortField(JNIEnv *env, jobject object, jfieldID fieldID, jshort value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  FIELD(object, fieldInfo->offset, jshort) = value;
}

/* SetIntField
 */
static
void JNICALL JNI_SetIntField(JNIEnv *env, jobject object, jfieldID fieldID, jint value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  FIELD(object, fieldInfo->offset, jint) = value;
}

/* SetLongField
 */
static
void JNICALL JNI_SetLongField(JNIEnv *env, jobject object, jfieldID fieldID, jlong value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  FIELD(object, fieldInfo->offset, jlong) = value;
}

/* SetFloatField
 */
static
void JNICALL JNI_SetFloatField(JNIEnv *env, jobject object, jfieldID fieldID, jfloat value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  FIELD(object, fieldInfo->offset, jfloat) = value;
}

/* SetDoubleField
 */
static
void JNICALL JNI_SetDoubleField(JNIEnv *env, jobject object, jfieldID fieldID, jdouble value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  FIELD(object, fieldInfo->offset, jdouble) = value;
}

/* GetStaticObjectField
 */
static
jobject JNICALL JNI_GetStaticObjectField(JNIEnv *env, jclass clazz, jfieldID fieldID)
{ 
  const struct fieldInfo *fieldInfo;
  jobject result;

  fieldInfo = FIELD_INFO(fieldID);
  mutex_lock(&env->lock);
  result = JNI_NewLocalRef(env, STATICFIELD(clazz, fieldInfo->offset, jobject));
  mutex_unlock(&env->lock);
  return result;
}

/* GetStaticBooleanField
 */
static
jboolean JNICALL JNI_GetStaticBooleanField(JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jboolean result;

  fieldInfo = FIELD_INFO(fieldID);
  result = STATICFIELD(clazz, fieldInfo->offset, jboolean);
  return result;
}

/* GetStaticByteField
 */
static
jbyte JNICALL JNI_GetStaticByteField(JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jbyte result;

  fieldInfo = FIELD_INFO(fieldID);
  result = STATICFIELD(clazz, fieldInfo->offset, jbyte);
  return result;
}

/* GetStaticCharField
 */
static
jchar JNICALL JNI_GetStaticCharField(JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jchar result;

  fieldInfo = FIELD_INFO(fieldID);
  result = STATICFIELD(clazz, fieldInfo->offset, jchar);
  return result;
}

/* GetStaticShortField
 */
static
jshort JNICALL JNI_GetStaticShortField(JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jshort result;

  fieldInfo = FIELD_INFO(fieldID);
  result = STATICFIELD(clazz, fieldInfo->offset, jshort);
  return result;
}

/* GetStaticIntField
 */
static
jint JNICALL JNI_GetStaticIntField(JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jint result;

  fieldInfo = FIELD_INFO(fieldID);
  result = STATICFIELD(clazz, fieldInfo->offset, jint);
  return result;
}

/* GetStaticLongField
 */
static
jlong JNICALL JNI_GetStaticLongField(JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jlong result;

  fieldInfo = FIELD_INFO(fieldID);
  result = STATICFIELD(clazz, fieldInfo->offset, jlong);
  return result;
}

/* GetStaticFloatField
 */
static
jfloat JNICALL JNI_GetStaticFloatField(JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jfloat result;

  fieldInfo = FIELD_INFO(fieldID);
  result = STATICFIELD(clazz, fieldInfo->offset, jfloat);
  return result;
}

/* GetStaticDoubleField
 */
static
jdouble JNICALL JNI_GetStaticDoubleField(JNIEnv *env, jclass clazz, jfieldID fieldID)
{ 
  const struct fieldInfo *fieldInfo;
  jdouble result;

  fieldInfo = FIELD_INFO(fieldID);
  result = STATICFIELD(clazz, fieldInfo->offset, jdouble);
  return result;
}

/* SetStaticObjectField
 */
static
void JNICALL JNI_SetStaticObjectField(JNIEnv *env, jclass clazz, jfieldID fieldID, jobject value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  mutex_lock(&env->lock);
  STATICFIELD(clazz, fieldInfo->offset, jobject) = value;
  mutex_unlock(&env->lock);
}

/* SetStaticBooleanField
 */
static
void JNICALL JNI_SetStaticBooleanField(JNIEnv *env, jclass clazz, jfieldID fieldID, jboolean value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  STATICFIELD(clazz, fieldInfo->offset, jboolean) = value;
}

/* SetStaticByteField
 */
static
void JNICALL JNI_SetStaticByteField(JNIEnv *env, jclass clazz, jfieldID fieldID, jbyte value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  STATICFIELD(clazz, fieldInfo->offset, jbyte) = value;
}

/* SetStaticCharField
 */
static
void JNICALL JNI_SetStaticCharField(JNIEnv *env, jclass clazz, jfieldID fieldID, jchar value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  STATICFIELD(clazz, fieldInfo->offset, jchar) = value;
}

/* SetStaticShortField
 */
static
void JNICALL JNI_SetStaticShortField(JNIEnv *env, jclass clazz, jfieldID fieldID, jshort value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  STATICFIELD(clazz, fieldInfo->offset, jshort) = value;
}

/* SetStaticIntField
 */
static
void JNICALL JNI_SetStaticIntField(JNIEnv *env, jclass clazz, jfieldID fieldID, jint value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  STATICFIELD(clazz, fieldInfo->offset, jint) = value;
}

/* SetStaticLongField
 */
static
void JNICALL JNI_SetStaticLongField(JNIEnv *env, jclass clazz, jfieldID fieldID, jlong value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  STATICFIELD(clazz, fieldInfo->offset, jlong) = value;
}

/* SetStaticFloatField
 */
static
void JNICALL JNI_SetStaticFloatField(JNIEnv *env, jclass clazz, jfieldID fieldID, jfloat value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  STATICFIELD(clazz, fieldInfo->offset, jfloat) = value;
}

/* SetStaticDoubleField
 */
static
void JNICALL JNI_SetStaticDoubleField(JNIEnv *env, jclass clazz, jfieldID fieldID, jdouble value)
{
  const struct fieldInfo *fieldInfo;

  fieldInfo = FIELD_INFO(fieldID);
  STATICFIELD(clazz, fieldInfo->offset, jdouble) = value;
}

/* IsAssignableFrom
 */
static
jboolean JNICALL JNI_IsAssignableFrom(JNIEnv *env, jclass sub, jclass super)
{
  return IsSubtypeOf(sub, super);
}

/* IsInstanceOf
 */
static
jboolean JNICALL JNI_IsInstanceOf(JNIEnv *env, jobject object, jclass clazz)
{
  return object == NULL || IsSubtypeOf(GETCLASS(object), clazz);
}

/* ReleaseBooleanArrayElements
 */
static
void JNICALL JNI_ReleaseBooleanArrayElements(JNIEnv *env, jbooleanArray array, jboolean *elements, jint mode)
{
}

/* ReleaseByteArrayElements
 */
static
void JNICALL JNI_ReleaseByteArrayElements(JNIEnv *env, jbyteArray array, jbyte *elements, jint mode)
{
}

/* ReleaseCharArrayElements
 */
static
void JNICALL JNI_ReleaseCharArrayElements(JNIEnv *env, jcharArray array, jchar *elements, jint mode)
{
}

/* ReleaseShortArrayElements
 */
static
void JNICALL JNI_ReleaseShortArrayElements(JNIEnv *env, jshortArray array, jshort *elements, jint mode)
{
}

/* ReleaseIntArrayElements
 */
static
void JNICALL JNI_ReleaseIntArrayElements(JNIEnv *env, jintArray array, jint *elements, jint mode)
{
}

/* ReleaseLongArrayElements
 */
static
void JNICALL JNI_ReleaseLongArrayElements(JNIEnv *env, jlongArray array, jlong *elements, jint mode)
{
}

/* ReleaseFloatArrayElements
 */
static
void JNICALL JNI_ReleaseFloatArrayElements(JNIEnv *env, jfloatArray array, jfloat *elements, jint mode)
{
}

/* ReleaseDoubleArrayElements
 */
static
void JNICALL JNI_ReleaseDoubleArrayElements(JNIEnv *env, jdoubleArray array, jdouble *elements, jint mode)
{
}

/* NewObject
 */
static
jobject JNICALL JNI_NewObject(JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  va_list args;
  jobject result;

  va_start(args, methodID);
  result = JNI_NewObjectV(env, clazz, methodID, args);
  va_end(args);
  return result;
}

/* NewObjectV
 */
static
jobject JNICALL JNI_NewObjectV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject object = NULL;

  object = JNI_AllocObject(env, clazz);
  if (object == NULL)
    return NULL;

  JNI_CallNonvirtualVoidMethodV(env, object, clazz, methodID, args);
  if (env->thrown != NULL) {
    JNI_DeleteLocalRef(env, object);
    return NULL;
  }

  return object;
}

/* NewObjectA
 */
static
jobject JNICALL JNI_NewObjectA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jobject result;

  va_startj(list, args);
  result = JNI_NewObjectV(env, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallObjectMethodA
 */
static
jobject JNICALL JNI_CallObjectMethodA(JNIEnv *env, jobject object, jmethodID methodID, jvalue *args)
{
  va_list list;
  jobject result;

  va_startj(list, args);
  result = JNI_CallObjectMethodV(env, object, methodID, list);
  va_endj(list);
  return result;
}

/* CallBooleanMethodA
 */
static
jboolean JNICALL JNI_CallBooleanMethodA(JNIEnv *env, jobject object, jmethodID methodID, jvalue *args)
{
  va_list list;
  jboolean result;

  va_startj(list, args);
  result = JNI_CallBooleanMethodV(env, object, methodID, list);
  va_endj(list);
  return result;
}

/* CallByteMethodA
 */
static
jbyte JNICALL JNI_CallByteMethodA(JNIEnv *env, jobject object, jmethodID methodID, jvalue *args)
{
  va_list list;
  jbyte result;

  va_startj(list, args);
  result = JNI_CallByteMethodV(env, object, methodID, list);
  va_endj(list);
  return result;
}

/* CallCharMethodA
 */
static
jchar JNICALL JNI_CallCharMethodA(JNIEnv *env, jobject object, jmethodID methodID, jvalue *args)
{
  va_list list;
  jchar result;

  va_startj(list, args);
  result = JNI_CallCharMethodV(env, object, methodID, list);
  va_endj(list);
  return result;
}

/* CallShortMethodA
 */
static
jshort JNICALL JNI_CallShortMethodA(JNIEnv *env, jobject object, jmethodID methodID, jvalue *args)
{
  va_list list;
  jshort result;

  va_startj(list, args);
  result = JNI_CallShortMethodV(env, object, methodID, list);
  va_endj(list);
  return result;
}

/* CallIntMethodA
 */
static
jint JNICALL JNI_CallIntMethodA(JNIEnv *env, jobject object, jmethodID methodID, jvalue *args)
{
  va_list list;
  jint result;

  va_startj(list, args);
  result = JNI_CallIntMethodV(env, object, methodID, list);
  va_endj(list);
  return result;
}

/* CallLongMethodA
 */
static
jlong JNICALL JNI_CallLongMethodA(JNIEnv *env, jobject object, jmethodID methodID, jvalue *args)
{
  va_list list;
  jlong result;

  va_startj(list, args);
  result = JNI_CallLongMethodV(env, object, methodID, list);
  va_endj(list);
  return result;
}

/* CallFloatMethodA
 */
static
jfloat JNICALL JNI_CallFloatMethodA(JNIEnv *env, jobject object, jmethodID methodID, jvalue *args)
{
  va_list list;
  jfloat result;

  va_startj(list, args);
  result = JNI_CallFloatMethodV(env, object, methodID, list);
  va_endj(list);
  return result;
}

/* CallDoubleMethodA
 */
static
jdouble JNICALL JNI_CallDoubleMethodA(JNIEnv *env, jobject object, jmethodID methodID, jvalue *args)
{
  va_list list;
  jdouble result;

  va_startj(list, args);
  result = JNI_CallDoubleMethodV(env, object, methodID, list);
  va_endj(list);
  return result;
}

/* CallVoidMethodA
 */
static
void JNICALL JNI_CallVoidMethodA(JNIEnv *env, jobject object, jmethodID methodID, jvalue *args)
{
  va_list list;

  va_startj(list, args);
  JNI_CallVoidMethodV(env, object, methodID, list);
  va_endj(list);
}

/* CallNonvirtualObjectMethodA
 */
static
jobject JNICALL JNI_CallNonvirtualObjectMethodA(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jobject result;

  va_startj(list, args);
  result = JNI_CallNonvirtualObjectMethodV(env, object, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallNonvirtualBooleanMethodA
 */
static
jboolean JNICALL JNI_CallNonvirtualBooleanMethodA(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jboolean result;

  va_startj(list, args);
  result = JNI_CallNonvirtualBooleanMethodV(env, object, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallNonvirtualByteMethodA
 */
static
jbyte JNICALL JNI_CallNonvirtualByteMethodA(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jbyte result;

  va_startj(list, args);
  result = JNI_CallNonvirtualByteMethodV(env, object, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallNonvirtualCharMethodA
 */
static
jchar JNICALL JNI_CallNonvirtualCharMethodA(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jchar result;

  va_startj(list, args);
  result = JNI_CallNonvirtualCharMethodV(env, object, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallNonvirtualShortMethodA
 */
static
jshort JNICALL JNI_CallNonvirtualShortMethodA(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jshort result;

  va_startj(list, args);
  result = JNI_CallNonvirtualShortMethodV(env, object, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallNonvirtualIntMethodA
 */
static
jint JNICALL JNI_CallNonvirtualIntMethodA(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jint result;

  va_startj(list, args);
  result = JNI_CallNonvirtualIntMethodV(env, object, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallNonvirtualLongMethodA
 */
static
jlong JNICALL JNI_CallNonvirtualLongMethodA(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jlong result;

  va_startj(list, args);
  result = JNI_CallNonvirtualLongMethodV(env, object, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallNonvirtualFloatMethodA
 */
static
jfloat JNICALL JNI_CallNonvirtualFloatMethodA(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jfloat result;

  va_startj(list, args);
  result = JNI_CallNonvirtualFloatMethodV(env, object, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallNonvirtualDoubleMethodA
 */
static
jdouble JNICALL JNI_CallNonvirtualDoubleMethodA(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jdouble result;

  va_startj(list, args);
  result = JNI_CallNonvirtualDoubleMethodV(env, object, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallNonvirtualVoidMethodA
 */
static
void JNICALL JNI_CallNonvirtualVoidMethodA(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;

  va_startj(list, args);
  JNI_CallNonvirtualVoidMethodV(env, object, clazz, methodID, list);
  va_endj(list);
}

/* CallStaticObjectMethodA
 */
static
jobject JNICALL JNI_CallStaticObjectMethodA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jobject result;

  va_startj(list, args);
  result = JNI_CallStaticObjectMethodV(env, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallStaticBooleanMethodA
 */
static
jboolean JNICALL JNI_CallStaticBooleanMethodA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue*args)
{
  va_list list;
  jboolean result;

  va_startj(list, args);
  result = JNI_CallStaticBooleanMethodV(env, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallStaticByteMethodA
 */
static
jbyte JNICALL JNI_CallStaticByteMethodA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jbyte result;

  va_startj(list, args);
  result = JNI_CallStaticByteMethodV(env, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallStaticCharMethodA
 */
static
jchar JNICALL JNI_CallStaticCharMethodA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jchar result;

  va_startj(list, args);
  result = JNI_CallStaticCharMethodV(env, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallStaticShortMethodA
 */
static
jshort JNICALL JNI_CallStaticShortMethodA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jshort result;

  va_startj(list, args);
  result = JNI_CallStaticShortMethodV(env, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallStaticIntMethodA
 */
static
jint JNICALL JNI_CallStaticIntMethodA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jint result;

  va_startj(list, args);
  result = JNI_CallStaticIntMethodV(env, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallStaticLongMethodA
 */
static
jlong JNICALL JNI_CallStaticLongMethodA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jlong result;

  va_startj(list, args);
  result = JNI_CallStaticLongMethodV(env, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallStaticFloatMethodA
 */
static
jfloat JNICALL JNI_CallStaticFloatMethodA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jfloat result;

  va_startj(list, args);
  result = JNI_CallStaticFloatMethodV(env, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallStaticDoubleMethodA
 */
static
jdouble JNICALL JNI_CallStaticDoubleMethodA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;
  jdouble result;

  va_startj(list, args);
  result = JNI_CallStaticDoubleMethodV(env, clazz, methodID, list);
  va_endj(list);
  return result;
}

/* CallStaticVoidMethodA
 */
static
void JNICALL JNI_CallStaticVoidMethodA(JNIEnv *env, jclass clazz, jmethodID methodID, jvalue *args)
{
  va_list list;

  va_startj(list, args);
  JNI_CallStaticVoidMethodV(env, clazz, methodID, list);
  va_endj(list);
}

/* ExceptionDescribe
 */
static
void JNICALL JNI_ExceptionDescribe(JNIEnv *env)
{
  jthrowable thrown = NULL;
  jmethodID methodID;

  thrown = JNI_ExceptionOccurred(env);

  if (thrown == NULL)
    goto exit;

  JNI_ExceptionClear(env);

  methodID = JNI_GetMethodID(env, GETCLASS(thrown), "printStackTrace", "()V");
  if (methodID == NULL)
    goto exit;

  JNI_CallVoidMethod(env, thrown, methodID);
  if (env->thrown != NULL)
    goto exit;

  JNI_Throw(env, thrown);

exit:
  
  JNI_DeleteLocalRef(env, thrown);
}

/* GetBooleanArrayRegion
 */
static
void JNICALL JNI_GetBooleanArrayRegion(JNIEnv *env, jbooleanArray array, jsize start, jsize length, jboolean *buffer)
{
  jsize array_length;
  jsize end;
  jboolean *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetBooleanArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    buffer[i-start] = elements[i];

  JNI_ReleaseBooleanArrayElements(env, array, elements, JNI_ABORT);
}

/* GetByteArrayRegion
 */
static
void JNICALL JNI_GetByteArrayRegion(JNIEnv *env, jbyteArray array, jsize start, jsize length, jbyte *buffer)
{
  jsize array_length;
  jsize end;
  jbyte *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetByteArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    buffer[i-start] = elements[i];

  JNI_ReleaseByteArrayElements(env, array, elements, JNI_ABORT);
}

/* GetCharArrayRegion
 */
static
void JNICALL JNI_GetCharArrayRegion(JNIEnv *env, jcharArray array, jsize start, jsize length, jchar *buffer)
{
  jsize array_length;
  jsize end;
  jchar *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetCharArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    buffer[i-start] = elements[i];

  JNI_ReleaseCharArrayElements(env, array, elements, JNI_ABORT);
}

/* GetShortArrayRegion
 */
static
void JNICALL JNI_GetShortArrayRegion(JNIEnv *env, jshortArray array, jsize start, jsize length, jshort *buffer)
{
  jsize array_length;
  jsize end;
  jshort *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetShortArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    buffer[i-start] = elements[i];

  JNI_ReleaseShortArrayElements(env, array, elements, JNI_ABORT);
}

/* GetIntArrayRegion
 */
static
void JNICALL JNI_GetIntArrayRegion(JNIEnv *env, jintArray array, jsize start, jsize length, jint *buffer)
{
  jsize array_length;
  jsize end;
  jint *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetIntArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    buffer[i-start] = elements[i];

  JNI_ReleaseIntArrayElements(env, array, elements, JNI_ABORT);
}

/* GetLongArrayRegion
 */
static
void JNICALL JNI_GetLongArrayRegion(JNIEnv *env, jlongArray array, jsize start, jsize length, jlong *buffer)
{
  jsize array_length;
  jsize end;
  jlong *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetLongArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    buffer[i-start] = elements[i];

  JNI_ReleaseLongArrayElements(env, array, elements, JNI_ABORT);
}

/* GetFloatArrayRegion
 */
static
void JNICALL JNI_GetFloatArrayRegion(JNIEnv *env, jfloatArray array, jsize start, jsize length, jfloat *buffer)
{
  jsize array_length;
  jsize end;
  jfloat *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetFloatArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    buffer[i-start] = elements[i];

  JNI_ReleaseFloatArrayElements(env, array, elements, JNI_ABORT);
}

/* GetDoubleArrayRegion
 */
static
void JNICALL JNI_GetDoubleArrayRegion(JNIEnv *env, jdoubleArray array, jsize start, jsize length, jdouble *buffer)
{
  jsize array_length;
  jsize end;
  jdouble *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetDoubleArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    buffer[i-start] = elements[i];

  JNI_ReleaseDoubleArrayElements(env, array, elements, JNI_ABORT);
}

/* SetBooleanArrayRegion
 */
static
void JNICALL JNI_SetBooleanArrayRegion(JNIEnv *env, jbooleanArray array, jsize start, jsize length, jboolean *buffer)
{
  jsize array_length;
  jsize end;
  jboolean *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetBooleanArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    elements[i] = buffer[i-start];

  JNI_ReleaseBooleanArrayElements(env, array, elements, JNI_COMMIT);
}

/* SetByteArrayRegion
 */
static
void JNICALL JNI_SetByteArrayRegion(JNIEnv *env, jbyteArray array, jsize start, jsize length, jbyte *buffer)
{
  jsize array_length;
  jsize end;
  jbyte *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetByteArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    elements[i] = buffer[i-start];

  JNI_ReleaseByteArrayElements(env, array, elements, JNI_COMMIT);
}

/* SetCharArrayRegion
 */
static
void JNICALL JNI_SetCharArrayRegion(JNIEnv *env, jcharArray array, jsize start, jsize length, jchar *buffer)
{
  jsize array_length;
  jsize end;
  jchar *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetCharArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    elements[i] = buffer[i-start];

  JNI_ReleaseCharArrayElements(env, array, elements, JNI_COMMIT);
}

/* SetShortArrayRegion
 */
static
void JNICALL JNI_SetShortArrayRegion(JNIEnv *env, jshortArray array, jsize start, jsize length, jshort *buffer)
{
  jsize array_length;
  jsize end;
  jshort *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetShortArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    elements[i] = buffer[i-start];

  JNI_ReleaseShortArrayElements(env, array, elements, JNI_COMMIT);
}

/* SetIntArrayRegion
 */
static
void JNICALL JNI_SetIntArrayRegion(JNIEnv *env, jintArray array, jsize start, jsize length, jint *buffer)
{
  jsize array_length;
  jsize end;
  jint *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetIntArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    elements[i] = buffer[i-start];

  JNI_ReleaseIntArrayElements(env, array, elements, JNI_COMMIT);
}

/* SetLongArrayRegion
 */
static
void JNICALL JNI_SetLongArrayRegion(JNIEnv *env, jlongArray array, jsize start, jsize length, jlong *buffer)
{
  jsize array_length;
  jsize end;
  jlong *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetLongArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    elements[i] = buffer[i-start];

  JNI_ReleaseLongArrayElements(env, array, elements, JNI_COMMIT);
}

/* SetFloatArrayRegion
 */
static
void JNICALL JNI_SetFloatArrayRegion(JNIEnv *env, jfloatArray array, jsize start, jsize length, jfloat *buffer)
{
  jsize array_length;
  jsize end;
  jfloat *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetFloatArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    elements[i] = buffer[i-start];

  JNI_ReleaseFloatArrayElements(env, array, elements, JNI_COMMIT);
}

/* SetDoubleArrayRegion
 */
static
void JNICALL JNI_SetDoubleArrayRegion(JNIEnv *env, jdoubleArray array, jsize start, jsize length, jdouble *buffer)
{
  jsize array_length;
  jsize end;
  jdouble *elements;
  jsize i;

  array_length = LENGTH(array);

  if (start < 0 || start > array_length) {
    ThrowBounds(env, start);
    return;
  }
  end = start+length;
  if (end < start || end > array_length) {
    ThrowBounds(env, end);
    return;
  }

  elements = JNI_GetDoubleArrayElements(env, array, NULL);
  if (elements == NULL)
    return;

  for (i = start; i < end; i++)
    elements[i] = buffer[i-start];

  JNI_ReleaseDoubleArrayElements(env, array, elements, JNI_COMMIT);
}

/* GetStringLength
 */
static
jsize JNICALL JNI_GetStringLength(JNIEnv *env, jstring string)
{
  jmethodID methodID;

  methodID = JNI_GetMethodID(env, GETCLASS(string), "length", "()I");
  if (methodID == NULL)
    return 0;
  
  return JNI_CallIntMethod(env, string, methodID);
}

/* ExceptionCheck
 */
static
jboolean JNICALL JNI_ExceptionCheck(JNIEnv *env)
{
  return env->thrown != NULL;
}

/* GetStringChars
 */
static
const jchar *JNICALL JNI_GetStringChars(JNIEnv *env, jstring string, jboolean *isCopy)
{
  jmethodID methodID;
  jsize length;
  jcharArray array = NULL;
  jchar *elements;
  jchar *chars = NULL;
  jsize i;

  methodID = JNI_GetMethodID(env, GETCLASS(string), "getChars", "(II[CI)V");
  if (methodID == NULL)
    goto exit;

  length = JNI_GetStringLength(env, string);
  if (env->thrown != NULL)
    goto exit;

  array = JNI_NewCharArray(env, length);
  if (array == NULL)
    goto exit;

  JNI_CallVoidMethod(env, string, methodID, 0, length, array, 0);
  if (env->thrown != NULL)
    goto exit;

  elements = JNI_GetCharArrayElements(env, array, NULL);
  if (elements == NULL)
    goto exit;

  chars = (jchar*)malloc(length*sizeof(jchar));
  if (chars == NULL) {
    JNI_ReleaseCharArrayElements(env, array, elements, JNI_ABORT);
    ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
    goto exit;
  }

  for (i = 0; i < length; i++)
    chars[i] = elements[i];

  JNI_ReleaseCharArrayElements(env, array, elements, JNI_ABORT);

  if (isCopy != NULL)
    *isCopy = JNI_TRUE;

exit:

  JNI_DeleteLocalRef(env, array);
  return chars;
}

/* ReleaseStringChars
 */
static
void JNICALL JNI_ReleaseStringChars(JNIEnv *env, jstring string, const jchar *chars)
{
  free((jchar*)chars);
}

/* GetStringRegion
 */
static
void JNICALL JNI_GetStringRegion(JNIEnv *env, jstring string, jsize start, jsize length, jchar *buffer)
{
  jmethodID methodID;
  jcharArray array = NULL;
  jchar *elements;
  jsize i;

  methodID = JNI_GetMethodID(env, GETCLASS(string), "getChars", "(II[CI)V");
  if (methodID == NULL)
    goto exit;

  array = JNI_NewCharArray(env, length);
  if (array == NULL)
    goto exit;

  JNI_CallVoidMethod(env, string, methodID, start, length, array, 0);
  if (env->thrown != NULL)
    goto exit;

  elements = JNI_GetCharArrayElements(env, array, NULL);
  if (elements == NULL)
    goto exit;

  for (i = 0; i < length; i++)
    buffer[i] = elements[i];

  JNI_ReleaseCharArrayElements(env, array, elements, JNI_ABORT);

exit:

  JNI_DeleteLocalRef(env, array);
}

/* GetStringCritical
 */
static
const jchar *JNICALL JNI_GetStringCritical(JNIEnv *env, jstring string, jboolean *isCopy)
{
  return JNI_GetStringChars(env, string, isCopy);
}

/* ReleaseStringCritical
 */
static
void JNICALL JNI_ReleaseStringCritical(JNIEnv *env, jstring string, const jchar *chars)
{
  JNI_ReleaseStringChars(env, string, chars);
}

/* GetJavaVM
 */
static
jint JNICALL JNI_GetJavaVM(JNIEnv *env, JavaVM **pjvm)
{
  *pjvm = env->jvm;
  return JNI_OK;
}

/* GetBooleanArrayElements
 */
static
jboolean *JNICALL JNI_GetBooleanArrayElements(JNIEnv *env, jbooleanArray array, jboolean *isCopy)
{
  if (isCopy != NULL)
    *isCopy = JNI_FALSE;
  return ARRAY_ELEMENTS(array, jboolean);
}

/* GetByteArrayElements
 */
static
jbyte *JNICALL JNI_GetByteArrayElements(JNIEnv *env, jbyteArray array, jboolean *isCopy)
{
  if (isCopy != NULL)
    *isCopy = JNI_FALSE;
  return ARRAY_ELEMENTS(array, jbyte);
}

/* GetCharArrayElements
 */
static
jchar *JNICALL JNI_GetCharArrayElements(JNIEnv *env, jcharArray array, jboolean *isCopy)
{
  if (isCopy != NULL)
    *isCopy = JNI_FALSE;
  return ARRAY_ELEMENTS(array, jchar);
}

/* GetShortArrayElements
 */
static
jshort *JNICALL JNI_GetShortArrayElements(JNIEnv *env, jshortArray array, jboolean *isCopy)
{
  if (isCopy != NULL)
    *isCopy = JNI_FALSE;
  return ARRAY_ELEMENTS(array, jshort);
}

/* GetIntArrayElements
 */
static
jint *JNICALL JNI_GetIntArrayElements(JNIEnv *env, jintArray array, jboolean *isCopy)
{
  if (isCopy != NULL)
    *isCopy = JNI_FALSE;
  return ARRAY_ELEMENTS(array, jint);
}

/* GetLongArrayElements
 */
static
jlong *JNICALL JNI_GetLongArrayElements(JNIEnv *env, jlongArray array, jboolean *isCopy)
{
  if (isCopy != NULL)
    *isCopy = JNI_FALSE;
  return ARRAY_ELEMENTS(array, jlong);
}

/* GetFloatArrayElements
 */
static
jfloat *JNICALL JNI_GetFloatArrayElements(JNIEnv *env, jfloatArray array, jboolean *isCopy)
{
  if (isCopy != NULL)
    *isCopy = JNI_FALSE;
  return ARRAY_ELEMENTS(array, jfloat);
}

/* GetDoubleArrayElements
 */
static
jdouble *JNICALL JNI_GetDoubleArrayElements(JNIEnv *env, jdoubleArray array, jboolean *isCopy)
{
  if (isCopy != NULL)
    *isCopy = JNI_FALSE;
  return ARRAY_ELEMENTS(array, jdouble);
}

/* NewObjectArray
 */
static
jobjectArray JNICALL JNI_NewObjectArray(JNIEnv *env, jsize length, jclass clazz, jobject value)
{
  const struct classInfo *classInfo;
  jint dimensions;
  char *name;
  jclass arrayClass;
  jobjectArray array;
  jint i;
  jobject *elements;

  classInfo = CLASS_INFO(clazz);
  dimensions = (jint)classInfo->dimensions & 0xFF;
  if (classInfo->name[0] == '<' || dimensions == 255) {
    ThrowByName(env, "java/lang/InstantiationException", NULL);
    return NULL;
  }

  if (value != NULL)
    if (!IsSubtypeOf(GETCLASS(value), clazz)) {
      ThrowByName(env, "java/lang/ArrayStoreException", NULL);
      return NULL;
    }

  name = (char*)malloc(3+strlen(classInfo->name)+1);
  if (name == NULL) {
    ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
    return NULL;
  }

  strcpy(name, "[");
  if (dimensions > 0)
    strcat(name, classInfo->name);
  else {
    strcat(name, "L");
    strcat(name, classInfo->name);
    strcat(name, ";");
  }

  arrayClass = FindClassFromClassLoader(env, classInfo->loader, name);
  free(name);
  if (arrayClass == NULL)
    return NULL;
   
  array = AllocArray(env, arrayClass, length);
  JNI_DeleteLocalRef(env, arrayClass);
  if (array == NULL)
    return NULL;

  if (value != NULL) {
    elements = ARRAY_ELEMENTS(array, jobject);
    mutex_lock(&env->lock);
    for (i = 0; i < length; i++)
      elements[i] = value;
    mutex_unlock(&env->lock);
  }

  return array;
}

/* DefineClass
 */
static
jclass JNICALL JNI_DefineClass(JNIEnv *env, const char *name, jobject loader, const jbyte *buffer, jsize length)
{ 
  jstring string = NULL;
  jbyteArray array = NULL;
  jbyte *elements;
  jint i;
  jmethodID methodID;
  jclass clazz = NULL;
  char *dotted;
  jclass cloaderClass = NULL;

  cloaderClass = JNI_FindClass(env, "java/lang/ClassLoader");
  if (cloaderClass == NULL)
    goto exit;

  methodID = JNI_GetStaticMethodID(env, cloaderClass, "defineClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;[BII)Ljava/lang/Class;");
  if (methodID == NULL)
    goto exit;

  dotted = strdup(name);
  if (dotted == NULL) {
    ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
    goto exit;
  }

  for (i = 0; dotted[i] != '\0'; i++)
    if (dotted[i] == '/')
      dotted[i] = '.';

  string = JNI_NewStringUTF(env, dotted);
  free(dotted);
  if (string == NULL)
    goto exit;

  array = JNI_NewByteArray(env, length);
  if (array == NULL)
    goto exit;

  elements = JNI_GetByteArrayElements(env, array, NULL);
  if (elements == NULL)
    goto exit;

  for (i = 0; i < length; i++)
    elements[i] = buffer[i];

  JNI_ReleaseByteArrayElements(env, array, elements, JNI_COMMIT);

  clazz = JNI_CallStaticObjectMethod(env, cloaderClass, methodID, loader, string, array, 0, length);
  if (env->thrown != NULL)
    goto exit;

exit:

  JNI_DeleteLocalRef(env, array);
  JNI_DeleteLocalRef(env, string);
  JNI_DeleteLocalRef(env, cloaderClass);
  return clazz;
}

/* FindClass
 */
static
jclass JNICALL JNI_FindClass(JNIEnv *env, const char *name)
{
  jclass current = NULL;
  const struct classInfo *classInfo;
  jmethodID methodID;
  jclass clazz = NULL;
  jobject loader = NULL;
  jobject thread;

  current = CallerClass(env, 0);
  if (env->thrown != NULL)
    goto exit;

  if (current != NULL) {
    classInfo = CLASS_INFO(current);
    loader = JNI_NewLocalRef(env, classInfo->loader);
  } else {
    thread = env->thread;
    if (thread != NULL) {
      methodID = JNI_GetMethodID(env, GETCLASS(thread), "getContextClassLoader", "()Ljava/lang/ClassLoader;");
      if (methodID == NULL)
        goto exit;
  
      loader = JNI_CallObjectMethod(env, thread, methodID);
      if (env->thrown != NULL)
        goto exit;
    }
  }

  clazz = FindClassFromClassLoader(env, loader, name);
  if (clazz == NULL)
    goto exit;

exit:

  JNI_DeleteLocalRef(env, loader);
  JNI_DeleteLocalRef(env, current);
  return clazz;
}

/* ToReflectedField
 */
static
jobject JNICALL JNI_ToReflectedField(JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  const struct fieldInfo *fieldInfo;
  jclass fieldClass = NULL;
  jmethodID constructorID;
  jclass declaringClass = NULL;
  const struct classInfo *classInfo;
  const struct fieldInfo *fields;
  jchar index;
  jclass type = NULL;
  char *descriptor;
  jstring name = NULL;
  jobject field = NULL;

  fieldInfo = FIELD_INFO(fieldID);

  fieldClass = JNI_FindClass(env, "java/lang/reflect/Field");
  if (fieldClass == NULL)
    goto exit;

  constructorID = JNI_GetMethodID(env, fieldClass, "<init>", "(Ljava/lang/Class;CCLjava/lang/Class;Ljava/lang/String;)V");
  if (constructorID == NULL)
    return NULL;

  declaringClass = JNI_NewLocalRef(env, fieldInfo->declaringClass);

  classInfo = CLASS_INFO(declaringClass);
  fields = classInfo->reflect->fields;
  for (index = 0; fields[index].declaringClass != NULL; index++)
    if (fieldInfo == &fields[index])
      break;

  switch (fieldInfo->descriptor[0]) {
  default:
    type = GetPrimitiveClass(env, fieldInfo->descriptor[0]);
    break;
  case '[':
    type = FindClassFromClassLoader(env, classInfo->loader, fieldInfo->descriptor);
    break;
  case 'L':
    descriptor = strdup(fieldInfo->descriptor+1);
    if (descriptor == NULL) {
      ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
      goto exit;
    }
    descriptor[strlen(descriptor)-1] = '\0';
    type = FindClassFromClassLoader(env, classInfo->loader, descriptor);
    free(descriptor);
    break;
  }
  if (type == NULL)
    goto exit;

  name = JNI_NewStringUTF(env, fieldInfo->name);
  if (name == NULL)
    goto exit;

  field = JNI_NewObject(env, fieldClass, constructorID, declaringClass, index, fieldInfo->accessFlags, type, name);
  if (field == NULL)
    goto exit;
  
exit:

  JNI_DeleteLocalRef(env, name);
  JNI_DeleteLocalRef(env, type);
  JNI_DeleteLocalRef(env, declaringClass);
  JNI_DeleteLocalRef(env, fieldClass);
  return field;
}

/* ToReflectedMethod
 */
static
jobject JNICALL JNI_ToReflectedMethod(JNIEnv *env, jclass clazz, jmethodID methodID)
{
  const struct methodInfo *methodInfo;
  jclass methodClass = NULL;
  jmethodID constructorID;
  jclass declaringClass = NULL;
  const struct classInfo *classInfo;
  const struct methodInfo *methods;
  jchar index;
  jclass type = NULL;
  char *typeName;
  char *descriptor;
  jstring name = NULL;
  jsize length;
  jclass classClass = NULL;
  jobjectArray parameters = NULL;
  jint start;
  jint end;
  jint i;
  jclass parameter;
  jobjectArray exceptions = NULL;
  jclass exception;
  jobject method = NULL;

  methodInfo = METHOD_INFO(methodID);

  if (methodInfo->name[0] == '<') {

    methodClass = JNI_FindClass(env, "java/lang/reflect/Constructor");
    if (methodClass == NULL)
      goto exit;

    constructorID = JNI_GetMethodID(env, methodClass, "<init>", "(Ljava/lang/Class;CC[Ljava/lang/Class;[Ljava/lang/Class;)V");
    if (constructorID == NULL)
      goto exit;

  } else {

    methodClass = JNI_FindClass(env, "java/lang/reflect/Method");
    if (methodClass == NULL)
      goto exit;

    constructorID = JNI_GetMethodID(env, methodClass, "<init>", "(Ljava/lang/Class;CCLjava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Class;)V");
    if (constructorID == NULL)
      goto exit;

  }

  declaringClass = JNI_NewLocalRef(env, methodInfo->declaringClass);

  classInfo = CLASS_INFO(declaringClass);
  methods = classInfo->reflect->methods;
  for (index = 0; methods[index].declaringClass != NULL; index++)
    if (methodInfo == &methods[index])
      break;

  if (methodInfo->name[0] != '<') {

    typeName = NULL;
    for (i = 0; methodInfo->descriptor[i] != '\0'; i++)
      if (methodInfo->descriptor[i] == ')') {
        typeName = (char*)&methodInfo->descriptor[i+1];
        break;
      }

    switch (typeName[0]) {
    default:
      type = GetPrimitiveClass(env, typeName[0]);
      break;
    case '[':
      type = FindClassFromClassLoader(env, classInfo->loader, typeName);
      break;
    case 'L':
      descriptor = strdup(typeName+1);
      if (descriptor == NULL) {
        ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
        goto exit;
      }
      descriptor[strlen(descriptor)-1] = '\0';
      type = FindClassFromClassLoader(env, classInfo->loader, descriptor);
      free(descriptor);
      break;
    }
    if (type == NULL)
      goto exit;

    name = JNI_NewStringUTF(env, methodInfo->name);
    if (name == NULL)
      goto exit;

  }

  length = 0;
  for (i = 1; methodInfo->descriptor[i] != ')'; i++) {
    while (methodInfo->descriptor[i] == '[')
      i++;
    if (methodInfo->descriptor[i] == 'L')
      while (methodInfo->descriptor[i] != ';')
        i++;
    length++;
  }

  classClass = JNI_FindClass(env, "java/lang/Class");
  if (classClass == NULL)
    goto exit;

  parameters = JNI_NewObjectArray(env, length, classClass, NULL);
  if (parameters == NULL)
    goto exit;

  start = 1;
  for (i = 0; i < length; i++) {
    end = start;
    while (methodInfo->descriptor[end] == '[')
      end++;
    if (methodInfo->descriptor[end] == 'L')
      while (methodInfo->descriptor[end] != ';')
        end++;
    end++;

    switch (methodInfo->descriptor[start]) {
    default:
      parameter = GetPrimitiveClass(env, methodInfo->descriptor[start]);
      break;
    case '[':
      typeName = (char*)malloc(end-start+1);
      if (typeName == NULL) {
        ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
        goto exit;
      }
      strncpy(typeName, &methodInfo->descriptor[start], end-start);
      typeName[end-start] = '\0';
      parameter = FindClassFromClassLoader(env, classInfo->loader, typeName);
      free(typeName);
      break;
    case 'L':
      typeName = (char*)malloc(end-start-1);
      if (typeName == NULL) {
        ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
        goto exit;
      }
      strncpy(typeName, &methodInfo->descriptor[start+1], end-start-1);
      typeName[end-start-2] = '\0';
      parameter = FindClassFromClassLoader(env, classInfo->loader, typeName);
      free(typeName);
      break;
    }
    if (parameter == NULL)
      goto exit;
    JNI_SetObjectArrayElement(env, parameters, i, parameter);
    JNI_DeleteLocalRef(env, parameter);
    if (env->thrown != NULL)
      goto exit;

    start = end;
  }

  length = 0;
  if (methodInfo->exceptions != NULL)
    for (i = 0; methodInfo->exceptions[i] != NULL; i++)
      length++;

  exceptions = JNI_NewObjectArray(env, length, classClass, NULL);
  if (exceptions == NULL)
    goto exit;

  if (methodInfo->exceptions != NULL)
    for (i = 0; methodInfo->exceptions[i] != NULL; i++) {
      exception = FindClassFromClassLoader(env, classInfo->loader, methodInfo->exceptions[i]);
      if (exception == NULL)
        goto exit;
      JNI_SetObjectArrayElement(env, exceptions, i, exception);
      JNI_DeleteLocalRef(env, exception);
      if (env->thrown != NULL)
        goto exit;
    }

  if (methodInfo->name[0] == '<') {

    method = JNI_NewObject(env, methodClass, constructorID, declaringClass, index, methodInfo->accessFlags, parameters, exceptions);
    if (method == NULL)
      goto exit;

  } else {

    method = JNI_NewObject(env, methodClass, constructorID, declaringClass, index, methodInfo->accessFlags, type, name, parameters, exceptions);
    if (method == NULL)
      goto exit;

  }

exit:

  JNI_DeleteLocalRef(env, exceptions);
  JNI_DeleteLocalRef(env, parameters);
  JNI_DeleteLocalRef(env, classClass);
  JNI_DeleteLocalRef(env, name);
  JNI_DeleteLocalRef(env, type);
  JNI_DeleteLocalRef(env, declaringClass);
  JNI_DeleteLocalRef(env, methodClass);
  return method;
}

/* FromReflectedField
 */
static
jfieldID JNICALL JNI_FromReflectedField(JNIEnv *env, jobject field)
{
  jmethodID methodID;
  jint index;
  jclass clazz = NULL;
  const struct classInfo *classInfo;
  const struct fieldInfo *fieldInfo;
  jfieldID result = NULL;

  methodID = JNI_GetMethodID(env, GETCLASS(field), "getIndex", "()I");
  if (methodID == NULL)
    goto exit;

  index = JNI_CallIntMethod(env, field, methodID);
  if (env->thrown != NULL)
    goto exit;

  methodID = JNI_GetMethodID(env, GETCLASS(field), "getDeclaringClass", "()Ljava/lang/Class;");
  if (methodID == NULL)
    goto exit;

  clazz = JNI_CallObjectMethod(env, field, methodID);
  if (env->thrown != NULL)
    goto exit;

  if (clazz == NULL) {
    ThrowByName(env, "java/lang/NullPointerException", NULL);
    goto exit;
  }

  LoadMetadata(env, clazz);
  if (env->thrown != NULL)
    goto exit;

  classInfo = CLASS_INFO(clazz);
  fieldInfo = &classInfo->reflect->fields[index];
  result = FIELD_ID(fieldInfo);

exit:

  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* FromReflectedMethod
 */
static
jmethodID JNICALL JNI_FromReflectedMethod(JNIEnv *env, jobject method)
{
  jmethodID methodID;
  jint index;
  jclass clazz = NULL;
  const struct classInfo *classInfo;
  const struct methodInfo *methodInfo;
  jmethodID result = NULL;

  methodID = JNI_GetMethodID(env, GETCLASS(method), "getIndex", "()I");
  if (methodID == NULL)
    goto exit;

  index = JNI_CallIntMethod(env, method, methodID);
  if (env->thrown != NULL)
    goto exit;

  methodID = JNI_GetMethodID(env, GETCLASS(method), "getDeclaringClass", "()Ljava/lang/Class;");
  if (methodID == NULL)
    goto exit;

  clazz = JNI_CallObjectMethod(env, method, methodID);
  if (env->thrown != NULL)
    goto exit;

  if (clazz == NULL) {
    ThrowByName(env, "java/lang/NullPointerException", NULL);
    goto exit;
  }

  LoadMetadata(env, clazz);
  if (env->thrown != NULL)
    goto exit;

  classInfo = CLASS_INFO(clazz);
  methodInfo = &classInfo->reflect->methods[index];
  result = METHOD_ID(methodInfo);

exit:

  JNI_DeleteLocalRef(env, clazz);
  return result;
}

/* NewGlobalRef
 */
static
jobject JNICALL JNI_NewGlobalRef(JNIEnv *env, jobject object)
{
  JavaVM *jvm;
  jobject element;
  jint base;
  jint index;
  jint i;
  JNIHasht *hasht;

  if (object == NULL)
    return NULL;

  jvm = env->jvm;
  mutex_lock(&jvm->lock);

  hasht = jvm->globals;
  if (hasht == NULL) {
    hasht = (JNIHasht*)calloc(1, sizeof(JNIHasht)+16*sizeof(jobject));
    if (hasht == NULL) {
      mutex_unlock(&jvm->lock);
      JNI_FatalError(env, "could not create global reference");
      return NULL;
    }
    hasht->capacity = 16;
    jvm->globals = hasht;
  }

  if (hasht->size/3 > hasht->capacity/4) {
    hasht = (JNIHasht*)realloc(hasht, sizeof(JNIHasht)+(2*hasht->capacity)*sizeof(jobject));
    if (hasht == NULL) {
      mutex_unlock(&jvm->lock);
      JNI_FatalError(env, "could not create global reference");
      return NULL;
    }

    for (i = hasht->capacity; i < 2*hasht->capacity; i++)
      hasht->entries[i] = NULL;

    for (i = 0; i < hasht->capacity; i++) {
      element = hasht->entries[i];
      if (element != NULL) {
        index = (jint)element % (2*hasht->capacity);
        if (hasht->entries[index] == NULL) {
          hasht->entries[index] = element;
          hasht->entries[i] = NULL;
        }
      }
    }

    hasht->capacity = 2*hasht->capacity;
    jvm->globals = hasht;
  }

  base = (jint)object % hasht->capacity;

  for (i = 0; i < hasht->capacity; i++) {
    index = (base+i) % hasht->capacity;
    if (hasht->entries[index] == NULL) {
      hasht->entries[index] = object;
      hasht->size++;
      break;
    }
  }

  mutex_unlock(&jvm->lock);
  return object;
}

/* DeleteGlobalRef
 */
static
void JNICALL JNI_DeleteGlobalRef(JNIEnv *env, jobject object)
{
  JavaVM *jvm;
  jint base;
  jint index;
  jint i;
  JNIHasht *hasht;
  jboolean found;

  if (object == NULL)
    return;

  found = JNI_FALSE;

  jvm = env->jvm;
  mutex_lock(&jvm->lock);

  hasht = jvm->globals;
  if (hasht != NULL && hasht->size > 0) {
    base = (jint)object % hasht->capacity;
    for (i = 0; i < hasht->capacity; i++) {
      index = (base+i) % hasht->capacity;
      if (hasht->entries[index] == object) {
        hasht->entries[index] = NULL;
        hasht->size--;
        found = JNI_TRUE;
        break;
      }
    }
  }

  mutex_unlock(&jvm->lock);

  if (!found)
    JNI_FatalError(env, "could not delete global reference");
}

/* NewWeakGlobalRef
 */
static
jweak JNICALL JNI_NewWeakGlobalRef(JNIEnv *env, jobject object)
{
  JavaVM *jvm;
  jobject element;
  jint base;
  jint index;
  jint i;
  JNIHasht *hasht;

  if (object == NULL)
    return NULL;

  jvm = env->jvm;
  mutex_lock(&jvm->lock);

  hasht = jvm->weaks;
  if (hasht == NULL) {
    hasht = (JNIHasht*)calloc(1, sizeof(JNIHasht)+16*sizeof(jobject));
    if (hasht == NULL) {
      mutex_unlock(&jvm->lock);
      JNI_FatalError(env, "could not create global reference");
      return NULL;
    }
    hasht->capacity = 16;
    jvm->weaks = hasht;
  }

  if (hasht->size/3 > hasht->capacity/4) {
    hasht = (JNIHasht*)realloc(hasht, sizeof(JNIHasht)+(2*hasht->capacity)*sizeof(jobject));
    if (hasht == NULL) {
      mutex_unlock(&jvm->lock);
      JNI_FatalError(env, "could not create global reference");
      return NULL;
    }

    for (i = hasht->capacity; i < 2*hasht->capacity; i++)
      hasht->entries[i] = NULL;

    for (i = 0; i < hasht->capacity; i++) {
      element = hasht->entries[i];
      if (element != NULL) {
        index = (jint)element % (2*hasht->capacity);
        if (hasht->entries[index] == NULL) {
          hasht->entries[index] = element;
          hasht->entries[i] = NULL;
        }
      }
    }

    hasht->capacity = 2*hasht->capacity;
    jvm->weaks = hasht;
  }

  base = (jint)object % hasht->capacity;

  for (i = 0; i < hasht->capacity; i++) {
    index = (base+i) % hasht->capacity;
    if (hasht->entries[index] == NULL) {
      hasht->entries[index] = object;
      hasht->size++;
      break;
    }
  }

  mutex_unlock(&jvm->lock);
  return (jweak)object;
}

/* DeleteWeakGlobalRef
 */
static
void JNICALL JNI_DeleteWeakGlobalRef(JNIEnv *env, jweak weak)
{
  jobject object;
  JavaVM *jvm;
  jint base;
  jint index;
  jint i;
  JNIHasht *hasht;

  object = (jobject)weak;
  if (object == NULL)
    return;

  jvm = env->jvm;
  mutex_lock(&jvm->lock);

  hasht = jvm->weaks;
  if (hasht != NULL)
    if (hasht->size > 0) {
      base = (jint)object % hasht->capacity;
      for (i = 0; i < hasht->capacity; i++) {
        index = (base+i) % hasht->capacity;
        if (hasht->entries[index] == object) {
          hasht->entries[index] = NULL;
          hasht->size--;
          break;
        }
      }
    }

  mutex_unlock(&jvm->lock);
}

/* IsSameObject
 */
static
jboolean JNICALL JNI_IsSameObject(JNIEnv *env, jobject one, jobject another)
{ 
  JavaVM *jvm;
  JNIHasht *hasht;
  jint i;
  JNIFrame *frame;

  if (one == another)
    return JNI_TRUE;
  if (one == NULL) {
    one = another;
    another = NULL;
  }
  if (another != NULL)
    return JNI_FALSE;
  jvm = env->jvm;
  mutex_lock(&jvm->lock);
  hasht = jvm->weaks;
  if (hasht != NULL)
    for (i = 0; i < hasht->capacity; i++)
      if (hasht->entries[i] == one) {
        mutex_unlock(&jvm->lock);
        return JNI_FALSE;
      }
  hasht = jvm->globals;
  if (hasht != NULL)
    for (i = 0; i < hasht->capacity; i++)
      if (hasht->entries[i] == one) {
        mutex_unlock(&jvm->lock);
        return JNI_FALSE;
      }
  mutex_unlock(&jvm->lock);
  for (frame = env->topFrame; frame != NULL; frame = frame->previous)
    for (i = 0; i < frame->size; i++)
      if (frame->entries[i] == one)
        return JNI_FALSE;
  return JNI_TRUE;
}

/* - - - - - - - -  end of reviewed code  - - - - - - - - */

/* CallObjectMethodV
 */
static
jobject JNICALL JNI_CallObjectMethodV(JNIEnv *env, jobject object, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jobject result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareMethodCall(env, object, methodID);
  if (text == NULL)
    return NULL;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeObject(env, text, object, size, args, env->callback);
  result = JNI_NewLocalRef(env, result);
  SwitchFromJava(env);
  return result;
}

/* CallBooleanMethodV
 */
static
jboolean JNICALL JNI_CallBooleanMethodV(JNIEnv *env, jobject object, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jboolean result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareMethodCall(env, object, methodID);
  if (text == NULL)
    return JNI_FALSE;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeBoolean(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallByteMethodV
 */
static
jbyte JNICALL JNI_CallByteMethodV(JNIEnv *env, jobject object, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jbyte result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeByte(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallCharMethodV
 */
static
jchar JNICALL JNI_CallCharMethodV(JNIEnv *env, jobject object, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jchar result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeChar(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallShortMethodV
 */
static
jshort JNICALL JNI_CallShortMethodV(JNIEnv *env, jobject object, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jshort result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeShort(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallIntMethodV
 */
static
jint JNICALL JNI_CallIntMethodV(JNIEnv *env, jobject object, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jint result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeInt(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallLongMethodV
 */
static
jlong JNICALL JNI_CallLongMethodV(JNIEnv *env, jobject object, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jlong result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeLong(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallFloatMethodV
 */
static
jfloat JNICALL JNI_CallFloatMethodV(JNIEnv *env, jobject object, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jfloat result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeFloat(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallDoubleMethodV
 */
static
jdouble JNICALL JNI_CallDoubleMethodV(JNIEnv *env, jobject object, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jdouble result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeDouble(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallVoidMethodV
 */
static
void JNICALL JNI_CallVoidMethodV(JNIEnv *env, jobject object, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareMethodCall(env, object, methodID);
  if (text == NULL)
    return;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  invokeVoid(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
}

/* CallNonvirtualObjectMethodV
 */
static
jobject JNICALL JNI_CallNonvirtualObjectMethodV(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jobject result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareNonvirtualMethodCall(env, object, methodID);
  if (text == NULL)
    return NULL;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeObject(env, text, object, size, args, env->callback);
  result = JNI_NewLocalRef(env, result);
  SwitchFromJava(env);
  return result;
}

/* CallNonvirtualBooleanMethodV
 */
static
jboolean JNICALL JNI_CallNonvirtualBooleanMethodV(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jboolean result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareNonvirtualMethodCall(env, object, methodID);
  if (text == NULL)
    return JNI_FALSE;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeBoolean(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallNonvirtualByteMethodV
 */
static
jbyte JNICALL JNI_CallNonvirtualByteMethodV(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jbyte result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareNonvirtualMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeByte(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallNonvirtualCharMethodV
 */
static
jchar JNICALL JNI_CallNonvirtualCharMethodV(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jchar result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareNonvirtualMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeChar(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallNonvirtualShortMethodV
 */
static
jshort JNICALL JNI_CallNonvirtualShortMethodV(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jshort result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareNonvirtualMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeShort(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallNonvirtualIntMethodV
 */
static
jint JNICALL JNI_CallNonvirtualIntMethodV(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jint result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareNonvirtualMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeInt(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallNonvirtualLongMethodV
 */
static
jlong JNICALL JNI_CallNonvirtualLongMethodV(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jlong result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareNonvirtualMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeLong(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallNonvirtualFloatMethodV
 */
static
jfloat JNICALL JNI_CallNonvirtualFloatMethodV(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jfloat result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareNonvirtualMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeFloat(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallNonvirtualDoubleMethodV
 */
static
jdouble JNICALL JNI_CallNonvirtualDoubleMethodV(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jdouble result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareNonvirtualMethodCall(env, object, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeDouble(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallNonvirtualVoidMethodV
 */
static
void JNICALL JNI_CallNonvirtualVoidMethodV(JNIEnv *env, jobject object, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareNonvirtualMethodCall(env, object, methodID);
  if (text == NULL)
    return;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  invokeVoid(env, text, object, size, args, env->callback);
  SwitchFromJava(env);
}

/* CallStaticObjectMethodV
 */
static
jobject JNICALL JNI_CallStaticObjectMethodV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jobject result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareStaticMethodCall(env, methodID);
  if (text == NULL)
    return NULL;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeObject(env, text, NULL, size, args, env->callback);
  result = JNI_NewLocalRef(env, result);
  SwitchFromJava(env);
  return result;
}

/* CallStaticBooleanMethodV
 */
static
jboolean JNICALL JNI_CallStaticBooleanMethodV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jboolean result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareStaticMethodCall(env, methodID);
  if (text == NULL)
    return JNI_FALSE;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeBoolean(env, text, NULL, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallStaticByteMethodV
 */
static
jbyte JNICALL JNI_CallStaticByteMethodV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jbyte result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareStaticMethodCall(env, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeByte(env, text, NULL, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallStaticCharMethodV
 */
static
jchar JNICALL JNI_CallStaticCharMethodV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jchar result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareStaticMethodCall(env, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeChar(env, text, NULL, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallStaticShortMethodV
 */
static
jshort JNICALL JNI_CallStaticShortMethodV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jshort result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareStaticMethodCall(env, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeShort(env, text, NULL, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallStaticIntMethodV
 */
static
jint JNICALL JNI_CallStaticIntMethodV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jint result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareStaticMethodCall(env, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeInt(env, text, NULL, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallStaticLongMethodV
 */
static
jlong JNICALL JNI_CallStaticLongMethodV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jlong result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareStaticMethodCall(env, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeLong(env, text, NULL, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallStaticFloatMethodV
 */
static
jfloat JNICALL JNI_CallStaticFloatMethodV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jfloat result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareStaticMethodCall(env, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeFloat(env, text, NULL, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallStaticDoubleMethodV
 */
static
jdouble JNICALL JNI_CallStaticDoubleMethodV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;
  jdouble result;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareStaticMethodCall(env, methodID);
  if (text == NULL)
    return 0;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  result = invokeDouble(env, text, NULL, size, args, env->callback);
  SwitchFromJava(env);
  return result;
}

/* CallStaticVoidMethodV
 */
static
void JNICALL JNI_CallStaticVoidMethodV(JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  jobject text;
  jsize size;

  size = countParamWords(METHOD_INFO(methodID)->descriptor);

  text = PrepareStaticMethodCall(env, methodID);
  if (text == NULL)
    return;
  SwitchToJava(env);
  JNI_DeleteLocalRef(env, text);
  invokeVoid(env, text, NULL, size, args, env->callback);
  SwitchFromJava(env);
}

/* - - - - - - - - start of reviewed code - - - - - - - - */

/* GetMethodID
 */
static
jmethodID JNICALL JNI_GetMethodID(JNIEnv *env, jclass clazz, const char *name, const char *descriptor)
{
  jchar i;
  jchar j;
  const struct methodInfo *methods;
  const struct interfaceInfo *interfaces;
  jclass cl;

  InitializeClass(env, clazz);
  if (env->thrown != NULL)
    return NULL;

  if (name[0] != '<' || name[1] != 'c') {

    for (cl = clazz; cl != NULL; cl = CLASS_INFO(cl)->superClass) {

      LoadMetadata(env, cl);
      if (env->thrown != NULL)
        return NULL;

      methods = CLASS_INFO(cl)->reflect->methods;
      if (methods != NULL)
        for (i = 0; methods[i].declaringClass != NULL; i++)
          if (strcmp(name, methods[i].name) == 0 && strcmp(descriptor, methods[i].descriptor) == 0) {
            if ((methods[i].accessFlags & ACC_STATIC) != 0)
              goto error;
            return METHOD_ID(&methods[i]);
          }

      if (name[0] == '<')
        goto error;
    
    }

    interfaces = CLASS_INFO(clazz)->interfaces;
    if (interfaces != NULL)
      for (j = 0; interfaces[j].clazz != NULL; j++) {
        
        cl = interfaces[j].clazz;

        LoadMetadata(env, cl);
        if (env->thrown != NULL)
          return NULL;

        methods = CLASS_INFO(cl)->reflect->methods;
        if (methods != NULL)
          for (i = 0; methods[i].declaringClass != NULL; i++)
            if (strcmp(name, methods[i].name) == 0 && strcmp(descriptor, methods[i].descriptor) == 0)
              return METHOD_ID(&methods[i]);
      
      }

  }
  
error:

  ThrowByName(env, "java/lang/NoSuchMethodError", name);
  return NULL;
}

/* GetFieldID
 */
static
jfieldID JNICALL JNI_GetFieldID(JNIEnv *env, jclass clazz, const char *name, const char *descriptor)
{
  jclass cl;
  const struct fieldInfo *fields;
  jsize i;

  InitializeClass(env, clazz);
  if (env->thrown != NULL)
    return NULL;

  for (cl = clazz; cl != NULL; cl = CLASS_INFO(cl)->superClass) {

    LoadMetadata(env, cl);
    if (env->thrown != NULL)
      return NULL;

    fields = CLASS_INFO(cl)->reflect->fields;
    if (fields != NULL)
      for (i = 0; fields[i].declaringClass != NULL; i++)
        if (strcmp(name, fields[i].name) == 0 && strcmp(descriptor, fields[i].descriptor) == 0) {
          if ((fields[i].accessFlags & ACC_STATIC) != 0)
            goto error;
          return FIELD_ID(&fields[i]);
        }
  
  }

error:

  ThrowByName(env, "java/lang/NoSuchFieldError", name);
  return NULL;
}

/* GetStaticMethodID
 */
static
jmethodID JNICALL JNI_GetStaticMethodID(JNIEnv *env, jclass clazz, const char *name, const char *descriptor)
{
  jchar i;
  const struct methodInfo *methods;
  jclass cl;

  InitializeClass(env, clazz);
  if (env->thrown != NULL)
    return NULL;

  if (name[0] != '<')
    for (cl = clazz; cl != NULL; cl = CLASS_INFO(cl)->superClass) {

      LoadMetadata(env, cl);
      if (env->thrown != NULL)
        return NULL;

      methods = CLASS_INFO(cl)->reflect->methods;
      if (methods != NULL)
        for (i = 0; methods[i].declaringClass != NULL; i++)
          if (strcmp(name, methods[i].name) == 0 && strcmp(descriptor, methods[i].descriptor) == 0) {
            if ((methods[i].accessFlags & ACC_STATIC) == 0)
              goto error;
            return METHOD_ID(&methods[i]);
          }
    
    }

error:

  ThrowByName(env, "java/lang/NoSuchMethodError", name);
  return NULL;
}

/* GetStaticFieldID
 */
static
jfieldID JNICALL JNI_GetStaticFieldID(JNIEnv *env, jclass clazz, const char *name, const char *descriptor)
{
  jchar i;
  jchar j;
  const struct fieldInfo *fields;
  const struct interfaceInfo *interfaces;
  jclass cl;

  InitializeClass(env, clazz);
  if (env->thrown != NULL)
    return NULL;

  for (cl = clazz; cl != NULL; cl = CLASS_INFO(cl)->superClass) {

    LoadMetadata(env, cl);
    if (env->thrown != NULL)
      return NULL;

    fields = CLASS_INFO(cl)->reflect->fields;
    if (fields != NULL)
      for (i = 0; fields[i].declaringClass != NULL; i++)
        if (strcmp(name, fields[i].name) == 0 && strcmp(descriptor, fields[i].descriptor) == 0) {
          if ((fields[i].accessFlags & ACC_STATIC) == 0)
            goto error;
          return FIELD_ID(&fields[i]);
        }
  
  }

  interfaces = CLASS_INFO(clazz)->interfaces;
  if (interfaces != NULL)
    for (j = 0; interfaces[j].clazz != NULL; j++) {
      cl = interfaces[j].clazz;

      LoadMetadata(env, cl);
      if (env->thrown != NULL)
        return NULL;

      fields = CLASS_INFO(cl)->reflect->fields;
      if (fields != NULL)
        for (i = 0; fields[i].declaringClass != NULL; i++)
          if (strcmp(name, fields[i].name) == 0 && strcmp(descriptor, fields[i].descriptor) == 0)
            return FIELD_ID(&fields[i]);
    
    }

error:

  ThrowByName(env, "java/lang/NoSuchFieldError", name);
  return NULL;
}

/* GetObjectArrayElement
 */
static
jobject JNICALL JNI_GetObjectArrayElement(JNIEnv *env, jobjectArray array, jsize index)
{
  jsize length;
  jobject *elements;
  jobject result;

  length = LENGTH(array);
  if (index < 0 || index >= length) {
    ThrowBounds(env, index);
    return NULL;
  }
  elements = ARRAY_ELEMENTS(array, jobject);
  mutex_lock(&env->lock);
  result = JNI_NewLocalRef(env, elements[index]);
  mutex_unlock(&env->lock);
  return result;
}

/* SetObjectArrayElement
 */
static
void JNICALL JNI_SetObjectArrayElement(JNIEnv *env, jobjectArray array, jsize index, jobject value)
{ 
  jsize length;
  jobject *elements;

  length = LENGTH(array);
  if (index < 0 || index >= length) {
    ThrowBounds(env, index);
    return;
  }
  if (value != NULL)
    if (!IsComptypeOf(GETCLASS(value), GETCLASS(array))) {
      ThrowByName(env, "java/lang/ArrayStoreException", NULL);
      return;
    }
  elements = ARRAY_ELEMENTS(array, jobject);
  mutex_lock(&env->lock);
  elements[index] = value;
  mutex_unlock(&env->lock);
}

/* RegisterNatives
 */
static
jint JNICALL JNI_RegisterNatives(JNIEnv *env, jclass clazz, const JNINativeMethod *natives, jint length)
{ 
  const struct classInfo *classInfo;
  const struct methodInfo *methods;
  jsize i;
  jsize j;
  jboolean found;
  jsize k;
  void **nativePtrs;

  LoadMetadata(env, clazz);
  if (env->thrown != NULL)
    return JNI_ERR;

  classInfo = CLASS_INFO(clazz);
  methods = classInfo->reflect->methods;

  for (i = 0; i < length; i++) {
    found = JNI_FALSE;
    if (methods != NULL)
      for (j = 0; methods[j].declaringClass != NULL; j++)
        if ((methods[j].accessFlags & ACC_NATIVE) != 0)
          if (strcmp(natives[i].name, methods[j].name) == 0 && strcmp(natives[i].signature, methods[j].descriptor) == 0) {
            found = JNI_TRUE;
            break;
          }
    if (!found) {
      ThrowByName(env, "java/lang/NoSuchMethodError", natives[i].name);
      return JNI_ERR;
    }
  }

  nativePtrs = classInfo->nativePtrs;
  for (i = 0; i < length; i++) {
    k = 0;
    for (j = 0; methods[j].declaringClass != NULL; j++)
      if ((methods[j].accessFlags & ACC_NATIVE) != 0) {
        if (strcmp(natives[i].name, methods[j].name) == 0 && strcmp(natives[i].signature, methods[j].descriptor) == 0) {
          if (env->jvm->verboseJNI)
            PrintFormatted(env, "[Registering native method '%s.%s%s']\n", classInfo->name, methods[j].name, methods[j].descriptor);
          nativePtrs[k] = natives[i].fnPtr;
          break;
        }
        k++;
      }
  }

  return JNI_OK;
}

/* UnregisterNatives
 */
static
jint JNICALL JNI_UnregisterNatives(JNIEnv *env, jclass clazz)
{ 
  const struct classInfo *classInfo;
  void **nativePtrs;
  const struct methodInfo *methods;
  jsize i;
  jsize j;

  LoadMetadata(env, clazz);
  if (env->thrown != NULL)
    return JNI_ERR;

  classInfo = CLASS_INFO(clazz);
  methods = classInfo->reflect->methods;
  if (methods != NULL) {
    j = 0;
    nativePtrs = classInfo->nativePtrs;
    for (i = 0; methods[i].declaringClass != NULL; i++)
      if ((methods[i].accessFlags & ACC_NATIVE) != 0) {
        if (nativePtrs[j] != NULL) {
          if (env->jvm->verboseJNI)
            PrintFormatted(env, "[Unregistering native method '%s.%s%s']\n", classInfo->name, methods[i].name, methods[i].descriptor);
          nativePtrs[j] = NULL;
        }
        j++;
      }
  }

  return JNI_OK;
}

/* NewString
 */
static
jstring JNICALL JNI_NewString(JNIEnv *env, const jchar *chars, jsize length)
{
  jclass stringClass = NULL;
  jmethodID methodID;
  jcharArray array = NULL;
  jstring string = NULL;

  stringClass = JNI_FindClass(env, "java/lang/String");
  if (stringClass == NULL)
    goto exit;

  methodID = JNI_GetMethodID(env, stringClass, "<init>", "([C)V");
  if (methodID == NULL)
    goto exit;

  array = JNI_NewCharArray(env, length);
  if (array == NULL)
    goto exit;

  JNI_SetCharArrayRegion(env, array, 0, length, (jchar*)chars);
  if (env->thrown != NULL)
    goto exit;

  string = JNI_NewObject(env, stringClass, methodID, array);
  if (env->thrown != NULL)
    goto exit;

exit:

  JNI_DeleteLocalRef(env, array);
  JNI_DeleteLocalRef(env, stringClass);
  return string;
}

/* NewStringUTF
 */
static
jstring JNICALL JNI_NewStringUTF(JNIEnv *env, const char *chars)
{ 
  jint length;
  jint i;
  jint bite;
  jint bite2;
  jint bite3;
  jboolean valid;
  jstring string;
  jchar *elements;

  length = 0;
  valid = JNI_TRUE;
  for (i = 0; chars[i] != '\0'; i++) {
    bite = (jbyte)chars[i] & 0xFF;
    if ((bite & 0x80) == 0x00)
      ;
    else if ((bite & 0xE0) == 0xC0) {
      i++;
      bite2 = (jbyte)chars[i] & 0xFF;
      if ((bite2 & 0xC0) != 0x80){
        valid = JNI_FALSE;
        break;
      }
    } else if ((bite & 0xF0) == 0xE0) {
      i++;
      bite2 = (jbyte)chars[i] & 0xFF;
      if ((bite2 & 0xC0) != 0x80) {
        valid = JNI_FALSE;
        break;
      }
      i++;
      bite3 = (jbyte)chars[i] & 0xFF;
      if ((bite3 & 0xC0) != 0x80) {
        valid = JNI_FALSE;
        break;
      }
    } else {
      valid = JNI_FALSE;
      break;
    }
    length++;
  }

  if (!valid)
    JNI_FatalError(env, "invalid UTF8 string");

  elements = (jchar*)malloc(length*sizeof(jchar));
  if (elements == NULL) {
    ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
    return NULL;
  }

  length = 0;
  for (i = 0; chars[i] != '\0'; i++) {
    bite = (jbyte)chars[i] & 0xFF;
    switch (bite >> 4) {
    default:
      elements[length] = (jchar)bite;
      break;
    case 12: case 13:
      bite2 = (jbyte)chars[++i] & 0xFF;
      elements[length] = (jchar)(((bite & 0x1F) << 6)|(bite2 & 0x3F));
      break;
    case 14:
      bite2 = (jbyte)chars[++i] & 0xFF;
      bite3 = (jbyte)chars[++i] & 0xFF;
      elements[length] = (jchar)(((bite & 0x0F) << 12)|((bite2 & 0x3F) << 6)|(bite3 & 0x3F));
      break;
    }
    length++;
  }

  string = JNI_NewString(env, elements, length);

  free(elements);

  return string;
}

/* GetStringUTFLength
 */
static
jsize JNICALL JNI_GetStringUTFLength(JNIEnv *env, jstring string)
{
  jsize length;
  const jchar *chars;
  jsize utfLength;
  jsize index;

  length = JNI_GetStringLength(env, string);
  if (env->thrown != NULL)
    return 0;

  chars = JNI_GetStringChars(env, string, NULL);
  if (chars == NULL)
    return 0;

  utfLength = 0;
  for (index = 0; index < length; index++) {
    jchar shar = chars[index];
    if (0x0001 <= shar && shar <= 0x007F)
      utfLength++;
    else if (shar <= 0x07FF)
      utfLength += 2;
    else 
      utfLength += 3;
  }

  JNI_ReleaseStringChars(env, string, chars);

  return utfLength;
}

/* GetStringUTFChars
 */
static
const char *JNICALL JNI_GetStringUTFChars(JNIEnv *env, jstring string, jboolean *isCopy)
{
  jsize utfLength;
  jsize length;
  const jchar *chars;
  char *utfChars;
  jsize i;
  jsize j;
  jint shar;

  utfLength = JNI_GetStringUTFLength(env, string);
  utfChars = (char*)malloc(utfLength+1);
  if (utfChars == NULL) {
    ThrowByName(env, "java/lang/OutOfMemoryError", NULL);
    return NULL;
  }

  length = JNI_GetStringLength(env, string);
  if (env->thrown != NULL) {
    free(utfChars);
    return NULL;
  }

  chars = JNI_GetStringChars(env, string, NULL);
  if (chars == NULL) {
    free(utfChars);
    return NULL;
  }

  j = 0;
  for (i = 0; i < length; i++) {
    shar = chars[i];
    if (shar >= 0x0001 && shar <= 0x007F)
      utfChars[j++] = shar;
    else {
      if (shar <= 0x07FF)
        utfChars[j++] = ((shar >>  6)&0x1F)|0xC0;
      else {
        utfChars[j++] = ((shar >> 12)&0x0F)|0xE0;
        utfChars[j++] = ((shar >>  6)&0x3F)|0x80;
      }
      utfChars[j++] = (shar&0x3F)|0x80;
    }
  }
  utfChars[j++] = '\0';

  JNI_ReleaseStringChars(env, string, chars);

  if (isCopy != NULL)
    *isCopy = JNI_TRUE;

  return utfChars;
}

/* ReleaseStringUTFChars
 */
static
void JNICALL JNI_ReleaseStringUTFChars(JNIEnv *env, jstring string, const char *chars)
{
  free((char*)chars);
}

/* GetStringUTFRegion
 */
static
void JNICALL JNI_GetStringUTFRegion(JNIEnv *env, jstring string, jsize start, jsize length, char *buffer)
{ 
  jmethodID methodID;
  jint end;
  jstring substring = NULL;
  const char *chars;
  jint i;

  methodID = JNI_GetMethodID(env, GETCLASS(string), "substring", "(II)Ljava/lang/String;");
  if (methodID == NULL)
    goto exit;

  end = start+length;

  substring = JNI_CallObjectMethod(env, string, methodID, start, end);
  if (env->thrown != NULL)
    goto exit;

  chars = JNI_GetStringUTFChars(env, substring, NULL);
  if (chars == NULL)
    goto exit;

  for (i = 0; i < length; i++)
    buffer[i] = chars[i];
  buffer[length] = '\0';

  JNI_ReleaseStringUTFChars(env, substring, chars);

exit:

  JNI_DeleteLocalRef(env, substring);
}

/* GetPrimitiveArrayCritical
 */
static
void *JNICALL JNI_GetPrimitiveArrayCritical(JNIEnv *env, jarray array, jboolean *isCopy)
{
  if (isCopy != NULL)
    *isCopy = JNI_FALSE;
  return ARRAY_ELEMENTS(array, void);
}

/* ReleasePrimitiveArrayCritical
 */
static
void JNICALL JNI_ReleasePrimitiveArrayCritical(JNIEnv *env, jarray array, void *elements, jint mode)
{
}

/* GetEnv
 */
static
jint JNICALL JNI_GetEnv(JavaVM *jvm, JNIEnv **penv, jint version)
{
  JNIEnv *env;

  env = CurrentEnv();

  if (env == NULL)
    return JNI_EDETACHED;

  if (env->jvm != jvm)
    return JNI_EDETACHED;

  if (GNI_GetInterface((void**)penv, version) == JNI_OK)
    return JNI_OK;

  if (version != JNI_VERSION_1_1 && version != JNI_VERSION_1_2)
    return JNI_EVERSION;

  *penv = env;
  
  return JNI_OK;
}

/* DetachCurrentThread
 */
static
jint JNICALL JNI_DetachCurrentThread(JavaVM *jvm)
{
  JNIEnv *env;
  jint error;
  jthrowable thrown;
  jmethodID methodID;

  /* The thread should be attached */
  env = CurrentEnv();
  if (env == NULL)
    return JNI_EDETACHED;
  if (env->jvm != jvm)
    return JNI_EDETACHED;

  /* Cannot detach in the middle of a stack */
  if (env->callback != NULL)
    return JNI_ERR;

  if (env->thread != NULL) {
    /* Ensure we got space for the exception if any */
    error = JNI_EnsureLocalCapacity(env, 1);
    if (error != JNI_OK)
      return error;

    /* Get and clear the thrown exception */
    thrown = JNI_ExceptionOccurred(env);
    JNI_ExceptionClear(env);

    /* Execute API thread destruction method */
    methodID = JNI_GetMethodID(env, GETCLASS(env->thread), "destroy", "(Ljava/lang/Throwable;)V");
    if (methodID != NULL)
      JNI_CallVoidMethod(env, env->thread, methodID, thrown);
    JNI_DeleteLocalRef(env, thrown);

    /* Clear the thread reference */
    mutex_lock(&env->lock);
    env->thread = NULL;
    mutex_unlock(&env->lock);
  }

  /* Clear the exception reference */
  JNI_ExceptionClear(env);

  /* Update machine thread list */
  mutex_lock(&jvm->lock);
  if (!env->daemon) {
    jvm->aengels--;
    if (jvm->aengels == 0)
      mutex_notify(&jvm->lock);
  }
  if (env->previous != NULL)
    env->previous->next = env->next;
  else
    jvm->envs = env->next;
  if (env->next != NULL)
    env->next->previous = env->previous;
  env->previous = NULL;
  env->next = NULL;
  mutex_unlock(&jvm->lock);

  /* Release memory used by JNI frames */
  PopAllLocalFrames(env);

  /* Remove env from TLS */
  tls_set(&tlsJNI, NULL);

  /* Release env memory */
  free(env);

  return JNI_OK;
}

/* AttachCurrentThread
 */
static
jint JNICALL JNI_AttachCurrentThread(JavaVM *jvm, JNIEnv **penv, void *args)
{
  JNIEnv *env;
  JavaVMAttachArgs *attachArgs;
  char *name;
  jobject group;
  jstring string;
  jmethodID methodID;
  jint error;
  jclass threadClass;

  /* If Thread already attached return successfully */
  env = CurrentEnv();
  if (env != NULL) {
    if (env->jvm != jvm)
      return JNI_ERR;
    *penv = env;
    return JNI_OK;
  }

  /* Try to allocate space for env */
  env = (JNIEnv*)malloc(sizeof(JNIEnv));
  if (env == NULL)
    return JNI_ERR;

  /* Configure env */
  env->functions = &nativeInterface;
  env->jvm = jvm;
  env->gcstate = 0;
  env->daemon = JNI_FALSE;
  env->frames = 0;
  env->topFrame = NULL;
  env->thrown = NULL;
  env->thread = NULL;
  env->callback = NULL;
  mutex_init(&env->lock);

  error = JNI_PushLocalFrame(env, 16);
  if (error != JNI_OK) {
    free(env);
    return error;
  }
  
  /* Put env in TLS */
  tls_set(&tlsJNI, env);
  
  /* Add to jvm */
  mutex_lock(&jvm->lock);
  jvm->aengels++;
  env->previous = NULL;
  env->next = jvm->envs;
  if (env->next != NULL)
    env->next->previous = env;
  jvm->envs = env;
  mutex_unlock(&jvm->lock);

  /* Alloc thread */
  threadClass = JNI_FindClass(env, "java/lang/Thread");
  if (threadClass == NULL) {
    JNI_DetachCurrentThread(jvm);
    return JNI_ERR;
  }

  mutex_lock(&env->lock);
  env->thread = JNI_AllocObject(env, threadClass);
  mutex_unlock(&env->lock);
  if (env->thread == NULL) {
    JNI_DetachCurrentThread(jvm);
    return JNI_ERR;
  }
  JNI_DeleteLocalRef(env, env->thread);

  /* Get attach args */
  name = NULL;
  group = NULL;
  attachArgs = (JavaVMAttachArgs*)args;
  if (attachArgs != NULL)
    if (attachArgs->version == JNI_VERSION_1_2) {
      name = attachArgs->name;
      group = attachArgs->group;
    }

  /* Allocate string */
  string = NULL;
  if (name != NULL) {
    string = JNI_NewStringUTF(env, name);
    if (string == NULL) {
      JNI_DetachCurrentThread(jvm);
      return JNI_ERR;
    }
  }

  /* Execute constructor */
  methodID = JNI_GetMethodID(env, threadClass, "<init>", "(Ljava/lang/ThreadGroup;Ljava/lang/String;)V");
  if (methodID == NULL) {
    JNI_DetachCurrentThread(jvm);
    return JNI_ERR;
  }

  JNI_CallNonvirtualVoidMethod(env, env->thread, threadClass, methodID, group, string);
  if (env->thrown != NULL) {
    JNI_DetachCurrentThread(jvm);
    return JNI_ERR;
  }

  /* Throw away string reference */
  JNI_DeleteLocalRef(env, string);

  /* Make env available */
  *penv = env;

  return JNI_OK;
}

/* DestroyJavaVM
 */
static
jint JNICALL JNI_DestroyJavaVM(JavaVM *jvm)
{
  JavaVMAttachArgs attachArgs;
  JNIEnv *env;
  jint error;
  jclass runtimeClass;
  jmethodID methodID;

  /* Attach to the VM */
  attachArgs.version = JNI_VERSION_1_2;
  attachArgs.name = "destroyer";
  attachArgs.group = jvm->systemGroup;
  error = JNI_AttachCurrentThread(jvm, &env, &attachArgs);
  if (error != JNI_OK)
    return error;

  /* Wait for non-daemon threads */
  mutex_lock(&jvm->lock);
  if (!env->daemon) {
    env->daemon = JNI_FALSE;
    jvm->aengels--;
  }
  while (jvm->aengels > 0)
    mutex_wait(&jvm->lock);
  mutex_unlock(&jvm->lock);
  
  /* Run constructors and internalize strings */
  runtimeClass = JNI_FindClass(env, "java/lang/Runtime");
  if (runtimeClass == NULL)
    return JNI_ERR;

  methodID = JNI_GetStaticMethodID(env, runtimeClass, "runShutdownHooks", "()V");
  if (methodID == NULL)
    return JNI_ERR;

  JNI_CallStaticVoidMethod(env, runtimeClass, methodID);
  if (env->thrown != NULL)
    return JNI_ERR;

  /* But fail anyway */
  return JNI_ERR;
}

static
jint vmCount;
static
JavaVM *javaVMs[1];

/* GetDefaultJavaVMInitArgs
 */
JNIEXPORT
jint JNICALL JNI_GetDefaultJavaVMInitArgs(void *args)
{
  JDK1_1InitArgs *jni11Args;
  JavaVMInitArgs *jni12Args;
  
  jni12Args = (JavaVMInitArgs*)args;
  if (jni12Args->version == JNI_VERSION_1_2) {
    jni12Args->nOptions = 0;
    jni12Args->options = NULL;
    jni12Args->ignoreUnrecognized = JNI_FALSE;
    return JNI_OK;
  }
  jni11Args = (JDK1_1InitArgs*)args;
  jni11Args->version = JNI_VERSION_1_1;
  jni11Args->properties = NULL;
  jni11Args->checkSource = JNI_FALSE;
  jni11Args->nativeStackSize = DEFAULT_NATIVE_STACK_SIZE;
  jni11Args->javaStackSize = DEFAULT_JAVA_STACK_SIZE;
  jni11Args->minHeapSize = DEFAULT_MIN_HEAP_SIZE;
  jni11Args->maxHeapSize = DEFAULT_MAX_HEAP_SIZE;
  jni11Args->verifyMode = 2;
  jni11Args->classpath = GetBootstrapClasspath();
  jni11Args->vfprintf = NULL;
  jni11Args->exit = NULL;
  jni11Args->abort = NULL;
  jni11Args->enableClassGC = JNI_TRUE;
  jni11Args->enableVerboseGC = JNI_FALSE;
  jni11Args->disableAsyncGC = JNI_FALSE;
  return JNI_OK;
}

/* CreateJavaVM
 */
JNIEXPORT
jint JNICALL JNI_CreateJavaVM(JavaVM **pjvm, JNIEnv **penv, void *args)
{
  JavaVM *jvm;
  JDK1_1InitArgs *jni11Args;
  JavaVMInitArgs *jni12Args;
  jboolean knownVersion;
  jsize i;
  JavaVMAttachArgs attachArgs;
  JNIEnv *env;
  jstring mainName;
  jobject mainGroup;
  jobject systemLoader;
  jint error;
  jmethodID methodID;
  jstring className;
  jmethodID constructorID;
  char *dotted;
  jcharArray charArray;
  jobject o;
  jstring intern;
  char *optionString;
  char *optionEnd;
  void *extraInfo;
  jsize j;
  jsize k;
  jsize length;
  char *key;
  char *value;
  jclass tgroupClass;
  jclass cloaderClass;
  jclass stringClass;
  jclass textClass;
  jclass systemClass;
  jobject start;

  char *bootclasspath = "";
  char **properties = NULL;
  jint nativeStackSize = -1;
  jint javaStackSize = -1;
  jint minHeapSize = -1;
  jint maxHeapSize = -1;
  jint (JNICALL *vfprintf_hook)(FILE*,const char*,va_list) = NULL;
  void (JNICALL *exit_hook)(jint) = NULL;
  void (JNICALL *abort_hook)(void) = NULL;
  jboolean verboseClass = JNI_FALSE;
  jboolean verboseGC = JNI_FALSE;
  jboolean verboseJNI = JNI_FALSE;

  /* Check and prepare args*/
  knownVersion = JNI_FALSE;

  jni11Args = (JDK1_1InitArgs*)args;
  if (jni11Args->version == JNI_VERSION_1_1) {
    knownVersion = JNI_TRUE;
    properties = jni11Args->properties;
    if (jni11Args->checkSource) return JNI_ERR;
    if (jni11Args->nativeStackSize < 0) return JNI_ERR;
    nativeStackSize = jni11Args->nativeStackSize;
    if (jni11Args->javaStackSize < 0) return JNI_ERR;
    javaStackSize = jni11Args->javaStackSize;
    if (jni11Args->minHeapSize < 0) return JNI_ERR;
    minHeapSize = jni11Args->minHeapSize;
    if (jni11Args->maxHeapSize < 0) return JNI_ERR;
    maxHeapSize = jni11Args->maxHeapSize;
    if (jni11Args->verifyMode != 2) return JNI_ERR;
    if (jni11Args->classpath != NULL) bootclasspath = jni11Args->classpath;
    vfprintf_hook = jni11Args->vfprintf;
    exit_hook = jni11Args->exit;
    abort_hook = jni11Args->abort;
    if (!jni11Args->enableClassGC) return JNI_ERR;
    verboseClass = JNI_FALSE;
    verboseGC = jni11Args->enableVerboseGC;
    verboseJNI = JNI_FALSE;
    if (jni11Args->disableAsyncGC) return JNI_ERR;
  }

  jni12Args = (JavaVMInitArgs*)args;
  if (jni12Args->version == JNI_VERSION_1_2) {
    knownVersion = JNI_TRUE;
    j = 0;
    properties = (char**)calloc(jni12Args->nOptions+1, sizeof(char*));
    for (i = 0; i < jni12Args->nOptions; i++) {
      optionString = jni12Args->options[i].optionString;
      extraInfo = jni12Args->options[i].extraInfo;
      if (strcmp(optionString, "vfprintf") == 0)
        vfprintf_hook = extraInfo;
      else if (strcmp(optionString, "exit") == 0)
        exit_hook = extraInfo;
      else if (strcmp(optionString, "abort") == 0)
        abort_hook = extraInfo;
      else if (strncmp(optionString, "-D", 2) == 0)
        properties[j++] = &optionString[2];
      else if (strncmp(optionString, "-verbose", 8) == 0) {
        if (optionString[8] == '\0')
          verboseClass = JNI_TRUE;
        else {
          if (optionString[8] != ':') return JNI_ERR;
          optionEnd = &optionString[8];
          for (k = 8; optionString[k] != '\0'; k++)
            if (optionString[k+1] == ',' || optionString[k+1] == '\0') {
              length = &optionString[k]-optionEnd;
              if (length == 5 && strncmp(optionEnd+1, "class", 5) == 0)
                verboseClass = JNI_TRUE;
              else if (length == 2 && strncmp(optionEnd+1, "gc", 2) == 0)
                verboseGC = JNI_TRUE;
              else if (length == 3 && strncmp(optionEnd+1, "jni", 3) == 0)
                verboseJNI = JNI_TRUE;
              else
                return JNI_ERR;
              optionEnd = &optionString[k+1];
            }
        }
      } else if (strncmp(optionString, "-Xss", 4) == 0) {
        nativeStackSize = strtol(&optionString[4], &optionEnd, 10);
        if (optionEnd == &optionString[4]) return JNI_ERR;
        switch (optionEnd[0]) {
        case 'G': case 'g': nativeStackSize *= 1024;
        case 'M': case 'm': nativeStackSize *= 1024;
        case 'K': case 'k': nativeStackSize *= 1024;
        case 'B': case 'b': if (optionEnd[1] != '\0') return JNI_ERR;
        case '\0': break;
        default: return JNI_ERR;
        }
        if (nativeStackSize < 0) return JNI_ERR;
      } else if (strncmp(optionString, "-Xoss", 5) == 0) {
        javaStackSize = strtol(&optionString[5], &optionEnd, 10);
        if (optionEnd == &optionString[5]) return JNI_ERR;
        switch (optionEnd[0]) {
        case 'G': case 'g': javaStackSize *= 1024;
        case 'M': case 'm': javaStackSize *= 1024;
        case 'K': case 'k': javaStackSize *= 1024;
        case 'B': case 'b': if (optionEnd[1] != '\0') return JNI_ERR;
        case '\0': break;
        default: return JNI_ERR;
        }
        if (javaStackSize < 0) return JNI_ERR;
      } else if (strncmp(optionString, "-Xms", 4) == 0) {
        minHeapSize = strtol(&optionString[4], &optionEnd, 10);
        if (optionEnd == &optionString[4]) return JNI_ERR;
        switch (optionEnd[0]) {
        case 'G': case 'g': minHeapSize *= 1024;
        case 'M': case 'm': minHeapSize *= 1024;
        case 'K': case 'k': minHeapSize *= 1024;
        case 'B': case 'b': if (optionEnd[1] != '\0') return JNI_ERR;
        case '\0': break;
        default: return JNI_ERR;
        }
        if (minHeapSize < 0) return JNI_ERR;
      } else if (strncmp(optionString, "-Xmx", 4) == 0) {
        maxHeapSize = strtol(&optionString[4], &optionEnd, 10);
        if (optionEnd == &optionString[4]) return JNI_ERR;
        switch (optionEnd[0]) {
        case 'G': case 'g': maxHeapSize *= 1024;
        case 'M': case 'm': maxHeapSize *= 1024;
        case 'K': case 'k': maxHeapSize *= 1024;
        case 'B': case 'b': if (optionEnd[1] != '\0') return JNI_ERR;
        case '\0': break;
        default: return JNI_ERR;
        }
        if (maxHeapSize < 0) return JNI_ERR;
      } else {
        if (!jni12Args->ignoreUnrecognized)
          return JNI_ERR;
      }
    }
  }

  /* Check version */
  if (!knownVersion)
    return JNI_EVERSION;

  /* Setup stack size */
  if (nativeStackSize == -1) nativeStackSize = DEFAULT_NATIVE_STACK_SIZE;
  if (javaStackSize == -1) javaStackSize = DEFAULT_JAVA_STACK_SIZE;

  /* Setup heap size */
  if (minHeapSize == -1 && maxHeapSize == -1) {
    minHeapSize = DEFAULT_MIN_HEAP_SIZE;
    maxHeapSize = DEFAULT_MAX_HEAP_SIZE;
  }
  if (minHeapSize == -1) minHeapSize = maxHeapSize/4;
  if (maxHeapSize == -1) maxHeapSize = 4*minHeapSize;

  /* Check heap bounds */
  if (minHeapSize > maxHeapSize)
    return JNI_ERR;

  /* Check if it was already created */
  if (vmCount != 0)
    return JNI_ERR;

  /* Initialize JNI tls */
  tls_init(&tlsJNI);

  /* Only one VM is supported */
  jvm = (JavaVM*)malloc(sizeof(JavaVM));
  if (jvm == NULL)
    return JNI_ERR;

  javaVMs[vmCount++] = jvm;

  /* Initialize JavaVM fields */
  jvm->functions = &invokeInterface;
  jvm->systemGroup = NULL;
  jvm->aengels = 0;
  jvm->envs = NULL;
  jvm->globals = NULL;
  jvm->weaks = NULL;
  
  /* Configure jvm */
  jvm->nativeStackSize = nativeStackSize;
  jvm->javaStackSize = javaStackSize;
  jvm->minHeapSize = minHeapSize;
  jvm->maxHeapSize = maxHeapSize;
  jvm->vfprintf = vfprintf_hook;
  jvm->exit = exit_hook;
  jvm->abort = abort_hook;
  jvm->verboseClass = verboseClass;
  jvm->verboseGC = verboseGC;
  jvm->verboseJNI = verboseJNI;

  /* Initialize VM lock */
  mutex_init(&jvm->lock);

  /* Initialize locks */
  montab_init(&jvm->montab);

  /* Initialize heap */
  heap_init(&jvm->gcheap);

  /* Workaround to get hardcoded heap start */
  start = heap_first(&jvm->gcheap);

  attachArgs.version = JNI_VERSION_1_2;
  attachArgs.name = "bootstrapper";
  attachArgs.group = NULL;
  error = JNI_AttachCurrentThread(jvm, &env, &attachArgs);
  if (error != JNI_OK)
    goto exit;

  /* Run constructor and put bootstrap classes on bootstrap class loader table */
  cloaderClass = JNI_FindClass(env, "java/lang/ClassLoader");
  if (cloaderClass == NULL)
    goto exit;

  constructorID = JNI_GetMethodID(env, GETCLASS(cloaderClass), "<init>", "()V");
  if (constructorID == NULL)
    goto exit;

  methodID = JNI_GetStaticMethodID(env, cloaderClass, "putClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/lang/Class;)V");
  if (methodID == NULL)
    goto exit;

  /* Walk on the heap putting classes on bootstrap class loader table */
  for (o = start; o != NULL; o = heap_next(&jvm->gcheap, o))
    if (GETCLASS(GETCLASS(o)) == GETCLASS(o)) {
      JNI_CallNonvirtualVoidMethod(env, o, GETCLASS(o), constructorID, NULL);
      if (env->thrown != NULL)
        goto exit;

      dotted = strdup(CLASS_INFO(o)->name);
      if (dotted == NULL)
        goto exit;

      for (i = 0; dotted[i] != '\0'; i++)
        if (dotted[i] == '/')
          dotted[i] = '.';

      className = JNI_NewStringUTF(env, dotted);
      if (className == NULL)
        goto exit;

      free(dotted);

      JNI_CallStaticVoidMethod(env, cloaderClass, methodID, NULL, className, o);
      if (env->thrown != NULL)
        goto exit;

      JNI_DeleteLocalRef(env, className);
    }

  /* Run constructors and internalize strings */
  stringClass = JNI_FindClass(env, "java/lang/String");
  if (stringClass == NULL)
    goto exit;

  constructorID = JNI_GetMethodID(env, stringClass, "<init>", "([C)V");
  if (constructorID == NULL)
    goto exit;
  methodID = JNI_GetMethodID(env, stringClass, "intern", "()Ljava/lang/String;");
  if (methodID == NULL)
    goto exit;

  /* Walk on the heap interning strings */
  charArray = NULL;
  for (o = start; o != NULL; o = heap_next(&jvm->gcheap, o)) {
    if (GETCLASS(o) == stringClass) {
      JNI_CallNonvirtualVoidMethod(env, o, GETCLASS(o), constructorID, charArray);
      if (env->thrown != NULL)
        goto exit;

      intern = JNI_CallObjectMethod(env, o, methodID);
      if (env->thrown != NULL)
        goto exit;

      JNI_DeleteLocalRef(env, intern);
    }
    charArray = (jcharArray)o;
  }

  /* Run constructors for method texts */
  textClass = JNI_FindClass(env, "java/lang/MethodText");
  if (textClass == NULL)
    goto exit;

  constructorID = JNI_GetMethodID(env, textClass, "<init>", "()V");
  if (constructorID == NULL)
    goto exit;

  for (o = start; o != NULL; o = heap_next(&jvm->gcheap, o))
    if (GETCLASS(o) == textClass) {
      JNI_CallNonvirtualVoidMethod(env, o, GETCLASS(o), constructorID);
      if (env->thrown != NULL)
        goto exit;
    }

  /* Get system thread group object */
  tgroupClass = JNI_FindClass(env, "java/lang/ThreadGroup");
  if (tgroupClass == NULL)
    goto exit;

  methodID = JNI_GetStaticMethodID(env, tgroupClass, "getSystemGroup", "()Ljava/lang/ThreadGroup;");
  if (methodID == NULL)
    goto exit;

  jvm->systemGroup = JNI_CallStaticObjectMethod(env, tgroupClass, methodID);
  if (env->thrown != NULL)
    goto exit;

  JNI_DeleteLocalRef(env, jvm->systemGroup);

  /* Register system properties */
  RegisterSystemProperties(env);
  if (env->thrown != NULL)
    goto exit;

  /* modify bootstrap classpath */
  if (jni11Args->version == JNI_VERSION_1_1) {
    SetSystemProperty(env, "java.vm.class.path", bootclasspath);
    if (env->thrown != NULL)
      goto exit;
    SetSystemProperty(env, "java.class.path", bootclasspath);
    if (env->thrown != NULL)
      goto exit;
  }

  /* set user properties */
  if (properties != NULL) {
    for (i = 0; properties[i] != NULL; i++) {
      for (j = 0; properties[i][j] != '\0'; j++)
        if (properties[i][j] == '=')
          break;
      key = (char*)malloc(j+1);
      if (key == NULL)
        goto exit;
      strncpy(key, properties[i], j);
      key[j] = '\0';
      value = &properties[i][j];
      if (value[0] == '=')
        value++;
      SetSystemProperty(env, key, value);
      if (env->thrown != NULL) {
        free(key);
        goto exit;
      }
      free(key);
    }
    if (jni12Args->version == JNI_VERSION_1_2)
      free(properties);
  }

  /* install bootstrap and system loaders */
  methodID = JNI_GetStaticMethodID(env, cloaderClass, "installBootstrapLoader", "()V");
  if (methodID == NULL)
    goto exit;
  JNI_CallStaticVoidMethod(env, cloaderClass, methodID);
  if (env->thrown != NULL)
    goto exit;

  if (jni12Args->version == JNI_VERSION_1_2) {
    methodID = JNI_GetStaticMethodID(env, cloaderClass, "installSystemLoader", "()V");
    if (methodID == NULL)
      goto exit;
    JNI_CallStaticVoidMethod(env, cloaderClass, methodID);
    if (env->thrown != NULL)
      goto exit;
  }

  /* install security manager */
  systemClass = JNI_FindClass(env, "java/lang/System");
  if (systemClass == NULL)
    goto exit;
  methodID = JNI_GetStaticMethodID(env, systemClass, "installSecurityManager", "()V");
  if (methodID == NULL)
    goto exit;
  JNI_CallStaticVoidMethod(env, systemClass, methodID);
  if (env->thrown != NULL)
    goto exit;

  /* Create "main" thread group */
  mainName = JNI_NewStringUTF(env, "main");
  if (mainName == NULL)
    goto exit;

  methodID = JNI_GetMethodID(env, tgroupClass, "<init>", "(Ljava/lang/String;)V");
  if (methodID == NULL)
    goto exit;

  mainGroup = JNI_NewObject(env, tgroupClass, methodID, mainName);
  if (env->thrown != NULL)
    goto exit;

  /* Detach "bootstrapper" thread */
  error = JNI_DetachCurrentThread(jvm);
  if (error != JNI_OK)
    goto exit;

  /* Attach "main" thread */
  attachArgs.version = JNI_VERSION_1_2;
  attachArgs.name = "main";
  attachArgs.group = mainGroup;
  error = JNI_AttachCurrentThread(jvm, &env, &attachArgs);
  if (error != JNI_OK)
    goto exit;

  /* Get system class loader */
  methodID = JNI_GetStaticMethodID(env, cloaderClass, "getSystemClassLoader", "()Ljava/lang/ClassLoader;");
  if (methodID == NULL)
    goto exit;

  systemLoader = JNI_CallStaticObjectMethod(env, cloaderClass, methodID);
  if (env->thrown != NULL)
    goto exit;

  /* Set system loader as "main" thread context loader */
  methodID = JNI_GetMethodID(env, GETCLASS(env->thread), "setContextClassLoader", "(Ljava/lang/ClassLoader;)V");
  if (methodID == NULL)
    goto exit;

  JNI_CallVoidMethod(env, env->thread, methodID, systemLoader);
  if (env->thrown != NULL)
    goto exit;

  JNI_DeleteLocalRef(env, systemLoader);

  /* Publish jvm and "main" env */
  *pjvm = jvm;
  *penv = env;

  return JNI_OK;

exit:

  /* Finalize heap */
  heap_fini(&jvm->gcheap);

  /* Finalize locks */
  montab_fini(&jvm->montab);

  /* Destroy jvm */
  free(jvm->weaks);
  free(jvm->globals);
  free(jvm);

  /* Remove from created list */
  --vmCount;

  return JNI_ERR;
}

/* GetCreatedJavaVMs 
 */
JNIEXPORT
jint JNICALL JNI_GetCreatedJavaVMs(JavaVM **buffer, jsize length, jsize *count)
{ 
  jint i;

  *count = vmCount;
  if (length > vmCount)
    length = vmCount;
  for (i = 0; i < length; i++)
    buffer[i] = javaVMs[i];
  return JNI_OK;
}

