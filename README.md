# Easy Grapher

**Easy Grapher** is a JavaFX-based desktop application designed to help students visualize and analyze mathematical
functions. It allows users to plot functions, view derivatives, and manipulate graphs in an interactive environment.

## Key Features

* **Multi-Function Plotting:** Input and display up to 2 functions simultaneously.
* **Derivative Analysis:** Automatically calculate and display the derivative of any plotted function using *Math
  Eclipse*'s **Symja** library.
* **Interactive Graph Navigation:** Zoom, pan, and scroll to explore the graph.
* **Point Inspection:** Click anywhere on a curve to reveal the exact coordinates of that point.
* **Intersection & Roots:** Visualize intersection points and roots directly on the graph.
* **Customization:** Change line colors to easily distinguish between functions.
* **Virtual Keyboard:** A built-in on-screen keyboard for easy input of complex mathematical symbols.
* **Optimized Performance:** Includes input debouncing to ensure smooth performance while typing functions.

## How to Run

This is an **IntelliJ Maven** project using **JavaFX**. All necessary dependencies (including **Lombok** and **Math
Eclipse**) are managed via the `pom.xml` file.

### Prerequisites

* JDK 17 or higher (JavaFX compatible)
* IntelliJ IDEA

### Installation & Execution

1. **Clone the repository into desired folder:**
   ```bash
   git clone https://github.com/nepallium/Easy-Grapher.git
   ```
2. **Open the project:**
    * Open IntelliJ IDEA.
    * Select `File` > `Open` and select the project folder.
3. **Load Dependencies:**
    * IntelliJ should automatically detect the Maven project. If not, right-click `pom.xml` and select `Maven` >
      `Sync Project`.
    * Ensure the **Lombok** plugin is installed by going to `Settings` > `Plugins` > `Lombok` > `Install`.
4. **Run configurations**
    * Navigate to `Run` > `Edit confiurations` > `Modify options`
    * Check `Modify classpath`
    * Then under `Modify classpath`, include the following paths:
        1. `%UserProfile%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.15.3\` >
           `jackson-annotations-2.15.3.jar`

        2. `%UserProfile%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.15.3\` >
           `jackson-core-2.15.3.jar`
        3. `%UserProfile%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.15.3\` >
           `jackson-databind-2.15.3.jar`

> [!WARNING]
> Without including these three paths the derivative calculator will break.

5. **Run:**
    * Locate the main application class in `src/main/java/Main/App.java`.
    * Click the green **Run** button or press `Shift + F10`.

## Teamwork Summary

The development of Easy Grapher was divided between two team members, with specific responsibilities for the Model,
View, and Controller components.

### **Alex Huang**

* **Model Logic:** Handled function parsing and evaluation.
* **UI Construction:** Built the primary FXML layout and the on-screen keyboard.
* **Interactive Features:** Implemented the logic for zooming, panning, and scrolling the graph.

### **Sebastian Bobos**

* **Graph Rendering:** Developed the core logic for drawing graphs and visual elements.
* **Controller Logic:** Managed input handling and graph color changes.
* **Advanced Analysis:** Implemented logic for calculating and displaying intersection points and clicking curves to see
  coordinates.