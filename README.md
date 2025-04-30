# Modfest Oneoffs

Tiny mods slapped together for BlanketCon '25.

* **bobbyallowlist**: Allow specific blockentities to be loaded on the client in Bobby's fake chunks.
  * Bobby's own config file only lets you display "all block entities" or "no block entities".
  * For example, you could use this to show block entities from Templates in fake chunks, because without the block entity data they don't render correctly.
  * Add more with `/bobbyallowlist pos` (to allowlist a specific block entity) or `/bobbyallowlist type` (to allowlist an entire BlockEntityType). If you don't pass the position/type as an argument, it will use the position/type of the block entity under the cursor. 
  * The config file is `bobby-block-entity-allowlist.conf` (so it'll sort next to Bobby's alphabetically).
* **hideservermessages**: Only show chat messages that aren't "server" ones (command feedback, joins/leaves, etc).
  * Defaults to doing nothing. Toggle with `/hideservermessages`.
  * The config file is `hideservermessages.properties`.
* **quickfov**: Change FoV by holding a modifier key and moving the mouse left and right.
  * If you want up/down or if you want to reverse the zoom direction, see the config file.
  * The config file is `quickfov.properties`.
* **spectatorheads**: In spectator mode, removes the floating heads of other spectators.
  * Defaults to on. Toggle with `/spectatorheads`.
  * The config file is `spectatorheads.properties`.

## installation

The oneoffs are distributed in two ways: as separate mods, or as an "amalgam" which contains them all (kind of like `fabric-api`). To install the amalgam, simply install it like any other mod. To install them separately, make sure you get `oneoffs-lib`.

If you have the amalgam and want separate jars, double-click it (or run it with `java -jar modfest-oneoffs.jar`).

Either way: they all depend on `fabric-api`.

## how it works

The gradle setup is a bit weird; one mod per *source set* instead of per subproject. This is for convenience (very easy to hack on a new project) and performance (boy I'm tired of multiproject setups). `./gradlew build` builds everything.

### adding a new project

remember to add the source-set and stuff, and add to the source-set loop towards the bottom

and remember to add to "`amalgam`"'s `fabric.mod.json` contained-jars block. not sure how to do that automatically from gradle...

there are some warnings about `Failed to find mixin mappings mixin-map-loom.mappings.1_21_1.layered+hash.2198-v2.amalgam.tiny in task outputs: task 'compileAmalgamJava' output files`; it's fine