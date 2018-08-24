# JEWEL, Java Environment With Enhanced Linkage
# Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira

JAVAROOT=$(top_builddir)/classes
CLEANFILES=$(RINGSRCS:.ring=.java)
EXTRA_DIST=$(JAVASRCS) $(RINGSRCS)

noinst_JAVA=\
	$(JAVASRCS)\
	$(RINGSRCS:.ring=.java)

JAVASRCS=\
	i386_Addr.java\
	i386_AddressingModes.java\
	i386_Architecture.java\
	i386_Backend.java\
	i386_BinaryAssembler.java\
	i386_FlowAssembler.java\
	i386_Imm.java\
	i386_InstructionSet.java\
	i386_Opc.java\
	i386_Opcodes.java\
	i386_Reg.java\
	i386_RegisterAllocator.java\
	i386_RegisterSet.java

RINGSRCS=\
	i386_CodeGenerator.ring

.ring.java:
	$(RING) -o $@ $<
