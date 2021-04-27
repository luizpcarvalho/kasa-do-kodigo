package br.com.zup.edu.shared.handler.exceptions

import java.lang.RuntimeException

class LivroExistenteException(message: String?): RuntimeException(message)