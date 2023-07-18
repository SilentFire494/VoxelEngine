package com.libgdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class VoxelEngine extends Game {
    private ShapeRenderer shapeRenderer;
    private World world;
    private int voxelSize;
    private int chunkSize;
    private PerspectiveCamera camera;
    private float cameraAngleX;
    private float cameraAngleY;
    private FPSLogger fpsLogger;
    private OcclusionCulling occlusionCulling;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        voxelSize = 1;
        chunkSize = 16; // Change this to adjust the size of the chunk

        // Create a new world
        int seed = MathUtils.random(0, 1000000);
        world = new World(5, 5, 5, chunkSize, voxelSize, seed); // Example parameters
        occlusionCulling = new OcclusionCulling(world);

        // Initialize the camera
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f); // Set the initial camera position
        camera.lookAt(0f, 0f, 0f); // Set the initial camera look-at position
        camera.near = 1f; // Set the near clipping plane
        camera.far = 100f; // Set the far clipping plane
        camera.update();

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);

        fpsLogger = new FPSLogger();
    }

    @Override
    public void render() {
        fpsLogger.log();
    
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    
        handleCameraControls();
    
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    
        int renderedChunks = 0; // Counter for rendered chunks
        int renderedObjects = 0; // Counter for rendered objects
    
        // Render visible chunks with occlusion culling
        for (int x = 0; x < world.getWorldSizeX(); x++) {
            for (int y = 0; y < world.getWorldSizeY(); y++) {
                for (int z = 0; z < world.getWorldSizeZ(); z++) {
                    Chunk chunk = world.getChunk(x, y, z);
                    if (chunk == null || !occlusionCulling.shouldOccludeChunk(x, y, z, camera)) {
                        continue;
                    }
    
                    renderedChunks++; // Increment the counter for rendered chunks
                    renderChunk(chunk); // Increment the counter for rendered objects
                }
            }
        }
    
        System.out.println("Rendered Chunks: " + renderedChunks); // Print the number of rendered chunks
    
        shapeRenderer.end();
    }

    private void handleCameraControls() {
        float cameraMoveSpeed = 5f;
        float cameraRotationSpeed = 0.2f;
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.translate(camera.direction.cpy().scl(cameraMoveSpeed * deltaTime));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.translate(camera.direction.cpy().scl(-cameraMoveSpeed * deltaTime));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.translate(camera.direction.cpy().crs(camera.up).nor().scl(-cameraMoveSpeed * deltaTime));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.translate(camera.direction.cpy().crs(camera.up).nor().scl(cameraMoveSpeed * deltaTime));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            cameraAngleX += cameraRotationSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            cameraAngleX -= cameraRotationSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            cameraAngleY += cameraRotationSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            cameraAngleY -= cameraRotationSpeed * deltaTime;
        }

        camera.direction.set(-MathUtils.sin(cameraAngleY) * MathUtils.cos(cameraAngleX),
                MathUtils.sin(cameraAngleX),
                -MathUtils.cos(cameraAngleY) * MathUtils.cos(cameraAngleX));
        camera.update();
    }

    private void renderChunk(Chunk chunk) {
        int chunkSize = chunk.getChunkSize();
        int uniqueObjects = 0;  // Variable to track the number of unique objects
    
        for (int voxelX = 0; voxelX < chunkSize; voxelX++) {
            for (int voxelY = 0; voxelY < chunkSize; voxelY++) {
                for (int voxelZ = 0; voxelZ < chunkSize; voxelZ++) {
                    byte voxel = chunk.getVoxel(voxelX, voxelY, voxelZ);
                    if (voxel != 0) {
                        Color color = chunk.getVoxelColor(voxelX, voxelY, voxelZ);
                        shapeRenderer.setColor(color);
    
                        float posX = (chunk.getPosition().x + voxelX) * voxelSize;
                        float posY = (chunk.getPosition().y + voxelY) * voxelSize;
                        float posZ = (chunk.getPosition().z + voxelZ) * voxelSize;
    
                        shapeRenderer.box(posX, posY, posZ, voxelSize, voxelSize, voxelSize);
    
                        uniqueObjects++;  // Increment the count of unique objects
                    }
                }
            }
        }
    
        System.out.println("Unique Objects Rendered in Chunk: " + uniqueObjects);
    }
    
    

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
