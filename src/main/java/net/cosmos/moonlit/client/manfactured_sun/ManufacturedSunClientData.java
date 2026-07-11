package net.cosmos.moonlit.client.manfactured_sun;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class ManufacturedSunClientData {
    private ManufacturedSunClientData() {}

    public static boolean active = false;

    public static Vec3 center = Vec3.ZERO;
    public static Vector3f color = new Vector3f(1.0F, 0.75F, 0.28F);

    public static float radius = 8.0F;
    public static float intensity = 1.25F;

    public static long lastSubmittedGameTime = Long.MIN_VALUE;

    public static void submit(Vec3 centerIn, Vector3f colorIn, float radiusIn, float intensityIn, long gameTime) {
        active = true;
        center = centerIn;
        color.set(colorIn);
        radius = radiusIn;
        intensity = intensityIn;
        lastSubmittedGameTime = gameTime;
    }

    public static void clearIfStale(long gameTime) {
        // If no renderer submitted this frame/tick, stop rendering the post effect.
        if (gameTime - lastSubmittedGameTime > 1L) {
            active = false;
        }
    }
}