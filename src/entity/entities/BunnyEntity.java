package entity.entities;

import entity.api.Entity;
import entity.api.Goal;
import entity.api.GrowthStage;
import entity.api.Traits;
import util.Rect2d;
import util.Vec2d;
import world.WorldMap;

import java.awt.*;
import java.util.LinkedList;

public class BunnyEntity extends Entity implements Traits.Terrestrial, Traits.SpaceOccupying {

  private final int MATING_COOLDOWN = 3;

  private Goal currentGoal = Goal.NONE;
  private int idleCounter;
  private int lastMateAge;

  public BunnyEntity(WorldMap worldMap, int initR, int initC) {
    super(worldMap, initR, initC, 20);
  }

  @Override
  public void draw(Graphics g, Rect2d drawRegion) {
    switch (getGrowthStage()) {
      case YOUTH:
        g.setColor(Color.WHITE);
        break;
      case MATURE:
        if (currentGoal == Goal.MATE) {
          g.setColor(Color.PINK);
        } else {
          g.setColor(Color.MAGENTA);
        }
        break;
      default:
        g.setColor(Color.BLACK);
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
    if (getAge() > 10 && getGrowthStage() != GrowthStage.MATURE) {
      setGrowthStage(GrowthStage.MATURE);
    }
  }

  @Override
  public void incurCostOfLiving() {
    // add later when implementing food
    //    incrementHealth(-1);
  }

  @Override
  public void handleReproduction() {
    if (currentGoal != Goal.MATE) {
      return;
    }

    LinkedList<Vec2d> possibleSpawnPoints = getWorldMap().getPossibleSpawnPoints(this);
    if (possibleSpawnPoints.isEmpty()) {
      return;
    }

    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) {
          continue;
        }
        LinkedList<Entity> entities =
            getWorldMap().getEntitiesAtPosition(getCurR() + dr, getCurC() + dc);
        for (Entity e : entities) {
          if (e instanceof BunnyEntity) {
            BunnyEntity potentialMate = (BunnyEntity) e;
            if (potentialMate.canMate() && potentialMate.currentGoal == Goal.MATE) {
              // add baby bunny
              Vec2d spawnPoint = possibleSpawnPoints.get(
                  (int) (Math.random() * possibleSpawnPoints.size()));
              getWorldMap().addEntity(
                  new BunnyEntity(getWorldMap(), spawnPoint.x, spawnPoint.y), true /* isNewborn */);
              // incur spawn cost
              incrementHealth(-5);
              potentialMate.incrementHealth(-5);

              // reset
              potentialMate.lastMateAge = potentialMate.getAge();
              lastMateAge = getAge();
              potentialMate.currentGoal = Goal.NONE;
              currentGoal = Goal.NONE;
            }
          }
        }

      }
    }
  }

  @Override
  public void move() {
    switch (currentGoal) {
      case NONE:
        handleIdleState();
        break;
      case MATE:
        findMate();
        break;
    }
  }

  private void handleIdleState() {
    // todo: changes goals by using threshold from DNA
    if (++idleCounter < 5) {
      return;
    }
    idleCounter = 0;
    moveInDirection(1);

    if (Math.random() < 0.2) {
      turn();
    }

    if (canMate() && Math.random() < 0.2) {
      currentGoal = Goal.MATE;
    }
  }

  private boolean canMate() {
    return getGrowthStage() == GrowthStage.MATURE
           && getAge() - lastMateAge >= MATING_COOLDOWN
           && getHealth() > 5;
  }

  private void findMate() {
    moveInDirection(1);
  }
}
