package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.model.enums.Categoria;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;

public class Principal {

	private Scanner leitura = new Scanner(System.in);
	private ConsumoApi consumo = new ConsumoApi();
	private ConverteDados conversor = new ConverteDados();
	private final String ENDERECO = "https://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=6585022c";
	private List<DadosSerie> dadosSeries = new ArrayList<>();

	private SerieRepository repository;

	private List<Serie> series = new ArrayList<>();

	private Optional<Serie> serieBusca;
	
	public Principal(SerieRepository serieRepository) {
		this.repository = serieRepository;

	}

	public void exibeMenu() {
		var opcao = -1;
		while (opcao != 0) {
			var menu = """
					1 - Buscar séries
					2 - Buscar episódios
					3 - Listar séries buscadas
					4 - Buscar série por título
					5 - Buscar série por Ator
					6 - Top 5 séries
					7 - Buscar séries por categoria
					8 - Filtrar séries
					9 - Buscar episódio por trecho
					10 - Top episódios por serie
					11 - Buscar episódios a partir de uma data
					
					0 - Sair
					""";

			System.out.println(menu);
			opcao = leitura.nextInt();
			leitura.nextLine();

			switch (opcao) {
			case 1:
				buscarSerieWeb();
				break;
			case 2:
				buscarEpisodioPorSerie();
				break;
			case 3:
				listarSerieBuscadas();
				break;
			case 4:
				buscarSeriePorTitulo();
				break;
			case 5:
				buscarSeriePorAtor();
				break;
			case 6:
				buscarTop5Series();
				break;
			case 7:
				buscarSeriePorGenero();
				break;
			case 8:
				filtrarSeriesPorTemporadaEAvaliacao();
				break;
			case 9:
				buscarEpisodioPorTrecho();
				break;
			case 10:
				top5EpisodiosPorSerie();
				break;
			case 11:
				buscarEpisódiosDepoisDeUmaData();
				break;
			case 0:
				System.out.println("Saindo...");
				break;
			default:
				System.out.println("Opção inválida");
			}
		}
	}
	
	private DadosSerie getDadosSerie() {
		System.out.println("Digite o nome da série para busca");
		var nomeSerie = leitura.nextLine();
		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		return dados;
	}

	private void buscarSerieWeb() {
		DadosSerie dados = getDadosSerie();
		Serie serie = new Serie(dados);
//		dadosSeries.add(dados);
		repository.save(serie);
		System.out.println(dados);
	}

	private void buscarEpisodioPorSerie() {
		listarSerieBuscadas();
		System.out.println("Escolha uma série pelo nome: ");
		String nomeSerie = leitura.nextLine();

		Optional<Serie> serie = repository.findByTituloContainingIgnoreCase(nomeSerie);

		if (serie.isPresent()) {
			Serie serieEncontrada = serie.get();
			List<DadosTemporada> temporadas = new ArrayList<>();
			for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
				var json = consumo.obterDados(
						ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
				DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
				temporadas.add(dadosTemporada);
			}
			temporadas.forEach(System.out::println);

			List<Episodio> episodios = temporadas.stream()
					.flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
					.collect(Collectors.toList());

			serieEncontrada.setEpisodios(episodios);
			repository.save(serieEncontrada);
		} else {
			System.out.println("Série não encontrada!");
		}
	}

	private void listarSerieBuscadas() {
		series = repository.findAll();
		series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);

	}

	private void buscarSeriePorTitulo() {
		System.out.println("Escolha a série pelo nome: ");
		var nomeSerie = leitura.nextLine();
		serieBusca = repository.findByTituloContainingIgnoreCase(nomeSerie);

		if (serieBusca.isPresent()) {
			System.out.println("Dados da série: " + serieBusca.get());
		} else {
			System.out.println("Série não encontrada!");
		}
	}

	private void buscarSeriePorAtor() {
		System.out.println("Qual o nome para busca? ");
		var nomeAtor = leitura.nextLine();
		System.out.println("Avaliações a partir de que valor? ");
		var avaliacao = leitura.nextDouble();
		List<Serie> seriesEncontradas = repository
				.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
		System.out.println("Série em que " + nomeAtor + " trabalhou: ");

		seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));

	}

	private void buscarTop5Series() {
		List<Serie> serieTop = repository.findTop5ByOrderByAvaliacaoDesc();
		serieTop.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
	}

	private void buscarSeriePorGenero() {
		System.out.println("Deseja buscar séries de que categoria/gênero? ");
		var nomeGenero = leitura.nextLine();
		Categoria categoria = Categoria.fromPortugues(nomeGenero);
		List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
		System.out.println("Séries da categoria " + nomeGenero);
		seriesPorCategoria.forEach(System.out::println);

	}

	private void filtrarSeriesPorTemporadaEAvaliacao() {
		System.out.println("Filtrar séries até quantas temporadas? ");
		var totalTemporadas = leitura.nextInt();
		leitura.nextLine();
		System.out.println("Com avaliação a partir de que valor? ");
		var avaliacao = leitura.nextDouble();
		leitura.nextLine();
		List<Serie> filtroSeries = repository.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
		System.out.println("*** Séries filtradas ***");
		filtroSeries.forEach(s -> System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
	}

	private void buscarEpisodioPorTrecho() {
		System.out.println("Qual o nome do episodio para busca? ");
		var trechoEpisodio = leitura.nextLine();
		List<Episodio> episodiosEncontrados = repository.episodiosPorTrecho(trechoEpisodio);
		episodiosEncontrados.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s \n",
				e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));

	}

	private void top5EpisodiosPorSerie() {
		buscarSeriePorTitulo();
		if(serieBusca.isPresent()) {
			Serie serie = serieBusca.get();
			List<Episodio> topEpisodios = repository.topEpisodiosPorSerie(serie);
			topEpisodios.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliação %s \n",
				e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
		}

	}
	
	private void buscarEpisódiosDepoisDeUmaData() {
		buscarSeriePorTitulo();
		if(serieBusca.isPresent()) {
			Serie serie = serieBusca.get();
			System.out.println("Digite o ano limite de lançamento");
			var anoLancamento = leitura.nextInt();
			leitura.nextLine();
			
			List<Episodio> episodiosAno = repository.episodioPorSerieEAno(serie, anoLancamento);
			episodiosAno.forEach(System.out::println);
		}
		
	}

}