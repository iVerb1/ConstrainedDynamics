package view;

import org.lwjgl.opengl.Display;
import particle.ParticleSystem;
import particle.ParticleSystem2D;
import particle.ParticleSystem3D;
import particle.solve.EulerSolver;
import particle.solve.MidPointSolver;
import particle.solve.RungeKutta4Solver;
import particle.solve.Solver;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;


public class MainForm  {

    private ParticleSystem currentParticleSystem;
    private ParticleSystem2D particleSystem2D;
    private ParticleSystem3D particleSystem3D;

    public static int VIEWPORT_WIDTH = 1280;
    public static int VIEWPORT_HEIGHT = 720;
    private Thread currentThread;


    public MainForm() {
        canvas.setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        try {
            Display.setParent(canvas);
        }
        catch (Exception e) {
            throw new IllegalStateException();
        }

        setupComponents();

        particleSystem2D = new ParticleSystem2D();
        particleSystem3D = new ParticleSystem3D();

        particleSystem2D.initSimpleDemo();
        currentParticleSystem = particleSystem2D;
        startDemo();
    }

    private void setupComponents() {
        panel = new javax.swing.JPanel();
        pauseButton = new javax.swing.JButton();
        integrationSchemeLabel = new javax.swing.JLabel();
        simulationLabel = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        simulationComboBox = new javax.swing.JComboBox();
        integrationSchemeComboBox = new javax.swing.JComboBox();
        timeStepLabel = new javax.swing.JLabel();
        timeStepSpinner = new JSpinner(new SpinnerNumberModel(0.02, 0.001, 1, 0.001));
        variableTimeStepLabel = new javax.swing.JLabel();
        variableTimeStepTimeCheckBox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        drawNetForcesCheckbox = new javax.swing.JCheckBox();
        drawNetForcesLabel = new javax.swing.JLabel();

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pauseButton.setText("Pause");
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
                canvas.requestFocus();
            }
        });

        integrationSchemeLabel.setText("Integration method");

        simulationLabel.setText("Simulation");

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
                canvas.requestFocus();
            }
        });

        simulationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"2D - Simple", "2D - Cloth", "2D - Cloth & Wall", "2D - Wall", "2D - Angular springs", "3D - Simple", "3D - Cloth", "3D - Cloth & Wall"}));
        simulationComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                simulationComboBoxItemStateChanged(evt);
            }
        });
        simulationComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                canvas.requestFocus();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        integrationSchemeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Euler", "Midpoint", "Runge-Kutta 4"}));
        integrationSchemeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                integrationSchemeComboBoxItemStateChanged(evt);
            }
        });
        integrationSchemeComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                canvas.requestFocus();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        timeStepLabel.setText("Time step");

        timeStepSpinner.setEnabled(false);
        timeStepSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                timeStepSpinnerStateChanged(evt);
                canvas.requestFocus();
            }
        });

        variableTimeStepLabel.setText("Variable time step");
        variableTimeStepTimeCheckBox.setSelected(true);
        variableTimeStepTimeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                variableTimeStepTimeCheckStateChanged(evt);
                canvas.requestFocus();
            }
        });

        drawNetForcesLabel.setText("Draw net force directions");
        drawNetForcesCheckbox.setSelected(false);
        drawNetForcesCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                drawNetForcesCheckStateChanged(evt);
                canvas.requestFocus();
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
                panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(simulationLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(simulationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
                                                .addComponent(timeStepLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(timeStepSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(pauseButton, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(integrationSchemeLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(integrationSchemeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(drawNetForcesLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(drawNetForcesCheckbox))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(variableTimeStepLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(variableTimeStepTimeCheckBox)))
                                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
                panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(pauseButton)
                                        .addComponent(resetButton))
                                .addGap(18, 18, 18)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(simulationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(simulationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(integrationSchemeLabel)
                                        .addComponent(integrationSchemeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(9, 9, 9)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(variableTimeStepLabel)
                                        .addComponent(variableTimeStepTimeCheckBox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(timeStepLabel)
                                        .addComponent(timeStepSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(drawNetForcesCheckbox)
                                        .addComponent(drawNetForcesLabel))
                                .addContainerGap(77, Short.MAX_VALUE))
        );

        frame.setTitle("Particle System");
        frame.add(panel, BorderLayout.EAST);
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private void drawNetForcesCheckStateChanged(ActionEvent evt) {
        currentParticleSystem.cam.toggleDrawNetForces();
    }

    private void integrationSchemeComboBoxItemStateChanged(ItemEvent evt) {
        int i = integrationSchemeComboBox.getSelectedIndex();

        switch (i) {
            case 0:
                currentParticleSystem.solver = new EulerSolver();
                break;
            case 1:
                currentParticleSystem.solver = new MidPointSolver();
                break;
            case 2:
                currentParticleSystem.solver = new RungeKutta4Solver();
                break;
        }
    }

    private void simulationComboBoxItemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {

            try {
                currentParticleSystem.interrupt();
                currentThread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            particleSystem2D.resetSystem();
            particleSystem3D.resetSystem();

            String item = simulationComboBox.getSelectedItem().toString();

            if (item.startsWith("2D"))
                currentParticleSystem = particleSystem2D;
            else if (item.startsWith("3D"))
                currentParticleSystem = particleSystem3D;
            else
                throw new RuntimeException("Invalid combo box item name!");

            if (item.equals("2D - Simple")) {
                particleSystem2D.initSimpleDemo();
            } else if (item.equals("2D - Cloth")) {
                particleSystem2D.initClothDemo();
            } else if (item.equals("2D - Cloth & Wall")) {
                particleSystem2D.initClothWallDemo();
            } else if (item.equals("2D - Wall")) {
                particleSystem2D.initWallDemo();
            } else if (item.equals("2D - Angular springs")) {
                particleSystem2D.initAngularSpringsDemo();
            } else if (item.equals("3D - Simple")) {
                particleSystem3D.initSimpleDemo();
            } else if (item.equals("3D - Cloth")) {
                particleSystem3D.initClothDemo();
            } else if (item.equals("3D - Cloth & Wall")) {
                particleSystem3D.initClothWallDemo();
            } else {
                    throw new RuntimeException("Invalid combo box item name!");
            }

            currentParticleSystem.cam.drawNetForces = drawNetForcesCheckbox.isSelected();

            startDemo();
        }
    }

    private void variableTimeStepTimeCheckStateChanged(ActionEvent evt) {
        currentParticleSystem.toggleVariableTimeStep();
        timeStepSpinner.setEnabled(!timeStepSpinner.isEnabled());
    }

    private void pauseButtonActionPerformed(ActionEvent evt) {
        currentParticleSystem.togglePause();
        if (currentParticleSystem.isPaused()) {
            pauseButton.setText("Resume");
        }
        else {
            pauseButton.setText("Pause");
        }
    }

    private void timeStepSpinnerStateChanged(ChangeEvent evt) {
        currentParticleSystem.timeStep = ((Double) timeStepSpinner.getValue());
    }

    private void resetButtonActionPerformed(ActionEvent evt) {
        currentParticleSystem.restart();

        if (currentParticleSystem.hasCrashed()) {
            currentThread = new Thread(currentParticleSystem);
            currentThread.start();
        }
    }

    private void startDemo() {
        Solver s = currentParticleSystem.solver;
        if (s instanceof EulerSolver)
            integrationSchemeComboBox.setSelectedIndex(0);
        else if (s instanceof MidPointSolver)
            integrationSchemeComboBox.setSelectedIndex(1);
        else if (s instanceof RungeKutta4Solver)
            integrationSchemeComboBox.setSelectedIndex(2);

        variableTimeStepTimeCheckBox.setSelected(currentParticleSystem.variableTimeStep);
        timeStepSpinner.setValue(currentParticleSystem.timeStep);
        timeStepSpinner.setEnabled(!variableTimeStepTimeCheckBox.isSelected());
        drawNetForcesCheckbox.setSelected(currentParticleSystem.cam.drawNetForces);

        currentThread = new Thread(currentParticleSystem);
        currentThread.start();
    }

    private JFrame frame = new JFrame();
    private Canvas canvas = new Canvas();
    private javax.swing.JComboBox integrationSchemeComboBox;
    private javax.swing.JLabel integrationSchemeLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel panel;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JComboBox simulationComboBox;
    private javax.swing.JLabel simulationLabel;
    private javax.swing.JLabel timeStepLabel;
    private javax.swing.JSpinner timeStepSpinner;
    private javax.swing.JLabel variableTimeStepLabel;
    private javax.swing.JCheckBox variableTimeStepTimeCheckBox;
    private javax.swing.JCheckBox drawNetForcesCheckbox;
    private javax.swing.JLabel drawNetForcesLabel;

    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm();
            }
        });
    }
}