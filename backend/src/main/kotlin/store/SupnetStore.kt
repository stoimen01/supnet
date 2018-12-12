package store

import Friend
import InvitationID
import User
import UserID
import Invitation

interface SupnetStore {

    suspend fun containsEmail(email: String): Boolean

    suspend fun addUser(name: String, email:String, password:String): UserID

    suspend fun removeUser(id: UserID): Boolean

    suspend fun getUserById(id: UserID): User?

    suspend fun getUserByName(name: String): User?

    suspend fun getUserByCredentials(email: String, password: String): User?

    suspend fun getUserFriends(id: UserID): List<Friend>

    suspend fun addInvitation(initiatorId: UserID, recipientId: UserID, msg: String): InvitationID

    suspend fun removeInvitation(id: InvitationID): Boolean

    suspend fun getInvitation(id: InvitationID): Invitation?

    suspend fun getInvitationsByRecipient(recipientId: UserID): List<Invitation>

    suspend fun addFriendship(user1: UserID, user2: UserID)

}