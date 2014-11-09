#!/system/bin/sh
# ========================================
# script for modPack
# ========================================
# Created by lyapota
FILE=/system/build.prop
prop=$1
arg=$2

if grep -Fq $prop $FILE ; then
    lineNum=`busybox sed -n "/${prop}/=" $FILE`
    sed -i "${lineNum} c${prop}=${arg}" $FILE
else
    echo $prop=$arg >> $FILE
fi;
