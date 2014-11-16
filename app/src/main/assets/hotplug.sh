#!/system/bin/sh
# ========================================
# script lONElyX kernels
# ========================================
# Created by lyapota

#Init
HP_Q_ENABLED="/sys/devices/system/cpu/cpuquiet/enabled"
if [ -e $HP_Q_ENABLED ]; then
 	VAL_HP_Q=`cat $HP_Q_ENABLED`
else
	VAL_HP_Q="."
fi;

HP_I_ENABLED="/sys/module/intelli_plug/parameters/intelli_plug_active"
if [ -e $HP_I_ENABLED ]; then
 	VAL_HP_I=`cat $HP_I_ENABLED`
	GOV_Q=`cat /sys/devices/system/cpu/cpuquiet/current_governor`;
else
	VAL_HP_I="."
	GOV_Q="runnable";
fi;

HP_A_ENABLED="/sys/class/misc/mako_hotplug_control/mako_hotplug_active"
if [ -e $HP_A_ENABLED ]; then
 	VAL_HP_A=`cat $HP_A_ENABLED`
else
	VAL_HP_A="."
fi;

MPDEC="/system/bin/mpdecision";
PID_MPDEC=`pidof mpdecision`;

#set system writeble if need
if [ ! -w /system ]; then
  busybox mount -o rw,remount /system;
fi;

## set active hotplug
case $1 in
## get active hotplug
	@)
		# Mp-Decision
		if [ "$PID_MPDEC" -gt "0" ]; then
			echo "1";
		fi;

		# CPUQuiet
		if [ "$VAL_HP_Q" == "1" ]; then
			echo "2";
		fi;

		# Intelli
		if [ "$VAL_HP_I" == "1" ]; then
			echo "3";
		fi;

		# Mako
		if [ "$VAL_HP_A" == "1" ]; then
			echo "4";
		fi;
   	;;
## set active hotplug
   	1)
	#MpDecision
		if [ "$VAL_HP_Q" == "1" ]; then
			echo "0" > $HP_Q_ENABLED;
		fi;

		if [ "$VAL_HP_I" == "1" ]; then
			echo "0" > $HP_I_ENABLED;
		fi;

		if [ "$VAL_HP_A" == "1" ]; then
			echo "0" > $HP_A_ENABLED;
		fi;
		chmod 755 $MPDEC;
   	;;
    	2)
	#CPUQuiet
		chmod 444 $MPDEC;
		if [ "$PID_MPDEC" -gt "0" ]; then
			kill $PID_MPDEC;
		fi;

		if [ "$VAL_HP_I" == "1" ]; then
		   echo "0" > $HP_I_ENABLED;
		fi;

		if [ "$VAL_HP_A" == "1" ]; then
			echo "0" > $HP_A_ENABLED;
		fi;
		
		if [ "$VAL_HP_Q" == "0" ]; then
			echo "1" > $HP_Q_ENABLED;
			echo "$GOV_Q" > /sys/devices/system/cpu/cpuquiet/current_governor;
		fi;
	;;
    	3)
	#Intelli
		chmod 444 $MPDEC;
		if [ "$PID_MPDEC" -gt "0" ]; then
			kill $PID_MPDEC;
		fi;

		if [ "$VAL_HP_Q" == "1" ]; then
			echo "0" > $HP_Q_ENABLED;
		fi;

		if [ "$VAL_HP_A" == "1" ]; then
			echo "0" > $HP_A_ENABLED;
		fi;
		
		if [ "$VAL_HP_I" == "0" ]; then
			echo "1" > $HP_I_ENABLED;
		fi;
	;;
    	4)
	#Mako
		chmod 444 $MPDEC;
		if [ "$PID_MPDEC" -gt "0" ]; then
			kill $PID_MPDEC;
		fi;

		if [ "$VAL_HP_Q" == "1" ]; then
			echo "0" > $HP_Q_ENABLED;
		fi;

		if [ "$VAL_HP_I" == "1" ]; then
			echo "0" > $HP_I_ENABLED;
		fi;
		
		if [ "$VAL_HP_A" == "0" ]; then
			echo "1" > $HP_A_ENABLED;
		fi;
	;;
esac
