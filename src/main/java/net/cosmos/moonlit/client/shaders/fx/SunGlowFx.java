package net.cosmos.moonlit.client.shaders.fx;

import org.joml.Vector3f;
import team.lodestar.lodestone.systems.postprocess.DynamicShaderFxInstance;

import java.util.function.BiConsumer;

public class SunGlowFx extends DynamicShaderFxInstance {
    public Vector3f center;
    public Vector3f color;
    public float radius;
    public float intensity;

    public SunGlowFx(Vector3f center, Vector3f color, float radius, float intensity) {
        this.center = center;
        this.color = color;
        this.radius = radius;
        this.intensity = intensity;
    }

    @Override
    public void writeDataToBuffer(BiConsumer<Integer, Float> writer) {
        writer.accept(0, center.x());
        writer.accept(1, center.y());
        writer.accept(2, center.z());
        writer.accept(3, color.x());
        writer.accept(4, color.y());
        writer.accept(5, color.z());
        writer.accept(6, radius);
        writer.accept(7, intensity);
    }
}
