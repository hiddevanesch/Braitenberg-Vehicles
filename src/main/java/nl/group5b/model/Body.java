package nl.group5b.model;

import nl.group5b.engine.Entity;
import nl.group5b.engine.Renderer;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public abstract class Body {
    private BodyElement[] bodyElements;

    public BodyElement[] getBodyElements() {
        return bodyElements;
    }

    public void setBody(Model[] loadedModels, Material[] materialSets,
                        Vector3f[] startingPositions, float[] scales) throws FileNotFoundException {
        int length = loadedModels.length;

        // Check if the arrays are the same length
        if (length != materialSets.length || length != startingPositions.length || length != scales.length) {
            throw new FileNotFoundException("The length of the arrays are not equal");
        }

        // Create the body elements
        this.bodyElements = new BodyElement[length];
        for (int i = 0; i < length; i++) {
            this.bodyElements[i] = new BodyElement(
                    new Entity(loadedModels[i], startingPositions[i], new Vector3f(0, 0, 0), scales[i]),
                    materialSets[i]
            );
        }
    }

    // Code for setting the position and orientation of the body
    public abstract void setPosition(Vector3f position, Vector3f rotation);

    public abstract Vector3f getPosition();

    public abstract Vector3f getRotation();
}
