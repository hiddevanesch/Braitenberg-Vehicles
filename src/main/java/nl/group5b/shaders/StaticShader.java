package nl.group5b.shaders;

import nl.group5b.engine.Camera;
import nl.group5b.engine.Light;
import nl.group5b.model.Material;
import nl.group5b.model.Model;
import nl.group5b.util.Algebra;
import org.lwjgl.util.vector.Matrix4f;


public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/main/java/nl/group5b/shaders/vertex_shader.glsl";
    private static final String FRAGMENT_FILE = "src/main/java/nl/group5b/shaders/fragment_shader.glsl";

    private int transformationMatrixLocation;
    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int colourLocation;
    private int dampingLocation;
    private int shininessLocation;

    private int lightPositionLocations[];
    private int lightColourLocations[];
    private int lightAttenuationLocations[];

    public StaticShader(int lightCount) {
        super(VERTEX_FILE, FRAGMENT_FILE, lightCount);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(Model.POSITION_ATTR, "position");
        super.bindAttribute(Model.NORMAL_ATTR, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        transformationMatrixLocation = super.getUniformLocation("transformationMatrix");
        projectionMatrixLocation = super.getUniformLocation("projectionMatrix");
        viewMatrixLocation = super.getUniformLocation("viewMatrix");
        colourLocation = super.getUniformLocation("colour");
        dampingLocation = super.getUniformLocation("damping");
        shininessLocation = super.getUniformLocation("shininess");

        int lightCount = super.getLightCount();

        lightPositionLocations = new int[lightCount];
        lightColourLocations = new int[lightCount];
        lightAttenuationLocations = new int[lightCount];

        for (int i = 0; i < lightCount; i++) {
            lightPositionLocations[i] = super.getUniformLocation("lightPosition[" + i + "]");
            lightColourLocations[i] = super.getUniformLocation("lightColour[" + i + "]");
            lightAttenuationLocations[i] = super.getUniformLocation("lightAttenuation[" + i + "]");
        }
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(transformationMatrixLocation, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Algebra.createViewMatrix(camera);
        super.loadMatrix(viewMatrixLocation, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(projectionMatrixLocation, matrix);
    }

    public void loadLights(Light[] lights) {
        for (int i = 0; i < super.getLightCount(); i++) {
            super.loadVector(lightPositionLocations[i], lights[i].getPosition());
            super.loadVector(lightColourLocations[i], lights[i].getColour());
            super.loadVector(lightAttenuationLocations[i], lights[i].getAttenuation());
        }
    }

    public void loadMaterial(Material material) {
        super.loadVector(colourLocation, material.getColour());
        super.loadFloat(dampingLocation, material.getDamping());
        super.loadFloat(shininessLocation, material.getShininess());
    }

}
