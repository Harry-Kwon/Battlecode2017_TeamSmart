package cleanplayer;

import battlecode.common.*;

public class UtilTrees {
	
	public static TreeInfo findLowestTree(TreeInfo[] trees) {
		if(trees==null) {
			return null;
		}
		
		TreeInfo lowestTree = trees[0];
		for(TreeInfo ti : trees){ 
			if(ti.health < lowestTree.health ) {
				lowestTree = ti;
			}
		}
		
		return lowestTree;
	}
}
