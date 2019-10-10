
import java.lang.reflect.*;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
		//Declaring class
		System.out.print("1. " + c.getName() + "\n");
		
		//interfaces
		for(Class enterface : c.getInterfaces()) {
			System.out.print(enterface + "\n");
		}
		
		//methods
		for(Method method : c.getMethods()) {
			String tmp = method.getName() + "(";
			boolean put_comma = false;
			for(Class parameter : method.getParameterTypes()) {
				if(put_comma) {
					tmp += ", ";
				}
				tmp += parameter.getName();
				put_comma = true;
			}
			System.out.print(tmp + ")\n");
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
