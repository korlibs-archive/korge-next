package components

data class Spawner(
    // config
    var numberOfObjects: Int = 1,
    var interval: Int = 0,  // 0 - disabled, 1 - every frame, 2 - every second frame, 3 - every third frame,...
    var timeVariation: Int = 0,  // 0 - no variation, 1 - one frame variation, 2 - two frames variation, ...
    var xPosVariation: Double = 0.0,
    var yPosVariation: Double = 0.0,
    var xAccel: Double = 0.0,
    var yAccel: Double = 0.0,
    // internal state
    var nextSpawnIn: Int = 0
)
