package com.mygdx.game;

import box2dLight.Light;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.VertexArray;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.Timer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ShortArray;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class WheelSpinner extends ApplicationAdapter {
  private SpriteBatch batch;
  private Texture LineImage;
  private TextureRegion radi;
  private Array<Sprite> Line;
  private OrthographicCamera camera;
  private float spinTime = 0f;
  private float decrement = 5f;
  private boolean spin = false;
  private ShapeRenderer triAngle;
  private FloatArray vertices;
  private Array<FloatArray> vertexArr;
  private Texture slice;
  private PolygonSprite polySprite;
  private PolygonSpriteBatch polyBatch;

  @Override
  public void create () {
     batch = new SpriteBatch();
     LineImage = new Texture("Line.png");

     camera = new OrthographicCamera();
     camera.setToOrtho(false, 800, 800);
     batch = new SpriteBatch();

     Line = new Array<Sprite>();
     spawnLine();
     spawnLine();
     spawnLine();
     spawnLine();
     spawnLine();
     spawnLine();
     spawnLine();

     rotateR(Line);

     triAngle = new ShapeRenderer();

     //Adding all sets of vertices
     vertexArr = new Array<FloatArray>(20);
     for(int i = 0;i<6;i++) {
        vertices = new FloatArray(new float[]{vertexCalc(Line.get(0).getRotation()).x,vertexCalc(Line.get(0).getRotation()).y, 400, 406, vertexCalc(Line.get(1).getRotation()).x,vertexCalc(Line.get(1).getRotation()).y});
        vertexArr.add(vertices);
     }

     //STOLEN CODE from https://www.youtube.com/watch?v=UL_XQLu6sPQ (THIS CODE ONLY FILLS AND COLORS THE POLYGONS
     polyBatch = new PolygonSpriteBatch();

     Pixmap pix = new Pixmap(1,1,Pixmap.Format.RGBA8888);
     pix.setColor(1,0,0,1);
     pix.fill();

     slice = new Texture(pix);

     TextureRegion textureRegion = new TextureRegion(slice);

     EarClippingTriangulator triangulator = new EarClippingTriangulator();
     ShortArray triangleIndices = triangulator.computeTriangles(vertices);

     PolygonRegion polyReg = new PolygonRegion(textureRegion, vertices.toArray(),triangleIndices.toArray());

     polySprite = new PolygonSprite(polyReg);
  }

  @Override
  public void render () {
     ScreenUtils.clear(0, 0, 1, 1);
     camera.update();

     batch.setProjectionMatrix(camera.combined);

     Timer timer = new Timer();

     batch.begin();

     TextureRegion LineRegion = new TextureRegion(LineImage,800,800);

     for(int i=0;i<Line.size;i++) {
        batch.draw(LineRegion, Line.get(i).getX(), Line.get(i).getY(), Line.get(i).getOriginX(), Line.get(i).getOriginY(), Line.get(i).getWidth(), Line.get(i).getHeight(), Line.get(i).getScaleX(), Line.get(i).getScaleY(), Line.get(i).getRotation());
     }

     batch.end();

     spinTime += Gdx.graphics.getRawDeltaTime();

     if(Gdx.input.justTouched()) {
        spinTime -=spinTime;
        decrement = 5;
        spin = true;
     }

     if (spinTime < 8 && spin) {
        decrement *= 0.995;
        rotate2(Line, decrement);
     }

     polyBatch.begin();
     polySprite.draw(polyBatch);
     polySprite.setOrigin(400,406);
     //polySprite.rotate(2f);
     polyBatch.end();
  }

  private Vector2 vertexCalc(float angle){
     float y =0;
     float x =0;
     x = (float) (cos(angle)*200);
     y = (float) (sin(angle)*200);
     Vector2 coords = new Vector2(x+400,y+400);
     return coords;
  }

  private void spawnLine(){
     Sprite Lin = new Sprite();
     Lin.setPosition(400, 400);
     Lin.setScale(1f, 1f);
     Lin.setOrigin(0, 6);
     Lin.setSize(200,12);
     Line.add(Lin);
  }

  private void rotateR(Array<Sprite> arr){
     float angle = 360 / arr.size;
     float currentAngle = 0;
     for (int j = 1; j < arr.size; j++) {
        arr.get(j).rotate(currentAngle+angle);
        currentAngle += angle;
     }
  }

  public void rotate2(Array<Sprite> arr, float rotation){
     for (int i = 0; i < Line.size; i++) {
        Line.get(i).rotate(rotation);

     }
  }

  @Override
  public void dispose() {
     batch.dispose();
     LineImage.dispose();
     slice.dispose();
  }
}
//:)
// :)

