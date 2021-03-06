package com.example.grocerycodeu.grocerycodeu.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

public class FloatingActionButton2 extends View {

    Context context;
    Paint mButtonPaint;
    Paint mImagePaint;
    Bitmap mImage;
    boolean mHidden = false;

    Color mColor;

    public FloatingActionButton2(Context context) {
        super(context);
        this.context = context;
        init(Color.WHITE);
    }

    public void init(int color) {
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }


    private void createPaintObjects(Color color, Drawable image) {

        //Create the paint object for the circular button
        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(color.hashCode());
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(10.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));
        invalidate();

        //Create the paint object for the image on the button
        mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mImage = ((BitmapDrawable) image).getBitmap();
        invalidate();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        setClickable(true);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float) (getWidth() / 2.6), mButtonPaint);
        canvas.drawBitmap(mImage, (getWidth() - mImage.getWidth()) / 2,
                (getHeight() - mImage.getHeight()) / 2, mImagePaint);
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

    public void hide() {
        if (!mHidden) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1, 0);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1, 0);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleX, scaleY);
            animSetXY.setInterpolator(new AccelerateInterpolator());
            animSetXY.setDuration(100);
            animSetXY.start();
            mHidden = true;
        }
    }

    public void show() {
        if (mHidden) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0, 1);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleX, scaleY);
            animSetXY.setInterpolator(new OvershootInterpolator());
            animSetXY.setDuration(200);
            animSetXY.start();
            mHidden = false;
        }
    }

    public boolean isHidden() {
        return mHidden;
    }

    public static class Builder {
        private FrameLayout.LayoutParams params;
        private final Activity activity;
        int gravity = Gravity.BOTTOM | Gravity.RIGHT; // default bottom right
        Drawable drawable;
        int color = Color.WHITE;
        int size = 0;
        float scale = 0;

        /**
         * Constructor using a context for this builder and the
         * {@link FloatingActionButton} it creates
         * @param context
         */
        public Builder(Activity context) {
            scale = context.getResources().getDisplayMetrics().density;
            // The calculation (value * scale + 0.5f) is a widely used to convert to dps to pixel
            // units based on density scale
            // see <a href="http://developer.android.com/guide/practices/screens_support.html">
            // developer.android.com (Supporting Multiple Screen Sizes)</a>
            size = (int) (72 * scale + 0.5f); // default size is 72dp by 72dp
            params = new FrameLayout.LayoutParams(size, size);
            params.gravity = gravity;

            this.activity = context;
        }

        /**
         * Sets the FAB gravity.
         */
        public Builder withGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * Sets the FAB margins in dp.
         */
        public Builder withMargins(int left, int top, int right, int bottom) {
            params.setMargins((int) (left * scale + 0.5f), (int) (top * scale + 0.5f),
                    (int) (right * scale + 0.5f), (int) (bottom * scale + 0.5f));
            return this;
        }

        /**
         * Sets the FAB size.
         *
         * @param size
         * @return
         */
        public Builder withSize(int size) {
            size = (int) (size * scale + 0.5f);
            params = new FrameLayout.LayoutParams(size, size);
            return this;
        }

        /**
         * Creates a {@link FloatingActionButton2} with the
         * arguments supplied to this builder.
         */
        public FloatingActionButton2 create(final int color, final Drawable drawable) {
            final FloatingActionButton2 button = new FloatingActionButton2(activity);

            params.gravity = this.gravity;
            ViewGroup root = (ViewGroup) activity.findViewById(android.R.id.content);
            root.addView(button, params);
            return button;
        }
    }

}
