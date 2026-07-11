#version 150

#moj_import <lodestone:common_math.glsl>
#moj_import <lodestone:multi.glsl>

uniform sampler2D DiffuseSampler;
uniform sampler2D MainDepthSampler;
// Multi-Instance uniforms
uniform samplerBuffer DataBuffer;
uniform int InstanceCount;
// Matrices needed for world position calculation
uniform mat4 invProjMat;
uniform mat4 invViewMat;
uniform vec3 cameraPos;

in vec2 texCoord;
out vec4 fragColor;

float smootherstep(float edge0, float edge1, float x) {
    x = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    return x * x * x * (x * (x * 6.0 - 15.0) + 10.0);
}

bool invalidVec3(vec3 v) {
    return any(isnan(v)) || any(isinf(v)) || length(v) > 1000000.0;
}

vec3 getViewPositionFromDepth(vec2 uv, float depth) {
    vec4 clipPos = vec4(
    uv.x * 2.0 - 1.0,
    uv.y * 2.0 - 1.0,
    depth * 2.0 - 1.0,
    1.0
    );

    vec4 viewPos = invProjMat * clipPos;

    if (abs(viewPos.w) < 0.000001) {
        return vec3(0.0 / 0.0);
    }

    viewPos /= viewPos.w;

    return viewPos.xyz;
}

void main() {
    vec4 diffuseColor = texture(DiffuseSampler, texCoord);
    vec3 worldPos = getWorldPos(MainDepthSampler, texCoord, invProjMat, invViewMat, cameraPos);
    float depth = texture(MainDepthSampler, texCoord).r;
    // Its important to set the fragColor to the diffuseColor before applying the effect!
    fragColor = diffuseColor;

    for (int instance = 0; instance < InstanceCount; instance++) {
        int index = instance * 8;
        vec3 center = fetch3(DataBuffer, index);
        vec3 color = fetch3(DataBuffer, index + 3);
        float radius = fetch(DataBuffer, index + 7);
        float intensity = fetch(DataBuffer, index + 8);
        vec3 centerView = vec3(center.x - cameraPos.x, center.y - cameraPos.y, center.z - cameraPos.z);
        if (depth >= 1.0) {
            fragColor = vec4(diffuseColor.rgb, 1.0);
            return;
        }
        vec3 viewPos = getViewPositionFromDepth(texCoord, depth);
        if (invalidVec3(viewPos)) {
            fragColor = vec4(diffuseColor.rgb, 1.0);
            return;
        }
        float distanceToSun = length(worldPos - center);
        if (isnan(distanceToSun) || isinf(distanceToSun)) {
            fragColor = vec4(diffuseColor.rgb, 1.0);
            return;
        }
        float falloff = 1.0 - smootherstep(0.0, radius, distanceToSun);
        falloff *= falloff;

        vec3 glow = color * falloff * intensity;
        fragColor = vec4(diffuseColor.rgb + glow, 1.0);
    }
}
