/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team2898.robot.commands

import com.team2898.robot.subsystems.ExampleSubsystem
import edu.wpi.first.wpilibj2.command.CommandBase

class ExampleCommand(val m_subsystem: ExampleSubsystem) : CommandBase() {
  /**
   * Creates a new ExampleCommand.
   *
   * @param m_subsystem The subsystem used by this command.
   */
  init {
    addRequirements(m_subsystem)
  }

  // Called when the command is initially scheduled.
  override fun initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  override fun execute() {
  }

  // Called once the command ends or is interrupted.
  override fun end(interrupted: Boolean) {
  }

  // Returns true when the command should end.
  override fun isFinished(): Boolean {
    return false
  }
}
