package com.itplace.userapi.user.dto;

import com.itplace.userapi.user.entity.Gender;
import com.itplace.userapi.user.entity.UplusData;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UplusDataDto {
    private String name;
    private String phoneNumber;
    private Gender gender;
    private LocalDate birthday;
    private String membershipId;

    public static UplusDataDto from(UplusData uplusData) {
        return UplusDataDto.builder()
                .gender(uplusData.getGender())
                .birthday(uplusData.getBirthday())
                .membershipId(uplusData.getMembershipId())
                .build();
    }
}
