/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <errno.h>
#include <sys/time.h>

#include "plaf.h"

/* Memory implementation
 */
void *memoryCommit(void *base, jint size)
{
#ifdef WIN32
  return VirtualAlloc(base, size, MEM_COMMIT, PAGE_EXECUTE_READWRITE);
#else
  void *p;
#ifdef __linux__
  p = mmap(base, size, PROT_EXEC|PROT_READ|PROT_WRITE, MAP_PRIVATE|MAP_ANONYMOUS, -1, 0);
#else
  static int fd0 = -1;

  if (fd0 == -1)
    fd0 = open("/dev/zero", O_RDWR);
  p = mmap(base, size, PROT_EXEC|PROT_READ|PROT_WRITE, MAP_PRIVATE, fd0, 0);
#endif /* __linux__ */
  if (p == MAP_FAILED)
    p = NULL;
  return p;
#endif /* WIN32 */
}

/* Link implementation
 */
void *linkSymbol(const char *symbol, jint words)
{
#ifdef WIN32
  static HINSTANCE lang_dll = NULL;
  void *p;
  char *temp;

  if (lang_dll == NULL)
    lang_dll = LoadLibrary("lang");
  if (lang_dll != NULL) {
    p = GetProcAddress(lang_dll, symbol);
    if (p != NULL)
      return p;
    temp = malloc(1+strlen(symbol)+1+6+1);
    if (temp == NULL) abort();
    sprintf(temp, "_%s@%d", symbol, 8+4*words);
    p = GetProcAddress(lang_dll, temp);
    free(temp);
    return p;
  }
  return NULL;
#else
  static void *liblang_so = NULL;
  char buffer[PATH_MAX];

  if (liblang_so == NULL) {
    strcpy(buffer, userBindir());
    strcat(buffer, "liblang.so");
    liblang_so = dlopen(buffer, RTLD_NOW);
  }
  if (liblang_so != NULL)
    return dlsym(liblang_so, symbol);
  return NULL;
#endif /* WIN32 */
}

/* User implementation
 */
char *userTopdir()
{
#ifdef WIN32
  static char buffer[MAX_PATH];
  int length;

  length = GetModuleFileName(GetModuleHandle("jvm.dll"), buffer, sizeof(buffer));
  buffer[length-11] = '\0';
  return buffer;
#else
#ifdef __linux__
  static char buffer[PATH_MAX];
  struct link_map *l_map;
  char *l_name;
  char *l_file;

  for (l_map = dlopen(NULL, RTLD_LAZY); l_map != NULL; l_map = l_map->l_next) {
    l_name = realpath(l_map->l_name, buffer);
    if (l_name == NULL)
      continue;
    l_file = strstr(l_name, "lib/libjvm.so");
    if (l_file != NULL) {
      l_file[0] = '\0';
      break;
    }
  }
  return buffer;
#else
  Dl_info dlinfo;
  static char buffer[PATH_MAX];
  char *l_name;
  char *l_file;

  dladdr((void*)userBindir, &dlinfo);
  l_name = realpath((char*)dlinfo.dli_fname, buffer);
  l_file = strstr(l_name, "lib/libjvm.so");
  l_file[0] = '\0';
  return buffer;
#endif /* __linux__ */
#endif /* WIN32 */
}

char *userBindir()
{
#ifdef WIN32
  static char buffer[MAX_PATH];
  
  strcpy(buffer, userTopdir());
  strcat(buffer, "bin\\");
  return buffer;
#else
  return userLibdir();
#endif /* WIN32 */
}

char *userLibdir()
{
#ifdef WIN32
  static char buffer[MAX_PATH];
  
  strcpy(buffer, userTopdir());
  strcat(buffer, "lib\\");
  return buffer;
#else
  static char buffer[PATH_MAX];

  strcpy(buffer, userTopdir());
  strcat(buffer, "lib/");
  return buffer;
#endif /* WIN32 */
}

char *userLibpath()
{
#ifdef WIN32
  int length;
  char *path;
  char *library_path;
  char *l_file;
  char tmp[MAX_PATH];

  length = 0;
  path = getenv("PATH");
  if (path != NULL)
    length = strlen(path)+1;
  library_path = malloc(3*(MAX_PATH+1)+1+length+1);
  
  GetModuleFileName(GetModuleHandle("jvm.dll"), tmp, sizeof(tmp));
  l_file = strrchr(tmp, '\\');
  l_file[0] = '\0';
  strcpy(library_path, tmp);
  strcat(library_path, ";");
  
  strcat(library_path, ".");
  
  strcat(library_path, ";");
  GetSystemDirectory(tmp, sizeof(tmp));
  strcat(library_path, tmp);
  
  strcat(library_path, ";");
  GetWindowsDirectory(tmp, sizeof(tmp));
  strcat(library_path, tmp);
  
  if (path != NULL) {
    strcat(library_path, ";");
    strcat(library_path, path);
  }
  
  return library_path;
#else
  char *user_lib;
  static char buffer[PATH_MAX];

  strcpy(buffer, ".");
  user_lib = getenv("LD_LIBRARY_PATH");
  if (user_lib != NULL) {
    strcat(buffer, ":");
    strcat(buffer, user_lib);
  }
  return buffer;
#endif /* WIN32 */
}

/* Thread implementation
 */
thread_t thread_current()
{
#ifdef WIN32
  return GetCurrentThreadId();
#else
  return pthread_self();
#endif /* WIN32 */
}

jboolean thread_equals(thread_t one, thread_t another)
{
#ifdef WIN32
  return one == another;
#else
  return pthread_equal(one, another);
#endif /* WIN32 */
}

