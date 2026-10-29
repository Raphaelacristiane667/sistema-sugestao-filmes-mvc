package br.com.mvc.exception;

/** O recurso pedido (filme, categoria ou avaliação) não existe. Vira HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) { super(mensagem); }
}
