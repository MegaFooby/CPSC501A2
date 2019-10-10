
import java.lang.reflect.*;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
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
		System.out.print("\n");
		
		//methods
		for(Method method : c.getMethods()) {
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
    }

	public static void main(String args[]) {
		try {
			new Inspector().inspect(Class.forName(args[0]).newInstance(), true);
		} catch(Exception e) {
			System.out.print("Class not found\n");
		}
	}

}
