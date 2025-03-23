# Modfest Oneoffs

Tiny mods slapped together for Modfest.

* **bobbyallowlist**: Allow specific blockentities to be loaded on the client in Bobby's fake chunks. (Bobby's own config file only lets you display "all block entities" or "no block entities".)
* **spectatorheads**: In spectator mode, removes the floating heads of other spectators.

## how it works

The gradle setup is a bit weird; one mod per *source set* instead of per subproject. This is for convenience (very easy to hack on a new project) and performance (boy I'm tired of multiproject setups). `./gradlew build` builds everything.

The Shadow plugin is used to move `src/main/java/agency.highlysuspect.oneoffs.common` into each mod's jar at a unique location, meaning I don't need to worry about jar-in-jars or a "modfest oneoffs lib" containing the shared classes. Importantly, this means you need to be very careful about `static` in the `main` sourceset, since static things are shared across all  mods in the devenv, but in the distributed jar they all have unique copies.

And then to *actually* share a class across multiple mods i put it in `oneoffs.shared`. This is not shaded; each mod has the same class in the same location. Which means I need this class to be more stable since I don't know which jar will provide the class.