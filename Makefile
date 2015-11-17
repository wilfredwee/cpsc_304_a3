all:
	javac -cp ".;*.jar;*.zip" Main.java
	jar cvfe Main.jar Main *.class

run: Main.jar
	java -jar Main.jar
clean:
	rm -f *.class
	rm -f Main.jar
