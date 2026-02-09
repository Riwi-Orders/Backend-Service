# Flyway Database Migrations

Este directorio contiene las migraciones de base de datos gestionadas por Flyway.

## 📋 Convención de Nombres

Los archivos de migración deben seguir el formato:

```
V{VERSION}__{DESCRIPCION}.sql
```

### Ejemplos:
- `V1__initial_schema.sql` - Esquema inicial
- `V2__add_orders_table.sql` - Agregar tabla de órdenes
- `V3__add_user_phone_column.sql` - Agregar columna de teléfono

### Reglas:
1. **VERSION**: Número incremental (1, 2, 3, etc.)
2. **Doble guión bajo** (`__`) entre versión y descripción
3. **Descripción**: snake_case, descriptivo del cambio
4. **Extensión**: `.sql`

## 🚀 Comandos Útiles

### Ejecutar migraciones (automático al iniciar la app)
```bash
./mvnw spring-boot:run
```

### Ver estado de migraciones
```bash
./mvnw flyway:info
```

### Reparar historial de migraciones (si hay errores)
```bash
./mvnw flyway:repair
```

## ⚠️ Reglas Importantes

1. **NUNCA** modifiques una migración ya aplicada en producción
2. **SIEMPRE** prueba las migraciones localmente antes de commit
3. **USA** `IF NOT EXISTS` y `IF EXISTS` para SQL idempotente
4. Para cambios, crea una **NUEVA** migración

## 📁 Estructura de Carpetas

```
db/
└── migration/
    ├── V1__initial_schema.sql
    ├── V2__add_orders_table.sql
    └── ...
```

## 🔧 Configuración en application.yaml

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
    clean-disabled: true  # Seguridad: evita borrar datos
```
