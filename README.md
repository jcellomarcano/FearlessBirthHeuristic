# Fearless Birth Heuristic - Algorithm Design Project

[![Kotlin](https://img.shields.io/badge/Kotlin-1.x-blue.svg)](https://kotlinlang.org)
[![Build Tool](https://img.shields.io/badge/Build-Gradle-green.svg)](https://gradle.org)
## Overview

This repository contains a Kotlin implementation of a custom metaheuristic developed to solve the **0/1 Knapsack Problem (KP)**[cite: 1, 6]. This project, internally named "Fearless Birth Heuristic", implements the **"El que tenga miedo a morir, que no nazca"** metaheuristic described in the accompanying research paper[cite: 36]. The work was completed as a project for the Algorithm Design subject.

The implementation demonstrates heuristic concepts applied to combinatorial optimization, focusing on quickly finding near-optimal solutions for the KP[cite: 6, 10, 44, 60].

## Context

This project was created as part of the coursework for the Algorithm Design subject at Universidad Simón Bolívar[cite: 1]. The primary goal was to design, implement, and evaluate heuristic algorithms for the 0/1 Knapsack Problem, comparing a novel approach against standard methods like Dynamic Programming, Genetic Algorithms, and Ant Colony Optimization[cite: 1, 3, 12].

## The "El que tenga miedo a morir, que no nazca" Metaheuristic

This metaheuristic, developed by the authors Jesús Marcano and Pedro Samuel Fagundez[cite: 1, 36], tackles the 0/1 Knapsack problem with a strategy inspired by its name.

1.  **Core Idea:** Items are divided into two groups based on their value-to-weight ratio relative to a calculated threshold[cite: 36, 37].
    * **"Fearless" items:** Those with a high value/weight ratio (above the threshold). These are prioritized for inclusion in the knapsack[cite: 38, 40].
    * **"Cautious" items:** Those with a lower value/weight ratio (below the threshold). These are considered only after attempting to fill the knapsack with fearless items[cite: 38, 41].
2.  **Algorithm Steps:**
    * Calculate the value/weight ratio for all items.
    * Determine a threshold to separate items into "fearless" and "cautious" groups[cite: 36].
    * Fill the knapsack primarily with items from the "fearless" group, ensuring the total weight does not exceed capacity[cite: 38].
    * If capacity remains, fill the remaining space with items from the "cautious" group[cite: 38].
    * The goal is to maximize the total value within the knapsack's weight limit[cite: 39].
3.  **Problem Domain:** This heuristic is specifically designed for the **0/1 Knapsack Problem**[cite: 1, 6, 9].

While simple and fast, the paper notes the current implementation might converge to sub-optimal solutions in some cases, and suggests potential enhancements like introducing randomness or adaptive thresholds to improve exploration[cite: 42, 43, 68, 70].

## Features

* Implementation of the "El que tenga miedo a morir, que no nazca" metaheuristic.
* Targets the 0/1 Knapsack Problem[cite: 1, 6].
* Designed for speed, often outperforming Dynamic Programming in execution time while finding near-optimal solutions[cite: 44, 57, 60].
* Written in Kotlin, utilizing modern programming practices.
* Built using the Gradle build tool.

## Technology Stack

* **Language:** Kotlin [1.9.x]
* **Build Tool:** Gradle [8.x]
* **Runtime:** JVM [JDK 17]

## Prerequisites

* Java Development Kit (JDK) [JDK 17 or newer]

## Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/jcellomarcano/FearlessBirthHeuristic.git](https://github.com/jcellomarcano/FearlessBirthHeuristic.git)
    cd FearlessBirthHeuristic
    ```

2.  **Build the project:**
    The project uses the Gradle wrapper, so you don't need to install Gradle separately.
    ```bash
    # On Linux/macOS
    ./gradlew build

    # On Windows
    .\gradlew.bat build
    ```
    This command will download dependencies and compile the source code.

## Usage

[**Confirm or update these instructions based on your project's specific entry point and arguments.**]

* **Running the heuristic:**
    Use the Gradle `run` task (assuming you have configured the `application` plugin in your `build.gradle.kts`):
    ```bash
    # On Linux/macOS
    ./gradlew run [Optional Arguments]

    # On Windows
    .\gradlew.bat run [Optional Arguments]
    ```
    * [**Specify if command-line arguments are needed (e.g., path to knapsack problem instance file).**]
    * [**Describe the expected output (e.g., prints the selected items, total value, total weight to console).**]

* **Example:**
    [**Provide a concrete example command if possible.**]
    ```bash
    # Example: Specify the input file if needed
    ./gradlew run --args="<path_to_knapsack_data_file>"
    ```

## Project Structure
```
.
├── .gradle/          # Gradle cache and metadata
├── .idea/            # IDE configuration (optional, often in .gitignore)
├── build/            # Compiled code and build artifacts
├── gradle/           # Gradle wrapper files
├── src/
│   ├── main/
│   │   ├── kotlin/   # Main Kotlin source code
│   │   │   └── ...   # Your package structure and .kt files
│   │   └── resources/ # Resource files (e.g., input data)
│   └── test/
│       ├── kotlin/   # Unit/Integration test source code
│       └── resources/ # Test resource files
├── build.gradle.kts  # Gradle build script (Kotlin DSL)
├── gradlew           # Gradle wrapper script (Linux/macOS)
├── gradlew.bat       # Gradle wrapper script (Windows)
├── LICENSE           # [Recommended] Add a license file (e.g., MIT, Apache 2.0)
├── README.md         # This file
└── settings.gradle.kts # Gradle settings script
```

## Results / Evaluation

The performance of this metaheuristic ("El que tenga miedo a morir, que no nazca") was evaluated against other algorithms (Dynamic Programming, Genetic Algorithms, Ant Colony, Local Search, Tabu Search, Memetic Algorithm) on 0/1 Knapsack Problem instances with 250 and 500 items[cite: 1, 12, 47, 49].

* **Speed:** The heuristic demonstrated significantly faster execution times compared to most other methods, including Dynamic Programming (DP)[cite: 57].
* **Solution Quality:** While DP guarantees the optimal solution, this metaheuristic consistently found solutions very close to the optimal value, and in many cases, found the optimal value itself[cite: 57, 62]. It generally outperformed other heuristics like GA, ACO, Tabu Search, and Memetic Algorithms in terms of the quality/time trade-off for this problem[cite: 54, 55, 56, 60, 63].
* **Trade-off:** It serves as an excellent choice when a very fast, near-optimal approximation is sufficient, whereas DP should be used if the absolute optimal solution is strictly required[cite: 60, 61].

Detailed results can be found in the accompanying paper[cite: 47, 49, 52].

## References

* Marcano, J., & Fagundez, P. S. (n.d.). *Knapsack Problem solucionado con múltiple enfoques*. Universidad Simón Bolívar. [*(Accompanying paper PDF)*] [cite: 1]
* GeeksforGeeks. (n.d.). *0/1 Knapsack Problem | DP-10*. Retrieved from [https://www.geeksforgeeks.org/0-1-knapsack-problem-dp-10/](https://www.geeksforgeeks.org/0-1-knapsack-problem-dp-10/) [cite: 2]

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

* **jcellomarcano** (As part of the paper by Jesús Marcano and Pedro Samuel Fagundez [cite: 1])

---
