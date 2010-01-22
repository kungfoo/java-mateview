package com.redcareditor.plist.parser;

/**
 * encapsulates a {@link java.util.Stack} but does not throw exceptions when
 * empty.<br>
 * Also has a _proper_ stack interface.
 */
public class Stack<T> {
	private java.util.Stack<T> stack = new java.util.Stack<T>();

	public T peek() {
		if (stack.isEmpty()) {
			return null;
		} else {
			return stack.peek();
		}
	}

	public void push(T t) {
		stack.push(t);
	}

	public T pop(T t) {
		if (stack.isEmpty()) {
			return null;
		} else {
			return stack.pop();
		}
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public String toString() {
		return stack.toString();
	}
}
