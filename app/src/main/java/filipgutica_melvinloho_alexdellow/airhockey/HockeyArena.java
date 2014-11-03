package filipgutica_melvinloho_alexdellow.airhockey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

/**
 * Created by Filip on 2014-09-15.
 */
public class HockeyArena extends View  {


    final int PADDLE = 1;
    final int PUCK = 2;

    private Paint mPaint = new Paint();         // Paint to draw set color etc...
    private Bitmap paddle;                      // First Paddle img
    private Bitmap puck;                        // Puck img
    private Bitmap paddle2;                     // Second paddle img
    private Ball paddleBall2;                   // Second paddle. Ball object to give it Collision detection and trakc speed
    private Ball paddleBall;                    // First paddle. "" ""
    private Ball puckBall;                      // Puck "" ""
    private float paddleWidth;                  // Width of a paddle
    private float paddleHeight;                 // Height of a paddle
    private float puckWidth;                    // Width of a puck
    private float puckHeight;                   // Height of a puck
    private VelocityTracker velocity = null;    // Used to track velocity of your finger as it swipes along the screen
    private VelocityTracker velocity2 = null;    // "" ""

    public HockeyArena(Context context) {
        super(context);
        commonConstructor();
    }
    public HockeyArena(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonConstructor();
    }

    public void commonConstructor() {
        cleanUp();

      // mPaint.setColor(Color.BLUE);
        paddle = BitmapFactory.decodeResource(getResources(),R.drawable.blue_paddle);
        paddle2 = BitmapFactory.decodeResource(getResources(), R.drawable.green_paddle);
        puck = BitmapFactory.decodeResource(getResources(), R.drawable.puck);

        paddleWidth = paddle.getWidth();
        paddleHeight = paddle.getHeight();
        puckWidth = puck.getWidth();
        puckHeight = puck.getHeight();

        paddleBall2 = new Ball(paddle2, 200f, 500f, (int)(paddleHeight)/2);
        paddleBall = new Ball(paddle, 400f, 700f, (int) (paddleHeight/2));
        puckBall = new Ball(puck, 500f, 800f, (int)(puckWidth/2) );
    }

    public void cleanUp() {
        paddleBall = paddleBall2 = puckBall = null;
        Ball.balls.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent m) {

        int action = m.getActionMasked();
        int index = m.getActionIndex();
        int id;

        if (index > 0) {
            Log.d("", "MULTITOUCH");
            paddleBall2.x = m.getX(index);
            paddleBall2.y = m.getY(index);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Get the number of pointers
                int pointerCount = m.getPointerCount();

                for (int i = 0; i < pointerCount; i ++) {
                    // Get the pointer ids
                    id = m.getPointerId(i);
                    // First Pointer
                    if (id == 0) {
                        paddleBall.x = m.getX(i);
                        paddleBall.y = m.getY(i);
                        paddleBall.speed_x = 0;
                        paddleBall.speed_y = 0;

                        if (velocity == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            velocity = VelocityTracker.obtain();
                            velocity2 = VelocityTracker.obtain();
                        } else {
                            // Reset the velocity tracker back to its initial state.
                            velocity.clear();
                            velocity2.clear();
                        }
                        // Add a user's movement to the tracker.
                        velocity.addMovement(m);
                        velocity2.addMovement(m);
                    }
                    // Second pointer
                    else if (id == 1) {
                        paddleBall2.x = m.getX(i);
                        paddleBall2.y = m.getY(i);
                        paddleBall2.speed_x = 0;
                        paddleBall2.speed_y = 0;

                        if (velocity2 == null) {
                            velocity = VelocityTracker.obtain();
                            velocity2 = VelocityTracker.obtain();
                        } else {
                            velocity.clear();
                            velocity2.clear();
                        }
                        velocity.addMovement(m);
                        velocity2.addMovement(m);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //Number of pointers
                int pointerCount2 = m.getPointerCount();

                for (int i = 0; i < pointerCount2; i++){
                    id = m.getPointerId(i);
                    if (id == 0) {
                        paddleBall.update();
                        paddleBall.detectCollisions();
                        paddleBall.x = m.getX(i);
                        paddleBall.y = m.getY(i);
                        //Add movement to tracker
                        velocity.addMovement(m);
                        // When you want to determine the velocity, call
                        // computeCurrentVelocity(). Then call getXVelocity()
                        // and getYVelocity() to retrieve the velocity in pixels/10ms
                        velocity.computeCurrentVelocity(10);
                        paddleBall.speed_x = velocity.getXVelocity(i);
                        paddleBall.speed_y = velocity.getYVelocity(i);
                    }
                    if (id == 1) {
                        paddleBall2.update();
                        paddleBall2.detectCollisions();
                        paddleBall2.x = m.getX(i);
                        paddleBall2.y = m.getY(i);
                        velocity2.addMovement(m);
                        velocity2.computeCurrentVelocity(10);
                        paddleBall2.speed_x = velocity2.getXVelocity(i);
                        paddleBall2.speed_y = velocity2.getYVelocity(i);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                paddleBall.speed_x = 0;
                paddleBall.speed_y = 0;
                paddleBall2.speed_x = 0;
                paddleBall2.speed_y = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                paddleBall.speed_x = 0;
                paddleBall.speed_y = 0;
                paddleBall2.speed_x = 0;
                paddleBall2.speed_y = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                //velocity.recycle();
                break;
        }

        return true;
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawARGB(255, 255, 255, 255);

        mPaint.setAntiAlias(true);

        canvas.drawBitmap(puckBall.getBitmap(), puckBall.x - puckWidth/2, puckBall.y - puckWidth/2, mPaint);
        canvas.drawBitmap(paddleBall2.getBitmap(), paddleBall2.x - paddleWidth / 2, paddleBall2.y - paddleWidth / 2, mPaint);
        canvas.drawBitmap(paddleBall.getBitmap(), paddleBall.x - paddleWidth / 2, paddleBall.y - paddleWidth / 2, mPaint);


        for (Ball b : Ball.balls) b.update();
        for (Ball b : Ball.balls) b.detectCollisions();
        for (Ball b : Ball.balls) detectWallCollisions(b, canvas);

        invalidate();
    }

    public void detectWallCollisions(Ball b, Canvas c) {

        Canvas canvas = c;

        //when paddle hits left wall
        if (b.x < 0 + b.ballRadius/2) {
            b.speed_x = Math.abs(b.speed_x);
            b.x = 0 + b.ballRadius/2 ;

            //when paddle hits right wall
        } else if ( b.x > canvas.getWidth()- b.ballRadius/2) {

            b.speed_x= -Math.abs(b.speed_x);
            b.x = canvas.getWidth() - b.ballRadius/2;
        }
        //paddle hits top wall
        if (b.y < 0 + b.ballRadius/2) {

            b.speed_y = Math.abs(b.speed_y);
            b.y = 0 + b.ballRadius/2;

            //paddle hits bottom wall
        } else if (b.y > canvas.getHeight() - b.ballRadius/2) {

            b.speed_y = -Math.abs(b.speed_y);
            b.y = canvas.getHeight() - b.ballRadius/2;
        }

    }

}




