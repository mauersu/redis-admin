// BasicBSONCallback.java

/**
 *      Copyright (C) 2008 10gen Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.bson;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bson.types.BSONTimestamp;
import org.bson.types.BasicBSONList;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.CodeWScope;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;

public class BasicBSONCallback implements BSONCallback {
	private Object _root;
	private final LinkedList<BSONObject> _stack = new LinkedList<BSONObject>();
	private final LinkedList<String> _nameStack = new LinkedList<String>();

	public BasicBSONCallback() {
		reset();
	}

	public BSONObject create() {
		return new BasicBSONObject();
	}

	protected BSONObject createList() {
		return new BasicBSONList();
	}

	@Override
	public BSONCallback createBSONCallback() {
		return new BasicBSONCallback();
	}

	public BSONObject create(boolean array, List<String> path) {
		if (array) return createList();
		return create();
	}

	@Override
	public void objectStart() {
		if (_stack.size() > 0) throw new IllegalStateException("something is wrong");

		objectStart(false);
	}

	@Override
	public void objectStart(boolean array) {
		_root = create(array, null);
		_stack.add((BSONObject) _root);
	}

	@Override
	public void objectStart(String name) {
		objectStart(false, name);
	}

	public void objectStart(boolean array, String name) {
		_nameStack.addLast(name);
		final BSONObject o = create(array, _nameStack);
		_stack.getLast().put(name, o);
		_stack.addLast(o);
	}

	@Override
	public Object objectDone() {
		final BSONObject o = _stack.removeLast();
		if (_nameStack.size() > 0)
			_nameStack.removeLast();
		else if (_stack.size() > 0) throw new IllegalStateException("something is wrong");

		return !BSON.hasDecodeHooks() ? o : (BSONObject) BSON.applyDecodingHooks(o);
	}

	@Override
	public void arrayStart() {
		objectStart(true);
	}

	@Override
	public void arrayStart(String name) {
		objectStart(true, name);
	}

	@Override
	public Object arrayDone() {
		return objectDone();
	}

	@Override
	public void gotNull(String name) {
		cur().put(name, null);
	}

	@Override
	public void gotUndefined(String name) {
	}

	@Override
	public void gotMinKey(String name) {
		cur().put(name, new MinKey());
	}

	@Override
	public void gotMaxKey(String name) {
		cur().put(name, new MaxKey());
	}

	@Override
	public void gotBoolean(String name, boolean v) {
		_put(name, v);
	}

	@Override
	public void gotDouble(final String name, final double v) {
		_put(name, v);
	}

	@Override
	public void gotInt(final String name, final int v) {
		_put(name, v);
	}

	@Override
	public void gotLong(final String name, final long v) {
		_put(name, v);
	}

	@Override
	public void gotDate(String name, long millis) {
		_put(name, new Date(millis));
	}

	@Override
	public void gotRegex(String name, String pattern, String flags) {
		_put(name, Pattern.compile(pattern, BSON.regexFlags(flags)));
	}

	@Override
	public void gotString(final String name, final String v) {
		_put(name, v);
	}

	@Override
	public void gotSymbol(String name, String v) {
		_put(name, v);
	}

	@Override
	public void gotTimestamp(String name, int time, int inc) {
		_put(name, new BSONTimestamp(time, inc));
	}

	@Override
	public void gotObjectId(String name, ObjectId id) {
		_put(name, id);
	}

	@Override
	public void gotDBRef(String name, String ns, ObjectId id) {
		_put(name, new BasicBSONObject("$ns", ns).append("$id", id));
	}

	@Override
	@Deprecated
	public void gotBinaryArray(String name, byte[] data) {
		gotBinary(name, BSON.B_GENERAL, data);
	}

	@Override
	public void gotBinary(String name, byte type, byte[] data) {
		if (type == BSON.B_GENERAL || type == BSON.B_BINARY)
			_put(name, data);
		else
			_put(name, new Binary(type, data));
	}

	@Override
	public void gotUUID(String name, long part1, long part2) {
		_put(name, new UUID(part1, part2));
	}

	@Override
	public void gotCode(String name, String code) {
		_put(name, new Code(code));
	}

	@Override
	public void gotCodeWScope(String name, String code, Object scope) {
		_put(name, new CodeWScope(code, (BSONObject) scope));
	}

	protected void _put(final String name, final Object o) {
		cur().put(name, !BSON.hasDecodeHooks() ? o : BSON.applyDecodingHooks(o));
	}

	protected BSONObject cur() {
		return _stack.getLast();
	}

	protected String curName() {
		return (!_nameStack.isEmpty()) ? _nameStack.getLast() : null;
	}

	@Override
	public Object get() {
		return _root;
	}

	protected void setRoot(Object o) {
		_root = o;
	}

	protected boolean isStackEmpty() {
		return _stack.size() < 1;
	}

	@Override
	public void reset() {
		_root = null;
		_stack.clear();
		_nameStack.clear();
	}

}
