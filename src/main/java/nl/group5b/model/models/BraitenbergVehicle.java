package nl.group5b.model.models;

import nl.group5b.model.*;
import nl.group5b.model.interfaces.CollisionHandler;
import nl.group5b.model.interfaces.PositionHandler;
import nl.group5b.util.Algebra;
import org.joml.Vector3f;

import java.io.FileNotFoundException;
import java.util.List;

public abstract class BraitenbergVehicle extends Body implements PositionHandler, CollisionHandler {

    protected static final float SPEED = 1.5f;
    protected static final float WHEEL_ROTATION_SPEED = 150;
    protected static final float ACCELERATION = 0.5f;
    protected static final float DECELERATION = 3;
    protected static final float CLAMP = 0.001f;
    protected static final float STEERING = 1.3f;


    protected HitBox hitBox;
    protected static List<Body> bodiesPotentialCollide;
    static final Vector3f carBodyRelativePosition = new Vector3f(0, 0.3f, 0);
    static final Vector3f carLeftWheelRelativePosition = new Vector3f(0.72f, 0.3f, 0);
    static final Vector3f carRightWheelRelativePosition = new Vector3f(-0.72f, 0.3f, 0);

    protected float leftWheelRotation = 0;
    protected float rightWheelRotation = 0;

    protected float leftWheelSpeed = 0;
    protected float rightWheelSpeed = 0;

    public BraitenbergVehicle(ModelLoader modelLoader) throws FileNotFoundException {
        Model carBody = OBJLoader.loadOBJ("carbody", modelLoader);
        Model carWheel = OBJLoader.loadOBJ("carwheel", modelLoader);

        Material brownMaterial = new Material(0.45f, 0.30f, 0.2f, 10, 0.5f);
        Material blackMaterial = new Material(0.2f, 0.2f, 0.2f, 10, 0.5f);

        Vector3f defaultRotation = new Vector3f(0, 0, 0);

        Model[] loadedModels = {carBody, carWheel, carWheel};
        Material[] materialSets = {brownMaterial, blackMaterial, blackMaterial};
        Vector3f[] startingPositions = {carBodyRelativePosition, carLeftWheelRelativePosition,
                carRightWheelRelativePosition};
        Vector3f[] startingRotations = {defaultRotation, defaultRotation, defaultRotation};
        float[] scales = {1, 1, 1};

        super.setBody(loadedModels, materialSets, startingPositions, startingRotations, scales);
    }

    public BraitenbergVehicle(ModelLoader modelLoader, Vector3f position,
                              Vector3f rotation) throws FileNotFoundException {
        this(modelLoader);
        this.setPosition(position);
        this.setRotation(rotation);

        this. hitBox = new HitBox(
                new Vector3f(-0.67f, 0f, 1.7f), // front left offset
                new Vector3f(0.67f, 0f, 1.7f), // front right offset
                new Vector3f(-0.67f, 0f, -0.3f), // rear left offset
                new Vector3f(0.67f, 0f, -0.3f), // rear right offset
                super.getBodyElements()[0].getEntity()); // entity (car base)
    }

    protected void rotateWheels(float frameTime) {
        leftWheelRotation += leftWheelSpeed * WHEEL_ROTATION_SPEED * frameTime;
        rightWheelRotation += rightWheelSpeed * WHEEL_ROTATION_SPEED * frameTime;

        // Get y-axis rotation
        float yRotation = getRotation().y;

        // Rotate the wheels in the axis direction of yDirection
        Vector3f newLeftWheelRotation = Algebra.rotateWheelGivenCurrentAngle(yRotation, leftWheelRotation);
        Vector3f newRightWheelRotation = Algebra.rotateWheelGivenCurrentAngle(yRotation, rightWheelRotation);
        super.getBodyElements()[1].getEntity().setRotation(newLeftWheelRotation);
        super.getBodyElements()[2].getEntity().setRotation(newRightWheelRotation);
    }

