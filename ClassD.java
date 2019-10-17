
public class ClassD {

    public ClassA val = new ClassA(17);
    public static ClassA val2;
    public int val3 = 34;
    public ClassA[] vallarray = new ClassA[10];

    public ClassD() {
    }

    public ClassD(int i) {
        val3 = i;
    }

    public String toString() {
        return "ClassD";
    }

    public int getVal3() {
        return val3;
    }

}
