package nl.group5b.model.models;

import nl.group5b.model.ModelLoader;
import org.joml.Vector3f;

import java.io.FileNotFoundException;

public class AttachableLamp extends Lamp {
    private BraitenbergVehicle vehicle = null;

    public AttachableLamp(ModelLoader modelLoader, Vector3f position, Vector3f colour, Vector3f attenuation) throws FileNotFoundException {
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
