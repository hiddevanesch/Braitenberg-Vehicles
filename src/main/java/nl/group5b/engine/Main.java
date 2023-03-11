package nl.group5b.engine;

import nl.group5b.model.*;
import nl.group5b.model.models.Arena;
import nl.group5b.model.models.Dragon;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.opengl.GL;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;


public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        // Initialize the window
        DisplayBuilder.init();

        // Store the window handle
        long window = DisplayBuilder.window;

        // Create ModelLoader instance
        ModelLoader modelLoader = new ModelLoader();

        // Initialize
        GL.createCapabilities();

        // Load Bodies
        Arena arena = new Arena(modelLoader);
        Dragon dragon = new Dragon(modelLoader);

        // Create lights
        Light[] lights = {new Light(new Vector3f(0, 20, 0), new Vector3f(1, 1, 1)),
                new Light(new Vector3f(0, 10, 15), new Vector3f(0.25f, 0, 0.5f))};

        // Create MasterRenderer instance
        MasterRenderer renderer = new MasterRenderer(lights.length);

        BodyCamera camera = new BodyCamera(dragon, 0.5f);
        //camera.setPosition(new Vector3f(-15, 7, 15));
        //camera.setRotation(new Vector3f(30, 45, 0));
        camera.enableZoom(window);
        camera.enableMouseTracking(window);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!GLFW.glfwWindowShouldClose(window)) {
            // Poll for window events and move camera accordingly
            //camera.move(window);
            dragon.move(window, renderer.getRenderer());

            camera.move(window);

            renderer.processBody(arena);
            renderer.processBody(dragon);

            renderer.render(lights, camera, window);
        }

        // Clean up
        renderer.cleanUp();

        // Remove all VAOs and VBOs
        modelLoader.cleanUp();

        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

}