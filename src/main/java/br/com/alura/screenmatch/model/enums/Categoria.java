package br.com.alura.screenmatch.model.enums;

public enum Categoria {

	ACAO("Action"), 
	ROMANCE("Romance"), 
	COMEDIA("Comedy"), 
	SUSPENSE("Suspended"), 
	DRAMA("Drama"), 
	CRIME("Crime");

	private String categoriaOmdb;

	Categoria(String categoriaOmdb) {
		this.categoriaOmdb = categoriaOmdb;
	}
	
	public static Categoria fromString(String text) {
		for(Categoria categoria : Categoria.values()) {
			if(categoria.categoriaOmdb.equalsIgnoreCase(text)) {
				return categoria;
			}
		}
	 throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida");
	}
}