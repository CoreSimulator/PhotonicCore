package edu.salisbury.photonic.core_simulator;

public abstract class MRRSwitch {
	public enum State {ON, OFF}
	public enum Status {FREE, BUSY}
	public CoreNode topLeftLink = null;
	public CoreNode topRightLink = null;
	public CoreNode bottomRightLink = null;
	public CoreNode bottomLeftLink = null;
	public int mrrSwitchNumber;
	public State state = State.ON;
	public Status status = Status.FREE;
	public CoreNode[] links = new CoreNode[4];
	
	public abstract CoreNode getDestinationNode(CoreNode sourceNode);
	
	public CoreNode[] updateConnectedLinks() {
		//clockwise orientation
		links[0] = topLeftLink;
		links[1] = topRightLink;
		links[2] = bottomRightLink;
		links[3] = bottomLeftLink;
		return links;
	}

	public Status checkStatus() {
		return status;
	}
	
	public State checkState() {
		return state;
	}
	
	public CoreNode[] getEdges() {
		return links;
	}

	public void switchOn() {
		state = State.ON;
	}
	
	public void switchOff() {
		state = State.OFF;
	}
	
	public void setStatusFree() {
		 status = Status.FREE;
	}
	
	public void setStatusBusy() {
		 status = Status.BUSY;
	}
}
