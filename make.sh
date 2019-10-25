mkdir out
javac -d out Inspector.java
javac -d out -cp out:junit-platform-console-standalone-1.5.2.jar InspectorTest.java
