package particle.force;

import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.vector.Vector3f;
import particle.Particle;
import particle.State;
import util.Util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

/**
 * Created by iVerb on 23-5-2015.
 */
public class MouseSpringForce extends Force {
    private Particle particle;
    private RealVector mousePosition;

    public double springConstant;
    public double dampingConstant;

    public MouseSpringForce(Particle particle, Vector3f mousePosition, double springConstant, double dampingConstant) {
        this.particle = particle;
        this.springConstant = springConstant;
        this.dampingConstant = dampingConstant;

        setMousePosition(mousePosition);
    }

    public void setMousePosition(Vector3f mousePosition) {
        this.mousePosition = Util.toRealVector(mousePosition);
    }

    public Particle getParticle() {
        return particle;
    }

    @Override
    public void apply(State s, RealVector Q) {
        RealVector l = particle.getPosition(s).subtract(mousePosition);
        double lLength = Math.sqrt(l.dotProduct(l));

        if (lLength != 0) {
            RealVector lDot = particle.getVelocity(s);
            RealVector force = l.mapDivideToSelf(lLength).mapMultiplyToSelf(-(this.springConstant * (lLength) + this.dampingConstant * (lDot.dotProduct(l)) / lLength));
            particle.addForce(force, Q);
        }
    }

    @Override
    protected void draw2D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex2f(((float) particle.getX(state)), ((float) particle.getY(state)));
        glVertex2f((float) mousePosition.getEntry(0), (float) mousePosition.getEntry(1));
        glEnd();
    }

    @Override
    protected void draw3D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex3d(particle.getX(state), particle.getY(state), particle.getZ(state));
        glVertex3d(mousePosition.getEntry(0), mousePosition.getEntry(1), mousePosition.getEntry(2));
        glEnd();
    }

}
