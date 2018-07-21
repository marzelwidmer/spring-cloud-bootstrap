package ch.keepcalm.cloud.service.security.jwt

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken


class JwtTokenVerifier(private val config: JwtSecurityProperties) {

    private val LOG = LoggerFactory.getLogger(JwtTokenVerifier::class.java)

    companion object {
        const val AUTHORITIES_KEY = "roles"
    }

    fun getAuthentication(token: String): Authentication =
            createAuthenticationToken(createPrincipal(toClaims(token)), token)


    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parser()
                    .setSigningKey(config.secret)
                    .requireIssuer(config.issuer)
                    .requireAudience(config.audience)
                    .parseClaimsJws(authToken)
            return true

        } catch (e: SignatureException) {
            LOG.info("Invalid JWT signature.")
            LOG.trace("Invalid JWT signature trace: {}", e)
        } catch (e: MalformedJwtException) {
            LOG.info("Invalid JWT token.")
            LOG.trace("Invalid JWT token trace: {}", e)
        } catch (e: ExpiredJwtException) {
            LOG.info("Expired JWT token.")
            LOG.trace("Expired JWT token trace: {}", e)
        } catch (e: UnsupportedJwtException) {
            LOG.info("Unsupported JWT token.")
            LOG.trace("Unsupported JWT token trace: {}", e)
        } catch (e: IllegalArgumentException) {
            LOG.info("JWT token compact of handler are invalid.")
            LOG.trace("JWT token compact of handler are invalid trace: {}", e)
        }
        return false
    }



    private fun toClaims(token: String): Claims =
            Jwts.parser()
                    .setSigningKey(config.secret)
                    .parseClaimsJws(token)
                    .body

    private fun createAuthenticationToken(principal: User, token: String) =
            PreAuthenticatedAuthenticationToken(principal, token, principal.authorities)

    private fun createPrincipal(claims: Claims) =
            User(claims.subject, "", commaSeparatedStringToAuthorityList(claims.get(AUTHORITIES_KEY, String::class.java)))


}