/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include "hstructs.h"

#ifndef __i386__
#error i386!
#endif /* __i386__ */

void beforeJava()
{
#ifdef __i386__
  struct {
    unsigned char lo;
    unsigned char hi;
  } cw;
  void *p = &cw;

  asm("fnstcw (%0)" : : "r" (p) : "memory");
  cw.hi &= 243;
  cw.hi |= 3;
  cw.lo |= 63;
  asm("fldcw (%0)" : : "r" (p) : "memory");
#endif /* __i386__ */
}

/* Atomic implementation
 */
int atomicGet(void *p)
{
  return *(int*)p;
}

void atomicSet(void *p, int m)
{
  *(int*)p = m;
}

void atomicAnd(void *p, int m)
{
#ifdef __i386__
  asm("lock;andl %1,(%0)" : : "r" (p), "r" (m) : "memory");
#endif /* __i386__ */
}

void atomicOr(void *p, int m)
{
#ifdef __i386__
  asm("lock;orl %1,(%0)" : : "r" (p), "r" (m) : "memory");
#endif /* __i386__ */
}

/* TraverseStack
 */
jboolean TraverseStack(void *callback, jboolean (*func)(jboolean,jobject,void*,void*,void*), void *data)
{
  struct frameInfo {
    struct frameInfo *previous;
    union {
      jobject text;
      void *raddr;
    } u;
  } /*JNIPACKED*/;
  jboolean native;
  jobject text;
  struct frameInfo *frame;
  void *raddr;

  native = JNI_TRUE;
  frame = callback;
  while (frame != NULL) {
    raddr = frame->u.raddr;
    frame = frame->previous;
    text = frame[-1].u.text;
    if (text == NULL) {
      frame = frame[-1].previous;
      native = JNI_TRUE;
    } else {
      if (func(native, text, frame, raddr, data))
        return JNI_TRUE;
      native = JNI_FALSE;
    }
  }
  return JNI_FALSE;
}

void *getcatcher(jobject object)
{
  struct textInfo *textInfo;

  textInfo = TEXT_INFO(object);
  return textInfo->catcher;
}

void setthrown(JNIEnv *env, jthrowable thrown)
{
  (*env)->Throw(env, thrown);
}

#ifdef __i386__
#ifndef WIN32
asm(".globl _athrow_");
asm("_athrow_:");
#else
asm(".globl __athrow_@4");
asm("__athrow_@4:");
#endif /* WIN32 */
asm("popl %ecx");
asm("_athrow_.L0:");
asm("movl 4(%ebp),%edx");        // save return address
asm("movl (%ebp),%ebp");         // pop frame
asm("movl -4(%ebp),%eax");       // load MethodText
asm("testl %eax,%eax");          // pseudo frame
asm("je _athrow_.L1");
asm("pushl %edx");               // save return address
asm("pushl %eax");
#ifndef WIN32
asm("call getcatcher");
#else
asm("call _getcatcher");
#endif /* WIN32 */
asm("addl $4,%esp");
asm("popl %edx");                // restore return address
asm("testl %eax,%eax");          
asm("je _athrow_.L0");           // no Catcher
asm("pushl %edx");
asm("call *%eax");               // call catcher
asm("popl %edx");
asm("jmp _athrow_.L0");          // not catched
asm(".align 8");
asm("_athrow_.L1:");
asm("popl %eax");
asm("leal -20(%ebp),%esp");      // adjust stack pointer
asm("popl %edi");                // restore C caller %edi
asm("popl %esi");                // restore C caller %esi
asm("popl %ebx");                // restore C caller %ebx
asm("popl %ecx");                // topmost Java %ebp
asm("movl 8(%ebp),%edx");        // get JNIEnv as caller parameter
asm("pushl %eax");               // set JNIEnv.thrown
asm("pushl %edx");
#ifndef WIN32
asm("call setthrown");
#else
asm("call _setthrown");
#endif /* WIN32 */
asm("addl $8,%esp");
asm("xorl %eax,%eax");           // return NULL,0,check float/double
asm("xorl %edx,%edx");           
asm("leave");
asm("ret");
#endif /* __i386__ */

void *getentry(jobject object)
{
  return ENTRY4TEXT(object);
}

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

