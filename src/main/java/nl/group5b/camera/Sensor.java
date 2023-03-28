package nl.group5b.camera;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class Sensor {

    private final int width;
    private final int height;
    private final int frameBufferID;
    private final int textureID;
    private final Camera camera;

    public Sensor(Vector3f position, Vector3f rotation, int resolution) {
        this.width = resolution;
        this.height = resolution;
        this.camera = new Camera(position, rotation);
        this.frameBufferID = createFrameBuffer();
        this.textureID = createTexture(width, height);
    }

    private int createFrameBuffer() {
        int frameBuffer = GL46.glGenFramebuffers();
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, frameBuffer);
        GL46.glFramebufferRenderbuffer(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_ATTACHMENT, GL46.GL_RENDERBUFFER, GL46.glGenRenderbuffers());
        GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH_COMPONENT, width, height);

        // Create a depth buffer attachment
        int depthBuffer = GL46.glGenRenderbuffers();
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, depthBuffer);
        GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH_COMPONENT, width, height);
        GL46.glFramebufferRenderbuffer(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_ATTACHMENT, GL46.GL_RENDERBUFFER, depthBuffer);

        return frameBuffer;
    }

    private int createTexture(int width, int height) {
        int texture = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture);
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGB, width, height, 0, GL46.GL_RGB, GL46.GL_UNSIGNED_BYTE, 0);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR);
        GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT0, GL46.GL_TEXTURE_2D, texture, 0);
        return texture;
    }

    public void bind() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, frameBufferID);
        GL46.glViewport(0, 0, width, height);
    }

    public void unbind() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }

    public int getTextureID() {
        return textureID;
    }

    public void cleanUp() {

    }

    public void setPosition(Vector3f position) {
        this.camera.setPosition(position);
    }

    public void setRotation(Vector3f rotation) {
        this.camera.setRotation(rotation);
    }

    public Vector3f getPosition() {
        return this.camera.getPosition();
    }

    public Vector3f getRotation() {
        return this.camera.getRotation();
    }

    public Camera getCamera() {
        return camera;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float calculateSensorBrightness() {
        float brightness = 0.0f;
        int totalPixels = width * height;

        // Create a buffer to store the data
        ByteBuffer buffer = BufferUtils.createByteBuffer(totalPixels * 4);

        // Bind the Texture and read the data
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, textureID);
        GL46.glGetTexImage(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGB, GL46.GL_UNSIGNED_BYTE, buffer);

        // Compute luminance
        for (int i = 0; i < totalPixels; i++) {
            float r = (buffer.get() & 0xFF) / 255.0f;
            float g = (buffer.get() & 0xFF) / 255.0f;
            float b = (buffer.get() & 0xFF) / 255.0f;
            brightness += (0.2126f * r + 0.7152f * g + 0.0722f * b);
        }

        return brightness / totalPixels;
    }
}
