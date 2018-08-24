# JEWEL, Java Environment With Enhanced Linkage
# Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira

AM_CFLAGS=-I$(top_srcdir)/include
AM_LDFLAGS=-avoid-version -module -no-undefined -rpath $(libdir) -Wl,-s

bin_PROGRAMS=@launcher_PROGRAMS_md@
man_MANS=\
	jewel.1\
	jeweld.1
EXTRA_PROGRAMS=\
	jewel\
	jewelw\
	jeweld

jewel_SOURCES=jewel.c
jewel_LDADD=@jewel_LDADD_md@
jewel_DEPENDENCIES=@jewel_LDADD_md@

jewelw_SOURCES=jewel.c
jewelw_LDFLAGS=-mwindows
jewelw_LDADD=@jewel_LDADD_md@
jewelw_DEPENDENCIES=@jewel_LDADD_md@

jeweld_SOURCES=jeweld.c
