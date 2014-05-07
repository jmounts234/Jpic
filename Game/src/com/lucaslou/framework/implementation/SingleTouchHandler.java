package com.lucaslou.framework.implementation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import com.lucaslou.framework.Image;
import com.lucaslou.framework.Pool;
import com.lucaslou.framework.Graphics.ImageFormat;
import com.lucaslou.framework.Input.TouchEvent;
import com.lucaslou.framework.Pool.PoolObjectFactory;
import com.lucaslou.game.Assets;
import com.lucaslou.game.GameScreen;
import com.lucaslou.game.LoadingScreen;

public class SingleTouchHandler implements TouchHandler {
    boolean isTouched;
    int touchX;
    int touchY;
    Pool<TouchEvent> touchEventPool;
    List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
    float scaleX;
    float scaleY;
    
    Socket socket;
    PrintWriter out;
    Scanner in;
    GameScreen gs;

    
    public SingleTouchHandler(View view, float scaleX, float scaleY) {
        PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }            
        };
        touchEventPool = new Pool<TouchEvent>(factory, 100);
        view.setOnTouchListener(this);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
    	
        //System.out.println("yeah");
    	synchronized(this) {
            TouchEvent touchEvent = touchEventPool.newObject();
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchEvent.type = TouchEvent.TOUCH_DOWN;
                isTouched = true;
                break;
            case MotionEvent.ACTION_MOVE:
                touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                isTouched = true;
                break;
            case MotionEvent.ACTION_CANCEL:                
            case MotionEvent.ACTION_UP:
                touchEvent.type = TouchEvent.TOUCH_UP;
                isTouched = false;
                break;
            }
            
            touchEvent.x = touchX = (int)(event.getX() * scaleX);
            touchEvent.y = touchY = (int)(event.getY() * scaleY);
            touchEventsBuffer.add(touchEvent);                        
            //System.out.printf("Touched: %d , %d\n",touchEvent.x,touchEvent.y);
            if(inBounds(touchEvent, 200, Assets.screensizey-140, Assets.exitb.getWidth(), Assets.exitb.getHeight())){
            	//finish();
            	System.exit(0);;
            }
            if(inBounds(touchEvent, 30, Assets.screensizey-140, Assets.buttons.getWidth(), Assets.buttons.getHeight())){
        		Assets.count = 0;
            	//Assets.buttons = g.newImage("play_again.png", ImageFormat.RGB565);
            	Random r = new Random();
        		int randomImageNum = r.nextInt(14);
        		if (randomImageNum < 1 || randomImageNum > 13) {
        			randomImageNum = 8;
        			
        		}
        		String img = randomImageNum + ".png";
        		//Assets.jpic = g.newImage(img, ImageFormat.RGB565);
        		//Assets.yellow = g.newImage("yellow.png", ImageFormat.RGB565);
        		Assets.jpic =  new AndroidImage( Bitmap.createScaledBitmap(Assets.jpic.getBitmap(), Assets.screensize, Assets.screensize, false),ImageFormat.RGB565);
        		//Assets.rows = 3;			//debug
        		//Assets.yellow =  new AndroidImage( Bitmap.createScaledBitmap(Assets.yellow.getBitmap(), Assets.screensize/Assets.rows, Assets.screensize/Assets.rows, false),ImageFormat.RGB565);
        		//Assets.tiles = Assets.rows * Assets.rows;
        		//Bitmap bitmap = null;
        		//Assets.tWidth = Assets.jpic.getWidth() / Assets.rows; // determines the chunk width and height  
        		//Assets.tHeight = Assets.jpic.getHeight() / Assets.rows;
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
            	Assets.isSolved = false;
            }
            if (!Assets.isSolved) {
            	for(int x=0;x<Assets.rows;x++){
            		for(int y=0;y<Assets.rows;y++){
            			if(inBounds(touchEvent, x*Assets.tWidth+40, y*Assets.tHeight, Assets.tWidth, Assets.tHeight)){
            				int index = (x*Assets.rows) + y;
            				
            				if(index == Assets.emptyi+1 || index == Assets.emptyi-1 || 	
            					index == Assets.emptyi+Assets.rows || index == Assets.emptyi-Assets.rows)
            				{ // check if adjacent to empty tile
            					//System.out.printf("  %d  %d\n", index%Assets.rows,Assets.emptyi%Assets.rows);
            					if(((index % Assets.rows == 0) && (Assets.emptyi % Assets.rows == Assets.rows-1)) || 
            						((index % Assets.rows == Assets.rows-1) && (Assets.emptyi % Assets.rows == 0))){
            						continue;
            					}
            					Image tmp = new AndroidImage(Assets.scram[index].getBitmap(), ImageFormat.RGB565);
            					//Image tmp = Assets.scram[index];
            					Assets.scram[index] = new AndroidImage(Assets.scram[Assets.emptyi].getBitmap(), ImageFormat.RGB565);
            					//Assets.scram[index] = Assets.scram[Assets.emptyi];
            					Assets.scram[Assets.emptyi] = new AndroidImage(tmp.getBitmap(), ImageFormat.RGB565);
            					//Assets.scram[Assets.emptyi] = tmp;
            					Assets.emptyi = index;
            					Assets.count++;
            					//System.out.printf("count: %d\n", Assets.count );
            					String difficulty = String.valueOf(Assets.rows);
            					//getHS();
            					int win = 0;
            					for(int i=0;i<Assets.tiles;i++){
            		            	if(Assets.images[i].getBitmap().sameAs(Assets.scram[i].getBitmap())) {
            		            		win++;
            		            	} else {
            		            		break;
            		            	}
            		            }
            					if (win == Assets.tiles) {
            						//gs.callGameOver();
            						String name = "Guest"+Assets.count;
            						updateHS(Integer.toString(Assets.rows),name,String.valueOf(Assets.count),"0");
            						getHS(Integer.toString(Assets.rows), "moves");
            						Assets.isSolved = true;
            					}
            				}
            			}
            		}
            	}
            	
            }
            
            
            
            
            return true;
        }
    }
    
    private void getHS(String difficulty, final String type) {

    	
    	
    	/*if(type.equals("time")){
    		final String request = "GET-HS-TIME|root|jpic|"+difficulty;
    	}
    	else if(type.equals("moves")){*/
		SystemClock.sleep(500);
    		final String request = "GET-HS-MOVES|root|jpic|"+difficulty;
    	//}
    	Thread contact = new Thread(){
    		@Override
    		public void run(){
    			
    			String hsList = null;
    			Assets.nameList = new ArrayList<String>();
    			Assets.moveList = new ArrayList<String>();
    			Assets.timeList = new ArrayList<String>();
    			
    			try {
    				socket = new Socket("sslab11.cs.purdue.edu", 9999);
    				
    				out = new PrintWriter(socket.getOutputStream(),true);
    				out.println(request);
    				
    				in = new Scanner(socket.getInputStream());
    				
    				while(in.hasNextLine()){	
    					hsList = (String) in.nextLine();
    					String[] output = hsList.split("\\|");
    					Assets.nameList.add(output[0]);
    					Assets.timeList.add(output[2]);
    					Assets.moveList.add(output[1]);
    					
    					
    				}
    				
    				//DATA SHOULD NOW BE IN GLOBAL ARRAYLISTS TO BE PRINTED

    			} 
    			catch (UnknownHostException e) {
    				e.printStackTrace();
    			} 
    			catch (IOException e) {
    				e.printStackTrace();
    			}
    			//whatevernameHandlerusedinthebeginning.sendEmptyMessage(0);
    			//initialHandler.sendEmptyMessage(0);
    			//refreshHandler.sendEmptyMessage(0);
    			//finishedHandler.sendEmptyMessage(0);
    		}
    	};
    	contact.start();
    }
    
    private void updateHS(String difficulty, String name, String moves, String time) {

    	final String request = "UPDATE-HS|root|jpic|"+difficulty+"|"+name+"|"+moves+"|"+time;
    	Thread contact = new Thread(){
    		@Override
    		public void run(){
    			try {
    				socket = new Socket("sslab11.cs.purdue.edu", 9999);
    				
    				out = new PrintWriter(socket.getOutputStream(),true);
    				out.println(request);
    			} 
    			catch (UnknownHostException e) {
    				e.printStackTrace();
    			} 
    			catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	};
    	contact.start();
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
    public boolean isTouchDown(int pointer) {
        synchronized(this) {
            if(pointer == 0)
                return isTouched;
            else
                return false;
        }
    }

    @Override
    public int getTouchX(int pointer) {
        synchronized(this) {
        	Assets.tx = touchX;
            return touchX;
        }
    }

    @Override
    public int getTouchY(int pointer) {
        synchronized(this) {
        	Assets.ty = touchY;
            return touchY;
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        synchronized(this) {     
            int len = touchEvents.size();
            for( int i = 0; i < len; i++ )
                touchEventPool.free(touchEvents.get(i));
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }
}