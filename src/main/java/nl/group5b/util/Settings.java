package nl.group5b.util;

import org.joml.Vector3f;

public class Settings {

    // Main window
    public static final String WINDOW_TITLE = "Group 5B - Braitenberg Vehicles";
    public static final int ANTI_ALIASING_SAMPLES = 4;
    public static final int DEFAULT_WINDOW_WIDTH = 1600;
    public static final int DEFAULT_WINDOW_HEIGHT = 720;

    // OpenGL
    public static final int VAO_POSITION_ATTR = 0;
    public static final int VAO_NORMAL_ATTR = 2;

    // Camera
    public static final float CAMERA_3P_HEIGHT_OFFSET = 0.8f;
    public static float VIEWPORT_FOV = 55;
    public static final float VIEWPORT_NEAR_PLANE = 0.1f;
    public static final float VIEWPORT_FAR_PLANE = 1000;
    public static float VIEWPORT_GAMMA_CORRECTION = 2.2f;
    public static final float SENSOR_GAMMA_CORRECTION = 0.5f;
    public static final Vector3f CAMERA_TOPDOWN_POSITION = new Vector3f(0, 55, 0);

    // Graphics
    public static float AMBIENT_LIGHT = 0.15f;
    public static final int SHADOW_MAP_RESOLUTION = 1024*16; // WARNING! If changed, this also changes in the shadow vertex shader!

    // Scene
    public static float SUN_BRIGHTNESS = 0.5f;
    public static Vector3f SUN_DEFAULT_POSITION = new Vector3f(0, 1, 0);
    public static Vector3f LAMP_DEFAULT_POSITION = new Vector3f(0, 1, 0);
    public static final Vector3f LAMP_DEFAULT_COLOUR = new Vector3f(1, 1, 0.5f);
    public static final Vector3f LAMP_DEFAULT_ATTENUATION = new Vector3f(1, 0.75f, 0.75f);
    public static final float ARENA_RADIUS = 25;
    public static final float ARENA_SPAWN_RADIUS = ARENA_RADIUS - 2;
    public static final float ARENA_WALL_OFFSET = 0.2f;

    // Controls
    public static final float CAMERA_MOUSE_SENSITIVITY = 0.2f;
    public static final float LAMP_MOVEMENT_SPEED = 5f;

    // Vehicle
    public static final float VEHICLE_SPEED = 1.5f;
    public static final float CONTROLLABLE_VEHICLE_SPEED = 1.5f * VEHICLE_SPEED;
    public static final float WHEEL_ROTATION_SPEED = VEHICLE_SPEED * 200;
    public static final float VEHICLE_ACCELERATION = 0.01f;
    public static final float VEHICLE_DECELERATION = 3;
    public static final float VEHICLE_CLAMP_SPEED = 0.001f;
    public static final float VEHICLE_STEERING_FACTOR = 2 * 1.3f;
    public static final int SENSOR_RESOLUTION = 480;
    public static final float SENSOR_FOV = 90;

    // GUI
    public static final int GUI_GRAPH_HISTORY_SIZE = 500;
    public static final float PANEL_WIDTH_MAIN = 300;
    public static final float PANEL_WIDTH_SETTINGS = 200;
    public static final float POPUP_WIDTH = 300;

}

