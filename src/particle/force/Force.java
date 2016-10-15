package particle.force;

import org.apache.commons.math3.linear.RealVector;
import particle.State;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_CURRENT_PROGRAM;
import static org.lwjgl.opengl.GL20.GL_SHADER_OBJECT;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * Created by iVerb on 16-5-2015.
 */
public abstract class Force {

    public boolean draw = true;

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

    public abstract void apply(State s, RealVector Q);

}
