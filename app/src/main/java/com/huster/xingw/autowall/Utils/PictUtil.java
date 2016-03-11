package com.huster.xingw.autowall.Utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Xingw on 2016/3/11.
 */
public class PictUtil {
    public static String getImageFileName(String url){
        String fileName = url.substring(url.lastIndexOf('/') + 1, url.length()) + (url.endsWith(".jpg")?"":".jpg");
        return fileName;
    }

    public static void saveToFile(File file, Bitmap bmp)throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
    }
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }
}
