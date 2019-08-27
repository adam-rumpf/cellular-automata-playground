# cellular-automata-playground
A program that lets you play around with various cellular automata to see how they evolve in real time.

I wrote this program in Spring 2012 out of personal interest while taking a Java class. Most of the controls should be relatively straightforward

The main display is a grid of black, white, and possibly gray cells. Black cells are alive, white are dead, and grey are an intermediate state present only for some CA models. Click on a dead cell to make it alive, or a live cell to kill it. Click and drag to change multiple cells.

At the bottom of the screen there are pause/play controls, as well as a dropdown menu to choose the simulation speed and an "Iterate" button to advance by exactly one time step.

The File menu includes controls for clearing the board and randomly inserting live cells onto the board.

The Options menu includes controls to change the rules of the CA model, including the following:

Cellular Automaton:
Conway's Game of Life (a live cell dies if it has less than two or more than four live neighbors, while a dead cell comes to life it if has exactly three live neighbors)
Conway's Rules with Dying (same as above but cells do not immediately die, and instead change into a gray cell that simply takes up space and dies one round later)
Halite Crystal Growth (a dead cell comes to life if it has exactly one live neighbor; cells never die)
Seeds (a live cells die after one time step, while a dead cell comes to life it it has exactly two live neighbors)
Brian's Brain (same as above but cells do not immediately die, and instead change into a gray cell that simply takes up space and dies one round later)

Edge Rules:
Toroidal Edges (edges "wrap around" vertically and horizontally)
Edges as Neighbors (acts as though all cells just beyond the bounds are always alive)
Edges as Empty (acts as though all cells just beyond the bounds are always dead)
