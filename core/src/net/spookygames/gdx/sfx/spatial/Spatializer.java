package net.spookygames.gdx.sfx.spatial;

/**
 * Applies panning and attenuation to a spatialized sound instance.
 */
public interface Spatializer<T> {
    void spatialize(SpatializedSound<T> instance, float nominalVolume);
}
