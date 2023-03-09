package nl.group5b.models;

import nl.group5b.engine.Entity;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public class Arena extends Body {
    public Arena(ModelLoader modelLoader) throws FileNotFoundException {
        super("arena", modelLoader);
        super.setMaterial(new Material(0.1f, 0.2f, 0.7f, 10, 0.5f));
    }
}
