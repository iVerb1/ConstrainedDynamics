package particle;

import entity3D.Material;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particle.constraint.*;
import particle.force.DampedSpringForce;
import particle.force.Force;
import particle.force.collision.Plane;
import particle.force.collision.PlaneCollisionForce;
import rendering.Camera3D;
import rendering.ShaderUtils;
import util.Util;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by iVerb on 23-5-2015.
 */
public class ParticleSystem3D extends ParticleSystem {

    public ParticleSystem3D() {
        super(3);
    }

    public void initSimpleDemo() {
        Vector3f center = new Vector3f(0f, 4.5f, 0f);
        Particle p1 = new Particle(new Vector3f(2f, 4.5f, 0f));
        Particle p2 = new Particle(new Vector3f(2f, 4.5f, 2f));
        Particle p3 = new Particle(new Vector3f(4f, 4.5f, 2f));
        particles.add(p1, p2, p3);

        Force spring = new DampedSpringForce(p2, p3, 2, 7, 1);
        forces.add(spring);

        Constraint c1 = new CircularWireConstraint(center, p1, 2);
        Constraint c2 = new RodConstraint(p1, p2, 2);
        constraints.add(c1, c2);

        addNaturalForces();
    }

    public void initClothDemo() {
        final int numRows = 10;
        final int numColumns = 10;
        final Vector3f topLeft = new Vector3f(-2.5f, 5f, 0f);
        final Particle[][] particleNetwork = createSpringNetwork(numRows, numColumns, topLeft, 7, 30, 1);

        Constraint xc = new XConstraint(particleNetwork[0][0], topLeft.x, 0, 0);
        constraints.add(xc);
        for (int i = 0; i < numColumns; i++) {
            Constraint yc = new YConstraint(particleNetwork[0][i], topLeft.y, 0, 0);
            Constraint zc = new ZConstraint(particleNetwork[0][i], topLeft.z, 0, 0);
            constraints.add(yc, zc);
        }

        cam = new Camera3D(this, time) {

            @Override
            protected void drawParticles(State s) {
                prepareShading();
                Material material = new Material();
                material.color = new Vector4f(1f, 0f, 0f, 0f);
                ShaderUtils.initializeShader(true, material);
                glBegin(GL_QUADS);
                for (int i = 0; i < numRows - 1; i++) {
                    for (int j = 0; j < numColumns - 1; j++) {
                        Vector3f v1 = (Vector3f)Util.toVector(particleNetwork[i+1][j].getPosition(s).subtract(particleNetwork[i][j].getPosition(s)));
                        Vector3f v2 = (Vector3f)Util.toVector(particleNetwork[i][j+1].getPosition(s).subtract(particleNetwork[i][j].getPosition(s)));
                        Vector3f cross = Vector3f.cross(v1, v2, null);
                        glNormal3d(cross.x, cross.y, cross.z);

                        glVertex3f((float)particleNetwork[i][j].getX(s), (float)particleNetwork[i][j].getY(s), (float)particleNetwork[i][j].getZ(s));
                        glVertex3f((float)particleNetwork[i+1][j].getX(s), (float)particleNetwork[i+1][j].getY(s), (float)particleNetwork[i+1][j].getZ(s));
                        glVertex3f((float)particleNetwork[i+1][j+1].getX(s), (float)particleNetwork[i+1][j+1].getY(s), (float)particleNetwork[i+1][j+1].getZ(s));
                        glVertex3f((float)particleNetwork[i][j+1].getX(s), (float)particleNetwork[i][j+1].getY(s), (float)particleNetwork[i][j+1].getZ(s));
                    }
                }
                glEnd();
                finalizeShading();
            }

            @Override
            protected void drawForces(State s) {
                super.drawForces(s);

                glLineWidth(2.5f);
                glColor3f(1.0f, 1.0f, 1.0f);
                glBegin(GL_LINES);
                glVertex3d(-10, topLeft.y, 0);
                glVertex3d(10, topLeft.y, 0);
                glEnd();
            }

        };
        cam.drawConstraints = true;
        cam.drawForces = true;

        addNaturalForces();
    }

