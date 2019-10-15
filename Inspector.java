
import java.lang.reflect.*;
import java.util.*;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
		ArrayList<Object> explore = new ArrayList<Object>();
		
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
		
		//fields
		for(Field f : c.getDeclaredFields()) {
			try {
				Object field = f.get(obj);
				System.out.print("\t" + Modifier.toString(f.getModifiers()) + " " + f.getType() + " " + f.getName() + " = " + field);
			} catch(Exception e) {
				System.out.print("\t" + Modifier.toString(f.getModifiers()) + " " + f.getType() + " " + f.getName());
			}
			System.out.print("\n");
		}
		if(c.getDeclaredFields().length != 0) {
			System.out.print("\n");
		}
		
		//constructors
		for(Constructor t : c.getDeclaredConstructors()) {
			System.out.print("\t" + Modifier.toString(t.getModifiers()) + " " + t.getName() + "(");
			loop_start = true;
			for(Class parameter : t.getParameterTypes()) {
				if(!loop_start) {
					System.out.print(", ");
				}
				System.out.print(parameter.getName());
				loop_start = false;
			}
			System.out.print(")\n");
		}
		if(c.getDeclaredConstructors().length != 0) {
			System.out.print("\n");
		}
		
		//methods
		for(Method method : c.getDeclaredMethods()) {
			//name, modifiers, return type
			String tmp = "\t" + Modifier.toString(method.getModifiers()) + " " + method.getReturnType().getName() + " " + method.getName() + "(";
			
			//parameters
			loop_start = true;
			for(Class parameter : method.getParameterTypes()) {
				if(!loop_start) {
					tmp += ", ";
				}
				tmp += parameter.getName();
				loop_start = false;
			}
			tmp += ")";
			
			//exceptions
			loop_start = true;
			for(Class eggception : method.getExceptionTypes()) {
				if(!loop_start) {
					tmp += ", ";
				} else {
					tmp += " throws ";
				}
				tmp += eggception.getName();
				loop_start = false;
			}
			System.out.print(tmp + "\n");
		}
		System.out.print("\n");
		
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
