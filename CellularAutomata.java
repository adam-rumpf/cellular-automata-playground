import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;

/**
 * This applet displays a cellular automata. The user is given a variety of options to alter the
 * update rules, edge rules, size, etc.
 */
public class GameOfLife extends JFrame
{
    /*
     * Contents:
     * Definitions-----------------------------
     * GUI-related definitions
     * Other definitions
     * Methods---------------------------------
     * GameOfLife
     * buildMenuBar
     * buildPanels
     * gridClear
     * randomize
     * update
     * addAdjacent
     * Inner Classes---------------------------
     * DrawingPanel
     * ExitListener
     * ClearListener
     * RandomizeListener
     * VisibleGridListener
     * UpdateRuleListener
     * EdgeRuleListener
     * SizeRuleListener
     * PauseUnpauseButtonListener
     * StepButtonListener
     * SpeedBoxListener
     * MouseEvents
     * TimerListener
     * Creating the GUI------------------------
     * main
     */
    
    ////////////////////////////////////////////////////
    // Definitions
    ////////////////////////////////////////////////////
    
    // GUI-related definitions:
    private JPanel gridPanel, controlPanel; // panels for drawing and for controls
    private DrawingPanel drawingPanel; // panel that actually draws everything
    private JButton pauseUnpauseButton, stepButton; // control buttons
    private JComboBox speedBox; // adjusts step speed
    private JTextField cellCoordinates, playIndicator; // display information
    private final int WINDOW_WIDTH = 1050;
    private final int WINDOW_HEIGHT = 700;
    private JMenuBar menuBar;
    private JMenu fileMenu; // change size, clear, randomly seed, exit, etc.
    private JMenu optionMenu; // game options (edge rules), etc.
    private JMenu sizeMenu; // dimensions of grid
    private JMenuItem clearItem, exitItem, randomizeItem;
    private JRadioButtonMenuItem conwayItem, haliteItem, conwayBodiesItem, seedsItem,
        briansBrainItem;
    private JRadioButtonMenuItem toroidalItem, onEdgesItem, offEdgesItem;
    private JRadioButtonMenuItem grid25x15item, grid50x30item, grid100x60item, grid200x120item;
    private JCheckBox visibleGridItem; // whether to draw the grid lines
    protected Timer timer; // the update timer
    
    // Other definitions:
    protected int gridWidth = 50; // default width of game grid
    protected int gridHeight = 30; // default height of game grid
    protected int[][] grid = new int[120][200]; // the cells of the game
    protected boolean filling; // whether clicking will fill or erase cells
    private enum UpdateRule {CONWAY, HALITE, CONWAY_BODIES, SEEDS, BRIANS_BRAIN}; // game rules
    private enum EdgeRule {TOROIDAL, ON_EDGES, OFF_EDGES}; // different edge rules
    protected UpdateRule updateRule = UpdateRule.CONWAY; // currently selected update rule
    protected EdgeRule edgeRule = EdgeRule.TOROIDAL; // currently selected edge rule
    private final String[] speedLabels = {"0.5 steps/sec", "1.0 steps/sec", "1.5 steps/sec",
        "2.0 steps/sec", "2.5 steps/sec", "3.0 steps/sec", "5.0 steps/sec", "10.0 steps/sec",
        "15.0 steps/sec", "20.0 steps/sec"};
    private final double[] speedList = {0.5, 1.0, 1.5, 2.0, 2.0, 3.0, 5.0, 10.0, 15.0, 20.0};
    protected int delay = (int) (1000 / speedList[6]); // milliseconds between timer steps
    protected boolean playing = false; // whether the timer is active
    protected int dx; // cell width
    protected int dy; // cell height
    protected boolean visibleGrid = true; // whether the grid is shown
    
    ////////////////////////////////////////////////////
    // Methods
    ////////////////////////////////////////////////////
    
