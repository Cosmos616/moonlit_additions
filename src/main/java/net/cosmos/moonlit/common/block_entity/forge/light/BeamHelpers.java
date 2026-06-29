package net.cosmos.moonlit.common.block_entity.forge.light;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class BeamHelpers {
    public static float toRadians(float degrees) {
        return (float) (degrees / 180F * Math.PI);
    }

    public static float locateX(float degrees, float origin) {
        return (float) (origin*Math.cos(toRadians(degrees)));
    }

    public static float locateY(float degrees, float origin) {
        return (float) (origin*Math.sin(toRadians(degrees)));
    }

    public static Vec2 locate2DPos(float degrees, Vec2 origin) {
        float x = locateX(degrees, origin.x);
        float y = locateY(degrees, origin.y);
        return new Vec2(x, y);
    }


    public static float yaw(Vec3 angle) {
        return (float) Math.toDegrees(Math.atan2(angle.z, angle.x));
    }

    public static float pitch(Vec3 angle) {
        return (float) (Math.toDegrees(Math.atan2(Math.sqrt(angle.z * angle.z + angle.x * angle.x), angle.y)) + 180);
    }

    public static Vec3 locate3DPos(Vec3 rotation, Vec3 origin, float distance) {
        if (rotation == null) return Vec3.ZERO;
        double radX = Math.toRadians(rotation.x);
        double radY = Math.toRadians(rotation.y);
        double radZ = Math.toRadians(rotation.z);

        double cx = Math.cos(radX), sx = Math.sin(radX);
        double cy = Math.cos(radY), sy = Math.sin(radY);
        double cz = Math.cos(radZ), sz = Math.sin(radZ);

        double dirX = cy * sz + sy * sx * cz;
        double dirY = cx * cz;
        double dirZ = -sy * sz + cy * sx * cz;

        return new Vec3(
                origin.x + (dirX * distance),
                origin.y + (dirY * distance),
                origin.z + (dirZ * distance)
        );
    }

    public static Vec3 getActualEndpoint(Level level, Vec3 start, Vec3 end) {
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
        if (level == null) {
            return end;
        }
        BlockHitResult result = level.clip(context);
        if (result.getType() != HitResult.Type.MISS) {
            return result.getLocation();
        }
        return end;
    }

    public static BlockPos posFromVec(Vec3 vec3) {
        return new BlockPos.MutableBlockPos(vec3.x, vec3.y, vec3.z);
    }

    public static List<Vec3> outlineVoxelShape(BlockPos pos, VoxelShape shape) {
        List<Vec3> points = new ArrayList<>();
        List<AABB> boxes = shape.toAabbs();
        double pointsPerBlock = 16.0;
        for (AABB aabb : boxes) {
            AABB alignedAabb = aabb.move(pos).inflate(0.001);
            Vec3[] corners = getAABBCorners(alignedAabb);
            int[][] edges = {
                    {0, 1}, {1, 2}, {2, 3}, {3, 0}, // Bottom face
                    {4, 5}, {5, 6}, {6, 7}, {7, 4}, // Top face
                    {0, 4}, {1, 5}, {2, 6}, {3, 7}  // Vertical edges
            };
            for (int[] edge : edges) {
                Vec3 start = corners[edge[0]];
                Vec3 end = corners[edge[1]];
                double edgeLength = start.distanceTo(end);
                int pointsLimit = (int) Math.max(1, edgeLength * pointsPerBlock);
                for (int i = 0; i <= pointsLimit; i++) {
                    double t = (double) i / pointsLimit;
                    Vec3 point = start.lerp(end, t);
                    points.add(point);
                }
            }
        }
        for (AABB aabb : boxes) {
            AABB alignedAabb = aabb.move(pos);
            points.removeIf(alignedAabb::contains);
        }
        return points;
    }

    private static Vec3[] getAABBCorners(AABB box) {
        return new Vec3[] {
                new Vec3(box.minX, box.minY, box.minZ),
                new Vec3(box.maxX, box.minY, box.minZ),
                new Vec3(box.maxX, box.minY, box.maxZ),
                new Vec3(box.minX, box.minY, box.maxZ),
                new Vec3(box.minX, box.maxY, box.minZ),
                new Vec3(box.maxX, box.maxY, box.minZ),
                new Vec3(box.maxX, box.maxY, box.maxZ),
                new Vec3(box.minX, box.maxY, box.maxZ)
        };
    }
}
