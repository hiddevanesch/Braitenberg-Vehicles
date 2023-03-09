package nl.group5b.model;

import org.lwjgl.util.vector.Vector3f;

public class Material {
    private Vector3f colour;
    private float damping;
    private float shininess;

    public Material(float red, float green, float blue, float damping, float shininess) {
        this.colour = new Vector3f(red, green, blue);
        this.damping = damping;
        this.shininess = shininess;
    }

    public Vector3f getColour() {
        return colour;
    }

    public float getDamping() {
        return damping;
    }

    public float getShininess() {
        return shininess;
    }
}
