package com.itplace.userapi.map.entity;

import com.itplace.userapi.partner.entity.Partner;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "store")
public class Store{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeId", updatable = false, nullable = false)
    private long storeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partnerId", nullable = false)
    private Partner partner;

    @Column(name = "storeName")
    private String storeName;

    @Column(name = "business")
    private String business;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "town")
    private String town;

    @Column(name = "legalDong")
    private String legalDong;

    @Column(name = "address")
    private String address;

    @Column(name = "roadName")
    private String roadName;

    @Column(name = "roadAddress")
    private String roadAddress;

    @Column(name = "postCode", length = 50)
    private String postCode;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "location", columnDefinition = "POINT SRID 4326", nullable = false)
    private Point location;
}
