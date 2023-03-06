package nl.group5b.models;

import nl.group5b.engine.Entity;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public abstract class Body {
    private Entity entity;
    private Material material;

    public Body(String modelName, ModelLoader modelLoader) throws FileNotFoundException {
        Model model = OBJLoader.loadOBJ(modelName, modelLoader);
        this.entity = new Entity(model, new Vector3f(0, 0, 0), 0, 0, 0, 1);
    }

    public Entity getEntity() {
        return entity;
    }

    public float getDamping() {
        return material.getDamping();
    }

    public float getShininess() {
        return material.getShininess();
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
