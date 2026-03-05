package com.toyproject.board.api.jwt;


import com.toyproject.board.api.constants.AuthConstants;
import com.toyproject.board.api.enums.RoleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RedisHash(value = AuthConstants.REFRESH_TOKEN, timeToLive = AuthConstants.REFRESH_TOKEN_MAX_AGE)
public class RefreshToken {

    @Id
    private String id;

    @Indexed
    private String token;

    public static RefreshToken of(Long idx, RoleType roleType, String token) {
        String compositeId = roleType.name() + ":" + idx;
        return new RefreshToken(compositeId, token);
    }

    /**
     * idx를 추출
     * @return idx
     */
    public Long getUserIdx() {
        return Long.parseLong(this.id.split(":")[1]);
    }

    /**
     * RoleType을 추출
     * @return RoleType
     */
    public RoleType getRoleType() {
        return RoleType.valueOf(this.id.split(":")[0]);
    }
}
