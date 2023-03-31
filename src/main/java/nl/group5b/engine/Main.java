package nl.group5b.engine;

import nl.group5b.camera.BodyCamera;
import nl.group5b.camera.Camera;
import nl.group5b.camera.Sensor;
import nl.group5b.gui.Element;
import nl.group5b.gui.GUI;
import nl.group5b.gui.elements.MainPanel;
import nl.group5b.gui.elements.SettingsPanel;
import nl.group5b.light.Light;
import nl.group5b.model.Body;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.ControlHandler;
import nl.group5b.model.interfaces.DriveHandler;
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
        Wall wall = new Wall(modelLoader);
        Controllable braitenbergVehicle = new Controllable(modelLoader,
                new Vector3f(-8, 0, 8), new Vector3f(0, 0, 0));
        Controllable secondCar = new Controllable(modelLoader,
                new Vector3f(0, 0, -5), new Vector3f(0, -45, 0));
        Controllable thirdCar = new Controllable(modelLoader,
                new Vector3f(-5, 0, -1), new Vector3f(0, 45, 0));
        LoveVehicle loveVehicle = new LoveVehicle(modelLoader,
                new Vector3f(-7.5f, 0, -8.67f), new Vector3f(0, 0, 0));
        FearVehicle fearVehicle = new FearVehicle(modelLoader,
                new Vector3f(-2.5f, 0, -8.67f), new Vector3f(0, 0, 0));
        HateVehicle hateVehicle = new HateVehicle(modelLoader,
                new Vector3f(2.5f, 0, 8.67f), new Vector3f(0, 180, 0));
        CuriousVehicle curiousVehicle = new CuriousVehicle(modelLoader,
                new Vector3f(7.5f, 0, 8.67f), new Vector3f(0, 180, 0));

        Lamp colouredLamp1 = new Lamp(modelLoader, new Vector3f(-7.5f, 0.5f, 0),
                new Vector3f(1, 1, 0.5f), new Vector3f(1, 0.3f, 0.3f));
        Lamp colouredLamp2 = new Lamp(modelLoader, new Vector3f(-2.5f, 0.5f, 0),
                new Vector3f(1, 1, 0.5f), new Vector3f(1, 0.3f, 0.3f));
        Lamp colouredLamp3 = new Lamp(modelLoader, new Vector3f(2.5f, 0.5f, 0),
                new Vector3f(1, 1, 0.5f), new Vector3f(1, 0.3f, 0.3f));
        Lamp colouredLamp4 = new Lamp(modelLoader, new Vector3f(7.5f, 0.5f, 0),
                new Vector3f(1, 1, 0.5f), new Vector3f(1, 0.3f, 0.3f));

        // Load bodies (except Arena) into list
        List<Body> bodies = new ArrayList<>(List.of(
                arena,
                wall,
                braitenbergVehicle,
                colouredLamp1,
                colouredLamp2,
                colouredLamp3,
                colouredLamp4,
                loveVehicle,
                fearVehicle,
                hateVehicle,
                curiousVehicle
        ));

        // Load lights into array (has to be an array with predefined length)
        // IMPORTANT! Sun HAS to be present at index 0 ======================
        Light[] lights = {
                sun,
                colouredLamp1.getLight(),
                colouredLamp2.getLight(),
                colouredLamp3.getLight(),
                colouredLamp4.getLight()
        };

        // Create Camera instances
        Camera topDownCamera = new Camera(new Vector3f(0, 25, 0), new Vector3f(90, 0, 0));
        BodyCamera thirdPersonCamera = new BodyCamera(braitenbergVehicle, 0.5f);
        thirdPersonCamera.enableZoom(window);
        thirdPersonCamera.enableMouseTracking(window);

        // Create GUI Elements
        MainPanel mainPanel = new MainPanel(bodies);
        SettingsPanel settingsPanel = new SettingsPanel(topDownCamera, thirdPersonCamera, sun);

        // Load GUI Elements into array
        Element[] elements = {
                mainPanel,
                settingsPanel
        };

        // Create GUI
        GUI gui = new GUI(window, elements);

        // Create MasterRenderer instance
        MasterRenderer renderer = new MasterRenderer(bodies, lights, gui, window);

        // set the bodies that the braitenberg vehicles can collide with
        braitenbergVehicle.setBodiesPotentialCollide(bodies);
        secondCar.setBodiesPotentialCollide(bodies);
        thirdCar.setBodiesPotentialCollide(bodies);
        loveVehicle.setBodiesPotentialCollide(bodies);
        fearVehicle.setBodiesPotentialCollide(bodies);
        hateVehicle.setBodiesPotentialCollide(bodies);
        curiousVehicle.setBodiesPotentialCollide(bodies);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!GLFW.glfwWindowShouldClose(window)) {
            // TODO remove demo code
//            if (demo.getSpawnSecondCar().get()) {
//                if (!bodies.contains(secondCar)) {
//                    bodies.add(secondCar);
//                }
//            } else {
//                bodies.remove(secondCar);
//            }

            // TODO remove demo code
//            demo.addVehicleSpeed(braitenbergVehicle.getSpeedLeft(), braitenbergVehicle.getSpeedRight());

            // Move all engine components that need to be moved (e.g. camera, bodies, etc.)
            renderer.move();

            // Update texture in GUI
            mainPanel.setImages(braitenbergVehicle.getLeftSensor().getTextureID(), braitenbergVehicle.getRightSensor().getTextureID());

            // Render scene
            renderer.render();

            //System.out.println(sensor.calculateSensorBrightness());
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