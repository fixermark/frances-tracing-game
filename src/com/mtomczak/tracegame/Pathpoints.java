package com.mtomczak.tracegame;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;

import java.util.Vector;

public class Pathpoints {
  Vector<PointF> points_ = null;

  public Pathpoints(Path path, float dist) {
    points_ = Pathpoints.PathToPoints(path, dist);
  }

  public Vector<PointF> getPoints() {
    return points_;
  }

  static public Vector<PointF> PathToPoints(Path path, float dist) {
    Vector<PointF> points = new Vector<PointF>();
    PathMeasure measure = new PathMeasure(path, false);
    float path_length = measure.getLength();

    float[] pos = new float[2];
    float[] tan = new float[2];

    for (float step = 0; step < path_length; step += dist) {
      measure.getPosTan(step, pos, tan);
      points.add(new PointF(pos[0], pos[1]));
    }
    return points;
  }
}