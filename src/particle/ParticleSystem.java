package particle;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.linear.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import particle.constraint.Constraint;
import particle.force.*;
import particle.solve.RungeKutta4Solver;
import particle.solve.Solver;
import picking.PickHandler;
import picking.PickListener;
import rendering.Camera;
import util.Time;
import util.VectorIndexMap;

import java.util.LinkedList;

public abstract class ParticleSystem implements PickListener, Runnable {

    public int numDimensions;

    public State currentState;
    public VectorIndexMap<Particle> particles;
    public VectorIndexMap<Constraint> constraints;
    public LinkedList<Force> forces;
    private DiagonalMatrix W;

    public RealVector Q;
    public RealVector QHat;

    public double Ks = 0.5;
    public double Kd = 0.5;
    public int MAX_ITERATIONS = 99;
    public double DELTA_THRESHOLD = 0.0001;
    public double VISCOUS_DRAG_COEFFICIENT = 0.8;
    public double GRAVITATIONAL__ACCELERATION = 9.81;

    public Solver solver;
    protected ConjugateGradient linearSolver;

    protected Time time;
    public Camera cam;
    public double timeStep;
    public boolean variableTimeStep;

    protected MouseSpringForce mouseForce;
    protected ConstantForce windForce;
    protected boolean applyingMouseForce;
    protected boolean applyingWindForce = false;
    public float WIND_FORCE = 2;
    public float WIND_FORCE_TURBO = 10;
    public double MOUSESPRING_SPRING_CONSTANT = 300;
    public double MOUSESPRING_DAMPING_CONSTANT = 1;

    private boolean paused;
    private boolean interrupted;
    private boolean crashed;


    public ParticleSystem(int numDimensions) {
        this.numDimensions = numDimensions;

        this.applyingMouseForce = false;
        this.linearSolver = new ConjugateGradient(MAX_ITERATIONS, DELTA_THRESHOLD, false);
        this.time = new Time();

        this.resetSystem();
    }

    public boolean is2D() {
        return numDimensions == 2;
    }

    public boolean is3D() {
        return numDimensions == 3;
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void run() {
        if (particles.size() > 0) {
            while (Display.isCreated()) {  }

            try {
                Display.create();
            }
            catch (LWJGLException e) {
                e.printStackTrace();
            }

            buildSystem();
            PickHandler.listener = this;
            cam.initializeOpenGL();
            time.initialize();

            crashed = false;
            interrupted = false;

            boolean varTimeStep = variableTimeStep;
            int c = 0;
            variableTimeStep = false;
            try {
                while (!interrupted) {
                    if (c < 4)
                        c++;
                    if (c == 3)
                        variableTimeStep = varTimeStep;

                    step();
                }
            }
            catch (MaxCountExceededException e) {
                System.out.println("Simulation crashed");
                crashed = true;
            }

            Display.destroy();
        }
    }

    private synchronized void step() {
        time.tick();

        if (!paused) {
            solver.solve(this, getTimeStep());
        }

        update();
        cam.update();
        cam.render();

        Display.update();
        //Display.sync(60);
    }

    public double getTimeStep() {
        if (variableTimeStep)
            return time.getDelta();
        else
            return timeStep;
    }

    private void update() {
        while(Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                if (Mouse.getEventButton() == 0) { // left mouse button
                    if (!PickHandler.isPicking()) {
                        int pickId = cam.pick();

                        if (variableTimeStep)
                            time.setNextDelta(0);

                        if (pickId != -1) {
                            PickHandler.startPicking(pickId);
                        }
                    }
                }
            }
            else {
                if (Mouse.getEventButton() == 0) { // left mouse button release
                    if (PickHandler.isPicking()) {
                        PickHandler.stopPicking();
                    }
                }
            }
        }

        float mult = Keyboard.isKeyDown(Keyboard.KEY_UP) ? WIND_FORCE_TURBO : WIND_FORCE;

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && !applyingWindForce) {
            applyingWindForce = true;
            Vector3f force = cam.getRightVector().normalise(null);
            windForce = new ConstantForce(new Vector3f(force.x * mult, force.y * mult, force.z * mult), particles.values());
            forces.add(windForce);
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && !applyingWindForce) {
            applyingWindForce = true;
            Vector3f force = cam.getRightVector().normalise(null);
            windForce = new ConstantForce(new Vector3f(-force.x * mult, -force.y * mult, -force.z * mult), particles.values());
            forces.add(windForce);
        }
        else if (applyingWindForce){
            clearWindForce();
        }


