package particle.force;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import particle.Particle;
import particle.State;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by iVerb on 16-5-2015.
 */
public class GravitationalForce extends Force {

    private Collection<Particle> particles;
    private double g;

    public GravitationalForce(double g, Collection<Particle> particles) {
        this.particles = particles;
        this.g = g;
    }

    public GravitationalForce(double g, Particle... p ) {
        particles = Arrays.asList(p);
        this.g = g;
    }

    @Override
    public void apply(State s, RealVector Q) {
        for (Particle p : particles) {
            p.addForce(new ArrayRealVector(new double[]{0.0, -(p.getMass() * g), 0.0}), Q);
        }
    }

    @Override
    protected void draw2D(State currentState) {

    }

    @Override
    protected void draw3D(State state) {

    }

}
