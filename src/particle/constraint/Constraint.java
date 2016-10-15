package particle.constraint;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import particle.Particle;
import particle.State;

/**
 * Created by iVerb on 16-5-2015.
 */
public abstract class Constraint {

    protected int index = -1;
    public boolean draw = true;

    public abstract void updateJ(RealMatrix J, State s);

    public abstract void updateJDot(RealMatrix JDot, State s);

    public abstract void updateC(RealVector C, State s);

    public void render2D(State state) {
        if (draw) {
            draw2D(state);
        }
    }

    public void render3D(State state) {
        if (draw) {
            draw3D(state);
        }
    }

    protected abstract void draw2D(State state);

    protected abstract void draw3D(State state);

    protected void updateMatrix(RealMatrix m, Particle p, double partialDerivX, double partialDerivY, double partialDerivZ) {
        if (p.getNumDimensions() >= 1)
            m.setEntry(index, p.getXIndex(), partialDerivX);
        if (p.getNumDimensions() >= 2)
            m.setEntry(index, p.getYIndex(), partialDerivY);
        if (p.getNumDimensions() >= 3)
            m.setEntry(index, p.getZIndex(), partialDerivZ);
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
