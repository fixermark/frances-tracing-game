package com.mtomczak.tracegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;

import com.larvalabs.svgandroid.SVGParser;
import com.larvalabs.svgandroid.SVG;

import com.mtomczak.tracegame.Pathpoints;

import java.lang.StringBuilder;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Vector;

public class Traceview extends View {

  int x_location_;
  int y_location_;

  SVG trace_image_;

  Vector<Pathpoints> path_points_;

  public Traceview(Context context, AttributeSet attrs) {
    super(context, attrs);
    x_location_ = 30;
    y_location_ = 30;
    trace_image_ = SVGParser.getSVGFromResource(getResources(), R.raw.smile, true);
    setBackgroundColor(Color.WHITE);

    path_points_ = new Vector<Pathpoints>();
    Vector<Path> paths = trace_image_.getPaths();

    for (Enumeration<Path> e = paths.elements(); e.hasMoreElements();) {
      path_points_.add(new Pathpoints(e.nextElement(), 15));
    }
  }

  @Override
    protected void onDraw (Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawPicture(trace_image_.getPicture());

    Paint paint = new Paint();

    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    formatter.format("Path count: %d", trace_image_.getPaths().size());

    int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.GRAY};

    int i=0;
    for (Enumeration<Pathpoints> e = path_points_.elements();
         e.hasMoreElements(); i++) {
      paint.setColor(colors[i % colors.length]);
      for (Enumeration<PointF> e2 = e.nextElement().getPoints().elements();
           e2.hasMoreElements();) {
        PointF p = e2.nextElement();
        canvas.drawCircle(p.x, p.y, 3, paint);
      }
    }

    paint.setColor(Color.RED);
    paint.setTextSize(15);
    canvas.drawText(sb.toString(), 5, 60, paint);

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