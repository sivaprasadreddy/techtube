package com.sivalabs.techtube

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.assertj.MockMvcTester

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestcontainersConfig::class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
abstract class BaseIT {
    @Autowired lateinit var mockMvcTester: MockMvcTester
}
