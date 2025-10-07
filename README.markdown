# Snake Game (Java Swing)

A modernized Snake Game built with Java 21, featuring a clean UI, real-time player analytics, and persistent data storage. Player statistics are logged in a CSV file and visualized in an interactive statistics screen.

## Project Overview

This project is a modularized Snake Game developed using Java Swing and Maven. It includes multiple screens and components that follow an organized architecture.

## Game Features

- Classic Snake gameplay with responsive controls
- Modern intro screen and menu navigation
- Real-time score, length, and time tracking
- Persistent player analytics stored in a CSV file
- Statistics dashboard (totals, averages, longest/shortest runs)
- Data reset functionality
- Logging using Log4j2 for better debugging and event tracing

## 🏗️ Project Architecture

### Package Structure
```
org.example
│
├── model        # Data models (SessionData, UserAnalytics)
├── service      # File handling and analytics calculation (FileService)
├── ui           # Swing UI components (IntroScreenUI, GameUI, UserStatsUI)
├── utils        # Utility classes (Formatter, Log helpers)
└── Snake.java   # Application entry point
```

### Key Components

#### IntroScreenUI
- Acts as the main menu screen
- Allows players to start the game or view statistics

#### GameUI
- Core game logic: snake movement, collision detection, and food spawning
- Tracks metrics like time, moves, and food eaten per session

#### UserStatsUI
- Displays player statistics loaded from the CSV file
- Sections:
  - **Totals**: Cumulative stats from all games
  - **Averages**: Mean values per session
  - **Longest/Shortest**: Best and least performing sessions
- Includes **Back** and **Reset** buttons
  - **Back** → returns to the intro screen
  - **Reset** → clears CSV data and resets on-screen stats

#### FileService
- Handles all CSV reading/writing operations
- Maintains a list of session data and computes analytics
- Uses OpenCSV for file operations
- Includes safe error handling and logging with Log4j2

#### Formatter
- Converts time values (milliseconds → days/hours/minutes/seconds)
- Used across UI components for clean display formatting

## ⚙️ Technologies Used
| Tool / Library | Purpose |
|----------------|---------|
| Java 21        | Core language |
| Swing          | UI framework |
| OpenCSV        | CSV file handling |
| Lombok         | Boilerplate reduction (@Data, @Builder, etc.) |
| Log4j2         | Logging framework |
| JUnit 5        | Testing framework |
| Maven          | Build and dependency management |

## Running the Project

### Prerequisites
- Java 21+
- Maven 3.9+
- An IDE such as IntelliJ IDEA or VS Code

### Steps
1. **Clone the repository**:
   ```bash
   git clone https://github.com/NelaniMaluka/SnakeGame.git
   cd Snake
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the game**:
   ```bash
   mvn exec:java -Dexec.mainClass="org.example.Snake"
   ```

## Data Storage
Player stats are stored in a CSV file (e.g., `data/sessions.csv`) with the following structure:
```
GameID,Score,Moves,FoodEaten,TimeSpent(ms),Date
1,250,120,10,120000,2025-10-06T12:30
```

This allows the analytics system to:
- Track player progress over time
- Compute averages and high scores
- Restore stats even after restarting the game

## Reset Functionality
When the **Reset** button is pressed:
- The CSV file is cleared (except for headers).
- The on-screen statistics are refreshed to zero.
- The action is logged in `logs/app.log` via Log4j2.

## Logging
Log4j2 is configured to track:
- File I/O operations
- Player session starts/ends
- Reset and error events

Example snippet:
```java
log.info("User statistics reset successfully.");
log.error("Failed to load session data from CSV file.", e);
```

Logs are stored in the `/logs` directory.

## 🧑‍💻 Author
**Nelani Maluka**  
🎓 Software Developer  
💻 Passionate about Java, Spring Boot, and Backend Development
