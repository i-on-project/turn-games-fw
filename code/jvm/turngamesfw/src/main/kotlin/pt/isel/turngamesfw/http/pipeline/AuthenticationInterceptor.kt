package pt.isel.turngamesfw.http.pipeline

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.turngamesfw.domain.User

@Component
class AuthenticationInterceptor(
    private val authorizationHeaderProcessor: AuthorizationHeaderProcessor
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.methodParameters.any { it.parameterType == User::class.java }
        ) {
            // Enforce authentication
            //val user = authorizationHeaderProcessor.process(request.getHeader(NAME_AUTHORIZATION_HEADER))
            val token: String? = request.cookies?.filter { cookie -> cookie.name.equals("bsToken") }?.map{ cookie -> cookie.value }?.firstOrNull()

            val user = authorizationHeaderProcessor.process(token)

            return if (user == null) {
                response.status = 401
                response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, AuthorizationHeaderProcessor.SCHEME)
                false
            } else {
                UserArgumentResolver.addUserTo(user, request)
                true
            }
        }

        return true
    }

    companion object {
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}