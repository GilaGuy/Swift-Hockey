package teamturbo.swifthockey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;

import teamturbo.swifthockey.wificonn.P2PManager;

import static insertcreativecompanynamehere.swifthockey.R.drawable;

/**
 * Created by Filip on 2014-09-15.
 */
public class HockeyArenaMP extends View {
    protected static int SCORE_TO_WIN = 5;
    protected static int AI_DIFFICULTY = 1;       // the lower, the more difficult
    protected static long TIME_OUT = 4000;

    protected Paint mPaint;                       // Paint to draw set color etc...
    protected Handler handler;                    // Handles delayed events

    protected Bitmap paddle;                      // First Paddle img
    protected Bitmap puck;                        // Puck img

    protected Ball paddleBall;                    // First paddle. "" ""
    protected Ball puckBall;                      // Puck "" ""

    protected float paddleWidth;                  // Width of a paddle
    protected float paddleHeight;                 // Height of a paddle
    protected float puckWidth;                    // Width of a puck
    protected float puckHeight;                   // Height of a puck

    protected VelocityTracker velocity;           // Used to track velocity of a finger as it swipes along the screen
    protected VelocityTracker velocity2;          // ----//----

    protected int goalCountTop;
    protected int goalCountBot;

    protected int screenWidth;
    protected int screenHeight;

    protected boolean scored;

    private RectF rectFTop;
    private RectF rectFbot;

    P2PManager p2PManager;
    public boolean sendLock = false;

    public HockeyArenaMP(Context context) {
        super(context);
        commonConstructor();
    }

    public HockeyArenaMP(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonConstructor();
    }

    public interface MessageTarget {
        public Handler getHandler();
    }

    private void commonConstructor() {
        cleanUp();

        SFXManager.initSounds(this);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        mPaint = new Paint();
        handler = new Handler();

        paddle = BitmapFactory.decodeResource(getResources(), drawable.funny);
        puck = BitmapFactory.decodeResource(getResources(), drawable.puck);

        paddle = getResizedBitmap(paddle, screenWidth / 7, screenWidth / 7);

        puck = getResizedBitmap(puck, screenWidth / 15, screenWidth / 15);

        paddleWidth = paddle.getWidth();
        paddleHeight = paddle.getHeight();
        puckWidth = puck.getWidth();
        puckHeight = puck.getHeight();

        paddleBall = new Ball(paddle, screenWidth / 2, screenHeight * 2 / 3, (int) (paddleHeight / 2), Ball.type.paddle);
        puckBall = new Ball(puck, screenWidth / 2, screenHeight / 2, (int) (puckWidth / 2), Ball.type.puck);

        rectFTop = new RectF(screenWidth / 3, -(screenWidth / 6), screenWidth * 2 / 3, screenWidth / 6);
        rectFbot = new RectF(screenWidth / 3, screenHeight - (screenWidth / 6), screenWidth * 2 / 3, screenHeight + (screenWidth / 6));
    }

