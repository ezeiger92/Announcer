package com.chromaclypse.announcer;

import java.util.List;

import com.chromaclypse.api.Defaults;
import com.chromaclypse.api.config.ConfigObject;

public class Announcements extends ConfigObject {

    public String first_join = "Welcome, %name%!";
    public String regular_join = "";

    public List<Instance> announcements = Defaults.emptyList();

    public static class Instance {
        public String permission = "*";
        public int offset = 0;
        public int interval = 1200;
        public boolean random = false;
        public List<String> messages = Defaults.list("Mesasge 1", "Message 2");
    }
}
