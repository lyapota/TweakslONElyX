#!/system/bin/sh
# ========================================
# script for modPack
# ========================================
# Created by lyapota
FILE=/system/build.prop
prop=$1
arg=$2

if [ $arg -eq @ ]; then
  prop_value=`grep -i $prop= $FILE | sed "s/$prop=//"`
  echo $prop_value
else
  if grep -Fq $prop $FILE ; then
      lineNum=`busybox sed -n "/${prop}/=" $FILE`
      sed -i "${lineNum} c${prop}=${arg}" $FILE
  else
      echo $prop=$arg >> $FILE
  fi;
fi;