    /**
     * The constructor adds panels to the window.
     */
    public GameOfLife()
    {
        super("Game of Life");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildMenuBar();
        buildPanels();
        add(gridPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        gridClear();
        timer = new Timer(delay, new TimerListener());
        setVisible(true);
    }
    
    /**
     * Sets up the buttons in the menu bar.
     */
    private void buildMenuBar()
    {
        menuBar = new JMenuBar();
        
        // File menu:
        clearItem = new JMenuItem("Clear");
        randomizeItem = new JMenuItem("Randomly Seed");
        exitItem = new JMenuItem("Exit");
        
        fileMenu = new JMenu("File");
        fileMenu.add(clearItem);
        clearItem.addActionListener(new ClearListener());
        fileMenu.add(randomizeItem);
        randomizeItem.addActionListener(new RandomizeListener());
        fileMenu.add(exitItem);
        exitItem.addActionListener(new ExitListener());
        
        // Option menu (check boxes and radio buttons):
        visibleGridItem = new JCheckBox("Display Grid", true);
        visibleGridItem.addItemListener(new VisibleGridListener());
        conwayItem = new JRadioButtonMenuItem("Conway's Game of Life", true);
        conwayItem.addActionListener(new UpdateRuleListener());
        haliteItem = new JRadioButtonMenuItem("Halite Crystal Growth");
        haliteItem.addActionListener(new UpdateRuleListener());
        conwayBodiesItem = new JRadioButtonMenuItem("Conway's Rules with Dying");
        conwayBodiesItem.addActionListener(new UpdateRuleListener());
        seedsItem = new JRadioButtonMenuItem("Seeds");
        seedsItem.addActionListener(new UpdateRuleListener());
        briansBrainItem = new JRadioButtonMenuItem("Brian's Brain");
        briansBrainItem.addActionListener(new UpdateRuleListener());
        
        toroidalItem = new JRadioButtonMenuItem("Toroidal Edges", true);
        toroidalItem.addActionListener(new EdgeRuleListener());
        onEdgesItem = new JRadioButtonMenuItem("Edges as Neighbors");
        onEdgesItem.addActionListener(new EdgeRuleListener());
        offEdgesItem = new JRadioButtonMenuItem("Edges as Empty");
        offEdgesItem.addActionListener(new EdgeRuleListener());
        
        grid25x15item = new JRadioButtonMenuItem("25 x 15");
        grid25x15item.addActionListener(new SizeRuleListener());
        grid50x30item = new JRadioButtonMenuItem("50 x 30", true);
        grid50x30item.addActionListener(new SizeRuleListener());
        grid100x60item = new JRadioButtonMenuItem("100 x 60");
        grid100x60item.addActionListener(new SizeRuleListener());
        grid200x120item = new JRadioButtonMenuItem("200 x 120");
        grid200x120item.addActionListener(new SizeRuleListener());
        
        ButtonGroup ruleGroup = new ButtonGroup();
        ruleGroup.add(conwayItem);
        ruleGroup.add(conwayBodiesItem);
        ruleGroup.add(haliteItem);
        ruleGroup.add(seedsItem);
        ruleGroup.add(briansBrainItem);
        
        ButtonGroup edgeGroup = new ButtonGroup();
        edgeGroup.add(toroidalItem);
        edgeGroup.add(onEdgesItem);
        edgeGroup.add(offEdgesItem);
        
        ButtonGroup sizeGroup = new ButtonGroup();
        sizeGroup.add(grid25x15item);
        sizeGroup.add(grid50x30item);
        sizeGroup.add(grid100x60item);
        sizeGroup.add(grid200x120item);
        
        optionMenu = new JMenu("Options");
        optionMenu.add(visibleGridItem);
        optionMenu.addSeparator();
        optionMenu.add(conwayItem);
        optionMenu.add(conwayBodiesItem);
        optionMenu.add(haliteItem);
        optionMenu.add(seedsItem);
        optionMenu.add(briansBrainItem);
        optionMenu.addSeparator();
        optionMenu.add(toroidalItem);
        optionMenu.add(onEdgesItem);
        optionMenu.add(offEdgesItem);
        optionMenu.addSeparator();
        optionMenu.add(grid25x15item);
        optionMenu.add(grid50x30item);
        optionMenu.add(grid100x60item);
        optionMenu.add(grid200x120item);
        
        // Activating the menu bar:
        menuBar.add(fileMenu);
        menuBar.add(optionMenu);
        setJMenuBar(menuBar);
    }
    
    /**
     * Initializes panels and panel elements, and places controls in control panel.
     */
    private void buildPanels()
    {
        // Grid panel stuff:
        gridPanel = new JPanel();
        drawingPanel = new DrawingPanel();
        drawingPanel.addMouseListener(new MouseEvents());
        drawingPanel.addMouseMotionListener(new MouseEvents());
        gridPanel.add(drawingPanel);
        
        // Control panel stuff:
        controlPanel = new JPanel();
        speedBox = new JComboBox(speedLabels);
        speedBox.setSelectedIndex(6);
        speedBox.addActionListener(new SpeedBoxListener());
        pauseUnpauseButton = new JButton("Pause/Play");
        pauseUnpauseButton.addActionListener(new PauseUnpauseButtonListener());
        stepButton = new JButton("Iterate");
        stepButton.addActionListener(new StepButtonListener());
        cellCoordinates = new JTextField(7);
        cellCoordinates.setEditable(false);
        cellCoordinates.setText("Cell 0, 0");
        playIndicator = new JTextField(4);
        playIndicator.setEditable(false);
        playIndicator.setText("Paused");
        controlPanel.add(cellCoordinates);
        controlPanel.add(playIndicator);
        controlPanel.add(pauseUnpauseButton);
        controlPanel.add(stepButton);
        controlPanel.add(speedBox);
    }
    
    /**
     * Clears the entire grid (sets cells to 0).
     */
    protected void gridClear()
    {
        for (int i = 0; i < gridHeight; i++)
        {
            for (int j = 0; j < gridWidth; j++)
                grid[i][j] = 0;
        }
        
        drawingPanel.repaint();
    }
    
    /**
     * Randomizes state of every cell. The user can select the density of "on" cells.
     */
    protected void randomize()
    {
        boolean looping;
        double density = 0;
        
        // Input validation:
        do
        {
            looping = false;
            String input = JOptionPane.showInputDialog(null, "Enter the density of \"on\" " +
                "cells (as a decimal between 0 and 1).", "Select Density",
                JOptionPane.QUESTION_MESSAGE);
            try
            {
                density = Double.parseDouble(input);
            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null, "That wasn't a decimal value. Please retry.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                looping = true;
            }
            finally
            {
                if (density < 0 || density > 1)
                {
                    JOptionPane.showMessageDialog(null, "That wasn't a value between 0 and 1." +
                        " Please retry.", "Error", JOptionPane.ERROR_MESSAGE);
                    looping = true;
                }
            }
        } while (looping);
        
        // Randomizing the cells:
        for (int i = 0; i < gridHeight; i++)
        {
            for (int j = 0; j < gridWidth; j++)
            {
                double number = Math.random();
                if (number <= density)
                    grid[i][j] = 1;
                else
                    grid[i][j] = 0;
            }
        }
        
        drawingPanel.repaint();
    }
    
    /**
     * Goes through each cell to conduct the proper updates.
     */
    protected void update()
    {
        // Parallel updating requires us to used a temporary grid to figure out the updates.
        int[][] tempGrid = new int[gridHeight][gridWidth];
        
        // This temporary grid holds the number of neighbors each cell possesses.
        for (int i = 0; i < gridHeight; i++)
        {
            for (int j = 0; j < gridWidth; j++)
                tempGrid[i][j] = 0;
        }
        for (int i = 0; i < gridHeight; i++)
        {
            for (int j = 0; j < gridWidth; j++)
            {
                if (grid[i][j] == 1)
                    addAdjacent(tempGrid, i, j);
            }
        }
        
        /*
         * In the case of the edge on model, we assume that every cell just outside the grid is
         * eternally on. We need to go through and add 3 neighbors to all edge cells, and 5 to
         * corner cells. We use 2 steps: add 3 to top/bottom, add 3 to left/right, then subtract 1
         * from corners.
         */
        if (edgeRule == EdgeRule.ON_EDGES)
        {
            for (int i = 0; i < gridWidth; i++)
            {
                tempGrid[0][i] += 3;
                tempGrid[gridHeight-1][i] += 3;
            }
            for (int i = 0; i < gridHeight; i++)
            {
                tempGrid[i][0] += 3;
                tempGrid[i][gridWidth-1] += 3;
            }
            tempGrid[0][0]--;
            tempGrid[gridHeight-1][0]--;
            tempGrid[0][gridWidth-1]--;
            tempGrid[gridHeight-1][gridWidth-1]--;
        }
        
        // Finally, we update the main grid.
        
        for (int i = 0; i < gridHeight; i++)
        {
            for (int j = 0; j < gridWidth; j++)
            {
                switch (updateRule)
                {
                    case CONWAY:
                        /*
                         * Conway's rules:
                         * An on cell turns off if it has less than 2 or more than 3 neighbors.
                         * An off cell turns on if it has exactly 3 neighbors.
                         */
                        
                        if (grid[i][j] == 1)
                        {
                            if (tempGrid[i][j] < 2 || tempGrid[i][j] > 3)
                                grid[i][j] = 0;
                        }
                        else
                        {
                            if (tempGrid[i][j] == 3)
                                grid[i][j] = 1;
                        }
                        break;
                    
                    case HALITE:
                        /*
                         * Halite model rules:
                         * An off cell turns on if it has exactly one neighbor. Cells never
                         * turn off.
                         */
                        
                        if (grid [i][j] == 0)
                        {
                            if (tempGrid[i][j] == 1)
                                grid [i][j] = 1;
                        }
                        break;
                    
                    case CONWAY_BODIES:
                        /*
                         * Conway's rules with dead bodies:
                         * Same as Conway, except that when cells die, instead of disappearing,
                         * they turn into a dead cell. Dead cells to not count towards neighbor
                         * count, but they do prevent anything from growing on them. They
                         * disappear after one step.
                         */
                        
                        if (grid[i][j] == 1)
                        {
                            if (tempGrid[i][j] < 2 || tempGrid[i][j] > 3)
                                grid[i][j] = 2;
                        }
                        else if (grid[i][j] == 0)
                        {
                            if (tempGrid[i][j] == 3)
                                grid[i][j] = 1;
                        }
                        else
                            grid[i][j] = 0;
                        break;
                    
                    case SEEDS:
                        /*
                         * Seeds rules:
                         * An off cell turns on if it has exactly 2 neighbors. All on cells turn
                         * off after one step.
                         */
                        
                        if (grid[i][j] == 0)
                        {
                            if (tempGrid[i][j] == 2)
                                grid[i][j] = 1;
                        }
                        else
                            grid[i][j] = 0;
                        break;
                    
                    case BRIANS_BRAIN:
                        /*
                         * Brian's Brain rules:
                         * Same as Seeds, except that when a cell dies, it leaves behind a body
                         * for one step.
                         */
                        
                        if (grid[i][j] == 0)
                        {
                            if (tempGrid[i][j] == 2)
                                grid[i][j] = 1;
                        }
                        else if (grid[i][j] == 1)
                            grid[i][j] = 2;
                        else
                            grid[i][j] = 0;
                        break;
                    
                    default:
                        JOptionPane.showMessageDialog(null, "No update rule selected (somehow).",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        drawingPanel.repaint();
    }
    
    /**
     * Updates the temporary grid from the update algorithm by adding 1 to every cell adjacent to
     * the specified cell, as long as it's filled.
     */
    private void addAdjacent(int[][] tempGrid, int i, int j)
    {
        for (int a = -1; a < 2; a++)
        {
            for (int b = -1; b < 2; b++)
            {
                // We figure out the coordinates of i,j's neighbors.
                int y = i + a;
                int x = j + b;
                
                switch (edgeRule)
                {
                    case TOROIDAL:
                        /*
                         * Out-of-bounds neighbor coordinates wrap around to the other side.
                         */
                        if (y < 0)
                            y = gridHeight - 1;
                        if (y > gridHeight - 1)
                            y = 0;
                        if (x < 0)
                            x = gridWidth - 1;
                        if (x > gridWidth - 1)
                            x = 0;
                        if (a != 0 || b != 0)
                            tempGrid[y][x]++;
                        break;
                    
                    case ON_EDGES:
                    case OFF_EDGES:
                        /*
                         * In either system, we simply ignore out-of-bounds cells. Edge effects
                         * are evaluated in the update code, itself.
                         */
                        if (y >= 0 && y < gridHeight && x >= 0 && x < gridWidth)
                        {
                            if (a != 0 || b != 0)
                                tempGrid[y][x]++;
                        }
                        break;
                    
                    default:
                        JOptionPane.showMessageDialog(null, "No edge rule selected (somehow).",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    ////////////////////////////////////////////////////
    // Inner Classes
    ////////////////////////////////////////////////////
      
    /**
     * This is the panel which actually draws the grid and everything on it.
     */
    private class DrawingPanel extends JPanel
    {
        private final int WIDTH = 1000;
        private final int HEIGHT = 600;
        
        // Standard drawing panel methods:
        
        public DrawingPanel()
        {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
        }
        
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            
            dx = (int) WIDTH / gridWidth;
            dy = (int) HEIGHT / gridHeight;
            
            // Drawing the cells on the grid:
            for (int i = 0; i < gridHeight; i++)
            {
                for (int j = 0; j < gridWidth; j++)
                {
                    if (grid[i][j] == 1)
                    {
                        g.setColor(Color.BLACK);
                        g.fillRect(dx*j+1, dy*i+1, dx, dy);
                    }
                    else if (grid[i][j] == 2) // dead body from the body rules
                    {
                        g.setColor(Color.GRAY);
                        g.fillRect(dx*j+1, dy*i+1, dx, dy);
                    }
                }
            }
            
            // Drawing the grid:
            if (visibleGrid)
            {
                g.setColor(Color.BLACK);
                for (int i = 0; i < gridWidth; i++)
                    g.drawLine(dx*i, 0, dx*i, HEIGHT);
                for (int i = 0; i < gridHeight; i++)
                    g.drawLine(0, dy*i, WIDTH, dy*i);
            }
        }
        
        // Custom methods:
        
        public int getWidth()
        {
            return WIDTH;
        }
        
        public int getHeight()
        {
            return HEIGHT;
        }
    }
    
    /**
     * Closes the program.
     */
    private class ExitListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    }
    
    /**
     * Sets all cells to blank.
     */
    private class ClearListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            gridClear();
        }
    }
    
    /**
     * Randomizes cells with a selectable density.
     */
    private class RandomizeListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            randomize();
        }
    }
    
    /**
     * Determines whether to draw the grid lines.
     */
    private class VisibleGridListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            if (visibleGridItem.isSelected())
                visibleGrid = true;
            else
                visibleGrid = false;
        }
    }
    
    /**
     * Alters the update rules.
     */
    private class UpdateRuleListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (conwayItem.isSelected())
                updateRule = UpdateRule.CONWAY;
            else if (haliteItem.isSelected())
                updateRule = UpdateRule.HALITE;
            else if (conwayBodiesItem.isSelected())
                updateRule = UpdateRule.CONWAY_BODIES;
            else if (seedsItem.isSelected())
                updateRule = UpdateRule.SEEDS;
            else if (briansBrainItem.isSelected())
                updateRule = UpdateRule.BRIANS_BRAIN;
            else // Unexpected selections default to Conway.
                updateRule = UpdateRule.CONWAY;
        }
    }
    
