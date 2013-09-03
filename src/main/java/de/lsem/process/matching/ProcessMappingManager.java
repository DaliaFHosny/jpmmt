package de.lsem.process.matching;

import de.lsem.matrix.Match;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class ProcessMappingManager {
	public void addNodeMatch(Match<ProcessNode> match, ProcessMapping processMapping) {
		FragmentMatch fragmentMatch1 = null;
		FragmentMatch fragmentMatch2 = null;
		
		for (FragmentMatch fragmentMatch : processMapping.getFragmentMatch()) {
			if (fragmentMatch.getFragment1().containsProcessNode(match.getObject1())) {
				fragmentMatch1 = fragmentMatch;
			}			
			if (fragmentMatch.getFragment2().containsProcessNode(match.getObject2())) {
				fragmentMatch2 = fragmentMatch;
			}		
		}
		
		if (fragmentMatch1 == null && fragmentMatch2 == null) {
			this.createNewFragmentMatch(match, processMapping);
		}
		else if (fragmentMatch1 == null && fragmentMatch2 != null) {
			this.addNodeToFragment(match.getObject1(), fragmentMatch2.getFragment1());
		}
		else if (fragmentMatch1 != null && fragmentMatch2 == null) {
			this.addNodeToFragment(match.getObject2(), fragmentMatch1.getFragment2());
		}
		else if (fragmentMatch1 == fragmentMatch2) {
			this.addNodeToFragment(match.getObject1(), fragmentMatch2.getFragment1());
			this.addNodeToFragment(match.getObject2(), fragmentMatch1.getFragment2());
		}		
		else {
			FragmentMatch mergedFragmentMatch = this.mergeFragmentMatches(fragmentMatch1, fragmentMatch2, processMapping);
			this.addNodeToFragment(match.getObject1(), mergedFragmentMatch.getFragment1());
			this.addNodeToFragment(match.getObject2(), mergedFragmentMatch.getFragment2());
		}
	}

	private FragmentMatch mergeFragmentMatches(FragmentMatch fragmentMatch1, FragmentMatch fragmentMatch2, ProcessMapping processMapping) {
		processMapping.removeFragmentMatch(fragmentMatch1);
		processMapping.removeFragmentMatch(fragmentMatch2);
				
		Fragment fragment1 = new Fragment();
		this.moveNodes(fragmentMatch1.getFragment1(), fragment1);
		this.moveNodes(fragmentMatch2.getFragment1(), fragment1);
		
		Fragment fragment2 = new Fragment();
		this.moveNodes(fragmentMatch1.getFragment2(), fragment2);
		this.moveNodes(fragmentMatch2.getFragment2(), fragment2);
		
		FragmentMatch fragmentMatch = new FragmentMatch(fragment1, fragment2);				
		processMapping.addFragmentMatch(fragmentMatch);
		
		return fragmentMatch;
	}
	
	private void moveNodes(Fragment fromFragment, Fragment toFragment) {
		for (ProcessNode node : fromFragment.getProcessNodes()) {
			toFragment.addProcessNode(node);
		}
	}

	private void addNodeToFragment(ProcessNode processNode, Fragment fragment) {
		if (!fragment.containsProcessNode(processNode)) {
			fragment.addProcessNode(processNode);
		}
	}

	private void createNewFragmentMatch(Match<ProcessNode> match, ProcessMapping processMapping) {
		Fragment fragment1 = new Fragment();
		fragment1.addProcessNode(match.getObject1());
		
		Fragment fragment2 = new Fragment();
		fragment2.addProcessNode(match.getObject2());
		
		processMapping.addFragmentMatch(new FragmentMatch(fragment1, fragment2));		
	}
}
