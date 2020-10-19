package uqac.dim.androidprojet.mj_falling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ArrayRes;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;

import uqac.dim.androidprojet.R;
import uqac.dim.androidprojet.mj_boule.Ball;

/**
 * Created by laure on 28/03/2018.
 */

public class FallingView extends SurfaceView implements SurfaceHolder.Callback {

    private static int HEIGHT;
    private static int WIDTH;

    //liste de pieces
    private ArrayList<Ring> fallingRings = null;

    //mon personnage
    private Catcher catcher = null;

    //l'activité link
    private Falling fallingActivity = null;

    //thread pour dessiner
    private DisplayThread thread;

    //dessin
    private SurfaceHolder mHolder = null;
    private Paint brush;

    //mutex
    private Object mutex;

    public FallingView(Falling fA,Object mutex) {
        super(fA);
        this.mutex = mutex;
        init(fA);
    }

    private void init(Falling fA) {
        setFallingActivity(fA);

        mHolder = getHolder();
        mHolder.addCallback(this);
        thread = new DisplayThread();

        brush = new Paint();
        brush.setStyle(Paint.Style.FILL);
        brush.setTextSize(50);

        fallingRings = new ArrayList<Ring>();
        catcher = new Catcher();

        //faire en sorte que toutes les pieces font 1/5 de l'écran en largeur
        WindowManager wm = (WindowManager) fA.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        setWIDTH(display.getWidth());
        setHEIGHT(display.getHeight());

    }

    public static void setHEIGHT(int HEIGHT) {
        FallingView.HEIGHT = HEIGHT;
        FallingEngine.HEIGHT = HEIGHT;
        Ring.setHEIGHT(HEIGHT);
    }

    public static void setWIDTH(int WIDTH) {
        FallingView.WIDTH = WIDTH;
        FallingEngine.WIDTH = WIDTH;
        Ring.setWIDTH(WIDTH);
    }

    public ArrayList<Ring> getFallingRings() {
        return this.fallingRings;
    }

