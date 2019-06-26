package world;

public class TerrainGenerator {

  public static WorldMap createMap(String id, int width, int height) {
    RNG rGen = new RNG(width);
    RNG cGen = new RNG(height);
    RNG eGen = new RNG(2, 7);

    int[][] terrainType = new int[width][height];
    int[][] terrainElevation = new int[width][height];

    int heightSeeds = width * height / 2;
    int r, c, dE;
    for (int i = 0; i < heightSeeds; i++) {
      r = rGen.getRandomInt();
      c = cGen.getRandomInt();
      dE = eGen.getRandomInt();
      terrainElevation[r][c] += dE;
      smooth(terrainElevation, r, c, dE / 2);
    }

    WorldMap worldMap = new WorldMap(id, terrainType, terrainElevation);

    floodWaterTable(worldMap, worldMap.getMaxHeight() / 3);
    flatten(worldMap, 3);
    return worldMap;
  }

  private static void floodWaterTable(WorldMap map, int waterTableHeight) {
    for (int r = 0; r < map.getWidth(); r++) {
      for (int c = 0; c < map.getHeight(); c++) {
        if (map.elevationMap[r][c] <= waterTableHeight) {
          map.terrainMap[r][c] = TerrainType.WATER;
        }
      }
    }
  }

  private static void flatten(WorldMap map, int checkRadius) {
    for (int r = 0; r < map.getWidth(); r++) {
      for (int c = 0; c < map.getHeight(); c++) {
        if (map.terrainMap[r][c] != TerrainType.WATER) {
          flattenHelper(map, checkRadius, r, c);
        }
      }
    }
  }

  /**
   * Reduce the number of standout bumps / jaggedness by reducing number of tiny hills (mini-maxes).
   * New value of point will be an average of nearby valid (TerrainType.GROUND) neighbors.
   * <p>
   * If there are no valid neighbors to find the new terrain height, then we must be surrounded by
   * water and convert the terrain into water and take the average depth of water neighbors.
   */
  private static void flattenHelper(
      WorldMap map,
      int checkRadius,
      int r,
      int c) {
    int i, j;
    int numValidNeighbors = 0;
    int numNeighborsLower = 0;
    for (int dR = -checkRadius; dR <= checkRadius; dR++) {
      for (int dC = -checkRadius; dC <= checkRadius; dC++) {
        i = r + dR;
        j = c + dC;
        if (ifInvalidIndex(i, j, map.getWidth(), map.getHeight()) || (dR == 0 && dC == 0)) {
          continue;
        }
        if (map.terrainMap[i][j] != TerrainType.WATER) {
          numValidNeighbors++;
          if (map.elevationMap[i][j] < map.elevationMap[r][c]) {
            numNeighborsLower++;
          }
        }
      }
    }

    // only flatten if most of neighbors are lower
    if (numValidNeighbors > 0 && (numNeighborsLower / numValidNeighbors < 0.5)) {
      return;
    }
    numValidNeighbors = 0;
    int validNeighborsHeightSum = 0;
    int allNeighborsHeightSum = 0;
    int numImmediateNeighbors = 0;
    for (int dR = -1; dR <= 1; dR++) {
      for (int dC = -1; dC <= 1; dC++) {
        i = r + dR;
        j = c + dC;
        if (ifInvalidIndex(i, j, map.getWidth(), map.getHeight()) || (i == r && j == c)) {
          continue;
        }
        numImmediateNeighbors++;
        if (map.terrainMap[i][j] != TerrainType.WATER) {
          numValidNeighbors++;
          validNeighborsHeightSum += map.elevationMap[i][j];
        }
      }
    }
    if (numValidNeighbors > 0) {
      map.elevationMap[r][c] = validNeighborsHeightSum / numValidNeighbors;
    } else {
      // surrounded by water, will update terrain type to avoid one-block islands
      map.elevationMap[r][c] = allNeighborsHeightSum / numImmediateNeighbors;
      map.terrainMap[r][c] = TerrainType.WATER;
    }

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
        if (ifInvalidIndex(i, j, terrainElevation.length, terrainElevation[0].length)
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

  private static boolean ifInvalidIndex(int r, int c, int maxR, int maxC) {
    return r < 0 || c < 0 || r >= maxR || c >= maxC;
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
