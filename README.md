# Read-write_lock
Завдання про читачів і письменників. Базу даних поділяють два типи процесів - читачі та письменники. Читачі виконують транзакції, які переглядають записи бази даних, транзакції письменників і переглядають і змінюють записи.
Створити багатопоточний додаток, що працює із загальним файлом.
Для захисту операцій із загальним файлом використовувати блокування читання-запису. Файл містить послідовність записів виду: Ф.І.О.1 - телефон1, Ф.І.О.2 - телефон2 ... Повинні працювати наступні потоки:
1) потоки, що знаходять телефони за вказаним прізвищем;
2) потоки, на які ходять П.І.Б. за вказаним телефоном;
3) потоки, що видаляють і додають записи в файл.
