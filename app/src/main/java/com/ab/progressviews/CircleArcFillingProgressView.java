package com.ab.progressviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Akshay Bhat.
 */
public class CircleArcFillingProgressView extends View {

    private static final String INSTANCE_STATE = "instance_state";
    private static final String INSTANCE_PROGRESS = "progress";

    private Paint mFillingPaint;
    private Paint mUnFillingPaint;
    private RectF mOvalRect = new RectF();
    private int mProgress;
    private float mFillingFactor = 0f;
    private int mStartAngle = 0;
    private int mSweepAngle = 0;
    private TextPaint mProgressTextPaint;

    public CircleArcFillingProgressView(Context context) {
        super(context);
        init();
    }

    public CircleArcFillingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleArcFillingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleArcFillingProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        //Filling Paint
        mFillingPaint = new Paint();
        mFillingPaint.setStyle(Paint.Style.FILL);
        mFillingPaint.setAntiAlias(true);
        mFillingPaint.setColor(Color.BLUE);
        //UnFilling Paint
        mUnFillingPaint = new Paint();
        mUnFillingPaint.setStyle(Paint.Style.FILL);
        mUnFillingPaint.setAntiAlias(true);
        mUnFillingPaint.setColor(Color.GRAY);
        //Text Color
        mProgressTextPaint = new TextPaint();
        mProgressTextPaint.setAntiAlias(true);
        mProgressTextPaint.setColor(Color.YELLOW);
        mProgressTextPaint.setTextAlign(Paint.Align.CENTER);
        mProgressTextPaint.setTextSize(25);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
        mOvalRect.set(0,0, size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //calculate sweep angle
        mSweepAngle = (int) (360 / mFillingFactor);
        //draw
        canvas.drawArc(mOvalRect, mStartAngle, mSweepAngle, true, mFillingPaint);
        canvas.drawArc(mOvalRect, mStartAngle, -(360 - mSweepAngle), true, mUnFillingPaint);
        String progressText = mProgress+"%";
        int textHeight = (int) (mProgressTextPaint.descent() + mProgressTextPaint.ascent());
        int x = getWidth() / 2 - (int) mProgressTextPaint.measureText(progressText) / 2;
        int y = getHeight() / 2 - textHeight / 2;
        canvas.drawText(progressText,x,y,mProgressTextPaint);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mFillingFactor = 100f / progress;
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
