package com.acg.outtamycircle;

import android.util.Log;

import com.acg.outtamycircle.entitycomponent.Component;
import com.acg.outtamycircle.entitycomponent.DrawableComponent;
import com.acg.outtamycircle.entitycomponent.impl.gameobjects.GameCharacter;
import com.acg.outtamycircle.network.GameMessage;
import com.acg.outtamycircle.network.googleimpl.MyGoogleRoom;
import com.badlogic.androidgames.framework.impl.AndroidGame;

import java.util.Iterator;

public class ClientScreen extends ClientServerScreen {
    private boolean playCollision;
    private int toleranceCollision = 4;

    public ClientScreen(AndroidGame game, MyGoogleRoom myGoogleRoom, String[] players, int[] skins, int[][] spawnPositions, int playerOffset) {
        super(game, myGoogleRoom, players, skins, spawnPositions, playerOffset);

        startRound();
        roundNum--;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(endGame)
            return;

        if(endRound) {
            startRound();
            return;
        }

        if(isAlive)
            send();

        receive();

        if(playCollision) {
            playCollision = false;
            if(Settings.soundEnabled)
                Assets.gameCharacterCollision.play(Settings.volume);
        }
    }

    private void updatePlayCollision(int objectId, int posX, int posY) {
        if(true)
            return;
        for(GameCharacter gameCharacter: status.living) {
            if (gameCharacter.getObjectId() != objectId) {
                DrawableComponent tmp = (DrawableComponent) gameCharacter.getComponent(Component.Type.Drawable);
                int deltaX = (posX - tmp.getX()) * (posX - tmp.getX());
                int deltaY = (posY - tmp.getY()) * (posY - tmp.getY());
                if(Math.sqrt(deltaX + deltaY) <= (RADIUS_CHARACTER*2 + toleranceCollision)) {
                    playCollision = true;
                    break;
                }
            }
        }
        status.living.resetIterator();
    }

    private void send() {
        GameMessage message = GameMessage.createInstance();
        interpreter.makeMoveClientMessage(message, playerOffset, (int) androidJoystick.getNormX(), (int) androidJoystick.getNormY());
        networkMessageHandler.putInBuffer(message);
        if(shouldAttack) {
            if(Settings.soundEnabled)
                Assets.attackEnabled.play(Settings.volume);
            shouldAttack = false;
            interpreter.makeAttackMessage(message, playerOffset, (int)androidJoystick.getNormX(),(int) androidJoystick.getNormY());
            networkMessageHandler.putInBuffer(message);
        }
        networkMessageHandler.sendUnreliable(myGoogleRoom.getServerId());
        GameMessage.deleteInstance(message);
    }

    private void receive() {
        playCollision = false;
        for (GameMessage message : networkMessageHandler.getMessages()) {
            switch (interpreter.getType(message)){
                case MOVE_SERVER: {
                    int objectId = interpreter.getObjectId(message);
                    int posX = interpreter.getPosX(message);
                    int posY = interpreter.getPosY(message);
                    float rotation = interpreter.getRotation(message);
                    DrawableComponent comp = (DrawableComponent) status.characters[objectId].getComponent(Component.Type.Drawable);
                    if(comp.getX() != posX || comp.getY() != posY) {
                        comp.setX(posX).setY(posY);
                        if(!playCollision)
                            updatePlayCollision(objectId, posX, posY);
                    }
                }
                break;
                case ATTACK: {
                    int objectId = interpreter.getObjectId(message);
                    if(objectId != playerOffset && Settings.soundEnabled)
                        Assets.attackEnabled.play(Settings.volume);
                }
                break;
                case DESTROY: {
                    int objectId = interpreter.getObjectId(message);
                    if(objectId == playerOffset)
                        isAlive = false;
                    Iterator<GameCharacter> iterator = status.living.iterator();
                    while(iterator.hasNext()) {
                        GameCharacter curr = iterator.next();
                        if(curr.getObjectId() == objectId) {
                            iterator.remove();
                            status.dying.add(curr);
                        }
                    }
                }
                break;
                case POWERUP: {
                    int powerupId = interpreter.getPowerupId(message);
                    int x = interpreter.getPosX(message);
                    int y = interpreter.getPosY(message);
                    int powerupType = interpreter.getPowerupType(message);
                    Log.d("POWERUP", "client powerup " + x + ", " + y + ", " + powerupType + ", " + powerupId);
                    if(Settings.soundEnabled)
                        Assets.newPowerup.play(Settings.volume);
                    status.setPowerup(createPowerup(x, y, powerupType, powerupId));
                }
                break;
                case POWERUP_ASSIGN: {
                    int objectId = interpreter.getObjectId(message);
                    int powerupId = interpreter.getPowerupId(message);
                    int powerupType = interpreter.getPowerupType(message);
                    if(Settings.soundEnabled && objectId == playerOffset)
                        Assets.powerupCollision.play(Settings.volume);
                    if(status.powerup != null && status.powerup.getObjectId() == powerupId)
                        status.setPowerup(null);
                }
                break;
                case END: {
                    startAt = System.currentTimeMillis()+5000;
                    winnerId[roundNum-1] = interpreter.getObjectId(message);
                    endRound = true;
                }
                break;
                case COLLISION: {
                    if(Settings.soundEnabled)
                        Assets.gameCharacterCollision.play(Settings.volume);
                }
                break;
            }
        }
    }

    @Override
    protected void startRound(){
        if (startAt > System.currentTimeMillis())
            return;
        super.startRound();
    }

}
