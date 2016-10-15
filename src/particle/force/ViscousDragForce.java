package particle.force;

import org.apache.commons.math3.linear.RealVector;
import particle.Particle;
import particle.State;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by iVerb on 16-5-2015.
 */
public class ViscousDragForce extends Force {

    private Collection<Particle> particles;
    private final double drag;

    public ViscousDragForce(double drag, Collection<Particle> particles) {
        this.particles = particles;
        this.drag = drag;
    }

    public ViscousDragForce(double drag, Particle... p) {
        particles = Arrays.asList(p);
        this.drag = drag;
    }

    @Override
    public void apply(State s, RealVector Q) {
        for (Particle p : particles) {
            p.addForce(p.getVelocity(s).mapMultiplyToSelf(-drag), Q);
        }
    }

    @Override
    protected void draw2D(State currentState) {

    }

    @Override
    protected void draw3D(State state) {

    }

}
