/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#ifndef _HSTRUCTS_H
#define _HSTRUCTS_H

#include "plaf.h"

#define JNIPACKED     __attribute__((packed))

/* Useful macros
 */
#define ALIGN(X)            (((X)+(sizeof(void*)-1))&~(sizeof(void*)-1))
#define GETCLASS(X)         (((const jclass*)(X))[-1])
#define SETCLASS(X,Y)       (((jclass*)(X))[-1] = (Y))
#define LENGTH(X)           (((const jint*)(X))[-((sizeof(jclass)+sizeof(jint))/sizeof(jint))])
#define SETLENGTH(X,Y)      (((jint*)(X))[-((sizeof(jclass)+sizeof(jint))/sizeof(jint))] = (Y))
#define TAIL(X)             (((const void**)(X))[-2])
#define SETTAIL(X,Y)        (((void**)(X))[-2] = (Y))
#define CLASS_INFO(X)       (&((struct classInfo**)(X))[-2][-1])
#define TEXT_INFO(X)        (&((struct textInfo**)(X))[-2][-1])
#define FIELD_ID(X)         ((jfieldID)(X))
#define FIELD_INFO(X)       ((const struct fieldInfo*)(X))
#define METHOD_ID(X)        ((jmethodID)(X))
#define METHOD_INFO(X)      ((const struct methodInfo*)(X))
#define FIELD(X,Y,T)        (*(T*)&((jbyte*)(X))[Y])
#define STATICFIELD(X,Y,T)  (*(T*)&((jbyte*)(X))[ALIGN(CLASS_INFO(GETCLASS(X))->instanceSize)+(CLASS_INFO(X)->dynamicEntries+CLASS_INFO(X)->staticEntries)*sizeof(jobject)+(Y)])
#define ARRAY_ELEMENTS(X,T) ((T*)&((jbyte*)(X))[ALIGN(CLASS_INFO(GETCLASS(X))->instanceSize)])
#define ENTRY4TEXT(X)       ((X) == NULL ? NULL : (void*)&((jbyte*)(X))[ALIGN(CLASS_INFO(GETCLASS(X))->instanceSize)])
#define REFENC(X,Y,Z,W)     (*(jint*)&((jbyte*)ENTRY4TEXT(X))[(Y)] = ((jint)(W)-(Z)))
#define REFDEC(X,Y,Z)       (jobject)(*(jint*)&((jbyte*)ENTRY4TEXT(X))[(Y)]+(Z))
#define GETMETHOD(X,Y)      (((const jobject*)&((jbyte*)(X))[ALIGN(CLASS_INFO(GETCLASS(X))->instanceSize)])[Y])
#define SETMETHOD(X,Y,Z)    (((jobject*)&((jbyte*)(X))[ALIGN(CLASS_INFO(GETCLASS(X))->instanceSize)])[Y] = (Z))
#define SCALED(X)           (sizeof(jobject)*(jint)((X)>>32)+(jint)(X))

struct classInfo {
  char *name;
  jlong version;
  jobject loader;
  jclass elementClass;
  jchar accessFlags;
  jchar directImpls;
  jbyte dimensions;
  jbyte arrayScale;
  jbyte gcflags;
  volatile jbyte status;
  volatile thread_t initThread;
  jsize referentOfs;
  jsize staticSize;
  jsize instanceSize;
  jsize staticRefOfs;
  jsize instanceRefOfs;
  jchar staticRefCount;
  jchar instanceRefCount;
  jsize staticEntries;
  jsize dynamicEntries;
  struct reflectInfo *reflect;
  void **nativePtrs;
  jclass superClass;
  struct interfaceInfo *interfaces;
} JNIPACKED;
                                 
struct textInfo {
  jobject target;
  jboolean skip;
  jboolean lazy;
  jchar index;
  jclass declaringClass;
  void (*catcher)(void*,jthrowable);
  struct inspectorInfo *inspector;
  struct refencInfo *refenc;
} JNIPACKED;

struct interfaceInfo {
  jclass clazz;
  jsize baseIndex;
} JNIPACKED;

struct reflectInfo {
  jchar sourceFlags;
  struct fieldInfo *fields;
  struct methodInfo *methods;
  char *declaringClass;
  struct innerInfo *inners;
  char *sourceFile;
} JNIPACKED;

struct fieldInfo {
  jclass declaringClass;
  char *name;
  char *descriptor;
  jsize offset;
  jchar accessFlags;
} JNIPACKED;

struct methodInfo {
  jclass declaringClass;
  char *name;
  char *descriptor;
  char **exceptions;
  jsize index;
  jchar accessFlags;
} JNIPACKED;

struct innerInfo {
  char *name;
  jchar accessFlags;
} JNIPACKED;

struct inspectorInfo {
  void *raddr;
  struct traceInfo *trace;
  jbyte *lives;
  //jint site;
} JNIPACKED;

struct traceInfo {
  jclass clazz;
  jchar index;
  jchar line;
} JNIPACKED;

struct refencInfo {
  jint offset;
  jint disp;
} JNIPACKED;

#endif /* _HSTRUCTS_H */

