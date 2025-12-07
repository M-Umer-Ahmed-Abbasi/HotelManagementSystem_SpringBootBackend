Hotel Management System – Backend Requirements (Markdown)
1. Project Summary
A backend-only Spring Boot REST API for a hotel booking platform similar to Airbnb. The system will support multiple user roles—Admin, Owner, Staff, Guest—and allow operations such as hotel registration, room management, searching, booking, payments, reviews, and financial reporting.

2. System Objectives


Allow secure registration, login, and role-based access using JWT.


Enable Owners to manage hotels, rooms, availability, staff, and policies.


Enable Guests to search hotels, check availability, create reservations, and make payments.


Provide Admin with system-wide oversight and reporting.


Maintain structured relationships between hotels, users, rooms, payments, and reservations.



3. User Roles &amp; Permissions
ADMIN


Manage all users and hotels


View system-wide revenue and reports


Create system-wide promotions


OWNER


Manage their hotels, rooms, staff


Confirm/cancel guest reservations


View hotel-specific revenue reports




          
            
          
        
  
        
    

STAFF


Assist owners in day-to-day management


Update room status, availability


Handle check-ins/outs (conceptually)


GUEST


Register, login


Search hotels and rooms


Make reservations


Submit payments


Write reviews after stay



4. Feature List
Authentication / User


Register as Guest or Owner


Login with JWT


Profile view/update


Role-based API protection




          
            
          
        
  
        
    

Hotel Management


Create/update/delete hotels


Add photos (URLs), amenities, policies


Associate a hotel with an Owner


Room &amp; Availability


Create room types (Deluxe, Standard, etc.)


Create individual rooms


Manage daily availability


Support dynamic pricing overrides


Mark room status (Available/Maintenance)


Hotel Search


Search hotels by city


Filter by price range, amenities, rating


Search by check-in/check-out dates


Search results must account for availability




          
            
          
        
  
        
    

Reservations


Create reservation


Validate availability


Confirm or cancel reservation


Prevent double-booking


Guest/Owner view of reservation details


Payments


Simulated/mock payment processing


Store payment details


Handle refunds (partial or full)


Maintain financial transaction logs


Staff Management


Owner creates staff accounts


Assign staff to hotels


Staff role-based access




          
            
          
        
  
        
    

Reviews


Guest reviews allowed after completed stay


Rating (1–5) + optional comment


Promotions &amp; Coupons


Admin creates promotions


Guests apply promo codes at booking


Validate date range + usage limits


Financial Reporting


Admin: system-wide revenue &amp; transaction summary


Owner: revenue per hotel


Filter by date range



5. Functional Requirements


Authentication


JWT authentication required for secured endpoints


User roles determine API access


User


Users can update their profile


Admin can view all users


Owner can view staff under their hotel


Hotel


Owner can manage only their own hotels


Admin can disable/enable any hotel




          
            
          
        
  
        
    

Room &amp; Availability


Rooms must belong to a RoomType


Availability must be day-based


System must prevent double-booking using DB-level checks or transactions


Search


Must support multi-criteria filters


Must only show hotels with availability


Reservations


Guest provides date range and guest count


Total price computed based on base price + overrides


Owners and guests can cancel under defined rules




Payments


Payment status must reflect transaction (SUCCESS/FAILED/REFUNDED)


Refund logic ties into reservation cancellation policy


Reviews


One review per reservation


Review only after checkout date


Promotions


DiscountType: PERCENTAGE or FIXED


System must validate code window + limits




          
            
          
        
  
        
    

Reports


Summaries include total revenue, total bookings, date filters



6. Non-Functional Requirements
Security


Passwords hashed using BCrypt


Token expiration and refresh logic


Access control using Spring Security


Performance


Search operations optimized with database-level indexing


API response time under normal load &lt; 500ms




Scalability


Must support growing hotels, rooms, reservations


All APIs stateless (via JWT)


Database


PostgreSQL


Schema normalized to at least 3NF


Flyway for all schema migrations


Architecture


Spring Boot layered architecture:
Controller → Service → Repository → Entity


Use DTOs for all requests &amp; responses


Global exception handling


Swagger/OpenAPI documentation



7. Database Entities (Minimum 14 Required, 18 Provided)


User


Role


Hotel


Location


RoomType


Room


Availability


Reservation


Payment


Promotion


Review


TransactionLog


Staff


Amenity


HotelAmenity (join table)


Photo


CancellationPolicy


LoyaltyAccount


These cover user management, hotel operations, booking workflow, transactions, and reporting.

8. Required API Endpoints (Overview)


          
            
          
        
  
        
    

Auth


POST /auth/register


POST /auth/login


Users


GET /users/{id}


PUT /users/{id}


Hotels


POST /hotels


GET /hotels (search + filters)


GET /hotels/{id}


PUT /hotels/{id}




Rooms


POST /hotels/{id}/room-types


POST /room-types/{id}/rooms


Availability


GET /hotels/{id}/availability


PUT /rooms/{id}/availability


Reservations


POST /reservations


GET /reservations/{id}


PUT /reservations/{id}/cancel




          
            
          
        
  
        
    

Payments


POST /payments


GET /payments/{id}


Reviews


POST /reservations/{id}/review


Promotions


POST /promotions


POST /promotions/apply




Admin


GET /admin/users


GET /admin/reports/revenue



9. Constraints &amp; Assumptions
Constraints


Backend-only project


Must use Spring Boot, Spring Security, JPA/Hibernate


PostgreSQL required


Migrations via Flyway


Entities must be normalized


GitHub collaboration required


Assumptions


Payment integration is mock only


Photos stored as URLs only


Availability is day-based (no hourly bookings)


Frontend is not included in this project