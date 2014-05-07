package com.lucaslou.game;

import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;

import com.lucaslou.framework.Game;
import com.lucaslou.framework.Graphics;
import com.lucaslou.framework.Image;
import com.lucaslou.framework.Screen;
import com.lucaslou.framework.Input.TouchEvent;

public class GameScreen extends Screen {
    enum GameState {
        Ready, Running, Paused, GameOver
    }

    GameState state = GameState.Running;

    // Variable Setup
    // You would create game objects here.

    int livesLeft = 1;
    Paint paint;

    public GameScreen(Game game) {
        super(game);

        // Initialize game objects here

        // Defining a paint object
        paint = new Paint();
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        // We have four separate update methods in this example.
        // Depending on the state of the game, we call different update methods.
        // Refer to Unit 3's code. We did a similar thing without separating the
        // update methods.

        if (state == GameState.Ready)
            updateReady(touchEvents);
        if (state == GameState.Running)
            updateRunning(touchEvents, deltaTime);
        if (state == GameState.Paused)
            updatePaused(touchEvents);
        if (state == GameState.GameOver)
            updateGameOver(touchEvents);
    }

    private void updateReady(List<TouchEvent> touchEvents) {
        
        // This example starts with a "Ready" screen.
        // When the user touches the screen, the game begins. 
        // state now becomes GameState.Running.
        // Now the updateRunning() method will be called!
        
        if (touchEvents.size() > 0)
            state = GameState.Running;
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
        
        //This is identical to the update() method from our Unit 2/3 game.
        
        
        // 1. All touch input is handled here:
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
			//System.out.printf("Touched: %d : %d\n",event.x,event.y);

            if (event.type == TouchEvent.TOUCH_DOWN) {
            	/*for(int x=0;x<Assets.rows-1;x++){
            		for(int y=0;y<Assets.rows-1;y++){
            			if(inBounds(event, x*Assets.tWidth+40, y*Assets.tHeight, Assets.tWidth, Assets.tHeight)){
            				int index = (x*Assets.rows) + y;
            				if(index == Assets.emptyi+Assets.rows || index == Assets.emptyi-Assets.rows || 	
            					index == Assets.emptyi+1 || index == Assets.emptyi-1)
            				{ // check if adjacent to empty tile
            					Image tmp = Assets.scram[index];
            					//Assets.scram[Assets.emptyi].getBitmap().eraseColor(Color.BLACK);
            					Assets.scram[index] = Assets.scram[Assets.emptyi];
            					Assets.scram[Assets.emptyi] = tmp;
            					Assets.emptyi = index;
            				}
            			}
            		}
            	}*/
                /*if (event.x < 640) {
                    // Move left.
                }

                else if (event.x > 640) {
                    // Move right.
                }*/

            }

            if (event.type == TouchEvent.TOUCH_UP) {

                if (event.x < 640) {
                    // Stop moving left.
                }

                else if (event.x > 640) {
                    // Stop moving right. }
                }
            }

            
        }
        
        // 2. Check miscellaneous events like death:
        
        if (Assets.isSolved) {
        	state = GameState.GameOver;
        } else {
        	state = GameState.Running;
        }
        
        if (livesLeft == 0) {
          //  state = GameState.GameOver;
        }
        
        
        // 3. Call individual update() methods here.
        // This is where all the game updates happen.
        // For example, robot.update();
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {

            }
        }
    }

    private void updateGameOver(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x > 300 && event.x < 980 && event.y > 100
                        && event.y < 500) {
                    nullify();
                    game.setScreen(new MainMenuScreen(game));
                    return;
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
        Graphics g = game.getGraphics();

        // First draw the game elements.

        // Example:
        // g.drawImage(Assets.background, 0, 0);
        // g.drawImage(Assets.character, characterX, characterY);

        // Secondly, draw the UI above the game elements.
        if (state == GameState.Ready)
            drawReadyUI();
        if (state == GameState.Running)
            drawRunningUI();
        if (state == GameState.Paused)
            drawPausedUI();
        if (state == GameState.GameOver)
            drawGameOverUI();

    }

    private void nullify() {

        // Set all variables to null. You will be recreating them in the
        // constructor.
        paint = null;

        // Call garbage collector to clean up memory.
        System.gc();
    }

    private void drawReadyUI() {
        Graphics g = game.getGraphics();

        g.drawARGB(155, 0, 0, 0);
        g.drawString("Tap each side of the screen to move in that direction.",
                640, 300, paint);

    }

    private void drawRunningUI() {
        Graphics g = game.getGraphics();
        paint.setColor(Color.WHITE);
        //g.drawString("count: "+Assets.count,440,Assets.jpic.getHeight()+40, paint);
        g.drawImage(Assets.buttons, 30, Assets.screensizey-140);
        g.drawImage(Assets.exitb, 200, Assets.screensizey-140);
        int count = 0;
        for (int x = 0; x < Assets.rows; x++) {
			for (int y = 0; y < Assets.rows; y++) {
				g.drawImage(Assets.scram[count++], x*Assets.tWidth+40, y*Assets.tHeight);
				//Assets.scram[count++] = new AndroidImage(Bitmap.createBitmap(Assets.jpic.getBitmap(),x*Assets.tWidth,y*Assets.tHeight,Assets.tWidth, Assets.tHeight), ImageFormat.RGB565);
				//Assets.images[count++] = Bitmap.createBitmap(Assets.jpic.getBitmap(),x*Assets.tWidth,y*Assets.tHeight,Assets.tWidth, Assets.tHeight);
				//Assets.jpic.getBitmap().createBitmap(Assets.jpic.getBitmap(), x, y, width, height);
			}
		}
        
    }

    private void drawPausedUI() {
        Graphics g = game.getGraphics();
        // Darken the entire screen so you can display the Paused screen.
        g.drawARGB(155, 0, 0, 0);

    }

    private void drawGameOverUI() {
        Graphics g = game.getGraphics();
        //g.drawRect(0, 0, 1281, 801, Color.BLACK);
        //g.drawString("GAME OVER.", 640, 300, paint);
        g.drawImage(Assets.jpic, 40, 0);
        paint.setColor(Color.WHITE);
        for (int j=0; j<3; j++) {
			SystemClock.sleep(500);
		}
        for (int i=0; i<3; i++) {
        	try{
        		//while(Assets.nameList.isEmpty() || Assets.moveList.isEmpty()){ }
        		if(!Assets.nameList.isEmpty() || !Assets.moveList.isEmpty()){
        			g.drawString(Assets.nameList.get(i)+"      "+Assets.moveList.get(i),Assets.screensize/2,Assets.jpic.getHeight()+100*(i+1), paint);
        		}
        	} catch(NullPointerException e){
        		System.out.printf("could not process high scores!");
        	}
        }
        g.drawImage(Assets.buttons, 30, Assets.screensizey-140);
        g.drawImage(Assets.exitb, 200, Assets.screensizey-140);
        
        //g.drawString(, x, y, paint);
    }

    @Override
    public void pause() {
        if (state == GameState.Running)
            state = GameState.Paused;

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        pause();
    }
    
    public void callGameOver() {
    	state = GameState.GameOver;
    }
}