package entity;

import util.Rect2d;

import java.awt.*;

public class PlantEntity extends Entity {

  public PlantEntity(int initR, int initC) {
    super(initR, initC, Integer.MAX_VALUE);
  }

  @Override
  public void draw(Graphics g, Rect2d drawRegion) {
    g.setColor(Color.GREEN);
    g.fillOval(drawRegion.x, drawRegion.y, drawRegion.width, drawRegion.height);
  }
}
