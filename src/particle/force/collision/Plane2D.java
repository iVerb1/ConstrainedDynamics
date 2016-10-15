package particle.force.collision;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by s113958 on 26-5-2015.
 */
public class Plane2D extends Plane {

    public Plane2D(Vector2f point, Vector2f spanner, double damping) {
        super(new ArrayRealVector(new double[]{point.x, point.y, 0}), new ArrayRealVector(new double[]{spanner.x, spanner.y, 0}), new ArrayRealVector(new double[]{0,0,1}), damping);
    }

}
