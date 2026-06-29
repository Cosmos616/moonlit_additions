package net.cosmos.moonlit.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;

public class OrientedBoundingBox {
    public Vec3 center;

    public Vec3 extent;

    public Vec3 axisX;
    public Vec3 axisY;
    public Vec3 axisZ;

    public Vec3 scaledAxisX;
    public Vec3 scaledAxisY;
    public Vec3 scaledAxisZ;
    public Matrix3f rotation = new Matrix3f();
    public Vec3 vertex1;
    public Vec3 vertex2;
    public Vec3 vertex3;
    public Vec3 vertex4;
    public Vec3 vertex5;
    public Vec3 vertex6;
    public Vec3 vertex7;
    public Vec3 vertex8;
    public Vec3[] vertices;

    public OrientedBoundingBox(Vec3 center, double width, double height, double depth, float yaw, float pitch) {
        this.center = center;
        this.extent = new Vec3(width/2.0, height/2.0, depth/2.0);
        this.axisZ = Vec3.directionFromRotation(yaw, pitch).normalize();
        this.axisY = Vec3.directionFromRotation(yaw + 90, pitch).reverse().normalize();
        this.axisX = axisZ.cross(axisY);
    }

    public OrientedBoundingBox(Vec3 center, Vec3 size, float yaw, float pitch) {
        this(center,size.x, size.y, size.z, yaw, pitch);
    }

    public OrientedBoundingBox(AABB box) {
        this.center = new Vec3((box.maxX + box.minX) / 2.0, (box.maxY + box.minY) / 2.0, (box.maxZ + box.minZ) / 2.0);
        this.extent = new Vec3(Math.abs(box.maxX - box.minX) / 2.0, Math.abs(box.maxY - box.minY) / 2.0, Math.abs(box.maxZ - box.minZ) / 2.0);
        this.axisX = new Vec3(1, 0, 0);
        this.axisY = new Vec3(0, 1, 0);
        this.axisZ = new Vec3(0, 0, 1);
    }

    public OrientedBoundingBox(OrientedBoundingBox obb) {
        this.center = obb.center;
        this.extent = obb.extent;
        this.axisX = obb.axisX;
        this.axisY = obb.axisY;
        this.axisZ = obb.axisZ;
    }

    public OrientedBoundingBox copy() {
        return new OrientedBoundingBox(this);
    }

    public OrientedBoundingBox offsetAlongAxisX(double offset) {
        this.center = this.center.add(axisX.multiply(offset, offset, offset));
        return this;
    }

    public OrientedBoundingBox offsetAlongAxisY(double offset) {
        this.center = this.center.add(axisY.multiply(offset, offset, offset));
        return this;
    }

    public OrientedBoundingBox offsetAlongAxisZ(double offset) {
        this.center = this.center.add(axisZ.multiply(offset, offset, offset));
        return this;
    }

    public OrientedBoundingBox offset(Vec3 offset) {
        this.center = this.center.add(offset);
        return this;
    }

    public OrientedBoundingBox scale(double scale) {
        this.extent = this.extent.multiply(scale, scale, scale);
        return this;
    }

    public OrientedBoundingBox updateVertex() {
        rotation.set(0,0, (float) axisX.x);
        rotation.set(0,1, (float) axisX.y);
        rotation.set(0,2, (float) axisX.z);
        rotation.set(1,0, (float) axisY.x);
        rotation.set(1,1, (float) axisY.y);
        rotation.set(1,2, (float) axisY.z);
        rotation.set(2,0, (float) axisZ.x);
        rotation.set(2,1, (float) axisZ.y);
        rotation.set(2,2, (float) axisZ.z);

        scaledAxisX = axisX.multiply(extent.x, extent.x, extent.x);
        scaledAxisY = axisY.multiply(extent.y, extent.y, extent.y);
        scaledAxisZ = axisZ.multiply(extent.z, extent.z, extent.z);

        vertex1 = center.subtract(scaledAxisZ).subtract(scaledAxisX).subtract(scaledAxisY);
        vertex2 = center.subtract(scaledAxisZ).add(scaledAxisX).subtract(scaledAxisY);
        vertex3 = center.subtract(scaledAxisZ).add(scaledAxisX).add(scaledAxisY);
        vertex4 = center.subtract(scaledAxisZ).subtract(scaledAxisX).add(scaledAxisY);
        vertex5 = center.add(scaledAxisZ).subtract(scaledAxisX).subtract(scaledAxisY);
        vertex6 = center.add(scaledAxisZ).add(scaledAxisX).subtract(scaledAxisY);
        vertex7 = center.add(scaledAxisZ).add(scaledAxisX).add(scaledAxisY);
        vertex8 = center.add(scaledAxisZ).subtract(scaledAxisX).add(scaledAxisY);

        vertices = new Vec3[]{
                vertex1,
                vertex2,
                vertex3,
                vertex4,
                vertex5,
                vertex6,
                vertex7,
                vertex8
        };

        return this;
    }

    public boolean contains(Vec3 point) {
        var distance = point.subtract(center).toVector3f();
        distance.mulTranspose(rotation);
        return Math.abs(distance.x()) < extent.x &&
                Math.abs(distance.y()) < extent.y &&
                Math.abs(distance.z()) < extent.z;
    }

    public boolean intersects(AABB boundingBox) {
        var otherOBB = new OrientedBoundingBox(boundingBox).updateVertex();
        return Intersects(this, otherOBB);
    }

