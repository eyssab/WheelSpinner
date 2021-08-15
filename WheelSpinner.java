package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.Timer;
import com.badlogic.gdx.utils.ShortArray;

import static com.badlogic.gdx.math.MathUtils.*;

public class WheelSpinner extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture WheelImage;
	private Texture LineImage;
	private Array<Sprite> Line;
	private OrthographicCamera camera;
	private float spinTime = 0f;
	private float decrement = 5f;
	private boolean spin = false;
	private Array<FloatArray> vertexArr;
	private Texture slice;
	private PolygonSpriteBatch polyBatch;
	private Array<PolygonSprite> polysArray;
	private Array<String> texts;
	private BitmapFont font;
	private PolygonSprite polySprite;
	private float multiplier = 3.6f;

	//Mutable UI Variables
	private float pies;
	private FloatArray sliceSize;

	@Override
	public void create () {
		batch = new SpriteBatch();
		WheelImage = new Texture("border.png");
		LineImage = new Texture("Line.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 800);
		batch = new SpriteBatch();

		texts = new Array<String>();
		String text = "Hello Worlds";
		texts.add("hello");
		texts.add(text);
		texts.add(text);
		texts.add(text);
		font = new BitmapFont(Gdx.files.internal("default.fnt"));

		//UI TABLE # of Pies and percentage of wheel per pie
		pies = 7;
		FloatArray percentages = new FloatArray(new float[]{20,10,5,5,20,15,25});

		Line = new Array<Sprite>();
		for(int i=0;i<pies;i++) {
			spawnLine();
		}

		vertexArr = new Array<FloatArray>();

		//degrees per pie
		sliceSize = new FloatArray();

		for(int i=0;i<pies;i++) {
			sliceSize.add(percentages.get(i) * multiplier);
		}

		System.out.println(sliceSize.get(2));

		for(int i=0;i<pies;i++) {
			FloatArray vertices = new FloatArray(new float[]{vertexCalc(sliceSize.get(i)).x, vertexCalc(sliceSize.get(i)).y, 400,401.5f,vertexCalc(0).x,vertexCalc(0).y, vertexCalc(sliceSize.get(i)/2).x,vertexCalc(sliceSize.get(i)/2).y});
			vertexArr.add(vertices);
		}

		rotateR(Line, sliceSize);

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
			polysArray.get(i).setColor(0.5f,0.3f,random(0f,10),1);
		}

		float current = 0;
		for (int i = 0; i < pies; i++) {
			polysArray.get(i).rotate(current);
			current +=sliceSize.get(i);
		}
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
			polysArray.get(i).setOrigin(400, 401.5f);
		}

		polyBatch.end();

		//draw wheel background
		batch.begin();
		//batch.draw(WheelImage, 180,185,440,440);
		TextureRegion LineRegion = new TextureRegion(LineImage,800,800);

		for(int i=0;i<Line.size;i++) {
			batch.draw(LineRegion, Line.get(i).getX(), Line.get(i).getY(), Line.get(i).getOriginX(), Line.get(i).getOriginY(), Line.get(i).getWidth(), Line.get(i).getHeight(), Line.get(i).getScaleX(), Line.get(i).getScaleY(), Line.get(i).getRotation());
		}
		font.draw(batch,texts.get(0),450,450);
		batch.end();

		spinTime += Gdx.graphics.getRawDeltaTime();

		//Spin for 8 seconds
		if(Gdx.input.justTouched()) {
			spinTime -=spinTime;
			decrement = 5;
			spin = true;
		}
		if (spinTime < random(10,15) && spin) {
			decrement *= 0.995;
			rotate1(polysArray, decrement);
			rotate2(Line, decrement);
		}
	}

	private Vector2 vertexCalc(float angle){
		float y =401.5f;
		float x =400;
		x += (float) cosDeg(angle)*200;
		y += (float) sinDeg(angle)*200;
		Vector2 coords = new Vector2(x,y);
		return coords;
	}

	private void rotateR(Array<Sprite> arr, FloatArray rotations){
		float currentRot = 0f;
		for (int j = 0; j < arr.size; j++) {
			arr.get(j).rotate(currentRot + rotations.get(j));
			currentRot += rotations.get(j);
		}
	}

	private void spawnLine(){
		Sprite Lin = new Sprite();
		Lin.setPosition(400, 401.5f);
		Lin.setScale(1f, 1f);
		Lin.setOrigin(0, 1.5f);
		Lin.setSize(200,3);
		Line.add(Lin);
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
