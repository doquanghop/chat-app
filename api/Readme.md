# 📩 Telegram Clone

A full-stack real-time chat application inspired by Telegram.  
This project is built for learning purposes and demonstrates how modern chat apps work, including messaging, group chats, and user presence.

---

## 🚀 Features

- 🔒 Authentication (Register & Login)
- 💬 One-on-one chat
- 👥 Group chat with multiple participants
- 📡 Real-time messaging using WebSocket
- 🕒 Message timestamps and delivery status
- ✅ Message read tracking
- 🔔 Online/offline user presence
- 📁 Media/file message support (optional)
- 🔎 User search and add to conversation

# Arduino Maze Solver Robot

## Overview
This project implements an autonomous maze-solving robot using Arduino. The robot uses line following sensors to navigate through a maze and obstacle detection sensors to avoid collisions. It implements various algorithms for maze exploration and path finding.

## Project Structure
```
arduino-maze-solver/
├── src/                     # Source code directory
│   ├── constants.h          # Project constants and configurations
│   ├── line_sensor.*        # Line following sensor implementation
│   ├── motor_control.*      # Motor control implementation
│   ├── obstacle_sensor.*    # HC-SR04 ultrasonic sensor implementation
│   ├── robot_line.*         # Line following robot logic
│   ├── robot_scan.*         # Maze scanning and navigation logic
│   └── main.ino             # Main Arduino program
├── data/                    # Data directory
│   ├── map.txt             # Maze map data
│   └── routes.txt          # Scanned routes data
└── wander_algo.cpp         # Wandering algorithm implementation
```

## Hardware Requirements
- Arduino board (compatible with Arduino IDE)
- Line following sensors
- HC-SR04 Ultrasonic sensor
- DC Motors with motor drivers
- Power supply
- Chassis and wheels

## Software Requirements
- Arduino IDE (version 2.0 or higher)
- Required Arduino libraries:
  - Wire.h
  - NewPing.h (for HC-SR04 sensor)

## Features
- Line following capability
- Obstacle detection and avoidance
- Maze mapping and exploration
- Path finding algorithms
- Autonomous navigation

## Setup Instructions
1. Clone this repository
2. Open the project in Arduino IDE
3. Install required libraries
4. Connect hardware components according to pin configurations in `constants.h`
5. Upload the code to your Arduino board

## Pin Configuration
The pin configurations can be found in `constants.h`. Make sure to connect the components according to these configurations:
- Line sensors
- Motor control pins
- Ultrasonic sensor pins

## Usage
1. Power on the robot
2. Place the robot at the start of the maze
3. The robot will automatically:
   - Follow the line
   - Detect obstacles
   - Map the maze
   - Find the optimal path

## Algorithms
The project implements several algorithms:
- Line following algorithm
- Obstacle avoidance
- Maze exploration (wandering algorithm)
- Path finding

## Contributing
Feel free to submit issues and enhancement requests.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments
- Arduino community
- Contributors and testers
- Open source libraries used in this project