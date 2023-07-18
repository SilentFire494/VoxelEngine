package com.libgdx.game;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class OcclusionCulling {
    private World world;

    public OcclusionCulling(World world) {
        this.world = world;
    }

    public boolean shouldOccludeVoxel(float worldX, float worldY, float worldZ) {
        int x = MathUtils.floor(worldX);
        int y = MathUtils.floor(worldY);
        int z = MathUtils.floor(worldZ);

        // Check if the coordinates are within the world bounds
        if (!isInWorldBounds(x, y, z)) {
            return false; // Coordinates are out of bounds, don't occlude
        }

        // Retrieve surrounding voxel values
        byte voxelAbove = getVoxel(x, y + 1, z);
        byte voxelBelow = getVoxel(x, y - 1, z);
        byte voxelLeft = getVoxel(x - 1, y, z);
        byte voxelRight = getVoxel(x + 1, y, z);
        byte voxelFront = getVoxel(x, y, z + 1);
        byte voxelBack = getVoxel(x, y, z - 1);

        // Perform occlusion check based on surrounding voxel values
        // If any surrounding voxel is empty, consider the voxel as not occluded
        return !(voxelAbove == 0 || voxelBelow == 0 || voxelLeft == 0 || voxelRight == 0 || voxelFront == 0 || voxelBack == 0);
    }

    public boolean shouldOccludeChunk(int chunkX, int chunkY, int chunkZ, PerspectiveCamera camera) {
        // Check if the chunk is within the camera's view frustum
        BoundingBox chunkBounds = calculateChunkBounds(chunkX, chunkY, chunkZ);
        return camera.frustum.boundsInFrustum(chunkBounds);
    }

    private BoundingBox calculateChunkBounds(int chunkX, int chunkY, int chunkZ) {
        Chunk chunk = world.getChunk(chunkX, chunkY, chunkZ);
        int chunkSize = chunk.getChunkSize();
        int voxelSize = world.getVoxelSize();

        float minX = chunkX * chunkSize * voxelSize;
        float minY = chunkY * chunkSize * voxelSize;
        float minZ = chunkZ * chunkSize * voxelSize;
        float maxX = minX + chunkSize * voxelSize;
        float maxY = minY + chunkSize * voxelSize;
        float maxZ = minZ + chunkSize * voxelSize;

        return new BoundingBox(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));
    }

    private byte getVoxel(int x, int y, int z) {
        if (isInWorldBounds(x, y, z)) {
            return world.getVoxel(x, y, z);
        }
        return 0; // Return an empty voxel if the coordinates are out of bounds
    }

    private boolean isInWorldBounds(int x, int y, int z) {
        return x >= 0 && x < world.getWorldSizeX() &&
               y >= 0 && y < world.getWorldSizeY() &&
               z >= 0 && z < world.getWorldSizeZ();
    }
}
