package com.example.shell.command;

import com.example.shell.service.DirectoryService;
import com.example.shell.service.LoginService;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ShellComponent
public record DirectoryCommand(LoginService loginService, DirectoryService directoryService) {

    @ShellMethod("List directories")
    public String ls(String path) {

        String content = "";
        try (Stream<Path> stream = Files.walk(Paths.get(path), 1)) {
            content = stream
                    .filter(Files::isDirectory)
                    .map(Path::toAbsolutePath)
                    .map(p -> {
                        String localPath=p.toString();
                        directoryService.uploadDirectories(localPath);
                        return localPath;
                    })
                    .collect(Collectors.joining("\n"));
        } catch (IOException ex) {
        }
        return content;
    }

    public Availability lsAvailability() {
        return this.loginService.isLoggedIn() ? Availability.available() : Availability.unavailable("Not log in yet");
    }
}
