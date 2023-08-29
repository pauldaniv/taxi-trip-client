package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.pauldaniv.promotion.yellowtaxi.client.model.CommandSpec;
import com.pauldaniv.promotion.yellowtaxi.facade.model.AuthRequest;
import com.pauldaniv.promotion.yellowtaxi.facade.model.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.pauldaniv.promotion.yellowtaxi.client.service.CommandUtils.listToMap;
import static com.pauldaniv.promotion.yellowtaxi.client.service.CommandUtils.validate;

@Slf4j
@Service("login")
@RequiredArgsConstructor
public class LoginService implements CmdService {

    public static final String USERNAME = "--username";

    private static final Map<String, CommandSpec> COMMANDS = Map.of(
            USERNAME, CommandSpec.builder()
                    .typeSpec("string")
                    .description("Email of a user that is trying to login")
                    .build()
    );

    private final FacadeService facadeService;
    private final CommandLineReader commandLineReader;

    @Override
    public void runCommand(List<String> params) {

        final List<String> commandsPassed = params.stream()
                .filter(COMMANDS.keySet()::contains)
                .toList();

        final List<String> availableCommands = COMMANDS.keySet().stream().map(it -> String.format("%s <value>", it)).toList();
        validate(params, commandsPassed, availableCommands);
        final Map<String, String> commands = listToMap(params);
        final String username = commands.getOrDefault(USERNAME, COMMANDS.get(USERNAME).getDefaultValue());
        System.out.printf("User: %s%n", username);
        System.out.print("Pass: ");

        final String password = commandLineReader.read();
        doLogin(username, password);
    }

    private void doLogin(final String username,
                         final String password) {
        log.info("Logging in username={}, password=****", username);
        final AuthResponse auth = facadeService.login(AuthRequest.builder()
                .email(username)
                .password(password)
                .build());
        final String accessToken = auth.getAccessToken();
        try {
            Files.writeString(Paths.get("token.txt"), accessToken);
        } catch (Exception e) {
            log.error("unable to store access token", e);
            throw new RuntimeException("Unable to store access token", e);
        }
    }
}
