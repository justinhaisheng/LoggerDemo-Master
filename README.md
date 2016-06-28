# LoggerDemo-Master

##功能介绍
###功能一：
**设置开关可以随时打印log,关闭log默认是打开的，当生成release版本时在Application中关闭 Logger.closeAllLog();**
###功能二：
**打印的日志具体可以到某类某方法以及所在的线程id和线程名**
###功能三：
**自动写入到sd卡siolette目录下的以当天日期命名的文本文件，目录可以修改**


##使用方法
* **在你自定义的MyApplication中配置 Logger.initFile(this); 初始化内容**
*  **Logger.d(TAG,"打印debug");**
*  **Logger.i(TAG,"打印info");**
*  **Logger.e(TAG,"打印error");**






