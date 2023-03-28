package nl.group5b.light;

import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class DirectionalShadowFBO {

	private final int width;
	private final int height;
	private int frameBufferID;
	private int shadowMapID;

	protected DirectionalShadowFBO(int width, int height) {
		this.width = width;
		this.height = height;
		initialiseFrameBuffer();
	}

	protected void cleanUp() {
		GL46.glDeleteFramebuffers(frameBufferID);
		GL46.glDeleteTextures(shadowMapID);
	}

	protected void bindFrameBuffer() {
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
		GL46.glBindFramebuffer(GL46.GL_DRAW_FRAMEBUFFER, frameBufferID);
		GL46.glViewport(0, 0, width, height);
	}

	protected void unbindFrameBuffer() {
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
	}

	protected int getShadowMap() {
		return shadowMapID;
	}

	private void initialiseFrameBuffer() {
		frameBufferID = createFrameBuffer();
		shadowMapID = createDepthBufferAttachment(width, height);
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
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture);
		GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_DEPTH_COMPONENT16, width, height, 0,
				GL46.GL_DEPTH_COMPONENT, GL46.GL_FLOAT, (ByteBuffer) null);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);
		GL46.glFramebufferTexture(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_ATTACHMENT, texture, 0);
		return texture;
	}
}
