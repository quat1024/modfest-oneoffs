# Modfest Oneoffs

Tiny mods slapped together for Modfest.

* **bobbyallowlist**: Allow specific blockentities to be loaded on the client in Bobby's fake chunks. (Bobby's own config file only lets you display "all block entities" or "no block entities".)
* **spectatorheads**: In spectator mode, removes the floating heads of other spectators.
* **quickfov**: Change FoV by holding a modifier key (default: Right Alt) and moving the mouse left and right.

## how it works

The gradle setup is a bit weird; one mod per *source set* instead of per subproject. This is for convenience (very easy to hack on a new project) and performance (boy I'm tired of multiproject setups). `./gradlew build` builds everything.
