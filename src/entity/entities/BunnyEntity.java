package entity.entities;

import entity.api.Entity;
import entity.api.Goal;
import entity.api.Traits;
import util.Rect2d;
import world.WorldMap;

import java.awt.*;

public class BunnyEntity extends Entity implements Traits.Terrestrial, Traits.SpaceOccupying {

  private Goal currentGoal = Goal.NONE;
  private int idleCounter;

  public BunnyEntity(WorldMap worldMap, int initR, int initC) {
    super(worldMap, initR, initC, 20);
  }

  @Override
  public void draw(Graphics g, Rect2d drawRegion) {
    //    g.setColor(Color.BLACK);
    //    g.drawOval(
    //        drawRegion.x + drawRegion.width / 4,
    //        drawRegion.y,
    //        drawRegion.width / 2,
    //        drawRegion.height);
    switch (getGrowthStage()) {
      case YOUTH:
        g.setColor(Color.WHITE);
        break;
      default:
        g.setColor(Color.RED);
        break;
    }
    if (getDirection() % 2 == 0) {
      g.fillOval(
          drawRegion.x,
          drawRegion.y + drawRegion.height / 4,
          drawRegion.width,
          drawRegion.height / 2);
    } else {
      g.fillOval(
          drawRegion.x + drawRegion.width / 4,
          drawRegion.y,
          drawRegion.width / 2,
          drawRegion.height);
    }
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

  @Override
  public void move() {
    switch (currentGoal) {
      case NONE:
        handleIdleState();
        break;
    }
  }

  private void handleIdleState() {
    if (++idleCounter < 5) {
      return;
    }
    idleCounter = 0;
    moveInDirection(1);

    // todo: changes goals by using threshold from DNA
  }
}
