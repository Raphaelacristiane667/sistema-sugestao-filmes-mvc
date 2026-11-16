package br.com.mvc.client;

/** Erro devolvido pela API (status HTTP + mensagem do campo "erro") ou falha ao conectar nela. */
public class ApiException extends Exception {
    /** Status usado quando a API não responde. */
    public static final int API_INDISPONIVEL = 503;

    private final int status;

    public ApiException(int status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public ApiException(String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.status = API_INDISPONIVEL;
    }

    public int getStatus() { return status; }

    /** Erros 4xx são problemas nos dados enviados (validação, conflito, não encontrado). */
    public boolean isErroDoUsuario() { return status >= 400 && status < 500; }
}
