package nl.group5b.shaders;

import nl.group5b.engine.Camera;
import nl.group5b.models.Model;
import nl.group5b.util.Algebra;
import org.lwjgl.util.vector.Matrix4f;


public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/main/java/nl/group5b/shaders/vertex_shader.glsl";
    private static final String FRAGMENT_FILE = "src/main/java/nl/group5b/shaders/fragment_shader.glsl";

    private int transformationMatrixLocation;
    private int projectionMatrixLocation;
    private int viewMatrixLocation;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(Model.POSITION_ATTR, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        transformationMatrixLocation = super.getUniformLocation("transformationMatrix");
        projectionMatrixLocation = super.getUniformLocation("projectionMatrix");
        viewMatrixLocation = super.getUniformLocation("viewMatrix");
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

}
