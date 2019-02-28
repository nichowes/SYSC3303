package src.main.scheduler.states;

import src.main.scheduler.StateMachine;
import src.main.settings.Settings;

/**
 * State indicating elevator motor is running, and continues until a destination floor is reached and signal is received.
 * 
 * @author austinjturner
 */
public class MotorStartedState extends State {
	
	/**
	 * Constructor for the MotorStartedState.
	 * 
	 * @param stateMachine StateMachine object used to manipulate current state of elevator.
	 */
	public MotorStartedState(StateMachine stateMachine) {
		super(stateMachine);
		
		/*
		 * We start this timer to ensure that an error in the elevator's movement
		 * can be detected
		 */
		startFloorWaitTimer();
	}

	
	private void startFloorWaitTimer() {
		new FloorWaitTimer(this.stateMachine, Settings.MAX_TIME_BEFORE_DOOR_FAULT, 
				this.stateMachine.currentFloor);
	}
	
	
	private void shutdownElevator() {
		this.stateMachine.schedulerSubsystem.sendShutdownElevatorMessage(
				this.stateMachine.elevatorID);
	}
	
	
	/**
	 * This state only moves to MotorStoppedState once it receives
	 * a signal from the elevator that it has reached the correct floor.
	 * 
	 * @return State Returns next state for the elevator.
	 */
	@Override
	public State elevatorReachedFloorEvent() {
		int targetFloor = this.stateMachine.getQueueFront().floorNum;
		
		if (this.stateMachine.currentFloor == targetFloor) {
			this.stateMachine.schedulerSubsystem.sendMotorStopMessage(
					this.stateMachine.elevatorID);
			return new MotorStoppedState(this.stateMachine);
		} else {
			return this;
		}
	}
	
	
	/**
	 * If the timer goes off and the floor has not changed, then
	 * we have detected an error
	 */
	@Override
	public State floorTimerEvent(int previousFloor) {

		if (previousFloor == this.stateMachine.currentFloor) {
			/*
			 * FAULT DETECTED
			 * Moving to FailedState
			 */
			shutdownElevator();
			//this.stateMachine.setFault("Missed expected floor arrival");
			return new FailedState(this.stateMachine);
		}
		
		return this;  // If no fault, we can ignore this event
	}
}
