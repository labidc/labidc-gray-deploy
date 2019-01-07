#!/usr/bin/env bash

deploy(){
    mvn  clean deploy -Dmaven.test.skip=true
}

development(){
    mvn release:update-versions  -DautoVersionSubmodules=true -DdevelopmentVersion=1.0.x
}

release(){
    read -t 30 -p "请输入版本号:" newVersion
    mvn versions:set -DnewVersion=${newVersion}
}

main(){
    nowDIR=${1};
    echo ""
    echo "0.deploy"
    echo "1.修改为开发版本"
    echo "2.修改为正式版本"

    read -t 30 -p "请选择功能:" gongneng

    if [ "0" -eq "${gongneng}" ];then
        deploy
    elif [ "1" -eq "${gongneng}" ];then
        development
    elif [ "2" -eq "${gongneng}" ];then
        release
     else
        echo "没有您选的功能，服务结束！"
    fi
}

main