#ifdef __i386__
asm(".text");
asm(".align 4");
#ifndef WIN32
asm(".globl invokeObject");
asm(".globl invokeBoolean");
asm(".globl invokeByte");
asm(".globl invokeChar");
asm(".globl invokeShort");
asm(".globl invokeInt");
asm(".globl invokeLong");
asm(".globl invokeFloat");
asm(".globl invokeDouble");
asm(".globl invokeVoid");
asm("invokeObject:");
asm("invokeBoolean:");
asm("invokeByte:");
asm("invokeChar:");
asm("invokeShort:");
asm("invokeInt:");
asm("invokeLong:");
asm("invokeFloat:");
asm("invokeDouble:");
asm("invokeVoid:");
#else
asm(".globl _invokeObject");
asm(".globl _invokeBoolean");
asm(".globl _invokeByte");
asm(".globl _invokeChar");
asm(".globl _invokeShort");
asm(".globl _invokeInt");
asm(".globl _invokeLong");
asm(".globl _invokeFloat");
asm(".globl _invokeDouble");
asm(".globl _invokeVoid");
asm("_invokeObject:");
asm("_invokeBoolean:");
asm("_invokeByte:");
asm("_invokeChar:");
asm("_invokeShort:");
asm("_invokeInt:");
asm("_invokeLong:");
asm("_invokeFloat:");
asm("_invokeDouble:");
asm("_invokeVoid:");
#endif /* WIN32 */
asm("pushl %ebp");
asm("movl %esp,%ebp");
asm("pushl $0");
asm("pushl 28(%ebp)");
asm("pushl %ebx");
asm("pushl %esi");
asm("pushl %edi");
asm("movl 20(%ebp),%ecx");
asm("subl %ecx,%esp");
asm("movl %esp,%edi");
asm("movl 24(%ebp),%esi");
asm("cld");
asm("rep");
asm("movsb");
asm("cmpl $0,16(%ebp)");
asm("je invoke.L0");
asm("pushl 16(%ebp)");
asm("invoke.L0:");
asm("pushl 12(%ebp)");
#ifndef WIN32
asm("call getentry");
#else
asm("call _getentry");
#endif /* WIN32 */
asm("addl $4,%esp");
asm("call *%eax");
asm("popl %edi");
asm("popl %esi");
asm("popl %ebx");
asm("leave");
asm("ret");
#endif /* __i386__ */

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

#ifdef __i386__
asm(".text");
asm(".align 4");
#ifndef WIN32
asm(".globl callObjectNative");
asm(".globl callBooleanNative");
asm(".globl callByteNative");
asm(".globl callCharNative");
asm(".globl callShortNative");
asm(".globl callIntNative");
asm(".globl callLongNative");
asm(".globl callFloatNative");
asm(".globl callDoubleNative");
asm(".globl callVoidNative");
asm("callObjectNative:");
asm("callBooleanNative:");
asm("callByteNative:");
asm("callCharNative:");
asm("callShortNative:");
asm("callIntNative:");
asm("callLongNative:");
asm("callFloatNative:");
asm("callDoubleNative:");
asm("callVoidNative:");
#else
asm(".globl _callObjectNative");
asm(".globl _callBooleanNative");
asm(".globl _callByteNative");
asm(".globl _callCharNative");
asm(".globl _callShortNative");
asm(".globl _callIntNative");
asm(".globl _callLongNative");
asm(".globl _callFloatNative");
asm(".globl _callDoubleNative");
asm(".globl _callVoidNative");
asm("_callObjectNative:");
asm("_callBooleanNative:");
asm("_callByteNative:");
asm("_callCharNative:");
asm("_callShortNative:");
asm("_callIntNative:");
asm("_callLongNative:");
asm("_callFloatNative:");
asm("_callDoubleNative:");
asm("_callVoidNative:");
#endif /* WIN32 */
asm("pushl %ebp");
asm("movl %esp,%ebp");
asm("pushl %esi");
asm("pushl %edi");
asm("movl 20(%ebp),%ecx");
asm("subl %ecx,%esp");
asm("movl %esp,%edi");
asm("movl 24(%ebp),%esi");
asm("cld");
asm("rep");
asm("movsb");
asm("pushl 16(%ebp)");
asm("pushl 8(%ebp)");
asm("call *12(%ebp)");      // cdecl or stdcall
asm("leal -8(%ebp),%esp");
asm("popl %edi");
asm("popl %esi");
asm("leave");
asm("ret");
#endif /* __i386__ */

