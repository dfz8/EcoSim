package entity.api;

public interface Traits {

  // Entity can only move across other land tiles
  interface Terrestrial {}

  // Entity consumes the space it occupies, not allowing other entities to move onto its space
  interface SpaceOccupying {
  }
}
