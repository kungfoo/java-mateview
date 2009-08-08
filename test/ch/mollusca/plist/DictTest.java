package ch.mollusca.plist;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class DictTest {
	private Dict dict;

	@Before
	public void setup(){
		dict = new Dict("input/Ruby.plist");
		assertNotNull(dict);
	}
	
	@Test
	public void testSimpleStringItem(){
	}
}
