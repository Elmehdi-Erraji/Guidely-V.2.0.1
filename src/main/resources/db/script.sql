-- Clear existing data (if any exists)
TRUNCATE TABLE app_users CASCADE;
TRUNCATE TABLE role_permissions CASCADE;
TRUNCATE TABLE roles CASCADE;
TRUNCATE TABLE permissions CASCADE;

-- Insert Permissions
INSERT INTO permissions (id, name) VALUES
-- User Management Permissions
(gen_random_uuid(), 'USER_CREATE'),
(gen_random_uuid(), 'USER_READ'),
(gen_random_uuid(), 'USER_UPDATE'),
(gen_random_uuid(), 'USER_DELETE'),
-- Profile Management
(gen_random_uuid(), 'PROFILE_READ'),
(gen_random_uuid(), 'PROFILE_UPDATE'),
-- Ticket Management
(gen_random_uuid(), 'TICKET_CREATE'),
(gen_random_uuid(), 'TICKET_READ'),
(gen_random_uuid(), 'TICKET_UPDATE'),
(gen_random_uuid(), 'TICKET_DELETE'),
(gen_random_uuid(), 'TICKET_ASSIGN'),
-- System Management
(gen_random_uuid(), 'SYSTEM_SETTINGS_READ'),
(gen_random_uuid(), 'SYSTEM_SETTINGS_UPDATE'),
-- Support Functions
(gen_random_uuid(), 'SUPPORT_DASHBOARD_ACCESS'),
(gen_random_uuid(), 'GENERATE_REPORTS');

-- Insert Roles
INSERT INTO roles (id, name) VALUES
                                 (gen_random_uuid(), 'ADMIN'),
                                 (gen_random_uuid(), 'SUPPORT_AGENT'),
                                 (gen_random_uuid(), 'USER');

-- Assign permissions to ADMIN role (all permissions)
INSERT INTO role_permissions (id, role_id, permission_id)
SELECT gen_random_uuid(),
       (SELECT id FROM roles WHERE name = 'ADMIN'),
       id
FROM permissions;

-- Assign permissions to SUPPORT_AGENT role
INSERT INTO role_permissions (id, role_id, permission_id)
SELECT gen_random_uuid(),
       (SELECT id FROM roles WHERE name = 'SUPPORT_AGENT'),
       p.id
FROM permissions p
WHERE p.name IN (
                 'TICKET_CREATE',
                 'TICKET_READ',
                 'TICKET_UPDATE',
                 'TICKET_ASSIGN',
                 'PROFILE_READ',
                 'PROFILE_UPDATE',
                 'USER_READ',
                 'SUPPORT_DASHBOARD_ACCESS',
                 'GENERATE_REPORTS'
    );

-- Assign permissions to USER role
INSERT INTO role_permissions (id, role_id, permission_id)
SELECT gen_random_uuid(),
       (SELECT id FROM roles WHERE name = 'USER'),
       p.id
FROM permissions p
WHERE p.name IN (
                 'TICKET_CREATE',
                 'TICKET_READ',
                 'PROFILE_READ',
                 'PROFILE_UPDATE'
    );
