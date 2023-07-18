package com.libgdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class World {
    private Chunk[] chunks;
    private int worldSizeX;
    private int worldSizeY;
    private int worldSizeZ;
    private int chunkSize;
    private int voxelSize;
    private int totalChunks;
    private FastNoiseLite noiseGenerator;

    public World(int worldSizeX, int worldSizeY, int worldSizeZ, int chunkSize, int voxelSize, int seed) {
        this.worldSizeX = worldSizeX;
        this.worldSizeY = worldSizeY;
        this.worldSizeZ = worldSizeZ;
        this.chunkSize = chunkSize;
        this.voxelSize = voxelSize;
        this.totalChunks = worldSizeX * worldSizeY * worldSizeZ;

        chunks = new Chunk[totalChunks];
        noiseGenerator = new FastNoiseLite(seed);
        generateWorld();
    }

    public int getWorldSizeX() {
        return worldSizeX * chunkSize * voxelSize;
    }

    public int getWorldSizeY() {
        return worldSizeY * chunkSize * voxelSize;
    }

    public int getWorldSizeZ() {
        return worldSizeZ * chunkSize * voxelSize;
    }

    public int getVoxelSize() {
        return voxelSize;
    }

    private void generateWorld() {
        for (int x = 0; x < worldSizeX; x++) {
            for (int y = 0; y < worldSizeY; y++) {
                for (int z = 0; z < worldSizeZ; z++) {
                    int chunkIndex = getChunkIndex(x, y, z);
                    Vector3 chunkPosition = new Vector3(x * chunkSize, y * chunkSize, z * chunkSize);
                    Chunk chunk = generateChunk(chunkPosition);
                    chunks[chunkIndex] = chunk;
                }
            }
        }
    }

    private Chunk generateChunk(Vector3 position) {
        Chunk chunk = new Chunk(chunkSize, position);

        int maxHeight = chunkSize / 2; // Define the maximum height of the terrain
        float hillScale = 0.1f; // Adjust the hilliness of the terrain (lower values for smoother terrain, higher values for more pronounced hills)
        float valleyScale = 0.1f; // Adjust the depth of the valleys (lower values for shallow valleys, higher values for deeper valleys)

        for (int x = 0; x < chunkSize; x++) {
            for (int y = 0; y < chunkSize; y++) {
                for (int z = 0; z < chunkSize; z++) {
                    float noiseValue = noiseGenerator.GetNoise(position.x + x, position.y + y, position.z + z);
                    float hillValue = noiseGenerator.GetNoise(position.x + x + 1000, position.y + y + 1000, position.z + z + 1000);
                    float valleyValue = noiseGenerator.GetNoise(position.x + x - 1000, position.y + y - 1000, position.z + z - 1000);

                    // Adjust the height based on the noise values and scales
                    int height = MathUtils.floor(maxHeight * (noiseValue + 1f) / 2f);
                    height += MathUtils.floor(maxHeight * hillValue * hillScale);
                    height -= MathUtils.floor(maxHeight * valleyValue * valleyScale);

                    if (position.y + y < height) {
                        chunk.setVoxel(x, y, z, (byte) 1); // Set voxel to a non-zero value for terrain
                    }
                }
            }
        }

        return chunk;
    }

    public Chunk getChunk(int x, int y, int z) {
        if (isChunkOutOfBounds(x, y, z)) {
            return null;
        }

        return chunks[getChunkIndex(x, y, z)];
    }

    public byte getVoxel(int x, int y, int z) {
        int chunkX = x / chunkSize;
        int chunkY = y / chunkSize;
        int chunkZ = z / chunkSize;

        if (isChunkOutOfBounds(chunkX, chunkY, chunkZ)) {
            return 0;
        }

        Chunk chunk = chunks[getChunkIndex(chunkX, chunkY, chunkZ)];
        int voxelX = x % chunkSize;
        int voxelY = y % chunkSize;
        int voxelZ = z % chunkSize;

        return chunk.getVoxel(voxelX, voxelY, voxelZ);
    }

    public Color getVoxelColor(int x, int y, int z) {
        int chunkX = x / chunkSize;
        int chunkY = y / chunkSize;
        int chunkZ = z / chunkSize;

        if (isChunkOutOfBounds(chunkX, chunkY, chunkZ)) {
            return Color.BLACK;
        }

        Chunk chunk = chunks[getChunkIndex(chunkX, chunkY, chunkZ)];
        int voxelX = x % chunkSize;
        int voxelY = y % chunkSize;
        int voxelZ = z % chunkSize;

        return chunk.getVoxelColor(voxelX, voxelY, voxelZ);
    }

    private boolean isChunkOutOfBounds(int x, int y, int z) {
        return x < 0 || x >= worldSizeX || y < 0 || y >= worldSizeY || z < 0 || z >= worldSizeZ;
    }

    private int getChunkIndex(int x, int y, int z) {
        return x + y * worldSizeX + z * (worldSizeX * worldSizeY);
    }
}
