package nl.group5b.shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

public abstract class ShaderProgram {

    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    private final int lightCount;

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public ShaderProgram(String vertexFile, String fragmentFile, int lightCount) {
        this.lightCount = lightCount;
        vertexShaderID = loadShader(vertexFile, GL46.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL46.GL_FRAGMENT_SHADER);
        programID = GL46.glCreateProgram();
        GL46.glAttachShader(programID, vertexShaderID);
        GL46.glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        GL46.glLinkProgram(programID);
        GL46.glValidateProgram(programID);
        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String uniformName) {
        return GL46.glGetUniformLocation(programID, uniformName);
    }

    public void start() {
        GL46.glUseProgram(programID);
    }

    public void stop() {
        GL46.glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        GL46.glDetachShader(programID, vertexShaderID);
        GL46.glDetachShader(programID, fragmentShaderID);
        GL46.glDeleteShader(vertexShaderID);
        GL46.glDeleteShader(fragmentShaderID);
        GL46.glDeleteProgram(programID);
    }

    protected abstract void bindAttributes();

    // Bind attributes from the VAO to the shader
    protected void bindAttribute(int attribute, String variableName) {
        GL46.glBindAttribLocation(programID, attribute, variableName);
    }

    protected void loadFloat(int location, float value) {
        GL46.glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector3f vector) {
        GL46.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void loadBoolean(int location, boolean value) {
        float toLoad = 0;
        if (value) {
            toLoad = 1;
        }
        GL46.glUniform1f(location, toLoad);
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL46.glUniformMatrix4fv(location, false, matrixBuffer);
    }

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                // This allows us to compile the shader with a "dynamic" amount of lights
                if (line.startsWith("#define LIGHT_COUNT")) {
                    line += " 4";
                }
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read file!");
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL46.glCreateShader(type);
        GL46.glShaderSource(shaderID, shaderSource);
        GL46.glCompileShader(shaderID);
        if (GL46.glGetShaderi(shaderID, GL46.GL_COMPILE_STATUS) == GL46.GL_FALSE) {
            System.out.println(GL46.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;
    }

}
