data class User(
        val id: UserID,
        val name: String,
        val email: String,
        val password: String
)

data class Invitation(
        val id: InvitationID,
        val initiatorId: UserID,
        val recipientId: UserID,
        val message: String
)

data class Friend(val name: String)

data class Gadget(val name: String, val owner: String)

data class InvitationRequest(
        val recipientName: String,
        val message: String
)

data class SignUpRequest(
        val name: String,
        val email: String,
        val password: String
)

data class SignUpResponse(
        val id: UserID,
        val token: String
)

data class SignInRequest(
        val email: String,
        val password: String
)

data class SignInResponse(
        val id: UserID,
        val token: String,
        val username: String,
        val friends: List<Friend>,
        val gadgets: List<Gadget>
)