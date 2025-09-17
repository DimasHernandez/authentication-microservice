CREATE TABLE IF NOT EXISTS public.roles
(
    role_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_type   VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Insertar roles solo si no existen
INSERT INTO public.roles (role_type, description)
SELECT 'ADMIN', 'Usuario con el rol administrador del sistema de préstamos'
    WHERE NOT EXISTS (
    SELECT 1 FROM public.roles WHERE role_type = 'ADMIN'
);

INSERT INTO public.roles (role_type, description)
SELECT 'APPLICANT', 'Usuario con el rol solicitante del sistema de préstamos'
    WHERE NOT EXISTS (
    SELECT 1 FROM public.roles WHERE role_type = 'APPLICANT'
);

INSERT INTO public.roles (role_type, description)
SELECT 'ADVISOR', 'Usuario con el rol de asesor'
    WHERE NOT EXISTS (
    SELECT 1 FROM public.roles WHERE role_type = 'ADVISOR'
);

-- INSERT INTO public.roles (role_type, description)
-- VALUES ('ADMINISTRADOR', 'Usuario con el rol adminitrador del sistema de prestamos'),
--        ('SOLICITANTE', 'Usuario con el rol solicitante del sistema de prestamos');

CREATE TABLE public.users
(
    user_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL,
    surname         VARCHAR(100) NOT NULL,
    email           VARCHAR(150) UNIQUE NOT NULL,
    password_hash   VARCHAR(100) NOT NULL,
    document_type   VARCHAR(20) NOT NULL,
    document_number VARCHAR(50) UNIQUE NOT NULL,
    birth_date      DATE,
    address         VARCHAR(250),
    phone_number    VARCHAR(20),
    base_salary     NUMERIC(12,2) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role_id         UUID,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES public.roles (role_id)
);

-- Insertar usuario ADMIN si no existe
INSERT INTO public.users (
    name, surname, email, password_hash, document_type, document_number,
    birth_date, address, phone_number, base_salary, role_id
)
SELECT
    'Jhon', 'Doe', 'admin@example.com', '$2y$10$NxMX8MdEuZ1HSz7QQCafBepfNnJA/tJhNBqg52/0r6rS636fKFYQO', 'CC', '111111111',
    '1980-01-01', 'Calle Falsa 123', '3011234567', 5000000.00,
    role_id
FROM public.roles
WHERE role_type = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM public.users WHERE email = 'admin@example.com'
);

-- Insertar usuario ADVISOR si no existe
INSERT INTO public.users (
    name, surname, email, password_hash, document_type, document_number,
    birth_date, address, phone_number, base_salary, role_id
)
SELECT
    'Laura', 'Gómez', 'advisor@example.com', '$2y$10$cuAl4Vmej/.VwkFfc1D1wu2N2A7xTg.6xe8B9CM6DFCreAj7w3SUe', 'CC', '77771111',
    '1990-05-15', 'Av. Siempre Viva 742', '3029876543', 4000000.00,
    role_id
FROM public.roles
WHERE role_type = 'ADVISOR'
  AND NOT EXISTS (
    SELECT 1 FROM public.users WHERE email = 'advisor@example.com'
);

-- Insertar usuario APPLICANT si no existe
INSERT INTO public.users (
    name, surname, email, password_hash, document_type, document_number,
    birth_date, address, phone_number, base_salary, role_id
)
SELECT
    'Arcadio', 'Doe', 'doe@gmail.com', '$2y$10$3vaRiW3MChtXUGjk0Q6W4eOolMYDYIhLSPwa./nQXYQhCmDNEANUO', 'CC', '1234567890',
    '1999-01-01', 'Calle Falsa Cr 4 - 123', '3011234567', 5000000.00,
    role_id
FROM public.roles
WHERE role_type = 'APPLICANT'
  AND NOT EXISTS (
    SELECT 1 FROM public.users WHERE email = 'doe@gmail.com'
);