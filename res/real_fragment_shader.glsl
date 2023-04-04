# version 460

#define LIGHT_COUNT 1 // Will be overwritten upon compilation

#define SHADOW_FACE_OFFSET 0.0011
#define SHADOW_MAP_SIZE 4096 // Will be overwritten upon compilation
#define TEXEL_SIZE (1.0 / SHADOW_MAP_SIZE)

#define PCF_RADIUS 3
#define PCF_KERNEL_SIZE (2 * PCF_RADIUS + 1)
#define PCF_TOTAL_KERNEL_SIZE (PCF_KERNEL_SIZE * PCF_KERNEL_SIZE)

in vec3 surfaceNormal;
in vec3 toLight[LIGHT_COUNT];
in vec3 toCamera;
in vec4 shadowCoordinates;

out vec4 outColour;

uniform sampler2D shadowMap;

uniform vec3 lightColour[LIGHT_COUNT];
uniform vec3 lightAttenuation[LIGHT_COUNT];
uniform vec3 colour;
uniform bool isEmissive;
uniform float damping;
uniform float shininess;
uniform float gammaCorrection;
uniform float ambientLight;

void main(void) {
    if (isEmissive) {
        outColour = vec4(colour, 1.0);
    } else {
        // Compute the shadow factor
        float totalShadow = 0.0;
        for (int x = -PCF_RADIUS; x <= PCF_RADIUS; x++) {
            for (int y = -PCF_RADIUS; y <= PCF_RADIUS; y++) {
                float objectNearestLight = texture(shadowMap,
                    shadowCoordinates.xy + vec2(x, y) * TEXEL_SIZE).r + SHADOW_FACE_OFFSET;
                if (shadowCoordinates.z > objectNearestLight) {
                    totalShadow += 1.0;
                }
            }
        }
        totalShadow /= PCF_TOTAL_KERNEL_SIZE;
        float shadowFactor = 1.0 - (totalShadow);

        // Normalise the vectors
        vec3 unitNormal = normalize(surfaceNormal);
        vec3 unitCamera = normalize(toCamera);

        vec3 totalDiffuse = vec3(0.0, 0.0, 0.0);
        vec3 totalSpecular = vec3(0.0, 0.0, 0.0);

        for (int i = 0; i < LIGHT_COUNT; i++) {
            float distanceToLight = length(toLight[i]);
            float attenuationFactor = lightAttenuation[i].x + lightAttenuation[i].y * distanceToLight
            + lightAttenuation[i].z * distanceToLight * distanceToLight;

            vec3 unitLight = normalize(toLight[i]);

            // Calculate the diffuse component
            float prod = dot(unitNormal, unitLight);
            float brightness = clamp(prod, 0.0, 1.0);
            if (i == 0) {
                totalDiffuse += ((colour * lightColour[i] * brightness) / attenuationFactor) * shadowFactor;
            } else {
                totalDiffuse += ((colour * lightColour[i] * brightness) / attenuationFactor);
            }

            // Calculate the specular component
            vec3 lightDirection = -unitLight;
            vec3 reflectedDirection = reflect(lightDirection, unitNormal);
            float specularFactor = dot(unitCamera, reflectedDirection);
            specularFactor = clamp(specularFactor, 0.0, 1.0);
            float dampedFactor = pow(specularFactor, damping);
            if (i == 0) {
                totalSpecular += ((dampedFactor * shininess * lightColour[i]) / attenuationFactor) * shadowFactor;
            } else {
                totalSpecular += (dampedFactor * shininess * lightColour[i]) / attenuationFactor;
            }
        }

        // Add ambient light
        totalDiffuse = max(totalDiffuse, ambientLight * colour);

        // Compute out colour
        outColour = vec4(pow(totalDiffuse + totalSpecular, vec3(1.0/gammaCorrection)), 1.0);
    }
}