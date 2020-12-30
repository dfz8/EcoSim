package entity.entities;

import entity.api.Entity;
import util.Rect2d;
import world.WorldMap;

import java.awt.*;

public class BunnyEntity extends Entity {
  public BunnyEntity(WorldMap worldMap, int initR, int initC) {
    super(worldMap, initR, initC, 20);
  }

  @Override
  public void draw(Graphics g, Rect2d drawRegion) {

  }

  @Override
  public void updateGrowthStage() {

  }

  @Override
  public void incurCostOfLiving() {

  }

  @Override
  public void handleReproduction() {

  }
}
