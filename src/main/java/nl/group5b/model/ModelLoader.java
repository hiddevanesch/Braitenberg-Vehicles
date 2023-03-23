package nl.group5b.model;

import nl.group5b.util.Settings;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();

    public Model loadToVAO(float[] positions, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(Settings.VAO_POSITION_ATTR, 3, positions);
        storeDataInAttributeList(Settings.VAO_NORMAL_ATTR, 3, normals);
        unbindVAO();
        return new Model(vaoID, indices.length);
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

    private void storeDataInAttributeList(int attributeIndex, int length, float[] data) {
        int vboID = createVBO();

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vboID);

        // Store data in a buffer
        FloatBuffer buffer = storeFloatArrayInFloatBuffer(data);

        // Put the data into the VBO
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, buffer, GL46.GL_STATIC_DRAW);

        // Put the VBO into the VAO
        GL46.glVertexAttribPointer(attributeIndex, length, GL46.GL_FLOAT, false, 0, 0);

        unbindVBO();
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = createVBO();

        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, vboID);

        // Store data in a buffer
        IntBuffer buffer = storeIntArrayInIntBuffer(indices);

        // Put the data into the VBO
        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, buffer, GL46.GL_STATIC_DRAW);

        // We don't need to unbind the VBO here, because a VAO has a special slot for the index buffer
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

    private IntBuffer storeIntArrayInIntBuffer(int[] data) {
        // Create a new IntBuffer
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);

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