    public boolean intersects(OrientedBoundingBox otherOBB) {
        return Intersects(this, otherOBB);
    }

    public static boolean Intersects(OrientedBoundingBox a, OrientedBoundingBox b)  {
        if (Separated(a.vertices, b.vertices, a.scaledAxisX))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisY))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisZ))
            return false;

        if (Separated(a.vertices, b.vertices, b.scaledAxisX))
            return false;
        if (Separated(a.vertices, b.vertices, b.scaledAxisY))
            return false;
        if (Separated(a.vertices, b.vertices, b.scaledAxisZ))
            return false;

        if (Separated(a.vertices, b.vertices, a.scaledAxisX.cross(b.scaledAxisX)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisX.cross(b.scaledAxisY)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisX.cross(b.scaledAxisZ)))
            return false;

        if (Separated(a.vertices, b.vertices, a.scaledAxisY.cross(b.scaledAxisX)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisY.cross(b.scaledAxisY)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisY.cross(b.scaledAxisZ)))
            return false;

        if (Separated(a.vertices, b.vertices, a.scaledAxisZ.cross(b.scaledAxisX)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisZ.cross(b.scaledAxisY)))
            return false;
        if (Separated(a.vertices, b.vertices, a.scaledAxisZ.cross(b.scaledAxisZ)))
            return false;

        return true;
    }

    private static boolean Separated(Vec3[] vertsA, Vec3[] vertsB, Vec3 axis)  {
        if (axis.equals(Vec3.ZERO))
            return false;

        var aMin = Double.POSITIVE_INFINITY;
        var aMax = Double.NEGATIVE_INFINITY;
        var bMin = Double.POSITIVE_INFINITY;
        var bMax = Double.NEGATIVE_INFINITY;

        for (var i = 0; i < 8; i++)
        {
            var aDist = vertsA[i].dot(axis);
            aMin = Math.min(aDist, aMin);
            aMax = Math.max(aDist, aMax);
            var bDist = vertsB[i].dot(axis);
            bMin = Math.min(bDist, bMin);
            bMax = Math.max(bDist, bMax);
        }

        var longSpan = Math.max(aMax, bMax) - Math.min(aMin, bMin);
        var sumSpan = aMax - aMin + bMax - bMin;
        return longSpan >= sumSpan;
    }

    public void drawOutline(PoseStack poseStack) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0f);
        outlineOBB(poseStack, bufferBuilder,
                0, 1, 0,
                1, 1, 0,0.5F);
        BufferUploader.draw(bufferBuilder.buildOrThrow());

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableBlend();
    }

    public void outlineOBB(PoseStack poseStack, VertexConsumer vertexConsumer,
                           float red1, float green1, float blue1,
                           float red2, float green2, float blue2,
                           float alpha) {
        var top = poseStack.last();
        vertexConsumer.addVertex(top, (float) this.vertex1.x, (float) this.vertex1.y, (float) this.vertex1.z).setColor(0, 0, 0, 0).setNormal(top, 1,1,1);

        vertexConsumer.addVertex(top, (float) this.vertex1.x, (float) this.vertex1.y, (float) this.vertex1.z).setColor(red1, green1, blue1, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex2.x, (float) this.vertex2.y, (float) this.vertex2.z).setColor(red1, green1, blue1, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex3.x, (float) this.vertex3.y, (float) this.vertex3.z).setColor(red1, green1, blue1, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex4.x, (float) this.vertex4.y, (float) this.vertex4.z).setColor(red1, green1, blue1, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex1.x, (float) this.vertex1.y, (float) this.vertex1.z).setColor(red1, green1, blue1, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex5.x, (float) this.vertex5.y, (float) this.vertex5.z).setColor(red2, green2, blue2, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex6.x, (float) this.vertex6.y, (float) this.vertex6.z).setColor(red2, green2, blue2, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex2.x, (float) this.vertex2.y, (float) this.vertex2.z).setColor(red1, green1, blue1, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex6.x, (float) this.vertex6.y, (float) this.vertex6.z).setColor(red2, green2, blue2, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex7.x, (float) this.vertex7.y, (float) this.vertex7.z).setColor(red2, green2, blue2, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex3.x, (float) this.vertex3.y, (float) this.vertex3.z).setColor(red1, green1, blue1, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex7.x, (float) this.vertex7.y, (float) this.vertex7.z).setColor(red2, green2, blue2, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex8.x, (float) this.vertex8.y, (float) this.vertex8.z).setColor(red2, green2, blue2, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex4.x, (float) this.vertex4.y, (float) this.vertex4.z).setColor(red1, green1, blue1, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex8.x, (float) this.vertex8.y, (float) this.vertex8.z).setColor(red2, green2, blue2, alpha).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.vertex5.x, (float) this.vertex5.y, (float) this.vertex5.z).setColor(red2, green2, blue2, alpha).setNormal(top, 1,1,1);

        vertexConsumer.addVertex(top, (float) this.vertex5.x, (float) this.vertex5.y, (float) this.vertex5.z).setColor(0, 0, 0, 0).setNormal(top, 1,1,1);
        vertexConsumer.addVertex(top, (float) this.center.x, (float) this.center.y, (float) this.center.z).setColor(0, 0, 0, 0).setNormal(top, 1,1,1);
    }
}
