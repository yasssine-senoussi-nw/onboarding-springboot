package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import com.nimbleways.springboilerplate.common.domain.ports.PasswordEncoderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.EncodedPassword;

public class FakePasswordEncoder implements PasswordEncoderPort {
    public static final FakePasswordEncoder INSTANCE = new FakePasswordEncoder();

    @Override
    public EncodedPassword encode(String password) {
        return new EncodedPassword("$encoded$" + password);
    }

    @Override
    public boolean matches(CharSequence rawPassword, EncodedPassword encodedPassword) {
        EncodedPassword encodedRawPassword = encode(rawPassword.toString());
        return encodedRawPassword.equals(encodedPassword);
    }
}
