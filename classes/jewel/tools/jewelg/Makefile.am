# JEWEL, Java Environment With Enhanced Linkage
# Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira

JAVAROOT=$(top_builddir)/classes
CLEANFILES=$(RINGSRCS:.ring=.java)
EXTRA_DIST=$(JAVASRCS) $(RINGSRCS)

noinst_JAVA=\
	$(JAVASRCS)\
	$(RINGSRCS:.ring=.java)

JAVASRCS=\
	Actions.java\
	AssemblyImageOutputStream.java\
	BinaryImageOutputStream.java\
	ImageOutputStream.java\
	Machine.java\
	Main.java

RINGSRCS=\
	Closure.ring

.ring.java:
	$(RING) -o $@ $<
