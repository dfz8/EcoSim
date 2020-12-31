package world;

import entity.entities.BunnyEntity;
import entity.entities.PlantEntity;
import util.WorldUtil;
import util.Vec2d;

import java.util.LinkedList;

public class TerrainGenerator {
  private static RNG rGen;
  private static RNG cGen;

  public static WorldMap createMap(String id, int width, int height) {
    initMapRNG(width, height);

    WorldMap worldMap = initWorldMapWithRandomElevations(id, width, height);
    floodWaterTable(worldMap, worldMap.getMaxHeight() / 3);
    flatten(worldMap, 3);
    seedPlants(worldMap);
    seedAnimals(worldMap);

    return worldMap;
  }

  private static void initMapRNG(int width, int height) {
    rGen = new RNG(width);
    cGen = new RNG(height);
  }

  private static WorldMap initWorldMapWithRandomElevations(String id, int width, int height) {
    RNG eGen = new RNG(2, 7);

    int[][] terrainType = new int[width][height];
    int[][] terrainElevation = new int[width][height];

    int heightSeeds = width * height / 2;
    log("World dimen: " + width + " x " + height);
    log("Height Seeds: " + height);
    int r, c, dE;
    for (int i = 0; i < heightSeeds; i++) {
      r = rGen.getRandomInt();
      c = cGen.getRandomInt();
      dE = eGen.getRandomInt();
      terrainElevation[r][c] += dE;
      smooth(terrainElevation, r, c, dE / 2);
    }
    return new WorldMap(id, terrainType, terrainElevation);
  }

  /**
   * Roughly simulate grain of sand distribution: adding to one point will redistribute some to
   * nearby tiles. The further the tiles are away from the source, the less they will receive.
   */
  private static void smooth(int[][] terrainElevation, int r, int c, int dESmoothAmount) {
    int i, j, ddE;
    for (int dR = -dESmoothAmount; dR <= dESmoothAmount; dR++) {
      for (int dC = -dESmoothAmount; dC <= dESmoothAmount; dC++) {
        i = r + dR;
        j = c + dC;
        if (WorldUtil.ifInvalidIndex(i, j, terrainElevation.length, terrainElevation[0].length)
            || (dR == 0 && dC == 0)) {
          continue;
        }

        // amount received determined by furthest distance in a direction, farther away --> less
        ddE = dESmoothAmount - Math.max(Math.abs(dR), Math.abs(dC)) + 1;
        if (terrainElevation[i][j] < terrainElevation[r][c]) {
          // add up to be the at most the same height
          terrainElevation[i][j] = Math.min(terrainElevation[r][c], terrainElevation[i][j] + ddE);
        }
      }
    }
  }


  private static void floodWaterTable(WorldMap map, int waterTableHeight) {
    int waterCount = 0;
    for (int r = 0; r < map.getWidth(); r++) {
      for (int c = 0; c < map.getHeight(); c++) {
        if (map.elevationMap[r][c] <= waterTableHeight) {
          map.terrainMap[r][c] = TerrainType.WATER;
          waterCount++;
        }
      }
    }
    log("Tiles converted to water: " + waterCount + " (" +
        (100 * waterCount / map.getWidth() / map.getHeight()) * 1.0 + "%)");
  }

  private static void flatten(WorldMap map, int checkRadius) {
    int flattenedAreaCount = 0;
    for (int r = 0; r < map.getWidth(); r++) {
      for (int c = 0; c < map.getHeight(); c++) {
        if (map.terrainMap[r][c] != TerrainType.WATER) {
          flattenedAreaCount += flattenHelper(map, checkRadius, r, c);
        }
      }
    }
    log("Flattened: " + flattenedAreaCount);
  }

  /**
   * Reduce the number of standout bumps / jaggedness by reducing number of tiny hills (mini-maxes).
   * New value of point will be an average of nearby valid (TerrainType.GROUND) neighbors.
   * <p>
   * If there are no valid neighbors to find the new terrain height, then we must be surrounded by
   * water and convert the terrain into water and take the average depth of water neighbors.
   *
   * @Returns the number of tiles changed.
   */
  private static int flattenHelper(WorldMap map, int checkRadius, int r, int c) {
    LinkedList<Vec2d> nearbyAreas = WorldUtil.getValidNearbySquares(map, r, c, checkRadius);

    int numValidNeighbors = 0;
    int numNeighborsLower = 0;
    for (Vec2d vec : nearbyAreas) {
      if (map.terrainMap[vec.x][vec.y] != TerrainType.WATER) {
        numValidNeighbors++;
        if (map.elevationMap[vec.x][vec.y] < map.elevationMap[r][c]) {
          numNeighborsLower++;
        }
      }
    }

    // only flatten if most of neighbors are lower
    if (numValidNeighbors > 0 && (numNeighborsLower / numValidNeighbors < 0.5)) {
      return 0;
    }

    numValidNeighbors = 0;
    int validNeighborsHeightSum = 0;
    int allNeighborsHeightSum = 0;
    nearbyAreas = WorldUtil.getValidNearbySquares(map, r, c, checkRadius);
    for (Vec2d vec : nearbyAreas) {
      allNeighborsHeightSum += map.elevationMap[vec.x][vec.y];
      if (map.terrainMap[vec.x][vec.y] != TerrainType.WATER) {
        numValidNeighbors++;
        validNeighborsHeightSum += map.elevationMap[vec.x][vec.y];
      }
    }

    if (numValidNeighbors > 0) {
      map.elevationMap[r][c] = validNeighborsHeightSum / numValidNeighbors;
    } else {
      // surrounded by water, will update terrain type to avoid one-block islands
      map.elevationMap[r][c] = allNeighborsHeightSum / nearbyAreas.size();
      map.terrainMap[r][c] = TerrainType.WATER;
    }
    return 1;
  }

  private static void seedPlants(WorldMap map) {
    int seeds = map.terrainMap.length + map.terrainMap[0].length;
    int successAdds = 0;

    int r, c;
    for (int s = 0; s < seeds; s++) {
      r = rGen.getRandomInt();
      c = cGen.getRandomInt();
      if (map.terrainMap[r][c] != TerrainType.WATER
          && map.addEntity(new PlantEntity(map, r, c), false /* isNewborn */)) {
        successAdds++;
      }
    }
    log("Planted " + seeds + " seeds, " + successAdds + " germinated.");
  }

  private static void seedAnimals(WorldMap map) {
    int animals = 2;
    int r, c;
    int a = 0;
    while (a < animals) {
      r = rGen.getRandomInt();
      c = cGen.getRandomInt();
      if (map.terrainMap[r][c] != TerrainType.WATER
          && map.addEntity(new BunnyEntity(map, r, c), false /* isNewborn */)) {
        a++;
      }
    }
  }


  private static void log(String s) {
    System.out.println(s);
  }

  private static class RNG {
    private final int mMax;
    private final int mMin;
    private final int mInterval;

    public RNG(int max) {
      this(0, max);
    }

    public RNG(int min, int max) {
      mMin = min;
      mMax = max - 1;
      mInterval = mMax - mMin;
    }

    public int getRandomInt() {
      return (int) (Math.random() * mInterval + 0.5) + mMin;
    }
  }
}
