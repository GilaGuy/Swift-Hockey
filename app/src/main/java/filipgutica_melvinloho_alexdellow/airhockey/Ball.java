package filipgutica_melvinloho_alexdellow.airhockey;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Filip on 2014-09-16.
 * Based on code from...
 *  Modified by Melvin and Filip
 */
public class Ball {

    float frictionFactor = .98f;

    public static ArrayList<Ball> balls = new ArrayList<Ball>();


    float x;
    float y;
    float speed_x;
    float speed_y;
    public float ballRadius;
    Bitmap curBall;
    enum type {
        puck, paddle
    };

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
        if (x >= 300 - ballRadius && speed_x > 0) speed_x = Math.abs(speed_x);
        if (x <= ballRadius && speed_x < 0) speed_x = -Math.abs(speed_x);
        if (y >= 200 - ballRadius && speed_y > 0) speed_y = Math.abs(speed_y);
        if (y <= ballRadius && speed_y < 0) speed_y = -Math.abs(speed_y);

        speed_x *= frictionFactor;
        speed_y *= frictionFactor;
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
