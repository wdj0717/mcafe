package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.order.dto.OrderResponse
import com.midasit.mcafe.infra.component.UChefComponent
import org.springframework.stereotype.Service

@Service
class OrderService(
        private val uChefComponent: UChefComponent
) {

    fun getMenuList(): OrderResponse.GetMenuList {
        return OrderResponse.GetMenuList(uChefComponent.getMenuList())
    }

    fun getMenuInfo(menuCode: Long): OrderResponse.GetMenuInfo {
        return OrderResponse.GetMenuInfo(uChefComponent.getMenuInfo(menuCode))
    }
}