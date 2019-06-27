package world;

import entity.Entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WorldMap {
  private final String mMapId;
  int[][] terrainMap;
  int[][] elevationMap;
  Entity[][] entityMap;

  private int mMaxHeight;

  public WorldMap(String mapId, int[][] terrainMap, int[][] elevationMap) {
    mMapId = mapId;
    this.terrainMap = terrainMap;
    this.elevationMap = elevationMap;
    this.entityMap = new Entity[terrainMap.length][terrainMap[0].length];

    updateMetadata();
  }

  private void updateMetadata() {
    for (int r = 0; r < getWidth(); r++) {
      for (int c = 0; c < getHeight(); c++) {
        if (elevationMap[r][c] > mMaxHeight) {
          mMaxHeight = elevationMap[r][c];
        }
      }
    }
  }

  public int getWidth() {
    return terrainMap.length;
  }

  public int getHeight() {
    return terrainMap[0].length;
  }

  public int getMaxHeight() {
    return mMaxHeight;
  }

  public void save() {
    try {
      PrintWriter out = new PrintWriter(new FileWriter(new File(mMapId + ".txt")));

      out.println(getWidth() + " " + getHeight());
      for (int r = 0; r < getWidth(); r++) {
        for (int c = 0; c < getHeight(); c++) {
          out.print(elevationMap[r][c] + " ");
        }
        out.println();
      }
      out.close();
    } catch (IOException e) {

    }
  }
}
