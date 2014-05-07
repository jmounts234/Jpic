package com.lucaslou.game;

import java.io.File;
import java.awt.*;
//import android.graphics.Bitmap;
import java.io.*;
//import java.awt.*;
import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Color;

import com.lucaslou.framework.Game;
import com.lucaslou.framework.Graphics;
import com.lucaslou.framework.Graphics.ImageFormat;
import com.lucaslou.framework.Screen;
import com.lucaslou.framework.Image;
import com.lucaslou.framework.implementation.AndroidGraphics;
import com.lucaslou.framework.implementation.AndroidImage;

public class LoadingScreen extends Screen {
	public LoadingScreen(Game game) {
		
		super(game);
	}

	@Override
	public void update(float deltaTime) throws FileNotFoundException, IOException {
		Graphics g = game.getGraphics();
		Random r = new Random();
		int randomImageNum = r.nextInt(14);
		if (randomImageNum < 1 || randomImageNum > 13) {
			randomImageNum = 8;
			
		}
		Assets.buttons = g.newImage("play_again.png", ImageFormat.RGB565);
		Assets.exitb = g.newImage("exit.png", ImageFormat.RGB565);
		String img = randomImageNum + ".png";
		Assets.jpic =  g.newImage(img, ImageFormat.RGB565);
		Assets.yellow = g.newImage("yellow.png", ImageFormat.RGB565);
		Assets.jpic =  new AndroidImage( Bitmap.createScaledBitmap(Assets.jpic.getBitmap(), Assets.screensize, Assets.screensize, false),ImageFormat.RGB565);
		Assets.rows = 3;			//debug
		Assets.yellow =  new AndroidImage( Bitmap.createScaledBitmap(Assets.yellow.getBitmap(), Assets.screensize/Assets.rows, Assets.screensize/Assets.rows, false),ImageFormat.RGB565);
		Assets.tiles = Assets.rows * Assets.rows;
		Bitmap bitmap = null;
		Assets.tWidth = Assets.jpic.getWidth() / Assets.rows; // determines the chunk width and height  
		Assets.tHeight = Assets.jpic.getHeight() / Assets.rows;
		int count = 0;
		Assets.images = new Image[Assets.tiles]; //Image array to hold image chunks
		for (int x = 0; x < Assets.rows; x++) {
			for (int y = 0; y < Assets.rows; y++) {
				if (x == 0 && y == 0) {
					
					//bitmap = Bitmap.createBitmap(Assets.jpic.getBitmap(),x*Assets.tWidth,y*Assets.tHeight,Assets.tWidth, Assets.tHeight);
					//bitmap.eraseColor(Color.YELLOW);
					//Assets.images[count++] = new AndroidImage(bitmap, ImageFormat.RGB565);
					Assets.images[count++] = Assets.yellow;
				} else {
					Assets.images[count++] = new AndroidImage(Bitmap.createBitmap(Assets.jpic.getBitmap(),x*Assets.tWidth,y*Assets.tHeight,Assets.tWidth, Assets.tHeight), ImageFormat.RGB565);
				}
				
			}
		}
		
		Assets.scram = new Image[Assets.tiles];
		int i = 0;
		while(i<Assets.tiles){
			Random rand = new Random();
			int randomNum = rand.nextInt((Assets.tiles - 0) + 1) + 0;
			if(randomNum < Assets.tiles){
				if(Assets.scram[randomNum] == null){
					if (Assets.yellow.getBitmap().sameAs(Assets.images[i].getBitmap())) {
						Assets.emptyi = randomNum;
					}
					Assets.scram[randomNum] = Assets.images[i];
					i++;
					//System.out.printf("%d : %d\n",i-1,randomNum);
				}
			}
		}
		//Assets.scram = Assets.images;
		game.setScreen(new MainMenuScreen(game));

	}

	@Override
	public void paint(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawImage(Assets.jpic, 0, 0);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	
	
	
	
	
	
	
	@Override
	public void backButton() {

	}
}