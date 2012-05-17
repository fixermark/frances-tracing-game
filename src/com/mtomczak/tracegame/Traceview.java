package com.mtomczak.tracegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

public class Traceview extends View
  implements MediaPlayer.OnCompletionListener {

  int x_location_;
  int y_location_;

  int resource_index_ = 0;

  MediaPlayer yay_sound_ = null;
  MediaPlayer intro_sound_ = null;

  SVG trace_image_;

 boolean loading_ = false;

  public static final int TRACE_RESOURCES[] = {
    R.raw.circle,
    R.raw.square,
    R.raw.triangle,
    R.raw.smile,
    R.raw.car,
    R.raw.house
  };

  public static final int SOUND_RESOURCES[] = {
    R.raw.circle_snd,
    R.raw.square_snd,
    R.raw.triangle_snd,
    R.raw.smile_snd,
    R.raw.car_snd,
    R.raw.house_snd
  };

  public static final int YAY_RESOURCE = R.raw.yay_snd;

  Vector<Pathpoints> path_points_;

  public Traceview(Context context, AttributeSet attrs) {
    super(context, attrs);
    x_location_ = 30;
    y_location_ = 30;
    setBackgroundColor(Color.WHITE);
    loadSounds();
    loadImage();
  }

  private void loadSounds() {
    yay_sound_ = MediaPlayer.create(getContext(), YAY_RESOURCE);
    yay_sound_.setOnCompletionListener(this);
    intro_sound_ = new MediaPlayer();
  }

  private void loadImage() {
    trace_image_ = SVGParser.getSVGFromResource(
      getResources(),
      TRACE_RESOURCES[resource_index_ % TRACE_RESOURCES.length],
      true);

    path_points_ = new Vector<Pathpoints>();
    Vector<Path> paths = trace_image_.getPaths();

    for (Path path : paths) {
      path_points_.add(new Pathpoints(path, 15));
    }
    intro_sound_.release();
    intro_sound_ = MediaPlayer.create(
      getContext(),
      SOUND_RESOURCES[resource_index_ % SOUND_RESOURCES.length]);
    intro_sound_.start();
    loading_ = false;
    postInvalidate();
  }

  @Override
    protected void onDraw (Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawPicture(trace_image_.getPicture());

    Paint paint = new Paint();

    for (Pathpoints path_points : path_points_) {
      Vector<Path> paths = path_points.getSelectedSegments();
      Paint path_paint = new Paint();
      path_paint.setStrokeWidth(5);
      path_paint.setColor(Color.MAGENTA);
      path_paint.setStyle(Paint.Style.STROKE);
      for (Path path : paths) {
        canvas.drawPath(path, path_paint);
      }
    }

    //highlightPoints(canvas, paint);

    if (allPathsSelected() && !loading_) {
      goToNextTrace();
    }
  }

  private void highlightPoints(Canvas canvas, Paint paint) {
    int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.GRAY};

    int i=0;
    for (Pathpoints path_points : path_points_) {
      int current_color = colors[i % colors.length];

      for (Pathpoints.Pathpoint path_point : path_points.getPoints()) {
        PointF p = path_point.getPoint();
        if (path_point.isSelected()) {
          paint.setColor(Color.YELLOW);
        } else {
          paint.setColor(current_color);
        }
        canvas.drawCircle(p.x, p.y, 3, paint);
      }
    }
  }

  @Override
    public boolean onTouchEvent(MotionEvent event) {
    x_location_ = (int)event.getX();
    y_location_ = (int)event.getY();
    for (Pathpoints path_points : path_points_) {
      path_points.selectValidPoint(x_location_, y_location_);
    }

    invalidate();
    return true;
  }

  public void onCompletion(MediaPlayer mp) {
    loadNextImage();
  }

  private boolean allPathsSelected() {
    for (Pathpoints path_points : path_points_) {
      if (!path_points.allPointsSelected()) {
        return false;
      }
    }
    return true;
  }

  private void goToNextTrace() {
    loading_ = true;
    yay_sound_.start();
    // On completion, yay sound will move forward to next image.
  }

  private void loadNextImage() {
    resource_index_++;
    loadImage();
  }
}