package teamturbo.swifthockey;

import java.io.Serializable;

/**
 * Created by Filip on 2015-06-17.
 */
public class P2PMessage implements Serializable {

    public static enum Type {
        Disconnect, PuckInfo
    }

    private Type TYPE;
    private float xPos;
    private float xVelocity;
    private float yVelocity;

    public P2PMessage(Type type, float x_pos, float xVel, float yVel) {

        TYPE = type;
        xPos = x_pos;
        xVelocity = xVel;
        yVelocity = yVel;
    }

    public float getxPos() {
        return xPos;
    }

    public float getxVelocity() {
        return xVelocity;
    }

    public float getyVelocity() {
        return yVelocity;
    }

    public Type getTYPE() {
        return TYPE;
    }

    public String toString()
    {
        return ("xPos: " + xPos + " xSpeed: " + xVelocity + " ySpeed: " + yVelocity);
    }

}