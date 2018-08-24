/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <stdlib.h>
#include <string.h>

#define _JNI_IMPL

#include "arch.h"
#include "hstructs.h"
#include "jnivm.h"

#define BLOCK_SIZE          (1*1024*1024)
#define BLOCKALIGN(X)       (((X)+(BLOCK_SIZE-1))&~(BLOCK_SIZE-1))

// heap initialization

static
jint betoh(jint value)
{
  unsigned u = 1;

  if (((char*)&u)[0] == 1) {
    value = ( value        & 0xFF) << 24
          | ((value >>  8) & 0xFF) << 16
          | ((value >> 16) & 0xFF) <<  8
          | ((value >> 24) & 0xFF);
  }
  return value;
}

static
jint readInt(FILE *file)
{
  int b0, b1, b2, b3;

  b3 = fgetc(file);
  if (b3 == EOF)
    return -1;
  b2 = fgetc(file);
  if (b2 == EOF)
    return -1;
  b1 = fgetc(file);
  if (b1 == EOF)
    return -1;
  b0 = fgetc(file);
  if (b0 == EOF)
    return -1;
  return (b0 & 0xFF)
       | (b1 & 0xFF) <<  8
       | (b2 & 0xFF) << 16
       | (b3 & 0xFF) << 24;
}

static
char *readUTF(FILE *file)
{
  int b0, b1;
  jchar length;
  char *utf;
  jint i;

  b1 = fgetc(file);
  if (b1 == EOF)
    return NULL;
  b0 = fgetc(file);
  if (b0 == EOF)
    return NULL;
  length = (b0 & 0xFF)
         | (b1 & 0xFF) <<  8;
  utf = (char*)malloc(length+1);
  if (utf == NULL)
    return NULL;
  for (i = 0; i < length; i++) {
    b0 = fgetc(file);
    if (b0 == EOF) {
      free(utf);
      return NULL;
    }
    utf[i] = b0;
  }
  utf[length] = '\0';
  return utf;
}

//static
jclass _CMethodText;//remove

static
void *readImage(gcheap_t *gcheap, const char *name)
{
  FILE *file;
  jint magic;
  jint version;
  jint mode;
  jint size;
  void *buf;
  jint count;
  jint *relocs = NULL;
  jint i;
  jint patches;
  jint j;
  jint offset;
  char *symbol;
  void *fptr;
  jclass CCharArray = NULL;
  jclass CClassLoader = NULL;
  jclass CMethodText = NULL;
  jclass CString = NULL;
  jclass CThread = NULL;
  jclass HMainBlock = NULL;

  file = fopen(name, "rb");
  if (file == NULL)
    return NULL;

  magic = readInt(file);
  if (magic != 0xBEBACAFE)
    return NULL;
  version = readInt(file);
  if (version != 0x00010000)
    return NULL;
  mode = readInt(file);
  if ((mode & ~1) != 0)
    return NULL;
  size = readInt(file);
  if (size < 0)
    return NULL;

  buf = memoryCommit(NULL, size);
  if (buf == NULL)
    return NULL;
  if (fread(buf, 1, size, file) != size)
    return NULL;

  count = readInt(file);
  if (count < 0)
    return NULL;
  relocs = (jint*)realloc(relocs, count*sizeof(jint));
  if (relocs == NULL)
    return NULL;
  if (fread(relocs, sizeof(jint), count, file) != count)
    return NULL;
  for (i = 0; i < count; i++) {
    offset = betoh(relocs[i]);
    if (offset >= 0)
      *(unsigned*)(buf+ offset) += (unsigned)buf;
    else 
      *(unsigned*)(buf+~offset) -= (unsigned)buf;
  }

  if ((mode & 1) != 0) {
    count = readInt(file);
    if (count < 0)
      return NULL;
    for (i = 0; i < count; i++) {
      symbol = readUTF(file);
      if (symbol == NULL)
        return NULL;
      fptr = GetCallbackAddress(symbol);
      if (fptr == NULL)
        return NULL;
      patches = readInt(file);
      if (patches < 0)
        return NULL;
      relocs = (jint*)realloc(relocs, patches*sizeof(jint));
      if (relocs == NULL)
        return NULL;
      if (fread(relocs, sizeof(jint), patches, file) != patches)
        return NULL;
      for (j = 0; j < patches; j++) {
        offset = betoh(relocs[j]);
        if (offset >= 0)
          *(unsigned*)(buf+ offset) += (unsigned)(fptr-buf);
        else
          *(unsigned*)(buf+~offset) -= (unsigned)(fptr-buf);
      }
      free(symbol);
    }
  }

  free(relocs);

  count = readInt(file);
  if (count < 0)
    return NULL;
  for (i = 0; i < count; i++) {
    symbol = readUTF(file);
    if (symbol == NULL)
      return NULL;
    offset = readInt(file);
    if (offset < 0)
      return NULL;
         if (strcmp(symbol, "CCharArray") == 0) CCharArray = buf+offset;
    else if (strcmp(symbol, "CClassLoader") == 0) CClassLoader = buf+offset;
    else if (strcmp(symbol, "CMethodText") == 0) CMethodText = buf+offset;
    else if (strcmp(symbol, "CString") == 0) CString = buf+offset;
    else if (strcmp(symbol, "CThread") == 0) CThread = buf+offset;
    else if (strcmp(symbol, "HMainBlock") == 0) HMainBlock = buf+offset;
    free(symbol);
  }
  if (CCharArray == NULL || CClassLoader == NULL || CMethodText == NULL || CString == NULL || CThread == NULL || HMainBlock == NULL)
    return NULL;
  
  if (fclose(file) != 0)
    return NULL;

  gcheap->heapTop = HMainBlock;
  gcheap->totalMemory = size;
  gcheap->CCharArray = CCharArray;
  gcheap->CClassLoader = CClassLoader;
  gcheap->CString = CString;
  gcheap->CThread = CThread;

  _CMethodText = CMethodText;

  return buf;
}

