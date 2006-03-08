AC_INIT(Posemuckel Client, 0.4, jn@linux-fuer-alle.de)

AC_CONFIG_FILES([posemuckel])

AC_ARG_VAR(INSTALL_DIR,[The path to the direktory where you unpacked this achrive])

AC_ARG_VAR(MOZILLA_HOME,[The path to the libraries used to embed mozillas gecko engine. Currently only Debian sarge is tested.])

AC_ARG_VAR(JAVA_BINARY,[The path to the java(tm) vm executable, you will need 1.5 or newer])

AC_SUBST(INSTALL_DIR,[$PWD])

AC_PATH_PROG([MOZILLA],[mozilla])
if test -z $ac_cv_path_MOZILLA ; then
   AC_MSG_ERROR([Could not find mozilla, which is needed for the posemuckel client. The client can not be used without it. Get it from http://www.mozilla.or])
fi


AC_CHECK_FILE(/usr/lib/mozilla/components/libembedcomponents.so,
                [AC_SUBST(MOZILLA_HOME,[/usr/lib/mozilla])],
                [AC_MSG_ERROR([Could not find mozillas libraries, that are needed to embed the gecko engine. Posemuckel can not be run without it. Only Debian Sarge is tested yet. Please contact us via jn@linux-fuer-alle.de])])



AC_PATH_PROG([JAVA_BINARY],[java])
if test -z $ac_cv_path_JAVA_BINARY ; then
   AC_MSG_ERROR([Could not find java(tm), which is needed for the posemuckel client. The client can not be used without it. Get it from http://java.sun.com and be shure to use 1.5 or newer.])
fi

AC_OUTPUT

chmod 755 posemuckel