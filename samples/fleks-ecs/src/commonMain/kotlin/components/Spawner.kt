package components

data class Spawner(
    // config
    var numberOfObjects: Int = 1,
    var interval: Int = 0,  // 0 - disabled, 1 - every frame, 2 - every second frame, 3 - every third frame,...
    var timeVariation: Int = 0,  // 0 - no variation, 1 - one frame variation, 2 - two frames variation, ...
    //
    var spawnerNumberOfObjects: Int = 0,  // 0 - Disable spawning feature for spawned object
    var spawnerInterval: Int = 0,
    var spawnerTimeVariation: Int = 0, 
    //
    var positionX: Double = 0.0,
    var positionY: Double = 0.0,
    var positionVariationX: Double = 0.0,
    var positionVariationY: Double = 0.0,
    var positionAccelerationX: Double = 0.0,
    var positionAccelerationY: Double = 0.0,
    //
    var spriteImageData: String = "",  // "" - Disable sprite graphic for spawned object
    var spriteAnimation: String = "",
    var spriteIsPlaying: Boolean = false,
    var spriteForwardDirection: Boolean = true,
    var spriteLoop: Boolean = false,
    // internal state
    var nextSpawnIn: Int = 0
)
