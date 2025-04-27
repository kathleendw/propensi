package b02Propensi.siladu.user.authentication;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        UserModel user = userService.findByEmail(authentication.getName());

        System.out.println("tes user : " + user.getEmail());

        if (user.getIsPassUpdated()) {
            if(user.getRole().name().equals("ADMIN_LAYANAN")){
                response.sendRedirect("/dashboard");
            }else{
                response.sendRedirect("/home-member");
            }
        
        } else {
            response.sendRedirect("/home-member?success");
        }

    }

}
