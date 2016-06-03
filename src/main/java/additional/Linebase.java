package additional;

import additional.splayTree.SplayTreeItem;

public class Linebase implements SplayTreeItem {

	protected int _id = -1;

	protected Pointbase[] _endp = {null, null};

	protected int _type = TriUtils.UNKNOWN;

	protected double _key = 0;

	protected int _helper = -1;

	public Linebase() {
		for (int i = 0; i < 2; i++) _endp[i] = null;
		_id = 0;
	}

	public Linebase(Pointbase ep1, Pointbase ep2, int iType) {
		_endp[0] = ep1;
		_endp[1] = ep2;
		_id = (int) ++TriUtils.l_id;
		_type = iType;
	}

	public Linebase(Linebase line) {
		this._id = line._id;
		this._endp[0] = line._endp[0];
		this._endp[1] = line._endp[1];
		this._key = line._key;
		this._helper = line._helper;
	}

	public int id() {
		return _id;
	}

	public Pointbase endPoint(int i) {
		return _endp[i];
	}

	public int type() {
		return _type;
	}

	public Comparable keyValue() {
		return _key;
	}

	public void setKeyValue(double y) {
		if (_endp[1].y == _endp[0].y)
			_key = _endp[0].x < _endp[1].x ? _endp[0].x : _endp[1].x;
		else
			_key = (y - _endp[0].y) * (_endp[1].x - _endp[0].x) / (_endp[1].y - _endp[0].y) + _endp[0].x;
	}

	public void reverse() {
		assert (_type == TriUtils.INSERT);
		Pointbase tmp = _endp[0];
		_endp[0] = _endp[1];
		_endp[1] = tmp;
	}

	public void setHelper(int i) {
		_helper = i;
	}

	public int helper() {
		return _helper;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Linebase(");
		sb.append("ID = " + _id);
		sb.append(", " + TriUtils.typeToString(_type));
		sb.append(", [");
		sb.append(_endp[0]);
		sb.append(", ");
		sb.append(_endp[1]);
		sb.append("], type = " + _type);
		sb.append(", keyValue =" + keyValue());
		return sb.toString();
	}

	public void increaseKeyValue(double delta) {
		_key += delta;
	}

}
