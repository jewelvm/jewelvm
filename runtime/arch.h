/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#ifndef _ARCH_H
#define _ARCH_H

#include <jni.h>

extern void beforeJava();

int atomicGet(void*);
void  atomicSet(void*,int);
void atomicAnd(void*,int);
void atomicOr(void*,int);

jobject invokeObject(JNIEnv*,jobject,jobject,jsize,va_list,void*);
jboolean invokeBoolean(JNIEnv*,jobject,jobject,jsize,va_list,void*);
jbyte invokeByte(JNIEnv*,jobject,jobject,jsize,va_list,void*);
jchar invokeChar(JNIEnv*,jobject,jobject,jsize,va_list,void*);
jshort invokeShort(JNIEnv*,jobject,jobject,jsize,va_list,void*);
jint invokeInt(JNIEnv*,jobject,jobject,jsize,va_list,void*);
jlong invokeLong(JNIEnv*,jobject,jobject,jsize,va_list,void*);
jfloat invokeFloat(JNIEnv*,jobject,jobject,jsize,va_list,void*);
jdouble invokeDouble(JNIEnv*,jobject,jobject,jsize,va_list,void*);
void invokeVoid(JNIEnv*,jobject,jobject,jsize,va_list,void*);

jobject callObjectNative(JNIEnv*,jobject(*)(JNIEnv*,jobject,...),jobject,jsize,va_list);
jboolean callBooleanNative(JNIEnv*,jboolean(*)(JNIEnv*,jobject,...),jobject,jsize,va_list);
jbyte callByteNative(JNIEnv*,jbyte(*)(JNIEnv*,jobject,...),jobject,jsize,va_list);
jchar callCharNative(JNIEnv*,jchar(*)(JNIEnv*,jobject,...),jobject,jsize,va_list);
jshort callShortNative(JNIEnv*,jshort(*)(JNIEnv*,jobject,...),jobject,jsize,va_list);
jint callIntNative(JNIEnv*,jint(*)(JNIEnv*,jobject,...),jobject,jsize,va_list);
jlong callLongNative(JNIEnv*,jlong(*)(JNIEnv*,jobject,...),jobject,jsize,va_list);
jfloat callFloatNative(JNIEnv*,jfloat(*)(JNIEnv*,jobject,...),jobject,jsize,va_list);
jdouble callDoubleNative(JNIEnv*,jdouble(*)(JNIEnv*,jobject,...),jobject,jsize,va_list);
void callVoidNative(JNIEnv*,void(*)(JNIEnv*,jobject,...),jobject,jsize,va_list);

jboolean TraverseStack(void*,jboolean(*)(jboolean,jobject,void*,void*,void*),void*);

#endif /* _ARCH_H */

