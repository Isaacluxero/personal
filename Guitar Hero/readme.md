# Guitar Hero
## Introduction
In this project I aimed to create a version of the popular game Guitar Hero. The two biggest challenges I faced were first finding the correct and most useful data structures that would make implementing the various methods in the best way. The second challenge was in implementing new technologies that I had never worked with before and quickly adapting. 

## Deque
Upon looking at how sounds are played and the overall functions of Guitar Hero I came to the conclusion that a sound synthesizer was going to need to be implemented. The best way to do this was through a double ended queue. In my overall academic career data structures and how to implement has always been a foundation to build on so I challenged myself and designed my own deque structure to better understand it. I did this utilizing both

## Sound Synthesizer
The next most important part of this project was the sound synthesizer. In order to do this I had to use the Karplus-Strong string synthesis which emulates the sound of plucked strings in digital music. It involves creating a delay line representing the virtual string, filling it with noise to simulate the initial disturbance, and implementing a feedback loop to mimic the decay and sustain. A low-pass filter is then applied for natural decay. By adjusting parameters, such as the delay line length, feedback gain, and filter settings, various string-like sounds can be generated. This technique is valued for its simplicity and effectiveness in simulating the characteristics of plucked strings.

## Conclusion
Throughout this project I grew a deeper understanding for how data structures work and why each one is beneficial for different circumstances. I also came to realize through this project along with other experiences I have had that the most important aspects of programming is have the foundational basics down and being able to learn different echnologies and theories rather quickly utilizing those basics. For this specific instance I had never worked with a sound synthesizer, but I was able to adapt and use my prior knowledge to implement it.

