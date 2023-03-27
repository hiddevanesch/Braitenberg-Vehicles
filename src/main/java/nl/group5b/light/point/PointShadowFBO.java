package nl.group5b.light.point;

import org.lwjgl.opengl.GL46;

public class PointShadowFBO {
    private final int width;
    private final int height;
    private int frameBufferID;
    private int shadowCubeMapID;

    public PointShadowFBO(int width, int height) {
        this.width = width;
        this.height = height;
        initialiseFrameBuffer();
    }

    private void initialiseFrameBuffer() {
        frameBufferID = createFrameBuffer();
        shadowCubeMapID = createDepthBufferAttachment(width, height);
        unbindFrameBuffer();
    }

    private static int createFrameBuffer() {
        int frameBuffer = GL46.glGenFramebuffers();
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, frameBuffer);
        GL46.glDrawBuffer(GL46.GL_NONE);
        GL46.glReadBuffer(GL46.GL_NONE);
        return frameBuffer;
    }

    private static int createDepthBufferAttachment(int width, int height) {
        int texture = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, texture);
        for (int i = 0; i < 6; i++) {
            GL46.glTexImage2D(GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL46.GL_DEPTH_COMPONENT, width, height, 0, GL46.GL_DEPTH_COMPONENT, GL46.GL_FLOAT, 0);
        }
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_R, GL46.GL_CLAMP_TO_EDGE);
        GL46.glFramebufferTexture(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_ATTACHMENT, texture, 0);
        return texture;
    }

    public void bindFrameBuffer(int frameBuffer, int width, int height) {
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, 0);
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, frameBuffer);
        GL46.glViewport(0, 0, width, height);
    }

    public void unbindFrameBuffer() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }

    protected void cleanUp() {
        GL46.glDeleteFramebuffers(frameBufferID);
        GL46.glDeleteTextures(shadowCubeMapID);
    }

    public int getFrameBuffer() {
        return frameBufferID;
    }
}
