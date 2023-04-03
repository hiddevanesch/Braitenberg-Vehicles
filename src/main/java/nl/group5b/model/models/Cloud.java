package nl.group5b.model.models;

import nl.group5b.model.*;
import org.joml.Vector3f;

import java.io.FileNotFoundException;

public class Cloud extends Body {
    public Cloud(ModelLoader modelLoader, Vector3f position, Vector3f rotation) throws FileNotFoundException {
        Model cloud = OBJLoader.loadOBJ("cloud", modelLoader);

        Material cloudMaterial = new Material(1, 1, 1, 2, 0.5f);

        Model[] loadedModels = {cloud};
        Material[] materialSets = {cloudMaterial};
        Vector3f[] startingPositions = {position};
        Vector3f[] startingRotations = {rotation};

        float[] scales = {0.5f};

        super.setBody(loadedModels, materialSets, startingPositions, startingRotations, scales);
    }
}