// free cell manipulation

static
jboolean f_is(void *f)
{
  return ((*(unsigned*)(f-W(2))) & ENDING_MASK) == 0;
}

static
void f_make(void *f)
{
  *(unsigned*)(f-W(2)) = 0;
  *(void**)(f-W(1)) = NULL;
}

static
void *f_prev(void *f)
{
  return *(void**)(f-W(1));
}

static
void f_setprev(void *f, void *p)
{
  *(void**)(f-W(1)) = p;
}

static
void *f_next(void *f)
{
  return (void*)(*(unsigned*)(f-W(2)) & NEXT_MASK);
}

static
void f_setnext(void *f, void *n)
{
  *(unsigned*)(f-W(2)) = ((unsigned)n & NEXT_MASK)
                       | (*(unsigned*)(f-W(2)) & ~NEXT_MASK);
}

static
unsigned f_size(void *f)
{
  return (*(unsigned*)(f-W(2)) & SMALL_MASK) != 0 ? 0 : *(unsigned*)f;
}

static
unsigned f_rsize(void *f)
{
  return (*(unsigned*)(f-W(4)) & SMALL_MASK) != 0 ? 0 : *(unsigned*)(f-W(3));
}

static
void f_setsize(void *f, unsigned s)
{
  if (s == 0)
    *(unsigned*)(f-W(2)) |= SMALL_MASK;
  else {
    *(unsigned*)(f-W(2)) &= ~SMALL_MASK;
    *(unsigned*)f = s;
    if (s == W(1))
      *(unsigned*)(f-W(1)) &= ~SMALL_MASK;
    else {
      if (s > W(2))
        *(unsigned*)(f+s-W(2)) = 0;
      *(unsigned*)(f+s-W(1)) = s;
    }
  }
}

// ending block manipulation

static
void b_make(void *b)
{
  *(unsigned*)(b-W(2)) = ENDING_MASK;
  *(void**)(b-W(1)) = NULL;
}

static
void *b_next(void *b)
{
  return (void*)(*(unsigned*)(b-W(2)) & NEXT_MASK);
}

static
void b_setnext(void *b, void *n)
{
 *(unsigned*)(b-W(2)) = ((unsigned)n & NEXT_MASK)
                      | (*(unsigned*)(b-W(2)) & ~NEXT_MASK);
}

static
unsigned b_size(void *b)
{
  return *(unsigned*)b;
}

static
void b_setsize(void *b, unsigned s)
{
  *(unsigned*)b = s;
}

