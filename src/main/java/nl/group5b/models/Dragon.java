package nl.group5b.models;

import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public class Dragon extends Body {
    public Dragon(ModelLoader modelLoader) throws FileNotFoundException {
        super("dragon", modelLoader);
        super.setMaterial(new Material(new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(0.5f, 0.5f, 0.5f), 10, 0.5f));
    }
}
