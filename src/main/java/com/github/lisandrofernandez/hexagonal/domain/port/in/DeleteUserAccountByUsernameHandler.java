package com.github.lisandrofernandez.hexagonal.domain.port.in;

public interface DeleteUserAccountByUsernameHandler {

    boolean delete(String username);
}
