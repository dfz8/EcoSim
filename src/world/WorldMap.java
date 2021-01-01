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

  public final LinkedList<Entity> entityList;
  public final LinkedList<Entity> nonOccupyingEntityList = new LinkedList<>();
  public final LinkedList<Entity> occupyingEntityList = new LinkedList<>();

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
    LinkedList<Entity> entities = getEntitiesAtPosition(entity.getCurR(), entity.getCurC());

    // check if valid space for this type of entity
    boolean isSpaceOccupying = entity instanceof Traits.SpaceOccupying;
    boolean canAdd = true;
    for (Entity e : entities) {
      if (isSpaceOccupying == e instanceof Traits.SpaceOccupying) {
        canAdd = false;
        break;
      }
    }
    if (!canAdd) {
      return false;
    }

    if (isNewborn) {
      return newbornsList.add(entity);
    }
    if (entity instanceof Traits.SpaceOccupying) {
      occupyingEntityList.add(entity);
    } else {
      nonOccupyingEntityList.add(entity);
    }
    entityList.add(entity);
    return true;
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
    if (entity instanceof Traits.SpaceOccupying) {
      occupyingEntityList.remove(entity);
    } else {
      nonOccupyingEntityList.remove(entity);
    }
  }

  public LinkedList<Entity> getEntitiesAtPosition(int r, int c) {
    LinkedList<Entity> entities = new LinkedList<>();
    for (Entity e : nonOccupyingEntityList) {
      if (e.getCurR() == r && e.getCurC() == c) {
        entities.add(e);
      }
    }
    for (Entity e : occupyingEntityList) {
      if (e.getCurR() == r && e.getCurC() == c) {
        entities.add(e);
      }
    }
    return entities;
  }

  public boolean hasEmptySpace(int r, int c) {
    for (Entity e : getEntitiesAtPosition(r, c)) {
      if (e instanceof Traits.SpaceOccupying) {
        return false;
      }
    }
    return true;
  }
}
