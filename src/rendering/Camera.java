package rendering;

import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import particle.Particle;
import particle.ParticleSystem;
import util.Time;
import util.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public abstract class Camera {

    public Time time;
    public ParticleSystem particleSystem;

    public boolean drawParticles;
    public boolean drawForces;
    public boolean drawConstraints;
    public boolean drawNetForces;
    public int PARTICLE_SIZE;

    public Camera(ParticleSystem particleSystem, Time time) {
        this.particleSystem = particleSystem;
        this.time = time;

        drawParticles = true;
        drawForces = true;
        drawConstraints = true;
        PARTICLE_SIZE = 10;
    }

    public abstract void initializeOpenGL();

    public abstract void update();

    public abstract void render();

    protected abstract void renderPicking();

    public int pick() {
        int x = Mouse.getX();
        int y = Mouse.getY();

        // The selection buffer
        IntBuffer selBuffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder()).asIntBuffer();
        int buffer[] = new int[256];

        IntBuffer vpBuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
        // The size of the viewport. [0] Is <x>, [1] Is <y>, [2] Is <width>, [3] Is <height>
        int[] viewport = new int[4];

        // The number of "hits" (objects within the pick area).
        int hits;
        // Get the viewport info
        glGetInteger(GL_VIEWPORT, vpBuffer);
        vpBuffer.get(viewport);

        // Set the buffer that OpenGL uses for selection to our buffer
        glSelectBuffer(selBuffer);

        // Change to selection mode
        glRenderMode(GL_SELECT);

        // Initialize the name stack (used for identifying which object was selected)
        glInitNames();
        glPushName(0);

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();

        /*  create 5x5 pixel picking region near cursor location */
        GLU.gluPickMatrix((float) x, (float) y, 1.0f, 1.0f, IntBuffer.wrap(viewport));

        renderPicking();
        
        glMatrixMode (GL_PROJECTION);
        glPopMatrix();
        glFlush();

        // Exit selection mode and return to render mode, returns number selected
        hits = glRenderMode(GL_RENDER);

        int picked = -1;

        selBuffer.get(buffer);
        // Objects Were Drawn Where The Mouse Was
        int depth = Integer.MAX_VALUE;
        if (hits > 0) {

            for (int i = 0; i < hits; i++) {
                int hitId = buffer[i * 4 + 3];
                int hitDepth = buffer[i * 4 + 1];

                // Loop Through All The Detected Hits
                // If This Object Is Closer To Us Than The One We Have Selected
                if (buffer[i * 4 + 1] <  depth) {
                    picked = hitId; // Select The Closer Object
                    depth = hitDepth; // Store How Far Away It Is
                }
            }
        }
        return picked;
    }

    protected void drawNetForces() {
        for (Particle p : particleSystem.particles.values()) {
            RealVector pos = p.getPosition(particleSystem.currentState);
            RealVector nf = p.getForce(particleSystem.QHat).add(p.getForce(particleSystem.Q));
            nf = Util.normalise(nf).mapMultiply(0.5);
            RealVector nfPos = pos.add(nf);

            glLineWidth(2.5f);
            glColor3f(0.0f, 1.0f, 1.0f);
            glBegin(GL_LINES);
            glVertex3d(pos.getEntry(0), pos.getEntry(1), pos.getEntry(2));
            glVertex3d(nfPos.getEntry(0), nfPos.getEntry(1), nfPos.getEntry(2));
            glEnd();
        }
    }

    public void toggleDrawNetForces() {
        drawNetForces = !drawNetForces;
    }

    public abstract Vector3f getCoordinatesAtMouse(Particle particle);

    public Vector3f getUpVector() {
        if (particleSystem.is2D()) {
            return new Vector3f(0,1,0);
        }
        else if (particleSystem.is3D()) {
            FloatBuffer modelMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            glGetFloat(GL_MODELVIEW_MATRIX, modelMatrix);

            FloatBuffer projectionMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            glGetFloat(GL_PROJECTION_MATRIX, projectionMatrix);

            IntBuffer viewport = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
            glGetInteger(GL_VIEWPORT, viewport);

            FloatBuffer objectPosition = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            float[] pos = new float[3];

            GLU.gluUnProject(0, 0, 0, modelMatrix, projectionMatrix, viewport, objectPosition);

            objectPosition.get(pos);

            float[] pos2 = new float[3];

            GLU.gluUnProject(0, 1, 0, modelMatrix, projectionMatrix, viewport, objectPosition);

            objectPosition.get(pos2);

            return new Vector3f(pos2[0] - pos[0], pos2[1] - pos[1], pos2[2] - pos[2]);
        }
        else {
            throw new IllegalStateException("Cannot use this method in 1D");
        }
    }

    public Vector3f getRightVector() {
        if (particleSystem.is2D()) {
            return new Vector3f(1,0,0);
        }
        else if (particleSystem.is3D()) {
            FloatBuffer modelMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            glGetFloat(GL_MODELVIEW_MATRIX, modelMatrix);

            FloatBuffer projectionMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            glGetFloat(GL_PROJECTION_MATRIX, projectionMatrix);

            IntBuffer viewport = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
            glGetInteger(GL_VIEWPORT, viewport);

            FloatBuffer objectPosition = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            float[] pos = new float[3];

            GLU.gluUnProject(0, 0, 0, modelMatrix, projectionMatrix, viewport, objectPosition);

            objectPosition.get(pos);

            float[] pos2 = new float[3];

            GLU.gluUnProject(1, 0, 0, modelMatrix, projectionMatrix, viewport, objectPosition);

            objectPosition.get(pos2);

            return new Vector3f(pos2[0] - pos[0], pos2[1] - pos[1], pos2[2] - pos[2]);
        }
        else {
            throw new IllegalStateException("Cannot use this method in 1D");
        }
    }

}