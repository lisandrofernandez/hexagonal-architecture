package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.controller;

import com.github.lisandrofernandez.hexagonal.domain.DomainException;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.in.CreateUserAccountHandler;
import com.github.lisandrofernandez.hexagonal.domain.port.in.GetUserAccountQuery;
import com.github.lisandrofernandez.hexagonal.domain.port.in.command.CreateUserAccountCommand;
import com.github.lisandrofernandez.hexagonal.infrastructure.in.api.common.SingleApiResponse;
import com.github.lisandrofernandez.hexagonal.infrastructure.in.api.dto.UserAccountCreationRequest;
import com.github.lisandrofernandez.hexagonal.infrastructure.in.api.dto.UserAccountResponse;
import com.github.lisandrofernandez.hexagonal.infrastructure.in.api.mapper.UserAccountResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

// inbound adapter
@RestController
@RequestMapping(value = UserAccountController.BASE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserAccountController {
    public static final String BASE_URL = "/api/v1/users";

    private final GetUserAccountQuery getUserAccountQuery;
    private final CreateUserAccountHandler createUserAccountHandler;
    private final UserAccountResponseMapper userAccountResponseMapper;

    @GetMapping("/{id}")
    SingleApiResponse<UserAccountResponse> getById(@PathVariable UUID id) {
        return getUserAccountQuery.getUserAccountById(id)
                .map(userAccountResponseMapper::fromDomain)
                .map(SingleApiResponse::of)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user account not found"));
    }

    @PostMapping
    SingleApiResponse<UserAccountResponse> create(@RequestBody UserAccountCreationRequest userAccountCreationRequest) {
        CreateUserAccountCommand createUserAccountCommand = mapToCreateUserAccountCommand(userAccountCreationRequest);
        UserAccount userAccount;
        try {
            userAccount = createUserAccountHandler.createUserAccount(createUserAccountCommand);
        } catch (DomainException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        UserAccountResponse userAccountResponse = userAccountResponseMapper.fromDomain(userAccount);
        return SingleApiResponse.of(userAccountResponse);
    }

    private CreateUserAccountCommand mapToCreateUserAccountCommand(
            UserAccountCreationRequest userAccountCreationRequest
    ) {
        return CreateUserAccountCommand.builder()
                .username(userAccountCreationRequest.getUsername())
                .name(userAccountCreationRequest.getName())
                .build();
    }
}
