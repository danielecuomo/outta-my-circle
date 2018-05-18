package com.example.mfaella.physicsapp;

import android.graphics.Color;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.JoyStick;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidJoyStick;
import com.example.mfaella.physicsapp.entitycomponent.Component;
import com.example.mfaella.physicsapp.entitycomponent.DrawableComponent;
import com.example.mfaella.physicsapp.entitycomponent.EntityFactory;
import com.example.mfaella.physicsapp.entitycomponent.impl.Arena;
import com.google.fpl.liquidfun.World;

import java.util.List;

public class JoyStickScreen extends Screen {
    private final JoyStick androidJoyStick = new AndroidJoyStick(game.getInput(),300,300,100);
    private final World world;
    private final Arena arena;
    public JoyStickScreen(Game game) {
        super(game);
        world = new World(0,0);
        EntityFactory.setGraphics(game.getGraphics());
        EntityFactory.setWorld(world);
        arena = EntityFactory.createArena(300,200,200);
    }

    @Override
    public void update(float deltaTime) {
        List<Input.TouchEvent> events;
        events = androidJoyStick.processAndRelease(game.getInput().getTouchEvents());

        for (Input.TouchEvent event : events) {
            Log.d(tag, "Ho la possibilita' di gestire " + event.x + "," + event.y);
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawTile(Assets.backgroundTile, 0,0, g.getWidth(), g.getHeight());
        DrawableComponent arenaDrawable = (DrawableComponent) arena.getComponent(Component.Type.Drawable);
        arenaDrawable.setColor(Color.BLUE);
        arenaDrawable.drawColor();
        androidJoyStick.draw(g, Color.GREEN);
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
}
