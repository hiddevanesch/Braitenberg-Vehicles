package nl.group5b.models;

import nl.group5b.engine.Entity;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public abstract class Body {
    private Entity[] entities;
    private Material[] materials;

    public Entity[] getEntities() {
        return entities;
    }

    public Material[] getMaterials() {
        return materials;
    }

    public void setBody(Model[] loadedModels, Material[] materialSets,
                        Vector3f[] startingPositions, float[] scales) throws FileNotFoundException {
        int length = loadedModels.length;

        // Check if the arrays are the same length
        if (length != materialSets.length || length != startingPositions.length || length != scales.length) {
            throw new FileNotFoundException("The length of the arrays are not equal");
        }
        
        this.entities = new Entity[length];
        this.materials = new Material[length];
        for (int i = 0; i < length; i++) {
            this.entities[i] = new Entity(loadedModels[i], startingPositions[i], 0, 0, 0, scales[i]);
            this.materials[i] = materialSets[i];
        }
    }
}