    /**
     * Alters the edge rules.
     */
    private class EdgeRuleListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (toroidalItem.isSelected())
                edgeRule = EdgeRule.TOROIDAL;
            else if (onEdgesItem.isSelected())
                edgeRule = EdgeRule.ON_EDGES;
            else if (offEdgesItem.isSelected())
                edgeRule = EdgeRule.OFF_EDGES;
            else // Unexpected selections default to toroidal.
                edgeRule = EdgeRule.TOROIDAL;
        }
    }
    
    /**
     * Alters the size of the grid.
     */
    private class SizeRuleListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (grid25x15item.isSelected())
            {
                gridWidth = 25;
                gridHeight = 15;
            }
            else if (grid50x30item.isSelected())
            {
                gridWidth = 50;
                gridHeight = 30;
            }
            else if (grid100x60item.isSelected())
            {
                gridWidth = 100;
                gridHeight = 60;
            }
            else if (grid200x120item.isSelected())
            {
                gridWidth = 200;
                gridHeight = 120;
            }
            else // Unexpected selections default to 50 x 30.
            {
                gridWidth = 50;
                gridHeight = 30;
            }
            gridClear();
        }
    }
    
    /**
     * Toggles the timer.
     */
    private class PauseUnpauseButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (playing)
            {
                timer.stop();
                playing = false;
                playIndicator.setText("Paused");
            }
            else
            {
                timer.start();
                playing = true;
                playIndicator.setText("Playing");
            }
        }
    }
    
    /**
     * Updates the grid by one step without actually starting the timer.
     */
    private class StepButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            timer.stop();
            playIndicator.setText("Paused");
            update();
        }
    }
    
    /**
     * Updates the play speed.
     */
    private class SpeedBoxListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            delay = (int) (1000 / speedList[speedBox.getSelectedIndex()]);
            timer.setDelay(delay);
        }
    }
    
    /**
     * This class contains the events for both moving and clicking the mouse. Note that instances
     * of this listener are registered for the drawing panel, itself.
     */
    private class MouseEvents implements MouseListener, MouseMotionListener
    {
        // Definitions:
        private int cellX; // j coordinate of cell that the mouse is over
        private int cellY; // i coordinate of cell that the mouse is over
        
        // Standard mouse event methods:
        
        /**
         * Displays the cell the mouse is currently over, and allows cells to be painted.
         */
        public void mouseDragged(MouseEvent e)
        {
            reposition(e);
            grid[cellY][cellX] = (filling)? 1: 0;
            reposition(e);
        }
        
        /**
         * Displays the cell the mouse is currently over.
         */
        public void mouseMoved(MouseEvent e)
        {
            reposition(e);
        }

        public void mouseClicked(MouseEvent e) {}
        
        public void mouseEntered(MouseEvent e) {}
        
        public void mouseExited(MouseEvent e) {}
        
        public void mousePressed(MouseEvent e)
        {
            /*
             * If the user clicks and drags on an empty cell, we assume they're trying to paint
             * cells. If they click and drag on a filled cell, we assume they're trying to erase
             * cells. Once we pick one, any other empty/filled cells get filled/erased,
             * respectively. We only update the filling variable as soon as they click.
             */
            reposition(e);
            filling = (grid[cellY][cellX] == 1)? false: true;
            grid[cellY][cellX] = (filling)? 1: 0;
            reposition(e);
        }
        
        public void mouseReleased(MouseEvent e) {}
        
        // Custom methods:
        
        public void reposition(MouseEvent e)
        {
            cellX = (int) (1.0 * e.getX()) / dx; // cell j coordinate (starts at 0)
            cellY = (int) (1.0 * e.getY()) / dy; // cell i coordinate (starts at 0)
            
            /*
             * Near the edges of the screen the cell coordinates may mistakenly become too high or
             * low. We need to manually restrict them.
             */
            if (cellX > gridWidth - 1)
                cellX = gridWidth - 1;
            if (cellX < 0)
                cellX = 0;
            if (cellY > gridHeight - 1)
                cellY = gridHeight - 1;
            if (cellY < 0)
                cellY = 0;
            
            cellCoordinates.setText("Cell " + cellX + ", " + cellY);
            drawingPanel.repaint();
        }
    }
    
    /**
     * Every time the timer "fires", the grid updates.
     */
    private class TimerListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            update();
        }
    }
    
    ////////////////////////////////////////////////////
    // Main Method
    ////////////////////////////////////////////////////
    
    /**
     * Creates an instance of the GUI window.
     */
    public static void main(String[] args)
    {
        new GameOfLife();
    }
}
