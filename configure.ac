# JEWEL, Java Environment With Enhanced Linkage
# Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira

AC_INIT(jewelvm,0.95a,bugreport@jewelvm.com)
AC_CONFIG_SRCDIR([launcher/jewel.c])
AC_CANONICAL_TARGET
AM_INIT_AUTOMAKE
AM_CONFIG_HEADER([config.h])
AC_PREFIX_DEFAULT([/usr/java/jewelvm])
AC_DISABLE_STATIC
AC_LIBTOOL_WIN32_DLL
AC_PROG_CC
AC_PROG_LIBTOOL
AC_PROG_JAVAC
AC_PROG_JAR
AC_EXEEXT

AC_CHECK_PROGS(RING, ring ring$EXEEXT)
if test -z "$RING"; then
  AC_MSG_ERROR([RInG was not found in \$PATH])
fi
AC_SUBST(RING)

AC_ARG_WITH(glasses,
AC_HELP_STRING([--with-glasses=DIR],[assume the GLASSES package is installed in DIR]),
[gni_includedir=$withval/include],
[gni_includedir=/usr/java/glasses/include])
AC_SUBST(gni_includedir)

launcher_PROGRAMS_md=
jewel_LDADD_md=
native_LTLIBRARIES_md=
runtime_LTLIBRARIES_md=
case "$host" in
*-*-mingw*)
  launcher_PROGRAMS_md='jewel$(EXEEXT) jewelw$(EXEEXT) jeweld$(EXEEXT)'
  jewel_LDADD_md='$(top_builddir)/runtime/jvm.la'
  native_LTLIBRARIES_md=builtin.la
  runtime_LTLIBRARIES_md=jvm.la
  ;;
*)
  launcher_PROGRAMS_md='jewel$(EXEEXT) jeweld$(EXEEXT)'
  jewel_LDADD_md='$(top_builddir)/runtime/libjvm.la'
  native_LTLIBRARIES_md=libbuiltin.la
  runtime_LTLIBRARIES_md=libjvm.la
  AC_CHECK_LIB(pthread,pthread_create)
  AC_CHECK_LIB(posix4,sched_yield)
  AC_CHECK_LIB(dl,dlopen)
  ;;
esac
AC_SUBST(launcher_PROGRAMS_md)
AC_SUBST(jewel_LDADD_md)
AC_SUBST(native_LTLIBRARIES_md)
AC_SUBST(runtime_LTLIBRARIES_md)

AC_CONFIG_FILES([Makefile
LICENSE
README
classes/MANIFEST.MF
classes/Makefile
classes/java/Makefile
classes/java/lang/Makefile
classes/jewel/Makefile
classes/jewel/arch/Makefile
classes/jewel/arch/i386/Makefile
classes/jewel/core/Makefile
classes/jewel/core/bend/Makefile
classes/jewel/core/cache/Makefile
classes/jewel/core/clfile/Makefile
classes/jewel/core/jiro/Makefile
classes/jewel/core/jiro/beg/Makefile
classes/jewel/core/jiro/control/Makefile
classes/jewel/core/jiro/dataflow/Makefile
classes/jewel/core/jiro/graph/Makefile
classes/jewel/core/jiro/optimize/Makefile
classes/jewel/tools/Makefile
classes/jewel/tools/cfdump/Makefile
classes/jewel/tools/jewelg/Makefile
classes/jewel/tools/jewelq/Makefile
classes/jewel/tools/jiff/Makefile
include/Makefile
launcher/Makefile
launcher/jewel.1
launcher/jeweld.1
native/Makefile
runtime/Makefile])
AC_OUTPUT
