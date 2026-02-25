# ICOOP – 2D Grid-Based Platformer (Java)

2D Zelda-style game developed at EPFL (CS-107).

Built on top of an academic game engine framework, the project implements the complete gameplay layer: player logic, enemy behaviors, combat system, interactions, and level design.

---

## Overview

ICOOP is a tile-based platformer featuring:

- Grid-based movement
- Collision-driven interactions
- Multiple levels
- Inventory and health system
- Melee and projectile combat
- Enemy AI with pathfinding and movement patterns
- Directional animations (N/S/E/W)

Rendering, window management, and the base game loop are provided by the EPFL framework.  
All gameplay systems and entity logic were implemented by our team.

---

## Architecture

The project is structured in layers:

- **Engine layer** (provided): rendering, window, core loop  
- **Gameplay layer**: entities, combat logic, AI, interactions  
- **Level layer**: level definitions written in Java  

Enemies inherit from abstract base classes to allow behavior reuse and extension.  
Collisions are handled using a grid-occupancy system.

---

## Tech Stack

- Java
- Maven
- Grid-based collision system
- Pathfinding-based enemy movement

---

## Run the project

The entry point of the game is: iccoop/src/main/java/ch/epfl/cs107/Play.java

### Run from an IDE (recommended)

Open the project in IntelliJ (or another Java IDE) and run: ch.epfl.cs107.Play


### Run with Maven

From the project root directory:

```bash
mvn clean install
mvn exec:java -pl iccoop -Dexec.mainClass="ch.epfl.cs107.Play"
```

---

## Authors

Mamoun Chami
Wejdane Mansouri
