# SmartShop API - Business Logic Completion & Testing Guide

## Summary of Additions

### 1. **PromoCode Management Module** (NEW)
Complete CRUD operations for managing promotional discount codes.

#### Files Created:
- `PromoCodeController.java` - REST API endpoints
- `PromoCodeService.java` & `PromoCodeServiceImpl.java` - Business logic
- `PromoCodeMapper.java` - Entity to DTO mapping
- `PromoCodeDTO.java` - Data transfer object
- `PromoCodeCreateRequest.java` - Create request DTO
- `PromoCodeUpdateRequest.java` - Update request DTO

#### Repository Enhanced:
- `PromoCodeRepository.java` - Added query methods

#### Endpoints:
- `POST /api/promocodes` - Create promo code
- `GET /api/promocodes` - Get all promo codes (with activeOnly filter)
- `GET /api/promocodes/{id}` - Get promo code by ID
- `GET /api/promocodes/code/{code}` - Get promo code by code
- `PUT /api/promocodes/{id}` - Update promo code
- `PUT /api/promocodes/{id}/activate` - Activate promo code
- `PUT /api/promocodes/{id}/deactivate` - Deactivate promo code
- `DELETE /api/promocodes/{id}` - Delete promo code

### 2. **Payment Module Enhancements**
Added missing GET endpoints for payment retrieval.

#### Service Methods Added:
- `getPaymentById(Long id)` - Get single payment
- `getPaymentsByOrderId(Long orderId)` - Get all payments for an order
- `getAllPayments()` - Get all payments in system

#### Repository Enhanced:
- `PaymentRepository.java` - Added `findByOrderId(Long orderId)` method

#### Endpoints Added:
- `GET /api/payments` - Get all payments (with orderId filter)
- `GET /api/payments/{id}` - Get payment by ID

### 3. **Controller Enhancement**
- `PaymentController.java` - Completed confirm payment method implementation

---

## Complete API Structure

### Authentication
- POST `/api/auth/login` - Login
- POST `/api/auth/logout` - Logout
- GET `/api/auth/me` - Get current user

### Clients
- POST `/api/clients` - Create client
- GET `/api/clients` - Get all clients
- GET `/api/clients/{id}` - Get client by ID
- GET `/api/clients/email/{email}` - Get client by email
- GET `/api/clients/user/{userId}` - Get client by user ID
- PUT `/api/clients/{id}` - Update client
- DELETE `/api/clients/{id}` - Delete client
- GET `/api/clients/{id}/orders` - Get client orders

### Products
- POST `/api/products` - Create product
- GET `/api/products` - Get all products (paginated, searchable)
- GET `/api/products/{id}` - Get product by ID
- PUT `/api/products/{id}` - Update product
- DELETE `/api/products/{id}` - Soft delete product
- POST `/api/products/{id}/restore` - Restore product

### Promo Codes (NEW)
- POST `/api/promocodes` - Create promo code
- GET `/api/promocodes` - Get all promo codes
- GET `/api/promocodes/{id}` - Get promo code by ID
- GET `/api/promocodes/code/{code}` - Get promo code by code
- PUT `/api/promocodes/{id}` - Update promo code
- PUT `/api/promocodes/{id}/activate` - Activate promo code
- PUT `/api/promocodes/{id}/deactivate` - Deactivate promo code
- DELETE `/api/promocodes/{id}` - Delete promo code

### Orders
- POST `/api/orders` - Create order
- GET `/api/orders` - Get all orders (with filters)
- GET `/api/orders/{id}` - Get order by ID
- PUT `/api/orders/{id}/confirm` - Confirm order
- PUT `/api/orders/{id}/cancel` - Cancel order
- PUT `/api/orders/{id}/reject` - Reject order

### Payments
- POST `/api/payments` - Create payment
- GET `/api/payments` - Get all payments (with order filter) (NEW)
- GET `/api/payments/{id}` - Get payment by ID (NEW)
- POST `/api/payments/confirm` - Process/confirm payment

---

## Business Logic Features

### 1. **Customer Tier System**
Automatic discounts based on client tier:
- BASIC: Standard pricing
- SILVER: Tier discount
- GOLD: Higher tier discount
- PLATINUM: Maximum tier discount

### 2. **Promo Code System**
- Percentage-based discounts
- Expiration date validation
- Active/inactive status
- Usage tracking
- Combinable with tier discounts

### 3. **Order Management**
- Multi-item orders
- Automatic calculation:
  - Subtotal
  - Tier discount
  - Promo code discount
  - Net amount
  - Tax (TVA)
  - Total amount
  - Remaining amount

