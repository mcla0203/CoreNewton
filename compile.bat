REM Clean out the old compile.


del /S bin\*.class


del /S src\*.class


REM Compile the server package -- javac requires the JDK/bin directory in your PATH
javac -classpath "src/com/cn/server" -d "bin" src/com/cn/server/*.java

javac -classpath "src/com/cn/protocol" -d "bin" src/com/cn/protocol/*.java

javac -classpath "src/com/cn/monsters" -d "bin" src/com/cn/monsters/*.java


javac -classpath "src/com/cn/players" -d "bin" src/com/cn/players/*.java