        if (applyingMouseForce) {
            mouseForce.setMousePosition(cam.getCoordinatesAtMouse(mouseForce.getParticle()));
        }
    }

    private void buildSystem() {
        for (Integer i : constraints.keySet()) {
            constraints.get(i).setIndex(i);
        }

        Particle.numDimensions = numDimensions;
        currentState.positions = new ArrayRealVector(numDimensions * particles.size(), 0);
        currentState.velocities = new ArrayRealVector(numDimensions * particles.size(), 0);
        for (Integer i : particles.keySet()) {
            Particle p = particles.get(i);
            p.setIndex(i);
            p.reset(currentState);
        }

        W = new DiagonalMatrix(numDimensions * particles.size());
        for (Particle p : particles.values()) {
            if (numDimensions > 0)
                W.setEntry(p.getXIndex(), p.getXIndex(), p.getMass());
            if (numDimensions > 1)
                W.setEntry(p.getYIndex(), p.getYIndex(), p.getMass());
            if (numDimensions > 2)
                W.setEntry(p.getZIndex(), p.getZIndex(), p.getMass());
        }
        W = W.inverse();
    }

    public void resetSystem() {
        resetCamera();

        variableTimeStep = true;
        timeStep = 0.02;
        Ks = 0.5;
        Kd = 0.5;

        currentState = new State(new ArrayRealVector(0), new ArrayRealVector(0));
        particles = new VectorIndexMap<Particle>();
        constraints = new VectorIndexMap<Constraint>();
        forces = new LinkedList<Force>();
        solver = new RungeKutta4Solver();

        applyingMouseForce = false;
        applyingWindForce = false;
    }

    public synchronized void restart() {
        for (Particle p : particles.values()) {
            p.reset(currentState);
        }
        if (hasCrashed()) {
            clearMouseForce();
            clearWindForce();
        }
    }

    protected abstract void resetCamera();

    public void interrupt() {
        interrupted = true;
    }

    public boolean hasCrashed() {
        return crashed;
    }

    public void togglePause() {
        paused = !paused;
    }

    public void toggleVariableTimeStep() {
        variableTimeStep = !variableTimeStep;
    }

    public StateDelta derivEval(State s) {
        int particlesDimension = s.positions.getDimension();
        int numConstraints = constraints.size();

        //clear forces
        Q = new ArrayRealVector(particlesDimension, 0.0);
        QHat = new ArrayRealVector(particlesDimension, 0.0);

        //apply forces
        for (Force f : forces) {
            f.apply(s, Q);
        }

        if (numConstraints > 0) {
            RealMatrix J = new Array2DRowRealMatrix(numConstraints, particlesDimension);
            RealMatrix JDot = new Array2DRowRealMatrix(numConstraints, particlesDimension);
            RealVector C = new ArrayRealVector(numConstraints);
            RealVector CDot = new ArrayRealVector(numConstraints);

            //obtain C, CDot, J, and JDot
            for (Constraint c : constraints.values()) {
                c.updateJ(J, s);
                c.updateJDot(JDot, s);
                c.updateC(C, s);
                CDot = J.operate(s.velocities);
            }

            RealMatrix JTranspose = J.transpose();
            RealMatrix JW = J.multiply(W);
            RealVector RHS1 = JDot.operate(s.velocities).mapMultiplyToSelf(-1);
            RealVector RHS2 = JW.operate(Q);
            RealVector RHS3 = C.mapMultiplyToSelf(Ks);
            RealVector RHS4 = CDot.mapMultiplyToSelf(Kd);
            RealVector RHS = RHS1.subtract(RHS2).subtract(RHS3).subtract(RHS4);
            RealMatrix LHS = JW.multiply(JTranspose);

            //solving lambda
            RealVector lambda = new ArrayRealVector(numConstraints, 0.0);
            lambda = linearSolver.solveInPlace((Array2DRowRealMatrix) LHS, null, RHS, lambda);
            QHat = JTranspose.operate(lambda);
        }

        RealVector netForce = Q.add(QHat);
        RealVector velocities = s.velocities.copy();
        RealVector accelerations = W.operate(netForce);
        return new StateDelta(velocities, accelerations);
    }

    protected void addNaturalForces() {
        Particle[] particlesArray = particles.values().toArray(new Particle[particles.size()]);
        Force gravity = new GravitationalForce(GRAVITATIONAL__ACCELERATION, particlesArray);
        Force viscousDrag = new ViscousDragForce(VISCOUS_DRAG_COEFFICIENT, particlesArray);
        forces.add(gravity);
        forces.add(viscousDrag);
    }

    @Override
    public void startPicking(int id) {
        Particle p = particles.get(id);
        mouseForce = new MouseSpringForce(p, cam.getCoordinatesAtMouse(p), MOUSESPRING_SPRING_CONSTANT, MOUSESPRING_DAMPING_CONSTANT);
        forces.addFirst(mouseForce);
        applyingMouseForce = true;
    }

    @Override
    public void stopPicking() {
        clearMouseForce();
    }

    private void clearWindForce() {
        forces.remove(windForce);
        windForce = null;
        applyingWindForce = false;
    }

    private void clearMouseForce() {
        forces.remove(mouseForce);
        mouseForce = null;
        applyingMouseForce = false;
    }

}