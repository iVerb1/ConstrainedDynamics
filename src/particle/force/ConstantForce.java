package particle.force;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.vector.Vector3f;
import particle.Particle;
import particle.State;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by iVerb on 27-5-2015.
 */
public class ConstantForce extends Force {

    private Collection<Particle> particles;
    private Vector3f force;

    public ConstantForce(Vector3f force, Collection<Particle> particles) {
        this.particles = particles;
        this.force = force;
    }

    public ConstantForce(Vector3f force, Particle... p ) {
        particles = Arrays.asList(p);
        this.force = force;
    }

    public void setForce(Vector3f force){
        this.force = force;
    }

    @Override
    public void apply(State s, RealVector Q) {
        for (Particle p : particles) {
            p.addForce(new ArrayRealVector(new double[]{force.x, force.y, force.z}), Q);
        }
    }

    @Override
    protected void draw2D(State currentState) {

    }

    @Override
    protected void draw3D(State state) {

    }

}
