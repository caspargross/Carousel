
class: middle, center

# A short introduction to JavaFX

---
# About JavaFX

- First release in 2008
- Current version: JavaFX 9.0 
- Intended to replace Swing as standard library for Java GUIs
- Both Swing and JavaFX are still included in current Java 
- In future (Sep. 2018): JavaFX will not be part of the standard JDK with further development by OpenJFX 

---
# Where to find help? 

- [Official Documentation](https://docs.oracle.com/javase/8/javase-clienttechnologies.htm)
- [API Document](https://docs.oracle.com/javase/8/javafx/api/overview-summary.html) with informations and usage of all classes and interfaces
- Independant online documentation [Tutorial Points](https://www.tutorialspoint.com/javafx/index.htm)
- Youtube Courses [thenewboston](https://www.youtube.com/watch?v=FLkOX4Eez6o)
- Lecture advanced java for bioinformatics (Wintersemester, Prof. Huson)

---
# Running a JavaFX Application:


---
# The scene graph
[doc](https://docs.oracle.com/javafx/2/scenegraph/jfxpub-scenegraph.htm)

* Tree data structure
* Contains all graphical objects in the application
* Items are called Nodes

.center[![alt text](img/scenegraph.png)]

---
# Properties

[Official Doc](https://docs.oracle.com/javafx/2/binding/jfxpub-binding.htm)

Limits of static, linear program flow
    - Methods need to be executed when objects are modified
    - Dynamic responses to user input (click button)
    - Update view when model changes

All this can be achieved with *Properties* and *Bindings*

---
# Nodes


---

# Shapes

[doc](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/shape/Shape.html)

* Arc
* Circle
* CubicCurve
* Ellipse
* Line
* Polygon
* Rectangle
* Text

Interesting Properties:
- stroke -> Defines colour of the border 
- fill -> Defines colour inside the shape

---

# Design patterns

Proposition for JavaFX: MVC **M**odel **V**iew **C**ontroller



---
# Model:

---
# View:

---
# Controller:


---
# FXML



---
# (Gluon) Scene Builder

- Can be integrated into IntelliJ


---







