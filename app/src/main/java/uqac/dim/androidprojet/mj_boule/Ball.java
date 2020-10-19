package uqac.dim.androidprojet.mj_boule;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by laure on 25/03/2018.
 */

public class Ball {

    //données sur la boule

    //pos
    private float x;
    private float y;

    //sa position initiale afin d'etre placée
    private RectF initialBallRect;

    //sa hitbox afin de vérifier dans le MazeData si l'on a pas une collision
    private RectF ballHitbox;

    //son rayon
    public static int RADIUS;

    //sa couleur
    private int color = Color.BLUE;

    //vitesse de base
    private float xSpeed =0;
    private float ySpeed = 0;

    //max vitesse
    public static final float MAX_SPEED = 4.0f;

    //limitateurs
    public static final float SLOWER = 200f;
    public static final float HIT = 10f;

    //taille ball image
    private int width;
    private int height;

    //gap
    private static int GAP;

    public Ball()
    {
        Log.i("Ball","Constructor Ball");

        //on crée la hitbox de base
        ballHitbox = new RectF();
    }

    public static int getGAP() {
        return GAP;
    }

    public static void setGAP(int GAP) {
        Ball.GAP = GAP;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {

        Log.i("Ball","Methode setX");
        this.x = x ;

        //on vérifie qu'on ne dépasse pas les limites
        if(this.x < RADIUS)
        {
            this.x = RADIUS;
            ySpeed = -ySpeed / HIT;
        }
        else if(this.x > width - RADIUS )
        {
            this.x = width - RADIUS ;
            ySpeed = -ySpeed / HIT;
        }
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y ;

        if(getY() < RADIUS )
        {
            this.y = RADIUS ;
            xSpeed = -xSpeed / HIT;
        }
        else if(getY() > height - RADIUS )
        {
            this.y = height - RADIUS ;
            xSpeed = -xSpeed / HIT;

        }
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    public RectF setNewCoordinates(float _x, float _y)
    {
        Log.i("Ball","Changement de vitesse et de position");

        xSpeed += _x / SLOWER;
        //si on dépasse la limite de vitesse
        if(xSpeed > MAX_SPEED)
        {
            xSpeed = MAX_SPEED;
        }
        if(xSpeed < - MAX_SPEED)
        {
            xSpeed = -MAX_SPEED;
        }

        ySpeed += _y / SLOWER;
        //si on dépasse la limite de vitesse
        if(ySpeed > MAX_SPEED)
        {
            ySpeed = MAX_SPEED;
        }
        if(ySpeed < - MAX_SPEED)
        {
            ySpeed = -MAX_SPEED;
        }

        //met à jour les coordonnées
        setX(this.x + ySpeed);
        setY(this.y + xSpeed);

        //l'hitbox de la balle a donc changé
        Log.i("Ball","new Hitbox");
        ballHitbox.set(this.x - RADIUS, this.y - RADIUS, this.x + RADIUS, this.y + RADIUS);

        return ballHitbox;
    }

    public void setRectInitial(RectF initial)
    {
        //on initialise les coordonnées de la balle de base
        this.initialBallRect = initial;
        this.x = initial.left + RADIUS;
        this.y = initial.top + RADIUS;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void lose()
    {
        //on remet la ball à la position initiale
        xSpeed = 0; ySpeed = 0;
        x = initialBallRect.left + RADIUS; y = initialBallRect.top + RADIUS;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public static int getRADIUS()
    {
        return Ball.RADIUS;
    }

    public static void setRADIUS(int r)
    {
        Ball.RADIUS = r;
    }


}
