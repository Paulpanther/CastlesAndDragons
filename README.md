# CastlesAndDragons

Online Game to play with your friends.

### Install

#### Server

1. Go into `server/`
2. Run `./gradlew shadowJar`
3. Start the Jar with: `java -jar build/libs/<name-of-jar>.jar`

The following args can be set:
- `--port <int>`: The port to run at
- `--start-game-delay <int>`: The Delay in the Waiting Room before a Game starts
- `--show-grid-delay <int>`: The Delay in Game before the Task is shown
- `--player-count <int>`: The Amount of Players in each Game
- `--grid-width <int>`: The Width of the Grid
- `--grid-height <int>`: The Height of the Grid
