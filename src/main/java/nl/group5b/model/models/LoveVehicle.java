package nl.group5b.model.models;

import nl.group5b.engine.Renderer;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.DriveHandler;
import nl.group5b.util.Settings;
import org.joml.Vector3f;

import java.io.FileNotFoundException;

/**
 * Love type vehicle:
 * Sensors are connected to the wheel on the same side
 * More brightness sensed -> slower wheel speed
 */
public class LoveVehicle extends BraitenbergVehicle implements DriveHandler {
    public LoveVehicle(ModelLoader modelLoader, Vector3f position,
                        Vector3f rotation) throws FileNotFoundException {
        super(modelLoader, position, rotation);
    }

    @Override
    public void move(Renderer renderer) {
        float leftSensorBrightness = getLeftSensor().calculateSensorBrightness();
        float rightSensorBrightness = getRightSensor().calculateSensorBrightness();

        // multiply brightness by 15, clamp between 0 and 1
        leftSensorBrightness = Math.min(1, leftSensorBrightness * 12);
        rightSensorBrightness = Math.min(1, rightSensorBrightness * 12);

        // print sensor brightness
        System.out.println("Left sensor brightness: " + leftSensorBrightness);
        System.out.println("Right sensor brightness: " + rightSensorBrightness);


        // Set wheel speed based on sensor brightness
        leftWheelSpeed = (Settings.VEHICLE_SPEED - (leftSensorBrightness * Settings.VEHICLE_SPEED));
        rightWheelSpeed = (Settings.VEHICLE_SPEED - (rightSensorBrightness * Settings.VEHICLE_SPEED));


        // Move the vehicle based on the new wheel speeds
        super.updatePosition(renderer);
    }
}
