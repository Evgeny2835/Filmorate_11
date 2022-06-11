# Учебное приложение "Online кинотеатр "Filmorate"

1. Добавление, обновление и получение информации о пользователях и фильмах
2. Хранение информации в базе данных
3. Валидация сведений о пользователях и фильмах
4. Логирование
5. Тестирование
6. Стек технологий: Java, SpringBoot, Lombok

### Пример кода:

```
@PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable Long userId) {
        if (id <= 0 || userId <= 0) {
            writeLogAndThrowValidationException();
        }
        filmService.addLike(id, userId);
    }
```

### ER диаграмма

<img src="/home/user/Yandex.Disk/STORAGE/LEARNING/IT/Yandex/IDEA/FinalSprints/java-filmorate/ER_diagram.png"/>

### Пояснение к ER диаграмме

Таблица genre содержит список жанров фильма.
У фильма может быть несколько жанров.

Таблица rating содержит перечень возрастных ограничений
фильма в соответствии с рейтингом Ассоциации кинокомпаний (МРА).

Таблица friendship определяет статус «дружба» между двумя пользователями.

### Примеры запросов для основных операций приложения:

Получение всех фильмов:

SELECT *
FROM films AS f
LEFT OUTER JOIN likes AS l ON f.film_id = l. film_id
LEFT OUTER JOIN genre AS g ON f.genre_id = g.genre_id
LEFT OUTER JOIN rating AS r ON f.rating_id = r.rating_id;

Получение всех пользователей:

SELECT *
FROM users AS u
LEFT OUTER JOIN friendship AS fr ON u.user_id = fr.user_id;

Получение ТОП-5 наиболее популярных фильмов:

SELECT f.name
    COUNT(l.user_id)
FROM films AS f
LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id
GROUP BY f.name
ORDER BY COUNT(l.user_id) DESC
LIMIT 5;