#!/bin/sh

# shell directory
SH_DIR=$(cd "$(dirname "$0")"; pwd)
cd ${SH_DIR}

#[ ! -e "${JAVA_HOME}/bin/java" ]
export JAVA_HOME
JAVA="${JAVA_HOME}/bin/java"
DATE_STR=`date +%F | sed 's/-//g'`-`date +%T | sed 's/://g'`

#JAVA="/CloudResetPwdUpdateAgent/depend/jre1.8.0_131/bin/java"
#JAVA="/usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java"
#JAVA="/usr/lib/jvm/openjdk-8u40-b25-linux-x64-10_feb_2015/java-se-8u40-ri/bin/java"
#JAVA="${JAVA_HOME}/bin/java"

################# user options ###############
APP_ID="headwaters"
JAR_FILE_NAME="headwaters.jar"
COMMON_LOG_FILE="./logs/systemout.log"
USER_OPT=" 172.16.84.161 9099 "
USER_OPT=" ${USER_OPT}  -Dspring.config.location=classpath:./ -Dfile.encoding=UTF-8"
#DEBUG_ARGS="-Xdebug -agentlib:jdwp=transport=dt_socket,address=9999,server=y,suspend=n"
################# user options ###############

################# java options ###############
JAVA_OPT=" -DappId=${APP_ID}  -server -Xmx2g  -Xms2g -Xmn1g"

# jetty suggestion
JAVA_OPT="${JAVA_OPT} -verbose:gc"
#JAVA_OPT="${JAVA_OPT} -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+PrintGCDetails"
JAVA_OPT="${JAVA_OPT} -Xloggc:./logs/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=20m"
JAVA_OPT="${JAVA_OPT} -XX:+PrintTenuringDistribution"
JAVA_OPT="${JAVA_OPT} -XX:+PrintCommandLineFlags"
JAVA_OPT="${JAVA_OPT} -XX:+DisableExplicitGC"
JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC"
JAVA_OPT="${JAVA_OPT} -XX:CMSInitiatingOccupancyFraction=80"
# r&d suggestion
JAVA_OPT="${JAVA_OPT} -XX:HeapDumpPath=./logs/dumpfile.hprof"
JAVA_OPT="${JAVA_OPT} -XX:+HeapDumpOnOutOfMemoryError"
# other like rocketmq
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
#JAVA_OPT="${JAVA_OPT} -XX:+AlwaysPreTouch"
JAVA_OPT="${JAVA_OPT} -XX:MaxDirectMemorySize=15g"
JAVA_OPT="${JAVA_OPT} -XX:-UseLargePages -XX:-UseBiasedLocking"
JAVA_OPT="${JAVA_OPT} ${OSS_OUT_OPTION}"
# r&d
################# java options ###############

################# pid  ###############
#ONE JAR MORE INSTANCE
PID=$(ps -ef|grep "\\-DappId=${APP_ID}" | grep -v grep|awk '{print $2}')
#ONE JAR ONE INSTANCE
#PID=$(fuser ${JAR_FILE_NAME} 2>/dev/null | xargs echo)
################# pid  ###############

start(){
    if [ ! -d "./logs" ];then
        mkdir ./logs
    fi
    if [ ! -d "./logs/jvm" ]; then
        mkdir ./logs/jvm
    fi
    if [ ! -d "./tmp" ]; then
        mkdir ./tmp
    fi
    if ! kill -0 ${PID} 2>/dev/null ;then
        echo "${APP_ID} is starting! File is ${JAR_FILE_NAME}"
        nohup ${JAVA}   -jar ${JAR_FILE_NAME}  ${USER_OPT} ${JAVA_OPT} >>  ${COMMON_LOG_FILE}  2>&1 &
    else
        echo "Failed to start '${APP_ID}',because it is running (pid:${PID})."
    fi 
    
}

stop(){
    if  kill -0 ${PID} 2>/dev/null ;then
        kill ${PID}
        echo "killing [${PID}]${APP_ID}"
    else
        echo "Failed to stop '${APP_ID}',It is not running."
    fi
}

status(){
    if kill -0 ${PID} 2>/dev/null ;then
        echo "${APP_ID} (pid:${PID}) is running."
    else
        echo "${APP_ID} is not running."
    fi 
}

log(){
     tail -100f logs/info.log
}

elog(){
     tail -100f logs/error.log
}

clog(){
     tail -100f logs/systemout.log
}

case $1 in 
    start)
	   start;;
    stop)
	   stop;;
    status)
	   status;;
    log)
       log;;
    elog)
       elog;;
    clog)
	   clog;;
    *)
	echo "Usage: $0 start|stop|status|log|elog|clog"
esac