// object manipulation

static
jboolean o_is(void *p)
{
  jobject object;
  jclass clazz;
  jclass clazzclazz;
  jclass clazzclazzclazz;

  object = p;
  clazz = GETCLASS(object);
  if (clazz == NULL)
    return JNI_FALSE;
  clazzclazz = GETCLASS(clazz);
  if (clazzclazz == NULL)
    return JNI_FALSE;
  clazzclazzclazz = GETCLASS(clazzclazz);
  return clazzclazzclazz == clazzclazz;
}

static
jsize o_size(jobject object)
{
  jclass clazz;
  jsize size;
  const struct classInfo *classInfo;

  clazz = GETCLASS(object);
  if (GETCLASS(clazz) == clazz || clazz == _CMethodText) {
    size = (jbyte*)TAIL(object)-(jbyte*)object;
    size += sizeof(void*);
  } else {
    classInfo = CLASS_INFO(clazz);
    size = ALIGN(classInfo->instanceSize);
    if (classInfo->dimensions != 0) {
      size += LENGTH(object)*classInfo->arrayScale;
      size = ALIGN(size);
      size += sizeof(void*);
    }
  }
  return size;
}

static
void *o_gcinfo(jobject object)
{
  jclass clazz;
  const struct classInfo *classInfo;
  jsize size;
  void *gcinfo;
  void *u;

  clazz = GETCLASS(object);
  if (clazz != NULL) {
    if (GETCLASS(clazz) == clazz || clazz == _CMethodText) {
      gcinfo = (void*)TAIL(object);
      return gcinfo;
    } 
    classInfo = CLASS_INFO(clazz);
    if (classInfo->dimensions != 0) {
      size = ALIGN(classInfo->instanceSize);
      size += LENGTH(object)*classInfo->arrayScale;
      size = ALIGN(size);
      gcinfo = (void*)&((jbyte*)object)[size];
      return gcinfo;
    }
  }
  u = object;
  gcinfo = (void*)(u-W(2));
  return gcinfo;
}

// free cell cache primitives

static
void insert_free(gcheap_t *gcheap, void *f)
{
  jint size;
  void *n;

  size = f_size(f);
  if (size < SMALL_SIZE) {
    n = gcheap->smallCache[size/W(1)];
    gcheap->smallCache[size/W(1)] = f;
  } else {
    n = gcheap->bigCache;
    gcheap->bigCache = f;
  }
  f_setprev(f, NULL);
  f_setnext(f, n);
  if (n != NULL)
    f_setprev(n, f);
}

static
void remove_free(gcheap_t *gcheap, void *f)
{
  jint size;
  void *p;
  void *n;

  p = f_prev(f);
  n = f_next(f);
  if (n != NULL)
    f_setprev(n, p);
  if (p != NULL)
    f_setnext(p, n);
  else {
    size = f_size(f);
    if (size < SMALL_SIZE)
      gcheap->smallCache[size/W(1)] = n;
    else
      gcheap->bigCache = n;
  }
}

static
void *search_free(gcheap_t *gcheap, jint size)
{
  void *f;
  void *n;
  void *p;
  jint ssize;

  if (size < SMALL_SIZE) {
    f = gcheap->smallCache[size/W(1)];
    if (f != NULL) {
      n = f_next(f);
      gcheap->smallCache[size/W(1)] = n;
      if (n != NULL)
        f_setprev(n, NULL);
      return f;
    }
    for (ssize = size+W(2); ssize < SMALL_SIZE; ssize += W(1)) {
      f = gcheap->smallCache[ssize/W(1)];
      if (f != NULL) {
        n = f_next(f);
        gcheap->smallCache[ssize/W(1)] = n;
        if (n != NULL)
          f_setprev(n, NULL);
        return f;
      }
    }
  }
  for (f = gcheap->bigCache; f != NULL; f = f_next(f))
    if (f_size(f) == size || f_size(f) >= size+W(2)) {
      p = f_prev(f);
      n = f_next(f);
      if (n != NULL)
        f_setprev(n, p);
      if (p != NULL)
        f_setnext(p, n);
      else
        gcheap->bigCache = n;
      return f;
    }
  return NULL;
}

