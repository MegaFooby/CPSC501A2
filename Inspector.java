
import java.lang.reflect.*;
import java.util.*;

public class Inspector {
    
    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
		
		this.print_title(c, obj, depth);
		
		//System.out.print("\t0x" + String.format("%08x", obj.hashCode()) + "\n");
		
		if(c.getDeclaredFields().length != 0) {
			for(int  i = 0; i < depth; i++) {
				System.out.print("\t");
			}
			System.out.print("Fields:\n");
			this.print_fields(c, obj, depth, recursive);
			System.out.print("\n");
		}
		
		if(c.getDeclaredConstructors().length != 0) {
			for(int  i = 0; i < depth; i++) {
				System.out.print("\t");
			}
			System.out.print("Constructors:\n");
			this.print_constructors(c, depth);
			System.out.print("\n");
		}
		
		if(c.getDeclaredMethods().length != 0) {
			for(int  i = 0; i < depth; i++) {
				System.out.print("\t");
			}
			System.out.print("Methods:\n");
			this.print_methods(c, depth);
			System.out.print("\n");
		}
		
		//recursivly print superclasses
		if(c.getSuperclass() != null) {
			this.inspectClass(c.getSuperclass(), obj, recursive, depth+1);
		}
		
		//recursivly checks out interfaces
		for(Class enterface : c.getInterfaces()) {
			this.inspectClass(enterface, obj, recursive, depth+1);
		}
		
		System.out.print("\n");
    }
    
    /**
     * Prints the name, modifiers, interfaces, and superclass of the specified class
     * 
     * @param c	The class to inspect
     */
    public void print_title(Class c, Object obj, int depth) {
		//Declaring class
		for(int  i = 0; i < depth; i++) {
			System.out.print("\t");
		}
		System.out.print(Modifier.toString(c.getModifiers()) + " ");
		if(c.isInterface()) {//if I just do toString, "interface" prints twice
			System.out.print(c.getName());
		} else {
			System.out.print(c.toString());
		}
		System.out.print("@" + String.format("%08x", obj.hashCode()));
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
	
	public String format_class_name(Class c, integer array_depth) {
		String ret = "";
		if(c.getName().charAt(0) == '[') {
			int depth = 0;
			for(depth = 0; depth < c.getName().length(); depth++) {
				if(c.getName().charAt(depth) != '[') {
					break;
				}
			}
			switch(c.getName().charAt(depth)) {
			case 'B':
				ret += "byte";
				break;
			case 'C':
				ret += "char";
				break;
			case 'D':
				ret += "double";
				break;
			case 'F':
				ret += "float";
				break;
			case 'I':
				ret += "int";
				break;
			case 'J':
				ret += "long";
				break;
			case 'L':
				ret += c.getName().substring(depth+1, c.getName().length()-1);
				break;
			case 'S':
				ret += "short";
				break;
			case 'Z':
				ret += "boolean";
				break;
			}
			/*for(int i = 0; i < depth; i++) {
				ret += "[]";
			}*/
			array_depth.i = depth;
			return ret;
		} else {
			return c.getName();
		}
	}
	
	public void print_array(Object obj, int depth, boolean recursive) {
		try {
			for(int i = 0; i < Array.getLength(obj); i++) {
				for(int j = 0; j < depth; j++) {
					System.out.print("\t");
				}
				System.out.print(i + ") ");
				try {
					Object element = Array.get(obj, i);
					integer myint = new integer();
					if(element.getClass().isPrimitive()) {
						System.out.print(element + "\n");
					} else {
						if(element.getClass().isArray()) {
							System.out.print(this.format_class_name(element.getClass(), myint) + "[" + Array.getLength(element) + "]" + "\n");
							print_array(element, depth+1, recursive);
						} else {
							System.out.print(this.format_class_name(element.getClass(), myint) + "@" + String.format("%08x", element.hashCode()) + "\n");
							if(recursive) {
								inspectClass(element.getClass(), element, recursive, depth+1);
							}
						}
					}
				} catch(NullPointerException e) {
					System.out.print("null\n");
				}
			}
		} catch(IllegalArgumentException e) {}
	}
	
	/**
	 * Prints all fields and values if public
	 * 
	 * @param c	The class to print
	 * @param obj	The object with the values
	 */
	public void print_fields(Class c, Object obj, int depth, boolean recursive) {
		//fields
		for(Field f : c.getDeclaredFields()) {
			f.setAccessible(true);
			for(int  i = 0; i < depth; i++) {
				System.out.print("\t");
			}
			System.out.print(Modifier.toString(f.getModifiers()) + " ");
			if(!f.getType().isPrimitive()) {
				integer array_depth = new integer(0);
				System.out.print(this.format_class_name(f.getType(), array_depth));
				for(int i = 0; i < array_depth.i; i++) {
					System.out.print("[]");
				}
				System.out.print(" " + f.getName());
				try {
					Object field = f.get(obj);
					System.out.print(" = " + format_class_name(field.getClass(), new integer()));
					if(recursive && !f.getType().isArray()) {
						System.out.print("@" + String.format("%08x", field.hashCode()) + "\n");
						inspectClass(field.getClass(), field, recursive, depth+1);
					} else if(f.getType().isArray()) {
						System.out.print("[" + Array.getLength(field) + "]\n");
						this.print_array(field, depth+1, recursive);
					} else {
						System.out.print("@" + String.format("%08x", field.hashCode()) + "\n");
					}
				} catch(NullPointerException e) {
					System.out.print(" = null\n");
				} catch(IllegalArgumentException | IllegalAccessException e) {
					System.out.print("\n");
				}
			} else {
				System.out.print(f.getType() + " " + f.getName());
				try {
					Object field = f.get(obj);
					System.out.print(" = " + field.toString() + "\n");
				} catch(NullPointerException e) {
					System.out.print(" = null\n");
				} catch(IllegalArgumentException | IllegalAccessException e) {
					System.out.print("\n");
				}
			}
		}
	}
	
	/**
	 * Prints all constructors
	 * 
	 * @param c	The class to print
	 */
	public void print_constructors(Class c, int depth) {
		//constructors
		for(Constructor t : c.getDeclaredConstructors()) {
			for(int  i = 0; i < depth; i++) {
				System.out.print("\t");
			}
			System.out.print(Modifier.toString(t.getModifiers()) + " " + t.getName() + "(");
			boolean loop_start = true;
			for(Class parameter : t.getParameterTypes()) {
				if(!loop_start) {
					System.out.print(", ");
				}
				System.out.print(this.format_class_name(parameter, new integer()));
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
	public void print_methods(Class c, int depth) {
		//methods
		for(Method method : c.getDeclaredMethods()) {
			//name, modifiers, return type
			for(int  i = 0; i < depth; i++) {
				System.out.print("\t");
			}
			System.out.print(Modifier.toString(method.getModifiers()) + " " + method.getReturnType().getName() + " " + method.getName() + "(");
			
			//parameters
			boolean loop_start = true;
			for(Class parameter : method.getParameterTypes()) {
				if(!loop_start) {
					System.out.print(", ");
				}
				System.out.print(this.format_class_name(parameter, new integer()));
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
			e.printStackTrace();
			//System.out.print("Could not find class\n");
		}
	}

}

class integer {
	public int i;
	public integer() { i = 0; }
	public integer(int i) {
		this.i = i;
	}
}
