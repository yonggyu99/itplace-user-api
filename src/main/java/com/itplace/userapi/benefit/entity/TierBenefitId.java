package com.itplace.userapi.benefit.entity;

import com.itplace.userapi.benefit.entity.enums.Grade;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TierBenefitId implements Serializable {
    private Grade grade;
    private Long benefit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TierBenefitId)) return false;
        TierBenefitId that = (TierBenefitId) o;
        return grade == that.grade && Objects.equals(benefit, that.benefit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grade, benefit);
    }
}
