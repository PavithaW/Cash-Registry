package com.cbasolutions.cbapos.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Created by "Don" on 11/13/2017.
 * Class Functionality :-
 */

public class Util {

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        if(bitmap != null) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
        return null;
    }

    public static File saveImage(Bitmap finalBitmap,String fileName) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/cba_pos");
        if(!myDir.exists()) {
            myDir.mkdirs();
        }
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
        String fname = "item_img_"+ fileName +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public static boolean deleteImageFile(String fileToDelete, Context context){

        File file = new File(fileToDelete);
        file.delete();
        if(file.exists()){
            try {
                file.getCanonicalFile().delete();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(file.exists()){
                context.deleteFile(file.getName());
            }
        }
        return false;
    }
}
