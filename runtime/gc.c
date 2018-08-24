/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <stdlib.h>

#define _JNI_IMPL

#include "arch.h"
#include "hstructs.h"
#include "jnivm.h"

#define MAX_DEPTH           1024

static jboolean mark_children(jobject,jint);

static
jboolean mark(jobject object, jint reach, jint depth)
{
  jboolean changed;

  if (object != NULL) {
    changed = gc_touch(object, reach);
    if (changed) {
      if (depth == MAX_DEPTH)
        return JNI_TRUE;
      else
        return mark_children(object, depth);
    }
  }
  return JNI_FALSE;
}

static
jboolean mark_children(jobject object, jint depth)
{
  jboolean changed;
  jboolean grayed;
  jint reach;
  jclass clazz;
  jclass super;
  struct classInfo *classInfo;
  struct classInfo *superInfo;
  struct classInfo *objectInfo;
  struct textInfo *textInfo;
  struct textInfo *textInfo2;
  jint i;
  jint j;
  jint length;
  jint mentries;
  jobject referent;
  jobject reference;
  jobject *references;
  struct inspectorInfo *inspector;
  struct traceInfo *trace;
  struct refencInfo *refenc;

  changed = JNI_FALSE;

  grayed = gc_grayed(object, &reach);
  if (grayed) {

    clazz = GETCLASS(object);
    classInfo = CLASS_INFO(clazz);

    changed |= mark(clazz, reach, depth+1);

    referent = NULL;
    if ((classInfo->gcflags & REACHABILITY_MASK) != 0) {
      referent = FIELD(object, classInfo->referentOfs, jobject);
      FIELD(object, classInfo->referentOfs, jobject) = NULL;
      changed |= mark(referent, (reach & classInfo->gcflags & REACHABILITY_MASK)|(reach & FINALIZABLE_MASK), depth+1);
    }

    for (super = clazz; super != NULL; super = superInfo->superClass) {
      superInfo = CLASS_INFO(super);
      references = &FIELD(object, superInfo->instanceRefOfs, jobject);
      for (i = 0; i < superInfo->instanceRefCount; i++)
        changed |= mark(references[i], reach, depth+1);
    }

    if (referent != NULL)
      FIELD(object, classInfo->referentOfs, jobject) = referent;

    if (classInfo->dimensions > 0)
      if (classInfo->name[1] == 'L' || classInfo->name[1] == '[') {
        length = LENGTH(object);
        references = ARRAY_ELEMENTS(object,jobject);
        for (i = 0; i < length; i++)
          changed |= mark(references[i], reach, depth+1);
      }

    if (GETCLASS(clazz) == clazz) {
      objectInfo = CLASS_INFO(object);
      mentries = objectInfo->dynamicEntries+objectInfo->staticEntries;
      references = (jobject*)&GETMETHOD(object, 0);
      for (i = 0; i < mentries; i++) {
        reference = references[i];
        if (reference != NULL) {
          textInfo = TEXT_INFO(reference);
          if (textInfo->lazy)
            if (textInfo->skip) {
              reference = textInfo->target;
              references[i] = reference;
            }
          changed |= mark(reference, reach, depth+1);
        }
      }
      references = &STATICFIELD(object, objectInfo->staticRefOfs, jobject);
      for (i = 0; i < objectInfo->staticRefCount; i++)
        changed |= mark(references[i], reach, depth+1);
      changed |= mark(objectInfo->loader, reach, depth+1);
      changed |= mark(objectInfo->elementClass, reach, depth+1);
      changed |= mark(objectInfo->superClass, reach, depth+1);
      if (objectInfo->interfaces != NULL)
        for (i = 0; objectInfo->interfaces[i].clazz != NULL; i++)
          changed |= mark(objectInfo->interfaces[i].clazz, reach, depth+1);
    }

    if (GETCLASS(object) == _CMethodText) {
      textInfo = TEXT_INFO(object);
      changed |= mark(textInfo->declaringClass, reach, depth+1);
      refenc = textInfo->refenc;
      if (refenc != NULL)
        for (i = 0; refenc[i].offset != -1; i++) {
          reference = REFDEC(object, refenc[i].offset, refenc[i].disp);
          if (reference != NULL) {
            if (GETCLASS(reference) == _CMethodText) {
              textInfo2 = TEXT_INFO(reference);
              if (textInfo2->lazy)
                if (textInfo2->skip) {
                  reference = textInfo2->target;
                  REFENC(object, refenc[i].offset, refenc[i].disp, reference);
                }
            }
            changed |= mark(reference, reach, depth+1);
          }
        }
      inspector = textInfo->inspector;
      if (inspector != NULL)
        for (i = 0; inspector[i].raddr != NULL; i++) {
          trace = inspector[i].trace;
          if (trace != NULL)
            for (j = 0; trace[j].clazz != NULL; j++)
              changed |= mark(trace[j].clazz, reach, depth+1);
        }
      if (textInfo->lazy)
        changed |= mark(textInfo->target, reach, depth+1);
    }

  }
  return changed;
}

