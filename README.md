## 1. Client
- id
- name
- email
- phone
- List\<Order\>
## 2. Order
- id
- createdAt
- status
- client
- driver
- vehicle
- route
- List\<Cargo\>
## 3. Cargo
- id
- name
- weight
- volume
- order
## 4. Vehicle
- id
- plateNumber
- model
- capacity
## 5. Driver
- id
- name
- licenseNumber
- experienceYears
## 6. Route
- id
- origin
- destination
- distance
- estimatedTime
## Реализовано:
- Spring Boot приложение
- REST API
- GET endpoint с @PathVariable
- GET endpoint с @RequestParam
- DTO + Mapper
- Слои Controller → Service → Repository
- Checkstyle
## SonarCloud
https://sonarcloud.io/project/issues?issueStatuses=OPEN%2CCONFIRMED&id=maximkozhin24_transproject&open=AZyZoDWzChjh1G8EauOA
