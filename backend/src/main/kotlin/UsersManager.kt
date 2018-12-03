import store.*
import java.util.*

class UsersManager(
        private val tokensContainer: TokensContainer,
        private val store: SupnetStore,
        private val wsManager: WsManager
) {

    suspend fun signUp(request: SignUpRequest): SignUpResponse? {
        val (name, email, password) = request

        if (name.length !in 3..64) return null

        if (password.length !in 8..128) return null

        if (store.containsEmail(email)) return null

        val id = store.addUser(name, email, password)

        val token = addToken(id).toString()

        return SignUpResponse(id, token)
    }

    suspend fun signOff(token: UUID): Boolean {
        val id = getUserId(token) ?: return false
        removeToken(token)
        return store.removeUser(id)
    }

    suspend fun signIn(signInRequest: SignInRequest): SignInResponse? {
        val (email, password) = signInRequest

        val user = store.getUserByCredentials(email, password) ?: return null

        val friends = store.getUserFriends(user.id)

        val token = addToken(user.id).toString()

        return SignInResponse(user.id, token, user.name, friends, listOf())
    }

    fun signOut(token: UUID): Boolean {
        removeToken(token)
        return true
    }

    suspend fun sendInvitation(request: InvitationRequest, token: UUID): Boolean {
        val initiatorId = getUserId(token) ?: return false

        val (receiverName, msg) = request

        val recipient = store.getUserByName(receiverName) ?: return false

        val invId = store.addInvitation(initiatorId, recipient.id, msg)

        wsManager.tryToSendInvitation(Invitation(invId, initiatorId, recipient.id, msg))

        return true
    }

    private fun addToken(id: Int) = tokensContainer.add(id)

    private fun getUserId(token: UUID) = tokensContainer.get(token)

    private fun removeToken(token: UUID) = tokensContainer.remove(token)

}