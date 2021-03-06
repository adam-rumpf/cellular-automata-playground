# Cellular Automata Playground

<a href="https://adam-rumpf.itch.io/cellular-automata-playground"><img src="https://img.shields.io/badge/download-itch.io-fa5c5c?style=plastic&logo=itch.io&logoColor=white" alt="itch.io page"/></a> <a href="https://github.com/adam-rumpf/cellular-automata-playground/search?l=java"><img src="https://img.shields.io/badge/language-java-blue?style=plastic&logo=java&logoColor=white"/></a> <a href="https://github.com/adam-rumpf/cellular-automata-playground/releases"><img src="https://img.shields.io/github/v/release/adam-rumpf/cellular-automata-playground?style=plastic"/></a> <a href="https://github.com/adam-rumpf/cellular-automata-playground/blob/master/LICENSE"><img src="https://img.shields.io/github/license/adam-rumpf/cellular-automata-playground?style=plastic"/></a> <a href="https://github.com/adam-rumpf/cellular-automata-playground/commits/master"><img src="https://img.shields.io/maintenance/no/2019?style=plastic"/></a>

A program that lets you play around with various cellular automata to see how they evolve in real time.

Click [here](https://github.com/adam-rumpf/cellular-automata-playground/releases/tag/v1.1.1) for the latest release.

I wrote this program in Spring 2012 out of personal interest while taking a _Java_ class. At the time it was by far the most complicated project I had ever worked on. Hopefully it will be of interest to you now.

The main display is a grid of black, white, and possibly gray cells. Black cells are alive, white are dead, and gray are an intermediate state present only for some CA models. Click on a dead cell to make it alive, or a live cell to kill it. Click and drag to change multiple cells.

At the bottom of the screen there are **Pause/Play** controls, as well as a dropdown menu to choose the simulation **Speed** and an **Iterate** button to advance by exactly one time step.

The **File** menu includes controls for clearing the board and randomly seeding the board with a specified density of live cells.

The **Options** menu includes controls to change the rules of the CA model, including the following:

## Cellular Automaton

* **Conway's Game of Life:** A live cell dies if it has less than two or more than four live neighbors, while a dead cell comes to life it if has exactly three live neighbors.
* **Conway's Rules with Dying:** Same as above but cells do not immediately die, and instead change into a gray cell that simply takes up space and dies one round later.
* **Halite Crystal Growth:** A dead cell comes to life if it has exactly one live neighbor. Cells never die.
* **Seeds** A live cell dies after one time step, while a dead cell comes to life it it has exactly two live neighbors.
* **Brian's Brain:** Same as above but cells do not immediately die, and instead change into a gray cell that simply takes up space and dies one round later.
* **Odd Rule:** A live cell dies after one time step, while a dead cell comes to life if it has an odd number of live neighbors.
* **Even Rule:** Analogous to above but a dead cell comes to life if it has an even, nonzero number of live neighbors.

## Edge Rules

* **Toroidal Edges:** Edges "wrap around" vertically and horizontally.
* **Edges as Neighbors:** Acts as though all cells just beyond the bounds are always alive.
* **Edges as Empty:** Acts as though all cells just beyond the bounds are always dead.

## Screenshots

### Conway's Game of Life

<img src="https://raw.githubusercontent.com/adam-rumpf/adam-rumpf.github.io/master/images/ca/ca-01.png" alt="Conway's Game of Life" width="600"/>

### Halite Crystal Growth

<img src="https://raw.githubusercontent.com/adam-rumpf/adam-rumpf.github.io/master/images/ca/ca-02.png" alt="Halite Crystal Growth" width="600"/>

### Brian's Brain

<img src="https://raw.githubusercontent.com/adam-rumpf/adam-rumpf.github.io/master/images/ca/ca-03.png" alt="Brian's Brain" width="600"/>
