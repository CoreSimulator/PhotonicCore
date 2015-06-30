package edu.salisbury.basic_core_simulator;

import edu.salisbury.core_simulator.CoreNode;
import edu.salisbury.core_simulator.MRRSwitch;

public class BasicMRRSwitch extends MRRSwitch {
	
//	public MRRSwitch(CoreNode[] pathNodes) {
//	links = pathNodes;
//	updatePositionalLinks();
//  }
	
//	public BasicMRRSwitch(CoreNode topLeftNode) {
//		topLeftLink = topLeftNode;
//		updatePositionalLinks();
//		links = updateConnectedLinks();
//	}

	public BasicMRRSwitch(CoreNode topLeftNode, CoreNode topRightNode,
			CoreNode bottomRightNode, CoreNode bottomLeftNode) {
		topLeftLink = topLeftNode;
		topRightLink = topRightNode;
		bottomRightLink = bottomRightNode;
		bottomLeftLink = bottomLeftNode;
		links = updateConnectedLinks();
	}
	
	public CoreNode getDestinationNode(CoreNode sourceNode) {
		if (state == State.ON) {
			if (sourceNode == topLeftLink) {
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
		} else throw new RuntimeException("This source node does not connect to this switch.");
		return null; //sourceNode does not connect to this switch
	}
	
	//IF USING FIRST/SECOND CONSTRUCTOR, THEN NEED TO UPDATE WHICH CORE IS AT WHICH LINK
//	private void updatePositionalLinks() {
//		topRightLink = CoreNode[topLeftLink.BasicNode.nodeNumber + 1];
//		bottomLeftLink = CoreNode[topLeftLink.BasicNode.nodeNumber + 1]; 
//		bottomRightLink = CorrNode[numOfNodes - topLeftLink.BasicNode.nodeNumber];
//		bottomLeftLink = CoreNode[numOfNodes - topLeftLink.BasicNode.nodeNumber + 1];
//	}
}
