package nl.group5b.engine;

import nl.group5b.models.Model;
import nl.group5b.models.ModelLoader;
import nl.group5b.models.OBJLoader;
import nl.group5b.shaders.StaticShader;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwGetKey;


public class Main {

    public static void main(String[] args) throws FileNotFoundException {
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

        Model model = OBJLoader.loadOBJ("dragon", modelLoader);

        Entity entity = new Entity(model, new Vector3f(0, 0, -1.0f), 0, 0, 0, 1);

        Light light = new Light(new Vector3f(0, 2, 0), new Vector3f(1, 1, 1));

        Camera camera = new Camera(new Vector3f(0, 1, 0));

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !GLFW.glfwWindowShouldClose(window) ) {
            //entity.rotate(0.5f, 1, 0.25f);

            // Poll for window events and move camera accordingly
            camera.move(window);

            // Prepare next frame
            renderer.prepare();

            // Start shader
            shader.start();

            // Load light
            shader.loadLight(light);

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