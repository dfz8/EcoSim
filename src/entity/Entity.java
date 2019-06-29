package entity;

import util.Rect2d;
import world.WorldMap;

import java.awt.*;

public abstract class Entity {

  private int curR;
  private int curC;

  private int age;

  // If health hits 0, entity dies
  private int health;
  private GrowthStage growthStage = GrowthStage.YOUTH;

  private WorldMap worldMap;

  public Entity(WorldMap worldMap, int initR, int initC, int startingHealth) {
    this.worldMap = worldMap;
    curR = initR;
    curC = initC;
    age = 0;
    health = startingHealth;
  }

  public int getCurR() {
    return curR;
  }

  public int getCurC() {
    return curC;
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
    age++;
    updateGrowthStage();
    incurCostOfLiving();
  }

  // When age changes, entity may enter different growth stage
  public abstract void updateGrowthStage();

  public abstract void incurCostOfLiving();
}
