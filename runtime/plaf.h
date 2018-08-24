/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#ifndef _PLAF_H
#define _PLAF_H

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif /* HAVE_CONFIG_H */

#ifdef WIN32
#include <windows.h>
#include <shlobj.h>
#else
#include <pthread.h>
#include <sched.h>
#include <sys/mman.h>
#include <dlfcn.h>
#include <link.h>
#include <fcntl.h>
#include <pwd.h>
#include <sys/utsname.h>
#endif /* WIN32 */

#include <jni.h>

void *memoryCommit(void*,jint);
void *linkSymbol(const char*,jint);
char *userTopdir();
char *userBindir();
char *userLibdir();
char *userLibpath();

#define THREAD_NONE   ((thread_t)0)

#ifdef WIN32
typedef DWORD thread_t;
#else
typedef pthread_t thread_t;
#endif /* WIN32 */

thread_t thread_current();
jboolean thread_equals(thread_t,thread_t);
jint thread_create(void (JNICALL*)(JNIEnv*),JNIEnv*);
void thread_yield();

#ifdef WIN32
typedef struct {
  volatile DWORD level;
  volatile DWORD owner;
  CRITICAL_SECTION mutex;
  volatile DWORD waiting;
  HANDLE cond;
} mutex_t;
#else
typedef struct {
  pthread_mutex_t mutex;
  pthread_cond_t cond; 
} mutex_t;
#endif /* WIN32 */

void mutex_init(mutex_t*);
void mutex_lock(mutex_t*);
void mutex_unlock(mutex_t*);
jboolean mutex_locked(mutex_t*);
jboolean mutex_wait(mutex_t*);
jboolean mutex_timed_wait(mutex_t*,jlong);
void mutex_notify(mutex_t*);
void mutex_notify_all(mutex_t*);

#ifdef WIN32
typedef DWORD tls_t;
#else
typedef pthread_key_t tls_t;
#endif /* WIN32 */

void tls_init(tls_t*);
void *tls_get(tls_t*);
void tls_set(tls_t*,void*);

#endif /* _PLAF_H */

