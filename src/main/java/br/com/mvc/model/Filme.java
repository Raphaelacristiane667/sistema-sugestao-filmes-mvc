package br.com.mvc.model;

public class Filme {
    private int id;
    private String titulo;
    private int anoLancamento;
    private Double media;

    public Filme() {}
    public Filme(int id, String titulo, int anoLancamento) {
        this.id = id;
        this.titulo = titulo;
        this.anoLancamento = anoLancamento;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(int anoLancamento) { this.anoLancamento = anoLancamento; }
    public Double getMedia() { return media; }
    public void setMedia(Double media) { this.media = media; }
}
