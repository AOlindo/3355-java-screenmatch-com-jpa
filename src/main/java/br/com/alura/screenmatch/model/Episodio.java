package br.com.alura.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "episodios")
public class Episodio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Integer temporada;
	private String titulo;
	private Integer numeroEpisodio;
	private Double avaliacao;
	private LocalDate dataLancamento;

	
	@ManyToOne
	@JoinColumn(name = "serie_id")
	private Serie serie;
	
	public Episodio() {
		
	}

	public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodio) {
		this.temporada = numeroTemporada;
		this.titulo = dadosEpisodio.titulo();
		this.numeroEpisodio = dadosEpisodio.numero();

		try {
			this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
		} catch (NumberFormatException ex) {
			this.avaliacao = 0.0;
		}

		try {
			this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
		} catch (DateTimeParseException ex) {
			this.dataLancamento = null;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public Integer getTemporada() {
		return temporada;
	}

	public void setTemporada(Integer temporada) {
		this.temporada = temporada;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getNumeroEpisodio() {
		return numeroEpisodio;
	}

	public void setNumeroEpisodio(Integer numeroEpisodio) {
		this.numeroEpisodio = numeroEpisodio;
	}

	public Double getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Double avaliacao) {
		this.avaliacao = avaliacao;
	}

	public LocalDate getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(LocalDate dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Episodio [id = ");
		builder.append(id);
		builder.append(", temporada = ");
		builder.append(temporada);
		builder.append(", titulo = ");
		builder.append(titulo);
		builder.append(", numeroEpisodio = ");
		builder.append(numeroEpisodio);
		builder.append(", avaliacao = ");
		builder.append(avaliacao);
		builder.append(", dataLancamento = ");
		builder.append(dataLancamento);
		builder.append(", serie = ");
		builder.append(serie);
		builder.append("]");
		return builder.toString();
	}

	
}
