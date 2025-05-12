package com.chillmo.skatedb.user.registration.service;

import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.registration.domain.ConfirmationToken;
import org.springframework.stereotype.Service;

@Service
public class EmailRegisterService {


    /**
     * This function creates the body for the new user registration email.
     *
     * @param savedUser user for the username.
     * @param confirmationToken to send confirmation token for the email
     * @return body for the registration email.
     */
    public String createRegisterEmailBody(User savedUser, ConfirmationToken confirmationToken){


        String link = "http://localhost:8080/api/token/confirm?token=" + confirmationToken.getToken();

        return "<p>Hallo " + savedUser.getUsername() + ",</p>"
                + "<p>bitte bestätige deine E-Mail-Adresse, indem du auf den folgenden Link klickst:</p>"
                + "<a href=\"" + link + "\">E-Mail bestätigen</a>";
    }


}
