
import java.lang.reflect.*;

public class OtherDriver {
	public static void main(String[] args) {
		if(args.length != 3) {
			System.out.print("Usage: java OtherDriver [inspector class] [class to inspect] [recursive]\n");
			System.exit(0);
		}
		
		Class inspect_class = null;
		try {
			inspect_class = Class.forName(args[0]);
		} catch(ClassNotFoundException e) {
			System.out.print("Unable to find class " + args[0] + "\n");
			System.exit(0);
		} catch(ExceptionInInitializerError e) {
			System.out.print("Unable to initialize class " + args[0] + "\n");
			System.exit(0);
		} catch(LinkageError e) {
			System.out.print("Unable to link class " + args[0] + "\n");
			System.exit(0);
		}
		
		Class to_inspect = null;
		try {
			to_inspect = Class.forName(args[1]);
		} catch(ClassNotFoundException e) {
			System.out.print("Unable to find class " + args[1] + "\n");
			System.exit(0);
		} catch(ExceptionInInitializerError e) {
			System.out.print("Unable to initialize class " + args[1] + "\n");
			System.exit(0);
		} catch(LinkageError e) {
			System.out.print("Unable to link class " + args[1] + "\n");
			System.exit(0);
		}
		
		boolean recursive = false;
		switch(args[2].toLowerCase()) {
		case "true":
		case "t":
			recursive = true;
			break;
		case "false":
		case "f":
			recursive = false;
			break;
		default:
			System.out.print("Please specify recursive as \"true\", \"t\", \"false\", or \"f\"\n");
			System.exit(0);
		}
		
		Object inspector = null;
		try {
			inspector = inspect_class.newInstance();
		} catch(InstantiationException e) {
			System.out.print("Cannot create instance of " + args[0] + "\n");
			System.exit(0);
		} catch (IllegalAccessException e) {
			System.out.print("Cannot access " + args[0] + "\n");
			System.exit(0);
		}
		Method inspect = null;
		try {
			inspect = inspect_class.getDeclaredMethod("inspect", new Object().getClass(), Boolean.TYPE);
		} catch(NoSuchMethodException e) {
			System.out.print(args[0] + " has no inspect method\n");
			System.exit(0);
		}
		
		Object inspectee = null;
		try {
			inspectee = to_inspect.newInstance();
		} catch(InstantiationException e) {
			System.out.print("Cannot create instance of " + args[1] + "\n");
			System.exit(0);
		} catch (IllegalAccessException e) {
			System.out.print("Cannot access " + args[1] + "\n");
			System.exit(0);
		}
		
		try {
			inspect.invoke(inspector, inspectee, recursive);
		} catch (IllegalAccessException e) {
			System.out.print("Cannot access inspect method\n");
			System.exit(0);
		} catch(InvocationTargetException e) {
			System.out.print("Inspect method threw an exception\n");
			System.exit(0);
		}
	}
}
