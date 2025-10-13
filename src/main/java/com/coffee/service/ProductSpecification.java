package com.coffee.service;

import com.coffee.constant.Category;
import com.coffee.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Creteria : 판단이나 결정을 짓기위한 기준
// Specification : JPA의 Creteria API를 사용하여 where절을 객체지향적으로 만들기 위해 사용되는 인터페이스
// 즉, JPA를 사용하여 동적 쿼리를 만들어주는 인터페이스.
public class ProductSpecification {
    /*
        사용자가 지정한 날짜 범위의 상품 목록만 필터링해주는 Specification임.
        searchDateType : 검색할 날짜 범위를 의미하는 문자열.
        반환 값 : 검색된 범위의 상품목록을 조회하기 위한 Specification 객체 정보.
    */
    public static Specification<Product> hasDateRange(String searchDateType){
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                LocalDate now = LocalDate.now(); // 현재 시각

                LocalDate startDate = null; // 검색 시작일자

                // 선택한 콤보박스의 값을 보고, 검색 시작 일자를 계산함.
                switch (searchDateType){
                    case "1d" :
                        startDate = now.minus(1, ChronoUnit.DAYS);
                        break;
                    case "1w" :
                        startDate = now.minus(1, ChronoUnit.WEEKS);
                        break;
                    case "1m" :
                        startDate = now.minus(1, ChronoUnit.MONTHS);
                        break;
                    case "6m" :
                        startDate = now.minus(6, ChronoUnit.MONTHS);
                        break;
                    case "all":
                    default: //전체기간 조회
                    return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
                }
                // 상품 입고 일자(inputdate)가 검색 시작일자(startDate) 이후의 상품들만 검색.
                return criteriaBuilder.greaterThanOrEqualTo(root.get("inputdate"),startDate);
            }
        };
    }

    /*
        사용자가 지정한 특정 카테고리에 속하는 상품 목록만 필터링해주는 Specification임.
        category : 검색할 카테고리를 의미하는 문자열.
        반환 값 : 해당 카테고리에 해당하는 상품목록을 조회하기 위한 Specification 객체 정보.
    */
    public static Specification<Product> hasCategory(Category category){
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (category == Category.ALL){
                    return criteriaBuilder.conjunction();
                    // conjunction() 메소드는 논리적으로 항상 true인 객체를 반환함.
                    // sql의 where 구문을 만들지 않음.
                } else {
                    return criteriaBuilder.equal(root.get("category"),category);
                }
            }
        };
    }

    /*
        상품 이름에 특정 키워드가 포함된 상품 목록만 필터링해주는 Specification임.
        keyword : 상품 이름에 포함될 키워드.
        반환 값 : 상품 이름에 해당 키워드가 포함된 상품목록을 조회하기 위한 Specification 객체 정보.
    */
    public static Specification<Product> hasNameLike(String keyword){
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(root.get("name"),"%" + keyword + "%");
            }
        };
    }

    /*
        상품 상세 설명에 특정 키워드가 포함된 상품 목록만 필터링해주는 Specification임.
        keyword : 상품 상세 설명에 포함될 키워드.
        반환 값 : 상품 상세 설명에 해당 키워드가 포함된 상품목록을 조회하기 위한 Specification 객체 정보.
    */
    public static Specification<Product> hasDescriptionLike(String keyword){
        return new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(root.get("description"),"%" + keyword + "%");
            }
        };
    }
}
