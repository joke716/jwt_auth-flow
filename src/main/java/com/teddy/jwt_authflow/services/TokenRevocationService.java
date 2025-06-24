package com.teddy.jwt_authflow.services;

import lombok.NonNull;


public interface TokenRevocationService {
    void revoke();
    boolean isRevoked(@NonNull final String authHeader);
}
