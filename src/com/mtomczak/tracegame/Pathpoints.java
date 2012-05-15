package com.mtomczak.tracegame;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;

import java.lang.Math;
import java.util.Enumeration;
import java.util.Vector;

public class Pathpoints {
  Vector<Pathpoint> points_ = null;
  Vector<Pathpoint> selectable_points_ = null;

  public Pathpoints(Path path, float dist) {
    points_ = Pathpoints.PathToPoints(path, dist);
    selectable_points_ = new Vector<Pathpoint>();
  }

  public Vector<Pathpoint> getPoints() {
    return points_;
  }

  /**
   * Tries to select a valid point near the specified coordinate
   */
  public void selectValidPoint(float x, float y) {
    Vector<Pathpoint> points_to_search = selectable_points_;
    if (selectable_points_.isEmpty()) {
      points_to_search = points_;
    }
    for (Enumeration<Pathpoint> e=points_to_search.elements();
         e.hasMoreElements();) {
      Pathpoint path_point = e.nextElement();
      if (!path_point.isSelected()) {
        if (path_point.isInRange(x, y)) {
          selectAndQueueNeighbors(path_point);
        }
      }
    }
  }
  /**
   * Selects a specific point and queues its neighbors for selectability.
   */
  private void selectAndQueueNeighbors(Pathpoint point) {
    point.select();
    selectable_points_.removeElement(point);
    int idx = points_.indexOf(point);
    if (-1==idx) {
      return;
    }
    int left_element = idx - 1;
    if (left_element < 0) {
      left_element = 0;
    }
    int right_element = idx + 1;
    if (right_element > points_.size() - 1) {
      right_element = points_.size() - 1;
    }
    addIfNotSelected(points_.get(left_element));
    addIfNotSelected(points_.get(right_element));
  }

  private void addIfNotSelected(Pathpoint point) {
    if (!point.isSelected()) {
      selectable_points_.add(point);
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