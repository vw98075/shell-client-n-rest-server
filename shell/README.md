## Development

Run the following command in a terminal to build and run the app.
```
./mvnw install
java -jar target/shell-0.0.1-SNAPSHOT.jar
```
or
```
mvn -DskupTests clean package && java -jar target/*.jar
```
You shall see a shel prompt 
```
anonymous :> 
```
afterward.

## Use The Command Line Shell

To use any non-build-in commands, the server of this application needs to be running.

Once you are in the shell, you can run "help" to find out all available commands. To find how to run a command, you can run "help" follow by the command, for example,
```
anonymous :> help login
```
Currently, there are three non-build-in commands: login, logout, and ls. And the last two are available only after user login. The command availability isn't working with the current snapshot version of Spring Shell. To run login, user a user credential on its server to login. If you use the user credential of "user" to login, you shall see the shell as
```
user :>
```

## Warming
The application is depended on a snapshot version of spring-shell-starter. The behavior of this application isn't one hundred percent predicable and reliable as a result.




