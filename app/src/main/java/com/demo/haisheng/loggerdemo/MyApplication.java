package com.demo.haisheng.loggerdemo;

import android.app.Application;

import utils.Logger;

/**
 * @创建者 luhaisheng
 * @创建时间 2016/6/28 15:02
 * @描述 ${TOO}
 * @更新者 $AUTHOR$
 * @创建时间 2016/6/28 15:02
 * @描述 ${TOO}
 */
public class MyApplication extends Application {

    private boolean isDebug=true;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化log
        Logger.initFile(this);
        //如果是release版本则关闭日志，并且不需要保存日志
        if(!isDebug){
            Logger.closeAllLog();
            Logger.closeSaveLogToLocal();
        }
    }
}
