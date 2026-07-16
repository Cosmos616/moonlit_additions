package net.cosmos.moonlit.client.reflection;

import org.joml.Matrix4f;

public final class ViewBobbingCapture {

    private static final Matrix4f BEFORE_BOB = new Matrix4f();
    private static final Matrix4f INVERSE_BOB = new Matrix4f();

    private static boolean capturedBefore;
    private static boolean valid;

    private ViewBobbingCapture() {
    }

    public static void captureBefore(Matrix4f matrix) {
        BEFORE_BOB.set(matrix);
        capturedBefore = true;
        valid = false;
    }

    public static void captureAfter(Matrix4f matrix) {
        if (!capturedBefore) {
            valid = false;
            return;
        }

        /*
         * PoseStack/JOML transformations are normally appended:
         *
         * after = before * bob
         *
         * Therefore:
         *
         * bob = inverse(before) * after
         */
        Matrix4f bobMatrix = new Matrix4f(BEFORE_BOB)
                .invert()
                .mul(matrix);

        INVERSE_BOB.set(bobMatrix).invert();

        valid = true;
        capturedBefore = false;
    }

    public static Matrix4f getInverseBob(Matrix4f destination) {
        if (!valid) {
            return destination.identity();
        }

        return destination.set(INVERSE_BOB);
    }

    public static void reset() {
        capturedBefore = false;
        valid = false;
        INVERSE_BOB.identity();
    }
}