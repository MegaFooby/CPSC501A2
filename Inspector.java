
import java.lang.reflect.*;
import java.util.*;

public class Inspector {
	
	private ArrayList<Object> explore = new ArrayList<Object>();
    
    public static final boolean RECURSIVELY_EXPLORE_OBJECTS = false;
    
    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
		
		this.print_title(c);
		
		if(c.getDeclaredFields().length != 0) {
			this.print_fields(c, obj);
			System.out.print("\n");
		}
		
		if(c.getDeclaredConstructors().length != 0) {
			this.print_constructors(c);
			System.out.print("\n");
		}
		
		if(c.getDeclaredMethods().length != 0) {
			this.print_methods(c);
			System.out.print("\n");
		}
		
		System.out.print("\n");
		
		if(recursive) {
			for(Object o : explore) {
				inspect(o, RECURSIVELY_EXPLORE_OBJECTS);
			}
		}
		
		//recursivly print superclasses
		if((recursive || depth == 0) && c.getSuperclass() != null) {
			this.inspectClass(c.getSuperclass(), obj, recursive, depth+1);
		}
		
		//recursivly checks out interfaces
		if((recursive || depth == 0)) {
			for(Class enterface : c.getInterfaces()) {
				this.inspectClass(enterface, obj, true, depth+1);
			}
		}
    }
    
    /**
     * Prints the name, modifiers, interfaces, and superclass of the specified class
     * 
     * @param c	The class to inspect
     */
    public void print_title(Class c) {
		//Declaring class
		System.out.print(Modifier.toString(c.getModifiers()) + " " + c.getName());
		
		//interfaces
		boolean loop_start = true;
		for(Class enterface : c.getInterfaces()) {
			if(loop_start) {
				System.out.print(" implements ");
			} else {
				System.out.print(", ");
			}
			System.out.print(enterface.getName());
			loop_start = false;
		}
		
		if(c.getSuperclass() != null) {
			System.out.print(" extends " + c.getSuperclass().getName());
		}
		System.out.print("\n");
	}
	
	/**
	 * Prints all fields and values if public
	 * 
	 * @param c	The class to print
	 * @param obj	The object with the values
	 */
	public void print_fields(Class c, Object obj) {
		//fields
		/*for(Field f : c.getDeclaredFields()) {
			try {
				Object field = f.get(obj);
				System.out.print("\t" + Modifier.toString(f.getModifiers()) + " " + f.getType() + " " + f.getName() + " = " + field);
			} catch(IllegalArgumentException | IllegalAccessException e) {
				System.out.print("\t" + Modifier.toString(f.getModifiers()) + " " + f.getType() + " " + f.getName());
			}
			System.out.print("\n");
		}*/
		
		
		for(Field f : c.getDeclaredFields()) {
			System.out.print("\t" + Modifier.toString(f.getModifiers()) + " ");
			if(f.getType().getName().charAt(0) == '[') {
				int depth = 0;
				for(depth = 0; depth < f.getType().getName().length(); depth++) {
					if(f.getType().getName().charAt(depth) != '[') {
						break;
					}
				}
				switch(f.getType().getName().charAt(depth)) {
					case 'B':
					System.out.print("byte");
					break;
					case 'C':
					System.out.print("char");
					break;
					case 'D':
					System.out.print("double");
					break;
					case 'F':
					System.out.print("float");
					break;
					case 'I':
					System.out.print("int");
					break;
					case 'J':
					System.out.print("long");
					break;
					case 'L':
					System.out.print("reference type");
					break;
					case 'S':
					System.out.print("short");
					break;
					case 'Z':
					System.out.print("boolean");
					break;
				}
			} else {
				try {
					Object field = f.get(obj);
					System.out.print(f.getType() + " " + f.getName() + " = " + field);
				} catch(IllegalArgumentException | IllegalAccessException e) {
					System.out.print(f.getType() + " " + f.getName());
				}
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * Prints all constructors
	 * 
	 * @param c	The class to print
	 */
	public void print_constructors(Class c) {
		//constructors
		for(Constructor t : c.getDeclaredConstructors()) {
			System.out.print("\t" + Modifier.toString(t.getModifiers()) + " " + t.getName() + "(");
			boolean loop_start = true;
			for(Class parameter : t.getParameterTypes()) {
				if(!loop_start) {
					System.out.print(", ");
				}
				System.out.print(parameter.getName());
				loop_start = false;
			}
			System.out.print(")\n");
		}
	}
	
	/**
	 * Prints all methods from a class
	 * 
	 * @param c	The class to print
	 */
	public void print_methods(Class c) {
		//methods
		for(Method method : c.getDeclaredMethods()) {
			//name, modifiers, return type
			System.out.print("\t" + Modifier.toString(method.getModifiers()) + " " + method.getReturnType().getName() + " " + method.getName() + "(");
			
			//parameters
			boolean loop_start = true;
			for(Class parameter : method.getParameterTypes()) {
				if(!loop_start) {
					System.out.print(", ");
				}
				System.out.print(parameter.getName());
				loop_start = false;
			}
			System.out.print(")");
			
			//exceptions
			loop_start = true;
			for(Class eggception : method.getExceptionTypes()) {
				if(!loop_start) {
					System.out.print(", ");
				} else {
					System.out.print(" throws ");
				}
				System.out.print(eggception.getName());
				loop_start = false;
			}
			System.out.print("\n");
		}
	}

	/**
	 * A test method which recursively prints out a class
	 * 
	 * @param args[]	the name of the class
	 */
	public static void main(String args[]) {
		try {
			new Inspector().inspect(Class.forName(args[0]).newInstance(), true);
		} catch(NullPointerException e) {
			e.printStackTrace();
		} catch(Exception e) {
			System.out.print("Could not find class\n");
		}
	}

}
