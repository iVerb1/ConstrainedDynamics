package particle.force;

import org.apache.commons.math3.linear.RealVector;
import particle.Particle;
import particle.State;
import util.Util;

import static org.lwjgl.opengl.GL11.*;


/**
 * Created by Jeroen van Wijgerden on 16-5-2015.
 */
public class DampedSpringForce extends Force {

    private Particle p1;
    private Particle p2;

    private double length;
    private double springConstant;
    private double dampingConstant;

    public DampedSpringForce(Particle p1, Particle p2, double length, double springConstant, double dampingConstant) {
        this.p1 = p1;
        this.p2 = p2;

        this.length = length;
        this.springConstant = springConstant;
        this.dampingConstant = dampingConstant;
    }


    @Override
    public void apply(State s, RealVector Q) {
        RealVector l = p1.getPosition(s).subtract(p2.getPosition(s));
        double lLength = Util.length(l);

        if (lLength != 0) {
            RealVector lNorm = Util.normalise(l);
            RealVector lDot = p1.getVelocity(s).subtract(p2.getVelocity(s));
            RealVector force = lNorm.mapMultiply((springConstant * (lLength - length)) + (dampingConstant * (lDot.dotProduct(lNorm))));
            p2.addForce(force, Q);
            p1.addForce(force.mapMultiplyToSelf(-1), Q);
        }
    }

    @Override
    protected void draw2D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex2d(p1.getX(state), p1.getY(state));
        glVertex2d(p2.getX(state), p2.getY(state));
        glEnd();
    }

    @Override
    protected void draw3D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex3d(p1.getX(state), p1.getY(state), p1.getZ(state));
        glVertex3d(p2.getX(state), p2.getY(state), p2.getZ(state));
        glEnd();
    }

}
