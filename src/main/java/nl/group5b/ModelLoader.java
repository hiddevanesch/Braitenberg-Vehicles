package nl.group5b;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();

    public Model loadToVAO(float[] positions) {
        int vaoID = createVAO();
        storeDataInAttributeList(Model.POSITION_ATTR, positions);
        unbindVAO();
        return new Model(vaoID, positions.length / 3);
    }

    // Create VAO and return ID
    private int createVAO() {
        int vaoID = GL46.glGenVertexArrays();
        vaos.add(vaoID);
        GL46.glBindVertexArray(vaoID);
        return vaoID;
    }

    // Create VBO and return ID
    private int createVBO() {
        int vboID = GL46.glGenBuffers();
        vbos.add(vboID);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vboID);
        return vboID;
    }

    // Unbind VAO
    private void unbindVAO() {
        GL46.glBindVertexArray(0);
    }

    // Unbind VBO
    private void unbindVBO() {
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
    }

    private void storeDataInAttributeList(int attributeNumber, float[] data) {
        int vboID = createVBO();

        // Store data in a buffer
        FloatBuffer buffer = storeFloatArrayInFloatBuffer(data);

        // Put the data into the VBO
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, buffer, GL46.GL_STATIC_DRAW);

        // Put the VBO into the VAO
        GL46.glVertexAttribPointer(attributeNumber, 3, GL46.GL_FLOAT, false, 0, 0);

        unbindVBO();
    }

    // Stores array of floats into a new FloatBuffer object
    private FloatBuffer storeFloatArrayInFloatBuffer(float[] data) {
        // Create a new FloatBuffer
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);

        // Put the data into the buffer
        buffer.put(data);

        // Flip the buffer so it can be read
        buffer.flip();

        return buffer;
    }

    // Clean up VAOs and VBOs
    public void cleanUp() {
        for (int vao : vaos) {
            GL46.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL46.glDeleteBuffers(vbo);
        }
    }
}
