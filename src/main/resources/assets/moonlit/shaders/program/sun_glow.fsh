#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 InvProjMat;

uniform vec3 SunCenterView;
uniform float SunRadius;
uniform vec3 SunColor;
uniform float SunIntensity;

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

    vec4 viewPos = InvProjMat * clipPos;

    if (abs(viewPos.w) < 0.000001) {
        return vec3(0.0 / 0.0);
    }

    viewPos /= viewPos.w;

    return viewPos.xyz;
}

void main() {
    vec4 baseColor = texture(DiffuseSampler, texCoord);
    float depth = texture(DepthSampler, texCoord).r;

    if (depth >= 1.0) {
        fragColor = vec4(baseColor.rgb, 1.0);
        return;
    }

    vec3 viewPos = getViewPositionFromDepth(texCoord, depth);

    if (invalidVec3(viewPos)) {
        fragColor = vec4(baseColor.rgb, 1.0);
        return;
    }

    float distanceToSun = length(viewPos - SunCenterView);

    if (isnan(distanceToSun) || isinf(distanceToSun)) {
        fragColor = vec4(baseColor.rgb, 1.0);
        return;
    }

    float falloff = 1.0 - smootherstep(0.0, SunRadius, distanceToSun);
    falloff *= falloff;

    vec3 glow = SunColor * falloff * SunIntensity;

    fragColor = vec4(baseColor.rgb + glow, 1.0);
}