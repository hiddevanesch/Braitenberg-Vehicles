package nl.group5b.models;

import nl.group5b.engine.Entity;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public class Arena extends Body {
    public Arena(ModelLoader modelLoader) throws FileNotFoundException {
        super("arena", modelLoader);
        super.setMaterial(new Material(new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(0.5f, 0.5f, 0.5f), 0.5f, 0.5f));
    }
}
