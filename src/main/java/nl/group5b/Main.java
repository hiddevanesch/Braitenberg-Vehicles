package nl.group5b;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.Callbacks;


public class Main {

    public static void main(String[] args) {
        // Initialize the window
        DisplayBuilder.init();

        // Store the window handle
        long window = DisplayBuilder.window;

        // Create ModelLoader instance
        ModelLoader modelLoader = new ModelLoader();

        // Create Rendered instance and initialize it
        Renderer renderer = new Renderer();
        renderer.init();

        // Make a model TODO make actual models you fool
        float[] vertices = {
            -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
        };

        int[] indices = {
            0, 1, 3,
            3, 1, 2
        };

        Model model = modelLoader.loadToVAO(vertices, indices);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !GLFW.glfwWindowShouldClose(window) ) {
            // Prepare next frame
            renderer.prepare();

            // Render next frame
            renderer.render(model);

            // Finalize next frame
            renderer.complete(window);
        }

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