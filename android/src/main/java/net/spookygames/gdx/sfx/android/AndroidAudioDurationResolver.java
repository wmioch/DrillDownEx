package net.spookygames.gdx.sfx.android;

import android.util.Log;

/**
 * Placeholder to keep the existing initialization call compiling without the
 * external gdx-sfx dependency.
 */
public final class AndroidAudioDurationResolver {
    private static final String TAG = "AndroidAudioDurationResolver";

    private AndroidAudioDurationResolver() {
    }

    public static void initialize() {
        Log.d(TAG, "Initialized");
    }
}
