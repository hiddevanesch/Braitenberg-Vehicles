package nl.group5b.model.models;

import nl.group5b.model.ModelLoader;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public class Controllable extends BraitenbergVehicle {

    public Controllable(ModelLoader modelLoader) throws FileNotFoundException {
        super(modelLoader);
    }
}
