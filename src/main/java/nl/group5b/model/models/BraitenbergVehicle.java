package nl.group5b.model.models;

import nl.group5b.camera.Sensor;
import nl.group5b.model.*;
import nl.group5b.model.interfaces.CollisionHandler;
import nl.group5b.model.interfaces.PositionHandler;
import nl.group5b.util.Algebra;
import nl.group5b.util.Settings;
import org.joml.Vector3f;

import java.io.FileNotFoundException;
import java.util.List;

public abstract class BraitenbergVehicle extends Body implements PositionHandler, CollisionHandler {

    static final Vector3f carBodyRelativePosition = new Vector3f(0, 0.3f, 0);
    static final Vector3f carLeftWheelRelativePosition = new Vector3f(0.72f, 0.3f, 0);
    static final Vector3f carRightWheelRelativePosition = new Vector3f(-0.72f, 0.3f, 0);
    static final Vector3f carLeftSensorRelativePosition = new Vector3f(0.6f, 0.5f, 1.75f);
    static final Vector3f carRightSensorRelativePosition = new Vector3f(-0.6f, 0.5f, 1.75f);

    protected float leftWheelRotation = 0;
    protected float rightWheelRotation = 0;

    protected float leftWheelSpeed = 0;
    protected float rightWheelSpeed = 0;

    protected HitBox hitBox;
    protected List<Body> bodiesPotentialCollide;

    private Sensor leftSensor;
    private Sensor rightSensor;

    public BraitenbergVehicle(ModelLoader modelLoader) throws FileNotFoundException {
        Model carBody = OBJLoader.loadOBJ("carbody", modelLoader);
        Model carWheel = OBJLoader.loadOBJ("carwheel", modelLoader);
        Model sensorCamera = OBJLoader.loadOBJ("camera", modelLoader);

        Material brownMaterial = new Material(0.45f, 0.30f, 0.2f, 10, 0.5f);
        Material blackMaterial = new Material(0.2f, 0.2f, 0.2f, 10, 0.1f);
        Material greyMaterial = new Material(0.5f, 0.5f, 0.5f, 10, 0.1f);

        Vector3f defaultRotation = new Vector3f(0, 0, 0);

        Model[] loadedModels = {carBody, carWheel, carWheel, sensorCamera, sensorCamera};
        Material[] materialSets = {brownMaterial, blackMaterial, blackMaterial, greyMaterial, greyMaterial};
        Vector3f[] startingPositions = {carBodyRelativePosition, carLeftWheelRelativePosition,
                carRightWheelRelativePosition, carLeftSensorRelativePosition, carRightSensorRelativePosition};
        Vector3f[] startingRotations = {defaultRotation, defaultRotation, defaultRotation, defaultRotation, defaultRotation};
        float[] scales = {1, 1, 1, 1, 1};

        this.leftSensor = new Sensor(carLeftSensorRelativePosition, defaultRotation, Settings.SENSOR_RESOLUTION);
        this.rightSensor = new Sensor(carRightSensorRelativePosition, defaultRotation, Settings.SENSOR_RESOLUTION);

        super.setBody(loadedModels, materialSets, startingPositions, startingRotations, scales);
    }

    public BraitenbergVehicle(ModelLoader modelLoader, Vector3f position,
                              Vector3f rotation) throws FileNotFoundException {
        this(modelLoader);
        this.setPosition(position);
        this.setRotation(rotation);

        this.hitBox = new HitBox(
                new Vector3f(-0.67f, 0, 1.7f),
                new Vector3f(0.67f, 0, 1.7f),
                new Vector3f(-0.67f, 0, -0.3f),
                new Vector3f(0.67f, 0, -0.3f),
                this.getBodyElements()[0].getEntity());
    }

