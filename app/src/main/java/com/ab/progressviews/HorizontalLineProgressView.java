package com.ab.progressviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Akshay Bhat.
 */
public class HorizontalLineProgressView extends View {

    private static final String INSTANCE_STATE = "instance_state";
    private static final String INSTANCE_PROGRESS = "progress";

    private RectF mUnFilledRect = new RectF();
    private RectF mFilledRect = new RectF();
    private int mProgress = 0;
    private float mProgressFactor;
    private Paint mUnFilledPaint;
    private Paint mFilledPaint;
    private int mUnfilledColor = Color.GRAY;
    private int mFilledColor = Color.GREEN;

    public HorizontalLineProgressView(Context context) {
        super(context);
        init();
    }

    public HorizontalLineProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalLineProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //UnFilled paint
        mUnFilledPaint = new Paint();
        mUnFilledPaint.setColor(mUnfilledColor);
        mUnFilledPaint.setStyle(Paint.Style.FILL);
        mUnFilledPaint.setAntiAlias(true);
        //Filled Paint
        mFilledPaint = new Paint();
        mFilledPaint.setColor(mFilledColor);
        mFilledPaint.setStyle(Paint.Style.FILL);
        mFilledPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float left = 0;
        float top = 0;
        float right = getWidth();
        float bottom = getHeight();
        mUnFilledRect.set(left, top, right, bottom);
        mFilledRect.set(left, top, left, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //calculate
        mFilledRect.right = getWidth() / mProgressFactor;
        //draw
        canvas.drawRect(mUnFilledRect, mUnFilledPaint);
        canvas.drawRect(mFilledRect, mFilledPaint);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mProgressFactor = 100f / mProgress;
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_PROGRESS, mProgress);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getInt(INSTANCE_PROGRESS);
            init();
            setProgress(mProgress);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
