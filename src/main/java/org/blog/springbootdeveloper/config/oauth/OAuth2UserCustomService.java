package org.blog.springbootdeveloper.config.oauth;

import lombok.RequiredArgsConstructor;
import org.blog.springbootdeveloper.domain.User;
import org.blog.springbootdeveloper.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userrequest) throws OAuth2AuthenticationException {
        //요청을 바탕으로 user정보를 담은 객체를 반환함
        OAuth2User user = super.loadUser(userrequest);
        saveOrUpdate(user);
        return user;
    }

    //user가 없다면 update, 있다면 user 생성함
    private User saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .build());

        return userRepository.save(user);
    }

}
