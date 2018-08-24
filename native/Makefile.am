# JEWEL, Java Environment With Enhanced Linkage
# Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira

AM_CFLAGS=-I$(top_srcdir)/include -I$(top_srcdir)/runtime
AM_LDFLAGS=-avoid-version -module -no-undefined -rpath $(libdir) -Wl,-s

lib_LTLIBRARIES=@native_LTLIBRARIES_md@
EXTRA_LTLIBRARIES=\
	libbuiltin.la\
	builtin.la

libbuiltin_la_SOURCES=builtin.c
libbuiltin_la_LIBADD=$(top_builddir)/runtime/libjvm.la
libbuiltin_la_DEPENDENCIES=$(top_builddir)/runtime/libjvm.la

builtin_la_SOURCES=builtin.c
builtin_la_LIBADD=$(top_builddir)/runtime/jvm.la
builtin_la_DEPENDENCIES=$(top_builddir)/runtime/jvm.la
