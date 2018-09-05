package com.acg.outtamycircle;

import android.graphics.Color;
import android.util.Log;

import com.acg.outtamycircle.network.googleimpl.GoogleRoom;
import com.acg.outtamycircle.network.googleimpl.MyGoogleSignIn;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.AndroidButton;
import com.badlogic.androidgames.framework.impl.AndroidCircularButton;
import com.badlogic.androidgames.framework.impl.AndroidGame;
import com.badlogic.androidgames.framework.impl.AndroidRectangularButton;
import com.badlogic.androidgames.framework.impl.AndroidScreen;

public class CustomizeGameCharacterScreen extends AndroidScreen {
    private int currentIdSkin = 0;
    private int currentIdAttack = 0;

    private final AndroidButton leftSkin = new AndroidRectangularButton(androidGame.getGraphics(),490-74,200,74,80);
    private final AndroidButton leftAttack = new AndroidRectangularButton(androidGame.getGraphics(),490-74,400,74,80);
    private final AndroidButton rightSkin = new AndroidRectangularButton(androidGame.getGraphics(),790,200,74,80);
    private final AndroidButton rightAttack = new AndroidRectangularButton(androidGame.getGraphics(),790,400,74,80);
    private final AndroidButton fakeButton = new AndroidCircularButton(androidGame.getGraphics(),150,150,50);

    private final AndroidButton backButton = new AndroidRectangularButton(androidGame.getGraphics(),66,550,324,124);
    private final AndroidButton quickGameButton = new AndroidRectangularButton(androidGame.getGraphics(), 890,550,324,124);

    private boolean unchanged;


    private MyGoogleSignIn myGoogleSignIn = MyGoogleSignIn.getInstance();

    public CustomizeGameCharacterScreen(AndroidGame androidGame) {
        super(androidGame);
        leftSkin.setPixmap(Assets.leftArrow);
        rightSkin.setPixmap(Assets.rightArrow);
        leftAttack.setPixmap(Assets.leftArrow);
        rightAttack.setPixmap(Assets.rightArrow);
        fakeButton.setColor(Color.RED);
        backButton.setPixmap(Assets.back);
        quickGameButton.setPixmap(Assets.quickGame);
        myGoogleSignIn.signIn();
    }

    @Override
    public void update(float deltaTime) {
        boolean rom = false;

        boolean goBack = false;
        boolean goForward = false;
        for (Input.TouchEvent event : androidGame.getInput().getTouchEvents()) {
            if(event.type != Input.TouchEvent.TOUCH_UP)
                continue;
            if(fakeButton.inBounds(event) && fakeButton.isEnabled()) {
                rom = true;
            } else if(backButton.inBounds(event)) {
                goBack = true;
                Assets.click.play(100);
                break;
            } else if(quickGameButton.inBounds(event)) {
                goForward = true;
                Assets.click.play(100);
                break;
            } else if(rightSkin.inBounds(event)) {
                if(currentIdSkin < Assets.skins.length-1) {
                    currentIdSkin++;
                    unchanged = false;
                    Assets.click.play(100);
                }
            } else if(leftSkin.inBounds(event)) {
                if(currentIdSkin > 0) {
                    currentIdSkin--;
                    unchanged = false;
                    Assets.click.play(100);
                }
            } else if (rightAttack.inBounds(event)) {
                if(currentIdAttack < Assets.attacks.length-1) {
                    currentIdAttack++;
                    unchanged = false;
                    Assets.click.play(100);
                }
            } else if(leftAttack.inBounds(event)) {
                if(currentIdAttack > 0) {
                    currentIdAttack--;
                    unchanged = false;
                    Assets.click.play(100);
                }
            }
        }
        if(goBack) {
            androidGame.setScreen(new MainMenuScreen(androidGame));
            return;
        }
        if(goForward) {
            androidGame.setScreen(new ServerScreen(androidGame, new long[]{0, 1, 2, 3})); //TODO generalizzare
        }
        if(rom) {
            fakeButton.enable(false);
            GoogleRoom.getInstance().quickGame(2,2);
            return;
        }
    }

    @Override
    public void present(float deltaTime) {
        if(unchanged)
            return;
        final Graphics graphics = androidGame.getGraphics();
        unchanged = true;
        graphics.drawEffect(Assets.backgroundTile, 0,0, graphics.getWidth(), graphics.getHeight());
        fakeButton.draw();
        quickGameButton.draw();
        backButton.draw();
        graphics.drawText(androidGame.getString(R.string.select_player),520,150,40, android.graphics.Color.RED);
        graphics.drawText(androidGame.getString(R.string.select_attack),500,350,40, android.graphics.Color.RED);
        graphics.drawPixmap(Assets.skins[currentIdSkin], 590, 190);
        if(currentIdSkin != Assets.skins.length-1)
            rightSkin.draw();
        if(currentIdSkin != 0)
            leftSkin.draw();
        graphics.drawPixmap(Assets.attacks[currentIdAttack], 590, 390);
        if(currentIdAttack != Assets.attacks.length-1)
            rightAttack.draw();
        if(currentIdAttack != 0)
            leftAttack.draw();
    }

    @Override
    public void pause() {
        Log.d("GoogleS", "pause");
        fakeButton.enable(false);
    }

    @Override
    public void resume() {
        Log.d("GoogleS", "resume");
        fakeButton.enable(true);
    }

    @Override
    public void dispose() {

    }
}
