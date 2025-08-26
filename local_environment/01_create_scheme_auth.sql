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
    SELECT 1 FROM public.roles WHERE role_type = 'ADMINISTRADOR'
);

INSERT INTO public.roles (role_type, description)
SELECT 'APPLICANT', 'Usuario con el rol solicitante del sistema de préstamos'
    WHERE NOT EXISTS (
    SELECT 1 FROM public.roles WHERE role_type = 'SOLICITANTE'
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
    document_type   VARCHAR(20) NOT NULL,
    document_number VARCHAR(50) UNIQUE NOT NULL,
    birth_date      DATE,
    address         VARCHAR(250),
    phone_number    VARCHAR(20),
    base_salary     NUMERIC(12,2) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role_id         UUID,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES public.roles (role_id)
);