static
jboolean mark_stack_function(jboolean native, jobject text, void *frame, void *raddr, void *untyped)
{
  jboolean *changed = untyped;
  struct textInfo *textInfo;
  struct inspectorInfo *inspector;
  jbyte *lives;
  jobject *references;
  jint i;
  jint j;

  *changed |= mark(text, STRONGLY_REACHABLE, 0);
  textInfo = TEXT_INFO(text);
  inspector = textInfo->inspector;
  if (inspector != NULL)
    for (i = 0; inspector[i].raddr != NULL; i++)
      if (inspector[i].raddr == raddr) {
        lives = inspector[i].lives;
        if (lives != NULL) {
          references = (jobject*)frame;
          for (j = 0; lives[j] != 0; j++)
            *changed |= mark(references[lives[j]], STRONGLY_REACHABLE, 0);
        }
        break;
      }
  return JNI_FALSE;
}
static
jboolean mark_stack(void *callback)
{
  jboolean changed;

  changed = JNI_FALSE;
  TraverseStack(callback, mark_stack_function, &changed);
  return changed;
}

// preliminary non-parallel non-concurrent implementation
void gc(JNIEnv *env)
{
  JavaVM *jvm;
  gcheap_t *gcheap;
  jobject o;
  jclass clazz;
  const struct classInfo *classInfo;
  jobject referent;
  JNIEnv *e;
  JNIFrame *frame;
  jint i;
  jboolean changed;
  jobject tmp;
  jint reclaimed;
  jint enqueued;
  jint cleared;
  jint bfree;
  jint afree;
  jint totalk;

  jvm = env->jvm;
  gcheap = &jvm->gcheap;

  mutex_lock(&gcheap->gclock);
  if (thread_equals(gcheap->gcThread, thread_current())) {
    mutex_unlock(&gcheap->gclock);
    return;
  }
  if (gcheap->gcThread != THREAD_NONE) {
    while (mutex_wait(&gcheap->gclock));
    mutex_unlock(&gcheap->gclock);
    return;
  }
  gcheap->gcThread = thread_current();
  mutex_unlock(&gcheap->gclock);

  reclaimed = 0;
  enqueued = 0;
  cleared = 0;

  changed = JNI_FALSE;

  /* Wait for single threaded */
  if (jvm->verboseGC)
    PrintFormatted(env, "[GC, Waiting for single-threaded]\n");
  mutex_lock(&jvm->lock);
  for (e = jvm->envs; e != NULL; e = e->next)
    mutex_lock(&e->lock);

  bfree = (jint)((100*gcheap->freeMemory)/gcheap->totalMemory);
    
  /* Clear state mask */
  if (jvm->verboseGC)
    PrintFormatted(env, "[GC, Initializing state masks]\n");
  for (o = heap_first(gcheap); o != NULL; o = heap_next(gcheap, o))
    gc_init(o);

  /* Mark roots */
  if (jvm->verboseGC)
    PrintFormatted(env, "[GC, Marking roots]\n");
  changed |= mark(jvm->systemGroup, STRONGLY_REACHABLE, 0);
  changed |= mark(gcheap->CClassLoader, STRONGLY_REACHABLE, 0);
  if (jvm->globals != NULL)
    for (i = 0; i < jvm->globals->capacity; i++)
      changed |= mark(jvm->globals->entries[i], STRONGLY_REACHABLE, 0);
  if (jvm->weaks != NULL)
    for (i = 0; i < jvm->weaks->capacity; i++)
      changed |= mark(jvm->weaks->entries[i], WEAKLY_REACHABLE, 0);
  for (e = jvm->envs; e != NULL; e = e->next) {
    changed |= mark(e->thrown, STRONGLY_REACHABLE, 0);
    changed |= mark(e->thread, STRONGLY_REACHABLE, 0);
    changed |= mark_stack(e->callback);
    for (frame = e->topFrame; frame != NULL; frame = frame->previous)
      for (i = 0; i < frame->size; i++)
        changed |= mark(frame->entries[i], STRONGLY_REACHABLE, 0);
  }

  /* Mark unfinalized */
  if (jvm->verboseGC)
    PrintFormatted(env, "[GC, Marking unfinalized]\n");
  for (o = heap_first(gcheap); o != NULL; o = heap_next(gcheap, o))
    if (gc_unfinalized(o))
      changed |= mark(o, FINALIZABLE_MASK, 0);

  /* Iterate marking all heap */
  if (jvm->verboseGC)
    PrintFormatted(env, "[GC, Iterating through the heap]\n");
  for (i = 1; changed; i++) {
    if (jvm->verboseGC)
      PrintFormatted(env, "[GC, Iteration %d]\n", i);
    changed = JNI_FALSE;
    for (o = heap_first(gcheap); o != NULL; o = heap_next(gcheap, o))
      changed |= mark_children(o, 0);
  }

  /* Reclaim unreachable */
  if (jvm->verboseGC)
    PrintFormatted(env, "[GC, Reclaiming unreachable]\n");
  for (o = heap_first(gcheap); o != NULL; ) {
    tmp = o;
    o = heap_next(gcheap, o);
    if (gc_unreachable(tmp)) {
      mon_recycle(&jvm->montab, tmp);
      heap_free(gcheap, tmp);
      reclaimed++;
    }
  }

  /* Enqueue unfinalized */
  if (jvm->verboseGC)
    PrintFormatted(env, "[GC, Enqueueing unfinalized]\n");
  for (o = heap_first(gcheap); o != NULL; o = heap_next(gcheap, o))
    if (gc_finalizable(o)) {
      enqueued++;
      EnqueueFinalizable(env, o);
      if (env->thrown != NULL)
        goto exit;
    }

  /* Clear weak references */
  if (jvm->verboseGC)
    PrintFormatted(env, "[GC, Clearing weak references]\n");
  if (jvm->weaks != NULL)
    for (i = 0; i < jvm->weaks->capacity; i++)
      if (jvm->weaks->entries[i] != NULL)
        if (gc_reach(jvm->weaks->entries[i], WEAKLY_REACHABLE)) {
          jvm->weaks->entries[i] = NULL;
          cleared++;
        }
  for (o = heap_first(gcheap); o != NULL; o = heap_next(gcheap, o)) {
    clazz = GETCLASS(o);
    classInfo = CLASS_INFO(clazz);
    if ((classInfo->gcflags & REACHABILITY_MASK) == WEAKLY_REACHABLE || (classInfo->gcflags & REACHABILITY_MASK) == SOFTLY_REACHABLE) {
      referent = FIELD(o, classInfo->referentOfs, jobject);
      if (referent != NULL)
        if (gc_reach(referent, classInfo->gcflags & REACHABILITY_MASK)) {
          FIELD(o, classInfo->referentOfs, jobject) = NULL;
          cleared++;
          EnqueueReference(env, o);
          if (env->thrown != NULL)
            goto exit;
        }
    }
  }

exit:

  afree = (jint)((100*gcheap->freeMemory)/gcheap->totalMemory);
  totalk = (jint)(gcheap->totalMemory/1024);

  /* Notify threads */
  if (jvm->verboseGC)
    PrintFormatted(env, "[GC, Notifying all threads]\n");
  for (e = jvm->envs; e != NULL; e = e->next)
    mutex_unlock(&e->lock);
  mutex_unlock(&jvm->lock);

  if (jvm->verboseGC) {
    PrintFormatted(env, "[GC, %d objects reclaimed]\n", reclaimed);
    PrintFormatted(env, "[GC, %d unfinalized enqueued]\n", enqueued);
    PrintFormatted(env, "[GC, %d weak references cleared]\n", cleared);
    PrintFormatted(env, "[GC, %dk total memory (%d%% => %d%% free)]\n", totalk, bfree, afree);
  }

  mutex_lock(&gcheap->gclock);
  gcheap->gcThread = THREAD_NONE;
  mutex_notify_all(&gcheap->gclock);
  mutex_unlock(&gcheap->gclock);

  /* Run finalization */
  if (enqueued > 0) {
    if (env->jvm->verboseGC)
      PrintFormatted(env, "[GC, Running Finalizers]\n");
    RunFinalization(env);
  }

}

