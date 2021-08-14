package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.VertexArray;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.Timer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ShortArray;

import com.badlogic.gdx.math.MathUtils;

import static com.badlogic.gdx.math.MathUtils.cosDeg;
import static com.badlogic.gdx.math.MathUtils.sinDeg;
import static java.lang.Math.abs;

public class WheelSpinner extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture LineImage;
	private Texture WheelImage;
	private Circle wheel;
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
	private Array<PolygonSprite> polysArray;
	private Vector2 preVert;
	private Vector2 nextVert;

	@Override
	public void create () {
		batch = new SpriteBatch();
		WheelImage = new Texture("border.png");
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
		spawnLine();

		rotateR(Line);

		vertexArr = new Array<FloatArray>();

		//Adding all sets of vertices
		float pies = 8;
		float degreePer = (360)/(pies);

		for(int i = 0;i<pies;i++) {
		System.out.println(degreePer);
			vertices = new FloatArray(new float[]{vertexCalc(degreePer).x, vertexCalc(degreePer).y, 400,406,vertexCalc(0).x,vertexCalc(0).y, vertexCalc(degreePer/2).x,vertexCalc(degreePer/2).y, vertexCalc(degreePer/4).x,vertexCalc(degreePer/4).y, vertexCalc(degreePer/1.5f).x,vertexCalc(degreePer/1.5f).y});
			vertexArr.add(vertices);
		}
		System.out.println(vertexCalc(Line.get(1).getRotation()).x);
		System.out.println(vertexCalc(Line.get(1).getRotation()).y);


		polyBatch = new PolygonSpriteBatch();

		Pixmap pix = new Pixmap(1,1,Pixmap.Format.RGBA8888);
		pix.setColor(1,0,0,1);
		pix.fill();

		slice = new Texture(pix);

		TextureRegion textureRegion = new TextureRegion(slice);

		EarClippingTriangulator triangulator = new EarClippingTriangulator();

		polysArray = new Array<PolygonSprite>();

		for(int i = 0; i<vertexArr.size;i++) {
			ShortArray triangleIndices = triangulator.computeTriangles(vertexArr.get(i));
			PolygonRegion polyReg = new PolygonRegion(textureRegion, vertexArr.get(i).toArray(), triangleIndices.toArray());
			polySprite = new PolygonSprite(polyReg);
			polysArray.add(polySprite);
		}
		polyrotateR(polysArray, degreePer);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 1, 1);
		camera.update();

		batch.setProjectionMatrix(camera.combined);

		Timer timer = new Timer();

		polyBatch.begin();
		for(int i=0;i<polysArray.size;i++) {
			polysArray.get(i).draw(polyBatch);
			polysArray.get(i).setOrigin(400, 406f);
		}
		polyBatch.end();

		//draw wheel background
		batch.begin();
		batch.draw(WheelImage, 180,185,440,440);
		TextureRegion LineRegion = new TextureRegion(LineImage,800,800);

		for(int i=0;i<Line.size;i++) {
			batch.draw(LineRegion, Line.get(i).getX(), Line.get(i).getY(), Line.get(i).getOriginX(), Line.get(i).getOriginY(), Line.get(i).getWidth(), Line.get(i).getHeight(), Line.get(i).getScaleX(), Line.get(i).getScaleY(), Line.get(i).getRotation());
		}

		batch.end();

		spinTime += Gdx.graphics.getRawDeltaTime();

		//Spin for 8 seconds
		if(Gdx.input.justTouched()) {
			spinTime -=spinTime;
			decrement = 5;
			spin = true;
		}
		if (spinTime < 8 && spin) {
			decrement *= 0.995;
			rotate1(polysArray,decrement);
			rotate2(Line,decrement);
		}
	}

	private Vector2 vertexCalc(float angle){
		float y =406;
		float x =400;
		x += (float) abs((cosDeg(angle))*200);
		y += (float) abs((sinDeg(angle))*200);
		Vector2 coords = new Vector2(x,y);
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

	private void polyrotateR(Array<PolygonSprite> arr, float degrees){
		for (int i = 0; i < arr.size; i++) {
			arr.get(i).rotate(degrees*i);
		}
	}

	private void rotateR(Array<Sprite> arr){
		float angle = 360 / arr.size;
		float currentAngle = 0;
		for (int j = 1; j < arr.size; j++) {
			arr.get(j).rotate(currentAngle+angle);
			currentAngle += angle;
		}
	}

	public void rotate1(Array<PolygonSprite> arr, float rotation){
		for (int i = 0; i < arr.size; i++) {
			arr.get(i).rotate(rotation);
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
		WheelImage.dispose();
		slice.dispose();
	}
}
//:)
// :)
