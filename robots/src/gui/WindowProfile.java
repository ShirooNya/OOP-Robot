package gui;

import log.Logger;

import java.io.*;
import java.util.List;

public class WindowProfile {
    private static final String PROFILE_FILE = "window_profile.dat";

    public static class WindowState implements Serializable {
        private static final long serialVersionUID = 1L;
        public String type; // "main" или "internal"
        public String title;
        public int x, y, width, height;
        public boolean isIcon;
        public boolean isMaximum;
        public int extendedState; // только для главного окна

        public WindowState(String type, String title, int x, int y, int width, int height,
                           boolean isIcon, boolean isMaximum, int extendedState) {
            this.type = type;
            this.title = title;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.isIcon = isIcon;
            this.isMaximum = isMaximum;
            this.extendedState = extendedState;
        }
    }

    public static void saveProfile(List<WindowState> states) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROFILE_FILE))) {
            oos.writeObject(states);
        } catch (IOException e) {
            Logger.error("Failed to save window profile: " + e.getMessage());
        }
    }

    public static List<WindowState> loadProfile() {
        File file = new File(PROFILE_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PROFILE_FILE))) {
            return (List<WindowState>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Logger.error("Failed to load window profile: " + e.getMessage());
            return null;
        }
    }

    public static boolean hasSavedProfile() {
        return new File(PROFILE_FILE).exists();
    }
}