# JEWEL, Java Environment With Enhanced Linkage
# Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira

SUBDIRS=jewel java
CLEANFILES=jewel.jar
EXTRA_DIST=MANIFEST.MF.in
PKGDIRS=\
	java/lang\
	jewel/arch/i386\
	jewel/core\
	jewel/core/bend\
	jewel/core/cache\
	jewel/core/clfile\
	jewel/core/jiro\
	jewel/core/jiro/beg\
	jewel/core/jiro/control\
	jewel/core/jiro/dataflow\
	jewel/core/jiro/graph\
	jewel/core/jiro/optimize\
	jewel/tools/cfdump\
	jewel/tools/jewelg\
	jewel/tools/jewelq\
	jewel/tools/jiff

data_DATA=jewel.jar

jewel.jar: MANIFEST.MF $(PKGDIRS:=/classnoinst.stamp)
	$(JAR) cvfm $@ $< $(PKGDIRS:=/*.class)
