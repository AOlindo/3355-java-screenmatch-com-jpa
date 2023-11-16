package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
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
			case 0:
				System.out.println("Saindo...");
				break;
			default:
				System.out.println("Opção inválida");
			}
		}
	}

	private void buscarSeriePorAtor() {
		System.out.println("Qual o nome para busca? ");
		var nomeAtor = leitura.nextLine();
		System.out.println("Avaliações a partir de que valor? ");
		var avaliacao = leitura.nextDouble();
		List<Serie> seriesEncontradas = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
		System.out.println("Série em que " + nomeAtor +  " trabalhou: ");

		seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));

	}

	private void buscarSeriePorTitulo() {
		System.out.println("Escolha a série pelo nome: ");
		var nomeSerie = leitura.nextLine();
		Optional<Serie> serieBuscada = repository.findByTituloContainingIgnoreCase(nomeSerie);

		if (serieBuscada.isPresent()) {
			System.out.println("Dados da série: " + serieBuscada.get());
		} else {
			System.out.println("Série não encontrada!");
		}
	}

	private void buscarSerieWeb() {
		DadosSerie dados = getDadosSerie();
		Serie serie = new Serie(dados);
//		dadosSeries.add(dados);
		repository.save(serie);
		System.out.println(dados);
	}

	private DadosSerie getDadosSerie() {
		System.out.println("Digite o nome da série para busca");
		var nomeSerie = leitura.nextLine();
		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		return dados;
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

}