package nl.group5b.engine;

import nl.group5b.models.Model;
import nl.group5b.models.ModelLoader;
import nl.group5b.shaders.StaticShader;
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

        // Create a shader
        StaticShader shader = new StaticShader();

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

            // Start shader
            shader.start();

            // Render next frame
            renderer.render(model);

            // Stop shader
            shader.stop();

            // Finalize next frame
            renderer.complete(window);
        }

        // Clean up the shader
        shader.cleanUp();

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