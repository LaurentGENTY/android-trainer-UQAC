package uqac.dim.androidprojet.mj_boule;

import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by laure on 25/03/2018.
 */

public class Wall {

    private WALL_TYPE type = null;
    private int color;

    public static float WALL_SIZE = Ball.RADIUS *2;

    //positions
    public float x;
    public float y;

    //rectangle
    private RectF rectangle;

    //static du decalage de tous les walls
    private static int GAP;

    public Wall(String type, float x, float y) throws WrongWallType
    {
        Log.i("Wall","Constructor Wall");

            if(type == "VOID")
            {
                setType(WALL_TYPE.VOID);
                setColor(Color.BLACK);
            }
            else if(type == "STARTING")
            {
                setType(WALL_TYPE.STARTING);
                setColor(Color.GREEN);
            }
            else if(type == "ENDING")
            {
                setType(WALL_TYPE.ENDING);
                setColor(Color.RED);
            }
            else
            {
                throw new WrongWallType("Mauvais type de mur entré");
            }


        RectF temp = new RectF(x*WALL_SIZE + GAP,y*WALL_SIZE + GAP,(WALL_SIZE *(x+1)) + GAP ,GAP + (WALL_SIZE *(y+1)));
        setRectangle(temp);

    }

    public static int getGAP() {
        return GAP;
    }

    public static void setGAP(int GAP) {
        Wall.GAP = GAP;
    }

    public RectF getRectangle() {
        return rectangle;
    }

    public void setRectangle(RectF rectangle) {
        this.rectangle = rectangle;
    }

    public WALL_TYPE getType() {
        return type;
    }

    public void setType(WALL_TYPE type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }
}
