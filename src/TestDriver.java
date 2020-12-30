package src;

import src.world.TerrainGenerator;
import src.world.WorldMap;
import src.world.WorldVisualizer;

public class TestDriver {

  public static void main(String[] args) {
    WorldMap map = TerrainGenerator.createMap("world1", 100, 100);
    map.save();
    WorldVisualizer.visualize(map);
  }

}
