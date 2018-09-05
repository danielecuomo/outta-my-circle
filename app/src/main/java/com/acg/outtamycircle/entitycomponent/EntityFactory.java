package com.acg.outtamycircle.entitycomponent;

import android.graphics.Color;
import android.graphics.Shader;

import com.acg.outtamycircle.Assets;
import com.acg.outtamycircle.entitycomponent.impl.GameCharacter;
import com.acg.outtamycircle.entitycomponent.impl.Powerup;
import com.acg.outtamycircle.entitycomponent.impl.StaticCircle;
import com.acg.outtamycircle.utilities.Converter;
import com.badlogic.androidgames.framework.Effect;
import com.badlogic.androidgames.framework.Graphics;
import com.acg.outtamycircle.entitycomponent.impl.Arena;
import com.acg.outtamycircle.entitycomponent.impl.CircleDrawableComponent;
import com.acg.outtamycircle.entitycomponent.impl.DynamicCircle;
import com.badlogic.androidgames.framework.impl.AndroidEffect;
import com.badlogic.androidgames.framework.impl.ComposerAndroidEffect;
import com.badlogic.androidgames.framework.impl.RadialGradientEffect;
import com.google.fpl.liquidfun.World;

import java.util.LinkedList;
import java.util.List;

public class EntityFactory{
    private static World world;
    private static Graphics graphics;

    private EntityFactory(){}

    public static void setWorld(World world){ EntityFactory.world = world; }

    public static void setGraphics(Graphics graphics){ EntityFactory.graphics = graphics; }

    public static Arena createArena(int radius, int x, int y){
        Arena arena = new Arena();

        CircleDrawableComponent circleDrawableComponent = new CircleDrawableComponent(graphics);
        circleDrawableComponent.setRadius(radius).setX(x).setY(y)
                .setEffect(new ComposerAndroidEffect(
                        new RadialGradientEffect(x,y,radius,
                                new int[]{Color.parseColor("#348496"), Color.parseColor("#4DC1DD")},
                                new float[]{0f,1f}, Shader.TileMode.CLAMP
                        ),
                        (AndroidEffect)Assets.arenaTile)
                );

        arena.addComponent(circleDrawableComponent);

        return arena;
    }

    /*public static GameCharacter createServerDefaultCharacter(int radius, int x, int y, int color){
        GameCharacter c = new GameCharacter();

        CircleDrawableComponent drawable = new CircleDrawableComponent(graphics);

        drawable.setRadius(radius).setColor(color).setX(x).setY(y).setStroke(6,Color.BLACK);
        c.addComponent(drawable);

        c.addComponent(new DynamicCircle(world,
                Converter.frameToPhysics(radius),
                Converter.frameToPhysics(x),
                Converter.frameToPhysics(y)));

        return c;
    }

    //TODO privo di conversione
    public static GameCharacter createClientDefaultCharacter(int radius, int x, int y, int color){
        GameCharacter c = new GameCharacter();

        CircleDrawableComponent drawable = new CircleDrawableComponent(graphics);
        drawable.setRadius(radius).setColor(color).setX(x).setY(y).setStroke(2, Color.BLACK);
        c.addComponent(drawable);

        return c;
    }

    public static Powerup createServerDefaultPowerup(int radius, int x, int y){
        Powerup pu = new Powerup();

        CircleDrawableComponent drawable = new CircleDrawableComponent(graphics);
        drawable.setRadius(radius).setColor(Color.CYAN).setX(x).setY(y).setStroke(2, Color.BLACK);
        pu.addComponent(drawable);

        pu.addComponent(new DynamicCircle(world,
                Converter.frameToPhysics(radius),
                Converter.frameToPhysics(x),
                Converter.frameToPhysics(y)));

        return pu;
    }*/
}
