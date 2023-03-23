package nl.group5b.shaders.viewport;

import nl.group5b.camera.Camera;
import nl.group5b.light.Light;
import nl.group5b.model.Material;
import nl.group5b.shaders.ShaderProgram;
import nl.group5b.util.Algebra;
import nl.group5b.util.Settings;
import org.joml.Matrix4f;


public class ViewportShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/main/java/nl/group5b/shaders/viewport/vertex_shader.glsl";
    private static final String FRAGMENT_FILE = "src/main/java/nl/group5b/shaders/viewport/fragment_shader.glsl";

    private int transformationMatrixLocation;
    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int colourLocation;
    private int dampingLocation;
    private int shininessLocation;
    private int toShadowMapSpaceLocation;
    private int shadowMapLocation;

    private int lightPositionLocations[];
    private int lightColourLocations[];
    private int lightAttenuationLocations[];

    public ViewportShader(int lightCount) {
        super(VERTEX_FILE, FRAGMENT_FILE, lightCount);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(Settings.VAO_POSITION_ATTR, "position");
        super.bindAttribute(Settings.VAO_NORMAL_ATTR, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        transformationMatrixLocation = super.getUniformLocation("transformationMatrix");
        projectionMatrixLocation = super.getUniformLocation("projectionMatrix");
        viewMatrixLocation = super.getUniformLocation("viewMatrix");
        colourLocation = super.getUniformLocation("colour");
        dampingLocation = super.getUniformLocation("damping");
        shininessLocation = super.getUniformLocation("shininess");
        toShadowMapSpaceLocation = super.getUniformLocation("toShadowMapSpace");
        shadowMapLocation = super.getUniformLocation("shadowMap");

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

    public void connectTextureUnits() {
        super.loadInt(shadowMapLocation, 0);
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
            super.loadVector4f(lightPositionLocations[i], lights[i].getPosition());
            super.loadVector3f(lightColourLocations[i], lights[i].getColour());
            super.loadVector3f(lightAttenuationLocations[i], lights[i].getAttenuation());
        }
    }

    public void loadMaterial(Material material) {
        super.loadVector3f(colourLocation, material.getColour());
        super.loadFloat(dampingLocation, material.getDamping());
        super.loadFloat(shininessLocation, material.getShininess());
    }

    public void loadToShadowMapSpaceMatrix(Matrix4f matrix) {
        super.loadMatrix(toShadowMapSpaceLocation, matrix);
    }

}
