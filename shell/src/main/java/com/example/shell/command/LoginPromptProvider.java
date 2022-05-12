package com.example.shell.command;

import com.example.shell.service.LoginService;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public record LoginPromptProvider(LoginService service) implements PromptProvider {

    @Override
    public AttributedString getPrompt() {

        return service.isLoggedIn() ?
                new AttributedString(service.loggedInUser() + " :> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)) :
                new AttributedString("anonymous :> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
    }
}
