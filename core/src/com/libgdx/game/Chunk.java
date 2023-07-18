package com.libgdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Chunk {
    private byte[] voxels;
    private Color[] voxelColors;
    private int chunkSize;
    private int chunkArea;
    private Vector3 position;

    public Chunk(int chunkSize, Vector3 position) {
        this.chunkSize = chunkSize;
        this.chunkArea = chunkSize * chunkSize;
        this.voxels = new byte[chunkArea * chunkSize];
        this.voxelColors = new Color[chunkArea * chunkSize];
        this.position = position;

        generateRandomColors();
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setVoxel(int x, int y, int z, byte value) {
        int index = getIndex(x, y, z);
        voxels[index] = value;
    }

    public byte getVoxel(int x, int y, int z) {
        int index = getIndex(x, y, z);
        return voxels[index];
    }

    public Color getVoxelColor(int x, int y, int z) {
        int index = getIndex(x, y, z);
        return voxelColors[index];
    }

    private void generateRandomColors() {
        float colorVariation = 0.5f;
        float minColor = MathUtils.clamp(0.5f - colorVariation / 2f, 0f, 1f);
        float maxColor = MathUtils.clamp(0.5f + colorVariation / 2f, 0f, 1f);

        for (int i = 0; i < voxelColors.length; i++) {
            float red = MathUtils.random(minColor, maxColor);
            float green = MathUtils.random(minColor, maxColor);
            float blue = MathUtils.random(minColor, maxColor);

            voxelColors[i] = new Color(red, green, blue, 1f);
        }
    }

    private int getIndex(int x, int y, int z) {
        return x + y * chunkSize + z * chunkArea;
    }

    public Vector3 getPosition() {
        return position;
    }
}
