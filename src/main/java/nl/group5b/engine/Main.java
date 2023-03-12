package nl.group5b.engine;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import nl.group5b.gui.GUI;
import nl.group5b.gui.elements.Element;
import nl.group5b.gui.elements.Test;
import nl.group5b.model.*;
import nl.group5b.model.models.Arena;
import nl.group5b.model.models.Dragon;
import nl.group5b.model.models.Lamp;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.opengl.GL;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;
import java.util.List;


public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        // Initialize the window
        DisplayBuilder.init();

        // Store the window handle
        long window = DisplayBuilder.window;

        // Create GUI Elements
        Test test = new Test();

        // Load GUI Elements into array
        Element[] elements = {test};

        // Create GUI
        GUI gui = new GUI(window, elements);

        // Create ModelLoader instance
        ModelLoader modelLoader = new ModelLoader();

        // Create Bodies
        Arena arena = new Arena(modelLoader);
        Dragon dragon = new Dragon(modelLoader);
        Lamp mainLamp = new Lamp(modelLoader, new Vector3f(0, 5, 0), new Vector3f(0, 0, 0));
        Lamp colouredLamp = new Lamp(modelLoader, new Vector3f(5, 2, 0), new Vector3f(0, 0, -45),
                new Vector3f(0.25f, 0, 1), new Vector3f(1, 0.01f, 0.002f));

        // Load bodies into array
        List<Body> bodies = List.of(arena, dragon, mainLamp, colouredLamp);

        // Create lights
        //Light sun = new Light(new Vector3f(0, 20, 0), new Vector3f(1, 1, 1));
        //Light colouredLight = new Light(new Vector3f(0, 10, 15), new Vector3f(0.25f, 0, 0.5f));

        // Load lights into array (has to be an array with predefined length)
        Light[] lights = {mainLamp.getLight(), colouredLamp.getLight()};

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

            renderer.processBodies(bodies);

            renderer.render(lights, camera, window, gui);

        }

        // Clean up renderer
        renderer.cleanUp();

        // Clean up GUI
        gui.cleanUp();

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