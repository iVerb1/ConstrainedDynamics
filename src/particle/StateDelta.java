package particle;

import org.apache.commons.math3.linear.RealVector;

/**
 * Created by iVerb on 18-5-2015.
 */
public class StateDelta {

    public RealVector deltaPosition;
    public RealVector deltaVelocity;

    public StateDelta(RealVector deltaPosition, RealVector deltaVelocity) {
        this.deltaPosition = deltaPosition;
        this.deltaVelocity = deltaVelocity;
    }

    public StateDelta multiplySelf(double d) {
        deltaPosition.mapMultiplyToSelf(d);
        deltaVelocity.mapMultiplyToSelf(d);
        return this;
    }

    public StateDelta divideSelf(double d) {
        deltaPosition.mapDivideToSelf(d);
        deltaVelocity.mapDivideToSelf(d);
        return this;
    }

}
