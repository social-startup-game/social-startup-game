# The Social Startup Game
This is a strategy game about managing workers in a fictional social media startup. These are the key features:
* Authentic cybersecurity language
* Exposes educational paths toward careers in cybersecurity

The source code is released under [GPL 3](http://www.gnu.org/licenses/gpl-3.0.en.html) and
original assets under [CC BY-NC-SA 4.0](http://creativecommons.org/licenses/by-nc-sa/4.0/).
Soundtrack by Kevin MacLeod used under the license [included in the music folder](https://github.com/doctor-g/social-startup-game/blob/master/assets/src/main/resources/assets/music/license.txt).

## Building

*The Social Startup Game* currently depends on a snapshot release of [PlayN](http://playn.io);
once 2.0-rc3 is released, we expect to switch to that.
In the meantime, you will need to install the snapshot locally following [the instructions on the PlayN GitHub page](https://github.com/playn/playn).
That is, you will need to do the following:
```
git clone https://github.com/playn/playn.git
cd playn
mvn clean install
```
Then, because this project's pom file uses a playn-version of 2.0-SNAPSHOT, the build process will use that version
of the library to build the game.