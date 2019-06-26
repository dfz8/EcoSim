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

    return new WorldMap(id, terrainType, terrainElevation);
  }

  private static void smooth(int[][] terrainElevation, int r, int c, int dESmoothAmount) {
    // roughly simulate grain of sand distribution: adding to one point will redistribute some to
    // nearby tiles. The further the tiles are away from the source, the less they will receive.

    int i, j, ddE;
    for (int dR = -dESmoothAmount; dR <= dESmoothAmount; dR++) {
      for (int dC = -dESmoothAmount; dC <= dESmoothAmount; dC++) {
        i = r + dR;
        j = c + dC;
        if (i < 0 || j < 0 || i >= terrainElevation.length || j >= terrainElevation[0].length) {
          continue;
        } else if (dR == 0 && dC == 0) {
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
