package nl.group5b.model;

import org.joml.Vector3f;

public class Material {
    private final Vector3f colour;
    private boolean isEmissive;
    private float damping;
    private float shininess;

    public Material(float red, float green, float blue, float damping, float shininess) {
        this.colour = new Vector3f(red, green, blue);
        this.damping = damping;
        this.shininess = shininess;
    }
    
    public Material(float red, float green, float blue, boolean isEmissive) {
        this.colour = new Vector3f(red, green, blue);
        this.isEmissive = isEmissive;
    }

    public Vector3f getColour() {
        return colour;
    }
    
    public boolean isEmissive() {
        return isEmissive;
    }

    public float getDamping() {
        return damping;
    }

    public float getShininess() {
        return shininess;
    }
}
