package br.com.zup.edu.shared.handler.exceptions

import java.lang.RuntimeException

class LivroInexistenteException(message: String?): RuntimeException(message)