# Proyecto Base Implementando Clean Architecture

## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**

## 📖 Documentación del API

La documentación del API se genera automáticamente con **springdoc-openapi**.

- **Swagger UI (interfaz gráfica):**  
  👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

- **OpenAPI JSON (especificación en formato JSON):**  
  👉 [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Notas Importantes
### 📌 Inyección de beans en los casos de uso (Clean Architecture + Plugin Bancolombia)
En el enfoque de **Arquitectura Limpia** con el plugin de Bancolombia, los **casos de uso** (capa usecase) deben permanecer independientes de frameworks (Spring, R2DBC, etc). Esto significa que no podemos inyectar directamente beans del framework, como TransactionalOperator o R2dbcEntityTemplate.

Sin embargo, hay situaciones en las que necesitamos usar características provistas por la infraestructura (ej. transacciones).
La forma correcta de hacerlo es a **través de un puerto**:

1. **Definimos una interfaz (puerto) en el dominio o usecase**
```
public interface TransactionalWrapper {
    <T> Publisher<T> execute(Publisher<T> publisher);
}
```
2. **Creamos un adaptador en infraestructura que implemente este puerto**
```
@Component
public class TransactionalWrapperImpl implements TransactionalWrapper {

    private final TransactionalOperator operator;

    public TransactionalWrapperImpl(TransactionalOperator operator) {
        this.operator = operator;
    }

    @Override
    public <T> Publisher<T> execute(Publisher<T> publisher) {
        return operator.transactional(publisher);
    }
}
```
3. **Inyectamos el puerto en el caso de uso, no el bean directamente**
```
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final TransactionalWrapper transactionalWrapper;

    public Mono<User> registerUser(User user) {
        return transactionalWrapper.execute(
            userRepository.existsUserEmail(user.getEmail())
                .flatMap(emailExists -> {
                    if (Boolean.TRUE.equals(emailExists)) {
                        return Mono.error(new EmailAlreadyRegisteredException("Email already registered"));
                    }
                    user.setActive(true);
                    user.setCreatedAt(LocalDate.now());

                    return rolRepository
                            .findRoleByName(RoleType.APPLICANT.getName())
                            .switchIfEmpty(Mono.error(new RoleNotFoundException("Role not found")))
                            .flatMap(role -> {
                                user.setRoleId(role.getId());
                                return userRepository.registerUser(user);
                            });
                })
        );
    }
}
```

### ✅ Ventajas de este enfoque

- El caso de uso no conoce Spring ni R2DBC → se mantiene puro.

- El bean TransactionalOperator se declara en infraestructura y se inyecta a través del adaptador.

- Si cambiamos el motor de base de datos o incluso el framework, el dominio no se rompe.
