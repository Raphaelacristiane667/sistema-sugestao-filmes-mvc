package br.com.mvc.exception;

/** Os dados enviados não passam nas regras de validação. Vira HTTP 400. */
public class ValidacaoException extends RuntimeException {
    public ValidacaoException(String mensagem) { super(mensagem); }
}
