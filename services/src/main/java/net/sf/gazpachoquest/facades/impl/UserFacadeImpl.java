package net.sf.gazpachoquest.facades.impl;

import net.sf.gazpachoquest.domain.user.User;
import net.sf.gazpachoquest.dto.UserDTO;
import net.sf.gazpachoquest.facades.UserFacade;
import net.sf.gazpachoquest.services.UserService;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFacadeImpl implements UserFacade {

    @Autowired
    private Mapper mapper;

    @Autowired
    private UserService userService;

    @Override
    public void delete(final Integer id) {
        userService.delete(id);
    }

    @Override
    public UserDTO findOne(final Integer id) {
        return mapper.map(userService.findOne(id), UserDTO.class);
    }

    @Override
    public UserDTO save(final UserDTO user) {
        User entity = mapper.map(user, User.class);
        return mapper.map(userService.save(entity), UserDTO.class);
    }
}
