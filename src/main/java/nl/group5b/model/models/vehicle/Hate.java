package nl.group5b.model.models.vehicle;

import nl.group5b.engine.Renderer;
import nl.group5b.model.Material;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.DriveHandler;
import nl.group5b.util.Settings;
import org.joml.Vector3f;

import java.io.FileNotFoundException;

/**
 * Hate type vehicle:
 * Sensors are connected to the wheel on the opposite side
 * More brightness sensed -> faster wheel speed
 */
public class Hate extends BraitenbergVehicle implements DriveHandler {

    // Red body material
    static private final Material bodyMaterial = new Material(new Vector3f(0.541f, 0.004f, 0.004f), 10, 0.5f);

    public Hate(ModelLoader modelLoader, Vector3f position,
                Vector3f rotation) throws FileNotFoundException {
        super(modelLoader, bodyMaterial, position, rotation);
    }

    @Override
    public void move(Renderer renderer) {
        float leftSensorBrightness = getLeftSensor().calculateSensorBrightness();
        float rightSensorBrightness = getRightSensor().calculateSensorBrightness();

        // multiply brightness by 20, clamp between 0 and 1
        leftSensorBrightness = Math.min(1, leftSensorBrightness * 20);
        rightSensorBrightness = Math.min(1, rightSensorBrightness * 20);

        // Set wheel speed based on sensor brightness
        leftWheelSpeed = (rightSensorBrightness * Settings.VEHICLE_SPEED);
        rightWheelSpeed = (leftSensorBrightness * Settings.VEHICLE_SPEED);

        // Move the vehicle based on the new wheel speeds
        super.updatePosition(renderer);
    }
}
