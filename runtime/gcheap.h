/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#ifndef _GCHEAP_H
#define _GCHEAP_H

#include "plaf.h"

#define PREVFREE_MASK       0x00000001
#define FINALIZED_MASK      0x00000002
#define GRAY_MASK           0x00000004
#define FINALIZABLE_MASK    0x00000008
#define REACHABILITY_MASK   0x000000F0
#define STATE_MASK          (REACHABILITY_MASK|FINALIZABLE_MASK)

#define STRONGLY_REACHABLE  0x000000F0
#define SOFTLY_REACHABLE    0x00000070
#define WEAKLY_REACHABLE    0x00000030
#define PHANTOM_REACHABLE   0x00000010

#define SMALL_MASK          0x00000001
#define ENDING_MASK         0x00000002
#define NEXT_MASK           (~(ENDING_MASK|SMALL_MASK))

#define GCRQ  ((void*)-1)
#define OOME  ((void*)-2)

#define SMALL_SIZE          4096
#define W(X)                ((X)*sizeof(void*))

#define GCWIRED(X,Y)\
  if (1) {\
    jobject gcrval;\
    jbyte gcback = env->gcstate;\
loop:\
    mutex_lock(&env->lock);\
    gcrval = (Y);\
    env->gcstate = gcback;\
    if (gcrval == NULL)\
      env->functions->FatalError(env, "memory exausted");\
    if (gcrval == OOME) {\
      mutex_unlock(&env->lock);\
      env->gcstate = 2;\
      ThrowByName(env, "java/lang/OutOfMemoryError", NULL);\
      env->gcstate = gcback;\
      gcrval = NULL;\
      goto fail;\
    }\
    if (gcrval == GCRQ) {\
      mutex_unlock(&env->lock);\
      gc(env);\
      if (env->thrown != NULL) {\
        gcrval = NULL;\
        goto fail;\
      }\
      if (env->gcstate == 0)\
        env->gcstate = 1;\
      goto loop;\
    }\
    gcrval = env->functions->NewLocalRef(env, gcrval);\
    mutex_unlock(&env->lock);\
fail:\
    (X) = gcrval;\
  } else (void)0

typedef struct gcheap_t gcheap_t;

struct gcheap_t {
  void *heapTop;
  volatile jlong totalMemory;
  volatile jlong freeMemory;
  void *bigCache;
  void *smallCache[SMALL_SIZE/W(1)];
  mutex_t hlock;
  volatile thread_t gcThread;
  mutex_t gclock;
  //extra heap symbols
  jclass CCharArray;
  jclass CClassLoader;
  jclass CString;
  jclass CThread;
};

void heap_init(gcheap_t*);
void heap_fini(gcheap_t*);

jobject heap_alloc_object(gcheap_t*,jclass);
jarray heap_alloc_array(gcheap_t*,jclass,jsize);
jobject heap_alloc_method_text(gcheap_t*,jclass,jsize);
jclass heap_alloc_class(gcheap_t*,jclass,jobject,char*,jlong,jchar,jbyte,jsize,jclass,const jclass*, const jint*,
                        jchar,jint,jint,jchar,jint,jint,jchar,jint,jint,jchar,jclass,jbyte,jbyte);
jobject heap_clone(gcheap_t*,jobject);
void heap_free(gcheap_t*,jobject);

jobject heap_first(gcheap_t*);
jobject heap_next(gcheap_t*,jobject);

jint get_mon_id(jobject);
void set_mon_id(jobject,jint);

void gc_init(jobject);
jboolean gc_touch(jobject,jint);
jboolean gc_grayed(jobject,jint*);
jboolean gc_unfinalized(jobject);
jboolean gc_unreachable(jobject);
jboolean gc_finalizable(jobject);
jboolean gc_reach(jobject,jint);

extern jclass _CMethodText;//remove
void gc(JNIEnv*);

#endif /* _GCHEAP_H */