    public void setFallingRings(ArrayList<Ring> rings) {
        this.fallingRings = rings;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.setDrawing(true);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        thread.setDrawing(false);
        boolean fin = true;
        while (fin) {
            //on termine le thread
            try {
                thread.join();
                fin = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCatcher(Catcher catcher) {
        this.catcher = catcher;
    }

    public Falling getFallingActivity() {
        return fallingActivity;
    }

    public void setFallingActivity(Falling fallingActivity) {
        this.fallingActivity = fallingActivity;
    }

    public void stop() {
        this.surfaceDestroyed(mHolder);
    }

    private class DisplayThread extends Thread {

        private boolean drawing = true;

        @Override
        public void run() {
            while (drawing) {
                Canvas canvas = null;
                try {
                    //recupere et bloque pour dessiner sur le canvas
                    canvas = mHolder.lockCanvas();

                    synchronized (mHolder) {

                        synchronized (mutex) {
                            // Et on dessine
                            draw(canvas);
                            mutex.notifyAll();
                        }
                        /*draw(canvas);
                        try {
                            //on met en pause le thread
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }*/
                    }


                }finally {
                    if (canvas != null) {
                        //libere le canva pour l'afficher
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        public void setDrawing(boolean drawing) {
            this.drawing = drawing;
        }

        public boolean getDrawing() {
            return drawing;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawBackground(canvas);

        canvas.drawColor(Color.WHITE);

        drawLives(canvas);
        drawScore(canvas);
        //dessiner le personnage
        if (catcher != null) {
            //on dessine le rectangle de notre perso
            //sprite sonic : https://facundogomez.deviantart.com/art/Sonic-BtS-Sonic-Sprites-651348193

            brush.setColor(Color.RED);
            canvas.drawRect(catcher.getRectangle(), brush);

            Bitmap resizedBitmapCatcher = createBitmapCatcher();
            canvas.drawBitmap(resizedBitmapCatcher, catcher.getX() - catcher.WIDTH, catcher.getY() - catcher.HEIGHT, brush);
        }

        //dessine les pieces qui tombent
        if (fallingRings != null) {
            //dessiner les pieces qui tombent
            for (Ring r : fallingRings) {

                Bitmap resizedBitmapRing = createBitmapRing();
                canvas.drawBitmap(resizedBitmapRing, r.getLeft(), r.getTop(), brush);

            }
        }

    }

    private void drawBackground(Canvas canvas) {
        Bitmap _scratch = BitmapFactory.decodeResource(getResources(),
                R.drawable.ring);

        /*int outHeight = (_scratch.getHeight() * HEIGHT) / _scratch.getWidth();
        int outWidth = (_scratch.getWidth() * WIDTH) / _scratch.getHeight();

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(_scratch, outWidth, outHeight, false);*/
        canvas.drawBitmap(_scratch, 0, 0, brush);

    }

    private void drawLives(Canvas canvas) {
        Bitmap _scratch = BitmapFactory.decodeResource(getResources(),
                R.drawable.lives);

        int outWidth = 0;
        int outHeight = 0;

        final int maxSize = Ring.RADIUS * 2;

        int inWidth = _scratch.getWidth();
        int inHeight = _scratch.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(_scratch, outWidth, outHeight, false);
        canvas.drawBitmap(resizedBitmap, 5, 5, brush);

        brush.setColor(Color.BLACK);
        canvas.drawText(Integer.toString(getFallingActivity().getLives()), 15+Ring.RADIUS*2, 25+Ring.RADIUS, brush);
    }

    private void drawScore(Canvas canvas) {
        Bitmap _scratch = BitmapFactory.decodeResource(getResources(),
                R.drawable.ring);

        int outWidth = 0;
        int outHeight = 0;

        final int maxSize = Ring.RADIUS * 2;

        int inWidth = _scratch.getWidth();
        int inHeight = _scratch.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(_scratch, outWidth, outHeight, false);
        canvas.drawBitmap(resizedBitmap, 5, 15 + Ring.RADIUS*2, brush);

        brush.setColor(Color.BLACK);
        String texte = ": "+Integer.toString(getFallingActivity().getScore());
        canvas.drawText(texte, 15+Ring.RADIUS*2, 75 + Ring.RADIUS*2, brush);

    }

    private Bitmap createBitmapRing()
    {
        Bitmap _scratch = BitmapFactory.decodeResource(getResources(),
                R.drawable.ring);

        int outWidth = 0;
        int outHeight = 0;

        final int maxSize = Ring.RADIUS * 2;

        int inWidth = _scratch.getWidth();
        int inHeight = _scratch.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(_scratch, outWidth, outHeight, false);
        return resizedBitmap;
    }

    private Bitmap createBitmapCatcher() {
        Bitmap _scratch;
        switch (Catcher.getDIRECTION()) {
            case NORMALE:
                _scratch = BitmapFactory.decodeResource(getResources(),
                        R.drawable.sonic);
                break;
            case DROITE:
                _scratch = BitmapFactory.decodeResource(getResources(),
                        R.drawable.sonic_left);
                break;
            case GAUCHE:
                _scratch = BitmapFactory.decodeResource(getResources(),
                        R.drawable.sonic_right);
                break;
            default:
                _scratch = null;
                break;
        }

        int outWidth = 0;
        int outHeight = 0;

        final int maxSizeHeight = Catcher.HEIGHT;
        final int maxSizeWidth = Catcher.WIDTH;

        int inWidth = _scratch.getWidth();
        int inHeight = _scratch.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSizeWidth;
            outHeight = (inHeight * maxSizeHeight) / inWidth;
        } else {
            outHeight = maxSizeHeight;
            outWidth = (inWidth * maxSizeWidth) / inHeight;
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(_scratch, outWidth, outHeight, false);
        return resizedBitmap;
    }
}
