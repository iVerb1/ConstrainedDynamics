package particle;

import org.apache.commons.math3.linear.RealVector;

/**
 * Created by iVerb on 18-5-2015.
 */
public class State {

    public RealVector positions;
    public RealVector velocities;

    public State(RealVector positions, RealVector velocities) {
        this.positions = positions;
        this.velocities = velocities;
    }

    public State addDelta(StateDelta delta) {
        return new State(positions.add(delta.deltaPosition), velocities.add(delta.deltaVelocity));
    }

}
