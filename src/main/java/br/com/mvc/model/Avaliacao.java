package br.com.mvc.model;

public class Avaliacao {
    private int id;
    private int filmeId;
    private int categoriaId;
    private int nota;
    private String filmeTitulo;
    private String categoriaNome;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getFilmeId() { return filmeId; }
    public void setFilmeId(int filmeId) { this.filmeId = filmeId; }
    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getFilmeTitulo() { return filmeTitulo; }
    public void setFilmeTitulo(String filmeTitulo) { this.filmeTitulo = filmeTitulo; }
    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }
}