static
void reclaim_tail(gcheap_t *gcheap, void *f, jint size)
{
  jint fsize;
  void *gcinfo;

  fsize = f_size(f);
  if (fsize == size) {
    gcinfo = o_gcinfo(f+size+W(2));
    atomicAnd(gcinfo, ~PREVFREE_MASK);
  } else {
    f_setsize(f, size);
    f = f+size+W(2);
    size = fsize-size-W(2);
    f_make(f);
    f_setsize(f, size);
    insert_free(gcheap, f);
  }
}

static
void *commit(gcheap_t *gcheap, jint size)
{
  void *p;
  void *b;
  void *f;
  JNIEnv *env;
  jint bsize;
  jint fsize;
  jint rsize;
  void *gcinfo;

  f = search_free(gcheap, size);
  if (f == NULL) {

    bsize = BLOCKALIGN(W(2)+size+W(2)+W(3));

    env = CurrentEnv();
    if (env->gcstate < 2) {
      if (env->gcstate < 1)
        if (gcheap->totalMemory+bsize >= env->jvm->minHeapSize)
          return GCRQ;
      if (gcheap->totalMemory+bsize >= env->jvm->maxHeapSize)
        return OOME;
    }

    p = memoryCommit(gcheap->heapTop+W(1), bsize);
    if (p == NULL) {
      p = memoryCommit(NULL, bsize);
      if (p == NULL)
        return NULL;
    }

    gcheap->totalMemory += bsize;
    gcheap->freeMemory += bsize-W(3);

    b = p+bsize-W(1);
    b_make(b);
    b_setsize(b, bsize);
    b_setnext(b, gcheap->heapTop);

    f = p+W(2);
    fsize = -W(2)+bsize-W(3);

    if (p == gcheap->heapTop+W(1)) {

      gcheap->freeMemory += W(3);

      b_setsize(b, b_size(gcheap->heapTop)+bsize);
      b_setnext(b, b_next(gcheap->heapTop));

      f -= W(3);
      fsize += W(3);

      gcinfo = o_gcinfo(gcheap->heapTop);
      if ((atomicGet(gcinfo) & PREVFREE_MASK) != 0) {
        rsize = f_rsize(f);
        f -= W(2)+rsize;
        fsize += W(2)+rsize;
        remove_free(gcheap, f);
      }

    }

    gcheap->heapTop = b;

    f_make(f);
    f_setsize(f, fsize);

  }
  reclaim_tail(gcheap, f, size);
  memset(f, 0, size);
  gcheap->freeMemory -= W(2)+size;
  return f;
}

// heap initialization/finalization

void heap_init(gcheap_t *gcheap)
{
  jsize i;
  void *buf;
  char *bindir;
  char *name;

  gcheap->heapTop = NULL;
  gcheap->totalMemory = 0;
  gcheap->freeMemory = 0;
  gcheap->bigCache = NULL;
  for (i = 0; i < SMALL_SIZE/W(1); i++)
    gcheap->smallCache[i] = NULL;
  mutex_init(&gcheap->hlock);
  gcheap->gcThread = THREAD_NONE;
  mutex_init(&gcheap->gclock);
  gcheap->CCharArray = NULL;
  gcheap->CClassLoader = NULL;
  gcheap->CString = NULL;
  gcheap->CThread = NULL;

  //modify
  bindir = userBindir();
  name = (char*)malloc(strlen(bindir)+12+1);
  if (name == NULL)
    abort();
  strcpy(name, bindir);
  strcat(name, "stdalone.vmx");
  buf = readImage(gcheap, name);
  if (buf == NULL)
    abort();
  free(name);

}

void heap_fini(gcheap_t *gcheap)
{
  // implement
}

// heap traversal

static
jobject heap_skip(void *untyped)
{
  while (!o_is(untyped))
    if (f_is(untyped))
      untyped += f_size(untyped)+W(2);
    else {
      untyped = b_next(untyped);
      if (untyped == NULL)
        return NULL;
      untyped += W(1)-b_size(untyped)+W(2);
    }
  return untyped;
}

jobject heap_first(gcheap_t *gcheap)
{
  void *untyped;
  jobject result;

  mutex_lock(&gcheap->hlock);

  untyped = gcheap->heapTop;
  if (untyped == NULL) {
    result = NULL;
    goto exit;
  }
  untyped += W(1)-b_size(untyped)+W(2);
  result = heap_skip(untyped);

exit:

  mutex_unlock(&gcheap->hlock);
  return result;
}

