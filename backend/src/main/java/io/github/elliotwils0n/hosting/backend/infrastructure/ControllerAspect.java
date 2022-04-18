package io.github.elliotwils0n.hosting.backend.infrastructure;


import io.github.elliotwils0n.hosting.backend.model.ServerMessage;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ControllerAspect {

    @Around("anyPublicMethodInController()")
    @Order(1)
    public Object catchExceptions(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (GenericServerException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ServerMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body(new ServerMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error."));
        }
    }

    @Pointcut("execution(public * io.github.elliotwils0n.hosting.backend.controller.*Controller.*(..))")
    private void anyPublicMethodInController() {
        // no implementation, as this is only a pointcut definition
    }

}
