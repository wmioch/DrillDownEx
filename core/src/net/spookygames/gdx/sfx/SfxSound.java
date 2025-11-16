package net.spookygames.gdx.sfx;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

/**
 * Minimal {@link Sound} wrapper so the game can keep the original public API
 * from the gdx-sfx project without relying on the removed git submodule.
 */
public class SfxSound implements Sound, Disposable {
    private final Sound delegate;

    public SfxSound(Sound delegate) {
        this.delegate = delegate;
    }

    public Sound getDelegate() {
        return delegate;
    }

    @Override
    public long play() {
        return delegate.play();
    }

    @Override
    public long play(float volume) {
        return delegate.play(volume);
    }

    @Override
    public long play(float volume, float pitch, float pan) {
        return delegate.play(volume, pitch, pan);
    }

    @Override
    public long loop() {
        return delegate.loop();
    }

    @Override
    public long loop(float volume) {
        return delegate.loop(volume);
    }

    @Override
    public long loop(float volume, float pitch, float pan) {
        return delegate.loop(volume, pitch, pan);
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public void pause() {
        delegate.pause();
    }

    @Override
    public void resume() {
        delegate.resume();
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    @Override
    public void stop(long soundId) {
        delegate.stop(soundId);
    }

    @Override
    public void pause(long soundId) {
        delegate.pause(soundId);
    }

    @Override
    public void resume(long soundId) {
        delegate.resume(soundId);
    }

    @Override
    public void setLooping(long soundId, boolean looping) {
        delegate.setLooping(soundId, looping);
    }

    @Override
    public void setPitch(long soundId, float pitch) {
        delegate.setPitch(soundId, pitch);
    }

    @Override
    public void setVolume(long soundId, float volume) {
        delegate.setVolume(soundId, volume);
    }

    @Override
    public void setPan(long soundId, float pan, float volume) {
        delegate.setPan(soundId, pan, volume);
    }
}
