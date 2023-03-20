#version 460

in vec3 inPosition;

uniform mat4 mvpMatrix;

void main(void) {

	gl_Position = mvpMatrix * vec4(inPosition, 1.0);

}