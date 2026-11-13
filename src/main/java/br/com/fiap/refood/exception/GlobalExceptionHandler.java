package br.com.fiap.refood.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> tratarNaoEncontrado(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ApiError> tratarRegraNegocio(
            RegraNegocioException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.CONFLICT ,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .findFirst()
                .orElse("Dados enviados inválidos.");

        return criarResposta(
                HttpStatus.METHOD_NOT_ALLOWED,
                mensagem,
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> tratarJsonInvalido(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "JSON inválido ou dados em formato incorreto.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> tratarErroGeral(
            Exception ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno no servidor.",
                request
        );
    }

    private ResponseEntity<ApiError> criarResposta(
            HttpStatus status,
            String mensagem,
            HttpServletRequest request) {

        ApiError erro = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }
}