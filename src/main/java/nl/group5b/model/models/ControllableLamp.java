package nl.group5b.model.models;

import nl.group5b.engine.Renderer;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.ControlHandler;
import org.joml.Vector3f;

import java.io.FileNotFoundException;

public class ControllableLamp extends Lamp implements ControlHandler {
    public ControllableLamp(ModelLoader modelLoader, Vector3f position, Vector3f colour, Vector3f attenuation) throws FileNotFoundException {
        super(modelLoader, position, colour, attenuation);
    }

    @Override
    public void move(long window, Renderer renderer) {

    }
}
