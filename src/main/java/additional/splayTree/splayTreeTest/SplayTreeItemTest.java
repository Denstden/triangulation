package additional.splayTree.splayTreeTest;

import additional.splayTree.SplayTreeItem;

public class SplayTreeItemTest implements SplayTreeItem {
	
	public int data = 1;
	
	public SplayTreeItemTest(int a){
		data = a;
	}

	public void increaseKeyValue(double delta) {
		data += 1;
	}

	public Comparable keyValue() {
		return data;
	}

}
