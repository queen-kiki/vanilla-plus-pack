package dev.vanillaplus.tweaks;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** The pack version, baked into the jar at build time from pack.toml. */
public final class PackInfo {
    private static final String VERSION = load();

    private PackInfo() {
    }

    public static String version() {
        return VERSION;
    }

    private static String load() {
        try (InputStream in = PackInfo.class.getResourceAsStream("/vanillaplus_tweaks/pack.properties")) {
            if (in == null) return "?";
            Properties p = new Properties();
            p.load(in);
            return p.getProperty("pack_version", "?");
        } catch (IOException e) {
            return "?";
        }
    }
}
