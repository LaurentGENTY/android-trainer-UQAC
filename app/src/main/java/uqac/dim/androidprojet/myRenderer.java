package uqac.dim.androidprojet;

import android.app.Activity;
import android.content.Context;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.ArcballCamera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.loader.LoaderOBJ;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.view.SurfaceView;

/**
 * Created by remib on 12/04/2018.
 */

public class myRenderer extends Renderer {

    private Object3D mModel;
    private DirectionalLight mDirectionalLight;
    private Context context;
    private SurfaceView surfaceView;
    private int modelResource;

    public myRenderer(Context context, SurfaceView surface, int modelResource) {
        super(context);
        this.context = context;
        this.surfaceView = surface;
        this.modelResource = modelResource;
        setFrameRate(60);
    }

    public void changeModel(int modelId){
        switch(modelId){
            case R.raw.android_obj:
                getCurrentScene().removeChild(mModel);
                LoaderOBJ objLoader1 = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.android_obj);
                try {
                    objLoader1.parse();
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
                mModel = objLoader1.getParsedObject();
                getCurrentScene().addChild(mModel);
                break;

            case R.raw.greenhatedandroid_obj:
                getCurrentScene().removeChild(mModel);
                LoaderOBJ objLoader2 = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.greenhatedandroid_obj);
                try {
                    objLoader2.parse();
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
                mModel = objLoader2.getParsedObject();
                getCurrentScene().addChild(mModel);
                break;

            case R.raw.bluehatedandroid_obj:
                getCurrentScene().removeChild(mModel);
                LoaderOBJ objLoader3 = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.bluehatedandroid_obj);
                try {
                    objLoader3.parse();
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
                mModel = objLoader3.getParsedObject();
                getCurrentScene().addChild(mModel);
                break;

            case R.raw.redhatedandroid_obj:
                getCurrentScene().removeChild(mModel);
                LoaderOBJ objLoader4 = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.redhatedandroid_obj);
                try {
                    objLoader4.parse();
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
                mModel = objLoader4.getParsedObject();
                getCurrentScene().addChild(mModel);
                break;

            case R.raw.purplehatedandroid_obj:
                getCurrentScene().removeChild(mModel);
                LoaderOBJ objLoader5 = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.purplehatedandroid_obj);
                try {
                    objLoader5.parse();
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
                mModel = objLoader5.getParsedObject();
                getCurrentScene().addChild(mModel);
                break;

            case R.raw.goldenandroid_obj:
                getCurrentScene().removeChild(mModel);
                LoaderOBJ objLoader6 = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.goldenandroid_obj);
                try {
                    objLoader6.parse();
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
                mModel = objLoader6.getParsedObject();
                getCurrentScene().addChild(mModel);
                break;

            default: break;

        }
    }

    @Override
    protected void initScene() {
        mDirectionalLight = new DirectionalLight(1.0f, .2f, -1.0f);
        mDirectionalLight.setColor(1.0f, 1.0f, 1.0f);
        mDirectionalLight.setPower(2);
        getCurrentScene().addLight(mDirectionalLight);

        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(), mTextureManager, this.modelResource);
        /*
        Modèles dispo :
        - android_obj            = android vert simple
        - androidgolden_obj      = android en or
        - bluehatedandroid_obj   = android avec un chapeau bleu
        - greenhatedandroid_obj  = android avec un chapeau vert
        - redhatedandroid_obj    = android avec un chapeau rouge
        - purplehatedandroid_obj = android avec un chapeau violet

        */

        surfaceView.setOnTouchListener(new Listener());

        try {
            objParser.parse();
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        mModel = objParser.getParsedObject();
        //mModel.setMaterial(material);

        getCurrentScene().addChild(mModel);
        getCurrentScene().setBackgroundColor(255,255,255,1);
        ArcballCamera camera = new ArcballCamera(mContext, ((Activity)mContext).findViewById(R.id.surfaceModel3D));
        camera.setTarget(mModel);
        camera.setPosition(0,2,10);
        getCurrentScene().replaceAndSwitchCamera(getCurrentCamera(),camera);


    }


    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        //mModel.rotate(Vector3.Axis.Y, 1.0);
        //mModel.rotate(Vector3.Axis.X,  1.0);
        //mModel.rotate(Vector3.Axis.Z,  1.0);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
    }

    @Override
    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j) {
    }

    private class Listener implements View.OnTouchListener{

        private float pressedPosX, pressedPosY;
        public Listener(){
            pressedPosX = 0;
            pressedPosY = 0;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //Ici, il faut comprendre comment l'utilisateur veut déplacer le personnage
            //Il faut donc récupérer la position au moment du touch, et également celle
            //au moment du release.

            //Quand la "souris" monte, il faut faire un rotate positif sur l'axe X
            //----- -- -------- gauche,-- ---- ----- -- ------ ------- --- ----- Y

            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                pressedPosX = motionEvent.getX();
                pressedPosY = motionEvent.getY();
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                float diffOldCurrentX = pressedPosX - motionEvent.getX();
                float diffOldCurrentY = pressedPosY - motionEvent.getY();
                float angle = 3.0f;

                    if(diffOldCurrentX > 0){
                        //Log.i("onTouchListener", "Vers la gauche");
                        mModel.rotate(Vector3.Axis.Y, angle);
                    }
                    else if(diffOldCurrentX < 0){
                        //Log.i("onTouchListener", "Vers le droite");
                        mModel.rotate(Vector3.Axis.Y, -angle);
                    }

                    if(diffOldCurrentY > 0){
                        //Log.i("onTouchListener", "Vers le haut");
                        mModel.rotate(Vector3.Axis.X, angle);
                    }
                    else if(diffOldCurrentY < 0){
                        //Log.i("onTouchListener", "Vers le bas");
                        mModel.rotate(Vector3.Axis.X, -angle);
                    }
            }

            return true;
        }
    }
}
