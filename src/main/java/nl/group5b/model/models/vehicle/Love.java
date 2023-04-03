package nl.group5b.model.models.vehicle;

import nl.group5b.engine.Renderer;
import nl.group5b.model.Material;
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
public class Love extends BraitenbergVehicle implements DriveHandler {

    // Hot pink body material
    static private final Material bodyMaterial = new Material(new Vector3f(1.00f, 0.412f, 0.706f), 10, 0.5f);

    public Love(ModelLoader modelLoader, Vector3f position,
                Vector3f rotation) throws FileNotFoundException {
        super(modelLoader, bodyMaterial, position, rotation);
    }

    @Override
    public void move(Renderer renderer) {
        float leftSensorBrightness = getLeftSensor().calculateSensorBrightness();
        float rightSensorBrightness = getRightSensor().calculateSensorBrightness();

        // multiply brightness by 12, clamp between 0 and 1
        leftSensorBrightness = Math.min(1, leftSensorBrightness * 12);
        rightSensorBrightness = Math.min(1, rightSensorBrightness * 12);

        // Set wheel speed based on sensor brightness
        leftWheelSpeed = (Settings.VEHICLE_SPEED - (leftSensorBrightness * Settings.VEHICLE_SPEED));
        rightWheelSpeed = (Settings.VEHICLE_SPEED - (rightSensorBrightness * Settings.VEHICLE_SPEED));


        // Move the vehicle based on the new wheel speeds
        super.updatePosition(renderer);
    }
}
