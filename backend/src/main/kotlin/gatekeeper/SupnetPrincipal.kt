package gatekeeper

import io.ktor.auth.Principal

class SupnetPrincipal(val token: String) : Principal
