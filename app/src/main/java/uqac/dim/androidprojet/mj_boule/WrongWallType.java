package uqac.dim.androidprojet.mj_boule;

/**
 * Created by laure on 27/03/2018.
 */

class WrongWallType extends Throwable {

    private String error;
    public WrongWallType(String s) {
        this.error = s;
    }

    public String getError()
    {
        return this.error;
    }
}
