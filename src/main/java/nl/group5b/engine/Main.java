package nl.group5b.engine;

import nl.group5b.models.Model;
import nl.group5b.models.ModelLoader;
import nl.group5b.shaders.StaticShader;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwGetKey;


public class Main {

    public static void main(String[] args) {
        // Initialize the window
        DisplayBuilder.init();

        // Store the window handle
        long window = DisplayBuilder.window;

        // Create ModelLoader instance
        ModelLoader modelLoader = new ModelLoader();

        // Initialize the renderer using GL.createCapabilities()
        Renderer.init();

        // Create a shader
        StaticShader shader = new StaticShader();

        // Create Rendered instance and initialize it
        Renderer renderer = new Renderer(shader);

        // Make a model TODO make actual models you fool
        float[] vertices = {
            -0.5f,0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f,0.5f,-0.5f,

            -0.5f,0.5f,0.5f,
            -0.5f,-0.5f,0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,0.5f,0.5f,

            0.5f,0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,0.5f,0.5f,

            -0.5f,0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            -0.5f,-0.5f,0.5f,
            -0.5f,0.5f,0.5f,

            -0.5f,0.5f,0.5f,
            -0.5f,0.5f,-0.5f,
            0.5f,0.5f,-0.5f,
            0.5f,0.5f,0.5f,

            -0.5f,-0.5f,0.5f,
            -0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,0.5f
        };

        int[] indices = {
            0,1,3,
            3,1,2,
            4,5,7,
            7,5,6,
            8,9,11,
            11,9,10,
            12,13,15,
            15,13,14,
            16,17,19,
            19,17,18,
            20,21,23,
            23,21,22
        };

        Model model = modelLoader.loadToVAO(vertices, indices);

        Entity entity = new Entity(model, new Vector3f(0, 0, -1.0f), 0, 0, 0, 1);

        Camera camera = new Camera();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !GLFW.glfwWindowShouldClose(window) ) {
            entity.rotate(0.5f, 1, 0.25f);

            // Poll for window events and move camera accordingly
            camera.move(window);

            // Prepare next frame
            renderer.prepare();

            // Start shader
            shader.start();

            // Load view matrix
            shader.loadViewMatrix(camera);

            // Render next frame
            renderer.render(entity, shader);

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