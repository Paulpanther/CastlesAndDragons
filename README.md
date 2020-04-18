# CastlesAndDragons

Online Game to play with your friends.

### Debugging

To test new assets, place them in `src/public/app/assets`.
Run the WebSocket Server with: `./gradlew runServer --args="--debugStartWithGame"`
Run the DevServer with `npm start --prefix src/public`
**Warning:** Wait 2 minutes before restarting the Websocket Server 
(There is a bug which prevents the port from getting closed)
