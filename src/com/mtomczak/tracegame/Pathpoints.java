package com.mtomczak.tracegame;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;

import java.lang.Math;
import java.util.Vector;

public class Pathpoints {
  Vector<Pathpoint> points_ = null;

  public Pathpoints(Path path, float dist) {
    points_ = Pathpoints.PathToPoints(path, dist);
  }

  public Vector<Pathpoint> getPoints() {
    return points_;
  }

  static public Vector<Pathpoint> PathToPoints(Path path, float dist) {
    Vector<Pathpoint> points = new Vector<Pathpoint>();
    PathMeasure measure = new PathMeasure(path, false);
    float path_length = measure.getLength();

    float[] pos = new float[2];
    float[] tan = new float[2];

    for (float step = 0; step < path_length; step += dist) {
      measure.getPosTan(step, pos, tan);
      points.add(new Pathpoint(pos[0], pos[1]));
    }
    return points;
  }

  public static class Pathpoint {
    private PointF point_ = null;
    private boolean selected_;

    private static final float SELECT_RANGE = 20;

    public Pathpoint(float x, float y) {
      selected_ = false;
      point_ = new PointF(x, y);
    }

    public PointF getPoint() {
      return point_;
    }

    public boolean isSelected() {
      return selected_;
    }

    public boolean isInRange(float x, float y) {
      return (Math.abs(x - point_.x) <= SELECT_RANGE &&
              Math.abs(y - point_.y) <= SELECT_RANGE);
    }

    public void select() {
      selected_ = true;
    }
  }
}