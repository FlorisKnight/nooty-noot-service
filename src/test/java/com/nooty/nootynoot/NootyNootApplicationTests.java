package com.nooty.nootynoot;

import com.nooty.nootynoot.models.Noot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(NootController.class)
class NootyNootApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private NootRepo nootRepo;

	@BeforeEach
	public void init() {
		Noot n = new Noot();
		given(this.nootRepo.findById("1")).willReturn(Optional.of(n));
	}

	@Test
	void contextLoads() {
	}

}
