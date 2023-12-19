package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.room.dto.RoomDto
import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.domain.room.dto.RoomResponse
import com.midasit.mcafe.model.ControllerTest
import com.midasit.mcafe.model.Room
import com.midasit.mcafe.model.RoomStatus
import com.midasit.mcafe.model.getRandomOf
import com.midasit.mcafe.model.getRandomSn
import com.midasit.mcafe.model.getRandomString
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.mockito.InjectMocks
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
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
        afterContainer {
            clearAllMocks()
        }

        given("방 생성을 위한 정보를 받아온다.") {
            val request = RoomRequest.Create(
                name = getRandomString(10),
                status = getRandomOf(RoomStatus.values()),
                password = getRandomString(10)
            )
            val roomDto = RoomDto.of(Room())
            every { roomService.createRoom(any(), any()) } answers { roomDto }
            `when`("방 생성을 요청한다.") {
                val res = perform(
                    post("/room")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("방이 생성된다.") {
                    val response = res.response.contentAsString
                    val create = objectMapper.readValue(response, RoomResponse.Create::class.java)
                    create.sn shouldBe roomDto.sn
                    create.name shouldBe roomDto.name
                    create.status shouldBe roomDto.status
                }
            }
        }

        given("방 Sn과 비밀번호가 주어지면") {
            val roomSn = 1L
            val request = RoomRequest.EnterRoom(getRandomString(10))
            every { roomService.enterRoom(any(), any(), any()) } answers { true }
            `when`("방 입장을 요청한다.") {
                val res = perform(
                    post("/room/enter/$roomSn")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("방에 입장한다.") {
                    val response = res.response.contentAsString
                    val enter = objectMapper.readValue(response, Boolean::class.java)
                    enter shouldBe true
                }
            }
        }

        given("방 목록을 조회하려고 하면.") {
            val roomDto = RoomDto.of(Room())
            every { roomService.getRoomList() } answers { listOf(roomDto) }
            `when`("방 목록을 조회 API를 요청시.") {
                val res = perform(
                    get("/room")
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("방 목록이 반환된다.") {
                    val response = res.response.contentAsString
                    val getRoomList = objectMapper.readValue(response, RoomResponse.GetRoomList::class.java)
                    getRoomList.roomList shouldBe listOf(roomDto)
                }
            }
        }

        given("방 Sn이 주어지면") {
            val roomSn = getRandomSn()
            val roomDto = RoomDto.of(Room())
            every { roomService.getRoomInfo(any(), any()) } answers {
                RoomResponse.GetRoomInfo(
                    roomDto,
                    listOf(),
                    listOf()
                )
            }
            `when`("방 정보를 조회 API를 요청시.") {
                val res = perform(
                    get("/room/$roomSn")
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("방 정보가 반환된다.") {
                    val response = res.response.contentAsString
                    val getRoomInfo = objectMapper.readValue(response, RoomResponse.GetRoomInfo::class.java)
                    getRoomInfo.room shouldBe roomDto
                }
            }
        }

        given("입장한 방 목록을 보고싶으면") {
            val roomDto = RoomDto.of(Room())
            every { roomService.getEnteredRoomList(any()) } answers { listOf(roomDto) }
            `when`("입장한 방 목록을 조회 API를 요청시.") {
                val res = perform(
                    get("/room/entered")
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("입장한 방 목록이 반환된다.") {
                    val response = res.response.contentAsString
                    val getRoomList = objectMapper.readValue(response, RoomResponse.GetRoomList::class.java)
                    getRoomList.roomList shouldBe listOf(roomDto)
                }
            }
        }

        given("방정보 주어지면") {
            val request = RoomRequest.UpdateRoom(
                getRandomSn(),
                getRandomString(10),
                getRandomOf(RoomStatus.values()),
                getRandomString(10)
            )
            every { roomService.updateRoom(any(), any(), any(), any(), any()) } answers { true }
            `when`("방정보를 수정하면") {
                val res = perform(
                    patch("/room")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("방정보가 수정된다.") {
                    val response = res.response.contentAsString
                    val update = objectMapper.readValue(response, Boolean::class.java)
                    update shouldBe true
                }
            }
        }

        given("방 Sn이 주어지면") {
            val roomSn = getRandomSn()
            every { roomService.exitRoom(any(), any()) } answers { true }
            `when`("방을 나가기 요청하면") {
                val res = perform(
                    delete("/room/exit/$roomSn")
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("방이 나가진다.") {
                    val response = res.response.contentAsString
                    val delete = objectMapper.readValue(response, Boolean::class.java)
                    delete shouldBe true
                }
            }
        }

        given("방 Sn이 주어지면") {
            val roomSn = getRandomSn()
            every { roomService.deleteRoom(any(), any()) } answers { true }
            `when`("방을 삭제하면") {
                val res = perform(
                    delete("/room/$roomSn")
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("방이 삭제된다.") {
                    val response = res.response.contentAsString
                    val delete = objectMapper.readValue(response, Boolean::class.java)
                    delete shouldBe true
                }
            }
        }
    }
}