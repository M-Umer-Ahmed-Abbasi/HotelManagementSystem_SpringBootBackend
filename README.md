# Hotel Management System - Spring Boot Backend

A complete Spring Boot REST API for a hotel booking platform (similar to Airbnb) with JWT authentication, role-based access control, and comprehensive hotel management features.

## Team Members
- **Muhammad Umer Ahmed Abbasi** (22K-4599) - m.umer.ahmed.abbasi@gmail.com
- **Talha Asim** (22K-4589) - talha19308@gmail.com

## Features
- User registration and JWT authentication
- Role-based access control (ADMIN, OWNER, STAFF, GUEST)
- Hotel management with search and filters
- Room types and room management
- Day-based availability and dynamic pricing
- Reservation system with double-booking prevention
- Mock payment processing with refunds
- Guest reviews after checkout
- Promotions and discount codes
- Revenue reporting for admin and owners

## Tech Stack
- Spring Boot 3.2
- Spring Security with JWT
- Spring Data JPA / Hibernate
- H2 Database (development) / PostgreSQL (production)
- Lombok
- Springdoc OpenAPI (Swagger)

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+

### Running the Application
```bash
cd ESD_Project_springboot
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### API Documentation
Access Swagger UI: `http://localhost:8080/swagger-ui.html`

### H2 Console (Development)
Access H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:hoteldb`
- Username: `sa`
- Password: (empty)

## API Endpoints Overview

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | Get current user profile |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user profile |

### Hotels
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/hotels` | Create hotel (OWNER) |
| GET | `/api/hotels` | Search hotels with filters |
| GET | `/api/hotels/{id}` | Get hotel details |
| PUT | `/api/hotels/{id}` | Update hotel |
| DELETE | `/api/hotels/{id}` | Delete hotel |

### Rooms
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/hotels/{id}/room-types` | Create room type |
| POST | `/api/room-types/{id}/rooms` | Create room |
| GET | `/api/hotels/{id}/rooms` | Get rooms by hotel |
| PUT | `/api/rooms/{id}` | Update room |

### Reservations
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/reservations` | Create reservation |
| GET | `/api/reservations/{id}` | Get reservation details |
| PUT | `/api/reservations/{id}/cancel` | Cancel reservation |

### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments` | Process payment |
| GET | `/api/payments/{id}` | Get payment details |
| POST | `/api/payments/{id}/refund` | Refund payment |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/users` | List all users |
| GET | `/api/admin/reports/revenue` | Revenue reports |

## Project Structure
```
src/main/java/com/esd/hotelmanagement/
├── config/           # Configuration classes
├── controller/       # REST Controllers
├── dto/              # Data Transfer Objects
├── entity/           # JPA Entities (18 entities)
├── exception/        # Custom exceptions
├── repository/       # Data Access Layer
├── security/         # JWT & Spring Security
├── service/          # Business Logic
└── HotelManagementApplication.java
```

## Task Division

### Umer's Tasks
- Project setup and configuration
- Core entities (User, Hotel, Room, RoomType, Availability, Amenity)
- Security layer (JWT, authentication)
- Services: AuthService, UserService, HotelService, RoomService, AvailabilityService, AmenityService
- Controllers: AuthController, UserController, HotelController, RoomController, AvailabilityController, AmenityController
- Exception handling

### Talha's Tasks
- Booking entities (Reservation, Payment, Review, Promotion, Staff)
- Services: ReservationService, PaymentService, ReviewService, PromotionService, StaffService, ReportService
- Controllers: ReservationController, PaymentController, ReviewController, PromotionController, AdminController, StaffController
- API documentation

## License
This project is for educational purposes as part of ESD course project.
