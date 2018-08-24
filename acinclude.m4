# JEWEL, Java Environment With Enhanced Linkage
# Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira

AC_DEFUN([AC_PROG_JAVAC],[
AC_REQUIRE([AC_EXEEXT])
if test -z "$JAVAC"; then
  AC_CHECK_PROGS(JAVAC, javac javac$EXEEXT)
  if test -z "$JAVAC"; then
    AC_MSG_ERROR([no acceptable Java compiler found in \$PATH])
  fi
fi
AC_PROG_JAVAC_WORKS
AC_SUBST(JAVAC)
AC_SUBST(JAVACFLAGS)
AC_PROVIDE([$0])])

AC_DEFUN([AC_PROG_JAVAC_WORKS],[
AC_CACHE_CHECK([whether the Java compiler ($JAVAC $JAVACFLAGS) works],
  ac_cv_prog_javac_works,[AC_TRY_COMPILE_JAVA(ac_cv_prog_javac_works)])
if test "$ac_cv_prog_javac_works" = no; then
  AC_MSG_ERROR([installation or configuration problem: Java compiler cannot create classes.])
fi
AC_PROVIDE([$0])])

AC_DEFUN([AC_TRY_COMPILE_JAVA],[
cat > conftest.java << EOF
public strictfp class conftest { }
EOF
if AC_TRY_COMMAND($JAVAC $JAVACFLAGS conftest.java) && test -s conftest.class; then
  [$1]=yes
else
  [$1]=no
fi
rm -f conftest.java conftest.class
AC_PROVIDE([$0])])

AC_DEFUN([AC_PROG_JAR],[
AC_REQUIRE([AC_EXEEXT])
if test -z "$JAR"; then
  AC_CHECK_PROGS(JAR, jar jar$EXEEXT)
  if test -z "$JAR"; then
    AC_MSG_ERROR([no acceptable Java archiver found in \$PATH])
  fi
fi
AC_PROG_JAR_WORKS
AC_SUBST(JAR)
AC_PROVIDE([$0])])

AC_DEFUN([AC_PROG_JAR_WORKS],[
AC_CACHE_CHECK([whether the Java archiver ($JAR) works],
  ac_cv_prog_jar_works,[AC_TRY_ARCHIVE_JAVA(ac_cv_prog_jar_works)])
if test "$ac_cv_prog_jar_works" = no; then
  AC_MSG_ERROR([installation or configuration problem: Java archiver cannot create archives.])
fi
AC_PROVIDE([$0])])

AC_DEFUN([AC_TRY_ARCHIVE_JAVA],[
cat > conftest.java << EOF
public strictfp class conftest { }
EOF
if AC_TRY_COMMAND($JAR cf conftest.jar conftest.java) && test -s conftest.jar; then
  [$1]=yes
else
  [$1]=no
fi
rm -f conftest.java conftest.jar
AC_PROVIDE([$0])])

AC_DEFUN([AC_PROG_JAVAH],[
AC_REQUIRE([AC_EXEEXT])
if test -z "$JAVAH"; then
  AC_CHECK_PROGS(JAVAH, javah javah$EXEEXT)
  if test -z "$JAVAH"; then
    AC_MSG_ERROR([no acceptable Java header generator found in \$PATH])
  fi
fi
AC_PROG_JAVAH_WORKS
AC_SUBST(JAVAH)
AC_PROVIDE([$0])])

AC_DEFUN([AC_PROG_JAVAH_WORKS],[
AC_CACHE_CHECK([whether the Java header generator ($JAVAH) works],
  ac_cv_prog_javah_works,[AC_TRY_GENERATEHEADER_JAVA(ac_cv_prog_javah_works)])
if test "$ac_cv_prog_javah_works" = no; then
  AC_MSG_ERROR([installation or configuration problem: Java header generator cannot create headers.])
fi
AC_PROVIDE([$0])])

AC_DEFUN([AC_TRY_GENERATEHEADER_JAVA],[
if AC_TRY_COMMAND($JAVAH java.lang.Object) && test -s java_lang_Object.h; then
  [$1]=yes
else
  [$1]=no
fi
rm -f java_lang_Object.h
AC_PROVIDE([$0])])

AC_DEFUN([AC_JNI_INCLUDEDIRS],[
AC_REQUIRE([AC_CANONICAL_HOST])
AC_REQUIRE([AC_PROG_JAVAH])
AC_PATH_PROG(JAVAH,$JAVAH)
changequote(, )
jni_includedir=`echo $JAVAH | sed -e 's,//,/,' -e 's,\(.*\)/[^/]*/[^/]*$,\1/include,'`
jni_md_includedir=$jni_includedir/`echo $host_os | sed -e 's,[-0-9].*,,' -e 's,cygwin,win32,' -e 's,mingw,win32,'`
changequote([, ])
AC_CACHE_CHECK([for JNI include directories],
  ac_cv_jni_includedirs,[AC_TRY_INCLUDE_JNI_H(ac_cv_jni_includedirs)])
if test "$ac_cv_jni_includedirs" = no; then
  AC_MSG_ERROR([installation or configuration problem: unable to include <jni.h>.])
fi
AC_SUBST(jni_includedir)
AC_SUBST(jni_md_includedir)
AC_PROVIDE([$0])])

AC_DEFUN([AC_TRY_INCLUDE_JNI_H],[
ac_save_cppflags="$CPPFLAGS"
CPPFLAGS="$ac_save_cppflags -I$jni_includedir -I$jni_md_includedir"
AC_TRY_CPP([#include <jni.h>],[$1]=yes,[$1]=no)
CPPFLAGS="$ac_save_cppflags"
AC_PROVIDE([$0])])
