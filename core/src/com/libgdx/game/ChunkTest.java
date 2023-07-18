package com.libgdx.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChunkTest {
    @Test
    void testSetAndGetVoxel() {
        Chunk chunk = new Chunk(8, new Vector3(0, 0, 0));
        chunk.setVoxel(1, 2, 3, (byte) 1);
        byte voxel = chunk.getVoxel(1, 2, 3);
        Assertions.assertEquals(1, voxel);
    }

    @Test
    void testGetInvalidVoxel() {
        Chunk chunk = new Chunk(8, new Vector3(0, 0, 0));
        byte voxel = chunk.getVoxel(10, 10, 10);
        Assertions.assertEquals(0, voxel);
    }

    @Test
    void testGetVoxelColor() {
        Chunk chunk = new Chunk(8, new Vector3(0, 0, 0));
        Color color = chunk.getVoxelColor(1, 2, 3);
        Assertions.assertNotNull(color);
    }

    @Test
    void testChunkSize() {
        Chunk chunk = new Chunk(8, new Vector3(0, 0, 0));
        int chunkSize = chunk.getChunkSize();
        Assertions.assertEquals(8, chunkSize);
    }
}
