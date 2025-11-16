package net.spookygames.gdx.sfx.desktop;

import com.badlogic.gdx.Gdx;

/**
 * Legacy shim that used to prepare native audio duration resolvers. The new
 * implementation does not require any special setup but keeping the call in
 * place avoids touching platform code.
 */
public final class DesktopAudioDurationResolver {
    private DesktopAudioDurationResolver() {
    }

    public static void initialize() {
        if (Gdx.app != null) {
            Gdx.app.log("DesktopAudioDurationResolver", "Initialized");
        }
    }
}
