package additional;

import additional.splayTree.BTreeNode;
import additional.splayTree.SplayTreeAction;

public class UpdateKey implements SplayTreeAction {
	public void action(BTreeNode node, double y) {
		((Linebase) node.data()).setKeyValue(y);
	}
}
