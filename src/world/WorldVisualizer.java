package world;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WorldVisualizer {
  static int screenWidth = 500;
  static int screenHeight = 500;

  private static class WorldPanel extends JPanel {

    private BufferedImage mImage;
    private Graphics mGraphics;
    private WorldMap mMap;

    private WorldPanel(WorldMap map) {
      mImage = new BufferedImage(
          screenWidth,
          screenHeight,
          BufferedImage.TYPE_INT_RGB);
      mGraphics = mImage.getGraphics();
      mGraphics.setColor(Color.WHITE);
      mGraphics.fillRect(0, 0, screenWidth, screenHeight);
      mMap = map;
    }

    public void paintComponent(Graphics g) {
      g.drawImage(mImage, 0, 0, screenWidth, screenHeight, null);
    }

    public void drawMap() {

      int numR = mMap.getWidth();
      int numC = mMap.getHeight();

      final int minXBuffer = 25;
      final int minYBuffer = 25;
      final int xWidth = (screenWidth - 2 * minXBuffer) / numR;
      final int yWidth = (screenHeight - 2 * minYBuffer) / numC;

      int xBuffer = (screenWidth - xWidth * numR) / 2;
      int yBuffer = (screenHeight - yWidth * numC) / 2;

      for (int r = 0; r < numR; r++) {
        for (int c = 0; c < numC; c++) {
          setColorForTile(r, c);
          mGraphics.fillRect(xBuffer + r * xWidth, yBuffer + c * yWidth, xWidth, yWidth);
        }
      }
    }

    private void setColorForTile(int r, int c) {
      float alpha = 0.75f * mMap.elevationMap[r][c] / mMap.getMaxHeight();
      Color terrainColor = TerrainType.getColorForType(mMap.terrainMap[r][c]);
      mGraphics.setColor(new Color(
          (int) (alpha * terrainColor.getRed()),
          (int) (alpha * terrainColor.getGreen()),
          (int) (alpha * terrainColor.getBlue())));
    }
  }


  public static void visualize(WorldMap worldMap) {
    //init viewing window
    JFrame playWindow = new JFrame("World Visualizer");
    playWindow.setSize(screenWidth + 8, screenHeight + 34);
    playWindow.setLocation(100, 100);
    playWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    WorldPanel panel = new WorldPanel(worldMap);
    playWindow.setContentPane(panel);
    playWindow.setVisible(true);
    playWindow.setResizable(false);

    panel.drawMap();
  }
}
