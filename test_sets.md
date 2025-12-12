# Hotel Management System - API Test Sets

This document contains test data and expected results for testing all API endpoints via Swagger UI.

---

## 1. Authentication Tests

### Test 1.1: Register OWNER User
**Endpoint:** `POST /api/auth/register`
```json
{
  "email": "owner@hotel.com",
  "password": "owner123",
  "firstName": "John",
  "lastName": "Owner",
  "phone": "+1234567890",
  "role": "OWNER"
}
```
**Expected:** `201 Created` with JWT token

---

### Test 1.2: Register GUEST User
**Endpoint:** `POST /api/auth/register`
```json
{
  "email": "guest@example.com",
  "password": "guest123",
  "firstName": "Jane",
  "lastName": "Guest",
  "phone": "+0987654321",
  "role": "GUEST"
}
```
**Expected:** `201 Created` with JWT token

---

### Test 1.3: Register ADMIN User
**Endpoint:** `POST /api/auth/register`
```json
{
  "email": "admin@hotel.com",
  "password": "admin123",
  "firstName": "Admin",
  "lastName": "User",
  "phone": "+1111111111",
  "role": "ADMIN"
}
```
**Expected:** `201 Created` with JWT token

---

### Test 1.4: Login User
**Endpoint:** `POST /api/auth/login`
```json
{
  "email": "owner@hotel.com",
  "password": "owner123"
}
```
**Expected:** `200 OK` with JWT token

---

### Test 1.5: Login with Invalid Credentials
**Endpoint:** `POST /api/auth/login`
```json
{
  "email": "owner@hotel.com",
  "password": "wrongpassword"
}
```
**Expected:** `401 Unauthorized`

---

## 2. Hotel Management Tests (Requires OWNER Authorization)

> **Note:** Copy the JWT token from login response and authorize in Swagger first!

### Test 2.1: Create Hotel
**Endpoint:** `POST /api/hotels`
**Authorization:** Bearer Token (OWNER)
```json
{
  "name": "Grand Plaza Hotel",
  "description": "A luxury 5-star hotel in the heart of the city with stunning views",
  "starRating": 5,
  "location": {
    "address": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "country": "USA",
    "zipCode": "10001"
  },
  "amenityIds": [],
  "cancellationPolicy": {
    "name": "Standard",
    "refundPercentage": 100,
    "daysBeforeCheckIn": 7
  }
}
```
**Expected:** `201 Created` with hotel details (save the `id` for next tests)

---

### Test 2.2: Create Second Hotel
**Endpoint:** `POST /api/hotels`
**Authorization:** Bearer Token (OWNER)
```json
{
  "name": "Beach Resort Paradise",
  "description": "A beautiful beachfront resort with private beach access",
  "starRating": 4,
  "location": {
    "address": "456 Ocean Drive",
    "city": "Miami",
    "state": "FL",
    "country": "USA",
    "zipCode": "33139"
  },
  "amenityIds": [],
  "cancellationPolicy": {
    "name": "Flexible",
    "refundPercentage": 75,
    "daysBeforeCheckIn": 3
  }
}
```
**Expected:** `201 Created`

---

### Test 2.3: Search Hotels
**Endpoint:** `GET /api/hotels`
**Parameters:**
- `city`: New York
- `minRating`: 4
- `page`: 0
- `size`: 10
- `sort`: (leave empty or use `name`, `starRating`, `createdAt`)

**Expected:** `200 OK` with list of matching hotels

---

### Test 2.4: Get Hotel by ID
**Endpoint:** `GET /api/hotels/{id}`
**Parameters:**
- `id`: 1 (use ID from Test 2.1)

**Expected:** `200 OK` with hotel details

---

### Test 2.5: Get My Hotels (Owner's hotels)
**Endpoint:** `GET /api/hotels/my-hotels`
**Authorization:** Bearer Token (OWNER)

**Expected:** `200 OK` with list of owner's hotels

---

## 3. Room Type Tests (Requires OWNER Authorization)

### Test 3.1: Create Room Type - Deluxe Suite
**Endpoint:** `POST /api/hotels/{hotelId}/room-types`
**Authorization:** Bearer Token (OWNER)
**Parameters:**
- `hotelId`: 1
```json
{
  "name": "Deluxe Suite",
  "description": "Spacious suite with city views, king bed, and living area",
  "basePrice": 299.99,
  "maxOccupancy": 4
}
```
**Expected:** `201 Created` (save the room type `id`)

---

### Test 3.2: Create Room Type - Standard Room
**Endpoint:** `POST /api/hotels/{hotelId}/room-types`
**Authorization:** Bearer Token (OWNER)
**Parameters:**
- `hotelId`: 1
```json
{
  "name": "Standard Room",
  "description": "Comfortable room with queen bed and work desk",
  "basePrice": 149.99,
  "maxOccupancy": 2
}
```
**Expected:** `201 Created`

---

### Test 3.3: Get Room Types for Hotel
**Endpoint:** `GET /api/hotels/{hotelId}/room-types`
**Parameters:**
- `hotelId`: 1

**Expected:** `200 OK` with list of room types

---

## 4. Room Tests (Requires OWNER Authorization)

### Test 4.1: Create Room 101
**Endpoint:** `POST /api/room-types/{roomTypeId}/rooms`
**Authorization:** Bearer Token (OWNER)
**Parameters:**
- `roomTypeId`: 1 (Deluxe Suite ID from Test 3.1)
```json
{
  "roomNumber": "101",
  "floor": 1,
  "status": "AVAILABLE"
}
```
**Expected:** `201 Created` (save room `id` for reservations)

---

