package world;

import entity.api.Entity;
import entity.api.Traits;

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
  private final LinkedList<Entity> newbornsList = new LinkedList<>();

  public WorldMap(String mapId, int[][] terrainMap, int[][] elevationMap) {
    mMapId = mapId;
    this.terrainMap = terrainMap;
    this.elevationMap = elevationMap;
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

  /**
   * Adds {@param entity} to the map if the location is not currently occupied.
   *
   * @return if the entity was successfully added or not.
   */
  public boolean addEntity(Entity entity, boolean isNewborn) {
    Entity e = getEntityAtPosition(entity.getCurR(), entity.getCurC());
    if (e != null && entity instanceof Traits.SpaceOccupying
        == e instanceof Traits.SpaceOccupying) {
      return false;
    }
    if (isNewborn) {
      return newbornsList.add(entity);
    }
    return entityList.add(entity);
  }

  public void updateEntities() {
    for (Entity e : entityList) {
      e.update();
    }

    for (Entity ne : newbornsList) {
      // no longer need to delay addition now that we aren't modifying the list
      addEntity(ne, false /* isNewborn */);
    }
    newbornsList.clear();
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

  public boolean hasEmptySpace(int r, int c) {
    Entity e = getEntityAtPosition(r, c);
    return !(e instanceof Traits.SpaceOccupying);
  }
}
