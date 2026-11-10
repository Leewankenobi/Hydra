package com.example.hydra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private JavaMailSender mailSender;

  private NotificationService notificationService;

  @BeforeEach
  void setUp() {
    notificationService = new NotificationService(mailSender, "from@example.com");
  }

  @Test
  void sendsOneEmailPerRecipient() {
    notificationService.sendLowMoistureEmail(
        List.of("a@example.com", "b@example.com"),
        "Fern",
        28,
        30,
        OffsetDateTime.now()
    );

    ArgumentCaptor<SimpleMailMessage> captor =
        ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender, times(2)).send(captor.capture());

    List<SimpleMailMessage> messages = captor.getAllValues();
    assertThat(messages).hasSize(2);
    assertThat(messages.get(0).getTo()).containsExactly("a@example.com");
    assertThat(messages.get(1).getTo()).containsExactly("b@example.com");
    assertThat(messages).allSatisfy(message -> {
      assertThat(message.getFrom()).isEqualTo("from@example.com");
      assertThat(message.getSubject()).isEqualTo("Hydra: Fern needs water");
      assertThat(message.getText())
          .contains("Fern")
          .contains("Current moisture: 28%")
          .contains("Minimum: 30%")
          .contains("Recorded:");
    });
  }

  @Test
  void skipsBlankRecipients() {
    notificationService.sendLowMoistureEmail(
        List.of("", "a@example.com", "   "),
        "Fern",
        28,
        30,
        OffsetDateTime.now()
    );

    ArgumentCaptor<SimpleMailMessage> captor =
        ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender).send(captor.capture());

    assertThat(captor.getAllValues()).hasSize(1);
    assertThat(captor.getValue().getTo()).containsExactly("a@example.com");
  }

  @Test
  void mailFailureDoesNotEscape() {
    doThrow(new MailSendException("smtp down"))
        .when(mailSender).send(any(SimpleMailMessage.class));

    assertThatCode(() -> notificationService.sendLowMoistureEmail(
        List.of("a@example.com"),
        "Fern",
        28,
        30,
        OffsetDateTime.now()
    )).doesNotThrowAnyException();

    verify(mailSender).send(any(SimpleMailMessage.class));
  }
}
