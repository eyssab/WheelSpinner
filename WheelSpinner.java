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
	private Texture spinImage;
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
	private BitmapFont font;
	private Array<String> text;
	public SpriteBatch spriteBatch;
	private Array<Matrix4> mxArray;
	private Array<TextureRegion> textureRegion;
	private String prev = "              ";


	//Mutable UI Variables
	private float pies;
	private FloatArray sliceSize;

	@Override
	public void create () {
		batch = new SpriteBatch();
		WheelImage = new Texture("wheel.png");
		spinImage = new Texture("spin.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 800);
		batch = new SpriteBatch();

		Color prevColor = new Color(Color.WHITE);
		//UI TABLE # of Pies and percentage of wheel per pie
		FloatArray percentages = new FloatArray(new float[]{12,8,6,4,10,6,4,9,6,12,11,12});
		pies = percentages.size;
		colorsArray = new Array<Color>();
		text = new Array<String>();

		for(int i =0;i<pies;i++) {
			Color color = new Color(random(0,0.95f),random(0,0.95f),random(0,0.95f), 1);
			colorsArray.add(color);
			text.add(prev + "Heloa");
			if(prevColor == color){
				colorsArray.get(i).equals(color);
			}
			prevColor = new Color(color);
		}
		vertexArr = new Array<FloatArray>();

		sliceSize = new FloatArray();

		for (int i = 0; i < pies; i++) {
			sliceSize.add(percentages.get(i) * multiplier);
		}

		for (int i = 0; i < pies; i++) {
			FloatArray piesVertices = new FloatArray(new float[]{vertexCalc(sliceSize.get(i)).x, vertexCalc(sliceSize.get(i)).y, 400, 400, vertexCalc(0).x, vertexCalc(0).y, vertexCalc(sliceSize.get(i) * 0.15f).x, vertexCalc(sliceSize.get(i) * 0.15f).y, vertexCalc(sliceSize.get(i) / 4).x, vertexCalc(sliceSize.get(i) / 4).y, vertexCalc(sliceSize.get(i) / 2).x, vertexCalc(sliceSize.get(i) / 2).y, vertexCalc(sliceSize.get(i) * 0.7f).x, vertexCalc(sliceSize.get(i) * 0.7f).y, vertexCalc(sliceSize.get(i) * 0.8f).x, vertexCalc(sliceSize.get(i) * 0.8f).y});
			vertexArr.add(piesVertices);
		}

		polyBatch = new PolygonSpriteBatch();

		textureRegion = new Array<TextureRegion>();
		for (int i = 0; i < pies; i++) {
			Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
			pix.setColor(colorsArray.get(i));
			pix.fill();

			slice = new Texture(pix);

			TextureRegion textureRegionn = new TextureRegion(slice);
			textureRegion.add(textureRegionn);
		}

		EarClippingTriangulator triangulator = new EarClippingTriangulator();

		polysArray = new Array<PolygonSprite>();

		for (int i = 0; i < vertexArr.size; i++) {
			ShortArray triangleIndices = triangulator.computeTriangles(vertexArr.get(i));
			PolygonRegion polyReg = new PolygonRegion(textureRegion.get(i), vertexArr.get(i).toArray(), triangleIndices.toArray());
			polySprite = new PolygonSprite(polyReg);
			polysArray.add(polySprite);
			polysArray.get(i).translate(0, -20);
		}

		polyRotate(polysArray, sliceSize);

		font = new BitmapFont(Gdx.files.internal("default.fnt"));
		font.getData().setScale(1.5f);
		mxArray = new Array<Matrix4>();

		float current = 0f;
		FloatArray textRotation = new FloatArray();
		for(int i = 1;i<pies;i++){
			textRotation.add(polysArray.get(i).getRotation()/2);
		}

		for(int i=0;i<pies;i++){
			Matrix4 mx4Font = new Matrix4();
			mx4Font.rotate(new Vector3(0, 0, 1), (sliceSize.get(i))/2+current);
			current +=sliceSize.get(i);
			System.out.println(current);
			mx4Font.trn(392, 386, 0);
			mxArray.add(mx4Font);
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
			polysArray.get(i).setOrigin(400, 400);
		}
		polyBatch.end();

		batch.begin();
		batch.draw(WheelImage, 183,190,432,432);
		batch.draw(spinImage, 370,382,60,60);
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
			wheelSpin(polysArray, decrement);
			textSpin(mxArray, decrement);
		}

		spriteBatch = new SpriteBatch();

		for(int i=0;i<pies;i++) {
			spriteBatch.setTransformMatrix(mxArray.get(i));
			spriteBatch.begin();
			font.draw(spriteBatch, text.get(i), 0, 0);
			spriteBatch.end();
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

	public void wheelSpin(Array<PolygonSprite> arr, float rotation){
		for (int i = 0; i < arr.size; i++) {
			arr.get(i).rotate(rotation);
		}
	}

	public void textSpin(Array<Matrix4> arr, float rotation){
		int current =0;
		for (int i = 0; i < arr.size; i++) {
			arr.get(i).rotate(new Vector3(0, 0, 1), rotation);
		}
	}

	private void polyRotate(Array<PolygonSprite> arr, FloatArray rotations){
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
		spinImage.dispose();
		font.dispose();
	}
}
//:)
// :)
