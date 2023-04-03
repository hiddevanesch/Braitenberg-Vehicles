package nl.group5b.model.models;

import nl.group5b.model.*;
import org.joml.Vector3f;

import java.io.FileNotFoundException;

public class Arena extends Body {
    public Arena(ModelLoader modelLoader) throws FileNotFoundException {
        Model arena = OBJLoader.loadOBJ("arena", modelLoader);

        Material greenMaterial = new Material(new Vector3f(0.05f, 0.5f, 0.15f), 10, 0.1f);

        Vector3f defaultPosition = new Vector3f(0, 0, 0);
        Vector3f defaultRotation = new Vector3f(0, 0, 0);

        Model[] loadedModels = {arena};
        Material[] materialSets = {greenMaterial};
        Vector3f[] startingPositions = {defaultPosition};
        Vector3f[] startingRotations = {defaultRotation};

        float[] scales = {1};

        super.setBody(loadedModels, materialSets, startingPositions, startingRotations, scales);
    }
}
