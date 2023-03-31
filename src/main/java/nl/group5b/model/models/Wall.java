package nl.group5b.model.models;

import nl.group5b.model.*;
import org.joml.Vector3f;

import java.io.FileNotFoundException;

public class Wall extends Body {
    public Wall(ModelLoader modelLoader) throws FileNotFoundException {
        Model wall = OBJLoader.loadOBJ("wall", modelLoader);

        Material grayMaterial = new Material(0.05f, 0.5f, 0.15f, 10, 0.1f);

        Vector3f defaultPosition = new Vector3f(0, 0, 0);
        Vector3f defaultRotation = new Vector3f(0, 0, 0);

        Model[] loadedModels = {wall};
        Material[] materialSets = {grayMaterial};
        Vector3f[] startingPositions = {defaultPosition};
        Vector3f[] startingRotations = {defaultRotation};

        float[] scales = {1};

        super.setBody(loadedModels, materialSets, startingPositions, startingRotations, scales);
    }
}
