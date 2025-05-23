package ma.emsi.charitywebapp.security;

import ma.emsi.charitywebapp.entities.Role;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.RoleService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private RoleService roleService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        Utilisateur user = utilisateurService.getUserByEmail(email).orElseGet(() -> {
            Utilisateur newUser = new Utilisateur();
            newUser.setEmail(email);
            newUser.setNom((String) attributes.get("name"));
            newUser.setEnabled(true);
            Role userRole = roleService.getRoleByNom("UTILISATEUR").orElseThrow();
            newUser.setRole(userRole);
            newUser.getRoles().add("ROLE_UTILISATEUR");
            return utilisateurService.saveUser(newUser);
        });

        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                user.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()),
                attributes,
                "email");
    }
}
