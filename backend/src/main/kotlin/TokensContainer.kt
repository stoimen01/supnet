import java.util.*
import java.util.concurrent.ConcurrentHashMap

class TokensContainer {

    private val tokens = ConcurrentHashMap<UUID, Int>()

    fun add(id: Int): UUID {
        val newToken = UUID.randomUUID()
        tokens[newToken] = id
        return newToken
    }

    fun get(token: UUID) = tokens[token]

    fun remove(token: UUID) = tokens.remove(token)

}