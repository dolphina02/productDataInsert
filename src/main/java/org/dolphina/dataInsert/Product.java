package org.dolphina.dataInsert;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자
public class Product {

    private String productId;       // 상품 ID
    private String name;          // 상품 이름
    private String description;   // 상품 설명
    private Double price;         // 상품 가격
    private String category;      // 상품 카테고리
    private String brand;         // 브랜드 이름
    private Double rating;        // 상품 평점

}