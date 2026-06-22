package com.adgain.unified;

import java.util.Collections;
import java.util.Map;

public class AdTypeEntry {
    public final String type;
    public final String title;
    public final String targetClassName;
    public final Map<String, String> extras;

    public AdTypeEntry(String type, String title, String targetClassName) {
        this(type, title, targetClassName, Collections.<String, String>emptyMap());
    }

    public AdTypeEntry(String type, String title, String targetClassName, Map<String, String> extras) {
        this.type = type;
        this.title = title;
        this.targetClassName = targetClassName;
        this.extras = extras == null ? Collections.<String, String>emptyMap() : extras;
    }
}
