package nl.group5b.util;

public class Settings {

    // Main window
    public static final String WINDOW_TITLE = "Group 5B - Braitenberg Vehicles";
    public static final int REFRESH_RATE = 60;

    // OpenGL
    public static final int VAO_POSITION_ATTR = 0;
    public static final int VAO_NORMAL_ATTR = 2;

    // Camera
    public static final float VIEWPORT_FOV = 55;
    public static final float VIEWPORT_NEAR_PLANE = 0.1f;
    public static final float VIEWPORT_FAR_PLANE = 1000;

    // Graphics
    public static final float AMBIENT_LIGHT = 0.15f;
    public static final int SHADOW_MAP_RESOLUTION = 1024*16; // WARNING! If changed, this also changes in the shadow vertex shader!
    public static final float SHADOW_BOX_RADIUS = 10;

    // Scene
    public static final float SUN_BRIGHTNESS = 0.5f;

    // Controls
    public static final float CAMERA_MOUSE_SENSITIVITY = 0.2f;

    // Vehicle
    public static final float VEHICLE_SPEED = 1.5f;
    public static final float WHEEL_ROTATION_SPEED = VEHICLE_SPEED * 200;
    public static final float VEHICLE_ACCELERATION = 0.5f;
    public static final float VEHICLE_DECELERATION = 3;
    public static final float VEHICLE_CLAMP_SPEED = 0.001f;
    public static final float VEHICLE_STEERING_FACTOR = 1.3f;
    public static final int SENSOR_RESOLUTION = 100;
    public static final float SENSOR_FOV = 25;

    // GUI
    public static final int GUI_GRAPH_HISTORY_SIZE = 500;
}