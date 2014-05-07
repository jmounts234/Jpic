package com.lucaslou.framework;

import java.io.FileNotFoundException;
import java.io.IOException;


public abstract class Screen {
    protected final Game game;

    public Screen(Game game) {
        this.game = game;
    }

    public abstract void update(float deltaTime) throws FileNotFoundException, IOException;

    public abstract void paint(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();
    
    public abstract void backButton();
}