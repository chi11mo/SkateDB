package com.chillmo.skatedb.user.session.dto;

/**
 * Simple DTO bundling the access and refresh token pair that is returned to clients.
 *
 * @param accessToken          the short lived JWT access token
 * @param refreshToken         the long lived refresh token used to obtain new access tokens
 * @param accessTokenExpiresIn remaining lifetime of the access token in milliseconds
 */
public record SessionTokens(String accessToken, String refreshToken, long accessTokenExpiresIn) {
}

