package com.midasit.mcafe.model

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.order.Order
import com.midasit.mcafe.domain.room.Room
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

fun Member(
    phone: String = getRandomPhone(),
    username: String = getRandomString(10),
    password: String = getRandomString(10),
    nickname: String = getRandomString(10),
    role: Role = getRandomOf(Role.values()),
    memberSn: Long = getRandomSn()
): Member {
    val member = Member(
        phone,
        username,
        password,
        nickname,
        role,
    )
    ReflectionTestUtils.setField(member, "sn", memberSn)
    return member
}

fun Room(
    name: String = getRandomString(10),
    password: String? = getRandomString(10),
    host: Member = Member(),
    status: RoomStatus = getRandomOf(RoomStatus.values()),
    roomSn: Long = getRandomSn()
): Room {
    val room = Room(
        name,
        password,
        host,
        status
    )
    ReflectionTestUtils.setField(room, "sn", roomSn)

    return room
}

fun Order(
    member: Member = Member(),
    room: Room = Room(),
    menuCode: String = getRandomString(10),
    quantity: Long = getRandomLong(100),
    orderSn: Long = getRandomSn()
): Order {
    val order = Order(
        getRandomOf(OrderStatus.values()),
        menuCode,
        member,
        room,
        quantity
    )
    ReflectionTestUtils.setField(order, "sn", orderSn)

    return order
}


fun getRandomString(length: Int): String {
    val random = Random()
    return if (random.nextBoolean()) getRandomStringByType(
        length,
        'a',
        'z'
    ) else getRandomStringByType(length, 'A', 'Z')
}

fun getRandomLong(limit: Int): Long {
    val random = Random()
    return Integer.valueOf(random.nextInt(limit)).toLong()
}

fun getRandomInteger(limit: Int): Int {
    val random = Random()
    return random.nextInt(limit)
}

fun getRandomSn(): Long {
    return getRandomLong(1000000)
}

fun getRandomBoolean(): Boolean {
    val random = Random()
    return random.nextBoolean()
}

fun getRandomPhone(): String {
    return String.format(
        "%s%s%s",
        "010",
        getRandomStringByType(4, '0', '9'),
        getRandomStringByType(4, '0', '9')
    )
}

fun <T> getRandomOf(list: Array<T>): T {
    return getRandomOf(listOf(*list))
}

fun <T> getRandomOf(list: List<T>): T {
    val index: Int = getRandomLong(list.size).toInt()
    return list[index]
}

private fun getRandomStringByType(length: Int, charFrom: Char, charTo: Char): String {
    return (Random().ints(charFrom.code, charTo.code + 1).limit(length.toLong()).collect(
        { StringBuilder() },
        { obj: java.lang.StringBuilder, codePoint: Int ->
            obj.appendCodePoint(
                codePoint
            )
        }
    ) { obj: java.lang.StringBuilder, s: java.lang.StringBuilder? ->
        obj.append(
            s
        )
    } as java.lang.StringBuilder).toString()
}