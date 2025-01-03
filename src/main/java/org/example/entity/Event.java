package org.example.entity;
import jakarta.persistence.*; //how obj matches to db tables
import lombok.AllArgsConstructor;
import lombok.Data;  // Generates getters, setters, equals, hashCode, toString
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;


@Entity
@Table(name="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

   @Id
   @GeneratedValue(strategy= GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String name;

   private String description;

   @Column(nullable = false)
   private LocalDateTime startTime;

   @Column(nullable = false)
   private LocalDateTime endTime;

   @ManyToOne
   @JoinColumn(name="venue_id")
   private Venue venue;

}
