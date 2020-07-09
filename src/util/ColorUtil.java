package src.util;

import java.awt.*;
import java.util.HashMap;

public class ColorUtil {

  private static HashMap<Color, HashMap<Color, HashMap<Float, Color>>> existingColors;

  public static Color interpolate(Color startColor, Color endColor, float p) {
    p = round(p);
    if (existingColors == null) {
      existingColors = new HashMap<>();
    }

    Color computedColor =
        existingColors.computeIfAbsent(
            startColor,
            startColorKey -> new HashMap<>())
                      .computeIfAbsent(endColor, endColorKey -> new HashMap<>())
                      .get(p);

    if (computedColor == null) {
      final float inv = 1 - p;
      existingColors.get(startColor).get(endColor).put(p, new Color(
          (int) (inv * startColor.getRed() + p * endColor.getRed()),
          (int) (inv * startColor.getGreen() + p * endColor.getGreen()),
          (int) (inv * startColor.getBlue() + p * endColor.getBlue())));
    }
    return existingColors.get(startColor).get(endColor).get(p);
  }

  public static Color interpolate(Color startColor, Color midColor, Color endColor, float p) {
    if (p > 0.5) {
      return interpolate(midColor, endColor, 2 * p - 1);
    }
    return interpolate(startColor, midColor, 2 * p);
  }

  private static float round(float f) {
    return ((int) (10000 * f)) / 10000f;
  }
}
