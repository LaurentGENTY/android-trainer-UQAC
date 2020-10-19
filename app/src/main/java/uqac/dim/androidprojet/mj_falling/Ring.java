package uqac.dim.androidprojet.mj_falling;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by laure on 28/03/2018.
 */

public class Ring {

    //positions
    private float x;
    private float y;

    //vitesse de descente
    private static float ySpeed = 10;

    //couleur
    private int color = Color.GREEN;

    //hitbox
    private RectF hitbox;

    //son rayon
    public static int RADIUS;

    //valeurs statiques pour chaque piece
    private static int SIZE;

    //variables de taille écran
    private static int WIDTH;
    private static int HEIGHT;

    public Ring(float _x) {

        Log.i("Ring", "Nouveau Ring");
        Log.i("Ring","Position X : " + Float.toString(_x));

        if(_x + Ring.RADIUS > WIDTH)
        {
            Log.i("Ring",Integer.toString(SIZE));
            Log.i("Ring","Plus grand");
            this.x = WIDTH - Ring.RADIUS*2;
            this.y = - RADIUS;
            RectF temp = new RectF(this.x,this.y,WIDTH,RADIUS);
            setRectangle(temp);
        }
        else if(_x < Ring.RADIUS)
        {
            Log.i("Ring","Plus petit");
            this.x = Ring.RADIUS*2;
            this.y = - RADIUS;
            RectF temp = new RectF(0,this.y,this.x,RADIUS);
            setRectangle(temp);
        }
        else
        {
            this.y = - RADIUS;
            Log.i("Ring","Normal");
            RectF temp = new RectF(_x,this.y,_x+RADIUS*2,RADIUS);
            setRectangle(temp);
        }
    }

    public static void setSIZE(int width) {
        Ring.SIZE = width/20;
        Ring.RADIUS = width/20;
    }

    public RectF getRectangle() {
        return hitbox;
    }

    public void setRectangle(RectF rectangle) {
        this.hitbox = rectangle;
    }

    public float getX() {
        return hitbox.left;
    }

    public void setX(float x) {
        this.x = x;
        hitbox.left = x;
    }

    public float getY() {
        return hitbox.top;
    }

    public void setY(float y) {
        this.y = y;
        hitbox.top = y;
        hitbox.bottom = y+RADIUS*2;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static void setWIDTH(int WIDTH) {
        Ring.WIDTH = WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public static void setHEIGHT(int HEIGHT) {
        Ring.HEIGHT = HEIGHT;
    }

    public static float getySpeed() {
        return ySpeed;
    }

    public static void setySpeed(float ySpeed) {
        Ring.ySpeed = ySpeed;
    }

    public float getBottom() {
        return hitbox.bottom;
    }

    public float getLeft() {
        return hitbox.left;
    }

    public float getTop() {
        return hitbox.top;
    }

    public float getRight() {
        return hitbox.right;
    }
}
