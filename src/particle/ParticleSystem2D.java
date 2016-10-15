package particle;

import javafx.util.Pair;
import org.lwjgl.util.vector.Vector2f;
import particle.constraint.*;
import particle.force.AngularSpringForce;
import particle.force.DampedSpringForce;
import particle.force.Force;
import particle.force.collision.Plane;
import particle.force.collision.Plane2D;
import particle.force.collision.PlaneCollisionForce;
import rendering.Camera2D;
import util.Util;

/**
 * Created by iVerb on 23-5-2015.
 */
public class ParticleSystem2D extends ParticleSystem {

    public ParticleSystem2D() {
        super(2);
    }

    public void initSimpleDemo() {
        Vector2f center = new Vector2f(0f, 3f);
        Particle p1 = new Particle(new Vector2f(2f, 3f));
        Particle p2 = new Particle(new Vector2f(4f, 3f));
        Particle p3 = new Particle(new Vector2f(6f, 3f));
        particles.add(p1, p2, p3);

        Force spring = new DampedSpringForce(p2, p3, 2, 9, 5);
        forces.add(spring);

        Constraint c1 = new CircularWireConstraint(center, p1, 2);
        Constraint c2 = new RodConstraint(p1, p2, 2);
        constraints.add(c1, c2);

        addNaturalForces();
    }

    public void initClothDemo() {
        cam.drawParticles = false;

        int numRows = 12;
        int numColumns = 17;
        Vector2f topLeft = new Vector2f(-5f, 4f);
        Particle[][] particleNetwork = createSpringNetwork(numRows, numColumns, topLeft, 9, 35, 2);

        Constraint xc = new XConstraint(particleNetwork[0][0], topLeft.x, 0, 0);
        constraints.add(xc);
        for (int i = 0; i < numColumns; i++) {
            Constraint yc = new YConstraint(particleNetwork[0][i], topLeft.y, 0, 0);
            constraints.add(yc);
        }

        addNaturalForces();
    }

    public void initClothWallDemo() {
        cam.drawParticles = false;

        int numRows = 12;
        int numColumns = 17;
        Vector2f topLeft = new Vector2f(-7.9f, 4f);
        Particle[][] particleNetwork = createSpringNetwork(numRows, numColumns, topLeft, 9, 35, 2);

        for (int i = 0; i < numColumns; i++) {
            Constraint yc = new YConstraint(particleNetwork[0][i], topLeft.y, 0, 0);
            constraints.add(yc);
        }

        PlaneCollisionForce collisionForce = new PlaneCollisionForce(this, 0.001f, 1);
        Plane pl1 = new Plane2D(new Vector2f(4f, -1f), new Vector2f(-1f, -1f), 0.6);
        collisionForce.addPlanes(pl1);
        collisionForce.addParticles(particles.values());

        addNaturalForces();
        forces.add(collisionForce);
    }

    public void initWallDemo() {

        Plane pl1 = new Plane2D(new Vector2f(-1,-2), new Vector2f(-0.7f, 0.3f), 0.5f);
        Plane pl2 = new Plane2D(new Vector2f(0,-2), new Vector2f(-0.1f, 0.0f), 0.5f);
        Plane pl3 = new Plane2D(new Vector2f(1,-2), new Vector2f(-0.7f, -0.3f), 0.5f);
        PlaneCollisionForce f1 = new PlaneCollisionForce(this, 0.001f, 1);
        PlaneCollisionForce f2 = new PlaneCollisionForce(this, 0.001f, 8);

        f1.addPlanes(pl1, pl2);
        f2.addPlanes(pl3);

        for (int i = 0; i < 20; i++) {
            Particle p = new Particle(new Vector2f(-5+0.5f*i + (float)Math.random(), 4f+(float)Math.random()), 1 + Math.random()*10);
            particles.add(p);
            f1.addParticles(p);
            f2.addParticles(p);
        }

        addNaturalForces();
        forces.add(f1);
        forces.add(f2);
    }

    public void initAngularSpringsDemo() {
        Vector2f center = new Vector2f(0f, 3f);

        Particle p1 = new Particle(new Vector2f(0f, 2.9f));
        Particle p2 = new Particle(new Vector2f(0f, 2.8f));
        particles.add(p1, p2);

        Pair<Particle, Particle> lastParticles1 = addAngularSprings(15, 0.18, 170, 60, new Pair<Particle, Particle>(p1, p2));
        Pair<Particle, Particle> lastParticles2 = addAngularSprings(5, 0.18, 180, 60, lastParticles1);
        addAngularSprings(15, 0.18, 195, 60, lastParticles2);

        Constraint c1 = new CircularWireConstraint(center, p1, 0.1);
        Constraint c2 = new RodConstraint(p1, p2, 0.1);
        constraints.add(c1, c2);

        addNaturalForces();

        Ks = 100;
        Kd = 100;
        variableTimeStep = false;
        timeStep = 0.006;
        cam.drawParticles = false;
    }

    private Particle[][] createSpringNetwork(int numRows, int numColumns, Vector2f topLeft, double width, double springConstantMultiplier, double dampingConstant) {
        Particle[][] particleNetwork = new Particle[numRows][numColumns];

        float spacing = (float)(width / numColumns);
        double springConstant = numRows * springConstantMultiplier;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                Particle p = new Particle(new Vector2f(topLeft.x + j * spacing, topLeft.y - i * spacing));
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

    private Pair<Particle, Particle> addAngularSprings(int amount, double length, double incrementalAngle, double springForce, Pair<Particle, Particle> lastParticles) {
        Particle prev1 = lastParticles.getKey();
        Particle prev2 = lastParticles.getValue();
        Particle[] newParticles = new Particle[amount];
        Constraint[] newConstraints = new Constraint[amount];

        for (int i = 0; i < amount; i++) {
            Vector2f pos1 = new Vector2f(prev1.initialPosition.x, prev1.initialPosition.y);
            Vector2f pos2 = new Vector2f(prev2.initialPosition.x, prev2.initialPosition.y);
            Vector2f dir = Vector2f.sub(pos2, pos1, null).normalise(null);
            Vector2f reverse = dir.negate(null);

            Vector2f rotated = Util.rotateClockWise(reverse, incrementalAngle);
            Vector2f newPos = Vector2f.add(pos2, (Vector2f)rotated.scale((float)length), null);

            Particle p = new Particle(newPos);
            newParticles[i] = p;

            RodConstraint rod = new RodConstraint(prev2, p, length);
            newConstraints[i] = rod;

            AngularSpringForce spring1 = new AngularSpringForce(prev1, prev2, p, incrementalAngle, springForce);
            forces.add(spring1);

            prev1 = prev2;
            prev2 = p;
        }
        particles.add(newParticles);
        constraints.add(newConstraints);
        return new Pair<Particle, Particle>(prev1, prev2);
    }

    @Override
    protected void resetCamera() {
        cam = new Camera2D(this, time);
    }

}
