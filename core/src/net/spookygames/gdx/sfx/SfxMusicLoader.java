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
 * Simple music loader compatible with the legacy gdx-sfx API.
 */
public class SfxMusicLoader extends AsynchronousAssetLoader<SfxMusic, SfxMusicLoader.SfxMusicParameter> {
    public static class SfxMusicParameter extends AssetLoaderParameters<SfxMusic> {
    }

    public SfxMusicLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, SfxMusicParameter parameter) {
        // Nothing to do asynchronously.
    }

    @Override
    public SfxMusic loadSync(AssetManager manager, String fileName, FileHandle file, SfxMusicParameter parameter) {
        FileHandle resolved = resolve(fileName);
        return new SfxMusic(Gdx.audio.newMusic(resolved));
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SfxMusicParameter parameter) {
        return null;
    }
}
