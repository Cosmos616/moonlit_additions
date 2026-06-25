package net.cosmos.moonlit_additions.client.shockwave;
import org.joml.Vector3f;

public class ShockwaveClientData {
    public static boolean active = false;

    public static final Vector3f center = new Vector3f();
    public static float radius = 0.0F;
    public static float alpha = 0.0F;

    public static void clear() {
        active = false;
        radius = 0.0F;
        alpha = 0.0F;
    }
}