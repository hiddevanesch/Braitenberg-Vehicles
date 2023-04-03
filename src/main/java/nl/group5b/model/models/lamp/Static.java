package nl.group5b.model.models.lamp;

import nl.group5b.model.ModelLoader;
import org.joml.Vector3f;

import java.io.FileNotFoundException;

public class Static extends Lamp {
    public Static(ModelLoader modelLoader, Vector3f position, Vector3f colour, Vector3f attenuation) throws FileNotFoundException {
        super(modelLoader, position, colour, attenuation);
    }
}