jobject heap_next(gcheap_t *gcheap, jobject object)
{
  void *untyped;
  jobject result;

  mutex_lock(&gcheap->hlock);

  untyped = object;
  untyped += o_size(object)+W(2);
  result = heap_skip(untyped);

  mutex_unlock(&gcheap->hlock);
  return result;
}

// object allocation/deallocation

jobject heap_alloc_object(gcheap_t *gcheap, jclass clazz)
{
  const struct classInfo *classInfo;
  jsize size;
  jobject result;
  void *gcinfo;

  classInfo = CLASS_INFO(clazz);
  size = ALIGN(classInfo->instanceSize);

  mutex_lock(&gcheap->hlock);

  result = (jobject)commit(gcheap, size);
  if (result == NULL || result == GCRQ || result == OOME)
    goto exit;

  SETCLASS(result, clazz);
  gcinfo = o_gcinfo(result);
  atomicSet(gcinfo, STRONGLY_REACHABLE|(classInfo->gcflags & FINALIZED_MASK));

exit:

  mutex_unlock(&gcheap->hlock);
  return result;
}

jarray heap_alloc_array(gcheap_t *gcheap, jclass clazz, jsize length)
{
  const struct classInfo *classInfo;
  jsize size;
  jarray result;
  void *gcinfo;

  classInfo = CLASS_INFO(clazz);
  size = ALIGN(classInfo->instanceSize);
  size += ALIGN(length*classInfo->arrayScale);

  mutex_lock(&gcheap->hlock);

  result = (jarray)commit(gcheap, size+sizeof(void*));
  if (result == NULL || result == GCRQ || result == OOME)
    goto exit;

  SETLENGTH(result, length);
  SETCLASS(result, clazz);
  gcinfo = o_gcinfo(result);
  atomicSet(gcinfo, STRONGLY_REACHABLE|(classInfo->gcflags & FINALIZED_MASK));

exit:

  mutex_unlock(&gcheap->hlock);
  return result;
}

jobject heap_alloc_method_text(gcheap_t *gcheap, jclass clazz, jsize length)
{
  const struct classInfo *classInfo;
  jsize size;
  jobject result;
  void *gcinfo;

  classInfo = CLASS_INFO(clazz);
  size = ALIGN(classInfo->instanceSize);
  size += ALIGN(length);

  mutex_lock(&gcheap->hlock);

  result = (jobject)commit(gcheap, size+sizeof(void*));
  if (result == NULL || result == GCRQ || result == OOME)
    goto exit;

  SETTAIL(result, (void*)&((jbyte*)result)[size]);
  SETCLASS(result, clazz);
  gcinfo = o_gcinfo(result);
  atomicSet(gcinfo, STRONGLY_REACHABLE|(classInfo->gcflags & FINALIZED_MASK));

exit:

  mutex_unlock(&gcheap->hlock);
  return result;
}