    protected void cleanUp() {
        mPaint = null;
        handler = null;

        paddleBall = puckBall = null;
        paddle = puck = null;
        velocity = velocity2 = null;
        Ball.balls.clear();

        goalCountBot = 0;
        goalCountTop = 0;
        scored = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent m) {
        switch (m.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                paddleBall.x = m.getX();
                paddleBall.y = m.getY();
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


                break;

            case MotionEvent.ACTION_MOVE:

                paddleBall.x = m.getX();
                paddleBall.y = m.getY();
                //Add movement to tracker
                if (velocity != null) {
                    velocity.addMovement(m);
                    // When you want to determine the velocity, call
                    // computeCurrentVelocity(). Then call getXVelocity()
                    // and getYVelocity() to retrieve the velocity in pixels/10ms
                    velocity.computeCurrentVelocity(10);
                    paddleBall.speed_x = velocity.getXVelocity();
                    paddleBall.speed_y = velocity.getYVelocity();
                    paddleBall.detectCollisions();
                    detectWallCollisions(paddleBall);
                }

                break;
        }

        return true;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        loop();

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawARGB(255, 255, 255, 255);

        mPaint.setAntiAlias(true);

        mPaint.setStrokeWidth(5f);
        canvas.drawLine(0, 0, screenWidth, 0, mPaint);

        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(20f);
        canvas.drawLine(screenWidth / 3, screenHeight - 1, screenWidth * 2 / 3, screenHeight - 1, mPaint);

        canvas.drawArc(rectFbot, 180, 360, true, mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(3f);

        GameActivityMP.scoreTop.setText(String.valueOf(goalCountTop));
        GameActivityMP.scoreBot.setText(String.valueOf(goalCountBot));

        canvas.drawBitmap(puckBall.getBitmap(), puckBall.x - puckWidth / 2, puckBall.y - puckWidth / 2, mPaint);
        canvas.drawBitmap(paddleBall.getBitmap(), paddleBall.x - paddleWidth / 2, paddleBall.y - paddleHeight / 2, mPaint);

        invalidate();
    }

    public void setP2PManager(P2PManager obj) {
        p2PManager = obj;
    }

    public void updatePuckPosition(float x, float sx, float sy) {
        puckBall.x = (1 - x) * screenWidth;
        Log.d("RECEIVED XPOS", "" + puckBall.x);
        puckBall.y = 0;
        puckBall.speed_x = sx;
        puckBall.speed_y = sy;
    }

    protected void loop() {
        for (Ball b : Ball.balls) b.update();

        for (Ball b : Ball.balls) b.detectCollisions();

        detectWallCollisions(paddleBall);

        if (!scored) {
            detectWallCollisions(puckBall);
        }

        if (goalCountBot >= SCORE_TO_WIN || goalCountTop >= SCORE_TO_WIN) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (Ball b : Ball.balls) clearVelocity(b);

                    puckBall.x = screenWidth / 2;
                    puckBall.y = screenHeight / 2;

                    paddleBall.x = screenWidth / 2;
                    paddleBall.y = screenHeight * 2 / 3;

                    goalCountBot = 0;
                    goalCountTop = 0;
                }
            }, TIME_OUT);
        }
    }

    protected void detectWallCollisions(final Ball b) {
        //when paddle hits left wall
        if (b.x < 0 + b.ballRadius / 2) {

            b.speed_x = Math.abs(b.speed_x);
            b.x = 0 + b.ballRadius / 2;

            SFXManager.sfx_bounce(b);

            //when paddle hits right wall
        } else if (b.x > getWidth() - b.ballRadius / 2) {

            b.speed_x = -Math.abs(b.speed_x);
            b.x = getWidth() - b.ballRadius / 2;

            SFXManager.sfx_bounce(b);
        }

        //paddle hits top wall
        if (b.y < 0 - b.ballRadius) {

            if (b.getType() == Ball.type.paddle) {
                b.speed_y = Math.abs(b.speed_y);
                b.y = 0 + b.ballRadius / 2;

                SFXManager.sfx_bounce(b);
            } else if (b.getType() == Ball.type.puck) {
                if (!sendLock) {

                    P2PMessage p2PMessage = new P2PMessage(P2PMessage.Type.PuckInfo, puckBall.x / screenWidth, puckBall.speed_x, puckBall.speed_y);

                    if (p2PManager != null) {
                        try {
                            p2PManager.write(Serializer.serialize(p2PMessage));
                            Log.d("Sending P2P_MESSAGE", "" + p2PMessage.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        GameActivityMP.receiveLock = false;
                    }
                    sendLock = true;
                }
            }


            //paddle hits bottom wall
        } else if (b.y > getHeight() - b.ballRadius / 2) {

            if (b.getType() == Ball.type.paddle) {
                b.speed_y = -Math.abs(b.speed_y);
                b.y = getHeight() - b.ballRadius / 2;

                SFXManager.sfx_bounce(b);
            } else if (b.x < getWidth() / 3 || b.x > getWidth() * 2 / 3 && b.getType() == Ball.type.puck) {
                b.speed_y = -Math.abs(b.speed_y);
                b.y = getHeight() - b.ballRadius / 2;

                SFXManager.sfx_bounce(b);
            } else if (b.y > getHeight() + b.ballRadius) {
                //Goal scored
                SFXManager.sfx_verynice();

                goalCountTop++;
                scored = true;
                b.x = getWidth() + 100;
                b.y = getHeight();
                b.speed_x = 0;
                b.speed_y = 0;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetPuck();

                        b.y = b.y + screenHeight / 8;

                        scored = false;
                    }
                }, TIME_OUT);
            }
        }
    }

    protected void clearVelocity(Ball b) {
        b.speed_x = 0;
        b.speed_y = 0;
    }

    protected void resetPuck() {
        clearVelocity(puckBall);
        puckBall.x = screenWidth / 2;
        puckBall.y = screenHeight / 2;
    }

    protected Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public void closeConnection() {
        if (p2PManager != null)
            p2PManager.closeSocket();
    }
}