    public void initClothWallDemo() {

        final int numRows = 10;
        final int numColumns = 10;
        final Vector3f topLeft = new Vector3f(-2.5f, 5f, 0f);
        final Particle[][] particleNetwork = createSpringNetwork(numRows, numColumns, topLeft, 7, 30, 1);

        for (int i = 0; i < numColumns; i++) {
            Constraint yc = new YConstraint(particleNetwork[0][i], topLeft.y, 0, 0);
            Constraint zc = new ZConstraint(particleNetwork[0][i], topLeft.z, 0, 0);
            constraints.add(yc, zc);
        }

        cam = new Camera3D(this, time) {
            @Override
            protected void drawParticles(State s) {
                prepareShading();
                Material material = new Material();
                material.color = new Vector4f(1f, 0f, 0f, 0f);
                ShaderUtils.initializeShader(true, material);
                glBegin(GL_QUADS);
                for (int i = 0; i < numRows - 1; i++) {
                    for (int j = 0; j < numColumns - 1; j++) {
                        Vector3f v1 = (Vector3f)Util.toVector(particleNetwork[i+1][j].getPosition(s).subtract(particleNetwork[i][j].getPosition(s)));
                        Vector3f v2 = (Vector3f)Util.toVector(particleNetwork[i][j+1].getPosition(s).subtract(particleNetwork[i][j].getPosition(s)));
                        Vector3f cross = Vector3f.cross(v1, v2, null);
                        glNormal3d(cross.x, cross.y, cross.z);

                        glVertex3f((float)particleNetwork[i][j].getX(s), (float)particleNetwork[i][j].getY(s), (float)particleNetwork[i][j].getZ(s));
                        glVertex3f((float)particleNetwork[i+1][j].getX(s), (float)particleNetwork[i+1][j].getY(s), (float)particleNetwork[i+1][j].getZ(s));
                        glVertex3f((float)particleNetwork[i+1][j+1].getX(s), (float)particleNetwork[i+1][j+1].getY(s), (float)particleNetwork[i+1][j+1].getZ(s));
                        glVertex3f((float)particleNetwork[i][j+1].getX(s), (float)particleNetwork[i][j+1].getY(s), (float)particleNetwork[i][j+1].getZ(s));
                    }
                }
                glEnd();
                finalizeShading();
            }

            @Override
            protected void drawForces(State s) {
                super.drawForces(s);

                glLineWidth(2.5f);
                glColor3f(1.0f, 1.0f, 1.0f);
                glBegin(GL_LINES);
                glVertex3d(-20, topLeft.y, 0);
                glVertex3d(20, topLeft.y, 0);
                glEnd();
            }
        };
        cam.drawConstraints = true;
        cam.drawForces = true;

        PlaneCollisionForce collisionForce = new PlaneCollisionForce(this, 0.1, 1);
        Plane pl1 = new Plane(
                new ArrayRealVector(new double[]{10, 2, 0}),
                new ArrayRealVector(new double[]{0.5,0.5,0.0}),
                new ArrayRealVector(new double[]{0.0,1.0,-1.0}),
                0.5);
        collisionForce.addPlanes(pl1);
        collisionForce.addParticles(particles.values());

        addNaturalForces();
        forces.add(collisionForce);
    }

    private Particle[][] createSpringNetwork(int numRows, int numColumns, Vector3f topLeft, double width, double springConstantMultiplier, double dampingConstant) {
        Particle[][] particleNetwork = new Particle[numRows][numColumns];

        float spacing = (float)(width / numColumns);
        double springConstant = numRows * springConstantMultiplier;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                Particle p = new Particle(new Vector3f(topLeft.x + j * spacing, topLeft.y - i * spacing, topLeft.z));
                if (i > 0) {
                    Force spring = new DampedSpringForce(particleNetwork[i - 1][j], p, spacing, springConstant, dampingConstant);
                    forces.add(spring);
                    /*
                    if (j > 0) {
                        Force s1 = new DampedSpringForce(particleNetwork[i - 1][j - 1], p, Math.sqrt(2*Math.pow(spacing, 2)), 4, 2);
                        Force s2 = new DampedSpringForce(particleNetwork[i][j - 1], particleNetwork[i - 1][j], Math.sqrt(2*Math.pow(spacing, 2)), 4, 2);
                        forces.add(s1);
                        forces.add(s2);
                    }
                    */
                }
                if (j > 0) {
                    Force spring = new DampedSpringForce(particleNetwork[i][j - 1], p, spacing, springConstant, dampingConstant);
                    forces.add(spring);
                }
                particleNetwork[i][j] = p;
                particles.add(p);
            }
        }
        return particleNetwork;
    }

    @Override
    protected void resetCamera() {
        cam = new Camera3D(this, time);
    }

}
