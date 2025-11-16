package net.spookygames.gdx.sfx;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

/**
 * Wrapper around {@link Music} to match the historical gdx-sfx public API.
 */
public class SfxMusic implements Music, Disposable {
    private final Music delegate;

    public SfxMusic(Music delegate) {
        this.delegate = delegate;
    }

    public Music getDelegate() {
        return delegate;
    }

    @Override
    public void play() {
        delegate.play();
    }

    @Override
    public void pause() {
        delegate.pause();
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public boolean isPlaying() {
        return delegate.isPlaying();
    }

    @Override
    public void setLooping(boolean isLooping) {
        delegate.setLooping(isLooping);
    }

    @Override
    public boolean isLooping() {
        return delegate.isLooping();
    }

    @Override
    public void setVolume(float volume) {
        delegate.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return delegate.getVolume();
    }

    @Override
    public void setPan(float pan, float volume) {
        delegate.setPan(pan, volume);
    }

    @Override
    public void setPosition(float position) {
        delegate.setPosition(position);
    }

    @Override
    public float getPosition() {
        return delegate.getPosition();
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        delegate.setOnCompletionListener(listener);
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }
}
