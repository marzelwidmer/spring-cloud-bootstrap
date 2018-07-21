import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun main(args: Array<String>) {
    println("Welcome to JWT token generator....")

    val SUBJECT = "john"
    val ROLES = "keepcalm.user"
    val ISSUER = "KeepCalm Auth Portal"
    val AUDINCE = "KeepCalm"
    val SECRET = "willbereplacedinalaterversiononceRSAcanbeused"
    val USER_EMAIL = "${SUBJECT}@my-app.com"
    val EXPIRATION = 1


    println(message = "Enter a subject : [$SUBJECT]")
    val enterSubject = readLine()
    val subject: String = if (enterSubject == "") SUBJECT else enterSubject!!

    println("Enter a roles : [$ROLES]")
    val enterRoles = readLine()
    val roles: String = if (enterRoles == "") ROLES else enterRoles!!

    println("Enter a secret : [$SECRET]")
    val enterSecret = readLine()
    val secret: String = if (enterSecret == "") SECRET else enterSecret!!

    println("Enter a audience : [$AUDINCE]")
    val enterAudience = readLine()
    val audience: String = if (enterAudience == "") AUDINCE else enterAudience!!

    println("Enter a issuer : [$ISSUER]")
    val enterIssuer = readLine()
    val issuer: String = if (enterIssuer == "") ISSUER else enterIssuer!!

    println("Enter a user email : [$USER_EMAIL]")
    val enterUserEmail = readLine()
    val userEmail: String = if (enterUserEmail == "") USER_EMAIL else enterUserEmail!!

    println("Enter a expiration : [$EXPIRATION]")
    val enterExpiration = readLine()
    val expiration: String = if (enterExpiration == "") EXPIRATION.toString() else enterExpiration!!


    val signingKey = Base64.getEncoder().encodeToString(secret.toByteArray(StandardCharsets.UTF_8))


    var generatedBearer = Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .setSubject(subject)
            .setIssuedAt(Date())
            .setExpiration(Date.from(LocalDateTime.now().plusDays(expiration.toLong()).atZone(ZoneId.systemDefault()).toInstant()))
            .setIssuer(issuer)
            .setAudience(audience)
            .addClaims(mapOf(Pair("email", userEmail), Pair("roles", roles)))
            .signWith(SignatureAlgorithm.HS256, signingKey)
            .compact()

    println("###############################")
    println("Bearer \n ${generatedBearer} \n")
    println("###############################")

    println("-----------------")
    println("export token=${generatedBearer} \n")
    println("http --auth-type=token --auth=\"Bearer: \$token\" :4003/api/persons/12345678-1234-1234-1234-12345678 \n")
    println("-----------------")


}
