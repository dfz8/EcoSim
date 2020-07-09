package src.entity;

import src.util.ColorUtil;
import src.util.Rect2d;
import src.util.Vec2d;
import src.util.WorldUtil;
import src.world.TerrainType;
import src.world.WorldMap;

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
    // Plant color in growth is white --> green
    // Plant health trends color to yellow then red.
    switch (getGrowthStage()) {
      case YOUTH:
        int minYouthHealth = getAge() * getHealthIncrementForStage(GrowthStage.YOUTH);
        float healthP = 1 - (1f * Math.min(getHealth(), minYouthHealth) / minYouthHealth);
        g.setColor(
            ColorUtil.interpolate(
                ColorUtil.interpolate(
                    Color.WHITE,
                    Color.GREEN,
                    1f * getAge() / getAgeTo(GrowthStage.MATURE)),
                Color.YELLOW,
                Color.RED,
                healthP));
        g.fillOval(drawRegion.x, drawRegion.y, drawRegion.width, drawRegion.height);
        break;
      case MATURE:
        int minMatureHealth =
            getAgeTo(GrowthStage.MATURE) * getHealthIncrementForStage(GrowthStage.YOUTH);
        float pToDead = 1 - (1f * Math.min(getHealth(), minMatureHealth) / minMatureHealth);
        g.setColor(ColorUtil.interpolate(Color.GREEN, Color.YELLOW, Color.RED, pToDead));
        g.fillOval(drawRegion.x, drawRegion.y, drawRegion.width, drawRegion.height);

        // a "flower" is drawn when the plant is healthy enough
        int halfWidth = drawRegion.width / 2;
        int halfHeight = drawRegion.height / 2;
        float petalP = Math.min(0.5f, 1 - pToDead);
        int petalWidth = (int) (petalP * halfWidth);
        int petalHeight = (int) (petalP * halfHeight);
        g.setColor(Color.WHITE);
        g.fillOval(
            drawRegion.x + halfWidth - petalWidth,
            drawRegion.y + halfHeight - petalHeight,
            2 * petalWidth,
            2 * petalHeight);
        break;
    }
  }

  protected int getAgeTo(GrowthStage stage) {
    switch (stage) {
      case MATURE:
        return 10;
      default:
        return 0;
    }
  }

  protected int getHealthIncrementForStage(GrowthStage stage) {
    switch (stage) {
      case YOUTH:
        return 2;
      default:
        return 1;
    }
  }

  public void updateGrowthStage() {
    if (getAge() > 10 && getGrowthStage() != GrowthStage.MATURE) {
      setGrowthStage(GrowthStage.MATURE);
    }

    // Only grow if you have a water source nearby.
    if (hasWaterNearby()) {
      incrementHealth(getHealthIncrementForStage(getGrowthStage()));
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