void thread_yield()
{
#ifdef WIN32
  Sleep(0);
#else
  sched_yield();
#endif /* WIN32 */
}

jint thread_create(void (JNICALL *threadMain)(JNIEnv*), JNIEnv *env)
{
#ifdef WIN32
  DWORD tid;
  HANDLE handle;

  handle = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)threadMain, env, 0, &tid);
  if (handle == NULL)
    return -1;
  return 0;
#else
  pthread_t tid;
  pthread_attr_t attr;

  pthread_attr_init(&attr);
  pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
  return pthread_create(&tid, &attr, (void*(*)(void*))threadMain, env);
#endif /* WIN32 */
}

/* Mutex implementation
 */
void mutex_init(mutex_t *mutex)
{
#ifdef WIN32
  mutex->level = 0;
  mutex->owner = THREAD_NONE;
  InitializeCriticalSection(&mutex->mutex);
  mutex->waiting = 0;
  mutex->cond = CreateEvent(NULL, FALSE, FALSE, NULL);
#else
  pthread_mutexattr_t attr;
  
  pthread_mutexattr_init(&attr);
#ifdef __linux__
  pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_RECURSIVE_NP);
#else
  pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_RECURSIVE);
#endif /* __linux__ */
  pthread_mutex_init(&mutex->mutex, &attr);
  pthread_cond_init(&mutex->cond, NULL);
#endif /* WIN32 */
}

void mutex_lock(mutex_t *mutex)
{
#ifdef WIN32
  EnterCriticalSection(&mutex->mutex);
  if (mutex->level == 0)
    mutex->owner = thread_current();
  mutex->level++;
#else
  pthread_mutex_lock(&mutex->mutex);
#endif /* WIN32 */
}

void mutex_unlock(mutex_t *mutex)
{
#ifdef WIN32
  mutex->level--;
  if (mutex->level == 0)
    mutex->owner = THREAD_NONE;
  LeaveCriticalSection(&mutex->mutex);
#else
  pthread_mutex_unlock(&mutex->mutex);
#endif /* WIN32 */
}

jboolean mutex_locked(mutex_t *mutex)
{
#ifdef WIN32
  return mutex->owner == thread_current();
#else
  jint count;

  if (pthread_mutex_trylock(&mutex->mutex) == EBUSY)
    return JNI_FALSE;
#ifdef __linux__
  count = mutex->mutex.__m_count;
#else
  count = 1/*mutex->mutex.__m_count*/;
#endif /* __linux__ */
  pthread_mutex_unlock(&mutex->mutex);
  return count != 0;
#endif /* WIN32 */
}

jboolean mutex_wait(mutex_t *mutex)
{
#ifdef WIN32
  DWORD level;
  DWORD i;

  level = mutex->level;
  mutex->owner = THREAD_NONE;
  mutex->level = 0;
  mutex->waiting++;
  for (i = 0; i < level; i++)
    LeaveCriticalSection(&mutex->mutex);
  WaitForSingleObject(mutex->cond, INFINITE);
  for (i = 0; i < level; i++)
    EnterCriticalSection(&mutex->mutex);
  mutex->waiting--;
  mutex->owner = thread_current();
  mutex->level = level;
  return JNI_FALSE;
#else
  return pthread_cond_wait(&mutex->cond, &mutex->mutex) == EINTR;
#endif /* WIN32 */
}

jboolean mutex_timed_wait(mutex_t *mutex, jlong millis)
{
#ifdef WIN32
  DWORD level;
  DWORD i;

  level = mutex->level;
  mutex->owner = THREAD_NONE;
  mutex->level = 0;
  mutex->waiting++;
  for (i = 0; i < level; i++)
    LeaveCriticalSection(&mutex->mutex);
  WaitForSingleObject(mutex->cond, (DWORD)millis);
  for (i = 0; i < level; i++)
    EnterCriticalSection(&mutex->mutex);
  mutex->waiting--;
  mutex->owner = thread_current();
  mutex->level = level;
  return JNI_FALSE;
#else
  struct timeval tv;
  struct timespec ts;
  jlong deadline;

  gettimeofday(&tv, NULL);
  deadline = ((jlong)tv.tv_sec)*1000 + (jlong)(tv.tv_usec/1000);
  deadline += millis;
  ts.tv_sec = (int)(deadline / 1000);
  ts.tv_nsec = (int)(deadline % 1000) * 1000000;
  return pthread_cond_timedwait(&mutex->cond, &mutex->mutex, &ts) == EINTR;
#endif /* WIN32 */
}

void mutex_notify(mutex_t *mutex)
{
#ifdef WIN32
  PulseEvent(mutex->cond);
#else
  pthread_cond_signal(&mutex->cond);
#endif /* WIN32 */
}

void mutex_notify_all(mutex_t *mutex)
{
#ifdef WIN32
  DWORD waiting;
  DWORD i;

  waiting = mutex->waiting;
  for (i = 0; i < waiting; i++)
    PulseEvent(mutex->cond);
#else
  pthread_cond_broadcast(&mutex->cond);
#endif /* WIN32 */
}

/* Local storage implementation
 */
void tls_init(tls_t *tls)
{
#ifdef WIN32
  *tls = TlsAlloc();
#else
  pthread_key_create(tls, NULL);
#endif /* WIN32 */
}

void *tls_get(tls_t *tls)
{
#ifdef WIN32
  return TlsGetValue(*tls);
#else
  return pthread_getspecific(*tls);
#endif /* WIN32 */
}

void tls_set(tls_t *tls, void *value)
{
#ifdef WIN32
  TlsSetValue(*tls, value);
#else
  pthread_setspecific(*tls, value);
#endif /* WIN32 */
}

