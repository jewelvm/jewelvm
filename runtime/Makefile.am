# JEWEL, Java Environment With Enhanced Linkage
# Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira

AM_CFLAGS=-I$(gni_includedir) -I$(top_srcdir)/include
AM_LDFLAGS=-avoid-version -module -no-undefined -rpath $(libdir) -Wl,-s

noinst_HEADERS=\
	arch.h\
	gcheap.h\
	hstructs.h\
	jnivm.h\
	monman.h\
	plaf.h

lib_LTLIBRARIES=@runtime_LTLIBRARIES_md@
EXTRA_LTLIBRARIES=\
	libjvm.la\
	jvm.la

libjvm_la_SOURCES=\
	arch.c\
	callback.c\
	compat.c\
	gc.c\
	gni.c\
	heap.c\
	jni.c\
	monman.c\
	plaf.c

jvm_la_SOURCES=\
	arch.c\
	callback.c\
	compat.c\
	gc.c\
	gni.c\
	heap.c\
	jni.c\
	monman.c\
	plaf.c
