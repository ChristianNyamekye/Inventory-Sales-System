# Oracle-Based Inventory and Sales Management System (OISMS)

## Authors
- Chan Tin Yik 
- Christian Nyamekye 
- Daniel Gakpetor 

## Video Demonstration
[Video Demonstration](demo_vid.mov)

## Project Structure

### Library Dependencies
- `lib/ojdbc8.jar`: Oracle JDBC driver for database connectivity

### Source Files (`src/`)
- `Main.java`: Core menu system and program coordinator
- `Administrator.java`: Database management and administrative functions
  - Table creation and deletion
  - Data file loading
  - Record viewing capabilities
- `Salesperson.java`: Sales operations interface
- `Manager.java`: Analytics and reporting functionality

#### Data Directories
- `demo_data/`: Video demonstration datasets
- `Sample_data/`: Testing and verification data

### Compiled Files (`bin/`)
- `Main.class`
- `Administrator.class`
- `Salesperson.class`
- `Manager.class`

## Database Schema
The system manages five main tables:
- Category
- Manufacturer
- Part
- Salesperson
- Transaction

## Technical Requirements

### Java Environment
openjdk version "1.8.0_322"
OpenJDK Runtime Environment (build 1.8.0_322-b06)
OpenJDK 64-Bit Server VM (build 25.322-b06, mixed mode)

### Platform Compatibility
Primary development and testing performed on Linux systems. For optimal performance, Linux environment is recommended.

## Build Instructions

### Compilation
Navigate to project root:
cd ~/database/final_project
javac -cp .:lib/ojdbc8.jar -d bin src/*.java

### Execution
From project root:
java -cp bin:lib/ojdbc8.jar Main

## Features
1. Administrator Operations:
   - Database table creation and deletion
   - Bulk data import from files
   - Table content viewing
2. Salesperson Operations
3. Manager Operations

## Data File Format
The system accepts tab-separated text files for data import:
- category.txt
- manufacturer.txt
- part.txt
- salesperson.tx