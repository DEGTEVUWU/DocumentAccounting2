INSERT INTO public.roles (name)
SELECT * FROM (VALUES ('ROLE_ADMIN'), ('ROLE_MODERATOR'), ('ROLE_USER')) AS v(name)
WHERE NOT EXISTS (SELECT 1 FROM public.roles WHERE name = v.name);

INSERT INTO public.type_document (type)
SELECT * FROM (VALUES ('NOTE'), ('REPORT'), ('PRESENTATION'), ('ARTICLE'), ('DEFAULT_DOCUMENT')) AS v(type)
WHERE NOT EXISTS (SELECT 1 FROM public.type_document WHERE type = v.type);
