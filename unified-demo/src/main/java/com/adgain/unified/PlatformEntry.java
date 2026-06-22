package com.adgain.unified;

import java.util.List;

public class PlatformEntry {
    public final String id;
    public final String name;
    public final PlatformInitializer initializer;
    public final List<AdTypeEntry> adTypes;

    public PlatformEntry(String id, String name, PlatformInitializer initializer, List<AdTypeEntry> adTypes) {
        this.id = id;
        this.name = name;
        this.initializer = initializer;
        this.adTypes = adTypes;
    }
}
