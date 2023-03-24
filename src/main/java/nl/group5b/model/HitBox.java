package nl.group5b.model;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL;



public class HitBox {
    private Vector3f frontLeft;
    private Vector3f frontRight;
    private Vector3f rearLeft;
    private Vector3f rearRight;

    public HitBox(Vector3f frontLeft, Vector3f frontRight, Vector3f rearLeft, Vector3f rearRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
    }

    public Vector3f getFrontLeft() {
        return frontLeft;
    }

    public void setFrontLeft(Vector3f frontLeft) {
        this.frontLeft = frontLeft;
    }

    public Vector3f getFrontRight() {
        return frontRight;
    }

    public void setFrontRight(Vector3f frontRight) {
        this.frontRight = frontRight;
    }

    public Vector3f getRearLeft() {
        return rearLeft;
    }

    public void setRearLeft(Vector3f rearLeft) {
        this.rearLeft = rearLeft;
    }

    public Vector3f getRearRight() {
        return rearRight;
    }

    public void setRearRight(Vector3f rearRight) {
        this.rearRight = rearRight;
    }




}
