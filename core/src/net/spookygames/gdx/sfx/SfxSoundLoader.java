package net.spookygames.gdx.sfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * Lightweight loader that mirrors the API shape of the original gdx-sfx
 * project.
 */
public class SfxSoundLoader extends AsynchronousAssetLoader<SfxSound, SfxSoundLoader.SfxSoundParameter> {
    public static class SfxSoundParameter extends AssetLoaderParameters<SfxSound> {
    }

    public SfxSoundLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, SfxSoundParameter parameter) {
        // No asynchronous pre-processing necessary. The sound is created in loadSync.
    }

    @Override
    public SfxSound loadSync(AssetManager manager, String fileName, FileHandle file, SfxSoundParameter parameter) {
        FileHandle resolved = resolve(fileName);
        return new SfxSound(Gdx.audio.newSound(resolved));
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SfxSoundParameter parameter) {
        return null;
    }
}
