package utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/*
*
*@author luhaisheng
*@time 2016/6/28 14:21
*
* 功能一：设置开关可以随时打印log,关闭log默认是打开的，当生成release版本时在Application中关闭
* Logger.closeAllLog();
*
*功能二：打印的日志具体到某类某方法以及所在的线程
* 功能三：自动写入到sd卡
*
*
*/
public final class Logger {

    private static final String TAG = "MainActivity";

    private static Logger instance;

    public static boolean DEBUG_ENABLE = true;
    public static boolean FILE_ENABLE = true;

    private static String FILE_PATH = StorageUtil.getSDCardPath();
    private static final int QUEUE_SIZE = 9999;
    private File nLogFile;
    private BlockingQueue<String> nWaitLogs;
    private WriterThread nLogThread;

    private static String LOGDIR="siolette";

    private Logger(){
        CharSequence time = DateFormat.format("yyyy-MM-dd",
                System.currentTimeMillis());
        File logDir = new File(FILE_PATH);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        Log.e(TAG,""+logDir.exists());
        nLogFile = new File(logDir, time + ".txt");
        nWaitLogs = new ArrayBlockingQueue<String>(QUEUE_SIZE, true);
        nLogThread = new WriterThread(nWaitLogs);
        nLogThread.start();
    }

    /**如果有sd卡，日志文件放入sd卡的/siolette/PackageName的最后一个.后的name<br>
     * 例如com.siolette.calculator的日志文件目录在/sdcard/siolette/calculator/<br>
     * 日志文件名用天命名<br>
     * 如果没有sd卡，文件放在app目录下的/siolette/PackageName的最后一个.后的name<br>
     * init之后用close关系日志输入流<br>
     * @param context
     */
    public static void initFile(Context context){
        if(!FILE_ENABLE || !DEBUG_ENABLE){
            return;
        }

        if (StorageUtil.hasSDCard()) {
            FILE_PATH = StorageUtil.getSDCardPath();
        } else {
            FILE_PATH = StorageUtil.getApplicationPath(context);
        }

        FILE_PATH = FILE_PATH.endsWith("/") ? FILE_PATH +LOGDIR+ File.separator
                : FILE_PATH + File.separator+LOGDIR+ File.separator;
        String[] splitPackName = context.getPackageName().split("\\.");
        if (splitPackName != null && splitPackName.length > 0) {
            FILE_PATH = FILE_PATH + splitPackName[splitPackName.length - 1]
                    + File.separator;
        }
        close();
        android.util.Log.w("Log", "FILE_PATH = " + FILE_PATH);
        instance = new Logger();
    }
    /**指定一个目录输出日志文件，自动用天命名文件<br>
     * init之后用close关系日志输入流<br>
     * @param strDir
     */
    public static void initFile(String strDir){
        if(!FILE_ENABLE || !DEBUG_ENABLE){
            return;
        }
        FILE_PATH = strDir;
        if(TextUtils.isEmpty(FILE_PATH)){
            if(StorageUtil.hasSDCard()){
                FILE_PATH = StorageUtil.getSDCardPath();
            }else{
                return;
            }
        }
        close();
        instance = new Logger();
    }



    private void releaseRes(){
        if(nLogThread != null){
            nLogThread.shutdown();
        }
        if(nWaitLogs != null){
            nWaitLogs.clear();
        }
    }

    private void logToFile(String str){
        if(nWaitLogs != null){
            try {
                nWaitLogs.put(str);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void close(){
        if(instance != null){
            instance.releaseRes();
        }
    }


    /**
     * Set true or false if you want read logs or not
     */
    private static boolean logEnabled_d = true;
    private static boolean logEnabled_i = true;
    private static boolean logEnabled_e = true;

    /*
    *把日志全部关闭了
    *@author luhaisheng
    *@time 2016/6/28 10:35
    */
    public static void closeAllLog(){
        logEnabled_d=false;
        logEnabled_i=false;
        logEnabled_e=false;
    }
    /*
    *关闭日志除Error
    *@author luhaisheng
    *@time 2016/6/28 14:26
    */
    public static void closeLogUnlessE(){
        logEnabled_d=false;
        logEnabled_i=false;
    }
    //关闭保存日志到本地
    public static void closeSaveLogToLocal(){
        FILE_ENABLE=false;
    }

    public static void setLogDir(String logDir){
        LOGDIR=logDir;
    }


    public static void d(String TAG, String msg) {
        if (logEnabled_d) {
            android.util.Log.d(TAG, getLocation() + msg);
        }
        if(FILE_ENABLE){
            logToFile("DEBUG", TAG, getLocation() + msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (logEnabled_i) {
            android.util.Log.i(TAG, getLocation() + msg);
        }
        if(FILE_ENABLE){
            logToFile("INFO", TAG, getLocation() + msg);
        }
    }


    public static void e(String TAG, String msg) {
        if (logEnabled_e) {
            android.util.Log.e(TAG, getLocation() + msg);
        }
        if(FILE_ENABLE){
            logToFile("ERROR", TAG, getLocation() + msg);
        }
    }


    private static String getLocation() {
        final String className = Logger.class.getName();
        final StackTraceElement[] traces = Thread.currentThread()
                .getStackTrace();

        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + "; "
                                + trace.getMethodName() + "; "
                                + trace.getLineNumber() + "; ThreadName:"+ Thread.currentThread().getName() +"; ThreadId:"+ Thread.currentThread().getId()+"]: ";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }

    private class WriterThread extends Thread {
        BlockingQueue<String> nnContentQueue;
        boolean nnIsShutdown;

        public WriterThread(final BlockingQueue<String> bq) {
            nnContentQueue = bq;
            nnIsShutdown = true;
        }

        @Override
        public void run() {
            nnIsShutdown = false;
            String logContent = null;

            while(!nnIsShutdown){
                // 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                FileWriter fileWriter = null;
                BufferedWriter bufferedWriter = null;
                try {
                    fileWriter = new FileWriter(nLogFile, true);
                    bufferedWriter = new BufferedWriter(fileWriter);
                    logContent = nnContentQueue.take() + "\n";
                    if(logContent != null && bufferedWriter != null){
                        bufferedWriter.write(logContent);
                        bufferedWriter.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    StorageUtil.closeSilently(bufferedWriter);
                }
            }
        }

        public void shutdown(){
            nnIsShutdown = true;
        }
    }

    private final static void logToFile(String level, String tag, String msg) {
        if (!FILE_ENABLE)
            return;

        if(instance != null){
            Date date = new Date();
            String d = formatDate(date);

            StringBuffer sb = new StringBuffer();
            sb.append(d).append(" ").append(level).append("  ").append(tag)
                    .append("  ").append(msg).append("  ").append("\n");

            instance.logToFile(sb.toString());
        }
    }

    @SuppressLint("SimpleDateFormat")
    private final static String formatDate(Date d) {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS");
        return format.format(d);
    }

}
