package nl.group5b.engine;

import nl.group5b.camera.BodyCamera;
import nl.group5b.gui.GUI;
import nl.group5b.gui.Element;
import nl.group5b.gui.elements.Demo;
import nl.group5b.light.Light;
import nl.group5b.model.*;
import nl.group5b.model.interfaces.ControlHandler;
import nl.group5b.model.models.*;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.Callbacks;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        // Initialize the window
        DisplayBuilder.init();

        // Store the window handle
        long window = DisplayBuilder.window;

        // Create GUI Elements
        Demo demo = new Demo();

        // Load GUI Elements into array
        Element[] elements = {
                demo
        };

        // Create GUI
        GUI gui = new GUI(window, elements);

        // Create ModelLoader instance
        ModelLoader modelLoader = new ModelLoader();

        // Create sun
        Light sun = new Light(new Vector4f(20, 20, 0, 0), new Vector3f(1, 1, 1));

        // Create Arena body
        Arena arena = new Arena(modelLoader);

        // Create the rest of the Bodies
        Controllable braitenbergVehicle = new Controllable(modelLoader,
                new Vector3f(0, 0, 5), new Vector3f(0, 180, 0));
        Controllable secondCar = new Controllable(modelLoader,
                new Vector3f(0, 0, -5), new Vector3f(0, -45, 0));
        Lamp colouredLamp = new Lamp(modelLoader, new Vector3f(5, 2, 0), new Vector3f(0, 0, -45),
                new Vector3f(0.25f, 0, 1), new Vector3f(1, 0.01f, 0.002f));

        // Load bodies (except Arena) into list
        List<Body> bodies = new ArrayList<>(List.of(
                braitenbergVehicle,
                colouredLamp
        ));

        // Load lights into array (has to be an array with predefined length)
        // IMPORTANT! Sun HAS to be present at index 0 ======================
        Light[] lights = {
                sun,
                colouredLamp.getLight()
        };

        // Create Camera's
        BodyCamera camera = new BodyCamera(braitenbergVehicle, 0.5f);
        camera.enableZoom(window);
        camera.enableMouseTracking(window);

        // Create MasterRenderer instance
        MasterRenderer renderer = new MasterRenderer(lights, camera, window, gui);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!GLFW.glfwWindowShouldClose(window)) {
            // Poll for window events and move camera accordingly
            //camera.move(window);

            // TODO remove demo code
            if (demo.getSpawnSecondCar().get()) {
                if (!bodies.contains(secondCar)) {
                    bodies.add(secondCar);
                }
            } else {
                bodies.remove(secondCar);
            }

            // TODO remove demo code
            demo.addVehicleSpeed(braitenbergVehicle.getSpeedLeft(), braitenbergVehicle.getSpeedRight());

            for (Body body : bodies) {
                if (body instanceof ControlHandler) {
                    ((ControlHandler) body).move(window, renderer.getRenderer());
                }
            }

            camera.move(window);

            // Make renderMap of bodies
            renderer.processBodies(bodies);

            // Render shadows using renderer.getRenderMap()
            renderer.computeShadows();

            // Add arena to renderMap
            renderer.processArena(arena);

            // Update texture in GUI
            demo.setImage(renderer.getShadowMapTexture());

            // Render scene
            renderer.render();
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