package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.room.dto.RoomDto
import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.domain.room.dto.RoomResponse
import com.midasit.mcafe.model.ControllerTest
import com.midasit.mcafe.model.RoomStatus
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.mockito.InjectMocks
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RoomControllerTest : ControllerTest() {

    private val roomService: RoomService = mockk()

    @InjectMocks
    private val roomController = RoomController(roomService)

    override fun getController(): Any {
        return roomController
    }

    init {
        given("방 생성을 위한 정보를 받아온다.") {
            val request = RoomRequest.Create("test", "test", RoomStatus.PUBLIC)
            val phone = "test"
            val roomDto = RoomDto(1, request.name, request.password, request.status)
            every { roomService.createRoom(any(), any()) } returns roomDto
            `when`("방 생성을 요청한다.") {
                val res = perform(
                    post("/room/create")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("방이 생성된다.") {
                    val response = res.response.contentAsString
                    val result = objectMapper.readValue(response, RoomResponse.Result::class.java)
                    result.id shouldBe roomDto.id
                    result.name shouldBe roomDto.name
                    result.password shouldBe roomDto.password
                    result.status shouldBe roomDto.status
                }
            }
        }
    }
}