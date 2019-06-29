package world;

import entity.Entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class WorldMap {
  private final String mMapId;
  public int[][] terrainMap;
  public int[][] elevationMap;
  public Entity[][] entityMap;
  private LinkedList<Entity> entityList;

  private int mMaxHeight;

  public WorldMap(String mapId, int[][] terrainMap, int[][] elevationMap) {
    mMapId = mapId;
    this.terrainMap = terrainMap;
    this.elevationMap = elevationMap;
    this.entityMap = new Entity[terrainMap.length][terrainMap[0].length];

    entityList = new LinkedList<>();
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


  public boolean addEntity(Entity entity, int r, int c) {
    if (entityMap[r][c] == null) {
      entityMap[r][c] = entity;
      entityList.add(entity);
      return true;
    }
    return false;
  }

  public void updateEntities() {
    LinkedList<Entity> entitiesToRemove = new LinkedList<>();
    for (Entity entity : entityList) {
      entity.update();
      if(entity.getHealth() < 0) {
        entitiesToRemove.add(entity);
      }
    }

    for(Entity entity : entitiesToRemove) {
      removeEntity(entity);
    }
  }

  public void removeEntity(Entity entity) {
    if (entityList.remove(entity)) {
      entityMap[entity.getCurR()][entity.getCurC()] = null;
    }
  }
}
