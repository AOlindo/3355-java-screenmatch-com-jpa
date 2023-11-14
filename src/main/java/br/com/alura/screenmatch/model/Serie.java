package br.com.alura.screenmatch.model;

import java.util.OptionalDouble;

import br.com.alura.screenmatch.model.enums.Categoria;
import br.com.alura.screenmatch.service.ConsultaChatGPT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "series")
public class Serie {

	// "unique = true" quer dizer que o titulo da serie não pode ser repetido.
	@Column(unique = true)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String titulo;
	private Integer totalTemporadas;
	private Double avaliacao;

	@Enumerated(EnumType.STRING)
	private Categoria genero;

	private String atores;
	private String poster;
	private String sinopse;

	public Serie(DadosSerie dadosSerie) {
		this.titulo = dadosSerie.titulo();
		this.totalTemporadas = dadosSerie.totalTemporadas();
		this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
		this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
		this.atores = dadosSerie.atores();
		this.poster = dadosSerie.poster();
		this.sinopse = dadosSerie.sinopse();
//		this.sinopse = ConsultaChatGPT.obterTraducao(dadosSerie.sinopse()).trim();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getTotalTemporadas() {
		return totalTemporadas;
	}

	public void setTotalTemporadas(Integer totalTemporadas) {
		this.totalTemporadas = totalTemporadas;
	}

	public Double getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Double avaliacao) {
		this.avaliacao = avaliacao;
	}

	public Categoria getGenero() {
		return genero;
	}

	public void setGenero(Categoria genero) {
		this.genero = genero;
	}

	public String getAtores() {
		return atores;
	}

	public void setAtores(String atores) {
		this.atores = atores;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getSinopse() {
		return sinopse;
	}

	public void setSinopse(String sinopse) {
		this.sinopse = sinopse;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[Genero = ");
		builder.append(genero);
		builder.append(", titulo = ");
		builder.append(titulo);
		builder.append(", totalTemporadas = ");
		builder.append(totalTemporadas);
		builder.append(", avaliacao = ");
		builder.append(avaliacao);
		builder.append(", atores = ");
		builder.append(atores);
		builder.append(", poster = ");
		builder.append(poster);
		builder.append(", sinopse = ");
		builder.append(sinopse);
		builder.append("]");
		return builder.toString();
	}

}
