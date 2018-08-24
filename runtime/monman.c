/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <stdlib.h>

#include "gcheap.h"
#include "monman.h"

void montab_init(montab_t *montab)
{
  montab->top_id = 1;
  montab->recycled = NULL;
  mutex_init(&montab->monitors[0].mutex);
}

void montab_fini(montab_t *montab)
{
}

jboolean mon_acquire(montab_t *montab, jobject object)
{
  jint id;

  id = get_mon_id(object);
  mutex_lock(&montab->monitors[id].mutex);
  if (id == 0) {
    id = get_mon_id(object);
    if (id == 0) {
      if (montab->recycled != NULL) {
        id = montab->recycled-montab->monitors;
        montab->recycled = montab->recycled->next;
      } else {
        if (montab->top_id == 0) {
          mutex_unlock(&montab->monitors[0].mutex);
          return JNI_FALSE;
        }
        id = montab->top_id;
        montab->top_id = (montab->top_id+1)%MAX_MON;
      }
      set_mon_id(object, id);
      mutex_init(&montab->monitors[id].mutex);
    }
    mutex_unlock(&montab->monitors[0].mutex);
    mutex_lock(&montab->monitors[id].mutex);
  }
  return JNI_TRUE;
}

jboolean mon_owns(montab_t *montab, jobject object)
{
  jint id;
  
  id = get_mon_id(object);
  if (id == 0)
    return JNI_FALSE;
  return mutex_locked(&montab->monitors[id].mutex);
}

void mon_release(montab_t *montab, jobject object)
{
  jint id;

  id = get_mon_id(object);
  mutex_unlock(&montab->monitors[id].mutex);
}

void mon_recycle(montab_t *montab, jobject object)
{
  jint id;

  id = get_mon_id(object);
  if (id != 0) {
    mutex_lock(&montab->monitors[0].mutex);
    montab->monitors[id].next = montab->recycled;
    montab->recycled = &montab->monitors[id];
    mutex_unlock(&montab->monitors[0].mutex);
    set_mon_id(object, 0);
  }
}

void mon_notify(montab_t *montab, jobject object)
{
  jint id;

  id = get_mon_id(object);
  mutex_notify(&montab->monitors[id].mutex);
}

void mon_notify_all(montab_t *montab, jobject object)
{
  jint id;

  id = get_mon_id(object);
  mutex_notify_all(&montab->monitors[id].mutex);
}

jboolean mon_wait(montab_t *montab, jobject object)
{
  jint id;

  id = get_mon_id(object);
  return mutex_wait(&montab->monitors[id].mutex);
}

jboolean mon_timed_wait(montab_t *montab, jobject object, jlong millis)
{
  jint id;

  id = get_mon_id(object);
  return mutex_timed_wait(&montab->monitors[id].mutex, millis);
}

