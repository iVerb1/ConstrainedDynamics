package particle.constraint;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import particle.Particle;
import particle.State;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by iVerb on 22-5-2015.
 */
public class YConstraint extends Constraint {

    private Particle particle;
    private double yStart;
    private double xSlope;
    private double zSlope;

    public YConstraint(Particle particle, double yStart, double xSlope, double zSlope) {
        this.particle = particle;
        this.yStart = yStart;
        this.xSlope = xSlope;
        this.zSlope = zSlope;
    }


    @Override
    public void updateJ(RealMatrix J, State s) {
        updateMatrix(J, particle, -xSlope, 1, -zSlope);
    }

    @Override
    public void updateJDot(RealMatrix JDot, State s) {
        //no need. All relevant partial derivatives are 0.
    }

    @Override
    public void updateC(RealVector C, State s) {
        double c = particle.getY(s) - (yStart + xSlope*particle.getX(s) + zSlope*particle.getZ(s));

        C.setEntry(index, c);
    }

    @Override
    protected void draw2D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex2d(-50, (yStart - (xSlope*50)));
        glVertex2d(50, (yStart + (xSlope*50)));
        glEnd();
    }

    @Override
    protected void draw3D(State state) {

    }
}
