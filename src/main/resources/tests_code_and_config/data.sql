INSERT INTO public.roles (name)
SELECT * FROM (VALUES ('ROLE_ADMIN'), ('ROLE_MODERATOR'), ('ROLE_USER')) AS v(name)
WHERE NOT EXISTS (SELECT 1 FROM public.roles WHERE name = v.name);

INSERT INTO public.type_document (type)
SELECT * FROM (VALUES ('NOTE'), ('REPORT'), ('PRESENTATION'), ('ARTICLE'), ('DEFAULT_DOCUMENT')) AS v(type)
WHERE NOT EXISTS (SELECT 1 FROM public.type_document WHERE type = v.type);



-- Создание 13 тестовых документов, для отключения этого - заккоментируйте следующие строки
INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title1', 11,'Content1',
            (SELECT id FROM public.type_document WHERE id = 1),
            (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
        WHERE title = 'Title1'
);


INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title2', 22,'Content2',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title2'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title3', 33,'Content3',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title3'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title4', 44,'Content4',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title4'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title5', 55,'Content5',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title5'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title6', 66,'Content6',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title6'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title7', 77,'Content7',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title7'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title8', 88,'Content8',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title8'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title9', 99,'Content9',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title9'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title10', 110,'Content10',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title10'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title11', 111,'Content11',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title11'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title12', 122,'Content22',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title12'
);

INSERT INTO public.documents (title, number, content, type_id, author_id_user)
SELECT 'Title13', 133,'Content33',
        (SELECT id FROM public.type_document WHERE id = 1),
        (SELECT id_user  FROM public.users   WHERE id_user = 1)
    WHERE NOT EXISTS (
    SELECT 1 FROM public.documents
    WHERE title = 'Title13'
);

-- КОНЕЦ СОЗДАНИЯ ТЕСТОВЫХ ДОКУМЕНТОВ ДЛЯ НАЧАЛЬНОГО  ЗАПОЛНЕНИЯ ДАННЫМИ
------------------------------------------------------------------------

