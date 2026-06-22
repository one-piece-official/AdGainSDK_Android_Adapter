package com.adgain.unified;

import android.content.Context;

public class NoOpInitializer implements PlatformInitializer {
    @Override
    public void init(Context context) {
        // Placeholder for platforms that have not been wired yet.
    }
}
