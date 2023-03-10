package com.team2898.robot.commands

import com.team2898.robot.subsystems.Intake
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase

/**
 * Runs intake forwards or backwards to take a game peice or deposit it
 * @author Ori
 */
class ActivateIntake(private val mode: RunningIntakes) : CommandBase() {
    private val timer = Timer()

    override fun initialize() {
        if (mode == RunningIntakes.RUNINTAKE) {
            Intake.runIntake()
        }
        else if (mode == RunningIntakes.RUNOUTTAKE) {
            Intake.runOuttake()
        }
        timer.start()
    }

    override fun isFinished(): Boolean {
        return timer.hasElapsed(0.75)
    }

    override fun end(interrupted: Boolean) {
        Intake.stopIntake()
    }

    enum class RunningIntakes {
        RUNINTAKE,
        RUNOUTTAKE
    }
}