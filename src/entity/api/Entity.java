package entity.api;

import util.Rect2d;
import world.WorldMap;

import java.awt.*;

public abstract class Entity {
  static int MOVES_PER_AGE = 10;

  private int lifeUpdateCounter;
  private int curR;
  private int curC;
  private int direction;

  private int age;

  // If health hits 0, entity dies
  private int health;
  private GrowthStage growthStage = GrowthStage.YOUTH;

  private WorldMap worldMap;

  public Entity(WorldMap worldMap, int initR, int initC, int startingHealth) {
    this.worldMap = worldMap;
    curR = initR;
    curC = initC;
    age = 1;
    health = startingHealth;
  }

  public int getCurR() {
    return curR;
  }

  public int getCurC() {
    return curC;
  }

  public void moveInDirection(int numSteps) {
    int dr = 0;
    int dc = 0;
    boolean needsToTurn = false;
    switch (direction) {
      case 0: // up
        dr = -numSteps;
        while (dr != 0 && (worldMap.hasEntityAtPosition(curR + dr, curC)) || curR + dr < 0) {
          dr++;
          needsToTurn = true;
        }
        break;
      case 1: // right
        dc = numSteps;
        while (dc != 0 && worldMap.hasEntityAtPosition(curR, curC + dc)) {
          dc--;
          needsToTurn = true;
        }
        break;
      case 2: // down
        dr = numSteps;
        while (dr != 0 && worldMap.hasEntityAtPosition(curR + dr, curC)) {
          dr--;
          needsToTurn = true;
        }
        break;
      default: // left
        dc = -numSteps;
        while (dc != 0 && (worldMap.hasEntityAtPosition(curR, curC + dc) || curC + dc < 0)) {
          dc++;
          needsToTurn = true;
        }
        break;
    }
    curR += dr;
    curC += dc;
    // if entity already in square, then turn left/right
    if (needsToTurn) {
      direction += 2 * ((int) (Math.random() + 0.5)) - 1; // -1 or 1
      direction = (direction + 4) % 4;
    }
  }

  public int getAge() {
    return age;
  }

  public int getHealth() {
    return health;
  }

  public WorldMap getWorldMap() {
    return worldMap;
  }

  public GrowthStage getGrowthStage() {
    return growthStage;
  }

  protected void setGrowthStage(GrowthStage stage) {
    growthStage = stage;
  }

  protected void incrementHealth(int dh) {
    health += dh;
  }

  public abstract void draw(Graphics g, Rect2d drawRegion);

  public void update() {
    if (getHealth() <= 0) {
      return;
    }
    if (++lifeUpdateCounter < MOVES_PER_AGE) {
      move();
      return;
    }
    move();
    lifeUpdateCounter = 0;
    age++;
    updateGrowthStage();
    incurCostOfLiving();
    handleReproduction();
  }

  // When age changes, entity may enter different growth stage
  public abstract void updateGrowthStage();

  public abstract void incurCostOfLiving();

  public abstract void handleReproduction();

  public abstract void move();
}
