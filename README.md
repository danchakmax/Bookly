# 📚 Bookly — Android App

Додаток для обміну книгами між користувачами.

---

## 🚀 Як запустити проект

### 1. Відкрити в Android Studio
File → Open → вибрати папку `Bookly/`

### 2. Налаштувати Supabase
1. Зайдіть на [supabase.com](https://supabase.com) і створіть новий проект
2. У SQL Editor виконайте такий SQL:

```sql
CREATE TYPE role_t AS ENUM ('user','admin');
CREATE TYPE deal_type_t AS ENUM ('exchange','donation');

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  phone VARCHAR(20),
  password VARCHAR(255) NOT NULL,
  about TEXT,
  role role_t NOT NULL DEFAULT 'user',
  region VARCHAR(100),
  district VARCHAR(100),
  city VARCHAR(100),
  photo_url TEXT
);

CREATE TABLE posts (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id) ON DELETE CASCADE,
  title VARCHAR(50) NOT NULL,
  author VARCHAR(50),
  deal_type deal_type_t,
  description TEXT,
  photo_url TEXT
);

CREATE TABLE genres (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE books_genres (
  post_id INT REFERENCES posts(id) ON DELETE CASCADE,
  genre_id INT REFERENCES genres(id) ON DELETE CASCADE,
  PRIMARY KEY (post_id, genre_id)
);

CREATE TABLE complaints (
  id SERIAL PRIMARY KEY,
  text TEXT NOT NULL,
  date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  post_id INT REFERENCES posts(id) ON DELETE CASCADE,
  complainant_id INT REFERENCES users(id) ON DELETE CASCADE
);

-- Додати тестові жанри
INSERT INTO genres (name) VALUES
  ('Фантастика'),('Детектив'),('Романтика'),
  ('Дитяча література'),('Наукова'),('Пригоди'),
  ('Біографія'),('Поезія'),('Психологія'),('Інше');

-- Додати тестового адміна (пароль: admin123)
INSERT INTO users (name, email, password, role, city)
VALUES ('Адміністратор', 'admin@bookly.ua', 'admin123', 'admin', 'Львів');
```

3. У Supabase: Settings → API → скопіюйте:
   - **Project URL** (виглядає як `https://xxxxx.supabase.co`)
   - **anon public key**

4. Відкрийте файл:
   `app/src/main/java/com/example/bookly/data/api/RetrofitClient.java`

   Замініть:
   ```java
   public static final String BASE_URL = "https://YOUR_PROJECT_ID.supabase.co/rest/v1/";
   public static final String API_KEY  = "YOUR_SUPABASE_ANON_KEY";
   ```

5. У Supabase: Authentication → Policies → для кожної таблиці додайте RLS policy:
   - **Enable RLS** для кожної таблиці
   - Додайте policy: `Allow all` з `true` для anon role (для тестування)

### 3. Зібрати і запустити
- Build → Make Project
- Run → Run 'app' (або Shift+F10)
- Мінімальна версія Android: 8.0 (API 26)

---

## 📁 Структура проекту

```
app/src/main/java/com/example/bookly/
├── data/
│   ├── api/
│   │   ├── ApiService.java          ← Retrofit інтерфейс (Supabase REST)
│   │   ├── RetrofitClient.java      ← Налаштування Retrofit
│   │   └── SharedPrefsManager.java  ← Сесія користувача
│   ├── model/
│   │   ├── User.java
│   │   ├── Post.java
│   │   ├── Genre.java
│   │   ├── Complaint.java
│   │   └── BooksGenre.java
│   └── repository/
│       ├── UserRepository.java
│       ├── PostRepository.java
│       └── ComplaintRepository.java
├── domain/
│   └── usecase/
│       ├── AuthUseCase.java         ← Логін / Реєстрація + валідація
│       ├── PostUseCase.java         ← CRUD оголошень + фільтрація
│       ├── ComplaintUseCase.java    ← Подати / розглянути скаргу
│       └── ProfileUseCase.java     ← Профіль користувача
└── ui/
    ├── activities/
    │   ├── LoginActivity.java
    │   ├── RegisterActivity.java
    │   ├── MainActivity.java        ← BottomNav для User
    │   ├── AdminActivity.java       ← BottomNav для Admin
    │   ├── PostDetailActivity.java
    │   └── UserProfileActivity.java
    ├── fragments/
    │   ├── HomeFragment.java        ← Каталог + пошук + фільтр
    │   ├── MyBooksFragment.java     ← Мої оголошення
    │   ├── AddPostFragment.java     ← Додати книгу
    │   ├── EditPostFragment.java    ← Редагувати книгу
    │   ├── ProfileFragment.java     ← Мій профіль
    │   ├── EditProfileFragment.java
    │   ├── SettingsFragment.java    ← Вихід / видалення акаунту
    │   ├── ComplaintDialogFragment.java ← Форма скарги
    │   ├── AdminComplaintsFragment.java ← Список скарг (адмін)
    │   └── ComplaintDetailFragment.java ← Розгляд скарги (адмін)
    └── adapters/
        ├── PostAdapter.java         ← з контекстним меню
        └── ComplaintAdapter.java
```

---

## ✅ Виконані вимоги

| Вимога | Реалізація |
|--------|-----------|
| 2+ Activity | LoginActivity, RegisterActivity, MainActivity, AdminActivity, PostDetailActivity, UserProfileActivity |
| Fragments | 10 фрагментів |
| Наміри (Intent) | Явні (переходи між Activity), неявні (галерея) |
| Toolbar | У всіх Activity |
| Меню | Toolbar menu + BottomNavigationView |
| Контекстне меню | У PostAdapter (довгий натиск) |
| Діалогові вікна | AlertDialog (підтвердження), DialogFragment (скарга) |
| Сховище даних | SharedPreferences (сесія) + Supabase PostgreSQL |
| Валідація | Email regex, телефон, обов'язкові поля |
| Трирівнева архітектура | Data / Domain / UI |
