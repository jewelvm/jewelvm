/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#ifndef _MONMAN_H
#define _MONMAN_H

#include "plaf.h"

#define MAX_MON     (1*1024*1024)

typedef union mon_t mon_t;

union mon_t {
  mutex_t mutex;
  mon_t *next;
};

typedef struct montab_t montab_t;

struct montab_t {
  jint top_id;
  mon_t *recycled;
  mon_t monitors[MAX_MON];
};

void montab_init(montab_t*);
void montab_fini(montab_t*);

jboolean mon_acquire(montab_t*,jobject);
jboolean mon_owns(montab_t*,jobject);
void mon_release(montab_t*,jobject);
void mon_recycle(montab_t*,jobject);

void mon_notify(montab_t*,jobject);
void mon_notify_all(montab_t*,jobject);
jboolean mon_wait(montab_t*,jobject);
jboolean mon_timed_wait(montab_t*,jobject,jlong);

#endif /* _MONMAN_H */

