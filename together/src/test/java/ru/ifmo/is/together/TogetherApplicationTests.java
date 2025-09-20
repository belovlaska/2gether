package ru.ifmo.is.together;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("TogetherApplication")
public class TogetherApplicationTests {

  transient MockedStatic<SpringApplication> springApplicationMock;

  @BeforeAll
  void beforeAll() {
    springApplicationMock = mockStatic(SpringApplication.class);
  }

  @Test
  void contextLoads() {
    springApplicationMock
      .when(() -> SpringApplication.run(TogetherApplication.class, new String[] {}))
      .thenCallRealMethod();

    TogetherApplication.main(new String[] {});

    springApplicationMock.verify(() -> SpringApplication.run(TogetherApplication.class, new String[] {}), times(1));
  }
}
