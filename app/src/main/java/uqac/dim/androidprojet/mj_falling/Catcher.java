package uqac.dim.androidprojet.mj_falling;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by laure on 28/03/2018.
 */

public class Catcher {

    //positions
    private float x;
    private static float y;

    //vitesse de X
    private float xSpeed;

    //max vitesse
    public static final float MAX_SPEED = 4.0f;

    //limitateurs
    public static final float SLOWER = 20f;
    private static final float HIT = 10f;

    //couleur
    private int color;

    //hitbox
    private RectF hitbox = null;
    private RectF ring;

    //tailles
    public static int WIDTH;
    public static int HEIGHT;

    //positions initiales
    private RectF initialCatcher;

    //taille écran
    private static int SCREEN_WIDTH;

    //direction pour l'affichage des sprites
    private static DIRECTION DIRECTION = uqac.dim.androidprojet.mj_falling.DIRECTION.NORMALE;

    public Catcher()
    {
        hitbox = new RectF();
        ring = new RectF();
        initialCatcher = new RectF();
    }

    public static uqac.dim.androidprojet.mj_falling.DIRECTION getDIRECTION() {
        return DIRECTION;
    }

    public static void setDIRECTION(uqac.dim.androidprojet.mj_falling.DIRECTION DIRECTION) {
        Catcher.DIRECTION = DIRECTION;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {

        this.x = x;

        if(this.x < WIDTH)
        {
            this.x = WIDTH;
            xSpeed = -xSpeed / HIT;

        }
        else if(this.x > SCREEN_WIDTH - WIDTH )
        {
            this.x = SCREEN_WIDTH - WIDTH;
            xSpeed = -xSpeed / HIT;

        }
    }

    public float getY() {
        return y;
    }

    public static void setY(float y) {
        Catcher.y = y;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public RectF getRectangle()
    {
        return this.hitbox;
    }

    public void setRectangle(RectF rectangle) {
        this.hitbox = rectangle;
    }

    public RectF setNewCoordinates(float _x) {

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

        float newX = 0;
        newX = this.x + xSpeed;
        //met à jour les coordonnées
        setX(newX);

        hitbox.set(this.x - WIDTH, this.y - HEIGHT, this.x + WIDTH, this.y + HEIGHT);

        return hitbox;
    }

    public void setRectInitial(RectF initial)
    {
        //on initialise les coordonnées de la balle de base
        this.initialCatcher = initial;
        this.x = initial.left + WIDTH;
        this.y = initial.top + HEIGHT;
    }

    public static void setSIZE(int width, int height) {
        Catcher.SCREEN_WIDTH = width;
        Catcher.WIDTH = width/20;
        Catcher.HEIGHT = height/14;

        setY(height/10*8);
    }

    public RectF getHitbox() {
        return hitbox;
    }

    public void setHitbox(RectF hitbox) {
        this.hitbox = hitbox;
    }
}
