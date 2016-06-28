package utils;

import android.content.Context;
import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class StorageUtil {

	private final static String TAG = "StorageUtil";

	
	/**
	 * @return 得到sd卡的存储路径
	 */
	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**根据上下文(即：context)，得到其应用的存储路径
	 * @param context 上下文
	 * @return 应用的存储路径
	 */
	public static String getApplicationPath(Context context){
		return context.getApplicationContext().getFilesDir().getAbsolutePath();
	}
	
	/**判断是否存在sd卡
	 * @return 有SD卡返回true，否则false
	 */
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}
	
	/** 判断文件夹是否存在， 如果不存在会去创建，创建失败返回false
	 * @param filePath 文件路径
	 * @return true or false
	 */
	public static boolean isDirExist(String filePath){
		File file = new File(filePath);
		if(!file.exists()){
			if(file.mkdirs()){
				return true;
			}else{
				return false;
			}
		}
		return true;
	}


	public static final void closeSilently(Closeable close) {
		if (close == null) {
			return;
		}
		
		try {
			close.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	


}
