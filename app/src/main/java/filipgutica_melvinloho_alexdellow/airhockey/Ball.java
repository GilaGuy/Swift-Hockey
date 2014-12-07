package filipgutica_melvinloho_alexdellow.airhockey;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Filip on 2014-09-16.
 * Based on code from...
 *  Modified by Melvin and Filip
 */
public class Ball {

    public static float FRICTION_FACTOR = .98f;

    public static Point MAX_SPEED = new Point(50, 50);

    public static ArrayList<Ball> balls = new ArrayList<Ball>();


    enum type {
        puck, paddle
    }

    float x;
    float y;
    float speed_x;
    float speed_y;
    public float ballRadius;
    Bitmap curBall;
    type bType;

    public Ball( Bitmap img, float xPos, float yPos, int rad, type t)
    {
        speed_x = 0;
        speed_y = 0;
        x = xPos;
        y = yPos;
        ballRadius = rad;
        curBall = img;
        balls.add(this);
        bType = t;

    }

    public type getType() {
        return bType;
    }

    public Bitmap getBitmap() {

        return curBall;
    }

    public void update() {
        x += speed_x;
        y += speed_y;
        speed_x *= FRICTION_FACTOR;
        speed_y *= FRICTION_FACTOR;
    }

    public void detectCollisions() {
        for (Ball b : balls) {
            if (b != this) {
                if (x + ballRadius + b.ballRadius > b.x
                        && x < b.x + ballRadius + b.ballRadius
                        && y + ballRadius + b.ballRadius > b.y
                        && y < b.y + ballRadius + b.ballRadius) {
                    if (distanceTo(this, b) < ballRadius + b.ballRadius) {
                        calculateNewVelocities(this, b);
                        SoundEffects.sfx_bounce(b);
                    }
                }
            }
        }
    }

    public double distanceTo(Ball a, Ball b) {
        return Math.abs(Math.sqrt(((a.x - b.x) * (a.x - b.x)) + ((a.y - b.y) * (a.y - b.y))));
    }

    public void calculateNewVelocities(Ball firstBall, Ball secondBall) {
        float mass1 = firstBall.ballRadius;
        float mass2 = secondBall.ballRadius;
        float velX1 = firstBall.speed_x;
        float velX2 = secondBall.speed_x;
        float velY1 = firstBall.speed_y;
        float velY2 = secondBall.speed_y;

        float newVelX1 = (velX1 * (mass1 - mass2) + (2 * mass2 * velX2)) / (mass1 + mass2);
        float newVelX2 = (velX2 * (mass2 - mass1) + (2 * mass1 * velX1)) / (mass1 + mass2);
        float newVelY1 = (velY1 * (mass1 - mass2) + (2 * mass2 * velY2)) / (mass1 + mass2);
        float newVelY2 = (velY2 * (mass2 - mass1) + (2 * mass1 * velY1)) / (mass1 + mass2);

        firstBall.speed_x = newVelX1;
        secondBall.speed_x = newVelX2;
        firstBall.speed_y = newVelY1;
        secondBall.speed_y = newVelY2;

        firstBall.x = firstBall.x + newVelX1;
        firstBall.y = firstBall.y + newVelY1;
        secondBall.x = secondBall.x + newVelX2;
        secondBall.y = secondBall.y + newVelY2;


    }
}