jclass heap_alloc_class(gcheap_t *gcheap, jclass clazz, jobject loader, char *name, jlong version,
                        jchar flags, jbyte gcflags, jsize referentOfs, jclass superClass,
                        const jclass *interfaces, const jint *indexes, jchar dimpls,
                        jint ssize, jint srefo, jchar srefs,
                        jint isize, jint irefo, jchar irefs,
                        jint stable, jint dtable, jchar natives,
                        jclass elem, jbyte dims, jbyte scale)
{
  jsize ifcount;
  struct classInfo *classInfo;
  struct interfaceInfo *iinfo;
  jsize i;
  jsize j;
  const struct classInfo *classClassInfo;
  jsize instanceSize;
  jsize entries;
  jsize staticSize;
  jclass result;
  void *gcinfo;

  ifcount = 0;
  if (superClass != NULL) {
    classInfo = CLASS_INFO(superClass);
    if (classInfo->interfaces != NULL)
      for (j = 0; classInfo->interfaces[j].clazz != NULL; j++)
        ifcount++;
  }
  for (i = 0; i < dimpls; i++) {
    ifcount++;
    classInfo = CLASS_INFO(interfaces[i]);
    if (classInfo->interfaces != NULL)
      for (j = 0; classInfo->interfaces[j].clazz != NULL; j++)
        ifcount++;
  }

  if (ifcount == 0)
    iinfo = NULL;
  else {
    iinfo = (struct interfaceInfo*)calloc(1, ifcount*sizeof(struct interfaceInfo)+sizeof(void*));
    if (iinfo == NULL)
      abort();
  }

  ifcount = 0;
  for (i = 0; i < dimpls; i++) {
    iinfo[ifcount].clazz = interfaces[i];
    iinfo[ifcount].baseIndex = indexes[i];
    ifcount++;
  }
  if (superClass != NULL) {
    classInfo = CLASS_INFO(superClass);
    if (classInfo->interfaces != NULL)
      for (j = 0; classInfo->interfaces[j].clazz != NULL; j++) {
        iinfo[ifcount].clazz = classInfo->interfaces[j].clazz;
        iinfo[ifcount].baseIndex = classInfo->interfaces[j].baseIndex;
        ifcount++;
      }
  }
  for (i = 0; i < dimpls; i++) {
    classInfo = CLASS_INFO(interfaces[i]);
    if (classInfo->interfaces != NULL)
      for (j = 0; classInfo->interfaces[j].clazz != NULL; j++) {
        iinfo[ifcount].clazz = classInfo->interfaces[j].clazz;
        iinfo[ifcount].baseIndex = indexes[i]+classInfo->interfaces[j].baseIndex;
        ifcount++;
      }
  }

  classClassInfo = CLASS_INFO(clazz);
  instanceSize = ALIGN(classClassInfo->instanceSize);
  entries = stable+dtable;
  staticSize = ALIGN(ssize);

  mutex_lock(&gcheap->hlock);

  result = (jclass)commit(gcheap, instanceSize+entries*sizeof(jobject)+staticSize+natives*sizeof(void*)+sizeof(struct classInfo)+sizeof(void*));
  if (result == NULL || result == GCRQ || result == OOME)
    goto exit;

  classInfo = (void*)result+instanceSize+entries*sizeof(jobject)+staticSize+natives*sizeof(void*);

  SETTAIL(result, &classInfo[1]);
  SETCLASS(result, clazz);
  gcinfo = o_gcinfo(result);
  atomicSet(gcinfo, STRONGLY_REACHABLE|(classClassInfo->gcflags & FINALIZED_MASK));

  classInfo->accessFlags = flags;
  classInfo->directImpls = dimpls;
  classInfo->name = name;
  classInfo->version = version;
  classInfo->loader = loader;
  classInfo->elementClass = elem;
  classInfo->dimensions = dims;
  classInfo->arrayScale = scale;
  classInfo->gcflags = gcflags;
  classInfo->status = 0;
  classInfo->initThread = THREAD_NONE;
  classInfo->referentOfs = referentOfs;
  classInfo->staticSize = ssize;
  classInfo->instanceSize = isize;
  classInfo->staticRefOfs = srefo;
  classInfo->instanceRefOfs = irefo;
  classInfo->staticRefCount = srefs;
  classInfo->instanceRefCount = irefs;
  classInfo->staticEntries = stable;
  classInfo->dynamicEntries = dtable;
  classInfo->reflect = NULL;
  classInfo->nativePtrs = (void**)&((jbyte*)result)[instanceSize+entries*sizeof(jobject)+staticSize];
  classInfo->superClass = superClass;
  classInfo->interfaces = iinfo;

exit:

  mutex_unlock(&gcheap->hlock);
  return result;
}

jobject heap_clone(gcheap_t *gcheap, jobject object)
{
  jclass clazz;
  const struct classInfo *classInfo;
  jsize size;
  jobject result;
  void *gcinfo;

  clazz = GETCLASS(object);
  if (GETCLASS(clazz) == clazz || clazz == _CMethodText) {
    fprintf(stderr, "Clone not implemented for Class and MethodText\n");
    abort();
  }

  classInfo = CLASS_INFO(clazz);
  size = o_size(object);

  mutex_lock(&gcheap->hlock);

  result = (jobject)commit(gcheap, size);
  if (result == NULL || result == GCRQ || result == OOME)
    goto exit;

  memcpy(result, object, size);

  if (classInfo->dimensions != 0)
    SETLENGTH(result, LENGTH(object));
  SETCLASS(result, clazz);
  gcinfo = o_gcinfo(result);
  atomicSet(gcinfo, STRONGLY_REACHABLE|(classInfo->gcflags & FINALIZED_MASK));

exit:

  mutex_unlock(&gcheap->hlock);
  return result;
}

