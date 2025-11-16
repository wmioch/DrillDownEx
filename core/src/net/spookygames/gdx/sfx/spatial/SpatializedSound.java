package net.spookygames.gdx.sfx.spatial;

/**
 * Represents an actively playing sound that can be spatialized.
 */
public interface SpatializedSound<T> {
    T getPosition();

    void setPan(float pan, float volume);
}
