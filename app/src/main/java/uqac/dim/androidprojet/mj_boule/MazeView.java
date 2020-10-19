package uqac.dim.androidprojet.mj_boule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import uqac.dim.androidprojet.R;

/**
 * Created by laure on 25/03/2018.
 */

public class MazeView extends SurfaceView implements SurfaceHolder.Callback {

    //ma boule
    private Ball ball;

    //ma liste de murs
    private ArrayList<Wall> walls = null;

    //dessin
    private SurfaceHolder mHolder = null;
    private Paint brush;

    private DisplayThread thread;

    //taille écran
    private static int WIDTH;
    private static int HEIGHT;

    public MazeView(Context context) {
        super(context);
        init(context);

    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static void setWIDTH(int WIDTH) {
        MazeView.WIDTH = WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public static void setHEIGHT(int HEIGHT) {
        MazeView.HEIGHT = HEIGHT;
    }

    private void init(Context context) {
        mHolder = getHolder();
        mHolder.addCallback(this);
        thread = new DisplayThread();
        brush = new Paint();
        brush.setStyle(Paint.Style.FILL);
        ball = new Ball();

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Bitmap resizedBitmapBall = createBitmapResized("BALL");


        canvas.drawColor(Color.WHITE);
        //dessiner les blocs
        for (Wall w : walls) {
            brush.setColor(w.getColor());
            canvas.drawRect(w.getRectangle(), brush);


            if (w.getType() == WALL_TYPE.ENDING) {
                Bitmap resizedBitmapWall = createBitmapResized("ENDING");
                canvas.drawBitmap(resizedBitmapWall, w.getX() - Wall.WALL_SIZE, w.getY() - Wall.WALL_SIZE, brush);

            } else if (w.getType() == WALL_TYPE.STARTING) {
                Bitmap resizedBitmapWall = createBitmapResized("STARTING");
                canvas.drawBitmap(resizedBitmapWall, w.getX() - Wall.WALL_SIZE, w.getY() - Wall.WALL_SIZE, brush);
            } else if (w.getType() == WALL_TYPE.VOID) {

                //EDIT : lorsqu'on veut afficher toutes les images du jeu ca ram (trop demandant sans doute)
                /*Bitmap resizedBitmapWall = createBitmapResized("WALL");
                canvas.drawBitmap(resizedBitmapWall, w.getX() - Wall.WALL_SIZE, w.getY() - Wall.WALL_SIZE, brush);*/
                //canvas.drawRect(w.getRectangle(), brush);
            }
        }

        if (ball != null) {
            canvas.drawBitmap(resizedBitmapBall, ball.getX() - Ball.RADIUS, ball.getY() - Ball.RADIUS, brush);
        }
    }

    public Bitmap createBitmapResized(String type) {

        Bitmap _scratch;

        switch (type) {
            case "ENDING":
                Log.i("IMAGE", "END");
                _scratch = BitmapFactory.decodeResource(getResources(),
                        R.drawable.finish);
                break;
            case "STARTING":
                Log.i("IMAGE", "START");
                _scratch = BitmapFactory.decodeResource(getResources(),
                        R.drawable.start);
                break;
            case "VOID":
                _scratch = BitmapFactory.decodeResource(getResources(),
                        R.drawable.brickwall);
                break;
            case "BALL":
                _scratch = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ball);
                break;
            default:
                _scratch = null;
                break;
        }

        int outWidth = 0;
        int outHeight = 0;

        final int maxSize = (int)Wall.WALL_SIZE;

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

    //implémentations des méthodes de callback

    //lorsqu'on commence à dessiner dessus
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.setDrawing(true);
        thread.start();

        ball.setHeight(getHeight());
        ball.setWidth(getWidth());

    }

    //maj de l'image
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    //destruction de l'image
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i("MazeView", "surfaceDestroyed");
        thread.setDrawing(false);
        boolean fin = true;
        while (fin) {
            //on termine le thread
            try {
                thread.join();
                Log.i("MazeView", "Fin du thread");
                fin = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public ArrayList<Wall> getWalls() {
        return this.walls;
    }

    public void setWalls(ArrayList<Wall> walls) {
        this.walls = walls;
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
                        // Et on dessine
                        draw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        //libere le canva pour l'afficher
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    //on met en pause le thread
                    Thread.sleep(35);
                } catch (InterruptedException e) {
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
}
