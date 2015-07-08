package teamturbo.swifthockey;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import java.lang.Math;

/**
 * Created by Melvin and Filip
 */
public class HockeyArenaSPAI extends HockeyArenaSP2P {
    public static float AI_DIFFICULTY = 0.01f; // 0 - 1

    public HockeyArenaSPAI(Context context) {
        super(context);
        commonConstructor();
    }

    public HockeyArenaSPAI(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonConstructor();
    }

    private void commonConstructor() {
        GameActivitySP.scoreTop.setRotation(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent m) {
        switch (m.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < m.getPointerCount(); ++i) {
                    if (m.getY(i) > screenHeight / 2) {
                        paddleBall.x = m.getX(i);
                        paddleBall.y = m.getY(i);
                        paddleBall.speed_x = 0;
                        paddleBall.speed_y = 0;

                        if (velocity == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            velocity = VelocityTracker.obtain();
                        } else {
                            // Reset the velocity tracker back to its initial state.
                            velocity.clear();
                        }

                        // Add a user's movement to the tracker.
                        velocity.addMovement(m);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < m.getPointerCount(); ++i) {
                    if (m.getY(i) > screenHeight / 2 + paddleHeight / 2) {
                        paddleBall.x = m.getX(i);
                        paddleBall.y = m.getY(i);
                        //Add movement to tracker
                        if (velocity != null) {
                            velocity.addMovement(m);
                            // When you want to determine the velocity, call
                            // computeCurrentVelocity(). Then call getXVelocity()
                            // and getYVelocity() to retrieve the velocity in pixels/10ms
                            velocity.computeCurrentVelocity(10);
                            paddleBall.speed_x = velocity.getXVelocity(i);
                            paddleBall.speed_y = velocity.getYVelocity(i);
                            paddleBall.detectCollisions();
                            detectWallCollisions(paddleBall);
                        }
                    }
                }
                break;
        }

        return true;
    }

    protected void loop() {
        AiControl(paddleBall2);
        for (Ball b : Ball.balls) b.update();
        for (Ball b : Ball.balls) b.detectCollisions();

        detectWallCollisions(paddleBall);
        detectWallCollisions(paddleBall2);
        if (!scored) {
            detectWallCollisions(puckBall);
        }

        if (paddleBall.y < screenHeight / 2 + paddleHeight / 2) {
            paddleBall.y = screenHeight / 2 + paddleHeight / 2;
            paddleBall.speed_y = Math.abs(paddleBall.speed_y);
        }
        if (paddleBall2.y > screenHeight / 2 - paddleHeight / 2) {
            paddleBall2.y = screenHeight / 2 - paddleHeight / 2;
            paddleBall2.speed_y = -Math.abs(paddleBall2.speed_y);
        }

        if (goalCountBot >= SCORE_TO_WIN || goalCountTop >= SCORE_TO_WIN) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (Ball b : Ball.balls) clearVelocity(b);

                    puckBall.x = screenWidth / 2;
                    puckBall.y = screenHeight / 2;

                    paddleBall2.x = screenWidth / 2;
                    paddleBall2.y = screenHeight * 1 / 3;
                    paddleBall.x = screenWidth / 2;
                    paddleBall.y = screenHeight * 2 / 3;

                    goalCountBot = 0;
                    goalCountTop = 0;
                }
            }, TIME_OUT);
        }
    }

    protected void AiControl(Ball controlledBall)
    {
        if (puckBall.y < screenHeight * 0.5 && !scored)
        {
            float xDistance = puckBall.x - controlledBall.x;
            float yDistance = puckBall.y - controlledBall.y;
            float magDistance = (float)Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));

            if
                    (
                    controlledBall.y < puckBall.y
                            && Math.abs(xDistance) < puckBall.radius
                    ) // move towards the ball
            {
                float xuVel = xDistance / magDistance;
                float xyVel = yDistance / magDistance;

                controlledBall.speed_x = xDistance * AI_DIFFICULTY;
                controlledBall.speed_y = yDistance * AI_DIFFICULTY;

                if (magDistance <= controlledBall.radius + puckBall.radius) {
                    controlledBall.speed_y *= 1 + Ball.FRICTION_FACTOR;

                    if ((controlledBall.x <= screenWidth / 4 || controlledBall.x >= screenWidth * 3/4)) {
                        controlledBall.speed_y *= Ball.FRICTION_FACTOR;
                    }
                }

                controlledBall.angleInitialized = false;
            }
            else // get around the ball
            {
                if (controlledBall.angleInitialized)
                {
                    switch (controlledBall.rotationDirection)
                    {
                        case NONE:
                            if (puckBall.x > screenWidth * 0.5) {
                                controlledBall.rotationDirection = Ball.rotation.CW;
                                Log.d(">>>AI", "rot dir: CW");
                            } else {
                                controlledBall.rotationDirection = Ball.rotation.CCW;
                                Log.d(">>>AI", "rot dir: CCW");
                            }

                        case CW:
                            controlledBall.angle += Ball.MAX_ROTATION_SPEED * AI_DIFFICULTY;
                            break;

                        case CCW:
                            controlledBall.angle -= Ball.MAX_ROTATION_SPEED * AI_DIFFICULTY;
                            break;
                    }
                }
                else
                {
                    double angleRad = 0.5*Math.PI - ( Math.atan2( yDistance, xDistance ) );
                    angleRad = (angleRad + 2*Math.PI) % 2*Math.PI;

                    controlledBall.angle = (float)angleRad;
                    controlledBall.angleInitialized = true;
                    controlledBall.rotationDirection = Ball.rotation.NONE;
                    Log.d(">>>AI", "angle init to: " + controlledBall.angle);
                }

                float newX = puckBall.x + magDistance * (float)Math.cos(controlledBall.angle);
                float newY = puckBall.y + magDistance * (float)Math.sin(controlledBall.angle);

                controlledBall.speed_x = newX - controlledBall.x;
                controlledBall.speed_y = newY - controlledBall.y;

                //Log.d(">>>AI", "angle: " + controlledBall.angle);
            }

            controlledBall.detectCollisions();
        }
        else // move back to your goal
        {
            if (controlledBall.y > controlledBall.radius) {
                controlledBall.y *= 1 - AI_DIFFICULTY;
            }

            if (controlledBall.x > screenWidth / 2 + controlledBall.radius) {
                controlledBall.speed_x += -Ball.FRICTION_FACTOR * AI_DIFFICULTY;
            } else if (controlledBall.x < screenWidth / 2 - controlledBall.radius) {
                controlledBall.speed_x += Ball.FRICTION_FACTOR * AI_DIFFICULTY;
            }
        }
    }
}
