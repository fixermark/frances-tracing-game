/*
Copyright 2012 Mark T. Tomczak

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.mtomczak.tracegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;
import android.util.Log;

import com.larvalabs.svgandroid.SVGParser;
import com.larvalabs.svgandroid.SVG;

import com.mtomczak.tracegame.Pathpoints;

import java.lang.StringBuilder;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Vector;

public class Traceview extends View
  implements MediaPlayer.OnCompletionListener,
             View.OnTouchListener {

  int x_location_;
  int y_location_;

  // The center of the canvas is offset to center the picture in the view.
  float canvas_offset_x_ = 0;
  float canvas_offset_y_ = 0;

  // Index of the current picture to show. This starts at
  // -1 so that when we load, we increment it to 0 (and show
  // the first picture).
  int resource_index_ = -1;

  MediaPlayer yay_sound_ = null;
  MediaPlayer intro_sound_ = null;

  SVG trace_image_;
  String trace_image_name_;
  float trace_image_scale_factor_;

  boolean loading_ = false;
  boolean load_next_image_ = true;

  public static final String TRACE_NAMES[] = {
    "circle",
    "square",
    "triangle",
    "smile",
    "car",
    "house",
    "tree",
    "dog",
    "clouds",
    "sun",
    "moon",
    "jellyfish"
  };

  public static final int TRACE_RESOURCES[] = {
    R.raw.circle,
    R.raw.square,
    R.raw.triangle,
    R.raw.smile,
    R.raw.car,
    R.raw.house,
    R.raw.tree,
    R.raw.dog,
    R.raw.clouds,
    R.raw.sun,
    R.raw.moon,
    R.raw.jellyfish
  };

  // Note: 44.1kHz mono have generally worked best.
  public static final int SOUND_RESOURCES[] = {
    R.raw.circle_snd,
    R.raw.square_snd,
    R.raw.triangle_snd,
    R.raw.smile_snd,
    R.raw.car_snd,
    R.raw.house_snd,
    R.raw.tree_snd,
    R.raw.dog_snd,
    R.raw.clouds_snd,
    R.raw.sun_snd,
    R.raw.moon_snd,
    R.raw.jellyfish_snd
  };

  public static final int YAY_RESOURCE = R.raw.yay_snd;

  Vector<Pathpoints> path_points_;

  public Traceview(Context context, AttributeSet attrs) {
    super(context, attrs);
    x_location_ = 30;
    y_location_ = 30;
    setBackgroundColor(Color.WHITE);
    loadSounds();
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
    trace_image_name_ = TRACE_NAMES[resource_index_ % TRACE_NAMES.length];

    path_points_ = new Vector<Pathpoints>();
    Vector<Path> paths = trace_image_.getPaths();

    // Calculate offset needed to center the image in the view.
    Picture trace_picture = trace_image_.getPicture();
    float image_center_x = (float)(trace_picture.getWidth() / 2);
    float image_center_y = (float)(trace_picture.getHeight() / 2);
    float canvas_center_x = (float)(getWidth() / 2);
    float canvas_center_y = (float)(getHeight() / 2);
    Log.w("Frances", Float.toString(image_center_x)
	  + Float.toString(image_center_y) + " "
	  + Float.toString(canvas_center_x) + " "
	  + Float.toString(canvas_center_y));
    canvas_offset_x_ = canvas_center_x - image_center_x;
    canvas_offset_y_ = canvas_center_y - image_center_y;

    Log.w("Frances", "Bounds set to "
	  + Float.toString(canvas_offset_x_) + ", " +
          Float.toString(canvas_offset_y_));

    float viewWidth = (float)(getWidth());
    // scale factor = (scale ratio) * view width / image width
    trace_image_scale_factor_ = 0.8f * viewWidth / ((float)trace_picture.getWidth());

    for (Path path : paths) {
      path_points_.add(new Pathpoints(path, 15, trace_image_scale_factor_,
                                      image_center_x, image_center_y));
    }
    intro_sound_.release();
    intro_sound_ = MediaPlayer.create(
      getContext(),
      SOUND_RESOURCES[resource_index_ % SOUND_RESOURCES.length]);
    intro_sound_.start();
    loading_ = false;
  }

  @Override
    protected void onDraw (Canvas canvas) {
    super.onDraw(canvas);


    if (load_next_image_) {
      loadNextImage();
    }


    Picture trace_picture = trace_image_.getPicture();
    // Scale up the canvas to fit in the frame (with 1/4 margins left on each side)
    canvas.scale(trace_image_scale_factor_,
                 trace_image_scale_factor_,
                 canvas.getWidth() / 2.0f,
                 canvas.getHeight() / 2.0f);


    canvas.translate(canvas_offset_x_, canvas_offset_y_);
    trace_picture.draw(canvas);

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

    // Name the picture
    Paint text_paint = new Paint();
    text_paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    text_paint.setTextAlign(Paint.Align.CENTER);
    text_paint.setTextSize(24);
    text_paint.setColor(Color.BLUE);
    text_paint.setStyle(Paint.Style.FILL);
    canvas.drawText(trace_image_name_,
                    ((float)trace_picture.getWidth()) / 2.0f,
                    (float)trace_picture.getHeight() + 24.0f,
                    text_paint);

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
  public boolean onTouch(View v, MotionEvent event) {
    event.offsetLocation(-getLeft(), -getTop());
    return onTouchEvent(event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // We handle multiple touch pointers here, because it turns out
    // that it's asking a lot of tiny hands to keep their other
    // fingers, palm, etc. off the screen and only touch with one
    // fingertip at a time. :)
    for (int i=0; i < event.getPointerCount(); i++) {
      // subtract the canvas offset to convert from screen coordinates
      // back into picture-local coordinates (which is what we
      // calculated the touch points in).
      x_location_ = (int)event.getX(i) - (int)canvas_offset_x_;
      y_location_ = (int)event.getY(i) - (int)canvas_offset_y_;
      if (path_points_ != null) {
        for (Pathpoints path_points : path_points_) {
          path_points.selectValidPoint(x_location_, y_location_);
        }
      }
    }

    invalidate();
    return true;
  }

  public void onCompletion(MediaPlayer mp) {
    load_next_image_ = true;
    postInvalidate();
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
    load_next_image_ = false;
  }
}
