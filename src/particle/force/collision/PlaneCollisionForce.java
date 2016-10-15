package particle.force.collision;

import entity3D.Material;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particle.Particle;
import particle.ParticleSystem;
import particle.State;
import particle.force.Force;
import rendering.Camera3D;
import rendering.ShaderUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by s113958 on 26-5-2015.
 */
public class PlaneCollisionForce extends Force {

    private Collection<Particle> particles;
    private Collection<Plane> planes;
    private ParticleSystem system;
    private double response;
    private double epsilon;

    public PlaneCollisionForce(ParticleSystem system, double epsilon, double response) {
        this.particles = new HashSet<Particle>();
        this.planes = new HashSet<Plane>();
        this.system = system;
        this.epsilon = epsilon;
        this.response = response;
    }

    public void addParticles(Collection<Particle> particles) {
        this.particles.addAll(particles);
    }

    public void addParticles(Particle... particles) {
        Collections.addAll(this.particles, particles);
    }

    public void addPlanes(Collection<Plane> planes) {
        this.planes.addAll(planes);
    }

    public void addPlanes(Plane... planes) {
        Collections.addAll(this.planes, planes);
    }

    @Override
    public void apply(State s, RealVector Q) {
        for (Particle p : particles) {
            RealVector particleVelocity = p.getVelocity(s);

            for (Plane plane : planes) {
                RealVector particlePosition = p.getPosition(s);

                RealVector particleRelativePosition = particlePosition.subtract(plane.getPoint());

                double distance = particleRelativePosition.dotProduct(plane.getNormal());

                if (distance < this.epsilon) {

                    double dot = plane.getNormal().dotProduct(particleVelocity);
                    RealVector normalVelocity = plane.getNormal().mapMultiply(dot);

                    RealVector force = p.getForce(Q);

                    dot = plane.getNormal().dotProduct(force);
                    RealVector normalForce = plane.getNormal().mapMultiply(dot);

                    p.addForce(particleVelocity.subtract(normalVelocity).mapMultiply(-plane.getDamping()*-dot), Q);

                    if (plane.getNormal().dotProduct(normalForce) <= 0) {
                        p.addForce(normalForce.mapMultiply(-1), Q);
                    }

                    if (plane.getNormal().dotProduct(normalVelocity) <= 0) {
                        RealVector f = normalVelocity.mapMultiply(-this.response*p.getMass()/system.getTimeStep());
                        if (!Double.isNaN(f.getEntry(0)) && !Double.isNaN(f.getEntry(1)) && !Double.isNaN(f.getEntry(2)) && !Double.isInfinite(f.getEntry(0)) && !Double.isInfinite(f.getEntry(1)) && !Double.isInfinite(f.getEntry(2))) {
                            p.addForce(f, Q);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void draw2D(State state) {
        for (Plane p : planes) {
            p.draw2D();
        }
    }

    @Override
    protected void draw3D(State state) {
        Camera3D.prepareShading();
        Material material = new Material();
        ShaderUtils.initializeShader(true, material);

        for (Plane p : planes) {
            p.draw3D();
        }

        Camera3D.finalizeShading();
    }

}
