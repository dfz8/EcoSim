package world;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

public class WorldVisualizer {
  private static int screenWidth = 500;
  private static int screenHeight = 500;

  private static class WorldPanel extends JPanel implements ChangeListener, ItemListener {

    private BufferedImage mImage;
    private Graphics mGraphics;
    private WorldMap mMap;

    private final int xBlockWidth;
    private final int yBlockWidth;
    private final int xStartBuffer;
    private final int yStartBuffer;

    private JSlider elevationSliceSlider;
    private JCheckBox seeAllBelowCheckBox;

    private WorldPanel(WorldMap map) {
      mImage = new BufferedImage(
          screenWidth,
          screenHeight,
          BufferedImage.TYPE_INT_RGB);
      mGraphics = mImage.getGraphics();
      mGraphics.setColor(Color.WHITE);
      mGraphics.fillRect(0, 0, screenWidth, screenHeight);
      mMap = map;

      int sliderHeight = 20;
      int numR = mMap.getWidth();
      int numC = mMap.getHeight();
      xBlockWidth = (screenWidth - 2 * 25) / numR;
      yBlockWidth = (screenHeight - 2 * 25 - sliderHeight) / numC;
      xStartBuffer = (screenWidth - xBlockWidth * numR) / 2;
      yStartBuffer = (screenHeight - yBlockWidth * numC) / 2 + sliderHeight;

      elevationSliceSlider = new JSlider(0, mMap.getMaxHeight());
      elevationSliceSlider.setValue(mMap.getMaxHeight());
      elevationSliceSlider.setMajorTickSpacing(5);
      elevationSliceSlider.setPaintLabels(true);
      elevationSliceSlider.addChangeListener(this);

      seeAllBelowCheckBox = new JCheckBox("See all below");
      seeAllBelowCheckBox.setSelected(true);
      seeAllBelowCheckBox.addItemListener(this);

      add(elevationSliceSlider);
      add(seeAllBelowCheckBox);
    }

    public void paintComponent(Graphics g) {
      g.drawImage(mImage, 0, 0, screenWidth, screenHeight, null);
    }

    private void drawMap() {
      for (int r = 0; r < mMap.getWidth(); r++) {
        for (int c = 0; c < mMap.getHeight(); c++) {
          drawBlock(r, c);
        }
      }
    }

    private void drawSlice(int elevation) {
      mGraphics.setColor(Color.WHITE);
      mGraphics.fillRect(0, 0, screenWidth, screenHeight);
      for (int r = 0; r < mMap.getWidth(); r++) {
        for (int c = 0; c < mMap.getHeight(); c++) {

          if (seeAllBelowCheckBox.isSelected()
              ? mMap.elevationMap[r][c] <= elevation
              : mMap.elevationMap[r][c] == elevation) {
            drawBlock(r, c);
          }
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

    private void drawBlock(int r, int c) {
      setColorForTile(r, c);
      mGraphics.fillRect(
          xStartBuffer + r * xBlockWidth,
          yStartBuffer + c * yBlockWidth,
          xBlockWidth,
          yBlockWidth);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      drawSlice(elevationSliceSlider.getValue());
      repaint();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
      stateChanged(null);
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
