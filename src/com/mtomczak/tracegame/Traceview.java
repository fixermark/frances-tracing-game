package com.mtomczak.tracegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;

import com.larvalabs.svgandroid.SVGParser;
import com.larvalabs.svgandroid.SVG;

public class Traceview extends View {

  int x_location_;
  int y_location_;

  SVG trace_image_;

  public Traceview(Context context, AttributeSet attrs) {
    super(context, attrs);
    x_location_ = 30;
    y_location_ = 30;
    trace_image_ = SVGParser.getSVGFromResource(getResources(), R.raw.smile);
    setBackgroundColor(Color.WHITE);
  }

  @Override
    protected void onDraw (Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawPicture(trace_image_.getPicture());

    Paint paint = new Paint();
    paint.setColor(Color.GREEN);
    paint.setTextSize(25);
    canvas.drawText("Boom!", x_location_, y_location_, paint);
  }

  @Override
    public boolean onTouchEvent(MotionEvent event) {
    x_location_ = (int)event.getX();
    y_location_ = (int)event.getY();
    invalidate();
    return true;
  }
}