### 4. **Payment Processing**
- Multiple payment methods: CASH, CHECK, TRANSFER
- Automatic processing for CASH payments
- Manual confirmation for CHECK and TRANSFER
- Partial payments support
- Automatic order status update when fully paid

### 5. **Product Inventory**
- Stock management
- Soft delete (products can be restored)
- Search and pagination

---

## Testing Instructions

### Step 1: Import Postman Collection
1. Open Postman
2. Click "Import" button
3. Select file: `SmartShop_Complete_Postman_Collection.json`
4. The collection will be imported with all endpoints organized in folders

### Step 2: Setup Collection Variables
The collection uses variables that auto-populate from responses:
- `baseUrl`: http://localhost:8080 (default)
- `sessionId`: Auto-filled after login
- `clientId`: Auto-filled after creating client
- `productId`: Auto-filled after creating product
- `orderId`: Auto-filled after creating order
- `paymentId`: Auto-filled after creating payment
- `promoCodeId`: Auto-filled after creating promo code

### Step 3: Complete Test Flow

#### 3.1 Authentication
1. Run "Login as Admin" (creates session)
2. Run "Get Current User" (verify session)

#### 3.2 Create Test Data
1. Run "Create Client" (saves clientId)
2. Run "Create Product" (saves productId)
3. Run "Create Promo Code" (saves promoCodeId)

#### 3.3 Test Order Flow
1. Run "Create Order" (with promo code)
2. Run "Get Order by ID" (verify calculations)
3. Run "Get Orders by Client"

#### 3.4 Test Payment Flow
1. Run "Create Payment (Cash)" - auto-processes
2. Run "Create Payment (Check)" - pending status
3. Run "Confirm/Process Payment" - process the check payment
4. Run "Get Payments by Order" - view all payments
5. Run "Get Order by ID" - verify remaining amount updated

#### 3.5 Test Promo Code Management
1. Run "Get Active Promo Codes"
2. Run "Update Promo Code"
3. Run "Deactivate Promo Code"
4. Run "Activate Promo Code"

### Step 4: Edge Case Testing
- Create order without promo code
- Try to process already processed payment (should fail)
- Try to use expired/inactive promo code (should fail)
- Create order with insufficient stock (should fail)
- Soft delete and restore product

---

## Business Rules Implemented

1. **Order Creation**
   - Must have at least one item
   - Product stock must be sufficient
   - Promo codes are validated (active, not expired)
   - Tier and promo discounts are applied
   - TVA is calculated on net amount

2. **Payment Processing**
   - CASH payments auto-process
   - CHECK and TRANSFER require manual confirmation
   - Cannot process already-processed payments
   - Remaining amount updates automatically
   - Order confirms automatically when fully paid

3. **Promo Codes**
   - Codes must be unique
   - Must have future expiration date
   - Can be activated/deactivated
   - Discount percent: 0.01% - 100%

4. **Product Management**
   - Soft delete (can be restored)
   - Stock validation on orders
   - Search by name/description

5. **Authorization**
   - Admin required for most operations
   - Session-based authentication
   - Proper logout cleanup

---

## Database Schema Features

### Key Entities:
- **User** - Authentication
- **Client** - B2B customers with tier system
- **Product** - Inventory with soft delete
- **Order** - Multi-item orders with calculations
- **OrderItem** - Individual order line items
- **Payment** - Multiple payment methods and statuses
- **PromoCode** - Discount codes with expiration
- **AppConfig** - System configuration (TVA, tier discounts)

### Relationships:
- Client → User (One-to-One)
- Order → Client (Many-to-One)
- Order → PromoCode (Many-to-One)
- Order → OrderItem (One-to-Many)
- Order → Payment (One-to-Many)
- OrderItem → Product (Many-to-One)

---

## Next Steps for Production

1. **Add User Management API** - Currently only clients can be created
2. **Add AppConfig API** - To manage TVA and tier discount percentages
3. **Add Reporting Endpoints** - Sales reports, payment summaries
4. **Add Pagination to Orders/Payments** - Currently returns all
5. **Add Order Search/Filters** - Date range, amount range, etc.
6. **Add Payment Receipt Generation** - PDF generation
7. **Add Inventory Alerts** - Low stock notifications
8. **Add Audit Logging** - Track all important changes

---

## Notes

- All endpoints require authentication (session cookie)
- Admin role required for most operations
- Response format is consistent: `{ "message", "data" }`
- Error handling is comprehensive with appropriate HTTP status codes
- All monetary calculations use BigDecimal for precision
- Timestamps are in LocalDateTime format

---

**Collection File:** `SmartShop_Complete_Postman_Collection.json`
**Last Updated:** November 28, 2025