### Test 4.2: Create Room 102
**Endpoint:** `POST /api/room-types/{roomTypeId}/rooms`
**Authorization:** Bearer Token (OWNER)
**Parameters:**
- `roomTypeId`: 1
```json
{
  "roomNumber": "102",
  "floor": 1,
  "status": "AVAILABLE"
}
```
**Expected:** `201 Created`

---

### Test 4.3: Create Room 201 (Standard)
**Endpoint:** `POST /api/room-types/{roomTypeId}/rooms`
**Authorization:** Bearer Token (OWNER)
**Parameters:**
- `roomTypeId`: 2 (Standard Room ID from Test 3.2)
```json
{
  "roomNumber": "201",
  "floor": 2,
  "status": "AVAILABLE"
}
```
**Expected:** `201 Created`

---

### Test 4.4: Get Rooms by Hotel
**Endpoint:** `GET /api/hotels/{hotelId}/rooms`
**Parameters:**
- `hotelId`: 1

**Expected:** `200 OK` with list of all rooms

---

### Test 4.5: Update Room Status to MAINTENANCE
**Endpoint:** `PUT /api/rooms/{id}/status`
**Authorization:** Bearer Token (OWNER)
**Parameters:**
- `id`: 2
- `status`: MAINTENANCE

**Expected:** `200 OK` with updated room

---

## 5. Reservation Tests (Requires GUEST Authorization)

> **Important:** Login as GUEST user first and use their JWT token!

### Test 5.1: Create Reservation
**Endpoint:** `POST /api/reservations`
**Authorization:** Bearer Token (GUEST)
```json
{
  "roomId": 1,
  "checkInDate": "2025-12-20",
  "checkOutDate": "2025-12-25",
  "guestCount": 2,
  "promoCode": null
}
```
**Expected:** `201 Created` with reservation details (save `id`)

---

### Test 5.2: Get My Reservations
**Endpoint:** `GET /api/reservations`
**Authorization:** Bearer Token (GUEST)

**Expected:** `200 OK` with list of guest's reservations

---

### Test 5.3: Get Reservation by ID
**Endpoint:** `GET /api/reservations/{id}`
**Authorization:** Bearer Token (GUEST)
**Parameters:**
- `id`: 1

**Expected:** `200 OK` with reservation details

---

### Test 5.4: Confirm Reservation (Owner Only)
**Endpoint:** `PUT /api/reservations/{id}/confirm`
**Authorization:** Bearer Token (OWNER)
**Parameters:**
- `id`: 1

**Expected:** `200 OK` with confirmed reservation

---

### Test 5.5: Cancel Reservation
**Endpoint:** `PUT /api/reservations/{id}/cancel`
**Authorization:** Bearer Token (GUEST)
**Parameters:**
- `id`: 1

**Expected:** `200 OK` with cancelled reservation

---

## 6. Availability Tests

### Test 6.1: Check Room Availability
**Endpoint:** `GET /api/hotels/{hotelId}/availability`
**Parameters:**
- `hotelId`: 1
- `checkIn`: 2025-12-20
- `checkOut`: 2025-12-25

**Expected:** `200 OK` with available rooms

---

## 7. Review Tests (Requires GUEST Authorization)

> **Note:** Reviews require a completed reservation. The reservation must be for a hotel stay that has ended.

### Test 7.1: Add Review for Reservation
**Endpoint:** `POST /api/reservations/{reservationId}/review`
**Authorization:** Bearer Token (GUEST)
**Parameters:**
- `reservationId`: 1 (use reservation ID from Test 5.1)
```json
{
  "rating": 5,
  "comment": "Excellent stay! The staff was very friendly and the room was spotless."
}
```
**Expected:** `201 Created`

---

### Test 7.2: Get Reviews for Hotel
**Endpoint:** `GET /api/hotels/{hotelId}/reviews`
**Parameters:**
- `hotelId`: 1

**Expected:** `200 OK` with list of reviews

---

### Test 7.3: Get Average Rating for Hotel
**Endpoint:** `GET /api/hotels/{hotelId}/rating`
**Parameters:**
- `hotelId`: 1

**Expected:** `200 OK` with average rating number

---

## 8. User Profile Tests

### Test 8.1: Get Current User Profile
**Endpoint:** `GET /api/users/me`
**Authorization:** Bearer Token (any user)

**Expected:** `200 OK` with user profile

---

### Test 8.2: Update User Profile
**Endpoint:** `PUT /api/users/{id}`
**Authorization:** Bearer Token
**Parameters:**
- `id`: 1
```json
{
  "firstName": "John",
  "lastName": "Owner Updated",
  "phone": "+9999999999"
}
```
**Expected:** `200 OK` with updated profile

---

## Quick Reference: User Credentials

| Role  | Email             | Password   |
|-------|-------------------|------------|
| OWNER | owner@hotel.com   | owner123   |
| GUEST | guest@example.com | guest123   |
| ADMIN | admin@hotel.com   | admin123   |

---

## Room Status Values
- `AVAILABLE`
- `OCCUPIED`
- `MAINTENANCE`
- `CLEANING`

---

## Valid Sort Fields for Hotel Search
- `name`
- `starRating`
- `createdAt`
- `id`

> ⚠️ **Important:** Leave the `sort` field empty or remove the default "string" value in Swagger!

---

## Testing Order (Recommended)
1. Register users (Test 1.1, 1.2, 1.3)
2. Login as OWNER (Test 1.4)
3. Authorize in Swagger with OWNER token
4. Create hotels (Test 2.1, 2.2)
5. Create room types (Test 3.1, 3.2)
6. Create rooms (Test 4.1, 4.2, 4.3)
7. Login as GUEST and authorize
8. Create reservation (Test 5.1)
9. Login as OWNER and confirm reservation (Test 5.4)
10. Add review as GUEST (Test 7.1)
