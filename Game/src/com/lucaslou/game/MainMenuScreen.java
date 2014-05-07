package com.lucaslou.game;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.lucaslou.framework.Game;
import com.lucaslou.framework.Graphics;
import com.lucaslou.framework.Screen;
import com.lucaslou.framework.Graphics.ImageFormat;
import com.lucaslou.framework.Input.TouchEvent;
import com.lucaslou.framework.implementation.AndroidImage;


public class MainMenuScreen extends Screen {
    public MainMenuScreen(Game game) {
        super(game);
    }


    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();


        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {


                if (inBounds(event, 0, 0, 250, 250)) {
                    //START GAME
                                game.setScreen(new GameScreen(game));               
                }


            }
        }
    }


    private boolean inBounds(TouchEvent event, int x, int y, int width,
            int height) {
        if (event.x > x && event.x < x + width - 1 && event.y > y
                && event.y < y + height - 1)
            return true;
        else
            return false;
    }


    @Override
    public void paint(float deltaTime) {
    	int count = 0;
        Graphics g = game.getGraphics();
        for (int x = 0; x < Assets.rows; x++) {
			for (int y = 0; y < Assets.rows; y++) {
				g.drawImage(Assets.scram[count++], x*Assets.tWidth+40, y*Assets.tHeight);
				//Assets.scram[count++] = new AndroidImage(Bitmap.createBitmap(Assets.jpic.getBitmap(),x*Assets.tWidth,y*Assets.tHeight,Assets.tWidth, Assets.tHeight), ImageFormat.RGB565);
				//Assets.images[count++] = Bitmap.createBitmap(Assets.jpic.getBitmap(),x*Assets.tWidth,y*Assets.tHeight,Assets.tWidth, Assets.tHeight);
				//Assets.jpic.getBitmap().createBitmap(Assets.jpic.getBitmap(), x, y, width, height);
			}
		}
        String countStr = String.valueOf(Assets.count);
        //g.drawString(countStr, Assets.screensize / 3, Assets.screensizey * 2 / 3, g.getPaint());
        // paint.setColor(Color.WHITE);
        //g.drawString("count: "+Assets.count,440,Assets.jpic.getHeight()+40, paint);
        g.drawImage(Assets.buttons, 30, Assets.screensizey-140);
        g.drawImage(Assets.exitb, 200, Assets.screensizey-140);
        //g.drawImage(Assets.jpic, 0, 0);
       // g.drawImage(Assets.jpic, 50, 50);
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
        //Display "Exit Game?" Box


    }
}