    @Override
    public void setPosition(Vector3f position) {
        Vector3f bodyPosition = new Vector3f(position).add(carBodyRelativePosition);
        Vector3f leftWheelPosition = new Vector3f(position).add(carLeftWheelRelativePosition);
        Vector3f rightWheelPosition = new Vector3f(position).add(carRightWheelRelativePosition);

        super.getBodyElements()[0].getEntity().setPosition(bodyPosition);
        super.getBodyElements()[1].getEntity().setPosition(leftWheelPosition);
        super.getBodyElements()[2].getEntity().setPosition(rightWheelPosition);
    }

    @Override
    public void setRotation(Vector3f rotation) {
        super.getBodyElements()[0].getEntity().setRotation(rotation);
        super.getBodyElements()[1].getEntity().setRotation(rotation);
        super.getBodyElements()[2].getEntity().setRotation(rotation);

        // Get the vehicles pivot point
        Vector3f bodyPosition = super.getBodyElements()[0].getEntity().getPosition();

        // Rotate the wheels around the pivot point
        Vector3f leftWheelPosition = Algebra.rotatePointAroundPivot(carLeftWheelRelativePosition,
                bodyPosition, rotation.y);
        Vector3f rightWheelPosition = Algebra.rotatePointAroundPivot(carRightWheelRelativePosition,
                bodyPosition, rotation.y);
        super.getBodyElements()[1].getEntity().setPosition(leftWheelPosition);
        super.getBodyElements()[2].getEntity().setPosition(rightWheelPosition);
    }

    @Override
    public void movePosition(Vector3f position) {
        super.getBodyElements()[0].getEntity().move(position);
        super.getBodyElements()[1].getEntity().move(position);
        super.getBodyElements()[2].getEntity().move(position);
    }

    @Override
    public void moveRotation(Vector3f rotation) {
        Vector3f bodyRotation = new Vector3f(super.getBodyElements()[0].getEntity().getRotation()).add(rotation);

        setRotation(bodyRotation);
    }

    @Override
    public Vector3f getPosition() {
        return super.getBodyElements()[0].getEntity().getPosition();
    }

    @Override
    public Vector3f getRotation() {
        return super.getBodyElements()[0].getEntity().getRotation();
    }

    public float getSpeedLeft() {
        return leftWheelSpeed;
    }

    public float getSpeedRight() {
        return rightWheelSpeed;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public void setBodies(List<Body> bodies) {
        bodiesPotentialCollide = bodies;
    }

    protected void updateHitBox(HitBox nextHitBox) {
        hitBox = nextHitBox;
    }

//    protected HitBox nextHitBox(Vector3f deltaPosition) {
//
//        // Get the corners of the hitbox
//        Vector3f frontLeft = new Vector3f(hitBox.getFrontLeftOffset());
//        Vector3f frontRight = new Vector3f(hitBox.getFrontRight());
//        Vector3f rearLeft = new Vector3f(hitBox.getRearLeft());
//        Vector3f rearRight = new Vector3f(hitBox.getRearRight());
//        // Update the corners of the hitbox
//        frontLeft.add(deltaPosition);
//        frontRight.add(deltaPosition);
//        rearLeft.add(deltaPosition);
//        rearRight.add(deltaPosition);
//
//        return new HitBox(frontLeft, frontRight, rearLeft, rearRight);
//    }

    // Function that goes through all the bodies and checks if the hitbox of the vehicle is overlapping with the hitbox of the target
    @Override
    public boolean isColliding(HitBox hitBox, List<Body> bodies) {
        // Loop over all bodies in the list, exluding the vehicle itself
        for (Body body : bodies) {
            if (body != this && body instanceof CollisionHandler) {
                if (((CollisionHandler) body).isInHitBox(hitBox)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Function that uses the algebra util to check if the hitbox of the vehicle is overlapping with the hitbox of the target
    @Override
    public boolean isInHitBox(HitBox hitBoxTarget) {
        return Algebra.hitboxOverlap(hitBox, hitBoxTarget);
    }
}
