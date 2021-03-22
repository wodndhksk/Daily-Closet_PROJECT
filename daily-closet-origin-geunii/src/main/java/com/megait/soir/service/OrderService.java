package com.megait.soir.service;

import com.megait.soir.domain.*;
import com.megait.soir.repository.ItemRepository;
import com.megait.soir.repository.MemberRepository;
import com.megait.soir.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

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

        return cartOrder.getOrderItems();

    }
    
    
    public int getTotalPrice(List<OrderItem> list){
        // 장바구니에 담긴 Item 객체들의 총 가격을 계산
//        return list.stream().mapToInt(OrderItem::getOrderPrice).sum();
         return list.stream().mapToInt(orderItem -> orderItem.getOrderPrice()).sum();
    }
}
