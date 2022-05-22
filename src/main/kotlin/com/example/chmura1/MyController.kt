package com.example.chmura1

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import java.net.URI
import java.time.LocalDateTime
import java.time.ZoneId
import javax.servlet.http.HttpServletRequest


@RestController
class MyController(val restTemplate: RestTemplate) {

    @GetMapping
    fun getRequestHeadersInMap(request: HttpServletRequest): String {
        val ip = HttpUtils.getRequestIP(request)
        val isLocal = checkIfLocal(ip)
        return if (isLocal) {
            val now = LocalDateTime.now()
            val systemZone = ZoneId.systemDefault()
            "Jesteś lokalnym użytkownikiem, twój czas to: $now, a strefa czasowa $systemZone ${systemZone.rules.getOffset(now)}"
        } else {
            val apiResponse = performExternalCall(ip)
            "Twoje IP to ${ip}, twój czas ${apiResponse.currentLocalTime}, a strefa czasowa ${ZoneId.of(apiResponse.timeZone).rules.getOffset(apiResponse.currentLocalTime)}"
        }
    }

    private fun performExternalCall(ip: String): ApiResponse {
        val response = restTemplate.getForEntity<ApiResponse>(URI("https://www.timeapi.io/api/TimeZone/ip?ipAddress=${ip}"))
        if (response.statusCode == HttpStatus.OK && response.body != null) {
            return response.body!!
        } else {
            throw IllegalStateException("Nie udało się pobrać danych z zewnętrznego serwera")
        }
    }

    private fun checkIfLocal(ip: String) = ip == "0:0:0:0:0:0:0:1" || ip == "172.17.0.1"

}


object HttpUtils {

    private val IP_HEADERS: Array<String> = arrayOf(
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    )

    fun getRequestIP(request: HttpServletRequest): String {
        for (header in IP_HEADERS) {
            val value = request.getHeader(header)
            if (value == null || value.isEmpty()) {
                continue
            }
            val parts = value.split("\\s*,\\s*");
            return parts[0]
        }
        return request.remoteAddr
    }


}

data class ApiResponse(val timeZone: String, val currentLocalTime: LocalDateTime)
