# Учебное приложение "Online кинотеатр "Filmorate"

1. Добавление, обновление и получение информации о пользователях и фильмах
2. Хранение информации в приложении
3. Валидация сведений о пользователях и фильмах
4. Логирование
5. Тестирование
6. Стек технологий: Java, SpringBoot, Lombok

Пример кода:

```
@PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable Long userId) {
        if (id <= 0 || userId <= 0) {
            writeLogAndThrowValidationException();
        }
        filmService.addLike(id, userId);
    }
```
