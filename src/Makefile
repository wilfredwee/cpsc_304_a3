all:
	javac -cp "classes12.jar" Main.java Tuple.java
	jar cvfe Main.jar Main *.class

run: Main.jar
	java -cp Main.jar:classes12.jar Main
clean:
	rm -f *.class
	rm -f Main.jar
