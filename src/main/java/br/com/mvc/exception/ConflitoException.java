package br.com.mvc.exception;

/** A operação conflita com o estado atual dos dados (ex.: nome duplicado). Vira HTTP 409. */
public class ConflitoException extends RuntimeException {
    public ConflitoException(String mensagem) { super(mensagem); }
}