    protected void rotateWheels(float frameTime) {
        leftWheelRotation += leftWheelSpeed * Settings.WHEEL_ROTATION_SPEED * frameTime;
        rightWheelRotation += rightWheelSpeed * Settings.WHEEL_ROTATION_SPEED * frameTime;

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
        Vector3f leftSensorPosition = new Vector3f(position).add(carLeftSensorRelativePosition);
        Vector3f rightSensorPosition = new Vector3f(position).add(carRightSensorRelativePosition);

        super.getBodyElements()[0].getEntity().setPosition(bodyPosition);
        super.getBodyElements()[1].getEntity().setPosition(leftWheelPosition);
        super.getBodyElements()[2].getEntity().setPosition(rightWheelPosition);
        super.getBodyElements()[3].getEntity().setPosition(leftSensorPosition);
        super.getBodyElements()[4].getEntity().setPosition(rightSensorPosition);
        this.leftSensor.setPosition(leftSensorPosition);
        this.rightSensor.setPosition(rightSensorPosition);
    }

    @Override
    public void setRotation(Vector3f rotation) {
        super.getBodyElements()[0].getEntity().setRotation(rotation);
        super.getBodyElements()[1].getEntity().setRotation(rotation);
        super.getBodyElements()[2].getEntity().setRotation(rotation);
        super.getBodyElements()[3].getEntity().setRotation(rotation);
        super.getBodyElements()[4].getEntity().setRotation(rotation);

        Vector3f sensorRotation = new Vector3f(rotation).add(0, 180, 180); /////////

        this.leftSensor.setRotation(sensorRotation);
        this.rightSensor.setRotation(sensorRotation);

        // Get the vehicles pivot point
        Vector3f bodyPosition = super.getBodyElements()[0].getEntity().getPosition();

        // Rotate the wheels and sensors around the pivot point
        Vector3f leftWheelPosition = Algebra.rotatePointAroundPivot(carLeftWheelRelativePosition,
                bodyPosition, rotation.y);
        Vector3f rightWheelPosition = Algebra.rotatePointAroundPivot(carRightWheelRelativePosition,
                bodyPosition, rotation.y);
        Vector3f leftSensorPosition = Algebra.rotatePointAroundPivot(carLeftSensorRelativePosition,
                bodyPosition, rotation.y);
        Vector3f rightSensorPosition = Algebra.rotatePointAroundPivot(carRightSensorRelativePosition,
                bodyPosition, rotation.y);
        super.getBodyElements()[1].getEntity().setPosition(leftWheelPosition);
        super.getBodyElements()[2].getEntity().setPosition(rightWheelPosition);
        super.getBodyElements()[3].getEntity().setPosition(leftSensorPosition);
        super.getBodyElements()[4].getEntity().setPosition(rightSensorPosition);
        this.leftSensor.setPosition(leftSensorPosition);
        this.rightSensor.setPosition(rightSensorPosition);
    }

    @Override
    public void movePosition(Vector3f position) {
        super.getBodyElements()[0].getEntity().move(position);
        super.getBodyElements()[1].getEntity().move(position);
        super.getBodyElements()[2].getEntity().move(position);
        super.getBodyElements()[3].getEntity().move(position);
        super.getBodyElements()[4].getEntity().move(position);
        this.leftSensor.getPosition().add(position);
        this.rightSensor.getPosition().add(position);
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

    public Sensor getLeftSensor() {
        return leftSensor;
    }

    public Sensor getRightSensor() {
        return rightSensor;
    }

    public void setBodies(List<Body> bodies) {
        bodiesPotentialCollide = bodies;
    }

    // Function that goes through all the bodies and checks if the front of the vehicle is colliding with any of them
    public boolean isColliding() {
        Vector3f[] hitBoxCoordinates = hitBox.getCoordinates();
        // Loop over all bodies in the list, excluding the vehicle itself
        for (Body body : bodiesPotentialCollide) {
            if (body != this && body instanceof CollisionHandler) {
                // Get the hitbox of the target
                HitBox hitBoxTarget = ((CollisionHandler) body).getHitBox();
                if (Algebra.hitboxOverlap(hitBoxCoordinates, hitBoxTarget)) {
                    return true;
                }
            }
        }
        return false;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public void setBodiesPotentialCollide(List<Body> bodiesPotentialCollide) {
        this.bodiesPotentialCollide = bodiesPotentialCollide;
    }
}
