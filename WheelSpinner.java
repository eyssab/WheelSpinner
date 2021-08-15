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
	private OrthographicCamera camera;
	private float spinTime = 0f;
	private float decrement = 5f;
	private boolean spin = false;
	private Array<FloatArray> vertexArr;
	private Texture slice;
	private PolygonSpriteBatch polyBatch;
	private Array<PolygonSprite> polysArray;
	private PolygonSprite polySprite;
	private float multiplier = 3.6f;
	private Array<Color> colorsArray;

	//Mutable UI Variables
	private float pies;
	private FloatArray sliceSize;
	private Array<TextureRegion> textureRegion;

	@Override
	public void create () {
		batch = new SpriteBatch();
		WheelImage = new Texture("wheel.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 800);
		batch = new SpriteBatch();

		//UI TABLE # of Pies and percentage of wheel per pie
		pies = 7;
		FloatArray percentages = new FloatArray(new float[]{20,10,5,5,20,15,25});
		colorsArray = new Array<Color>(new Color[]{Color.MAROON, Color.CHARTREUSE, Color.BROWN, Color.ORANGE, Color.CORAL, Color.CYAN, Color.GOLD});

		vertexArr = new Array<FloatArray>();

		//degrees per pie
		sliceSize = new FloatArray();

		for(int i=0;i<pies;i++) {
			sliceSize.add(percentages.get(i) * multiplier);
		}

		for(int i=0;i<pies;i++) {
			FloatArray piesVertices = new FloatArray(new float[]{vertexCalc(sliceSize.get(i)).x, vertexCalc(sliceSize.get(i)).y, 400,400,vertexCalc(0).x,vertexCalc(0).y, vertexCalc(sliceSize.get(i)/2).x,vertexCalc(sliceSize.get(i)/2).y});
			vertexArr.add(piesVertices);
		}

		polyBatch = new PolygonSpriteBatch();

		textureRegion = new Array<TextureRegion>();
		for(int i =0;i<pies;i++) {
			Pixmap pix = new Pixmap(1,1,Pixmap.Format.RGBA8888);
			pix.setColor(colorsArray.get(i));
			pix.fill();

			slice = new Texture(pix);

			TextureRegion textureRegionn = new TextureRegion(slice);
			textureRegion.add(textureRegionn);
		}

		EarClippingTriangulator triangulator = new EarClippingTriangulator();

		polysArray = new Array<PolygonSprite>();

		for(int i = 0; i<vertexArr.size;i++) {
			ShortArray triangleIndices = triangulator.computeTriangles(vertexArr.get(i));
			PolygonRegion polyReg = new PolygonRegion(textureRegion.get(i), vertexArr.get(i).toArray(), triangleIndices.toArray());
			polySprite = new PolygonSprite(polyReg);
			polysArray.add(polySprite);
			polysArray.get(i).translate(0,-20);
		}

		polyRotater(polysArray, sliceSize);
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
			polysArray.get(i).setOrigin(400, 400);
		}
		polyBatch.end();

		batch.begin();
		batch.draw(WheelImage, 180,185,440,440);
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
		}
	}

	private Vector2 vertexCalc(float angle){
		float y =400f;
		float x =400;
		x += (float) cosDeg(angle)*200;
		y += (float) sinDeg(angle)*200;
		Vector2 coords = new Vector2(x,y);
		return coords;
	}

	public void rotate1(Array<PolygonSprite> arr, float rotation){
		for (int i = 0; i < arr.size; i++) {
			arr.get(i).rotate(rotation);
		}
	}

	private void polyRotater(Array<PolygonSprite> arr, FloatArray rotations){
		float current = 0;
		for (int i = 0; i < pies; i++) {
			polysArray.get(i).rotate(current);
			current +=sliceSize.get(i);
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
