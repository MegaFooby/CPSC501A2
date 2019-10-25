import org.junit.*;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.util.*;

public class InspectorTest {
	
	Inspector inspector = null;
	
	@Before
	public void init() {
		inspector = new Inspector();
	}
	
	@Test
	public void format_name_class_bool() {
		Object test = new byte[5][5];
		String compare = inspector.format_class_name(test.getClass(), false);
		assertTrue(compare.equals("byte"));
		compare = inspector.format_class_name(test.getClass(), true);
		assertTrue(compare.equals("byte[][]"));
		
		test = new char[2];
		compare = inspector.format_class_name(test.getClass(), false);
		assertTrue(compare.equals("char"));
		compare = inspector.format_class_name(test.getClass(), true);
		assertTrue(compare.equals("char[]"));
		
		test = new double[7][7][7];
		compare = inspector.format_class_name(test.getClass(), false);
		assertTrue(compare.equals("double"));
		compare = inspector.format_class_name(test.getClass(), true);
		assertTrue(compare.equals("double[][][]"));
		
		test = new Object[4][4][4][4];
		compare = inspector.format_class_name(test.getClass(), false);
		assertTrue(compare.equals("java.lang.Object"));
		compare = inspector.format_class_name(test.getClass(), true);
		assertTrue(compare.equals("java.lang.Object[][][][]"));
	}
	
	@Test
	public void format_name_class() {
		Object test = new byte[5][5];
		String compare = inspector.format_class_name(test.getClass());
		assertTrue(compare.equals("byte[][]"));
		
		test = new char[2];
		compare = inspector.format_class_name(test.getClass());
		assertTrue(compare.equals("char[]"));
		
		test = new double[7][7][7];
		compare = inspector.format_class_name(test.getClass());
		assertTrue(compare.equals("double[][][]"));
		
		test = new Object[4][4][4][4];
		compare = inspector.format_class_name(test.getClass());
		assertTrue(compare.equals("java.lang.Object[][][][]"));
	}
	
	@After
	public void destroy() {
		inspector = null;
	}
}
