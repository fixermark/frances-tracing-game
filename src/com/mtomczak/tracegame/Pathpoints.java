// Copyright 2012 Mark T. Tomczak
// Part of "Frances's Tracing Game"
// See LICENSE file at the root of the software project
// for more information.

package com.mtomczak.tracegame;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;

import java.lang.Math;
import java.util.Enumeration;
import java.util.Vector;

public class Pathpoints {
  Vector<Pathpoint> points_ = null;
  Vector<Path> path_segments_ = null;

  public Pathpoints(Path path, float dist) {
    points_ = Pathpoints.PathToPoints(path, dist);
    path_segments_ = Pathpoints.PathToSegments(path, dist);
  }

  public Vector<Pathpoint> getPoints() {
    return points_;
  }

  /**
   * Return a vector listing all the segments that are bounded
   * by two selected elements.
   */
  public Vector<Path> getSelectedSegments() {
    Vector<Path> selected = new Vector<Path>();
    for (int i=0; i < points_.size()-1; i++) {
      if (points_.get(i).isSelected() && points_.get(i+1).isSelected()) {
        selected.add(path_segments_.get(i));
      }
    }
    return selected;
  }

  public boolean allPointsSelected() {
    for (Pathpoint path_point : points_) {
      if (!path_point.isSelected()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Tries to select a valid point near the specified coordinate
   */
  public void selectValidPoint(float x, float y) {
    Vector<Pathpoint> points_to_search = points_;

    for (Pathpoint path_point : points_to_search) {
      if (!path_point.isSelected()) {
        if (path_point.isInRange(x, y)) {
          path_point.select();
        }
      }
    }
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
    measure.getPosTan(path_length, pos, tan);
    points.add(new Pathpoint(pos[0], pos[1]));
    return points;
  }

  static public Vector<Path> PathToSegments(Path path, float dist) {
    Vector<Path> segments = new Vector<Path>();
    PathMeasure measure = new PathMeasure(path, false);
    float path_length = measure.getLength();

    for (float step = 0; step < path_length; step += dist) {
      Path segment = new Path();
      if (measure.getSegment(step, step + dist, segment, true)) {
        segments.add(segment);
      }
    }
    return segments;
  }

  public static class Pathpoint {
    private PointF point_ = null;
    private boolean selected_;

    private static final float SELECT_RANGE = 25;

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