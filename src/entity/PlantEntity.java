package entity;

import util.Rect2d;
import util.Vec2d;
import util.WorldUtil;
import world.TerrainType;
import world.WorldMap;

import java.awt.*;
import java.util.LinkedList;

public class PlantEntity extends Entity {
  private LinkedList<Vec2d> importantTilesToTrack;
  private int plantRootDepth;

  public PlantEntity(WorldMap worldMap, int initR, int initC) {
    super(worldMap, initR, initC, 20);

    importantTilesToTrack = WorldUtil.getValidNearbySquares(worldMap, initR, initC, 3);
    plantRootDepth = 2;
  }

  @Override
  public void draw(Graphics g, Rect2d drawRegion) {
    if (getHealth() < 20) {
      g.setColor(Color.YELLOW);
    } else {
      g.setColor(Color.GREEN);
    }
    g.fillOval(drawRegion.x, drawRegion.y, drawRegion.width, drawRegion.height);
  }

  public void updateGrowthStage() {
    if (getAge() > 10 && getGrowthStage() != GrowthStage.MATURE) {
      setGrowthStage(GrowthStage.MATURE);
    }

    // Only grow if you have a water source nearby.
    if (hasWaterNearby()) {
      switch (getGrowthStage()) {
        case YOUTH:
          incrementHealth(2);
        case MATURE:
          incrementHealth(1);
      }
    }
  }

  public void incurCostOfLiving() {
    // Plants need a nearby water source to survive, otherwise they will start withering.
    // todo: explore idea of roots, and hitting the watertable for trees
    if (!hasWaterNearby()) {
      incrementHealth(-1);
    }
  }

  private boolean hasWaterNearby() {
    WorldMap map = getWorldMap();
    int elevationDifference;
    int currentElevation = map.elevationMap[getCurR()][getCurC()];
    for (Vec2d vec : importantTilesToTrack) {
      if (map.terrainMap[vec.x][vec.y] == TerrainType.WATER) {
        elevationDifference = currentElevation - map.elevationMap[vec.x][vec.y];
        if (elevationDifference <= plantRootDepth) {
          return true;
        }
      }
    }
    return false;
  }
}
