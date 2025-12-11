-- Исправление constraint для таблицы roles, чтобы разрешить роль USER
-- Выполните этот скрипт в вашей базе данных PostgreSQL

-- Удаляем старый constraint
ALTER TABLE roles DROP CONSTRAINT IF EXISTS roles_name_check;

-- Создаем новый constraint с включением роли USER
ALTER TABLE roles ADD CONSTRAINT roles_name_check 
    CHECK (name IN ('ADMIN', 'MANAGER', 'OPERATOR', 'USER'));

