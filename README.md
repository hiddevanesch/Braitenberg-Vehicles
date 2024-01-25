# Braitenberg Vehicles
A simulation of [Braitenberg vehicles](https://en.wikipedia.org/wiki/Braitenberg_vehicle) implemented in [OpenGL](https://www.opengl.org/) using [LWJGL](https://www.lwjgl.org/). 

> A  **Braitenberg vehicle**  is a concept conceived in a thought experiment by the Italian-Austrian  cyberneticist **Valentino Braitenberg**.  For the simplest vehicles modeled in his book, the motion of the vehicle is directly controlled by some sensors (for example photo cells). Yet the resulting behaviour may appear complex or even intelligent. 

In our case, we used OpenGL cameras (using FBOs) to model the sensors.

## Features
This Braitenberg vehicle simulation includes:
* Four different types of autonomous Braitenberg vehicles
  * Love
  * Fear
  * Hate
  * Curious
* Self-drivable `Controllable` vehicle (movable using <kbd>&uarr;</kbd>, <kbd>&darr;</kbd>, <kbd>&larr;</kbd> and <kbd>&rarr;</kbd>)
* Intuitive GUI made using [imgui-java](https://github.com/SpaiR/imgui-java)
* Dynamic lights
  * Static
  * Attachable (attaches to Braitenberg vehicles)
  * Controllable (movable using <kbd>w</kbd>, <kbd>a</kbd>, <kbd>s</kbd>, <kbd>d</kbd>, <kbd>q</kbd> and <kbd>e</kbd>)
* Different camera perspectives
  * Top down
  * Third person (movable using the mouse while holding middle mouse button)
 
## Demo Video
[![Watch on YouTube](https://img.youtube.com/vi/h5EYZifngp4/0.jpg)](https://www.youtube.com/watch?v=h5EYZifngp4)

## Acknowledgements
Special thanks to:
* @TheThinMatrix for his [YouTube playlist](https://www.youtube.com/watch?v=VS8wlS9hF8E&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP) on creating an OpenGL engine using LWJGL.
