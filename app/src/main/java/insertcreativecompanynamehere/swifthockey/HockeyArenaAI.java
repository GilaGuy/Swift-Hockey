package insertcreativecompanynamehere.swifthockey;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;

/**
 * Created by Filip on 2014-09-15.
 */
public class HockeyArenaAI extends HockeyArena
{
    public HockeyArenaAI(Context context) {
        super(context);
        commonConstructor();
    }
    public HockeyArenaAI(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonConstructor();
    }

    @Override
    public boolean onTouchEvent(MotionEvent m)
    {
        switch (m.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < m.getPointerCount(); ++i)
                {
                    if (m.getY(i) > screenHeight/2)
                    {
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
                for (int i = 0; i < m.getPointerCount(); ++i)
                {
                    if (m.getY(i) > screenHeight/2 + paddleHeight/2)
                    {
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

    protected void loop()
    {
        AiControl(paddleBall2);

        for (Ball b : Ball.balls) b.update();

        for (Ball b : Ball.balls) b.detectCollisions();

        detectWallCollisions(paddleBall);
        detectWallCollisions(paddleBall2);

        if (!scored) {
            detectWallCollisions(puckBall);
        }

        if (paddleBall.y < screenHeight/2 + paddleHeight/2) {
            paddleBall.y = screenHeight / 2 + paddleHeight / 2;
            paddleBall.speed_y = Math.abs(paddleBall.speed_y);
        }

        if (paddleBall2.y > screenHeight/2 - paddleHeight/2) {
            paddleBall2.y = screenHeight / 2 - paddleHeight / 2;
            paddleBall2.speed_y = -Math.abs(paddleBall2.speed_y);
        }

        if (goalCountBot >= SCORE_TO_WIN || goalCountTop >= SCORE_TO_WIN) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    for (Ball b : Ball.balls) clearVelocity(b);

                    puckBall.x = screenWidth/2;
                    puckBall.y = screenHeight/2;

                    paddleBall2.x = screenWidth/2;
                    paddleBall2.y = screenHeight * 1/3;
                    paddleBall.x = screenWidth/2;
                    paddleBall.y =  screenHeight * 2/3;

                    goalCountBot = 0;
                    goalCountTop = 0;
                }
            }, TIME_OUT);
        }
    }

    protected void AiControl(Ball controlledBall)
    {
        if (!scored && puckBall.y < screenHeight/2 )
        {
            // controlledBall.x = puckBall.x; // impossible mode

            if (getDistanceX(controlledBall, puckBall) > 0 + paddleHeight / 2) {
                controlledBall.speed_x = getDistanceX(puckBall, controlledBall) / AI_DIFFICULTY ;
            } else if (getDistanceX(controlledBall, puckBall) < 0 - paddleHeight / 2) {
                controlledBall.speed_x = getDistanceX(puckBall, controlledBall) / AI_DIFFICULTY ;
            }

            if (controlledBall.speed_x > Ball.MAX_SPEED.x)
                controlledBall.speed_x = Ball.MAX_SPEED.x;
            else if (controlledBall.speed_x < -Ball.MAX_SPEED.x)
                controlledBall.speed_x = -Ball.MAX_SPEED.x;

            if (getDistanceY(controlledBall, puckBall) > 0 + paddleHeight / 2) {
                controlledBall.speed_y = getDistanceY(puckBall, controlledBall) / AI_DIFFICULTY ;
            } else if (getDistanceY(controlledBall, puckBall) < 0 - paddleHeight / 2) {
                controlledBall.speed_y = getDistanceY(puckBall, controlledBall) / AI_DIFFICULTY ;
            }

            if (controlledBall.speed_y > Ball.MAX_SPEED.y)
                controlledBall.speed_y = Ball.MAX_SPEED.y;
            else if (controlledBall.speed_y < -Ball.MAX_SPEED.y)
                controlledBall.speed_y = -Ball.MAX_SPEED.y;

            if (controlledBall.y > puckBall.y
                    ||
                    (Math.abs(getDistanceX(puckBall, controlledBall)) < controlledBall.ballRadius
                            && Math.abs(getDistanceY(puckBall, controlledBall)) < controlledBall.ballRadius)) {
                controlledBall.speed_y *= 1.03;
            }

            if ((puckBall.x <= screenWidth / 4 || puckBall.x >= screenWidth * 3/4)
                    && Math.abs(getDistanceY(puckBall, controlledBall)) < controlledBall.ballRadius * 2)
                controlledBall.y *= Ball.FRICTION_FACTOR;

            controlledBall.detectCollisions();
        }
        else
        {
            if (controlledBall.y > controlledBall.ballRadius)
                controlledBall.y *= Ball.FRICTION_FACTOR;

            if (controlledBall.x > screenWidth/2 + controlledBall.ballRadius) {
                controlledBall.speed_x += -Ball.FRICTION_FACTOR;
            } else if (controlledBall.x < screenWidth/2 - controlledBall.ballRadius) {
                controlledBall.speed_x +=  Ball.FRICTION_FACTOR;
            }
        }
    }
}
