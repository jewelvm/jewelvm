# JEWEL, Java Environment With Enhanced Linkage
# Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira

SUBDIRS=beg control dataflow graph optimize
JAVAROOT=$(top_builddir)/classes
CLEANFILES=$(RINGSRCS:.ring=.java)
EXTRA_DIST=$(JAVASRCS) $(RINGSRCS)

noinst_JAVA=\
	$(JAVASRCS)\
	$(RINGSRCS:.ring=.java)

JAVASRCS=\
	BranchOptimizations.java\
	CFGBuilder.java\
	CrossJumping.java\
	IR.java\
	IRBasicBlock.java\
	IRCFG.java\
	IRControlEdge.java\
	IRConverter.java\
	IROptimizer.java\
	IRStatement.java\
	Measure.java\
	UnreachableCodeElimination.java

RINGSRCS=\
	ArrayLengthPropagation.ring\
	Canonicalization.ring\
	CodeMotion.ring\
	ConditionTightening.ring\
	ConstantFolding.ring\
	ConstantPropagation.ring\
	CopyPropagation.ring\
	CriticalEdgeSplitting.ring\
	DeadArmRemoval.ring\
	DeadCodeElimination.ring\
	ExpressionBreaking.ring\
	ExpressionBuilding.ring\
	GlobalValueNumbering.ring\
	IfRemoval.ring\
	InitializationRemoval.ring\
	IRBinder.ring\
	LiveVariables.ring\
	LoadPrefetching.ring\
	LocalCopyPropagation.ring\
	LoopPeeling.ring\
	NonNullPointers.ring\
	ObjectExplosion.ring\
	PathFolding.ring\
	PathForwarding.ring\
	ProcedureIntegration.ring\
	ReachingDefinitions.ring\
	RegisterAllocating.ring\
	RegisterPacking.ring\
	SSAConverter.ring\
	SSAReverter.ring\
	Straightening.ring\
	TypePropagation.ring\
	Unswitching.ring\
	UselessStoreRemoval.ring

.ring.java:
	$(RING) -o $@ $<
