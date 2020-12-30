package world;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TerrainType {
  public static final int GROUND = 0;

  public static final int WATER = -1;

  private static Map<Integer, Color> mColorMap;

  public static Color getColorForType(int terrain) {
    if (mColorMap == null) {
      mColorMap = new HashMap<>();
    }
    if (!mColorMap.containsKey(terrain)) {
      addColorToColorMap(terrain);
    }
    return mColorMap.get(terrain);
  }

  private static void addColorToColorMap(int terrain) {
    switch (terrain) {
      case WATER:
        mColorMap.put(terrain, Color.CYAN);
        break;
      case GROUND:
      default:
        mColorMap.put(terrain, Color.WHITE);
    }
  }

}
