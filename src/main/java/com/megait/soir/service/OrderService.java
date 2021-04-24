package com.megait.soir.service;

import com.megait.soir.domain.*;
import com.megait.soir.repository.ItemRepository;
import com.megait.soir.repository.MemberRepository;
import com.megait.soir.repository.OrderItemRepository;
import com.megait.soir.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    @Transactional
    public void addCart(Member member, List<Long> itemIdList){
        member = memberRepository.getOne(member.getId());

        Orders orders = orderRepository.findByMemberAndStatus(member, Status.CART);

        if(orders == null){
            orders = new Orders();
            orders.setStatus(Status.CART);
            orders.setMember(member);

            orderRepository.save(orders);
        }

        final Orders tmpOrders = orders;
        // OrderItem 등록하기
        List<Item> itemList = itemRepository.findAllById(itemIdList);
        List<OrderItem> orderItemList = itemList.stream().map(
                item ->{
                    OrderItem orderItem = new OrderItem();
                    orderItem.setItem(item);
                    orderItem.setOrder(tmpOrders);
                    orderItem.setCount(1);
                    orderItem.setOrderPrice((int)item.getPrice());

                    return orderItem;
                }
        ).collect(Collectors.toList());

        orders = orderRepository.getOne(orders.getId());

        if(orders.getOrderItems() == null){
            orders.setOrderItems(new ArrayList<>());
        }

        orders.getOrderItems().addAll(orderItemList);
    }


    public List<OrderItem> getCart(Member member){
        // 장바구니 목록 가져오기
        Orders cartOrder = orderRepository.findByMemberAndStatus(member, Status.CART);

        if(cartOrder == null){
            throw new IllegalArgumentException("empty.cart");
        }
        log.info("get cart list complete.");

        return cartOrder.getOrderItems();

    }



    @Transactional
    public void minusCart(Member member, Long deleteItemId){
        // 장바구니 목록 삭제하기
        Orders orders = orderRepository.findByMemberAndStatus(member, Status.CART);
        List<OrderItem> orderItemList = orders.getOrderItems();

        log.info("deleteItemId : " + deleteItemId);
        orderItemList.removeIf(item -> item.getId().equals(deleteItemId));
        orderItemRepository.deleteById(deleteItemId);
        log.info("orderItemList : " + orderItemList.toString());

        //orders.setOrderItems(orderItemList);
        orderRepository.save(orders);

    }


    public int getTotalPrice(List<OrderItem> list){
        // 장바구니에 담긴 Item 객체들의 총 가격을 계산
//        return list.stream().mapToInt(OrderItem::getOrderPrice).sum();
        return list.stream().mapToInt(orderItem -> orderItem.getOrderPrice()).sum();
    }
}
