package com.example.grocerycodeu.grocerycodeu.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FloatingButtonView extends View{

    private Context mContext;

    private int buttonColor;
    private int buttonDiameter;
    private Bitmap buttonImage;

    private Paint mButtonPaint;
    private Paint mImagePaint;

    private RectF destinationRect;

    private int buttonLayoutGravity;
    private int[] buttonLayoutMargins;
    private FrameLayout.LayoutParams buttonLayoutParams;

    private static int dpScale;

    //Gravity constants
    public static final int GRAVITY_BOTTOM_RIGHT = Gravity.BOTTOM | Gravity.RIGHT;

    /**
     * Creates a new floating button view default at the bottom left of the screen
     * @param context   the context of the calling activity
     * @param size      the diameter of the button
     * @param color     the color of the button
     * @param image     the image placed on the button
     */
    public FloatingButtonView(Activity context, int size, final int color, final Drawable image) {
        super(context);
        mContext = context;
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //Set the properties of the button
        setSize(size);
        setColor(color);
        setImage(image);

        //Set default gravity and margins
        setGravity(GRAVITY_BOTTOM_RIGHT);
        setMargins(0, 0, 16, 16);

        //Add the button to the screen
        ViewGroup root = (ViewGroup) context.findViewById(android.R.id.content);
        root.addView(this, buttonLayoutParams);
    }

    /**
     * Redraws the button when invalidated
     * @param canvas    the button canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        setClickable(true);

        int width = getWidth();
        int height = getHeight();

        canvas.drawCircle(width / 2, height / 2, (width / 2.1f), mButtonPaint);
        //canvas.drawBitmap(buttonImage, null, destinationRect, mImagePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            setAlpha(1.0f);
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setAlpha(0.6f);
        }
        return super.onTouchEvent(event);
    }

    /**
     * Sets the color of the button
     * @param color the new color of the button
     */
    public void setColor(final int color) {
        //Create the paint object for the circular button
        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(color);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(10.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));
        invalidate();
    }

    /**
     * Sets the gravity of the layout to define what margins the button uses to position itself
     * @param gravity   the gravity of the button in the layout
     */
    public void setGravity(int gravity){
        //Set the layout gravity
        buttonLayoutParams.gravity = gravity;
        buttonLayoutGravity = gravity;
    }

    /**
     * Sets the image to be displayed on the button
     * @param image the image drawable
     */
    public void setImage(final Drawable image) {
        //Create the paint object for the image on the button
        mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonImage = ((BitmapDrawable) image).getBitmap();
        invalidate();
    }

    /**
     * Sets the margins of the button from the edge of the screen in dp
     * @param left      the left margin in dp
     * @param top       the top margin in dp
     * @param right     the bottom margin in dp
     * @param bottom    the right margin in dp
     */
    public void setMargins(int left, int top, int right, int bottom) {
        //Set the margins in dp
        buttonLayoutParams.setMargins(dpToPixel(left),dpToPixel(top),
                dpToPixel(right), dpToPixel(bottom));

        //Set the layout margin
        buttonLayoutMargins = new int[4];
        buttonLayoutMargins[0] = left;
        buttonLayoutMargins[1] = top;
        buttonLayoutMargins[2] = right;
        buttonLayoutMargins[3] = bottom;
    }

    /**
     * Sets the diameter of the button
     * @param dp    the diameter measures in dp
     */
    public void setSize(int dp) {
        //Convert the dp value to pixels
        buttonDiameter = dpToPixel(dp);

        //Recreate the layout parameters
        buttonLayoutParams = new FrameLayout.LayoutParams(dp, dp);
        if (buttonLayoutMargins != null){
            setMargins(buttonLayoutMargins[0], buttonLayoutMargins[1],
                buttonLayoutMargins[2],buttonLayoutMargins[3]);
        }
        if (buttonLayoutGravity != 0) {
            setGravity(buttonLayoutGravity);
        }
        destinationRect = new RectF(0,0,buttonLayoutParams.width,buttonLayoutParams.height);
    }

    /**
     * Convert the dp value to pixels
     * @param dp    the dot pixel value
     * @return      the number of pixels the dp value represents on this screem
     */
    public int dpToPixel(int dp){
        //Calculate the scale of dpi
        if (dpScale == 0){
            dpScale = (int) (mContext.getResources().getDisplayMetrics().density + 0.5f);}
        return (int) (dp * dpScale + 0.5f);
    }

}
