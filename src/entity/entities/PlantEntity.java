package entity.entities;

import entity.api.Entity;
import entity.api.GrowthStage;
import entity.api.Traits;
import util.ColorUtil;
import util.Rect2d;
import util.Vec2d;
import util.WorldUtil;
import world.TerrainType;
import world.WorldMap;

import java.awt.*;
import java.util.LinkedList;

public class PlantEntity extends Entity {
  private final int germinationCooldown;

  private final LinkedList<Vec2d> possibleTilesWithWater;
  private final LinkedList<Vec2d> possibleTilesForSpawn;
  private final int plantRootDepth;

  private int lastTimeGerminated;
  private float seedPercentage;

  public PlantEntity(WorldMap worldMap, int initR, int initC) {
    super(worldMap, initR, initC, 20);
    plantRootDepth = 2;
    germinationCooldown = 5;

    possibleTilesWithWater = WorldUtil.getValidNearbySquares(worldMap, initR, initC, 3);
    possibleTilesForSpawn = WorldUtil.getValidNearbySquares(
        worldMap,
        initR,
        initC,
        3);
  }

  @Override
  public void draw(Graphics g, Rect2d drawRegion) {
    // Plant color in growth is white --> green
    // Plant health trends color to yellow then red.
    switch (getGrowthStage()) {
      case DEAD:
        g.setColor(Color.ORANGE);
        g.fillOval(drawRegion.x, drawRegion.y, drawRegion.width, drawRegion.height);
        break;
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
      case MATURE:
        return 1;
      default:
        return 0;
    }
  }

  public void updateGrowthStage() {
    if (getAge() > 10 && getGrowthStage() != GrowthStage.MATURE) {
      setGrowthStage(GrowthStage.MATURE);
    } else if (getGrowthStage() == GrowthStage.MATURE) {
      seedPercentage += 0.5 * Math.random();
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

  public void handleReproduction() {
    if (shouldGerminate()) {
      spread();
    }
  }

  @Override
  public void move() {
    // plants don't move :^)
  }

  private boolean shouldGerminate() {
    return getAge() >= lastTimeGerminated + germinationCooldown
           && seedPercentage > 1
           && getHealth() > 10;
  }

  private void spread() {
    lastTimeGerminated = getAge();

    // try and spread the seed to nearby tiles. Germination is only successful if the tile is not
    // already occupied by some other entity.
    WorldMap map = getWorldMap();
    while (seedPercentage > 1) {
      seedPercentage -= 1;
      Vec2d coord = possibleTilesForSpawn.get(
          (int) (possibleTilesForSpawn.size() * Math.random()));
      if (map.terrainMap[coord.x][coord.y] == TerrainType.GROUND) {
        boolean canAddToSpace = true;
        for (Entity e : map.getEntitiesAtPosition(coord.x, coord.y)) {
          if (!(e instanceof Traits.SpaceOccupying)) {
            canAddToSpace = false;
            break;
          }
        }
        if (canAddToSpace) {
          map.addEntity(new PlantEntity(map, coord.x, coord.y), true /* isNewborn */);
          incrementHealth(-2);
        }
      }
    }
  }

  private boolean hasWaterNearby() {
    WorldMap map = getWorldMap();
    int elevationDifference;
    int currentElevation = map.elevationMap[getCurR()][getCurC()];
    for (Vec2d vec : possibleTilesWithWater) {
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
