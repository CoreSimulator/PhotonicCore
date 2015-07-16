package edu.salisbury.photonic.cyclical_core_simulator;

import edu.salisbury.photonic.core_simulator.CoreNode;
import edu.salisbury.photonic.core_simulator.MRRSwitch;

public class CyclicalMRRSwitch extends MRRSwitch {
	
	public CyclicalMRRSwitch(CyclicalNode topLeftNode, int mrrSwitchNumber) {
		topLeftLink = topLeftNode;
		this.mrrSwitchNumber = mrrSwitchNumber;
		links = updateConnectedLinks();
	}
	
	public CyclicalMRRSwitch(CyclicalNode topLeftNode, CyclicalNode topRightNode,
			CyclicalNode bottomRightNode, CyclicalNode bottomLeftNode, int mrrSwitchNumber) {
		topLeftLink = topLeftNode;
		topRightLink = topRightNode;
		bottomRightLink = bottomRightNode;
		bottomLeftLink = bottomLeftNode;
		links = updateConnectedLinks();
		this.mrrSwitchNumber = mrrSwitchNumber;
	}
	
	public CoreNode getDestinationNode(CoreNode sourceNode) {
		if (state == State.ON) {
			if (sourceNode == topLeftLink) 
			{
				return topRightLink;
			} else if (sourceNode == topRightLink) {
				return topLeftLink;
			} else if (sourceNode == bottomRightLink) {
				return bottomLeftLink;
			} else if (sourceNode == bottomLeftLink) {
				return bottomRightLink;
			}
		} else if(state == State.OFF){//status = SwitchStatus.OFF
			if (sourceNode == topLeftLink) {
				return bottomRightLink;
			} else if (sourceNode == topRightLink) {
				return bottomLeftLink;
			} else if (sourceNode == bottomRightLink) {
				return topLeftLink;
			} else if (sourceNode == bottomLeftLink) {
				return topRightLink;
			}
		}  
		throw new RuntimeException("This source node does not connect to this switch.");
	}
	
	public int getMRRSwitchNumber() {
		return mrrSwitchNumber;
	}
	
	public void setTopRightLink(CyclicalNode topRightNode) {
		topRightLink = topRightNode;
	}
	
	public void setBottomRightLink(CyclicalNode bottomRightNode) {
		bottomRightLink = bottomRightNode;
	}
	
	public void setBottomLeftLink(CyclicalNode bottomLeftNode) {
		bottomLeftLink = bottomLeftNode;
	}
	
}
