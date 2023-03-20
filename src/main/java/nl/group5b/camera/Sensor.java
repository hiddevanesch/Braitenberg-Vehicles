package nl.group5b.camera;

import org.lwjgl.opengl.GL46;

public class Sensor {

    private int createFrameBuffer() {
        int frameBuffer = GL46.glGenFramebuffers();
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, frameBuffer);
        GL46.glDrawBuffer(GL46.GL_COLOR_ATTACHMENT0);
        return frameBuffer;
    }

    private int createTexture(int width, int height) {
        // TODO only need brightness, not color
        int texture = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture);
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGB, width, height, 0, GL46.GL_RGB, GL46.GL_UNSIGNED_BYTE, 0);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR);
        GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_COLOR_ATTACHMENT0, GL46.GL_TEXTURE_2D, texture, 0);
        return texture;
    }

    public void cleanUp() {

    }
}
