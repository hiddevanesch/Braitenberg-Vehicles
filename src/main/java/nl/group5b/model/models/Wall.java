package nl.group5b.model.models;

import nl.group5b.model.*;
import org.joml.Vector3f;

import java.io.FileNotFoundException;

public class Wall extends Body {
    public Wall(ModelLoader modelLoader, Vector3f position, Vector3f rotation) throws FileNotFoundException {
        Model wall = OBJLoader.loadOBJ("wall", modelLoader);

        Material wallMaterial = new Material(new Vector3f(1, 1, 1), 2, 0.5f);

        Model[] loadedModels = {wall};
        Material[] materialSets = {wallMaterial};
        Vector3f[] startingPositions = {position};
        Vector3f[] startingRotations = {rotation};

        float[] scales = {1};

        super.setBody(loadedModels, materialSets, startingPositions, startingRotations, scales);
    }
}
