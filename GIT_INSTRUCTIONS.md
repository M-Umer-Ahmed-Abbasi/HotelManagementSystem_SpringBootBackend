# Git Instructions for Talha

The base project and Umer's tasks (Auth, Hotel, Room, User, Security) have been pushed to the `main` branch.

**Your Tasks to Push:**
1.  Staff Management
2.  Reviews
3.  Reservations
4.  Payments
5.  Promotions
6.  Admin Reports
7.  Documentation (Requirements, SRS, etc.)

## Step-by-Step Push Guide

1.  **Open your project directory in terminal.**
2.  **Pull the latest changes:**
    ```bash
    git pull origin main
    ```
3.  **Create a new branch for your work:**
    ```bash
    git checkout -b feature/talha-tasks
    ```
4.  **Add and Commit your files group by group:**

    **Group 1: Staff Management**
    ```bash
    git add src/main/java/com/esd/hotelmanagement/controller/StaffController.java src/main/java/com/esd/hotelmanagement/service/StaffService.java src/main/java/com/esd/hotelmanagement/repository/StaffRepository.java src/main/java/com/esd/hotelmanagement/entity/Staff.java src/main/java/com/esd/hotelmanagement/dto/Staff*.java
    git commit -m "implemented staff management features"
    ```

    **Group 2: Reviews**
    ```bash
    git add src/main/java/com/esd/hotelmanagement/controller/ReviewController.java src/main/java/com/esd/hotelmanagement/service/ReviewService.java src/main/java/com/esd/hotelmanagement/repository/ReviewRepository.java src/main/java/com/esd/hotelmanagement/entity/Review.java src/main/java/com/esd/hotelmanagement/dto/Review*.java
    git commit -m "added review and rating system"
    ```

    **Group 3: Reservations**
    ```bash
    git add src/main/java/com/esd/hotelmanagement/controller/ReservationController.java src/main/java/com/esd/hotelmanagement/service/ReservationService.java src/main/java/com/esd/hotelmanagement/repository/ReservationRepository.java src/main/java/com/esd/hotelmanagement/entity/Reservation*.java src/main/java/com/esd/hotelmanagement/dto/Reservation*.java
    git commit -m "implemented reservation and booking logic"
    ```

    **Group 4: Promotions**
    ```bash
    git add src/main/java/com/esd/hotelmanagement/controller/PromotionController.java src/main/java/com/esd/hotelmanagement/service/PromotionService.java src/main/java/com/esd/hotelmanagement/repository/PromotionRepository.java src/main/java/com/esd/hotelmanagement/entity/Promotion.java src/main/java/com/esd/hotelmanagement/entity/DiscountType.java src/main/java/com/esd/hotelmanagement/dto/Promotion*.java
    git commit -m "added promotion code management"
    ```

    **Group 5: Payments**
    ```bash
    git add src/main/java/com/esd/hotelmanagement/controller/PaymentController.java src/main/java/com/esd/hotelmanagement/service/PaymentService.java src/main/java/com/esd/hotelmanagement/repository/PaymentRepository.java src/main/java/com/esd/hotelmanagement/repository/TransactionLogRepository.java src/main/java/com/esd/hotelmanagement/entity/Payment*.java src/main/java/com/esd/hotelmanagement/entity/TransactionLog.java src/main/java/com/esd/hotelmanagement/dto/Payment*.java src/main/java/com/esd/hotelmanagement/exception/PaymentFailedException.java
    git commit -m "implemented payment processing and transaction logging"
    ```

    **Group 6: Admin**
    ```bash
    git add src/main/java/com/esd/hotelmanagement/controller/AdminController.java src/main/java/com/esd/hotelmanagement/service/ReportService.java src/main/java/com/esd/hotelmanagement/dto/RevenueReportResponse.java
    git commit -m "added admin dashboard and reporting features"
    ```

    **Group 7: Documentation**
    ```bash
    git add *.md
    git commit -m "added project documentation and requirements"
    ```

5.  **Push your branch:**
    ```bash
    git push origin feature/talha-tasks
    ```
6.  **Create a Pull Request** on GitHub to merge `feature/talha-tasks` into `main`.
