package entity;

import util.Rect2d;

import java.awt.*;

public abstract class Entity {

  private int curR;
  private int curC;

  private int age;
  private int naturalLifespan;
  private boolean isAlive;

  public Entity(int initR, int initC, int naturalLifespan) {
    curR = initR;
    curC = initC;
    age = 0;
    this.naturalLifespan = naturalLifespan;
    isAlive = true;
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

  public int getNaturalLifespan() {
    return naturalLifespan;
  }

  public boolean isAlive() {
    return isAlive;
  }

  public abstract void draw(Graphics g, Rect2d drawRegion);
}
