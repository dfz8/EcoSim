package src.util;

import src.world.WorldMap;

import java.util.LinkedList;

public class WorldUtil {

  /**
   * Return list of coordinates of tiles that are valid coordinates on the map.
   */
  public static LinkedList<Vec2d> getValidNearbySquares(
      WorldMap worldMap,
      int curR,
      int curC,
      int searchRadius) {

    LinkedList<Vec2d> results = new LinkedList<>();

    for (int r = curR - searchRadius; r <= curR + searchRadius; r++) {
      for (int c = curC - searchRadius; c <= curC + searchRadius; c++) {
        if (!ifInvalidIndex(r, c, worldMap.getWidth(), worldMap.getHeight())
            && (r != curR && c != curC)) {
          results.add(new Vec2d(r, c));
        }
      }
    }
    return results;
  }

  public static boolean ifInvalidIndex(int r, int c, int maxR, int maxC) {
    return r < 0 || c < 0 || r >= maxR || c >= maxC;
  }
}
