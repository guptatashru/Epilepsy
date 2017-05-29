package com.hp.epilepsy.hardware.camera;

import android.content.Context;

public interface ISurfaceWatcher {
    void surfaceHasBeenCreated();

    Context getContext();
}