void heap_free(gcheap_t *gcheap, jobject object)
{
  void *f;
  jint rsize;
  jint size;
  void *gcinfo;
  void *n;
  void *u;

  mutex_lock(&gcheap->hlock);

  u = object;

  size = o_size(u);
  gcinfo = o_gcinfo(u);

  f = u;

  gcheap->freeMemory += W(2)+size;
  
  if ((atomicGet(gcinfo) & PREVFREE_MASK) != 0) {
    rsize = f_rsize(f);
    f -= W(2)+rsize;
    size += W(2)+rsize;
    remove_free(gcheap, f);
  }

  n = f+size+W(2);
  if (!o_is(n))
    if (f_is(n)) {
      size += W(2)+f_size(n);
      remove_free(gcheap, n);
    }

  f_make(f);
  f_setsize(f, size);
  insert_free(gcheap, f);

  n = f+size+W(2);
  gcinfo = o_gcinfo(n);
  atomicOr(gcinfo, PREVFREE_MASK);

  mutex_unlock(&gcheap->hlock);
}

// get and set object monitor index

jint get_mon_id(jobject object)
{
  void *gcinfo;

  gcinfo = o_gcinfo(object);
  return (jint)((unsigned int)atomicGet(gcinfo) >> 8);
}

void set_mon_id(jobject object, jint id)
{
  void *gcinfo;

  gcinfo = o_gcinfo(object);
  atomicAnd(gcinfo, 0xFF);
  atomicOr(gcinfo, id << 8);
}

// get and set gcflags

void gc_init(jobject object)
{
  void *gcinfo;

  gcinfo = o_gcinfo(object);
  atomicAnd(gcinfo, ~(STATE_MASK|GRAY_MASK));
}

jboolean gc_touch(jobject object, jint reach)
{
  void *gcinfo;
  jint bits;

  gcinfo = o_gcinfo(object);
  bits = atomicGet(gcinfo);
  bits &= STATE_MASK;
  reach |= bits;
  reach &= STATE_MASK;
  if (reach != bits) {
    atomicOr(gcinfo, reach|GRAY_MASK);
    return JNI_TRUE;
  }
  return JNI_FALSE;
}

jboolean gc_grayed(jobject object, jint *reach)
{
  void *gcinfo;
  jint bits;

  gcinfo = o_gcinfo(object);
  bits = atomicGet(gcinfo);
  *reach = bits & STATE_MASK;
  if ((bits & GRAY_MASK) != 0) {
    atomicAnd(gcinfo, ~GRAY_MASK);
    return JNI_TRUE;
  }
  return JNI_FALSE;
}

jboolean gc_unfinalized(jobject object)
{
  void *gcinfo;
  jint bits;
  
  gcinfo = o_gcinfo(object);
  bits = atomicGet(gcinfo);
  return (bits & FINALIZED_MASK) == 0;
}

jboolean gc_unreachable(jobject object)
{
  void *gcinfo;
  jint bits;

  gcinfo = o_gcinfo(object);
  bits = atomicGet(gcinfo);
  return (bits & STATE_MASK) == 0;
}

jboolean gc_finalizable(jobject object)
{
  void *gcinfo;
  jint bits;

  gcinfo = o_gcinfo(object);
  bits = atomicGet(gcinfo);
  if ((bits & REACHABILITY_MASK) <= PHANTOM_REACHABLE)
    if ((bits & FINALIZED_MASK) == 0) {
      atomicOr(gcinfo, FINALIZED_MASK);
      return JNI_TRUE;
    }
  return JNI_FALSE;
}

jboolean gc_reach(jobject object, jint reach)
{
  void *gcinfo;
  jint bits;

  gcinfo = o_gcinfo(object);
  bits = atomicGet(gcinfo);
  return (bits & REACHABILITY_MASK) <= reach;
}

