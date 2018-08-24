/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#ifndef _JNIVM_H
#define _JNIVM_H

#include "gcheap.h"
#include "monman.h"

/* Access flags
 */
#define ACC_PUBLIC        0x0001
#define ACC_PRIVATE       0x0002
#define ACC_PROTECTED     0x0004
#define ACC_STATIC        0x0008
#define ACC_FINAL         0x0010
#define ACC_SUPER         0x0020
#define ACC_SYNCHRONIZED  0x0020
#define ACC_VOLATILE      0x0040
#define ACC_TRANSIENT     0x0080
#define ACC_NATIVE        0x0100
#define ACC_INTERFACE     0x0200
#define ACC_ABSTRACT      0x0400
#define ACC_STRICT        0x0800

typedef struct JNIFrame JNIFrame;

struct JNIFrame {
  JNIFrame *previous;
  jint size;
  jint capacity;
  jobject entries[0];
};

typedef struct JNIHasht JNIHasht;

struct JNIHasht {
  jint size;
  jint capacity;
  jobject entries[0];
};

#ifdef _JNI_IMPL
struct JNIEnv {
  const struct JNINativeInterface *functions;
  JavaVM *jvm;
  JNIEnv *previous;
  JNIEnv *next;
  jbyte gcstate;
  jboolean daemon;
  jint frames;
  JNIFrame *topFrame;
  volatile jobject thrown;
  jobject thread;
  void *callback;
  mutex_t lock;
};

struct JavaVM {
  const struct JNIInvokeInterface *functions;
  jobject systemGroup;
  jint aengels;
  JNIEnv *envs;
  JNIHasht *globals;
  JNIHasht *weaks;
  jint nativeStackSize;
  jint javaStackSize;
  jint minHeapSize;
  jint maxHeapSize;
  int (JNICALL *vfprintf)(FILE*,const char*,va_list);
  void (JNICALL *exit)(int);
  void (JNICALL *abort)(void);
  jboolean verboseClass;
  jboolean verboseGC;
  jboolean verboseJNI;
  mutex_t lock;
  montab_t montab;
  gcheap_t gcheap;
};
#endif /* _JNI_IMPL */

jobject IMLookup(jclass,jclass,jsize);
jboolean IsSubtypeOf(jclass,jclass);
jboolean IsComptypeOf(jclass,jclass);

jclass NewClass(JNIEnv*,jobject,const char*,jlong,jchar,jclass,jchar,const jclass*,const jint*,jint,jint,jchar,jint,jint,jchar,jint,jint,jchar,jclass,jint);
jobject NewMethodText(JNIEnv*,jsize);
void *GetCallbackAddress(const char*);
jobject GetMethodText(JNIEnv*,jclass,jint);
void SetMethodText(JNIEnv*,jclass,jint,jobject);

jlong FreeMemory(JNIEnv*);
jlong MaxMemory(JNIEnv*);
jlong TotalMemory(JNIEnv*);

/* Extra internal calls
 */
jobject AllocObject(JNIEnv*,jclass);
jarray AllocArray(JNIEnv*,jclass,jsize);
jclass CallerClass(JNIEnv*,jint);
jobject CloneObject(JNIEnv*,jobject);
JNIEnv *CurrentEnv();
jobject CurrentThread(JNIEnv*);
jclass DeriveClass(JNIEnv*,jobject,jstring,jbyteArray,jint,jint);
void EnqueueFinalizable(JNIEnv*,jobject);
jboolean EnqueueReference(JNIEnv*,jobject);
void Exit(JNIEnv*,jint);
jclass FindClassFromClassLoader(JNIEnv*,jobject,const char*);
void InitializeClass(JNIEnv*,jclass);
jobject LazyResolve(JNIEnv*,jobject,jint);
void LoadMetadata(JNIEnv*,jclass);
void LinkClass(JNIEnv*,jclass);
jint MonitorNotify(JNIEnv*,jobject);
jint MonitorNotifyAll(JNIEnv*,jobject);
jboolean MonitorOwns(JNIEnv*,jobject);
jint MonitorWait(JNIEnv*,jobject,jlong);
void PopAllLocalFrames(JNIEnv*);
void PrintFormatted(JNIEnv*,const char*,...);
void *ResolveNative(JNIEnv*,jclass,jint);
void RunFinalization(JNIEnv*);
void SwitchFromJava(JNIEnv*);
void SwitchToJava(JNIEnv*);
jboolean TraverseStackByTrace(JNIEnv*,jboolean(*)(JNIEnv*,jclass,jchar,jint,void*),void*);
void ThreadStart(JNIEnv*,jobject,jint,jboolean,jlong);
void ThrowByName(JNIEnv*,const char*,const char*);

jint JNICALL GNI_GetInterface(void**,jint);

#endif /* _JNIVM_H */

