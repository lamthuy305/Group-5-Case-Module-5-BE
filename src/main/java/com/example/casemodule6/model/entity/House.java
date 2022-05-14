package com.example.casemodule6.model.entity;

import com.example.casemodule6.controller.RankBedroomController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "houses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double area;

    @ManyToOne
    private City city;

    private String location;

    @Column(length = 8192)
    private String description;

    private int bedroom;

    private int bathroom;

    private double price;

    private String img;

    private Long count_rent;

    @OneToOne
    private StatusHouse statusHouse;

    @OneToOne
    private Type type;

    @ManyToOne
    private User user;

    @ManyToOne
    private RankBathroom rankBathroom;

    @ManyToOne
    private RankBedroom rankBedroom;

    @ManyToOne
    private RankPrice rankPrice;

    public House(String name, double area, City city, String location, String description, int bedroom, int bathroom, double price, String img,
                 Long count_rent, StatusHouse statusHouse, Type type, User user, RankBathroom rankBathroom, RankBedroom rankBedroom, RankPrice rankPrice) {
        this.name = name;
        this.area = area;
        this.city = city;
        this.location = location;
        this.description = description;
        this.bedroom = bedroom;
        this.bathroom = bathroom;
        this.price = price;
        this.img = img;
        this.count_rent = count_rent;
        this.statusHouse = statusHouse;
        this.type = type;
        this.user = user;
        this.rankBathroom = rankBathroom;
        this.rankBedroom = rankBedroom;
        this.rankPrice = rankPrice;
    }
}
