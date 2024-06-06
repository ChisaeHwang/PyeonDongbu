package com.pyeondongbu.editorrecruitment.domain.auth.domain.dao;

import com.pyeondongbu.editorrecruitment.domain.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}