package com.cbasolutions.cbapos.helper;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingArea extends SurfaceView implements SurfaceHolder.Callback {

    private final String TAG = "JEYLOGS";

    private final float FACTOR_X = 0.2f;
    private final float FACTOR_Y = 0.05f;
    //private final int MIN_SIG_POINTS = 10;

    private Paint currentPaint;
    private Paint bk_paint;
    private Path path;

    private ArrayList<Path> drawingQue;

    private int signature_points;
    private int min_x, max_x;
    private int min_y, max_y;

    private boolean isGeasture_ON;

    // public Path previewPath;

    public DrawingArea(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setBackgroundColor(Color.TRANSPARENT);

        drawingQue = new ArrayList<Path>();
        initPaint();
        init_values();
        getHolder().addCallback(this);
        // init_Background();

    }
   
    public Bitmap fetchSignature(){
       
        if(! isValidSig()){
            return null ;
        }
       
        //Bitmap img  = Bitmap.createBitmap(max_x - min_x, max_y - min_y, Bitmap.Config.ARGB_8888) ;
        Bitmap img  = Bitmap.createBitmap(max_x + 100 , max_y + 100, Bitmap.Config.ARGB_8888) ;
        Canvas canvas = new Canvas(img);
       
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
                bk_paint);
       // Path newPath = new Path();
        for (int i = 0; i < drawingQue.size(); i++) {
            Path tmpPath = drawingQue.get(i);
            
            canvas.drawPath(tmpPath, currentPaint);
            
        }
        
        /* -10 removed to element the Y<=0 or X<=0 bug*/
        //Bitmap cropped = Bitmap.createBitmap(img, min_x - 10, min_y - 10 , (max_x - min_x) + 20, (max_y - min_y) + 20);
       Log.i(TAG, "value of min_y:" + min_y) ;
       Log.i(TAG, "value of min_x:" + min_x) ;
       
       if(min_y < 0){
    	   min_y = 0 ;
       }
       
       if(min_x < 0){
    	   min_x = 0 ;
       }
       
        Bitmap cropped = Bitmap.createBitmap(img, min_x, min_y, (max_x - min_x) + 20, (max_y - min_y) + 20);
        
        
        return cropped;
    }
   
    public void clearPad(){
        init_Background() ;
    }

    private void init_Background() {
        Canvas canvas = null;
        drawingQue = new ArrayList<Path>();
        init_values();
        SurfaceHolder mSurfaceHolder = getHolder();
        synchronized (mSurfaceHolder) {
            canvas = mSurfaceHolder.lockCanvas(null);
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
                    bk_paint);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean isValidSig() {

       
        if (!isGeasture_ON) {
            return false;
        }
      

        float dis_x = max_x - min_x;
        float dis_y = max_y - min_y;

        float ratio_x = dis_x / getWidth();
        float ratio_y = dis_y / getHeight();
       
       // Log.i(TAG, "point 30") ;

        if (ratio_x < FACTOR_X) {
            return false;
        }
       
      //  Log.i(TAG, "point 40") ;

        if (ratio_y < FACTOR_Y) {
            return false;
        }
       
      //  Log.i(TAG, "point 50") ;

        return true;
    }

    public void printLogs() {
        Log.i(TAG, "inside print logs");
        Log.i(TAG, "sc height:" + getHeight());
        Log.i(TAG, "sc width:" + getWidth());
    }

    private void init_values() {
        signature_points = 0;
        isGeasture_ON = false;

        min_x = 0;
        max_x = 0;
        min_y = 0;
        max_y = 0;
    }

    private void updateX(int x) {
        if (!isGeasture_ON) {
            min_x = max_x = x;
            return;
        }

        if (x < min_x) {
            min_x = x;
            return;
        }

        if (x > max_x) {
            max_x = x;
        }
    }

    private void updateY(int y) {
        if (!isGeasture_ON) {
            min_y = max_y = y;
            return;
        }

        if (y < min_y) {
            min_y = y;
            return;
        }

        if (y > max_y) {
            max_y = y;
        }
    }

    private void initPaint() {
        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(Color.BLUE);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(3);

        bk_paint = new Paint();
        bk_paint.setColor(0xFFFFFFFF);
    }

    public void drawArea() {
        Canvas canvas = null;
        SurfaceHolder mSurfaceHolder = getHolder();
        synchronized (mSurfaceHolder) {
            canvas = mSurfaceHolder.lockCanvas(null);

            canvas.drawColor(0, PorterDuff.Mode.CLEAR);

            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
                    bk_paint);

            for (int i = 0; i < drawingQue.size(); i++) {
                Path tmpPath = drawingQue.get(i);
                canvas.drawPath(tmpPath, currentPaint);
            }

            // canvas.drawPath(path, currentPaint) ;

            mSurfaceHolder.unlockCanvasAndPost(canvas);

        }

    }

    private void initDrawingThread() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1);
                    drawArea();
                } catch (Exception e) {

                }

            }
        });
        thread.start();
    }

    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.i(TAG, "inside the outouch down");

            path = new Path();
            drawingQue.add(path);
            path.moveTo(event.getX(), event.getY());
            path.lineTo(event.getX() + 1, event.getY());

            updateX((int) event.getX());
            updateY((int) event.getY());
            isGeasture_ON = true;
            signature_points++;

            initDrawingThread();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            path.lineTo(event.getX() + 1, event.getY());

            updateX((int) event.getX());
            updateY((int) event.getY());
            signature_points++;

            initDrawingThread();

        }

        return true;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        init_Background();

    }

    public void surfaceCreated(SurfaceHolder holder) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

}
