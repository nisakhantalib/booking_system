# Event Management System Documentation

## Overview
The Event Management System is a Spring Boot application that allows users to manage events and venues. It provides both a web interface and a REST API for creating, reading, updating, and deleting (CRUD) events and venues.

## Technical Stack & Core Concepts

### 1. Spring Boot Framework
Spring Boot is the backbone of our application, providing:
- Auto-configuration
- Standalone application capability
- Embedded server (Tomcat)
- Production-ready features

### 2. Frontend Technologies
- **Thymeleaf**: A modern server-side Java template engine
  - Natural templating - templates can be viewed in browsers
  - Integration with Spring MVC
  - Expression syntax: `${...}` for variables, `*{...}` for object properties
  - Example from our code:
    ```html
    <input type="text" th:field="*{name}" required>
    ```

- **Bootstrap 5**: Frontend framework for responsive design
  - Grid system for layout
  - Pre-built components
  - Utility classes

### 3. Database Layer
- **JPA (Java Persistence API)**
  - Standard specification for ORM in Java
  - Abstracts database operations
  - Provides annotations for entity mapping

- **Hibernate**
  - JPA implementation
  - Handles database operations
  - Manages entity relationships

### 4. Build Tool
- **Maven**
  - Dependency management
  - Project building
  - Standard project structure

### 5. Architecture
- **MVC Pattern**
  - Model: Data and business logic
  - View: User interface (Thymeleaf templates)
  - Controller: Request handling and flow control

## Key Features
1. Event Management
   - Create, view, edit, and delete events
   - Associate events with venues
   - Search events by time range
   - View all events in a list format

2. Venue Management
   - Create, view, edit, and delete venues
   - Track venue capacity
   - View events scheduled at each venue
   - Search venues by minimum capacity

## Project Structure

### 1. Domain Models (Entities)

#### Event Entity
```java
@Entity
@Table(name="events")
public class Event {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @ManyToOne
    private Venue venue;
}
```

#### Venue Entity
```java
@Entity
@Table(name="venues")
public class Venue {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String address;
    private Integer capacity;
    @OneToMany(mappedBy="venue")
    private List<Event> events;
}
```

## Deep Dive into Core Concepts

### 1. Transaction Management (@Transactional)

#### What is @Transactional?
`@Transactional` is a crucial Spring annotation that manages database transactions. A transaction ensures that a series of database operations either all succeed or all fail together, maintaining data consistency.

```java
@Service
@Transactional
public class EventService {
    // Service methods
}
```

#### Why Use @Transactional?
1. **Data Integrity**
   - Ensures ACID properties:
     - Atomicity: All operations complete or none do
     - Consistency: Database remains in valid state
     - Isolation: Transactions don't interfere
     - Durability: Changes are permanent

2. **Error Handling**
   ```java
   @Transactional
   public Event updateEvent(Long id, Event event) {
       Event existingEvent = getEventById(id);  // If this fails
       event.setId(existingEvent.getId());
       return eventRepository.save(event);      // This won't execute
   }
   ```
   - If any operation fails, all changes are rolled back
   - Prevents partial updates


### 2. JPA Entity Relationships

#### @ManyToOne Relationship
In our Event-Venue relationship:
```java
@Entity
public class Event {
    @ManyToOne
    @JoinColumn(name="venue_id")
    private Venue venue;
}
```

Why this design?
1. **Natural Modeling**: Many events can occur at one venue
2. **Database Structure**: Creates foreign key in events table
3. **Performance Considerations**: Efficient querying of events by venue

#### @OneToMany Relationship
```java
@Entity
public class Venue {
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    private List<Event> events;
}
```

Important aspects:
1. **mappedBy**: 
   - Indicates bidirectional relationship
   - Prevents duplicate foreign keys
   - References the field name in Event class

2. **CascadeType.ALL**:
   - Propagates operations to related events
   - When you delete a venue, related events are deleted
   - Operations cascade: PERSIST, MERGE, REMOVE, REFRESH, DETACH

### 3. Controllers: Web vs REST

#### Web Controllers (ViewController)

#### REST Controllers
- `EventController`: Handles API requests at `/api/events`
- `VenueController`: Handles API requests at `/api/venues`

Example Endpoints:
```java
// Event endpoints
GET    /api/events           // List all events
POST   /api/events           // Create new event
GET    /api/events/{id}      // Get specific event
PUT    /api/events/{id}      // Update event
DELETE /api/events/{id}      // Delete event
GET    /api/events/search    // Search events by time range

// Venue endpoints
GET    /api/venues           // List all venues
POST   /api/venues           // Create new venue
GET    /api/venues/{id}      // Get specific venue
PUT    /api/venues/{id}      // Update venue
DELETE /api/venues/{id}      // Delete venue
GET    /api/venues/search    // Search venues by capacity
```

#### View Controllers
- `EventViewController`: Handles web pages at `/events`
- `VenueViewController`: Handles web pages at `/venues`
- `HomeController`: Handles the home page at `/`

### 3. Services
Services contain the business logic and act as an intermediary between controllers and repositories:

- `EventService`: Handles event-related business logic
- `VenueService`: Handles venue-related business logic

Key features:
- Transaction management (`@Transactional`)
- Data validation
- Error handling
- Business rule implementation

### 4. Repositories
JPA repositories for database operations:

- `EventRepository`: Extends JpaRepository for Event entity
- `VenueRepository`: Extends JpaRepository for Venue entity

Custom queries:
```java
// Event queries
List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
List<Event> findByVenueId(Long venueId);

// Venue queries
List<Venue> findByCapacityGreaterThanEqual(Integer capacity);
```

### 5. Views (Thymeleaf Templates)
The application uses Thymeleaf templates with Bootstrap 5 for the web interface:

#### Event Views
- `events/list.html`: Displays all events in a table
- `events/add.html`: Form to create new events
- `events/edit.html`: Form to edit existing events

#### Venue Views
- `venues/list.html`: Displays all venues in a table
- `venues/add.html`: Form to create new venues
- `venues/edit.html`: Form to edit existing venues

#### Home View
- `home.html`: Landing page with navigation to events and venues

## Getting Started

1. Clone the repository
2. Configure your database settings in `application.properties`
3. Run `mvn spring-boot:run` to start the application
4. Access the web interface at `http://localhost:8080`


## Common Tasks

### Adding a New Event
1. Navigate to `/events`
2. Click "Add New Event"
3. Fill in the required fields:
   - Event name
   - Description (optional)
   - Start time
   - End time
   - Select a venue
4. Click "Create Event"

### Managing Venues
1. Navigate to `/venues`
2. View all venues in the table
3. Use the "Add New Venue" button to create venues
4. Edit or delete venues using the action buttons




## Future Improvements
1. Add user authentication and authorization
2. Implement event categories
3. Add image upload for venues
4. Create a booking system for events
5. Add email notifications
6. Implement a calendar view
