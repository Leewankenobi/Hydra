package com.example.hydra.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

  private final JavaMailSender mailSender;
  private final String from;

  public NotificationService(
      JavaMailSender mailSender,
      @Value("${app.mail.from}") String from) {
    this.mailSender = mailSender;
    this.from = from;
  }

  @Async
  public void sendLowMoistureEmail(
      List<String> recipients,
      String plantNickname,
      int currentPercent,
      int minPercent,
      OffsetDateTime recordedAt) {

    if (recipients == null || recipients.isEmpty()) {
      return;
    }

    String subject = "Hydra: " + plantNickname + " needs water";
    String body = plantNickname + " is below its minimum moisture level.\n"
        + "Current moisture: " + currentPercent + "%\n"
        + "Minimum: " + minPercent + "%\n"
        + "Recorded: " + recordedAt;

    for (String recipient : recipients) {
      if (recipient == null || recipient.isBlank()) {
        continue;
      }

      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(from);
      message.setTo(recipient);
      message.setSubject(subject);
      message.setText(body);

      try {
        mailSender.send(message);
        log.info("Sent low-moisture email to {}", recipient);
      } catch (MailException e) {
        log.error("Could not send low-moisture email to {}", recipient, e);
      }
    }
  }
}
