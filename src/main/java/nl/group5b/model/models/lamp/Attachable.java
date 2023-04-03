package nl.group5b.model.models.lamp;

import nl.group5b.model.ModelLoader;
import nl.group5b.model.models.vehicle.BraitenbergVehicle;
import org.joml.Vector3f;

import java.io.FileNotFoundException;

public class Attachable extends Lamp {
    private BraitenbergVehicle vehicle = null;

    public Attachable(ModelLoader modelLoader, Vector3f position, Vector3f colour, Vector3f attenuation) throws FileNotFoundException {
        super(modelLoader, position, colour, attenuation);
    }

    public BraitenbergVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(BraitenbergVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void removeVehicle() {
        this.vehicle = null;
    }
}
