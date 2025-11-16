package net.spookygames.gdx.sfx.spatial;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.LongMap;

import net.spookygames.gdx.sfx.SfxSound;

/**
 * Replacement for the original gdx-sfx spatial sound player with a minimal
 * feature set tailored to DrillDown's needs.
 */
public class FadingSpatializedSoundPlayer<T> {
    private final LongMap<SoundInstance<T>> instances = new LongMap<>();
    float volume = 1f;
    private float fadeTime = 0.15f;
    private Spatializer<T> spatializer;

    public Spatializer<T> getSpatializer() {
        return spatializer;
    }

    public void setSpatializer(Spatializer<T> spatializer) {
        this.spatializer = spatializer;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = MathUtils.clamp(volume, 0f, 1f);
    }

    public void setFadeTime(float fadeTime) {
        this.fadeTime = Math.max(0f, fadeTime);
    }

    public float getFadeTime() {
        return fadeTime;
    }

    public long play(T position, SfxSound sound, float pitch, boolean looping) {
        return play(position, sound, pitch, looping, false);
    }

    public long play(T position, SfxSound sound, float pitch, boolean looping, boolean fadeIn) {
        if (sound == null) {
            return -1;
        }
        long soundId = sound.play(fadeIn && fadeTime > 0f ? 0f : volume, pitch, 0f);
        if (soundId == -1) {
            return -1;
        }
        sound.setLooping(soundId, looping);
        SoundInstance<T> instance = new SoundInstance<>(sound, soundId, position);
        if (fadeIn && fadeTime > 0f) {
            instance.fade = 0f;
            instance.targetFade = 1f;
        }
        instances.put(soundId, instance);
        return soundId;
    }

    public void pause(long soundId) {
        SoundInstance<T> instance = instances.get(soundId);
        if (instance == null) {
            return;
        }
        instance.paused = true;
        instance.sound.pause(soundId);
    }

    public void resume(long soundId) {
        SoundInstance<T> instance = instances.get(soundId);
        if (instance == null) {
            return;
        }
        instance.paused = false;
        instance.sound.resume(soundId);
    }

    public void stop(long soundId) {
        SoundInstance<T> instance = instances.get(soundId);
        if (instance == null) {
            return;
        }
        if (fadeTime <= 0f) {
            instance.stopImmediate();
            instances.remove(soundId);
        } else {
            instance.targetFade = 0f;
            instance.stopWhenSilent = true;
        }
    }

    public void stop() {
        LongArray ids = new LongArray(instances.size);
        for (LongMap.Entry<SoundInstance<T>> entry : instances.entries()) {
            ids.add(entry.key);
            entry.value.stopImmediate();
        }
        for (int i = 0; i < ids.size; i++) {
            instances.remove(ids.get(i));
        }
    }

    public void update(float delta) {
        if (instances.size == 0) {
            return;
        }
        LongArray removals = null;
        for (LongMap.Entry<SoundInstance<T>> entry : instances.entries()) {
            SoundInstance<T> instance = entry.value;
            if (instance == null) {
                continue;
            }
            if (instance.updateFade(delta, fadeTime)) {
                if (removals == null) {
                    removals = new LongArray();
                }
                removals.add(entry.key);
                continue;
            }
            if (!instance.paused) {
                if (spatializer != null) {
                    spatializer.spatialize(instance, volume);
                } else {
                    instance.setPan(0f, volume);
                }
            }
        }
        if (removals != null) {
            for (int i = 0; i < removals.size; i++) {
                instances.remove(removals.get(i));
            }
        }
    }

    private static final class SoundInstance<T> implements SpatializedSound<T> {
        private final SfxSound sound;
        private final long id;
        private final T position;
        private float fade = 1f;
        private float targetFade = 1f;
        private float lastPan;
        private float lastVolume = 1f;
        private boolean stopWhenSilent;
        private boolean paused;

        SoundInstance(SfxSound sound, long id, T position) {
            this.sound = sound;
            this.id = id;
            this.position = position;
        }

        @Override
        public T getPosition() {
            return position;
        }

        @Override
        public void setPan(float pan, float volume) {
            lastPan = MathUtils.clamp(pan, -1f, 1f);
            lastVolume = MathUtils.clamp(volume, 0f, 1f);
            applyVolume();
        }

        private void applyVolume() {
            sound.setPan(id, lastPan, lastVolume * fade);
        }

        boolean updateFade(float delta, float fadeTime) {
            if (fadeTime <= 0f) {
                fade = targetFade;
            } else if (!MathUtils.isEqual(fade, targetFade)) {
                float change = delta / fadeTime;
                if (fade < targetFade) {
                    fade = Math.min(targetFade, fade + change);
                } else {
                    fade = Math.max(targetFade, fade - change);
                }
                applyVolume();
            }
            if (stopWhenSilent && MathUtils.isEqual(fade, 0f)) {
                stopImmediate();
                return true;
            }
            return false;
        }

        void stopImmediate() {
            sound.stop(id);
        }
    }
}
