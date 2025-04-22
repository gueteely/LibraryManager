### **Приложение для управление библиотекой с использованием микросервисной архитектуры**

***Для запуска программы используйте***
```bash 
docker-compose build
docker-compose up -d
```

***Регистрация обычного пользователя:***
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "password123",
    "fullName": "Test User",
    "birthDate": "1990-01-01"
  }'
  ```
***Регистрация администратора:***
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin1",
    "password": "admin123",
    "fullName": "Admin User",
    "birthDate": "1985-05-05"
  }'
  ```
После регистрации администратора нужно вручную изменить его роль в базе данных H2
1. Откройте консоль H2 по адресу: http://localhost:8081/h2-console
2. Подключитесь к базе данных с параметрами из application.properties:

* JDBC URL: jdbc:h2:mem:authdb

* UserName: sa

* Password: 
(оставить пустым)

3. Выполните SQL-запрос:
```SQL 
UPDATE APP_USER SET ROLE = 'ADMIN' WHERE USERNAME = 'admin1';
```

***Авторизация пользователей:***
```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{
"username": "user1",
"password": "password123"
}'
```
Сохраните полученный токен для использования в последующих запросах. Он будет выглядеть примерно так:
```json
{
"token": "eyJhbGciOiJIUzI1NiJ9...",
"userId": 1
}
```
***Авторизация администратора:***
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin1",
    "password": "admin123"
  }'
```
Сохраните токен администратора для доступа к функциям администрирования.

**Работа с книгами**

***Поиск книг (публичный доступ)***
```bash
curl -X GET "http://localhost:8080/books?author=Tolkien&title=Hobbit&year=1937"
```
***Добавление книги (требуется авторизация)***
```bash
curl -X POST http://localhost:8080/books \
  -H "Authorization: Bearer <токен пользователя>" \
  -H "Content-Type: application/json" \
  -d '{
    "author": "J.R.R. Tolkien",
    "title": "The Hobbit",
    "description": "Fantasy adventure novel",
    "publicationYear": 1937,
    "totalQuantity": 5,
    "availableQuantity": 5
  }'
```
***Обновление информации о книге***
```bash
curl -X PUT http://localhost:8080/books/1 \
  -H "Authorization: Bearer <токен пользователя>" \
  -H "Content-Type: application/json" \
  -d '{
    "author": "J.R.R. Tolkien",
    "title": "The Hobbit, or There and Back Again",
    "description": "Fantasy adventure novel for children",
    "publicationYear": 1937,
    "totalQuantity": 5,
    "availableQuantity": 5
  }'
```
***Получение книги***
```bash
curl -X POST http://localhost:8080/books/1/borrow \
  -H "Authorization: Bearer <токен пользователя>" \
  -d "plannedReturnDate=2025-05-20"
  ```
***Возврат книги***
```bash
curl -X POST http://localhost:8080/books/1/return \
  -H "Authorization: Bearer <токен пользователя>"
```

***Список книг, которые сейчас на руках***
```bash
curl -X GET http://localhost:8080/books/borrowed \
  -H "Authorization: Bearer <токен пользователя>"
```

***Список книг, у конкретного пользователя***
```bash
curl -X GET http://localhost:8080/books/borrowed/user \
-H "Authorization: Bearer <токен пользователя>"
```

***Список просроченных книг***
```bash
curl -X GET http://localhost:8080/books/overdue \
  -H "Authorization: Bearer <токен пользователя>"
```
**Проверка логов (Доступ только у администратора)**

***Получение всех логов***
```bash
curl -X GET http://localhost:8080/logs/all \
  -H "Authorization: Bearer <токен администратора>"
```
***Логи конкретного пользователя***
```bash
curl -X GET http://localhost:8080/logs/user/1 \
  -H "Authorization: Bearer <токен администратора>"
```
***Логи за период***
```bash
curl -X GET "http://localhost:8080/logs/period?from=2025-01-01T00:00:00&to=2025-12-31T23:59:59" \
  -H "Authorization: Bearer <токен администратора>"
```


Для проверки работы с БД можно воспользоваться консолями H2 для каждого сервиса:

* Auth Service: http://localhost:8081/h2-console
  
  JDBC URL: **jdbc:h2:mem:authdb**
  
  UserName: **sa**
  
  Password: (оставить пустым)
  
* Book Service: http://localhost:8082/h2-console
  
  JDBC URL: **jdbc:h2:file:/data/librarydb**
  
  UserName: (оставить пустым)
  
  Password: (оставить пустым)
  
* Logging Service: http://localhost:8083/h2-console
  
  JDBC URL: **jdbc:h2:mem:logsdb**
  
  UserName: **sa**
  
  Password: (оставить пустым)

Для проверки работы Eureka Server и зарегистрированных сервисов перейдите по адресу:
http://localhost:8761

Вы должны увидеть список всех запущенных микросервисов.

### Тестирование приложения

Тесты находятся по пути (запуск через Docker или напрямую в IDE):

***book-service\src\test\java\com\library\eureka\application\bookservice\BookServiceTest.java***
