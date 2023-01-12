package com.eloraam.redpower.core;

import java.io.File;
import java.io.InputStream;

import com.eloraam.redpower.RedPowerCore;
import cpw.mods.fml.common.Loader;

public class Config {
    private static File configDir;
    private static File configFile;
    private static TagFile config;

    public static void loadConfig() {
        config = new TagFile();
        InputStream is
            = RedPowerCore.class.getResourceAsStream("/assets/rpcore/default.cfg");
        config.readStream(is);
        if (configDir == null) {
            File file = Loader.instance().getConfigDir();
            configDir = file;
            configFile = new File(file, "redpower.cfg");
        }

        if (configFile.exists()) {
            config.readFile(configFile);
        }

        config.commentFile("RedPower 2 Configuration");
    }

    public static void saveConfig() {
        config.saveFile(configFile);
    }

    public static int getInt(String name) {
        return config.getInt(name);
    }

    public static int getInt(String name, int _default) {
        return config.getInt(name, _default);
    }

    public static String getString(String name) {
        return config.getString(name);
    }

    public static String getString(String name, String _default) {
        return config.getString(name, _default);
    }
}
