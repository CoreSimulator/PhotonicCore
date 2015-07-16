package edu.salisbury.photonic.cyclical_core_simulator;

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
	
	public CyclicalNode getDestinationNode(CyclicalNode sourceNode) {
		if (state == State.ON) {
			if (sourceNode == topLeftLink) 
			{
				return (CyclicalNode) topRightLink;
			} else if (sourceNode == topRightLink) {
				return (CyclicalNode) topLeftLink;
			} else if (sourceNode == bottomRightLink) {
				return (CyclicalNode) bottomLeftLink;
			} else if (sourceNode == bottomLeftLink) {
				return (CyclicalNode) bottomRightLink;
			}
		} else if(state == State.OFF){//status = SwitchStatus.OFF
			if (sourceNode == topLeftLink) {
				return (CyclicalNode) bottomRightLink;
			} else if (sourceNode == topRightLink) {
				return (CyclicalNode) bottomLeftLink;
			} else if (sourceNode == bottomRightLink) {
				return (CyclicalNode) topLeftLink;
			} else if (sourceNode == bottomLeftLink) {
				return (CyclicalNode) topRightLink;
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
	
	public boolean isThisNodeConnected(CyclicalNode node) {
		return topRightLink == node || topLeftLink == node || bottomRightLink == node || bottomLeftLink == node;
	}
	
}
