package com.team2898.robot

import com.bpsrobotics.engine.utils.DistanceUnit
import com.bpsrobotics.engine.utils.geometry.*
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.SubsystemBase
import com.bpsrobotics.engine.utils.ft
import edu.wpi.first.math.geometry.Pose2d

enum class ScoringType {
    Cone, Cube
}
class ScoreSpot(
        val RobotPosition: Coordinate,
        val type: ScoringType
        ){
    fun distance(pose: Pose2d): Double{
        return RobotPosition.ydistance(pose)
    }
    fun reflectHorizontally(x: DistanceUnit): ScoreSpot{
        return ScoreSpot(RobotPosition.reflectHorizontally(x), type)
    }
}

class Grid(
        val Cone1: ScoreSpot,
        val Cube: ScoreSpot,
        val Cone2: ScoreSpot
){
    val scoringSpots = arrayOfNulls<ScoreSpot>(3)
    init {
        scoringSpots[0] = Cone1
        scoringSpots[1] = Cube
        scoringSpots[2] = Cone2
    }
    fun distance(pose: Pose2d): Double{
        return Cube.RobotPosition.ydistance(pose)
    }
    fun getClosestSpot(pose: Pose2d): ScoreSpot{
        var closestSpot = Cone1
        scoringSpots.forEach {
            it ?: run { return@forEach }
            if (it.distance(pose) < closestSpot.distance(pose)) { closestSpot = it }
        }
        return closestSpot
    }
    fun reflectHorizontally(x: DistanceUnit): Grid{
        return Grid(
                Cone1.reflectHorizontally(x),
                Cube.reflectHorizontally(x),
                Cone2.reflectHorizontally(x)
        )
    }
}

/**
 * Used to store the locations the robot needs to be in to score game pieces
 */
class ScoringLocations(
        val Grid1: Grid,
        val Grid2: Grid,
        val Grid3: Grid
){
    val grids = arrayOfNulls<Grid>(3)
    init {
        grids[0] = Grid1
        grids[1] = Grid2
        grids[2] = Grid3
    }
    fun getClosestGrid(pose: Pose2d): Grid{
        var closestGrid = Grid1
        grids.forEach {
            it ?: run { return@forEach }
            if (it.distance(pose) < closestGrid.distance(pose)) { closestGrid = it }
        }
        return closestGrid
    }
    fun getClosestScoringSpot(pose: Pose2d): ScoreSpot {
        return getClosestGrid(pose).getClosestSpot(pose)
    }
    fun reflectHorizontally(x: DistanceUnit): ScoringLocations{
        return ScoringLocations(
                Grid1.reflectHorizontally(x),
                Grid2.reflectHorizontally(x),
                Grid3.reflectHorizontally(x)
        )
    }
}
val robot_length = 0.93
val robot_scoring_pos = (4.5+ 1.526).ft
val blueScoring = ScoringLocations(
        Grid(
                ScoreSpot(Coordinate(robot_scoring_pos, 1.682.ft), ScoringType.Cone),
                ScoreSpot(Coordinate(robot_scoring_pos, 3.518.ft), ScoringType.Cube),
                ScoreSpot(Coordinate(robot_scoring_pos, 5.349.ft), ScoringType.Cone)
        ),
        Grid(
                ScoreSpot(Coordinate(robot_scoring_pos, 7.182.ft), ScoringType.Cone),
                ScoreSpot(Coordinate(robot_scoring_pos, 9.018.ft), ScoringType.Cube),
                ScoreSpot(Coordinate(robot_scoring_pos, 10.849.ft), ScoringType.Cone)
        ),
        Grid(
                ScoreSpot(Coordinate(robot_scoring_pos, 12.682.ft), ScoringType.Cone),
                ScoreSpot(Coordinate(robot_scoring_pos, 14.518.ft), ScoringType.Cube),
                ScoreSpot(Coordinate(robot_scoring_pos, 16.349.ft), ScoringType.Cone)
        )
)
/**
 * Used to store Alliance-specific locations
 * @property community The community zone on the field, stored as a Polygon
 * @property chargingDock The alliance's charging dock, stored as a Rectangle
 * @property loadingBay The alliance's loading bay, stored as a Polygon
 * @property gridWall The line separating the scoring grid from the community zone.
 * */
class Map(val community : Polygon,
          val chargingDock : Rectangle,
          val loadingBay : Polygon,
          val gridWall : Line,
        val scoring: ScoringLocations
        ) {
    fun get_location(position: Coordinate): String{
        return when {
            community.contains(position)    -> "community"
            chargingDock.contains(position) -> "charging dock"
            loadingBay.contains(position)   -> "loading bay"
            else                            -> "other"
        }
    }
}

private val redLoadingBay = Polygon(
    Coordinate(0.0.ft, 18.0.ft),
    Coordinate(11.0.ft, 18.0.ft),
    Coordinate(11.0.ft, 22.048.ft),
    Coordinate(22.0.ft, 22.048.ft),
    Coordinate(22.0.ft, 26.25.ft),
    Coordinate(0.0.ft, 26.25.ft),
)
private val blueLoadingBay = Polygon(
    Coordinate(4.5.ft,0.0.ft),
    Coordinate(4.5.ft,18.0.ft),
    Coordinate(11.0.ft,18.0.ft),
    Coordinate(11.0.ft,13.22.ft),
    Coordinate(16.07.ft,13.22.ft),
    Coordinate(16.07.ft, 0.0.ft)
)
private val blueChargeStation = Rectangle(
    Coordinate(9.56.ft,13.05.ft),
    Coordinate(15.9.ft,4.95.ft)
)
private val blueGridWall = Line(
    Coordinate(4.5.ft,0.0.ft),
    Coordinate(4.5.ft,18.0.ft),
    )

/** Map of the blue alliance side
 * @see Field
 * */
val blueTeam = Map(
        community = blueLoadingBay,
        chargingDock = blueChargeStation,
        loadingBay = redLoadingBay.reflectHorizontally(27.ft),
        gridWall = blueGridWall,
        scoring = blueScoring
)
/** Map of the red alliance side
 * @see Field
 * */
val redTeam = Map(
        community = blueLoadingBay.reflectHorizontally(27.ft),
        chargingDock = blueChargeStation.reflectHorizontally(27.ft),
        loadingBay = redLoadingBay,
        gridWall = blueGridWall.reflectHorizontally(27.ft),
        scoring =  blueScoring.reflectHorizontally(27.ft)
)
/** Map of the feild based off of the Driverstation alliance
 * @author Ozy King
 * @property map Includes your alliances specific zones
 * @property red_team Red alliance specific zones
 * @property blue_team Blue alliance specific zones
 * @author Ozy King
 * */
object Field : SubsystemBase() {
    private var teamColor: DriverStation.Alliance = DriverStation.getAlliance()
    lateinit var map : Map
    val red_team = redTeam
    val blue_team = blueTeam
    fun initialize() {
        map = if (teamColor == DriverStation.Alliance.Blue) {
            blueTeam
        } else {
            redTeam
        }
    }

    /**
     * Debugging tool
     * @param position The robots position
     * @return A string correlating to the robots position on the field
     * @author Ozy King
     */
    fun get_area(position: Coordinate): String{
        val red = red_team.get_location(position)
        if (red != "other") return "Red " + red
        val blue = blue_team.get_location(position)
        if (blue != "other") return "Blue " + blue
        return "Other"
    }
}
