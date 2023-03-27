package nl.group5b.engine;

import nl.group5b.camera.BodyCamera;
import nl.group5b.camera.Sensor;
import nl.group5b.gui.Element;
import nl.group5b.gui.GUI;
import nl.group5b.gui.elements.Demo;
import nl.group5b.light.Light;
import nl.group5b.model.Body;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.ControlHandler;
import nl.group5b.model.models.Arena;
import nl.group5b.model.models.Controllable;
import nl.group5b.model.models.Lamp;
import nl.group5b.util.Settings;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

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
        Light sun = new Light(new Vector4f(20, 20, 0, 0), new Vector3f(
                Settings.SUN_BRIGHTNESS, Settings.SUN_BRIGHTNESS, Settings.SUN_BRIGHTNESS));

        // Create the Bodies
        Arena arena = new Arena(modelLoader);
        Controllable braitenbergVehicle = new Controllable(modelLoader,
                new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        Controllable secondCar = new Controllable(modelLoader,
                new Vector3f(0, 0, -5), new Vector3f(0, -45, 0));
        Controllable thirdCar = new Controllable(modelLoader,
                new Vector3f(-5, 0, -1), new Vector3f(0, 45, 0));

        Lamp colouredLamp = new Lamp(modelLoader, new Vector3f(0, 1.5f, 0),
                new Vector3f(1, 1, 0.5f), new Vector3f(1, 0.75f, 0.75f));

        // Load bodies (except Arena) into list
        List<Body> bodies = new ArrayList<>(List.of(
                arena,
                braitenbergVehicle,
                colouredLamp,
                secondCar,
                thirdCar
        ));

        // Load lights into array (has to be an array with predefined length)
        // IMPORTANT! Sun HAS to be present at index 0 ======================
        Light[] lights = {
                sun,
                colouredLamp.getLight()
        };

        Sensor sensor = new Sensor(new Vector3f(0, 1, 0), new Vector3f(0, 0, 0), Settings.SENSOR_RESOLUTION);

        // Create Camera's
        BodyCamera camera = new BodyCamera(braitenbergVehicle, 0.5f);
        camera.enableZoom(window);
        camera.enableMouseTracking(window);

        // Create MasterRenderer instance
        MasterRenderer renderer = new MasterRenderer(lights, camera, window, gui);

        // set the bodies that the braitenberg vehicles can collide with
        braitenbergVehicle.setBodiesPotentialCollide(bodies);
        secondCar.setBodiesPotentialCollide(bodies);
        thirdCar.setBodiesPotentialCollide(bodies);

        // TODO int for loop
        int i = 0;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!GLFW.glfwWindowShouldClose(window)) {
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

            // Render sensor views
            //renderer.renderSensors(bodies);
            //renderer.renderSensor();

            // Update texture in GUI
            demo.setImage(sensor.getTextureID());

            // Give bodies to renderer
            renderer.getRenderer().setBodies(bodies);

            // Render scene
            renderer.render(bodies, sensor);

            // if i is a multiple of 60
            if (i % 60 == 0) {
                // For every body that is instance of controllable
                for (Body body : bodies) {
                    if (body instanceof Controllable) {

                        Vector3f[] coordinates = ((Controllable) body).getHitBox().getCoordinates();


                        System.out.println("---------------------------------------");
                        // print the coordinates in 3 decimal place in the following format: (without newlines)
                        // (x1, z1), (x2, z2), (x3, z3), (x4, z4)
                        // Also replace the , in the coordinates with a .
                        String coordinateString = "Coordinates: (" + String.format("%.3f", coordinates[0].x) + ", " + String.format("%.3f", coordinates[0].z) + "), (" + String.format("%.3f", coordinates[1].x) + ", " + String.format("%.3f", coordinates[1].z) + "), (" + String.format("%.3f", coordinates[2].x) + ", " + String.format("%.3f", coordinates[2].z) + "), (" + String.format("%.3f", coordinates[3].x) + ", " + String.format("%.3f", coordinates[3].z) + ")";
                        coordinateString =  coordinateString.replaceAll(",", ".");
                        coordinateString = coordinateString.replaceAll(". ", ",");
                        String entityPosition = "Entity position: (" + String.format("%.3f", body.getBodyElements()[0].getEntity().getPosition().x) + ", " + String.format("%.3f", body.getBodyElements()[0].getEntity().getPosition().z) + ")";
                        entityPosition = entityPosition.replaceAll(",", ".");
                        entityPosition = entityPosition.replaceAll(". ", ",");
                        System.out.println(coordinateString);
                        System.out.println(entityPosition);
                        System.out.println("---------------------------------------");


                    }
                }
                System.out.println("\n");
            }
            i++;

            //System.out.println(sensor.calculateSensorBrightness());
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