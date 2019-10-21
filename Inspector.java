
import java.lang.reflect.*;
import java.util.*;

public class Inspector {
	
	private ArrayList<Object> explore = new ArrayList<Object>();
    
    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
		
		/*if(seen.containsKey(c)) {
			seen.get(c).i++;
		} else {
			seen.put(c, new integer(1));
		}*/
		
		this.print_title(c);
		
		//System.out.print("\t0x" + String.format("%08x", obj.hashCode()) + "\n");
		
		if(c.getDeclaredFields().length != 0) {
			this.print_fields(c, obj, true);
			System.out.print("\n");
		}
		
		if(c.getDeclaredConstructors().length != 0) {
			this.print_constructors(c);
			System.out.print("\n");
		}
		
		if(c.getDeclaredMethods().length != 0) {
			this.print_methods(c, true);
			System.out.print("\n");
		}
		
		System.out.print("\n");
		
		
		if(recursive && depth == 0) {
			//for(Object o : explore) {
			for(int i = 0; i < explore.size(); i++) {
				Object o = explore.get(i);
				//System.out.print(explore.size() + "\n");
				Class cls = o.getClass();
				inspectClass(cls, o, recursive, depth+1);
			}
		}
		//System.out.print(explore.size() + "\n");
    }
    
    /**
     * Prints the name, modifiers, interfaces, and superclass of the specified class
     * 
     * @param c	The class to inspect
     */
    public void print_title(Class c) {
		//Declaring class
		System.out.print(Modifier.toString(c.getModifiers()) + " ");
		if(c.isInterface()) {//if I just do toString, "interface" prints twice
			System.out.print(c.getName());
		} else {
			System.out.print(c.toString());
		}
		
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
	
	public String format_class_name(Class c) {
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
			for(int i = 0; i < depth; i++) {
				ret += "[]";
			}
			return ret;
		} else {
			return c.getName();
		}
	}
	
	/**
	 * Prints all fields and values if public
	 * 
	 * @param c	The class to print
	 * @param obj	The object with the values
	 */
	public void print_fields(Class c, Object obj, boolean initial_run) {
		//fields
		for(Field f : c.getDeclaredFields()) {
			if(!initial_run && Modifier.isPrivate(f.getModifiers())) continue;
			System.out.print("\t" + Modifier.toString(f.getModifiers()) + " ");
			if(!f.getType().isPrimitive()) {
				System.out.print(this.format_class_name(f.getType()) + " " + f.getName());
				try {
					Object field = f.get(obj);
					System.out.print(" = " + format_class_name(field.getClass()) + "@" + String.format("%08x", field.hashCode()));
				} catch(NullPointerException e) {
					System.out.print(" = null");
				} catch(IllegalArgumentException | IllegalAccessException e) {}
			} else {
				System.out.print(f.getType() + " " + f.getName());
				try {
					Object field = f.get(obj);
					System.out.print(" = " + field.toString());
				} catch(NullPointerException e) {
					System.out.print(" = null");
				} catch(IllegalArgumentException | IllegalAccessException e) {}
			}
			System.out.print("\n");
			if(f.getType().isPrimitive()) continue;
			try {
				this.add_objects(f.get(obj));
			} catch(IllegalAccessException e) {}
		}
		
		//recursivly print superclasses
		if(c.getSuperclass() != null) {
			this.print_fields(c.getSuperclass(), obj, false);
		}
		
		//recursivly checks out interfaces
		for(Class enterface : c.getInterfaces()) {
			this.print_fields(enterface, obj, false);
		}
	}
	
	public void add_objects(Object obj) {
		if(obj == null) return;
		if(obj.getClass().isArray()) {
			for(int i = 0; i < Array.getLength(obj); i++) {
				this.add_objects(Array.get(obj, i));
			}
		} else {
			if(obj != null) {
				explore.add(obj);
			}
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
				System.out.print(this.format_class_name(parameter));
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
	public void print_methods(Class c, boolean initial_run) {
		//methods
		for(Method method : c.getDeclaredMethods()) {
			if(!initial_run && Modifier.isPrivate(method.getModifiers())) continue;
			//name, modifiers, return type
			System.out.print("\t" + Modifier.toString(method.getModifiers()) + " " + method.getReturnType().getName() + " " + method.getName() + "(");
			
			//parameters
			boolean loop_start = true;
			for(Class parameter : method.getParameterTypes()) {
				if(!loop_start) {
					System.out.print(", ");
				}
				System.out.print(this.format_class_name(parameter));
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
		//recursivly print superclasses
		if(c.getSuperclass() != null) {
			this.print_methods(c.getSuperclass(), false);
		}
		
		//recursivly checks out interfaces
		for(Class enterface : c.getInterfaces()) {
			this.print_methods(enterface, false);
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
	public integer(int i) {
		this.i = i;
	}
}
