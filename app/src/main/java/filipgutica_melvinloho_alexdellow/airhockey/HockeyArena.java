package filipgutica_melvinloho_alexdellow.airhockey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Filip on 2014-09-15.
 */
public class HockeyArena extends View  {

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

    private int goalCountTop;
    private int goalCountBot;

    private int screenWidth;
    private int screenHeight;

    boolean scored;

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
        
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        goalCountBot = 0;
        goalCountTop = 0;
        scored = false;

        // mPaint.setColor(Color.BLUE);
        paddle = BitmapFactory.decodeResource(getResources(),R.drawable.blue_paddle);
        paddle2 = BitmapFactory.decodeResource(getResources(), R.drawable.green_paddle);
        puck = BitmapFactory.decodeResource(getResources(), R.drawable.puck);

        paddleWidth = paddle.getWidth();
        paddleHeight = paddle.getHeight();
        puckWidth = puck.getWidth();
        puckHeight = puck.getHeight();

        paddleBall2 = new Ball(paddle2, screenWidth/2, screenHeight * 1/3, (int)(paddleHeight)/2, Ball.type.paddle);
        paddleBall = new Ball(paddle, screenWidth/2, screenHeight * 2/3, (int) (paddleHeight/2), Ball.type.paddle);
        puckBall = new Ball(puck, screenWidth/2, screenHeight/2, (int)(puckWidth/2), Ball.type.puck);
    }

    public void cleanUp() {
        paddleBall = paddleBall2 = puckBall = null;
        paddle = paddle2 = puck = null;
        Ball.balls.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent m) {

        int action = m.getActionMasked();
        int index = m.getActionIndex();
        int id;

        if (index > 0) {
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
                    if (id == 0 && m.getY(i) > screenHeight/2) {
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
                    else if (id == 1 && m.getY(i) < screenHeight/2) {
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
                    if (id == 0 && m.getY(i) > screenHeight/2 + paddleHeight/2) {

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
                    if (id == 1 && m.getY(i) < screenHeight/2 - paddleHeight/2) {

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

                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                velocity.recycle();
                break;
        }

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawARGB(255, 255, 255, 255);

        mPaint.setAntiAlias(true);

        mPaint.setStrokeWidth(5f);
        canvas.drawLine(0, screenHeight/2, screenWidth, screenHeight/2, mPaint);

        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(20f);
        canvas.drawLine(screenWidth / 3, 1, screenWidth * 2 / 3, 1, mPaint);
        canvas.drawLine(screenWidth /3, screenHeight - 1, screenWidth * 2/3, screenHeight - 1, mPaint);

        RectF rectFTop = new RectF(screenWidth / 3, -(screenWidth /6), screenWidth * 2 / 3, screenWidth /6);
        RectF rectFbot = new RectF(screenWidth / 3, screenHeight - (screenWidth /6), screenWidth * 2 / 3, screenHeight + (screenWidth /6));
        canvas.drawArc(rectFTop, 0, 180, true, mPaint);
        canvas.drawArc(rectFbot, 180, 360, true, mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(3f);

        GameActivity.scoreTop.setText(String.valueOf(goalCountTop));
        GameActivity.scoreBot.setText(String.valueOf(goalCountBot));

        canvas.drawBitmap(puckBall.getBitmap(), puckBall.x - puckWidth/2, puckBall.y - puckWidth/2, mPaint);
        canvas.drawBitmap(paddleBall2.getBitmap(), paddleBall2.x - paddleWidth / 2, paddleBall2.y - paddleHeight / 2, mPaint);
        canvas.drawBitmap(paddleBall.getBitmap(), paddleBall.x - paddleWidth / 2, paddleBall.y - paddleHeight / 2, mPaint);

        if (paddleBall.y < screenHeight/2 + paddleHeight/2) {
            paddleBall.y = screenHeight / 2 + paddleHeight / 2;
            paddleBall.speed_y = Math.abs(paddleBall.speed_y);
        }

        if (paddleBall2.y > screenHeight/2 - paddleHeight/2) {
            paddleBall2.y = screenHeight / 2 - paddleHeight / 2;
            paddleBall2.speed_y = -Math.abs(paddleBall2.speed_y);
        }

        for (Ball b : Ball.balls) b.update();
        for (Ball b : Ball.balls) b.detectCollisions();
        detectWallCollisions(paddleBall);
        detectWallCollisions(paddleBall2);

        if (!scored) {
            detectWallCollisions(puckBall);
        }

        if (goalCountBot >= 10 || goalCountTop >= 10) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    commonConstructor();
                }
            }, 2000);
        }

        invalidate();
    }

    public void detectWallCollisions(final Ball b) {

        //when paddle hits left wall
        if (b.x < 0 + b.ballRadius/2) {
            b.speed_x = Math.abs(b.speed_x);
            b.x = 0 + b.ballRadius/2 ;

        //when paddle hits right wall
        } else if ( b.x > getWidth()- b.ballRadius/2) {

            b.speed_x= -Math.abs(b.speed_x);
            b.x = getWidth() - b.ballRadius/2;
        }
        //paddle hits top wall
        if (b.y < 0 + b.ballRadius/2) {

            if (b.getType() == Ball.type.paddle) {
                b.speed_y = Math.abs(b.speed_y);
                b.y = 0 + b.ballRadius / 2;
            }

            else if (b.x < getWidth() /3 || b.x > getWidth() * 2/3 && b.getType() == Ball.type.puck) {
                b.speed_y = Math.abs(b.speed_y);
                b.y = 0 + b.ballRadius / 2;
            }
            else
            {
                //Goal scored
                goalCountBot++;
                scored = true;
                b.speed_x = 0;
                b.speed_y = 0;
                b.x = getWidth() * 2;
                b.y = getHeight() * 2;

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.x = getWidth()/2;
                        b.y = getHeight() * 1/5;
                        scored = false;
                    }
                }, 2000);
            }

        //paddle hits bottom wall
        } else if (b.y > getHeight() - b.ballRadius/2) {

            if (b.getType() == Ball.type.paddle) {
                b.speed_y = -Math.abs(b.speed_y);
                b.y = getHeight() - b.ballRadius / 2;
            }
            else if (b.x < getWidth() /3 || b.x > getWidth() * 2/3 && b.getType() == Ball.type.puck) {
                b.speed_y = -Math.abs(b.speed_y);
                b.y = getHeight() - b.ballRadius / 2;
            }
            else
            {
                //Goal scored
                goalCountTop++;
                scored = true;
                b.x = getWidth() + 100;
                b.y = getHeight();
                b.speed_x = 0;
                b.speed_y = 0;

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.x = getWidth()/2;
                        b.y = getHeight() * 4/5;
                        scored = false;
                    }
                }, 2000);
            }
        }

    }

}




