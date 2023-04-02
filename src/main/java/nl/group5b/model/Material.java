package nl.group5b.model;

import org.joml.Vector3f;

public class Material {
    private Vector3f colour;
    private boolean isEmissive;
    private float damping;
    private float shininess;

    public Material(Vector3f colour, float damping, float shininess) {
        this.colour = colour;
        this.damping = damping;
        this.shininess = shininess;
    }
    
    public Material(Vector3f colour, boolean isEmissive) {
        this.colour = colour;
        this.isEmissive = isEmissive;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
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
