package nl.group5b.engine;

import nl.group5b.gui.Element;
import nl.group5b.gui.GUI;
import nl.group5b.gui.elements.MainPanel;
import nl.group5b.gui.elements.SettingsPanel;
import nl.group5b.light.Light;
import nl.group5b.model.Body;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.models.*;
import nl.group5b.util.Settings;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        // Initialize the window
        DisplayBuilder.init();

        // Store the window handle
        long window = DisplayBuilder.window;

        // Create ModelLoader instance
        ModelLoader modelLoader = new ModelLoader();

        // Create sun
        Light sun = new Light(
                new Vector4f(Settings.SUN_X, Settings.SUN_Y, Settings.SUN_Z, 0),
                new Vector3f(Settings.SUN_BRIGHTNESS, Settings.SUN_BRIGHTNESS, Settings.SUN_BRIGHTNESS)
        );

        // Create the Bodies
        Arena arena = new Arena(modelLoader);

        ControllableLamp controllableLamp = new ControllableLamp(modelLoader, new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 0.5f), new Vector3f(1, 0.75f, 0.75f));

        AttachableLamp attachableLamp = new AttachableLamp(modelLoader, new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 0.5f), new Vector3f(1, 0.75f, 0.75f));

        // Load bodies (except Arena) into list
        List<Body> bodies = new ArrayList<>(List.of(
                arena
        ));

        // Load lights into array (has to be an array with predefined length)
        // IMPORTANT! Sun HAS to be present at index 0 ======================
        Light[] lights = new Light[3 + Settings.DYNAMIC_LIGHT_COUNT];
        lights[0] = sun;
        lights[1] = attachableLamp.getLight();
        lights[2] = controllableLamp.getLight();

        // Create an array of all lamps
        Lamp[] lamps = new Lamp[2 + Settings.DYNAMIC_LIGHT_COUNT];
        lamps[0] = attachableLamp;
        lamps[1] = controllableLamp;

        // Add the rest of the lamps (we start at i = 3 because the first 3 lamps are already added)
        for (int i = 3; i < 3 + Settings.DYNAMIC_LIGHT_COUNT; i++) {
            Lamp lamp = new Lamp(modelLoader, new Vector3f(0, 1.5f, 0),
                    new Vector3f(1, 1, 0.5f), new Vector3f(1, 0.75f, 0.75f));
            lamps[i-1] = lamp;
            lights[i] = lamp.getLight();
        }

        // Create GUI Elements
        MainPanel mainPanel = new MainPanel(window, modelLoader, bodies, lamps);
        SettingsPanel settingsPanel = new SettingsPanel(sun);

        // Load GUI Elements into array
        Element[] elements = {
                mainPanel,
                settingsPanel
        };

        // Create GUI
        GUI gui = new GUI(window, elements);

        // Create MasterRenderer instance
        MasterRenderer renderer = new MasterRenderer(bodies, lights, gui, window);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!GLFW.glfwWindowShouldClose(window)) {
            // Move all engine components that need to be moved (e.g. camera, bodies, etc.)
            renderer.move();

            // Render scene
            renderer.render();
        }

        // Clean up renderer
        renderer.cleanUp();

        // Remove all VAOs and VBOs
        modelLoader.cleanUp();

        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }

}