# CastlesAndDragons

Online Game to play with your friends.

### Install for Production

Run `./deploy.sh`.
It will start two Docker images:
- `castlesanddragons-server`: running on port 5678
- `castlesanddragons-static`: running on port 80 (nginx server)

### Install for Development

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

#### Static

##### Build
1. Go into `static/`
2. Run `npm i`
3. Run `npm run build` (Build files are in `dist/`)

##### Run
You can run a dev server with `npm start`

##### Settings
Environment arguments can be set by creating a file named `.env` containing `key=value` pairs.   
The following args can be set:
- `SERVER`: Url of the websocket server. Example: `localhost:6789`

##### Release
A release `dist.tar.gz` can be created by running `npm run release`