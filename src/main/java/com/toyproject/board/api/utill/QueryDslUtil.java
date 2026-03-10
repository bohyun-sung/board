package com.toyproject.board.api.utill;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public class QueryDslUtil {

    /**
     * whiteList sort 제공
     * @param sort         페이징 정렬 정보
     * @param sortMap      key: alias value qEntity.colum
     * @param defaultOrder 정렬 조건이 없을 때 적용할 기본 정렬 (예: post.rgdt.desc())
     * @return 정렬된 값
     */
    public static OrderSpecifier<?>[] getSafeOrderSpecifier(
            Sort sort,
            Map<String, Expression<?>> sortMap,
            OrderSpecifier<?> defaultOrder) {

        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier[]{defaultOrder};
        }

        List<OrderSpecifier> orders = sort.stream()
                .filter(order -> sortMap.containsKey(order.getProperty())) // 화이트리스트에 있는 필드만 허용
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    Expression<?> path = sortMap.get(order.getProperty());
                    return new OrderSpecifier(direction, path);
                })
                .toList();

        return orders.isEmpty() ? new OrderSpecifier[]{defaultOrder} : orders.toArray(OrderSpecifier[]::new);
    }

}
