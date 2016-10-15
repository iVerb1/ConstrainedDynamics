package rendering;

import particle.force.collision.Plane;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.vector.Vector3f;
import particle.Particle;
import particle.ParticleSystem;
import particle.State;
import particle.constraint.Constraint;
import particle.force.Force;
import util.Time;
import view.MainForm;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by iVerb on 23-5-2015.
 */
public class Camera2D extends Camera {

    float worldWidth = 16.0f;
    float worldHeight = 9.0f;

    public Camera2D(ParticleSystem particleSystem, Time time) {
        super(particleSystem, time);
    }

    @Override
    public void initializeOpenGL() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, worldWidth, 0, worldHeight, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glTranslated(worldWidth / 2, worldHeight / 2, 0.0);
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {
        // Clear the screen and depth buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        State currentState = particleSystem.currentState;

        if (drawParticles) {
            drawParticles();
        }

        if (drawForces) {
            for (Force f : particleSystem.forces) {
                f.render2D(currentState);
            }
        }

        if (drawNetForces) {
            drawNetForces();
        }

        if (drawConstraints) {
            for (Constraint c : particleSystem.constraints.values()) {
                c.render2D(currentState);
            }
        }
    }

    @Override
    protected void renderPicking() {
        glOrtho(0, 16, 0, 9, 1, -1);

        // Clear the screen and depth buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawParticles();
    }

    private void drawParticles() {
        State currentState = particleSystem.currentState;

        for (Particle p : particleSystem.particles.values()) {
            glLoadName(p.index);
            glPushMatrix();
            glColor3f(p.color.x, p.color.y, p.color.z);
            glTranslated(p.getX(currentState), p.getY(currentState), 0);
            new Disk().draw(0, 0.1f, PARTICLE_SIZE, 1);
            glPopMatrix();
        }
    }

    @Override
    public Vector3f getCoordinatesAtMouse(Particle particle) {
        float x = (worldWidth * (((float)Mouse.getX()) / MainForm.VIEWPORT_WIDTH)) - (worldWidth / 2);
        float y = (worldHeight * (((float)Mouse.getY()) / MainForm.VIEWPORT_HEIGHT)) - (worldHeight / 2);
        return new Vector3f(x, y, 0f);
    }

}