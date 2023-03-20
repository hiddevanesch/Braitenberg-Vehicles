package nl.group5b.light;

import java.nio.ByteBuffer;

import nl.group5b.engine.DisplayBuilder;
import org.lwjgl.opengl.GL46;

public class ShadowFrameBuffer {

	private final int WIDTH;
	private final int HEIGHT;
	private int fbo;
	private int shadowMap;

	protected ShadowFrameBuffer(int width, int height) {
		this.WIDTH = width;
		this.HEIGHT = height;
		initialiseFrameBuffer();
	}

	protected void cleanUp() {
		GL46.glDeleteFramebuffers(fbo);
		GL46.glDeleteTextures(shadowMap);
	}

	protected void bindFrameBuffer() {
		bindFrameBuffer(fbo, WIDTH, HEIGHT);
	}

	protected void unbindFrameBuffer() {
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
		GL46.glViewport(0, 0, DisplayBuilder.getWidth(), DisplayBuilder.getHeight());
	}

	protected int getShadowMap() {
		return shadowMap;
	}

	private void initialiseFrameBuffer() {
		fbo = createFrameBuffer();
		shadowMap = createDepthBufferAttachment(WIDTH, HEIGHT);
		unbindFrameBuffer();
	}

	private static void bindFrameBuffer(int frameBuffer, int width, int height) {
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
		GL46.glBindFramebuffer(GL46.GL_DRAW_FRAMEBUFFER, frameBuffer);
		GL46.glViewport(0, 0, width, height);
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
