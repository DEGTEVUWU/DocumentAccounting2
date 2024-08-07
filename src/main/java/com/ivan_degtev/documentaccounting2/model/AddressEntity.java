package com.ivan_degtev.documentaccounting2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.documentaccounting2.model.interfaces.BaseEntity;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;

import lombok.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(schema = "public", name = "addresses")
@Getter
@ToString(exclude = { "user" })
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AddressEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("country")
    private String country;
    @JsonProperty("city")
    private String city;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("street")
    private String street;
    @JsonProperty("house")
    private String house;
    @JsonProperty("full_address")
    private String fullAddress;
    @JsonProperty("geo_lat")
    private Double geoLat;
    @JsonProperty("geo_lon")
    private Double geoLon;
    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;
    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private FileEntity fileEntity;

    public String getFullAddressByDataForOutput() {
        return "Страна: " + getCountry() + ", " +
                "город: " + getCity() + ", " +
                "улица: " + getStreet() + ", " +
                "дом: " + getHouse() + ", " +
                "почтовый код: " + getPostalCode();
    }
}
