# Учебное приложение "Online кинотеатр "Filmorate"

1. Добавление, обновление и получение информации о пользователях и фильмах
2. Хранение информации в приложении
3. Валидация сведений о пользователях и фильмах
4. Логирование
5. Тестирование
6. Стек технологий: Java, SpringBoot, Lombok

Пример кода:

```
@PutMapping
public Film update(@RequestBody Film film){
    if(!films.containsKey(film.getId())){
        logWarnAndThrowException("Фильм не существует");
    }
    log.info("Обновлен фильм: {}",film);
    films.put(film.getId(),film);
    return film;
}
```
