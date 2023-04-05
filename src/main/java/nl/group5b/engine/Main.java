package nl.group5b.engine;

import nl.group5b.gui.Element;
import nl.group5b.gui.GUI;
import nl.group5b.gui.elements.MainPanel;
import nl.group5b.gui.elements.SettingsPanel;
import nl.group5b.light.Light;
import nl.group5b.model.Body;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.models.*;
import nl.group5b.shaders.RealShader;
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
                new Vector4f(Settings.SUN_DEFAULT_POSITION, 0),
                new Vector3f(Settings.SUN_BRIGHTNESS, Settings.SUN_BRIGHTNESS, Settings.SUN_BRIGHTNESS)
        );

        // Create the Bodies
        Arena arena = new Arena(modelLoader);

        Wall backWall = new Wall(modelLoader, new Vector3f(0, 0, 24.9f), new Vector3f(0, 0, 0) );
        Wall leftWall = new Wall(modelLoader, new Vector3f(-24.9f, 0, 0), new Vector3f(0, -90, 0) );
        Wall rightWall = new Wall(modelLoader, new Vector3f(24.9f, 0, 0), new Vector3f(0, 90, 0) );
        Wall frontWall = new Wall(modelLoader, new Vector3f(0, 0, -24.9f), new Vector3f(0, 180, 0) );

        Cloud cloud = new Cloud(modelLoader, new Vector3f(10, 10, 10), new Vector3f(0, 0, 0));

        // Load bodies (except Arena) into list
        List<Body> bodies = new ArrayList<>(List.of(
                arena,
                backWall,
                leftWall,
                rightWall,
                frontWall,
                cloud
        ));

        // Load sun into list
        // IMPORTANT! Sun HAS to be present at index 0 at build time ======================
        List<Light> lights = new ArrayList<>(List.of(
                sun
        ));

        // Create a RealShader instance
        // (This is necessary, because both the GUI and the renderer need access to the same shader)
        RealShader realShader = new RealShader(lights.size());

        // Create GUI Elements
        MainPanel mainPanel = new MainPanel(window, modelLoader, realShader, bodies, lights);
        SettingsPanel settingsPanel = new SettingsPanel(sun);

        // Load GUI Elements into array
        Element[] elements = {
                mainPanel,
                settingsPanel
        };

        // Create GUI
        GUI gui = new GUI(window, elements);

        // Create MasterRenderer instance
        MasterRenderer renderer = new MasterRenderer(bodies, lights, gui, window, realShader);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!GLFW.glfwWindowShouldClose(window)) {
            if (Settings.SIMULATE) {
                // Move all engine components that need to be moved (e.g. camera, bodies, etc.)
                renderer.move();
            }

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