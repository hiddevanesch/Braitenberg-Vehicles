package nl.group5b.models;

import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public class Dragon extends Body {
    public Dragon(ModelLoader modelLoader) throws FileNotFoundException {
        super("dragon", modelLoader);
        super.setMaterial(new Material(0.1f, 0.2f, 0.7f, 10, 0.5f));
    }
}
