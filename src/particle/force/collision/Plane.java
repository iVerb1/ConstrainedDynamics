package particle.force.collision;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.vector.Vector3f;
import util.Util;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by s113958 on 26-5-2015.
 */
public class Plane {

    private RealVector point;
    private RealVector spanner1;
    private RealVector spanner2;
    private RealVector normal;
    private double damping;

    public static final Vector3f COLOR_DEFAULT = new Vector3f(0.25f, 0.25f, 0.25f);
    public Vector3f color = Plane.COLOR_DEFAULT;

    public Plane(RealVector point, RealVector spanner1, RealVector spanner2, double damping) {
        this.point = point;
        this.spanner1 = spanner1;
        this.spanner2 = spanner2;
        this.damping = damping;

        normal = Util.normalise(Util.crossProduct(spanner1, spanner2));
    }

    public RealVector getPoint() {
        return point;
    }

    public RealVector getNormal() {
        return normal;
    }

    public RealVector getSpanner1() {
        return spanner1;
    }

    public RealVector getSpanner2() {
        return spanner2;
    }

    public double getDamping() {
        return damping;
    }

    public void draw2D() {
        glLineWidth(4.5f);
        glColor3f(color.x, color.y, color.z);
        glBegin(GL_LINES);
        glVertex2d(point.getEntry(0) + 1000 * spanner1.getEntry(0), point.getEntry(1) + 1000 * spanner1.getEntry(1));
        glVertex2d(point.getEntry(0) - 1000 * spanner1.getEntry(0), point.getEntry(1) - 1000 * spanner1.getEntry(1));
        glEnd();
        glBegin(GL_LINES);
        glVertex2d(point.getEntry(0), point.getEntry(1));
        glVertex2d(point.getEntry(0) + 0.5f*normal.getEntry(0), point.getEntry(1) + 0.5f*normal.getEntry(1));
        glEnd();
    }

    public void draw3D() {
        RealVector p1 = new ArrayRealVector(new double[] {spanner1.getEntry(0), spanner1.getEntry(1), spanner1.getEntry(2)});
        RealVector p2 = new ArrayRealVector(new double[] {spanner2.getEntry(0), spanner2.getEntry(1), spanner2.getEntry(2)});
        p1 = Util.normalise(p1).mapMultiply(15);
        p2 = Util.normalise(p2).mapMultiply(15);

        glBegin(GL_QUADS);
        glNormal3d(normal.getEntry(0), normal.getEntry(1), normal.getEntry(2));
        glVertex3d(point.getEntry(0) + p1.getEntry(0), point.getEntry(1) + p1.getEntry(1), point.getEntry(2) + p1.getEntry(2));
        glVertex3d(point.getEntry(0) + p2.getEntry(0), point.getEntry(1) + p2.getEntry(1), point.getEntry(2) + p2.getEntry(2));
        glVertex3d(point.getEntry(0) - p1.getEntry(0), point.getEntry(1) - p1.getEntry(1), point.getEntry(2) - p1.getEntry(2));
        glVertex3d(point.getEntry(0) - p2.getEntry(0), point.getEntry(1) - p2.getEntry(1), point.getEntry(2) - p2.getEntry(2));
        glEnd();
    }
}
