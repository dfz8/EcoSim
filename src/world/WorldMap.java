package world;

import entity.api.Entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class WorldMap {
  private final String mMapId;
  public int[][] terrainMap;
  public int[][] elevationMap;
  public LinkedList<Entity> entityList;

  private int mMaxHeight;
  private LinkedList<Entity> queuedUpAdditions;
  private boolean isInUpdate;

  public WorldMap(String mapId, int[][] terrainMap, int[][] elevationMap) {
    mMapId = mapId;
    this.terrainMap = terrainMap;
    this.elevationMap = elevationMap;
    entityList = new LinkedList<>();
    queuedUpAdditions = new LinkedList<>();
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


  /**
   * Adds {@param entity} to the map if the location is not currently occupied.
   *
   * @return if the entity was successfully added or not.
   */
  public boolean addEntity(Entity entity, int r, int c) {
    if (isInUpdate) {
      for (Entity e : queuedUpAdditions) {
        if (e.getCurR() == r && e.getCurC() == c) {
          return false;
        }
      }
    }
    if (getEntityAtPosition(r, c) != null) {
      return false;
    }

    if (isInUpdate) {
      queuedUpAdditions.add(entity);
    } else {
      entityList.add(entity);
    }
    return true;
  }

  public void updateEntities() {
    isInUpdate = true;
    LinkedList<Entity> entitiesToRemove = new LinkedList<>();
    for (Entity entity : entityList) {
      entity.update();
      if (entity.getHealth() < 0) {
        entitiesToRemove.add(entity);
      }
    }

    for (Entity entity : entitiesToRemove) {
      removeEntity(entity);
    }
    isInUpdate = false;

    for (Entity entity : queuedUpAdditions) {
      addEntity(entity, entity.getCurR(), entity.getCurC());
    }
    queuedUpAdditions.clear();
  }

  public void removeEntity(Entity entity) {
    entityList.remove(entity);
  }

  public Entity getEntityAtPosition(int r, int c) {
    for (Entity e : entityList) {
      if (e.getCurR() == r && e.getCurC() == c) {
        return e;
      }
    }
    return null;
  }

  public boolean hasEntityAtPosition(int r, int c) {
    return getEntityAtPosition(r, c) != null;
  }
}
