package particle;

import entity3D.Entity3D;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import util.Util;

/**
 * Created by iVerb on 16-5-2015.
 */
public class Particle {

    public static int numDimensions;

    public int index = -1;
    private int xIndex = -1;
    private int yIndex = -1;
    private int zIndex = -1;

    public Vector3f color = new Vector3f(0.5f, 0.5f, 1f);
    public Vector3f initialPosition;
    private double mass;

    public Entity3D entity3D;

    public Particle(Vector3f initialPosition, double mass) {
        this.initialPosition = initialPosition;
        this.mass = mass;
    }

    public Particle(Vector3f initialPosition) {
        this(initialPosition, 1.0);
    }

    public Particle(Vector2f initialPosition, double mass) {
        this(new Vector3f(initialPosition.x, initialPosition.y, 0f), mass);
    }

    public Particle(Vector2f initialPosition) {
        this(initialPosition, 1.0);
    }

    public int getNumDimensions() {
        return numDimensions;
    }

    public boolean is1D() {
        return numDimensions == 1;
    }

    public boolean is2D() {
        return numDimensions == 2;
    }

    public boolean is3D() {
        return numDimensions == 3;
    }

    public void setIndex(int index) {
        this.index = index;
        if (is1D()) {
            this.xIndex = index;
        }
        else if (is2D()) {
            this.xIndex = 2 * index;
            this.yIndex = (2 * index) + 1;
        }
        else if (is3D()) {
            this.xIndex = 3 * index;
            this.yIndex = (3 * index) + 1;
            this.zIndex = (3 * index) + 2;
        }
        else
            throw new IllegalStateException("More than three dimensions!");
    }

    public void reset(State currentState) {
        setPosition(currentState, initialPosition);
        setVelocity(currentState, new Vector3f(0f, 0f, 0f));
    }

    public int getIndex() {
        return index;
    }

    public int getXIndex() {
        return xIndex;
    }

    public int getYIndex() {
        return yIndex;
    }

    public int getZIndex() {
        return zIndex;
    }

    private RealVector getSubVector(RealVector v) {
        if (is1D())
            return new ArrayRealVector(new double[] {v.getEntry(getXIndex()), 0, 0});
        if (is2D())
            return new ArrayRealVector(new double[] {v.getEntry(getXIndex()), v.getEntry(getYIndex()), 0});
        if (is3D())
            return new ArrayRealVector(new double[] {v.getEntry(getXIndex()), v.getEntry(getYIndex()), v.getEntry(getZIndex())});

        throw new IllegalStateException("More than three dimensions!");
    }

    public RealVector getPosition(State s) {
        return getSubVector(s.positions);
    }

    public void setPosition(State state, Vector3f position) {
        if (numDimensions > 0)
            state.positions.setEntry(getXIndex(), position.x);
        if (numDimensions > 1)
            state.positions.setEntry(getYIndex(), position.y);
        if (numDimensions > 2)
            state.positions.setEntry(getZIndex(), position.z);
    }

    public double getX(State s) {
        if (numDimensions >= 1)
            return s.positions.getEntry(getXIndex());
        else
            return 0.0;
    }

    public double getY(State s) {
        if (numDimensions >= 2)
            return s.positions.getEntry(getYIndex());
        else
            return 0.0;
    }

    public double getZ(State s) {
        if (numDimensions >= 3)
            return s.positions.getEntry(getZIndex());
        else
            return 0.0;
    }

    public RealVector getVelocity(State s) {
        return getSubVector(s.velocities);
    }

    public void setVelocity(State state, Vector3f velocity) {
        if (numDimensions >= 1)
            state.velocities.setEntry(getXIndex(), velocity.x);
        if (numDimensions >= 2)
            state.velocities.setEntry(getYIndex(), velocity.y);
        if (numDimensions >= 3)
            state.velocities.setEntry(getZIndex(), velocity.z);
    }

    public double getVelocityX(State s) {
        if (numDimensions >= 0)
            return s.velocities.getEntry(getXIndex());
        else
            return 0;
    }

    public double getVelocityY(State s) {
        if (numDimensions >= 2)
            return s.velocities.getEntry(getYIndex());
        else
            return 0.0;
    }

    public double getVelocityZ(State s) {
        if (numDimensions >= 3)
            return s.velocities.getEntry(getZIndex());
        else
            return 0.0;
    }

    public double getMass() {
        return mass;
    }

    public RealVector getForce(RealVector Q) {
        return getSubVector(Q);
    }

    public void addForce(RealVector force, RealVector Q) {
        if (numDimensions > 0)
            Q.addToEntry(getXIndex(), force.getEntry(0));
        if (numDimensions > 1)
            Q.addToEntry(getYIndex(), force.getEntry(1));
        if (numDimensions > 2)
            Q.addToEntry(getZIndex(), force.getEntry(2));
    }

}

